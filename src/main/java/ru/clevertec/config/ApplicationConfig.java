package ru.clevertec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.clevertec.dao.Dao;
import ru.clevertec.dao.impl.PlayerDao;
import ru.clevertec.entity.Player;
import ru.clevertec.mapper.PlayerMapper;
import ru.clevertec.mapper.PlayerMapperImpl;
import ru.clevertec.service.PlayerService;
import ru.clevertec.service.impl.PlayerServiceImpl;

public class ApplicationConfig {
    private static final Dao<Player> playerDao = new PlayerDao();
    private static final PlayerMapper playerMapper = new PlayerMapperImpl();

    @Getter
    private static final PlayerService playerService = new PlayerServiceImpl(playerDao, playerMapper);

    @Getter
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private ApplicationConfig() {
    }
}
