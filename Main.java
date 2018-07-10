package com.smriti.connect;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();

        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());


        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        // need to add this menubar to pane as a child element
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("connect4");
        primaryStage.setResizable(false);
        primaryStage.show();

    }


    private MenuBar createMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");

        newGame.setOnAction(event -> controller.resetgame());

        MenuItem resetGame = new MenuItem("reset");
        resetGame.setOnAction(event -> controller.resetgame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit");

        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //help menu

        Menu helpMenu = new Menu("help");
        MenuItem aboutGame = new MenuItem("About connect");
        aboutGame.setOnAction(event -> aboutconnect());


        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About me ");
        aboutMe.setOnAction(event-> aboutme());


        helpMenu.getItems().addAll(aboutGame, separatorMenuItem, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;


    }

    private void aboutme() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("about me!");
        alert.setHeaderText(" SMRITI TIWARI :0 !");
        alert.setContentText(" i am a 3rd year undergraduate at IIT kgp ! i love to slay !!!! :P ");
        alert.show();
    }

    private void aboutconnect() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("about connect");
        alert.setHeaderText("how to play ?");
        alert.setContentText("Connect Four is a two-player connection game in which the players"+
                "first choose a color and then take turns dropping colored discs from the top into"+
                "a seven-column, six-row vertically suspended grid. The pieces fall straight down, "+
                "occupying the next available space within the column. The objective of the game is to"+
                "be the first to form a horizontal, vertical, or diagonal line of four of one's own discs."+
                "Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
