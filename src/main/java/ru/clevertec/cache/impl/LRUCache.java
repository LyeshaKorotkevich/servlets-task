package ru.clevertec.cache.impl;

import ru.clevertec.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация кэша со стратегией вытеснения "Наиболее давно используемый" (LRU - Least Recently Used).
 *
 * @param <K> Тип ключей в кэше.
 * @param <V> Тип значений в кэше.
 */
public class LRUCache<K, V> implements Cache<K, V> {
    /**
     * Размер кэша по умолчанию.
     */
    public static final Long DEFAULT_SIZE = 20L;

    /**
     * Текущий размер кэша.
     */
    private final Long size;

    /**
     * Хранилище элементов кэша.
     */
    private Map<K, Node<K, V>> cacheMap;

    /**
     * Узел двусвязного списка для отслеживания порядка использования элементов.
     */
    private Node<K, V> head, tail;

    /**
     * Внутренний класс, представляющий узел двусвязного списка.
     *
     * @param <K> Тип ключа узла.
     * @param <V> Тип значения узла.
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        Node<K, V> prev;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Конструктор LRU-кэша с размером по умолчанию.
     */
    public LRUCache() {
        this(DEFAULT_SIZE);
    }

    /**
     * Конструктор LRU-кэша с заданным размером.
     *
     * @param cacheSize Максимальное количество элементов, которые может содержать кэш.
     */
    public LRUCache(Long cacheSize) {
        this.size = cacheSize;
        this.clean();
    }

    @Override
    public void clean() {
        cacheMap = new HashMap<>();
    }

    @Override
    public Optional<V> get(K key) {
        if (cacheMap.containsKey(key)) {
            Node<K, V> node = cacheMap.get(key);
            removeNode(node);
            addToTail(node);
            return Optional.of(node.value);
        }
        return Optional.empty();
    }

    /**
     * Связывает указанное значение с указанным ключом в кэше.
     * Если кэш уже содержит ключ, значение будет обновлено.
     * Если кэш заполнен, может произойти вытеснение самой давно использованной записи.
     *
     * @param key   Ключ, с которым ассоциируется указанное значение.
     * @param value Значение, которое будет ассоциировано с указанным ключом.
     */
    @Override
    public void put(K key, V value) {
        if(cacheMap.containsKey(key)){
            Node<K, V> item = cacheMap.get(key);
            item.value = value;

            removeNode(item);
            addToTail(item);
        }else{
            if(cacheMap.size() >= size){
                cacheMap.remove(head.key);
                removeNode(head);
            }

            Node<K, V> node = new Node<>(key, value);
            addToTail(node);
            cacheMap.put(key, node);
        }
    }

    /**
     * Удаляет указанный узел из списка.
     * Обновляет ссылки предыдущего и следующего узлов при необходимости.
     *
     * @param node Узел, который нужно удалить.
     */
    private void removeNode(Node<K, V> node){

        if(node.prev != null){
            node.prev.next = node.next;
        }else{
            head = node.next;
        }

        if(node.next != null){
            node.next.prev = node.prev;
        }else{
            tail = node.prev;
        }
    }

    /**
     * Добавляет указанный узел в конец списка.
     * Обновляет ссылки узлов для поддержания структуры двусвязного списка.
     *
     * @param node Узел, который нужно добавить в конец списка.
     */
    private void addToTail(Node<K, V> node){

        if(tail != null){
            tail.next = node;
        }

        node.prev = tail;
        node.next = null;
        tail = node;

        if(head == null){
            head = tail;
        }
    }
}
