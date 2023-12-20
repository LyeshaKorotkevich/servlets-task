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
import java.util.UUID;

@WebServlet("/players")
public class PlayerServlet extends HttpServlet {

    private final PlayerService playerService;
    private final ObjectMapper mapper;

    private static final String PLAYER_ID_REQUIRED = "Player ID is required";

    public PlayerServlet() {
        this.playerService = ApplicationConfig.getPlayerService();
        this.mapper = ApplicationConfig.getMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerIdParam = req.getParameter("id");
        if (playerIdParam == null || playerIdParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(PLAYER_ID_REQUIRED);
            return;
        }

        try {
            UUID id = UUID.fromString(playerIdParam);
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
            resp.getWriter().write("Invalid Player ID format");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            PlayerDto playerDto = mapper.readValue(req.getInputStream(), PlayerDto.class);
            UUID id = playerService.create(playerDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "?id=" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Player data format");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerIdParam = req.getParameter("id");
        if (playerIdParam == null || playerIdParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(PLAYER_ID_REQUIRED);
            return;
        }
        UUID id = UUID.fromString(playerIdParam);

        try {
            PlayerDto playerDto = mapper.readValue(req.getInputStream(), PlayerDto.class);
            playerService.update(id, playerDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "?id=" + id);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Player data format");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerIdParam = req.getParameter("id");
        if (playerIdParam == null || playerIdParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(PLAYER_ID_REQUIRED);
            return;
        }

        try {
            UUID id = UUID.fromString(playerIdParam);
            playerService.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Player ID format");
        }
    }
}