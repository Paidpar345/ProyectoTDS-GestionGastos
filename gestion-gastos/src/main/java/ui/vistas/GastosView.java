package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import ui.MainApp;
import java.time.LocalDate;
import java.util.List;

public class GastosView {
    private FachadaAplicacion fachada;
    private MainApp mainApp;
    private ScrollPane scrollPane;
    private VBox view;
    private TableView<Gasto> tablaGastos;
    private Label lblTotal;
    private Label lblFiltroActivo;
    
    public GastosView(FachadaAplicacion fachada, MainApp mainApp) {
        this.fachada = fachada;
        this.mainApp = mainApp;
        crearVista();
    }
    
    private void crearVista() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label titulo = new Label("GESTION DE GASTOS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        HBox botonesAccion = new HBox(10);
        botonesAccion.setAlignment(Pos.CENTER_LEFT);
        
        Button btnNuevo = new Button("+ Nuevo Gasto");
        btnNuevo.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnNuevo.setOnAction(e -> mostrarDialogoNuevoGasto());
        
        Button btnModificar = new Button("Modificar");
        btnModificar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnModificar.setOnAction(e -> modificarGastoSeleccionado());
        
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnEliminar.setOnAction(e -> eliminarGastoSeleccionado());
        
        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> actualizar());

        Button btnFiltros = new Button("Filtrar Gastos");
        btnFiltros.setStyle("-fx-background-color: #9B59B6; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnFiltros.setOnAction(e -> mostrarDialogoFiltros());
        
        botonesAccion.getChildren().addAll(btnNuevo, btnModificar, btnEliminar, btnActualizar, btnFiltros);

        lblFiltroActivo = new Label("");
        lblFiltroActivo.setStyle("-fx-font-size: 13px; -fx-text-fill: #9B59B6; -fx-font-weight: bold;");

        tablaGastos = new TableView<>();
        tablaGastos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Gasto, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setPrefWidth(120);
        
        TableColumn<Gasto, Double> colCantidad = new TableColumn<>("Cantidad (EUR)");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setPrefWidth(120);
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<Gasto, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategoria() != null ? 
                cellData.getValue().getCategoria().getNombre() : "Sin categoria"
            )
        );
        colCategoria.setPrefWidth(150);
        
        TableColumn<Gasto, String> colDescripcion = new TableColumn<>("Descripcion");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDescripcion.setPrefWidth(300);
        
        tablaGastos.getColumns().addAll(colFecha, colCantidad, colCategoria, colDescripcion);
        VBox.setVgrow(tablaGastos, Priority.ALWAYS);

        HBox panelTotales = new HBox(20);
        panelTotales.setAlignment(Pos.CENTER_RIGHT);
        panelTotales.setPadding(new Insets(10));
        panelTotales.setStyle("-fx-background-color: #ECF0F1; -fx-background-radius: 5;");
        
        Label lblTotalTexto = new Label("Total de Gastos:");
        lblTotalTexto.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        lblTotal = new Label("0.00 EUR");
        lblTotal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");
        
        panelTotales.getChildren().addAll(lblTotalTexto, lblTotal);
        
        view.getChildren().addAll(titulo, botonesAccion, lblFiltroActivo, tablaGastos, panelTotales);
        
        scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
    
    
    private void mostrarDialogoFiltros() {
        DialogoFiltros dialogo = new DialogoFiltros(fachada);
        dialogo.showAndWait().ifPresent(resultado -> {
            List<Gasto> gastosFiltrados = resultado.gastos;
            String descripcionFiltro = resultado.descripcion;

            tablaGastos.getItems().clear();
            tablaGastos.setItems(FXCollections.observableArrayList(gastosFiltrados));
            tablaGastos.refresh();
            
            double total = gastosFiltrados.stream()
                .mapToDouble(Gasto::getCantidad)
                .sum();
            lblTotal.setText(String.format("%.2f EUR", total));

            if (descripcionFiltro.isEmpty()) {
                lblFiltroActivo.setText("");
            } else {
                lblFiltroActivo.setText("Filtro activo: " + descripcionFiltro + 
                    String.format(" (%d resultados)", gastosFiltrados.size()));
            }
        });
    }
    
    public void actualizar() {
        tablaGastos.getItems().clear();
        
        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        tablaGastos.setItems(FXCollections.observableArrayList(gastos));
        tablaGastos.refresh();
        
        double total = fachada.getControladorGastos().calcularTotalGastos();
        lblTotal.setText(String.format("%.2f EUR", total));
        
        lblFiltroActivo.setText("");
        
        mainApp.actualizarNotificaciones();
    }
    
    private void mostrarDialogoNuevoGasto() {
        Dialog<Gasto> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Gasto");
        dialog.setHeaderText("Registrar un nuevo gasto");
        
        ButtonType btnAceptar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("0.00");
        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripcion del gasto");
        
        ComboBox<Categoria> cbCategoria = new ComboBox<>();
        cbCategoria.setItems(FXCollections.observableArrayList(
            fachada.getControladorCategorias().obtenerTodasLasCategorias()
        ));
        cbCategoria.setPromptText("Seleccione categoria");
        
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(dpFecha, 1, 0);
        grid.add(new Label("Cantidad (EUR):"), 0, 1);
        grid.add(txtCantidad, 1, 1);
        grid.add(new Label("Categoria:"), 0, 2);
        grid.add(cbCategoria, 1, 2);
        grid.add(new Label("Descripcion:"), 0, 3);
        grid.add(txtDescripcion, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                try {
                    double cantidad = Double.parseDouble(txtCantidad.getText());
                    LocalDate fecha = dpFecha.getValue();
                    String descripcion = txtDescripcion.getText();
                    Categoria categoria = cbCategoria.getValue();
                    
                    if (categoria == null) {
                        mostrarError("Debe seleccionar una categoria");
                        return null;
                    }
                    
                    fachada.getControladorGastos().registrarGasto(
                        cantidad, fecha, descripcion, categoria.getNombre()
                    );
                    
                    return new Gasto(cantidad, fecha, descripcion, categoria);
                    
                } catch (NumberFormatException e) {
                    mostrarError("La cantidad debe ser un numero valido");
                } catch (Exception e) {
                    mostrarError("Error al crear gasto: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(gasto -> {
            mostrarExito("Gasto registrado correctamente");
            actualizar();
        });
    }
    
    private void modificarGastoSeleccionado() {
        Gasto gastoSeleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (gastoSeleccionado == null) {
            mostrarAdvertencia("Debe seleccionar un gasto para modificar");
            return;
        }
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modificar Gasto");
        dialog.setHeaderText("Modificar datos del gasto");
        
        ButtonType btnAceptar = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        DatePicker dpFecha = new DatePicker(gastoSeleccionado.getFecha());
        TextField txtCantidad = new TextField(String.valueOf(gastoSeleccionado.getCantidad()));
        TextField txtDescripcion = new TextField(gastoSeleccionado.getDescripcion());
        
        ComboBox<Categoria> cbCategoria = new ComboBox<>();
        cbCategoria.setItems(FXCollections.observableArrayList(
            fachada.getControladorCategorias().obtenerTodasLasCategorias()
        ));
        cbCategoria.setValue(gastoSeleccionado.getCategoria());
        
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(dpFecha, 1, 0);
        grid.add(new Label("Cantidad (EUR):"), 0, 1);
        grid.add(txtCantidad, 1, 1);
        grid.add(new Label("Categoria:"), 0, 2);
        grid.add(cbCategoria, 1, 2);
        grid.add(new Label("Descripcion:"), 0, 3);
        grid.add(txtDescripcion, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                try {
                    double cantidad = Double.parseDouble(txtCantidad.getText());
                    LocalDate fecha = dpFecha.getValue();
                    String descripcion = txtDescripcion.getText();
                    Categoria categoria = cbCategoria.getValue();
                    
                    if (categoria == null) {
                        mostrarError("Debe seleccionar una categoria");
                        return null;
                    }
                    
                    fachada.getControladorGastos().modificarGasto(
                        gastoSeleccionado.getId(),
                        cantidad,
                        fecha,
                        descripcion,
                        categoria.getNombre()
                    );
                    
                    mostrarExito("Gasto modificado correctamente");
                    actualizar();
                    
                } catch (NumberFormatException e) {
                    mostrarError("La cantidad debe ser un numero valido");
                } catch (Exception e) {
                    mostrarError("Error al modificar gasto: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void eliminarGastoSeleccionado() {
        Gasto gastoSeleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (gastoSeleccionado == null) {
            mostrarAdvertencia("Debe seleccionar un gasto para eliminar");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText("¿Eliminar gasto?");
        confirmacion.setContentText("Esta accion no se puede deshacer.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                fachada.getControladorGastos().eliminarGasto(gastoSeleccionado.getId());
                mostrarExito("Gasto eliminado correctamente");
                actualizar();
            }
        });
    }
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exito");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public ScrollPane getView() {
        return scrollPane;
    }
    
    
    public static class ResultadoFiltro {
        public final List<Gasto> gastos;
        public final String descripcion;
        
        public ResultadoFiltro(List<Gasto> gastos, String descripcion) {
            this.gastos = gastos;
            this.descripcion = descripcion;
        }
    }
}
