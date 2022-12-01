package myapp.persistence.implementations;

import myapp.model.entities.User;
import myapp.persistence.interfaces.IUserRepository;
import org.hibernate.SessionFactory;

public class UserORMRepository extends ORMRepository<String,User> implements IUserRepository {

    public UserORMRepository(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }
}
