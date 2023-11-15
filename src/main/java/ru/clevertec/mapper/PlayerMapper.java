package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.dto.PlayerDto;
import ru.clevertec.entity.Player;

@Mapper
public interface PlayerMapper {

    /**
     * Маппит DTO в игрока без UUID
     *
     * @param playerDto - DTO для маппинга
     * @return новый игрок
     */
    @Mapping(target = "id", ignore = true)
    Player toPlayer(PlayerDto playerDto);
}
