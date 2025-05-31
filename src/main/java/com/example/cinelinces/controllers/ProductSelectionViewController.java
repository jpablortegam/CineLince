package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.DAO.impl.ProductoDAOImpl;
import com.example.cinelinces.model.DTO.ProductoDTO;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.utils.SummaryContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProductSelectionViewController {

    @FXML
    private GridPane productsGrid;
    @FXML
    private Button btnNext;

    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    @FXML
    public void initialize() {
        List<ProductoDTO> todos;
        try {
            todos = productoDAO.findAllAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            todos = new ArrayList<>();
        }

        int row = 0;
        for (ProductoDTO p : todos) {
            Label lbl = new Label(p.getNombre() + " — $" + p.getPrecio());
            lbl.setMinWidth(280);
            lbl.setStyle("-fx-font-size: 14px;");

            Spinner<Integer> spinner = new Spinner<>();
            spinner.setPrefWidth(70);
            SpinnerValueFactory<Integer> vf =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, p.getStock(), 0);
            spinner.setValueFactory(vf);

            spinner.setUserData(p);

            productsGrid.add(lbl, 0, row);
            productsGrid.add(spinner, 1, row);

            row++;
        }
    }

    @FXML
    private void handleNext() {
        List<ProductoSelectionDTO> seleccionados = new ArrayList<>();

        for (Node nodo : productsGrid.getChildren()) {
            if (nodo instanceof Spinner<?>) {
                @SuppressWarnings("unchecked")
                Spinner<Integer> sp = (Spinner<Integer>) nodo;
                int cantidad = sp.getValue();
                if (cantidad > 0) {
                    ProductoDTO p = (ProductoDTO) sp.getUserData();
                    seleccionados.add(new ProductoSelectionDTO(
                            p.getIdProducto(),
                            p.getNombre(),
                            p.getPrecio(),
                            cantidad
                    ));
                }
            }
        }

        SummaryContext.getInstance().setSelectedProducts(seleccionados);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/purchase-summary-view.fxml")
            );
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Resumen de compra");
            dialog.setScene(new Scene(loader.load()));
            dialog.setResizable(false);
            dialog.showAndWait();

            Stage actual = (Stage) btnNext.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
