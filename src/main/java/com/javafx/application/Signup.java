package com.javafx.application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Signup extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LaunchGUI.launch(primaryStage, "signup.fxml", "Signup");
    }
}