module com.example.cinelinces {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.prefs;
    requires java.desktop;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires jbcrypt;

    opens com.example.cinelinces to javafx.fxml;
    exports com.example.cinelinces;
    exports com.example.cinelinces.controllers;
    opens com.example.cinelinces.controllers to javafx.fxml;
}