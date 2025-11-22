package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.CuentaCompartida;
import dominio.Persona;
import dominio.enums.TipoDistribucion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class CuentasView {
    private FachadaAplicacion fachada;
    private ScrollPane scrollPane;
    private VBox view;
    private ListView<CuentaCompartida> listaCuentas;
    
    public CuentasView(FachadaAplicacion fachada) {
        this.fachada = fachada;
        crearVista();
    }
    
    private void crearVista() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label titulo = new Label("CUENTAS COMPARTIDAS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        HBox botonesAccion = new HBox(10);
        botonesAccion.setAlignment(Pos.CENTER_LEFT);
        
        Button btnNueva = new Button("+ Nueva Cuenta");
        btnNueva.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnNueva.setOnAction(e -> mostrarDialogoNuevaCuenta());
        
        Button btnVerDetalle = new Button("Ver Detalle");
        btnVerDetalle.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnVerDetalle.setOnAction(e -> verDetalleCuenta());
        
        Button btnAnadirGasto = new Button("Añadir Gasto");
        btnAnadirGasto.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnAnadirGasto.setOnAction(e -> anadirGastoACuenta());
        
        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> actualizar());
        
        botonesAccion.getChildren().addAll(btnNueva, btnVerDetalle, btnAnadirGasto, btnActualizar);

        listaCuentas = new ListView<>();
        listaCuentas.setCellFactory(param -> new ListCell<CuentaCompartida>() {
            @Override
            protected void updateItem(CuentaCompartida cuenta, boolean empty) {
                super.updateItem(cuenta, empty);
                if (empty || cuenta == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String info = String.format("%s (%d personas) - Tipo: %s",
                            cuenta.getNombre(),
                            cuenta.getPersonas().size(),
                            cuenta.getTipoDistribucion());
                    setText(info);
                    setStyle("-fx-padding: 10; -fx-font-size: 13px;");
                }
            }
        });
        
        VBox.setVgrow(listaCuentas, Priority.ALWAYS);
        
        view.getChildren().addAll(titulo, botonesAccion, listaCuentas);
        
        scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
    
    
    private void mostrarDialogoNuevaCuenta() {
        Dialog<CuentaCompartida> dialog = new Dialog<>();
        dialog.setTitle("Nueva Cuenta Compartida");
        dialog.setHeaderText("Crear cuenta compartida entre varias personas");
        
        ButtonType btnCrear = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCrear, ButtonType.CANCEL);

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setPrefWidth(550);

        Label lblNombre = new Label("Nombre de la cuenta:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Viaje a Madrid");

        Label lblTipo = new Label("Tipo de distribucion:");
        lblTipo.setStyle("-fx-font-weight: bold;");
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setItems(FXCollections.observableArrayList("EQUITATIVA", "PORCENTUAL"));
        cbTipo.setValue("EQUITATIVA");
        cbTipo.setPrefWidth(200);

        Separator separador = new Separator();

        Label lblPersonas = new Label("Personas en la cuenta:");
        lblPersonas.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox listaPersonas = new VBox(10);
        listaPersonas.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 15; " +
                              "-fx-border-color: #DFE4EA; -fx-border-width: 1; " +
                              "-fx-border-radius: 5; -fx-background-radius: 5;");
        listaPersonas.setPrefHeight(200);
        
        ScrollPane scrollPersonas = new ScrollPane(listaPersonas);
        scrollPersonas.setFitToWidth(true);
        scrollPersonas.setPrefHeight(220);

        Button btnAnadirPersona = new Button("+ Añadir Persona");
        btnAnadirPersona.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; " +
                                 "-fx-cursor: hand; -fx-padding: 8 15; -fx-font-weight: bold;");
        
        btnAnadirPersona.setOnAction(e -> {
            HBox filaPersona = crearFilaPersona(listaPersonas, cbTipo);
            listaPersonas.getChildren().add(filaPersona);
        });

        Label lblNota = new Label("Nota: Añada al menos 2 personas. En distribución porcentual, la suma debe ser 100%.");
        lblNota.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-font-size: 11px;");
        lblNota.setWrapText(true);
        
        contenido.getChildren().addAll(
            lblNombre, txtNombre,
            lblTipo, cbTipo,
            separador,
            lblPersonas,
            scrollPersonas,
            btnAnadirPersona,
            lblNota
        );
        
        dialog.getDialogPane().setContent(contenido);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCrear) {
                try {
                    String nombreCuenta = txtNombre.getText().trim();
                    if (nombreCuenta.isEmpty()) {
                        mostrarError("Debe ingresar un nombre para la cuenta");
                        return null;
                    }

                    List<String> nombresPersonas = new ArrayList<>();
                    Map<String, Double> porcentajes = new HashMap<>();
                    
                    for (var node : listaPersonas.getChildren()) {
                        HBox fila = (HBox) node;
                        TextField txtNombrePersona = (TextField) fila.getChildren().get(0);
                        String nombrePersona = txtNombrePersona.getText().trim();
                        
                        if (!nombrePersona.isEmpty()) {
                            nombresPersonas.add(nombrePersona);

                            if (cbTipo.getValue().equals("PORCENTUAL")) {
                                TextField txtPorcentaje = (TextField) fila.getChildren().get(2);
                                double porcentaje = Double.parseDouble(txtPorcentaje.getText());
                                porcentajes.put(nombrePersona, porcentaje);
                            }
                        }
                    }
                    
                    if (nombresPersonas.size() < 2) {
                        mostrarError("Debe añadir al menos 2 personas");
                        return null;
                    }

                    if (cbTipo.getValue().equals("PORCENTUAL")) {
                        double sumaTotal = porcentajes.values().stream()
                                .mapToDouble(Double::doubleValue)
                                .sum();
                        
                        if (Math.abs(sumaTotal - 100.0) > 0.01) {
                            mostrarError("La suma de porcentajes debe ser 100%. Actual: " + 
                                       String.format("%.2f%%", sumaTotal));
                            return null;
                        }
                    }

                    if (cbTipo.getValue().equals("EQUITATIVA")) {
                        fachada.getControladorCuentas().crearCuentaEquitativa(nombreCuenta, nombresPersonas);
                    } else {
                        fachada.getControladorCuentas().crearCuentaPorcentual(nombreCuenta, porcentajes);
                    }

                    List<Persona> personasTemp = nombresPersonas.stream()
                            .map(Persona::new)
                            .collect(Collectors.toList());
                    return new CuentaCompartida(nombreCuenta, TipoDistribucion.EQUITATIVA, personasTemp);
                    
                } catch (NumberFormatException e) {
                    mostrarError("Los porcentajes deben ser números validos");
                } catch (Exception e) {
                    mostrarError("Error al crear cuenta: " + e.getMessage());
                }
            }
            return null;
        });

        listaPersonas.getChildren().add(crearFilaPersona(listaPersonas, cbTipo));
        listaPersonas.getChildren().add(crearFilaPersona(listaPersonas, cbTipo));
        
        dialog.showAndWait().ifPresent(cuenta -> {
            mostrarExito("Cuenta creada correctamente");
            actualizar();
        });
    }
    
    
    private HBox crearFilaPersona(VBox contenedor, ComboBox<String> cbTipoDistribucion) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(5));
        fila.setStyle("-fx-background-color: white; -fx-border-color: #DFE4EA; " +
                     "-fx-border-width: 0 0 1 0;");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la persona");
        txtNombre.setPrefWidth(250);

        Label lblPorcentaje = new Label("%:");
        TextField txtPorcentaje = new TextField("0.00");
        txtPorcentaje.setPrefWidth(80);
        txtPorcentaje.setPromptText("0.00");

        lblPorcentaje.setVisible(cbTipoDistribucion.getValue().equals("PORCENTUAL"));
        txtPorcentaje.setVisible(cbTipoDistribucion.getValue().equals("PORCENTUAL"));
        
        cbTipoDistribucion.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esPorcentual = newVal.equals("PORCENTUAL");
            lblPorcentaje.setVisible(esPorcentual);
            txtPorcentaje.setVisible(esPorcentual);
        });

        Button btnEliminar = new Button("✖");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; " +
                            "-fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 5 10;");
        btnEliminar.setTooltip(new Tooltip("Eliminar persona"));
        btnEliminar.setOnAction(e -> {
            if (contenedor.getChildren().size() > 2) {
                contenedor.getChildren().remove(fila);
            } else {
                mostrarAdvertencia("Debe haber al menos 2 personas en la cuenta");
            }
        });
        
        fila.getChildren().addAll(txtNombre, lblPorcentaje, txtPorcentaje, btnEliminar);
        
        return fila;
    }
    
    private void verDetalleCuenta() {
        CuentaCompartida cuentaSeleccionada = listaCuentas.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada == null) {
            mostrarAdvertencia("Debe seleccionar una cuenta");
            return;
        }
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle de Cuenta");
        dialog.setHeaderText(cuentaSeleccionada.getNombre());
        
        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setPrefWidth(450);

        Label lblInfo = new Label("Tipo: " + cuentaSeleccionada.getTipoDistribucion());
        lblInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Persona> tablaPersonas = new TableView<>();
        
        TableColumn<Persona, String> colNombre = new TableColumn<>("Persona");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);
        
        TableColumn<Persona, Double> colSaldo = new TableColumn<>("Saldo (EUR)");
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colSaldo.setPrefWidth(150);
        colSaldo.setStyle("-fx-alignment: CENTER-RIGHT;");
        colSaldo.setCellFactory(column -> new TableCell<Persona, Double>() {
            @Override
            protected void updateItem(Double saldo, boolean empty) {
                super.updateItem(saldo, empty);
                if (empty || saldo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f EUR", saldo));
                    if (saldo > 0) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (saldo < 0) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
        
        tablaPersonas.getColumns().addAll(colNombre, colSaldo);
        tablaPersonas.setItems(FXCollections.observableArrayList(cuentaSeleccionada.getPersonas()));
        tablaPersonas.setPrefHeight(200);
        
        contenido.getChildren().addAll(lblInfo, tablaPersonas);
        
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void anadirGastoACuenta() {
        CuentaCompartida cuentaSeleccionada = listaCuentas.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada == null) {
            mostrarAdvertencia("Debe seleccionar una cuenta");
            return;
        }
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Añadir Gasto");
        dialog.setHeaderText("Añadir gasto a: " + cuentaSeleccionada.getNombre());
        
        ButtonType btnAnadir = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAnadir, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("0.00");
        
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        
        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripcion del gasto");
        
        ComboBox<Persona> cbPersona = new ComboBox<>();
        cbPersona.setItems(FXCollections.observableArrayList(cuentaSeleccionada.getPersonas()));
        cbPersona.setPromptText("Persona que paga");
        
        grid.add(new Label("Cantidad (EUR):"), 0, 0);
        grid.add(txtCantidad, 1, 0);
        grid.add(new Label("Fecha:"), 0, 1);
        grid.add(dpFecha, 1, 1);
        grid.add(new Label("Descripcion:"), 0, 2);
        grid.add(txtDescripcion, 1, 2);
        grid.add(new Label("Pagado por:"), 0, 3);
        grid.add(cbPersona, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAnadir) {
                try {
                    double cantidad = Double.parseDouble(txtCantidad.getText());
                    LocalDate fecha = dpFecha.getValue();
                    String descripcion = txtDescripcion.getText();
                    Persona persona = cbPersona.getValue();
                    
                    if (persona == null) {
                        mostrarError("Debe seleccionar la persona que paga");
                        return null;
                    }

                    fachada.getControladorCuentas().registrarGastoEnCuenta(
                        cuentaSeleccionada.getId(),
                        cantidad,
                        fecha,
                        descripcion,
                        persona.getNombre()
                    );
                    
                    mostrarExito("Gasto añadido correctamente");
                    actualizar();
                    
                } catch (NumberFormatException e) {
                    mostrarError("La cantidad debe ser un numero valido");
                } catch (Exception e) {
                    mostrarError("Error al añadir gasto: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    public void actualizar() {
        List<CuentaCompartida> cuentas = fachada.getControladorCuentas().obtenerTodasLasCuentas();
        listaCuentas.setItems(FXCollections.observableArrayList(cuentas));
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
}
