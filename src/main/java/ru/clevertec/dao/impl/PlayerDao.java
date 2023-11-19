package ru.clevertec.dao.impl;

import ru.clevertec.aop.annotation.PostPlayer;
import ru.clevertec.aop.annotation.PutPlayer;
import ru.clevertec.aop.annotation.DeletePlayer;
import ru.clevertec.aop.annotation.GetPlayer;
import ru.clevertec.dao.Dao;
import ru.clevertec.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerDao implements Dao<Player> {
    @Override
    @PostPlayer
    public Player save(Player obj) {
        return null;
    }

    @Override
    @GetPlayer
    public Optional<Player> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Player> findAll() {
        return null;
    }

    @Override
    @PutPlayer
    public void update(Player obj) {

    }

    @Override
    @DeletePlayer
    public void delete(UUID id) {

    }
}
