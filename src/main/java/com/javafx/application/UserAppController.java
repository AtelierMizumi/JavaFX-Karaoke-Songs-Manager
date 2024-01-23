package com.javafx.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class UserAppController {
    // ObservableList
    private static final ObservableList<Song> SONGLIST;

    static {
        try {
            SONGLIST = FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllSongs());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Button logoutButton;
    public Button exitButton;

    // Table View
    @FXML
    private TableView<Song> viewTable;
    @FXML
    private TableColumn<Song, Integer> viewIDCol;
    @FXML
    private TableColumn<Song, String> viewTitleCol;
    @FXML
    private TableColumn<Song, String> viewArtistCol;
    @FXML
    private TableColumn<Song, String> viewAlbumCol;
    @FXML
    private TableColumn<Song, String> viewLengthCol;
    @FXML
    private TextField searchBar;

    public void initialize() throws SQLException {
        // Initialize viewTable
        viewIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        viewTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        viewAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        viewArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        viewLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        viewTable.setItems(SONGLIST);

        viewTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                Song selectedSong = viewTable.getSelectionModel().getSelectedItem();
                if (selectedSong != null) {
                    String audioFilePath = null;
                    try {
                        audioFilePath = DatabaseHandler.getInstance().getSongPath(selectedSong.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (audioFilePath != null) {
                        // Prepend the path of the Songs directory to the audio file path
                        audioFilePath = "Songs/" + audioFilePath;

                        // Play the audio file
                        // Apparently Desk.getDesktop().open() doesn't work on Linux lel
                        // NOTE: Default application for playing media files have to be set
                        //
                        // Consider create a custom media player instead
                        if (SystemUtils.IS_OS_WINDOWS) {
                            try {
                                System.out.println("Playing: " + audioFilePath);
                                Desktop.getDesktop().open(new File(audioFilePath));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (SystemUtils.IS_OS_MAC) {
                            System.out.println("Playing: " + audioFilePath);
                            ProcessBuilder pb = new ProcessBuilder("open", audioFilePath);
                            try {
                                pb.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (SystemUtils.IS_OS_LINUX){
                            System.out.println("Playing: " + audioFilePath + " with xdg-open");
                            ProcessBuilder pb = new ProcessBuilder("xdg-open", audioFilePath);
                            try {
                                pb.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        // Create a FilteredList wrapping the ObservableList
        FilteredList<Song> viewTableFilteredList = new FilteredList<>(SONGLIST, p -> true);

        // Set the filter Predicate whenever the filter changes
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            viewTableFilteredList.setPredicate(song -> {
                // If filter text is empty, display all songs
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare song title, album, and artist with filter text
                String lowerCaseFilter = newValue.toLowerCase();

                return song.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        song.getAlbum().toLowerCase().contains(lowerCaseFilter) ||
                        song.getArtist().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Song> viewTableSortedList = new SortedList<>(viewTableFilteredList);
        // Initialize viewTable with the sorted (and filtered) data
        viewTable.setItems(viewTableSortedList);
    }

    @FXML
    void exitButtonOnAction(ActionEvent event) {
        System.exit(0);
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
}
