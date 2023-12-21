package ru.clevertec.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.config.ApplicationConfig;
import ru.clevertec.dto.PlayerDto;
import ru.clevertec.service.PlayerService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/players/*")
public class PlayerServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String INVALID_PLAYER_ID = "Invalid Player ID format";

    private final PlayerService playerService;
    private final ObjectMapper mapper;

    public PlayerServlet() {
        this.playerService = ApplicationConfig.getPlayerService();
        this.mapper = ApplicationConfig.getMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            handleGetPlayers(req, resp);
        } else {
            handleGetPlayerById(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            PlayerDto playerDto = mapper.readValue(req.getInputStream(), PlayerDto.class);
            UUID id = playerService.create(playerDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Player data format");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID id = extractPlayerIdFromPath(req, resp);

        try {
            PlayerDto playerDto = mapper.readValue(req.getInputStream(), PlayerDto.class);
            playerService.update(id, playerDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Player data format");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID id = extractPlayerIdFromPath(req, resp);

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
                String json = mapper.writeValueAsString(playerDtoList);
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
        UUID id = extractPlayerIdFromPath(req, resp);

        try {
            PlayerDto playerDto = playerService.get(id);
            if (playerDto != null) {
                String json = mapper.writeValueAsString(playerDto);
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

    private UUID extractPlayerIdFromPath(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length != 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Player ID is required");
            return null;
        }

        UUID id;
        try {
            id = UUID.fromString(pathParts[1]);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_ID);
            return null;
        }
        return id;
    }
}