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
     * Получает значение по имени из конфигурационного файла.
     *
     * @return значение
     * @throws RuntimeException Если значение не найдено в конфигурационном файле.
     */
    public static Object getProperty(String property) {
        if (configMap.containsKey(property)) {
            return configMap.get(property);
        } else {
            throw new RuntimeException("Property is not found in config");
        }
    }
}
