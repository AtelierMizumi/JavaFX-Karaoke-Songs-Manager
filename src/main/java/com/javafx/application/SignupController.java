package com.javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SignupController {
    @FXML
    public PasswordField passwordField;
    public Button switchToLoginButton;
    @FXML
    private TextField confirmPasswordField;
    @FXML
    private TextField textFieldUsername;


    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/karaoke";
    private static final String DATABASE_USERNAME = "thuanc177";
    private static final String DATABASE_PASSWORD = "14032005";

    @FXML
    public void signupOnAction(ActionEvent actionEvent) {
        String username = textFieldUsername.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String result = validateSignup(username, password, confirmPassword);
        Alert alert;
        if (result.equals("Signup successful")) {
            alert = new Alert(Alert.AlertType.INFORMATION, result, ButtonType.OK);
            if (alert.getResult() == ButtonType.OK) {
                // close signup window
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
                // open main window
                App main = new App();
            }
        } else if (result.equals("Username already exists." + "\n" + "Do you want to login or try again?")) {
            ButtonType loginButtonType = new ButtonType("Switch to Login");
            ButtonType tryAgainButtonType = new ButtonType("Try again");
            alert = new Alert(Alert.AlertType.ERROR, result, loginButtonType, tryAgainButtonType);
            alert.setTitle("Signup Failed");
            alert.setHeaderText(null);
            Optional<ButtonType> resultButtonType = alert.showAndWait();
            if (resultButtonType.isPresent() && resultButtonType.get() == loginButtonType) {
                switchToLoginOnAction(actionEvent);
            } else if (resultButtonType.isPresent() && resultButtonType.get() == tryAgainButtonType) {
                textFieldUsername.clear();
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR, result, ButtonType.OK);
            alert.setTitle("Signup Failed");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private String validateSignup(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Username and password cannot be empty.";
        } else if (username.length() < 6) {
            return "Username must be at least 6 characters.";
        } else if (password.length() < 6 || confirmPassword.length() < 6) {
            return "Password must be at least 6 characters.";
        } else if (username.contains(" ")) {
            return "Username cannot contain space.";
        } else if (password.contains(" ")) {
            return "Password cannot contain space.";
        } else if (!password.equals(confirmPassword)) {
            return "Password does not match.";
        } else {
            try {
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM userinfo WHERE username = ?");
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return "Username already exists." + "\n" + "Do you want to login or try again?";
                } else {
                    // Hash password
                    String salt =  PasswordUtils.generateSalt();
                    String hashedPassword = PasswordUtils.hashPassword(password, salt);
                    // Insert new user into database
                    preparedStatement = connection.prepareStatement("INSERT INTO userinfo (username, password, salt) VALUES (?, ?, ?)");
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, hashedPassword);
                    preparedStatement.setString(3, salt);

                    preparedStatement.executeUpdate();
                    return "Signup successful";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Signup failed";
            }
        }
    }
    public void switchToLoginOnAction(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            LaunchGUI.launch(stage, "login.fxml", "Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
