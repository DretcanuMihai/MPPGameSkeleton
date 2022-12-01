package myapp.persistence.implementations;

import myapp.model.entities.Configuration;
import myapp.persistence.interfaces.IConfigurationRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Random;

public class ConfigurationORMRepository extends ORMRepository<Integer, Configuration> implements IConfigurationRepository {

    public ConfigurationORMRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Configuration.class);
    }

    @Override
    public Configuration getRandomConfig() {
        Configuration toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Configuration> choices = session.createQuery("from Configuration as c", Configuration.class)
                        .list();
                int ind = (new Random()).nextInt(choices.size());
                Configuration aux = choices.get(ind);
                transaction.commit();
                toReturn = aux;
            } catch (RuntimeException e) {
                e.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }
}
