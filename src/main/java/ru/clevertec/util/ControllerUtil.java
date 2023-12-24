package ru.clevertec.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static ru.clevertec.util.Constants.INVALID_PLAYER_ID;

public class ControllerUtil {

    private ControllerUtil() {
    }

    public static UUID extractPlayerIdFromPath(HttpServletRequest req, HttpServletResponse resp, int urlLength, int idPosition) throws IOException {
        String pathInfo = req.getPathInfo();

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length != urlLength) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Player ID is required");
            return null;
        }

        UUID id;
        try {
            id = UUID.fromString(pathParts[idPosition]);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_PLAYER_ID);
            return null;
        }
        return id;
    }
}
