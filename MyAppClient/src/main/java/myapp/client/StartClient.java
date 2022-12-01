package myapp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import myapp.client.controllers.LoginController;
import myapp.services.interfaces.ISuperService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class StartClient extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ClientSpringConfig.class);
        ISuperService server = context.getBean(ISuperService.class);

        FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("login.fxml"));
        Parent root = loader.load();


        LoginController ctrl = loader.getController();
        ctrl.setSuperService(server);
        ctrl.setStage(primaryStage);

        primaryStage.setTitle("Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}


