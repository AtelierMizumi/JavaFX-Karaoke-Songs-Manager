module com.javafx.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires com.h2database;
    requires org.kordamp.ikonli.javafx;

    exports com.javafx.application;
    opens com.javafx.application to javafx.fxml;
}