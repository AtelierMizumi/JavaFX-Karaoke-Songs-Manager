package com.javafx.application;

import javafx.application.Application;
import javafx.stage.Stage;



public class UserApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LaunchGUI.launch(primaryStage, "user-app.fxml", "Song Manager");
    }
}
