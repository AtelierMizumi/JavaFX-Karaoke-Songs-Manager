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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.concurrent.Task;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

public class AdminAppController {

    // ObservableList
    private static final ObservableList<Song> SONGLIST;

    static {
        try {
            SONGLIST = FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllSongs());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
    private TableView<Song> previewAddTable;
    @FXML
    private TableColumn<Song, Integer> addIDCol;
    @FXML
    private TableColumn<Song, String> addTitleCol;
    @FXML
    private TableColumn<Song, String> addArtistCol;
    @FXML
    private TableColumn<Song, String> addAlbumCol;
    @FXML
    private TableColumn<Song, String> addLengthCol;


    // Delete Table
    @FXML
    private TableView<Song> previewDeleteTable;
    @FXML
    private TableColumn<Song, Integer> deleteIDCol;
    @FXML
    private TableColumn<Song, String> deleteTitleCol;
    @FXML
    private TableColumn<Song, String> deleteAlbumCol;
    @FXML
    private TableColumn<Song, String> deleteArtistCol;
    @FXML
    private TableColumn<Song, String> deleteLengthCol;
    // Edit Table
    @FXML
    private TableView<Song> previewEditTable;
    @FXML
    private TableColumn<Song, String> editArtistCol;
    @FXML
    private TableColumn<Song, Integer> editIDCol;
    @FXML
    private TableColumn<Song, String> editTitleCol;
    @FXML
    private TableColumn<Song, String> editAlbumCol;
    @FXML
    private TableColumn<Song, String> editLengthCol;

    // TextField
    @FXML
    private TextField addTabIDTextField;
    @FXML
    private TextField addTabAlbumTextField;
    @FXML
    private TextField addTabArtistTextField;
    @FXML
    private TextField addTabAudioPath;
    @FXML
    private TextField addTabTitleTextField;
    @FXML
    private TextField editAlbumTextField;
    @FXML
    private TextField editArtistTextField;
    @FXML
    private TextField editSongPathTextField;
    @FXML
    private TextField editTitleTextField;
    @FXML
    private TextField searchBar;
    @FXML
    private TextField editSearchBar;
    @FXML
    private TextField deleteSearchBar;

    // Button
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Button viewButton;

    // AnchorPane
    @FXML
    private AnchorPane addTab;
    @FXML
    private AnchorPane deleteTab;
    @FXML
    private AnchorPane editTab;
    @FXML
    private AnchorPane viewTab;

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

        // Initialize addTable
        addIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        addTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        addArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        addAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        addLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        previewAddTable.setItems(SONGLIST);
        // Create a FilteredList for addTable
        FilteredList<Song> addTabFilteredList = new FilteredList<>(SONGLIST, p -> true);

        // Init deleteTable
        deleteIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        deleteTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        deleteArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        deleteAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        deleteLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        previewDeleteTable.setItems(SONGLIST);
        // Create a FilteredList for deleteTable
        FilteredList<Song> deleteTabFilteredList = new FilteredList<>(SONGLIST, p -> true);

        // Set the filter Predicate whenever the filter changes
        deleteSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            deleteTabFilteredList.setPredicate(song -> {
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
        SortedList<Song> deleteSortedList = new SortedList<>(deleteTabFilteredList);
        // Initialize viewTable with the sorted (and filtered) data
        previewDeleteTable.setItems(deleteSortedList);
        // Allow multiple selections in the deleteTable
        previewDeleteTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Init editTable
        editIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        editTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        editArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        editAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        editLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        previewEditTable.setItems(SONGLIST);
        // Create a FilteredList for editTable
        FilteredList<Song> editTabFilteredList = new FilteredList<>(SONGLIST, p -> true);

        editSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            editTabFilteredList.setPredicate(song -> {
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
        SortedList<Song> editSortedList = new SortedList<>(editTabFilteredList);
        // Initialize previewEditTable with the sorted (and filtered) data
        previewEditTable.setItems(editSortedList);

        previewEditTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("Listener triggered"); // Debugging line
            if (newSelection != null) {
                System.out.println("New selection is not null"); // Debugging line
                Song selectedSong = previewEditTable.getSelectionModel().getSelectedItem();
                System.out.println("Selected song: " + selectedSong); // Debugging line
                editTitleTextField.setText(selectedSong.getTitle());
                editArtistTextField.setText(selectedSong.getArtist());
                editAlbumTextField.setText(selectedSong.getAlbum());
            } else {
                System.out.println("New selection is null"); // Debugging line
            }
        });
    }

    @FXML
    public void editTabEditButtonOnAction(ActionEvent event) {
        Song selectedSong = previewEditTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            String idAsString = Integer.toString(selectedSong.getId());
            String title = editTitleTextField.getText();
            String artist = editArtistTextField.getText();
            String album = editAlbumTextField.getText();
            String audioFilePath = editSongPathTextField.getText();

            // Validate title
            if (title == null || title.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Title cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Upload the audio file to the Songs folder
            Task<String> uploadAudioTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    String hashedName = null;
                    if (audioFilePath != null && !audioFilePath.trim().isEmpty()) {
                        hashedName = AudioUploadUtils.uploadAudioToDatabase(audioFilePath);
                    }
                    return hashedName;
                }
            };

            // Create a new task to get the duration of the audio file in a separate thread
            Task<String> getDurationTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    if (audioFilePath != null && !audioFilePath.trim().isEmpty()) {
                        return AudioUploadUtils.getAudioDuration(audioFilePath);
                    }
                    return null;
                }
            };

            // Create a new task to update the song in the database in a separate thread
            Task<Void> updateSongTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Get the hashed name and duration from the previous tasks
                    String hashedName = uploadAudioTask.get();
                    String duration = getDurationTask.get();

                    // Create a new Song object
                    int id = Integer.parseInt(idAsString);
                    if (hashedName == null) {
                        Song updatedSong = new Song(id, title, album, artist, duration, null);
                        DatabaseHandler.getInstance().updateSongNoAudio(updatedSong);
                    } else {
                        // delete old audio file
                        String oldAudioFilePath = DatabaseHandler.getInstance().getSongPath(id);
                        if (oldAudioFilePath != null) {
                            // Prepend the path of the Songs directory to the audio file path
                            oldAudioFilePath = "Songs/" + oldAudioFilePath;

                            // Delete the audio file
                            Path audioPath = Paths.get(oldAudioFilePath);
                            Files.deleteIfExists(audioPath);
                        }
                        Song updatedSong = new Song(id, title, album, artist, duration, hashedName);
                        DatabaseHandler.getInstance().updateSongWithNewAudio(updatedSong);
                    }

                    return null;
                }
            };

            // Update the UI when the updateSongTask is done
            updateSongTask.setOnSucceeded(e -> {
                // Refresh the tables to reflect the updated song
                try {
                    refreshTables();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            // Start the tasks in new threads
            new Thread(uploadAudioTask).start();
            new Thread(getDurationTask).start();
            new Thread(updateSongTask).start();
        }
    }
    private boolean validateID(String idAsString) {
        if (idAsString != null && !idAsString.trim().isEmpty()) {
            try {
                int parsedID = Integer.parseInt(idAsString);
                if (parsedID <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "ID must be a positive integer.", ButtonType.OK);
                    alert.showAndWait();
                    return false;
                }
                // Check if ID already exists in the database
                for (Song song : SONGLIST) {
                    if (song.getId() == parsedID) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "ID already exists.", ButtonType.OK);
                        alert.showAndWait();
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "ID must be an integer.", ButtonType.OK);
                alert.showAndWait();
                return false;
            }
        }
        return true;
    }
    @FXML
    public void addTabUploadButtonOnAction(ActionEvent event) {
        try {
            String idAsString = addTabIDTextField.getText();
            String title = addTabTitleTextField.getText();
            String album = addTabAlbumTextField.getText();
            String artist = addTabArtistTextField.getText();
            String audioFilePath = addTabAudioPath.getText();

            // Validate ID
            if (idAsString != null && !idAsString.trim().isEmpty() && !validateID(idAsString)) {
                return;
            }

            // Validate title
            if (title == null || title.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Title cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Upload the audio file to the Songs folder
            Task<String> uploadAudioTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    String hashedName = null;
                    if (audioFilePath != null && !audioFilePath.trim().isEmpty()) {
                        hashedName = AudioUploadUtils.uploadAudioToDatabase(audioFilePath);
                    }
                    return hashedName;
                }
            };

            // Create a new task to get the duration of the audio file in a separate thread
            Task<String> getDurationTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    if (audioFilePath != null && !audioFilePath.trim().isEmpty()) {
                        return AudioUploadUtils.getAudioDuration(audioFilePath);
                    }
                    return null;
                }
            };

            // Create a new task to add the new song to the database in a separate thread
            Task<Void> addSongTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Get the hashed name and duration from the previous tasks
                    String hashedName = uploadAudioTask.get();
                    String duration = getDurationTask.get();

                    // Create a new Song object
                    int id;
                    if (idAsString == null || idAsString.trim().isEmpty()) {
                        // If ID is not specified, generate a new ID
                        id = SONGLIST.isEmpty() ? 1 : SONGLIST.stream().mapToInt(Song::getId).max().orElse(0) + 1;
                    } else {
                        id = Integer.parseInt(idAsString);
                    }
                    Song newSong = new Song(id, title, album, artist, duration, hashedName);

                    // Add the new song to the database
                    DatabaseHandler.getInstance().insertSong(newSong);

                    return null;
                }
            };
            // Update the UI when the addSongTask is done
            addSongTask.setOnSucceeded(e -> {
                // Refresh the tables to reflect the new song
                try {
                    refreshTables();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            // Start the tasks in new threads
            new Thread(uploadAudioTask).start();
            new Thread(getDurationTask).start();
            new Thread(addSongTask).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void editTabUploadAudioButtonOnAction(ActionEvent event) {
        getMediaPath(editSongPathTextField);
    }

    private void uploadAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.wav", "*.mp3", "*.aac", "*.ogg", "*.flac", "*.mp4", "*.avi", "*.mkv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String audioFilePath = selectedFile.getAbsolutePath();
        } else {
            System.out.println("No file selected");
        }
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
    @FXML
    void editTabClearButtonOnAction(ActionEvent event){
        editTitleTextField.clear();
        editArtistTextField.clear();
        editAlbumTextField.clear();
        editSongPathTextField.clear();
    }
    public void refreshTables() throws SQLException {
        // Fetch the updated list of songs from the database
        ObservableList<Song> updatedSongList = FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllSongs());

        // Update the SONGLIST with the new list of songs
        SONGLIST.setAll(updatedSongList);
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
        // Get all selected songs from the deleteTable
        ObservableList<Song> selectedSongs = previewDeleteTable.getSelectionModel().getSelectedItems();

        if (selectedSongs != null && !selectedSongs.isEmpty()) {
            // Remove all selected songs from the database
            for (Song song : selectedSongs) {
                try {
                    // Get the audio file path from the database
                    String audioFilePath = DatabaseHandler.getInstance().getSongPath(song.getId());

                    if (audioFilePath != null) {
                        // Prepend the path of the Songs directory to the audio file path
                        audioFilePath = "Songs/" + audioFilePath;

                        // Delete the audio file
                        Path audioPath = Paths.get(audioFilePath);
                        Files.deleteIfExists(audioPath);
                    }

                    // Delete the song from the database
                    DatabaseHandler.getInstance().deleteSong(song.getId());
                } catch (NoSuchFileException e) {
                    System.out.println("File or directory does not exist, skipping deletion.");
                } catch (SQLException e) {
                    System.out.println("SQLException while deleting song from the database: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IOException while deleting audio file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // Refresh the tables to reflect the deletion
            try {
                refreshTables();
            } catch (SQLException e) {
                System.out.println("SQLException while refreshing tables: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    @FXML
    void addTabClearButton(ActionEvent event) {
        // clear all text fields
        addTabIDTextField.clear();
        addTabTitleTextField.clear();
        addTabArtistTextField.clear();
        addTabAlbumTextField.clear();
        addTabAudioPath.clear();
    }
    @FXML
    public void addTabUploadAudioButtonOnAction(ActionEvent event) {
        // open file chooser and set upload path to text field
        getMediaPath(addTabAudioPath);
    }
    private void getMediaPath(TextField addTabAudioPath) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.wav", "*.mp3", "*.aac", "*.ogg", "*.flac", "*.mp4", "*.avi", "*.mkv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Use the selected file
            String audioFilePath = selectedFile.getAbsolutePath();
            addTabAudioPath.setText(audioFilePath);
            // Now you can use the path wherever you need it
        } else {
            System.out.println("No file selected");
        }
    }
    @FXML
    public void editAudioUploadButtonOnAction(MouseEvent mouseEvent) {
        getMediaPath(editSongPathTextField);
    }
}

