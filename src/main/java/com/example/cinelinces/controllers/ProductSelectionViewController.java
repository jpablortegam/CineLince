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

/**
 * Controlador para product-selection-view.fxml
 * Muestra todos los productos disponibles con un Spinner para indicar la cantidad (0..stock).
 * Ya no hay “Omitir”; si todos los Spinners quedan en 0, se asume que no se compran productos.
 */
public class ProductSelectionViewController {

    /** Grilla donde se agregan dinámicamente:
     *  - Columna 0: Label “Nombre — $Precio”
     *  - Columna 1: Spinner<Integer> (valores de 0 a stock)
     */
    @FXML private GridPane productsGrid;

    /** Botón “Siguiente” */
    @FXML private Button btnNext;

    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    /**
     * initialize() se invoca automáticamente justo después de cargar el FXML.
     * Carga todos los productos activos y crea un Spinner para cada uno.
     */
    @FXML
    public void initialize() {
        // Obtenemos todos los productos disponibles (stock > 0, estado = "Activo", etc.)
        List<ProductoDTO> todos;
        try {
            todos = productoDAO.findAllAvailable();
        } catch (Exception e) {
            // En caso de error al traerse de la BD, podemos loguear y dejar lista vacía
            e.printStackTrace();
            todos = new ArrayList<>();
        }

        int row = 0;
        for (ProductoDTO p : todos) {
            // 1) Label: “NombreProducto — $Precio”
            Label lbl = new Label(p.getNombre() + " — $" + p.getPrecio());
            lbl.setMinWidth(280);        // Ajusta según tu layout
            lbl.setStyle("-fx-font-size: 14px;");

            // 2) Spinner<Integer> con rango 0..stock
            Spinner<Integer> spinner = new Spinner<>();
            spinner.setPrefWidth(70);
            SpinnerValueFactory<Integer> vf =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, p.getStock(), 0);
            spinner.setValueFactory(vf);

            // Guardamos el ProductoDTO dentro del Spinner (para luego recuperar)
            spinner.setUserData(p);

            // Añadir a la grilla: Label en columna 0, Spinner en columna 1
            productsGrid.add(lbl, 0, row);
            productsGrid.add(spinner, 1, row);

            row++;
        }
    }

    /**
     * Al hacer clic en “Siguiente”:
     *  - Recorremos cada Spinner de productsGrid.
     *  - Si spinner.getValue() > 0, creamos un ProductoSelectionDTO (id, nombre, precio, cantidad).
     *  - Guardamos la lista de seleccionados en SummaryContext.
     *  - Abrimos la vista de resumen (“purchase-summary-view.fxml”) como modal.
     *  - Al cerrar esa ventana, cerramos esta de “selección de productos”.
     */
    @FXML
    private void handleNext() {
        List<ProductoSelectionDTO> seleccionados = new ArrayList<>();

        // Recorremos each nodo del GridPane
        for (Node nodo : productsGrid.getChildren()) {
            if (nodo instanceof Spinner<?>) {
                @SuppressWarnings("unchecked")
                Spinner<Integer> sp = (Spinner<Integer>) nodo;
                int cantidad = sp.getValue();
                if (cantidad > 0) {
                    // El userData es el ProductoDTO original
                    ProductoDTO p = (ProductoDTO) sp.getUserData();
                    // Creamos un DTO específico para la selección (incluye subtotal)
                    seleccionados.add(new ProductoSelectionDTO(
                            p.getIdProducto(),
                            p.getNombre(),
                            p.getPrecio(),
                            cantidad
                    ));
                }
            }
        }

        // Guardamos en el SummaryContext (podría ser lista vacía si todos fueron 0)
        SummaryContext.getInstance().setSelectedProducts(seleccionados);

        // Cargar la ventana de “Resumen de compra”
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

            // Al cerrar la ventana de resumen, cerramos esta de selección de productos
            Stage actual = (Stage) btnNext.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
