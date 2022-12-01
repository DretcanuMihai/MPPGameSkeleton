package myapp.persistence.implementations;

import myapp.model.entities.Identifiable;
import myapp.persistence.interfaces.IRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.Serializable;

public class ORMRepository<ID extends Serializable, E extends Identifiable<ID>> implements IRepository<ID, E> {
    protected final SessionFactory sessionFactory;

    private final Class<E> type;

    public ORMRepository(SessionFactory sessionFactory, Class<E> type) {
        this.sessionFactory = sessionFactory;
        this.type = type;
    }

    //returns the entity if succeeded or null if failed
    //it will also set the id of the entity given as parameter
    @Override
    public E create(E entity) {
        E toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.persist(entity);
                transaction.commit();
                toReturn = entity;
            } catch (RuntimeException e) {
                e.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }

    //returns the entity if it exists or null if failed
    @Override
    public E read(ID id) {
        E toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                E aux;
                transaction = session.beginTransaction();
                aux = session.get(type, id);
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

    //returns the old entity if succeeded or null if failed
    //entity will represent the new state of the object
    @Override
    public E update(E entity) {
        E toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                E aux;
                transaction = session.beginTransaction();
                aux = session.get(type, entity.getIdentifier());
                session.detach(aux);
                session.merge(entity);
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

    //returns the deleted entity or null if failed
    @Override
    public E delete(ID id) {
        E toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                E aux;
                transaction = session.beginTransaction();
                aux = session.get(type, id);
                session.remove(aux);
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
