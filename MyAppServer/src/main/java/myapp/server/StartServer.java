package myapp.server;

import myapp.network.utils.AbstractServer;
import myapp.network.utils.ServerException;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class StartServer {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(ServerSpringConfig.class);
        AbstractServer server = context.getBean(AbstractServer.class);
        try {
            server.start();
        } catch (ServerException e) {
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                e.printStackTrace();
            }
            SessionFactory sessionFactory=context.getBean(SessionFactory.class);
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
