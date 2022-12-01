package myapp.persistence.implementations;

import myapp.model.entities.Game;
import myapp.model.entities.User;
import myapp.persistence.interfaces.IGameRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class GameORMRepository extends ORMRepository<Integer, Game> implements IGameRepository {

    public GameORMRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Game.class);
    }

    @Override
    public List<Game> getLeaderBoard() {
        List<Game> toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Game> aux= session.createQuery("from Game as g", Game.class)
                        .stream().filter(Game::isFinished).sorted((o1, o2) -> {
                            int toReturn1 = o2.getCurrentScore().compareTo(o1.getCurrentScore());
                            if (toReturn1 == 0) {
                                toReturn1 = o1.getIdentifier().compareTo(o2.getIdentifier());
                            }
                            return toReturn1;
                        }).toList();
                transaction.commit();
                toReturn=aux;
            } catch (RuntimeException e) {
                e.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }

    @Override
    public List<Game> getUserGames(User user) {
        List<Game> toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Game> aux = session.createQuery("from Game as g where g.user=:user", Game.class)
                        .setParameter("user", user)
                        .stream().filter(Game::isFinished).sorted((o1, o2) -> {
                            int toReturn1 = o2.getCurrentScore().compareTo(o1.getCurrentScore());
                            if (toReturn1 == 0) {
                                toReturn1 = o1.getIdentifier().compareTo(o2.getIdentifier());
                            }
                            return toReturn1;
                        }).toList();
                transaction.commit();
                toReturn =aux;
            } catch (RuntimeException e) {
                e.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }
}
