module com.example.cinelinces {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens com.example.cinelinces to javafx.fxml;
    exports com.example.cinelinces;
}