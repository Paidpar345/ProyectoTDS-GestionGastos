package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Alerta;
import dominio.Categoria;
import dominio.Notificacion;
import dominio.enums.PeriodoTemporal;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.MainApp;

public class AlertasView {
    private FachadaAplicacion fachada;
    private MainApp mainApp;
    private ScrollPane scrollPane;
    private VBox view;
    private TableView<Alerta> tablaAlertas;
    private ListView<Notificacion> listaNotificaciones;
    
    public AlertasView(FachadaAplicacion fachada, MainApp mainApp) {
        this.fachada = fachada;
        this.mainApp = mainApp;
        crearVista();
    }
    
    private void crearVista() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label titulo = new Label("GESTION DE ALERTAS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.6);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        
        VBox panelAlertas = crearPanelAlertas();
        VBox panelNotificaciones = crearPanelNotificaciones();
        
        splitPane.getItems().addAll(panelAlertas, panelNotificaciones);
        
        view.getChildren().addAll(titulo, splitPane);

        scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
    
    private VBox crearPanelAlertas() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        
        Label subtitulo = new Label("Alertas Configuradas");
        subtitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox botones = new HBox(10);
        Button btnNueva = new Button("+ Nueva Alerta");
        btnNueva.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 8 15;");
        btnNueva.setOnAction(e -> mostrarDialogoNuevaAlerta());
        
        Button btnActivar = new Button("Activar/Desactivar");
        btnActivar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 8 15;");
        btnActivar.setOnAction(e -> toggleAlertaSeleccionada());
        
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 8 15;");
        btnEliminar.setOnAction(e -> eliminarAlertaSeleccionada());
        
        botones.getChildren().addAll(btnNueva, btnActivar, btnEliminar);

        tablaAlertas = new TableView<>();
        tablaAlertas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Alerta, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isActiva() ? "[ACTIVA]" : "[INACTIVA]"
            )
        );

        colEstado.setCellFactory(column -> new TableCell<Alerta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("[ACTIVA]")) {
                        setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colEstado.setPrefWidth(100);
        
        TableColumn<Alerta, Double> colLimite = new TableColumn<>("Limite (EUR)");
        colLimite.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("limiteGasto"));
        colLimite.setPrefWidth(120);
        colLimite.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<Alerta, String> colPeriodo = new TableColumn<>("Periodo");
        colPeriodo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPeriodo() != null ? 
                cellData.getValue().getPeriodo().getDescripcion() : "N/A"
            )
        );
        colPeriodo.setPrefWidth(100);
        
        TableColumn<Alerta, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategoria() != null ? 
                cellData.getValue().getCategoria().getNombre() : "Global"
            )
        );
        colCategoria.setPrefWidth(120);
        
        tablaAlertas.getColumns().addAll(colEstado, colLimite, colPeriodo, colCategoria);
        VBox.setVgrow(tablaAlertas, Priority.ALWAYS);
        
        panel.getChildren().addAll(subtitulo, botones, tablaAlertas);
        return panel;
    }
    
    private VBox crearPanelNotificaciones() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        
        Label subtitulo = new Label("Notificaciones");
        subtitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Button btnMarcarLeidas = new Button("Marcar todas como leidas");
        btnMarcarLeidas.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 8 15;");
        btnMarcarLeidas.setOnAction(e -> marcarTodasLeidas());
        
        listaNotificaciones = new ListView<>();
        listaNotificaciones.setCellFactory(param -> new ListCell<Notificacion>() {
            @Override
            protected void updateItem(Notificacion notif, boolean empty) {
                super.updateItem(notif, empty);
                if (empty || notif == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(notif.getMensaje());
                    setStyle(notif.isLeida() ? 
                        "-fx-text-fill: gray;" : 
                        "-fx-font-weight: bold; -fx-background-color: #FFF3CD;");
                }
            }
        });
        VBox.setVgrow(listaNotificaciones, Priority.ALWAYS);
        
        panel.getChildren().addAll(subtitulo, btnMarcarLeidas, listaNotificaciones);
        return panel;
    }
    
    public void actualizar() {

        tablaAlertas.getItems().clear();
        tablaAlertas.setItems(FXCollections.observableArrayList(
            fachada.getControladorAlertas().obtenerTodasLasAlertas()
        ));

        tablaAlertas.refresh();
        
        listaNotificaciones.setItems(FXCollections.observableArrayList(
            fachada.getControladorAlertas().obtenerNotificacionesNoLeidas()
        ));
        
        mainApp.actualizarNotificaciones();
    }
    
    private void mostrarDialogoNuevaAlerta() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nueva Alerta");
        dialog.setHeaderText("Configurar nueva alerta de gastos");
        
        ButtonType btnAceptar = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtLimite = new TextField();
        txtLimite.setPromptText("Ej: 100.00");
        
        ComboBox<PeriodoTemporal> cbPeriodo = new ComboBox<>();
        cbPeriodo.setItems(FXCollections.observableArrayList(PeriodoTemporal.values()));
        cbPeriodo.setPromptText("Seleccione periodo");
        
        ComboBox<Categoria> cbCategoria = new ComboBox<>();
        cbCategoria.setItems(FXCollections.observableArrayList(
            fachada.getControladorCategorias().obtenerTodasLasCategorias()
        ));
        cbCategoria.setPromptText("Global (todas las categorias)");
        
        grid.add(new Label("Limite de gasto (EUR):"), 0, 0);
        grid.add(txtLimite, 1, 0);
        grid.add(new Label("Periodo:"), 0, 1);
        grid.add(cbPeriodo, 1, 1);
        grid.add(new Label("Categoria:"), 0, 2);
        grid.add(cbCategoria, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                try {
                    double limite = Double.parseDouble(txtLimite.getText());
                    PeriodoTemporal periodo = cbPeriodo.getValue();
                    Categoria categoria = cbCategoria.getValue();
                    
                    if (periodo == null) {
                        mostrarError("Debe seleccionar un periodo");
                        return null;
                    }
                    
                    String nombreCategoria = categoria != null ? categoria.getNombre() : null;
                    fachada.getControladorAlertas().crearAlerta(limite, periodo, nombreCategoria);
                    
                    mostrarExito("Alerta creada correctamente");
                    
                } catch (NumberFormatException e) {
                    mostrarError("El limite debe ser un numero valido");
                } catch (Exception e) {
                    mostrarError("Error al crear alerta: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
        actualizar();
    }
    
    
    private void toggleAlertaSeleccionada() {
        Alerta seleccionada = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Debe seleccionar una alerta");
            return;
        }
        
        boolean nuevoEstado = !seleccionada.isActiva();
        
        fachada.getControladorAlertas().modificarAlerta(
            seleccionada.getId(), 
            seleccionada.getLimiteGasto(), 
            nuevoEstado
        );

        actualizar();
        
        String mensaje = nuevoEstado ? "Alerta ACTIVADA correctamente" : "Alerta DESACTIVADA correctamente";
        mostrarExito(mensaje);
    }
    
    private void eliminarAlertaSeleccionada() {
        Alerta seleccionada = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Debe seleccionar una alerta");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setContentText("¿Eliminar esta alerta?");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                fachada.getControladorAlertas().eliminarAlerta(seleccionada.getId());
                actualizar();
                mostrarExito("Alerta eliminada");
            }
        });
    }
    
    private void marcarTodasLeidas() {
        actualizar();
        mostrarExito("Notificaciones marcadas como leidas");
    }
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public ScrollPane getView() {
        return scrollPane;
    }
}
