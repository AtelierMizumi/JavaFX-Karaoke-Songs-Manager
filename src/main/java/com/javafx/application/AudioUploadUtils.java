package com.javafx.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

public class AudioUploadUtils extends Application {

    private TextField filePathTextField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Audio Upload Field");

        HBox root = new HBox(10);

        Label label = new Label("Select an audio file:");
        filePathTextField = new TextField();
        filePathTextField.setPrefColumnCount(20);
        filePathTextField.setEditable(false);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> openFileChooser());

        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(e -> processUploadedFile());

        root.getChildren().addAll(label, filePathTextField, browseButton, uploadButton);

        Scene scene = new Scene(root, 500, 100);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Update the file path text field with the selected file path
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void processUploadedFile() {
        // Implement the logic to upload the audio file to the database
        // Use filePathTextField.getText() to get the selected file path
        // You can use JDBC or an ORM framework like Hibernate for database interactions
        // Insert the file or its metadata into the database, depending on your requirements
        // For simplicity, we print the file path here
        String filePath = filePathTextField.getText();
        System.out.println("Uploaded audio file: " + filePath);
    }

    public static byte[] convertAudioToBytes(String filePath) {
        byte[] audioBytes = null;
        try {
            File audioFile = new File(filePath);
            audioBytes = new byte[(int) audioFile.length()];
            FileInputStream fileInputStream = new FileInputStream(audioFile);
            fileInputStream.read(audioBytes);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioBytes;
    }
}
