package com.javafx.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Optional;

public class AppController {
    @FXML
    public Button editTabUploadAudioButton;
    @FXML
    public FontIcon editAudioUploadButton;
    @FXML
    public Button addTabUploadAudioButton;
    @FXML
    public Button editTabEditButton;
    @FXML
    public Button addTabClearButton;
    @FXML
    public TextField addTabAudioPath;
    @FXML
    private TextField addTabArtistTextField;
    @FXML
    private TextField getAddTabAudioPathTextField;
    @FXML
    private TextField addTabAudioPathTextField;
    @FXML
    private TextField addTabAlbumTextField;
    @FXML
    private Button updateUploadButton;

    @FXML
    private Button updateButton;


    @FXML
    private Button addButton;

    @FXML
    private AnchorPane addTab;

    @FXML
    private Button deleteButton;

    @FXML
    private AnchorPane deleteTab;
    @FXML
    private TextField addTabTitleTextField;

    @FXML
    private Button deleteTabDeleteButton;

    @FXML
    private TextField editAlbumTextField;

    @FXML
    private TextField editArtistTextField;

    @FXML
    private Button editButton;

    @FXML
    private TextField editSongPathTextField;

    @FXML
    private AnchorPane editTab;

    @FXML
    private TextField editTitleTextField;

    @FXML
    private Button editUpdateButton;

    @FXML
    private Button editUploadButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TableView<?> previewDeleteTable;

    @FXML
    private TableView<?> previewEditTable;

    @FXML
    private TextField searchBar;

    @FXML
    private Button updateClearButton;

    @FXML
    private Button viewButton;

    @FXML
    private AnchorPane viewTab;

    @FXML
    private TableView<Song> viewTable;

    @FXML
    private TableColumn<Song, Integer> idCol;
    @FXML
    private TableColumn<Song, String> titleCol;
    @FXML
    private TableColumn<Song, String> albumCol;
    @FXML
    private TableColumn<Song, String> artistCol;
    @FXML
    private TableColumn<Song, String> lengthCol;

    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        ObservableList<Song> songs = FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllSongs());
        viewTable.setItems(songs);
    }
    @FXML
    void editTabUploadButtonOnAction(ActionEvent event) {
        uploadAudio();
    }
    @FXML
    void addTabUploadButtonOnAction(ActionEvent event) {
        uploadAudio();
    }
    @FXML
    void editTabUploadAudioButtonOnAction(ActionEvent event) {
        uploadAudio();
    }

    private void uploadAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac", "*.ogg", "*.flac")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Use the selected file
            String audioFilePath = selectedFile.getAbsolutePath();
            // Now you can use the path wherever you need it
        } else {
            System.out.println("No file selected");
        }
    }

    @FXML
    void editSongPathTextFieldOnAction(ActionEvent event) {

    }

    @FXML
    void exitButtonOnAction(ActionEvent event) {

    }


    @FXML
    void previewEditTableOnSort(ActionEvent event) {

    }
    @FXML
    void addTabTitleTextFieldOnAction(ActionEvent event){

    }

    @FXML
    void logoutButtonOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            // open app window
            try {
                Stage stage1 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                LaunchGUI.launch(stage1, "login.fxml", "Login");
                alert.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alert.close();
        }
    }

    @FXML
    void searchBarOnAction(ActionEvent event) {

    }

    @FXML
    void tableViewOnSort(ActionEvent event) {

    }

    @FXML
    void updateButtonOnAction(ActionEvent event) {

    }

    @FXML
    void viewButtonOnAction(ActionEvent event) {
    }

    @FXML
    void updateClearButtonOnAction(ActionEvent event) {

    }

    public String getAudioDuration(String audioFilePath) {
        Media media = new Media(new File(audioFilePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        Duration duration = media.getDuration();
        int totalSeconds = (int) duration.toSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    @FXML
    void viewTableOnSort(ActionEvent event) {

    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == viewButton) {
            viewTab.setVisible(true);
            addTab.setVisible(false);
            editTab.setVisible(false);
            deleteTab.setVisible(false);
        } else if (event.getSource() == addButton) {
            viewTab.setVisible(false);
            addTab.setVisible(true);
            editTab.setVisible(false);
            deleteTab.setVisible(false);
        } else if (event.getSource() == editButton) {
            viewTab.setVisible(false);
            addTab.setVisible(false);
            editTab.setVisible(true);
            deleteTab.setVisible(false);
        } else if (event.getSource() == deleteButton) {
            viewTab.setVisible(false);
            addTab.setVisible(false);
            editTab.setVisible(false);
            deleteTab.setVisible(true);
        }
    }
    @FXML
    void deleteTabDeleteButtonOnAction(ActionEvent event) {

    }

    void addTabAlbumTextFieldOnAction(ActionEvent event) {

    }

    @FXML
    void addTabArtistTextFieldOnAction(ActionEvent event) {

    }

    @FXML
    void addTabAudioPathOnAction(ActionEvent event) {

    }

    @FXML
    void addTabClearButton(ActionEvent event) {

    }

    public void addTabUploadAudioButtonOnAction(ActionEvent event) {
    }

    public void editAudioUploadButtonOnAction(MouseEvent mouseEvent) {
    }

    public void editTabEditButtonOnAction(ActionEvent event) {
    }
}

