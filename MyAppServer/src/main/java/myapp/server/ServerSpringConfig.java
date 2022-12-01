package myapp.server;

import myapp.model.validators.implementations.Validator;
import myapp.model.validators.interfaces.IValidator;
import myapp.network.utils.AbstractServer;
import myapp.network.utils.AppConcurrentServer;
import myapp.persistence.implementations.GameORMRepository;
import myapp.persistence.implementations.ConfigurationORMRepository;
import myapp.persistence.implementations.UserORMRepository;
import myapp.persistence.interfaces.IGameRepository;
import myapp.persistence.interfaces.IConfigurationRepository;
import myapp.persistence.interfaces.IUserRepository;
import myapp.services.interfaces.ISuperService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class ServerSpringConfig {
    @Bean
    SessionFactory sessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return null;
    }

    @Bean
    IGameRepository gameRepository() {
        return new GameORMRepository(sessionFactory());
    }

    @Bean
    IConfigurationRepository configurationRepository() {
        return new ConfigurationORMRepository(sessionFactory());
    }

    @Bean
    IUserRepository userRepository() {
        return new UserORMRepository(sessionFactory());

    }

    @Bean
    IValidator validator() {
        return new Validator();
    }

    @Bean
    ISuperService superService() {
        return new SuperService(validator(), userRepository(), gameRepository(), configurationRepository());
    }

    @Bean
    Properties properties() {
        Properties properties = new Properties();
        try {
            properties.load(ServerSpringConfig.class.getResourceAsStream("/server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            properties = null;
        }
        return properties;
    }

    @Bean
    Integer port() {
        Integer port = null;
        try {
            port = Integer.parseInt(properties().getProperty("server.port"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return port;
    }

    @Bean
    AbstractServer server() {
        return new AppConcurrentServer(port(), superService());
    }
}
