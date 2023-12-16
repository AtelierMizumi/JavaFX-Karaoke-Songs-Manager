module com.javafx.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;

    exports com.javafx.application;
    opens com.javafx.application to javafx.fxml;
}