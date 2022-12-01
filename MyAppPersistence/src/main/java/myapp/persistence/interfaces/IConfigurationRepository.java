package myapp.persistence.interfaces;


import myapp.model.entities.Configuration;

public interface IConfigurationRepository extends IRepository<Integer, Configuration> {
    Configuration getRandomConfig();
}
