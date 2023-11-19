package ru.clevertec.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.impl.LFUCache;
import ru.clevertec.cache.impl.LRUCache;
import ru.clevertec.entity.Player;
import ru.clevertec.util.YamlReader;

import java.util.Optional;
import java.util.UUID;

@Aspect
public class PlayerProxy {
    private static final String ALGORITHM_FROM_FILE = YamlReader.getAlgorithm();
    private static final long MAX_SIZE_FROM_FILE = YamlReader.getMaxSize();
    private Cache<UUID, Player> cache = null;

    public PlayerProxy() {
        switch (ALGORITHM_FROM_FILE) {
            case "LRU" -> cache = new LRUCache<>(MAX_SIZE_FROM_FILE);
            case "LFU" -> cache = new LFUCache<>(MAX_SIZE_FROM_FILE);
        }
    }

    @Around("@annotation(ru.clevertec.aop.annotation.GetPlayer) && args(id)")
    public Player getPlayer(ProceedingJoinPoint joinPoint, UUID id) throws Throwable {
        Optional<Player> player = cache.get(id);
        if (player.isPresent())
            return player.get();
        Player obj = (Player) joinPoint.proceed();
        cache.put(obj.getId(), obj);
        return obj;
    }

    @After("@annotation(ru.clevertec.aop.annotation.PostPlayer) && args(player)")
    public void postPlayer(Player player) {
        cache.put(player.getId(), player);
    }

    @After("@annotation(ru.clevertec.aop.annotation.DeletePlayer) && args(id)")
    public void deletePlayer(UUID id) {
        cache.remove(id);
    }

    @After("@annotation(ru.clevertec.aop.annotation.PutPlayer) && args(player)")
    public void updatePlayer(Player player) {
        if (cache.get(player.getId()).isPresent()) {
            cache.put(player.getId(), player);
        }
    }
}
