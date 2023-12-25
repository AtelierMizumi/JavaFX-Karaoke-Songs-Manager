package com.javafx.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import java.sql.Blob;
import javafx.concurrent.Task;

import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;

public class AppController {

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
    private TextField editTabIDTextField;
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
    private TextField deleteSearchBar;

    // Button
    @FXML
    private Button addButton;
    @FXML
    private Button addTabClearButton;
    @FXML
    private Button addTabUploadAudioButton;
    @FXML
    private Button addTabUploadButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button deleteTabDeleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Button editTabClearButton;
    @FXML
    private Button editTabEditButton;
    @FXML
    private Button editTabUploadAudioButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;
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

    // FontIcon
    @FXML
    private FontIcon editAudioUploadButton;
    private Blob audioData;

    public void initialize() throws SQLException {

        // Initialize viewTable
        viewIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        viewTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        viewAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        viewArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        viewLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        ObservableList<Song> SONGLIST = FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllSongs());
        viewTable.setItems(SONGLIST);
        // Create a FilteredList wrapping the ObservableList
        FilteredList<Song> viewTabFilteredList = new FilteredList<>(SONGLIST, p -> true);

        // Set the filter Predicate whenever the filter changes
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            viewTabFilteredList.setPredicate(song -> {
                // If filter text is empty, display all songs
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare song title, album, and artist with filter text
                String lowerCaseFilter = newValue.toLowerCase();

                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches title
                } else if (song.getAlbum().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches album
                } else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches artist
                }
                return false; // Does not match
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Song> sortedList = new SortedList<>(viewTabFilteredList);
        // Bind the SortedList comparator to the TableView comparator
        sortedList.comparatorProperty().bind(viewTable.comparatorProperty());
        // Add sorted (and filtered) data to the table
        viewTable.setItems(sortedList);

        // Initialize addTable
        addIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        addTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        addArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        addAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        addLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        previewAddTable.setItems(SONGLIST);
        // Create a FilteredList for addTable
        FilteredList<Song> addTabFilteredList = new FilteredList<>(SONGLIST, p -> true);

        // Set the filter Predicate whenever the filter changes
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            addTabFilteredList.setPredicate(song -> {
                // If filter text is empty, display all songs
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare song title, album, and artist with filter text
                String lowerCaseFilter = newValue.toLowerCase();

                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches title
                } else if (song.getAlbum().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches album
                } else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches artist
                }
                return false; // Does not match
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Song> addTabSortedList = new SortedList<>(addTabFilteredList);

        // Bind the SortedList comparator to the TableView comparator
        addTabSortedList.comparatorProperty().bind(previewAddTable.comparatorProperty());

        // Add sorted (and filtered) data to the table
        previewAddTable.setItems(addTabSortedList);


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

                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches title
                } else if (song.getAlbum().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches album
                } else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches artist
                }
                return false; // Does not match
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Song> deleteSortedList = new SortedList<>(deleteTabFilteredList);

        // Bind the SortedList comparator to the TableView comparator
        deleteSortedList.comparatorProperty().bind(previewDeleteTable.comparatorProperty());

        // Add sorted (and filtered) data to the table
        previewDeleteTable.setItems(deleteSortedList);


        // Init editTable

        editIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        editTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        editArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        editAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        editLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        previewEditTable.setItems(SONGLIST);

        // Allow multiple selections in the viewTable
        viewTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Allow multiple selections in the deleteTable
        previewDeleteTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Allow multiple selections in the editTable
        previewEditTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }
    @FXML
    void editTabUploadButtonOnAction(ActionEvent event) {
        uploadAudio();
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
        String artist = addTabArtistTextField.getText();
        String album = addTabAlbumTextField.getText();
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

        // Create a new Song object
        int id;
        if (idAsString == null || idAsString.trim().isEmpty()) {
            // If ID is not specified, generate a new ID
            id = SONGLIST.stream().mapToInt(Song::getId).max().orElse(0) + 1;
        } else {
            id = Integer.parseInt(idAsString);
        }

        // Create a new task to convert the audio file to a Blob in a separate thread
        Task<Blob> convertAudioTask = new Task<Blob>() {
            protected Blob call() throws Exception {
                if (audioFilePath != null && !audioFilePath.trim().isEmpty()) {
                    return AudioUploadUtils.convertAudioToBlob(audioFilePath);
                }
                return null;
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
                try {
                    // Wait for the convertAudioTask and getDurationTask to complete
                    Blob audioData = convertAudioTask.get();
                    String length = getDurationTask.get();

                    Song newSong = new Song(id, title, artist, album, length, audioData);

                    // Add the new song to the database
                    DatabaseHandler.getInstance().insertSong(newSong);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        new Thread(convertAudioTask).start();
        new Thread(getDurationTask).start();
        new Thread(addSongTask).start();
    } catch (Exception e) {
        e.printStackTrace();
    }
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
    void exitButtonOnAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void previewAddTableOnSort(ActionEvent event) {

    }



    @FXML
    void previewEditTableOnSort(ActionEvent event) {

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

    public void refreshTables() throws SQLException {
        // Replace the existing items in the tables with new ObservableLists
        viewTable.setItems(FXCollections.observableArrayList());
        previewAddTable.setItems(FXCollections.observableArrayList());
        previewDeleteTable.setItems(FXCollections.observableArrayList());
        previewEditTable.setItems(FXCollections.observableArrayList());

        // Repopulate the tables with the updated data
        ObservableList<Song> updatedSongList = FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllSongs());
        SONGLIST.setAll(updatedSongList);
        viewTable.setItems(SONGLIST);
        previewAddTable.setItems(SONGLIST);
        previewDeleteTable.setItems(SONGLIST);
        previewEditTable.setItems(SONGLIST);
    }

    @FXML
    void viewTableOnSort() {
        // Get the TableView's comparator
        Comparator<? super Song> comparator = viewTable.getComparator();

        // Get the sorted data from the TableView's items
        ObservableList<Song> sortedData = viewTable.getItems();
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
                    DatabaseHandler.getInstance().delete("SONGLIST", "id = " + song.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Refresh the tables to reflect the deletion
            try {
                refreshTables();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void addTabAlbumTextFieldOnAction(ActionEvent event) {}

    @FXML
    void addTabArtistTextFieldOnAction(ActionEvent event) {}

    @FXML
    void addTabAudioPathOnAction(ActionEvent event) {}

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
        getAudioPath(addTabAudioPath);
    }

    private void getAudioPath(TextField addTabAudioPath) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac", "*.ogg", "*.flac")
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
        // open file chooser and set upload path to text field
        getAudioPath(editSongPathTextField);
    }
    @FXML
    public void editTabEditButtonOnAction(ActionEvent event) {
    }
    @FXML
    public void deleteSearchBarOnAction(ActionEvent event) {

    }
}

