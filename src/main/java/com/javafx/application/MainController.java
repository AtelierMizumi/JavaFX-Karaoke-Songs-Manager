package com.javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private TextField textFieldID;
    @FXML
    private TextField textFieldTitle;
    @FXML
    private TextField textFieldArtist;
    @FXML
    private TextField textFieldAlbum;
    @FXML
    private TextField textFieldGenre;
    

    public void setApp(Main application){
        // setApp() method is used to pass reference of Main application to MainController.
        // This reference is used to call the stop() method defined in Main.java
        // when the user clicks on the close button of the main window.
    }


    @FXML
    public void searchOnAction(ActionEvent actionEvent) {
        System.out.println("Search button clicked");
    }

    public void clearOnAction(ActionEvent actionEvent) {
        // debug
        System.out.println("Clear button clicked");
        // clear all the text fields
        textFieldID.clear();
        textFieldTitle.clear();
        textFieldArtist.clear();
        textFieldAlbum.clear();
        textFieldGenre.clear();
    }
}

