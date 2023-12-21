package ru.clevertec.service.impl;

import ru.clevertec.dao.Dao;
import ru.clevertec.dto.PlayerDto;
import ru.clevertec.entity.Player;
import ru.clevertec.exception.PlayerNotFoundException;
import ru.clevertec.mapper.PlayerMapper;
import ru.clevertec.service.PlayerService;
import ru.clevertec.validator.PlayerValidator;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerServiceImpl implements PlayerService {
    private final Dao<Player> playerDao;
    private final PlayerMapper mapper;

    public PlayerServiceImpl(Dao<Player> playerDao, PlayerMapper mapper) {
        this.playerDao = playerDao;
        this.mapper = mapper;
    }

    @Override
    public PlayerDto get(UUID id) {
        Player player = playerDao.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
        return mapper.toPlayerDto(player);
    }

    @Override
    public List<PlayerDto> getAll() {
        return playerDao.findAll().stream()
                .map(mapper::toPlayerDto)
                .toList();
    }

    @Override
    public List<PlayerDto> getAll(int page, int pageSize) {
        List<PlayerDto> list = this.getAll();
        int listSize = list.size();

        int totalPages = listSize % pageSize == 0 ? listSize / pageSize : listSize / pageSize + 1;

        if (page < 1 || page > totalPages) {
            return Collections.emptyList();
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, listSize);
        return list.subList(start, end);
    }


    @Override
    public UUID create(PlayerDto playerDto) {
        try {
            if (PlayerValidator.validate(playerDto)) {
                Player player = mapper.toPlayer(playerDto);
                player.setId(UUID.randomUUID());
                return playerDao.save(player).getId();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(UUID id, PlayerDto playerDto) {
        try {
            if (PlayerValidator.validate(playerDto)) {
                Player player = playerDao.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
                playerDao.update(id, mapper.merge(player, playerDto));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        playerDao.delete(id);
    }
}
