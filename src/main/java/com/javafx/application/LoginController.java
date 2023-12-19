package com.javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {
    @FXML
    public PasswordField passwordField;
    @FXML
    private TextField textFieldUsername;
    @FXML
    public void initialize() {
        passwordField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                loginOnAction(new ActionEvent());
            }
        });
    }
    @FXML
    public void loginOnAction(ActionEvent actionEvent) {
        String username = textFieldUsername.getText();
        String password = passwordField.getText();

        String result = validateLogin(username, password);
        Alert alert;
        if (result.equals("Login successful")) {
            alert = new Alert(Alert.AlertType.INFORMATION, result, ButtonType.OK);
            alert.setTitle("Login Successful");
            alert.setHeaderText(null);
            Optional<ButtonType> resultButtonType = alert.showAndWait();
            if (resultButtonType.isPresent() && resultButtonType.get() == ButtonType.OK) {
                // close login window
                Stage stage = (Stage) passwordField.getScene().getWindow();
                stage.close();
                // open app window
                try {
                    Stage stage1 = (Stage) passwordField.getScene().getWindow();
                    LaunchGUI.launch(stage1, "app.fxml", "Song Manager");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (result.equals("Username does not exist." + "\n" + "Do you want to sign up or try again?")) {
            ButtonType signUpButtonType = new ButtonType("Sign up");
            ButtonType tryAgainButtonType = new ButtonType("Try again");
            alert = new Alert(Alert.AlertType.ERROR, result, signUpButtonType, tryAgainButtonType);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            Optional<ButtonType> resultButtonType = alert.showAndWait();
            if (resultButtonType.isPresent() && resultButtonType.get() == signUpButtonType) {
                // close login window
                Stage stage = (Stage) passwordField.getScene().getWindow();
                stage.close();
                // open signup window
                try {
                    Stage stage1 = (Stage) passwordField.getScene().getWindow();
                    LaunchGUI.launch(stage1, "signup.fxml", "Signup new account");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultButtonType.isPresent() && resultButtonType.get() == tryAgainButtonType) {
                // clear password field
                passwordField.clear();
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR, result, ButtonType.CLOSE);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private String validateLogin(String username, String password) {
        if (username.isEmpty()) {
            return "Username cannot be empty";
        } else if (password.isEmpty()) {
            return "Password field cannot be empty";
        } else if (username.length() < 6 || password.length() < 6) {
            return "Username and password must be at least 6 characters long";
        } else if (username.contains(" ") || password.contains(" ")) {
            return "Username and password cannot contain space";
        } else {
            try {
                Connection connection = DatabaseHandler.getInstance().getConnection();
                String sql = "SELECT password, salt FROM userinfo WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return "Username does not exist." + "\n" + "Do you want to sign up or try again?";
                } else {
                    String storedPassword = resultSet.getString("password");
                    String salt = resultSet.getString("salt");
                    if (PasswordUtils.checkPassword(password, storedPassword, salt)) {
                        return "Login successful";
                    } else {
                        return "Wrong password";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Unknown error";
            }
        }
    }

    public void switchToSignupOnAction(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            LaunchGUI.launch(stage, "signup.fxml", "Signup new account");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setApp(Login application){

    }
}