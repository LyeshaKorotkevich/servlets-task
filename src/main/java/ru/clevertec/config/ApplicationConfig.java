package ru.clevertec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.factory.CacheFactory;
import ru.clevertec.entity.Player;

import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("ru.clevertec")
@PropertySource("classpath:application.yml")
@Import(DatabaseConfig.class)
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    public Cache<UUID, Player> cache(CacheFactory<UUID, Player> cacheFactory) {
        return cacheFactory.createCache();
    }

    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties property = Objects.requireNonNull(yaml.getObject(), "Not found");
        configurer.setProperties(property);
        return configurer;
    }
}
