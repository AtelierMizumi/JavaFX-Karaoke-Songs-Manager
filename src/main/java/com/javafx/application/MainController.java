package com.javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

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
    public void search(ActionEvent actionEvent) {
        // Xử lý sự kiện khi nút "Search" được nhấn
        System.out.println("Search button clicked");
    }

    public void clear(ActionEvent actionEvent) {
        // debug
        System.out.println("Clear button clicked");
        // clear all the text fields

    }
}

