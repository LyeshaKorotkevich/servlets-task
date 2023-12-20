package ru.clevertec.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.config.ApplicationConfig;
import ru.clevertec.dto.PlayerDto;
import ru.clevertec.service.PlayerService;
import ru.clevertec.util.Http;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/players")
public class PlayerServlet extends HttpServlet {

    private final PlayerService playerService;
    private final ObjectMapper mapper;

    private static final String CONTENT_TYPE_JSON = "application/json";

    public PlayerServlet() {
        this.playerService = ApplicationConfig.getPlayerService();
        this.mapper = ApplicationConfig.getMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerIdParam = req.getParameter("id");
        if (playerIdParam == null || playerIdParam.isEmpty()) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Player ID is required");
            return;
        }

        try {
            UUID id = UUID.fromString(playerIdParam);
            PlayerDto playerDto = playerService.get(id);
            if (playerDto != null) {
                String json = mapper.writeValueAsString(playerDto);
                resp.setStatus(Http.OK.getCode());
                resp.setContentType(CONTENT_TYPE_JSON);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(Http.NOT_FOUND.getCode());
                resp.getWriter().write("Player not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Invalid Player ID format");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            PlayerDto playerDto = mapper.readValue(req.getInputStream(), PlayerDto.class);
            UUID id = playerService.create(playerDto);
            resp.setStatus(Http.CREATED.getCode());
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "?id=" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Invalid Player data format");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerIdParam = req.getParameter("id");
        if (playerIdParam == null || playerIdParam.isEmpty()) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Player ID is required");
            return;
        }
        UUID id = UUID.fromString(playerIdParam);

        try {
            PlayerDto playerDto = mapper.readValue(req.getInputStream(), PlayerDto.class);
            playerService.update(id, playerDto);
            resp.setStatus(Http.CREATED.getCode());
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "?id=" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Invalid Player data format");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerIdParam = req.getParameter("id");
        if (playerIdParam == null || playerIdParam.isEmpty()) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Player ID is required");
            return;
        }

        try {
            UUID id = UUID.fromString(playerIdParam);
            playerService.delete(id);
            resp.setStatus(Http.NO_CONTENT.getCode());
        } catch (IllegalArgumentException e) {
            resp.setStatus(Http.BAD_REQUEST.getCode());
            resp.getWriter().write("Invalid Player ID format");
        }
    }
}