package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ui.MainApp;

import java.time.LocalDate;
import java.util.List;

/**
 * Vista principal para la gestión de gastos usando iconos PNG.
 * 
 * @version 2.0
 * @since 2025-11-15
 */
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
    
    /**
     * Crea un ImageView para usar como icono.
     */
    private ImageView crearIcono(String rutaImagen, int tamano) {
        try {
            Image imagen = new Image(getClass().getResourceAsStream(rutaImagen));
            ImageView imageView = new ImageView(imagen);
            imageView.setFitWidth(tamano);
            imageView.setFitHeight(tamano);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + rutaImagen);
            return new ImageView();
        }
    }
    
    private void crearVista() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Título con icono
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconoTitulo = crearIcono("/images/gastos.png", 32);
        Label titulo = new Label("GESTIÓN DE GASTOS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        headerBox.getChildren().addAll(iconoTitulo, titulo);

        // Botones de acción con iconos
        HBox botonesAccion = new HBox(10);
        botonesAccion.setAlignment(Pos.CENTER_LEFT);
        
        Button btnNuevo = crearBotonConIcono("Nuevo Gasto", "/images/agregar.png", 16);
        btnNuevo.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnNuevo.setOnAction(e -> mostrarDialogoNuevoGasto());
        
        Button btnModificar = crearBotonConIcono("Modificar", "/images/lapiz.png", 16);
        btnModificar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnModificar.setOnAction(e -> modificarGastoSeleccionado());
        
        Button btnEliminar = crearBotonConIcono("Eliminar", "/images/eliminar.png", 16);
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnEliminar.setOnAction(e -> eliminarGastosSeleccionados());
        
        Button btnActualizar = crearBotonConIcono("Actualizar", "/images/actualizar-datos.png", 16);
        btnActualizar.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> actualizar());

        Button btnFiltros = crearBotonConIcono("Filtrar Gastos", "/images/filtrar.png", 16);
        btnFiltros.setStyle("-fx-background-color: #9B59B6; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnFiltros.setOnAction(e -> mostrarDialogoFiltros());
        
        botonesAccion.getChildren().addAll(btnNuevo, btnModificar, btnEliminar, btnActualizar, btnFiltros);

        lblFiltroActivo = new Label("");
        lblFiltroActivo.setStyle("-fx-font-size: 13px; -fx-text-fill: #9B59B6; -fx-font-weight: bold;");

        // Tabla de gastos con selección múltiple
        tablaGastos = new TableView<>();
        tablaGastos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaGastos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Columnas con iconos en los headers
        TableColumn<Gasto, String> colFecha = new TableColumn<>();
        Label lblFecha = new Label(" Fecha");
        lblFecha.setGraphic(crearIcono("/images/calendario.png", 16));
        colFecha.setGraphic(lblFecha);
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setPrefWidth(120);
        
        TableColumn<Gasto, Double> colCantidad = new TableColumn<>();
        Label lblCantidad = new Label(" Cantidad (EUR)");
        lblCantidad.setGraphic(crearIcono("/images/billete-de-banco.png", 16));
        colCantidad.setGraphic(lblCantidad);
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setPrefWidth(120);
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<Gasto, String> colCategoria = new TableColumn<>();
        Label lblCategoria = new Label(" Categoría");
        lblCategoria.setGraphic(crearIcono("/images/carpeta.png", 16));
        colCategoria.setGraphic(lblCategoria);
        colCategoria.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategoria() != null ? 
                cellData.getValue().getCategoria().getNombre() : "Sin categoría"
            )
        );
        colCategoria.setPrefWidth(150);
        
        TableColumn<Gasto, String> colDescripcion = new TableColumn<>();
        Label lblDescripcion = new Label(" Descripción");
        lblDescripcion.setGraphic(crearIcono("/images/archivo.png", 16));
        colDescripcion.setGraphic(lblDescripcion);
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDescripcion.setPrefWidth(300);
        
        tablaGastos.getColumns().addAll(colFecha, colCantidad, colCategoria, colDescripcion);
        VBox.setVgrow(tablaGastos, Priority.ALWAYS);

        // Panel de totales
        HBox panelTotales = new HBox(20);
        panelTotales.setAlignment(Pos.CENTER_RIGHT);
        panelTotales.setPadding(new Insets(10));
        panelTotales.setStyle("-fx-background-color: #ECF0F1; -fx-background-radius: 5;");
        
        HBox totalBox = new HBox(5);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        ImageView iconoTotal = crearIcono("/images/billete-de-banco.png", 20);
        Label lblTotalTexto = new Label("Total de Gastos:");
        lblTotalTexto.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        lblTotal = new Label("0.00 EUR");
        lblTotal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");
        
        totalBox.getChildren().addAll(iconoTotal, lblTotalTexto);
        panelTotales.getChildren().addAll(totalBox, lblTotal);
        
        view.getChildren().addAll(headerBox, botonesAccion, lblFiltroActivo, tablaGastos, panelTotales);
        
        scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
    
    /**
     * Crea un botón con icono PNG.
     */
    private Button crearBotonConIcono(String texto, String rutaIcono, int tamanoIcono) {
        Button btn = new Button(" " + texto);
        btn.setGraphic(crearIcono(rutaIcono, tamanoIcono));
        btn.setContentDisplay(ContentDisplay.LEFT);
        return btn;
    }
    
    private void mostrarDialogoNuevoGasto() {
        Dialog<Gasto> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Gasto");
        dialog.setHeaderText("Registrar un nuevo gasto");
        
        // Icono en el header
        dialog.setGraphic(crearIcono("/images/agregar.png", 48));
        
        ButtonType btnAceptar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("0.00");
        
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                txtCantidad.setText(oldVal);
            }
        });
        
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción del gasto");
        
        ComboBox<Categoria> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(fachada.getControladorCategorias().obtenerTodasLasCategorias());
        cbCategoria.setPromptText("Seleccione categoría");
        
        // Labels con iconos
        Label lblCantidad = new Label(" Cantidad (€):");
        lblCantidad.setGraphic(crearIcono("/images/billete-de-banco.png", 16));
        
        Label lblFecha = new Label(" Fecha:");
        lblFecha.setGraphic(crearIcono("/images/calendario.png", 16));
        
        Label lblDescripcion = new Label(" Descripción:");
        lblDescripcion.setGraphic(crearIcono("/images/archivo.png", 16));
        
        Label lblCategoria = new Label(" Categoría:");
        lblCategoria.setGraphic(crearIcono("/images/carpeta.png", 16));
        
        grid.add(lblCantidad, 0, 0);
        grid.add(txtCantidad, 1, 0);
        grid.add(lblFecha, 0, 1);
        grid.add(dpFecha, 1, 1);
        grid.add(lblDescripcion, 0, 2);
        grid.add(txtDescripcion, 1, 2);
        grid.add(lblCategoria, 0, 3);
        grid.add(cbCategoria, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        javafx.scene.Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnAceptar);
        btnGuardarNode.setDisable(true);
        
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            btnGuardarNode.setDisable(newVal.trim().isEmpty() || cbCategoria.getValue() == null);
        });
        
        cbCategoria.valueProperty().addListener((obs, oldVal, newVal) -> {
            btnGuardarNode.setDisable(txtCantidad.getText().trim().isEmpty() || newVal == null);
        });
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                try {
                    double cantidad = Double.parseDouble(txtCantidad.getText());
                    LocalDate fecha = dpFecha.getValue();
                    String descripcion = txtDescripcion.getText();
                    Categoria categoria = cbCategoria.getValue();
                    
                    if (categoria == null) {
                        mainApp.getNotificacionManager().mostrarError(
                            "Error de Validación",
                            "Debe seleccionar una categoría"
                        );
                        return null;
                    }
                    
                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        descripcion = "Sin descripción";
                    }
                    
                    fachada.getControladorGastos().registrarGasto(
                        cantidad, fecha, descripcion, categoria.getNombre()
                    );
                    
                    mainApp.getNotificacionManager().mostrarExito(
                        "Gasto Registrado",
                        String.format("%.2f € en %s", cantidad, categoria.getNombre())
                    );
                    
                    return new Gasto(cantidad, fecha, descripcion, categoria);
                    
                } catch (NumberFormatException e) {
                    mainApp.getNotificacionManager().mostrarError(
                        "Error de Formato",
                        "La cantidad debe ser un número válido"
                    );
                } catch (Exception e) {
                    mainApp.getNotificacionManager().mostrarError(
                        "Error",
                        "Error al crear gasto: " + e.getMessage()
                    );
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(gasto -> {
            actualizar();
            mainApp.verificarNuevasAlertas();
        });
    }
    
    private void modificarGastoSeleccionado() {
        Gasto gastoSeleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        
        if (gastoSeleccionado == null) {
            mainApp.getNotificacionManager().mostrarAdvertencia(
                "Selección requerida",
                "Debe seleccionar un gasto para modificar"
            );
            return;
        }
        
        Dialog<Gasto> dialog = new Dialog<>();
        dialog.setTitle("Modificar Gasto");
        dialog.setHeaderText("Editar información del gasto");
        dialog.setGraphic(crearIcono("/images/lapiz.png", 48));
        
        ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtCantidad = new TextField(String.valueOf(gastoSeleccionado.getCantidad()));
        
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                txtCantidad.setText(oldVal);
            }
        });
        
        DatePicker dpFecha = new DatePicker(gastoSeleccionado.getFecha());
        TextField txtDescripcion = new TextField(gastoSeleccionado.getDescripcion());
        
        ComboBox<Categoria> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(fachada.getControladorCategorias().obtenerTodasLasCategorias());
        cbCategoria.setValue(gastoSeleccionado.getCategoria());
        
        Label lblCantidad = new Label(" Cantidad (€):");
        lblCantidad.setGraphic(crearIcono("/images/billete-de-banco.png", 16));
        
        Label lblFecha = new Label(" Fecha:");
        lblFecha.setGraphic(crearIcono("/images/calendario.png", 16));
        
        Label lblDescripcion = new Label(" Descripción:");
        lblDescripcion.setGraphic(crearIcono("/images/archivo.png", 16));
        
        Label lblCategoria = new Label(" Categoría:");
        lblCategoria.setGraphic(crearIcono("/images/carpeta.png", 16));
        
        grid.add(lblCantidad, 0, 0);
        grid.add(txtCantidad, 1, 0);
        grid.add(lblFecha, 0, 1);
        grid.add(dpFecha, 1, 1);
        grid.add(lblDescripcion, 0, 2);
        grid.add(txtDescripcion, 1, 2);
        grid.add(lblCategoria, 0, 3);
        grid.add(cbCategoria, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    double cantidad = Double.parseDouble(txtCantidad.getText());
                    LocalDate fecha = dpFecha.getValue();
                    String descripcion = txtDescripcion.getText();
                    Categoria categoria = cbCategoria.getValue();
                    
                    if (categoria == null) {
                        mainApp.getNotificacionManager().mostrarError(
                            "Error de Validación",
                            "Debe seleccionar una categoría"
                        );
                        return null;
                    }
                    
                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        descripcion = "Sin descripción";
                    }
                    
                    fachada.getControladorGastos().modificarGasto(
                        gastoSeleccionado.getId(),
                        cantidad,
                        fecha,
                        descripcion,
                        categoria.getNombre()
                    );
                    
                    mainApp.getNotificacionManager().mostrarInfo(
                        "Gasto Modificado",
                        String.format("Actualizado a %.2f € en %s", cantidad, categoria.getNombre())
                    );
                    
                    return gastoSeleccionado;
                    
                } catch (NumberFormatException e) {
                    mainApp.getNotificacionManager().mostrarError(
                        "Error de Formato",
                        "La cantidad debe ser un número válido"
                    );
                } catch (Exception e) {
                    mainApp.getNotificacionManager().mostrarError(
                        "Error al modificar",
                        e.getMessage()
                    );
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(gasto -> {
            actualizar();
            mainApp.verificarNuevasAlertas();
        });
    }
    
    private void eliminarGastosSeleccionados() {
        List<Gasto> gastosSeleccionados = tablaGastos.getSelectionModel().getSelectedItems();
        
        if (gastosSeleccionados.isEmpty()) {
            mainApp.getNotificacionManager().mostrarAdvertencia(
                "Selección requerida",
                "Debe seleccionar al menos un gasto para eliminar"
            );
            return;
        }
        
        if (gastosSeleccionados.size() == 1) {
            Gasto gasto = gastosSeleccionados.get(0);
            
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar este gasto?");
            confirmacion.setGraphic(crearIcono("/images/eliminar.png", 48));
            confirmacion.setContentText(String.format(
                "Gasto: %.2f €\nDescripción: %s\nCategoría: %s\nFecha: %s",
                gasto.getCantidad(),
                gasto.getDescripcion(),
                gasto.getCategoria().getNombre(),
                gasto.getFecha()
            ));
            
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        fachada.getControladorGastos().eliminarGasto(gasto.getId());
                        
                        mainApp.getNotificacionManager().mostrarInfo(
                            "Gasto Eliminado",
                            String.format("Se eliminó el gasto de %.2f €", gasto.getCantidad())
                        );
                        
                        actualizar();
                        mainApp.verificarNuevasAlertas();
                        
                    } catch (Exception e) {
                        mainApp.getNotificacionManager().mostrarError(
                            "Error al eliminar",
                            e.getMessage()
                        );
                    }
                }
            });
        } else {
            double totalEliminar = gastosSeleccionados.stream()
                .mapToDouble(Gasto::getCantidad)
                .sum();
            
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación múltiple");
            confirmacion.setHeaderText(String.format(
                "¿Eliminar %d gastos seleccionados?", 
                gastosSeleccionados.size()
            ));
            confirmacion.setGraphic(crearIcono("/images/eliminar.png", 48));
            confirmacion.setContentText(String.format(
                "Total a eliminar: %.2f €\n\nEsta acción no se puede deshacer.",
                totalEliminar
            ));
            
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        List<Gasto> copiaGastos = new java.util.ArrayList<>(gastosSeleccionados);
                        
                        int eliminados = 0;
                        for (Gasto gasto : copiaGastos) {
                            fachada.getControladorGastos().eliminarGasto(gasto.getId());
                            eliminados++;
                        }
                        
                        mainApp.getNotificacionManager().mostrarExito(
                            "Gastos Eliminados",
                            String.format("Se eliminaron %d gastos (%.2f €)", eliminados, totalEliminar)
                        );
                        
                        actualizar();
                        mainApp.verificarNuevasAlertas();
                        
                    } catch (Exception e) {
                        mainApp.getNotificacionManager().mostrarError(
                            "Error al eliminar",
                            e.getMessage()
                        );
                    }
                }
            });
        }
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
            
            lblFiltroActivo.setText("Filtro activo: " + descripcionFiltro + 
                                   String.format(" (%d gastos, %.2f €)", gastosFiltrados.size(), total));
            
            mainApp.getNotificacionManager().mostrarInfo(
                "Filtro Aplicado",
                String.format("%d gastos encontrados (%.2f €)", gastosFiltrados.size(), total)
            );
        });
    }
    
    public void actualizar() {
        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        tablaGastos.setItems(FXCollections.observableArrayList(gastos));
        
        double total = fachada.getControladorGastos().calcularTotalGastos();
        lblTotal.setText(String.format("%.2f EUR", total));
        
        lblFiltroActivo.setText("");
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
