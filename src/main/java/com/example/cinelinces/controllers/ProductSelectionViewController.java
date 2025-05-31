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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSelectionViewController {

    @FXML private GridPane productsGrid;
    @FXML private Button btnNext;
    @FXML private Button btnOmitir;

    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    /**
     * Se invoca automáticamente justo después de que se cargue el FXML.
     * Carga todos los productos y crea un Spinner por cada uno.
     */
    @FXML
    public void initialize() {
        List<ProductoDTO> todos = productoDAO.findAllAvailable();
        // Supón que findAllAvailable() devuelve lista de ProductoDTO con id, nombre, precio, stock, estado = 'Activo'.

        int row = 0;
        for (ProductoDTO p : todos) {
            // 1) Label: "NombreProducto — $Precio"
            Label lbl = new Label(p.getNombre() + " — $" + p.getPrecio());
            lbl.setMinWidth(300);
            lbl.setStyle("-fx-font-size: 14px;");

            // 2) Spinner<Integer> con 0 .. stock
            Spinner<Integer> spinner = new Spinner<>();
            spinner.setPrefWidth(70);
            SpinnerValueFactory<Integer> vf =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, p.getStock(), 0);
            spinner.setValueFactory(vf);

            // En la lista de usuario, guardamos un DTO que contenga id, nombre, precio y la referencia al spinner
            spinner.setUserData(p);

            // Añadir a la grilla: Label en columna 0, Spinner en columna 1
            productsGrid.add(lbl, 0, row);
            productsGrid.add(spinner, 1, row);

            row++;
        }
    }

    /**
     * Omitir selección de productos: no pone nada en el context y cierra la ventana.
     */
    @FXML
    private void handleOmitir() {
        // No agregamos productos al SummaryContext; dejamos la lista vacía.
        SummaryContext.getInstance().setSelectedProducts(new ArrayList<>());
        // Cerrar la ventana:
        Stage st = (Stage) btnOmitir.getScene().getWindow();
        st.close();
    }

    /**
     * Lee todas las filas de productsGrid.
     * Si el spinner > 0, crea un ProductoSelectionDTO y lo guarda en el SummaryContext.
     * Luego abre la pantalla de “Resumen de compra”.
     */
    @FXML
    private void handleNext() {
        List<ProductoSelectionDTO> seleccionados = new ArrayList<>();

        // Recorrer cada fila del GridPane
        for (Node nodo : productsGrid.getChildren()) {
            if (nodo instanceof Spinner) {
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

        // Abrir ventana modal de resumen de compra
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/purchase-summary-view.fxml")
            );
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Resumen de compra");

            dialog.setScene(new Scene(loader.load()));
            dialog.setResizable(false);

            // No es necesario pasar datos, el PurchaseSummaryViewController leerá directamente de SummaryContext
            dialog.showAndWait();

            // Al cerrar esa ventana, cerramos esta
            Stage actual = (Stage) btnNext.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
