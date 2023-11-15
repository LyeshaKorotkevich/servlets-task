package ru.clevertec.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * Класс для чтения конфигурационных параметров из YAML файла.
 */
public class YamlReader {
    private static final String CONFIG_FILE = "application.yml";

    private static final Yaml yaml = new Yaml();
    private static final InputStream inputStream = YamlReader.class
            .getClassLoader()
            .getResourceAsStream(CONFIG_FILE);
    private static final Map<String, Object> configMap = yaml.load(inputStream);

    /**
     * Получает значение размера кэша из конфигурационного файла.
     *
     * @return размер кэша.
     * @throws RuntimeException Если значение не найдено в конфигурационном файле.
     */
    public static Long getMaxSize() {
        if (configMap.containsKey("maxSize")) {
            return (Long) configMap.get("maxSize");
        } else {
            throw new RuntimeException("Maximum size is not found in config");
        }
    }

    /**
     * Получает вид алгоритма кэширования из конфигурационного файла.
     *
     * @return алгоритм.
     * @throws RuntimeException Если значение не найдено в конфигурационном файле.
     */
    public static String getAlgorithm() {
        if (configMap.containsKey("algorithm")) {
            return (String) configMap.get("algorithm");
        } else {
            throw new RuntimeException("Algorithm is not found in config");
        }
    }
}
