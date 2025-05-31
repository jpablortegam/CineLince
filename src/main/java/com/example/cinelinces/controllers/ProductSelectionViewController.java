package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.DAO.impl.ProductoDAOImpl;
import com.example.cinelinces.model.Producto;
import com.example.cinelinces.utils.SummaryContext;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ProductSelectionViewController {
    @FXML private VBox productsContainer;

    private final ProductoDAO productDao = new ProductoDAOImpl();
    private final List<Producto> selectedProducts = new ArrayList<>();

    @FXML
    public void initialize() {
        List<Producto> all = productDao.findAll();
        for (Producto p : all) {
            CheckBox cb = new CheckBox(p.getNombre() + " ($" + p.getPrecio() + ")");
            cb.setUserData(p);
            cb.setOnAction(e -> {
                Producto pr = (Producto) cb.getUserData();
                if (cb.isSelected()) selectedProducts.add(pr);
                else selectedProducts.remove(pr);
            });
            productsContainer.getChildren().add(cb);
        }
    }

    @FXML
    private void handleSkip() {
        saveAndClose();
    }

    @FXML
    private void handleNext() {
        saveAndClose();
        // Abrir resumen
        PurchaseSummaryViewController.showSummary();
    }

    private void saveAndClose() {
        SummaryContext.getInstance().setSelectedProducts(new ArrayList<>(selectedProducts));
        ((Stage) productsContainer.getScene().getWindow()).close();
    }
}