package ru.clevertec.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import ru.clevertec.dto.PlayerDto;
import ru.clevertec.service.PlayerService;
import ru.clevertec.util.ControllerUtil;
import ru.clevertec.view.PdfPrinter;
import ru.clevertec.view.Printer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static ru.clevertec.util.Constants.DEFAULT_PAGE_SIZE;
import static ru.clevertec.util.Constants.INVALID_PLAYER_DATA_FORMAT;
import static ru.clevertec.util.Constants.INVALID_PLAYER_ID;

@WebServlet({"/players/*", "/players/*/checks/pdf"})
public class PlayerServlet extends HttpServlet {

    private PlayerService playerService;
    private ObjectMapper objectMapper;
    private Printer printer;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("appContext");
        playerService = context.getBean(PlayerService.class);
        objectMapper = context.getBean(ObjectMapper.class);
        printer = context.getBean(PdfPrinter.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            handleGetPlayers(req, resp);
        } else if (pathInfo.endsWith("/checks/pdf")) {
            handleGeneratePdfReport(req, resp);
        } else {
            handleGetPlayerById(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            PlayerDto playerDto = objectMapper.readValue(req.getInputStream(), PlayerDto.class);
            UUID id = playerService.create(playerDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_DATA_FORMAT);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID id = ControllerUtil.extractPlayerIdFromPath(req, resp, 2, 1);

        try {
            PlayerDto playerDto = objectMapper.readValue(req.getInputStream(), PlayerDto.class);
            playerService.update(id, playerDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_DATA_FORMAT);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID id = ControllerUtil.extractPlayerIdFromPath(req, resp, 2, 1);

        try {
            playerService.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_ID);
        }
    }

    private void handleGetPlayers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringPage = req.getParameter("page");
        if (stringPage == null || stringPage.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Page is required");
            return;
        }
        int page = Integer.parseInt(stringPage);

        String stringPageSize = req.getParameter("page-size");
        int pageSize;
        if (stringPageSize == null || stringPageSize.isEmpty()) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else {
            pageSize = Integer.parseInt(stringPageSize);
        }

        try {
            List<PlayerDto> playerDtoList = playerService.getAll(page, pageSize);

            if (!playerDtoList.isEmpty()) {
                String json = objectMapper.writeValueAsString(playerDtoList);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Players not found");
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing request");
        }
    }

    private void handleGetPlayerById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID id = ControllerUtil.extractPlayerIdFromPath(req, resp, 2, 1);

        try {
            PlayerDto playerDto = playerService.get(id);
            if (playerDto != null) {
                String json = objectMapper.writeValueAsString(playerDto);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Player not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_ID);
        }
    }

    private void handleGeneratePdfReport(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID id = ControllerUtil.extractPlayerIdFromPath(req, resp, 4, 1);

        try {
            PlayerDto playerDto = playerService.get(id);
            if (playerDto != null) {
                printer.printPlayer(id);

                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "attachment; filename=player_report_" + id + ".pdf");

                try (InputStream inputStream = new FileInputStream("player_report_" + id + ".pdf");
                     OutputStream outputStream = resp.getOutputStream()) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("Error reading or writing PDF report");
                }

                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Player not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_ID);
        }
    }
}