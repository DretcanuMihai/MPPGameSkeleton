package myapp.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import myapp.model.entities.Game;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IObserver;

import java.io.IOException;
import java.time.LocalDate;

public class MainController implements IObserver {

    public TableView<Game> leaderboardTableView;
    public TableColumn<Game, String> leaderboardNameColumn;
    public TableColumn<Game, LocalDate> leaderboardDateTimeColumn;
    public TableColumn<Game, String> leaderboardScoreColumn;

    public TextArea gameTextArea;

    private ObservableList<Game> games;


    private ISuperService superService;
    private Stage stage;
    private User user;
    private Game game;


    public ISuperService getSuperService() {
        return superService;
    }

    public void setSuperService(ISuperService superService) {
        this.superService = superService;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void initializeForShow() {
        initializeLeaderBoard();
        loadLeaderBoard();
        initializeGame();
    }

    private void initializeLeaderBoard() {
        //todo: might need to update this if the leaderboard should be displayed in a specific way
        games = FXCollections.observableArrayList();
        leaderboardTableView.setItems(games);
        leaderboardNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        leaderboardDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        leaderboardScoreColumn.setCellValueFactory(new PropertyValueFactory<>("currentScore"));
    }

    private void loadLeaderBoard() {
        games.clear();
        try {
            games.addAll(superService.getLeaderBoard());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
        leaderboardTableView.refresh();
    }

    private void initializeGame() {
        gameTextArea.setEditable(false);
        refreshGame();
    }

    private void refreshGame() {
        //todo: add how the game should be displayed on the board
        String myText = game.toString();
        gameTextArea.setText(myText);
    }

    @FXML
    public void onLogoutButtonClick() throws IOException {
        try {
            superService.logout(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("login.fxml"));
        Parent root = loader.load();


        LoginController ctrl = loader.getController();
        ctrl.setSuperService(getSuperService());
        ctrl.setStage(getStage());
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void gameEnded(Game game) {
        Platform.runLater(this::loadLeaderBoard);
    }

    public Round getRound() {
        //todo: add the logic that creates the data for the next round
        Round round = new Round();
        return round;
    }

    @FXML
    public void onNextRoundButtonClick() {
        try {
            game = superService.nextRound(user, getRound());
            refreshGame();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }
}
