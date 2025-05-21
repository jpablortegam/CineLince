package com.example.cinelinces;

import com.example.cinelinces.controllers.HelloController;
import com.example.cinelinces.controllers.SideBarController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1) Cargo el main-view.fxml y obtengo su controlador
        FXMLLoader mainLoader = new FXMLLoader(
                getClass().getResource("main-view.fxml")
        );
        Parent root = mainLoader.load();
        HelloController mainCtrl = mainLoader.getController();

        // 2) Cargo el sidebar con su propio FXMLLoader
        FXMLLoader sideLoader = new FXMLLoader(
                getClass().getResource("sidebar-view.fxml")
        );
        Parent sidebarNode = sideLoader.load();
        SideBarController sideCtrl = sideLoader.getController();

        // 3) Le paso la referencia del main controller al sidebar
        sideCtrl.setMainController(mainCtrl);

        // 4) Inserto el sidebar en el BorderPane (rootPane) del HelloController
        mainCtrl.getRootPane().setLeft(sidebarNode);

        // 5) Creo UNA ÚNICA escena sobre el root
        Scene scene = new Scene(root);

        // 6) Le añado tu hoja de estilos principal
        scene.getStylesheets().add(
                getClass()
                        .getResource("/com/example/styles/main.css")
                        .toExternalForm()
        );
        // (si tienes más CSS, los agregas aquí):
        // scene.getStylesheets().add(
        //     getClass()
        //         .getResource("/com/example/styles/components/sidebar.css")
        //         .toExternalForm()
        // );

        // 7) Asigno la escena y muestro el stage
        stage.setScene(scene);
        stage.setTitle("CineLince");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
