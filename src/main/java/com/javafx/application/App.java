package com.javafx.application;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
        private static Stage primaryStage;

        @Override
        public void start(Stage primaryStage) {
            App.primaryStage = primaryStage;
            LaunchGUI.launch(primaryStage, "app.fxml", "Song Library");
        }

        public static void main(String[] args) {
            launch(args);
        }

        public static Stage getPrimaryStage() {
            return primaryStage;
        }
}