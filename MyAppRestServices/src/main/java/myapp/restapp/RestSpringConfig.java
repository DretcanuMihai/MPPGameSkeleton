package myapp.restapp;

import myapp.model.validators.implementations.Validator;
import myapp.model.validators.interfaces.IValidator;
import myapp.persistence.implementations.ConfigurationORMRepository;
import myapp.persistence.implementations.GameORMRepository;
import myapp.persistence.implementations.UserORMRepository;
import myapp.persistence.interfaces.IConfigurationRepository;
import myapp.persistence.interfaces.IGameRepository;
import myapp.persistence.interfaces.IUserRepository;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestSpringConfig {
    @Bean
    SessionFactory sessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Error creating session factory;");
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy( registry );
        }
        return null;
    }

    @Bean
    IConfigurationRepository configurationRepository(){
        return new ConfigurationORMRepository(sessionFactory());
    }

    @Bean
    IGameRepository gameRepository(){
       return new GameORMRepository(sessionFactory());
    }

    @Bean
    IUserRepository userRepository(){
        return new UserORMRepository(sessionFactory());
    }

    @Bean
    IValidator validator(){
       return new Validator();
    }
}
