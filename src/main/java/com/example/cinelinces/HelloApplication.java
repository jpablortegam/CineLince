package com.example.cinelinces;

import com.example.cinelinces.controllers.MainViewController;
import com.example.cinelinces.controllers.SideBarViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(
                getClass().getResource("main-view.fxml")
        );
        Parent root = mainLoader.load();
        MainViewController mainCtrl = mainLoader.getController();

        FXMLLoader sideLoader = new FXMLLoader(
                getClass().getResource("sidebar-view.fxml")
        );
        Parent sidebarNode = sideLoader.load();
        SideBarViewController sideCtrl = sideLoader.getController();


        sideCtrl.setMainController(mainCtrl);
        mainCtrl.getRootPane().setLeft(sidebarNode);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass()
                        .getResource("/com/example/styles/main.css")).toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("CineLince");
        stage.setMaximized(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
