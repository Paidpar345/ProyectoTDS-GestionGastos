package ui;

import controlador.FachadaAplicacion;
import dominio.Notificacion;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.vistas.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Aplicación principal con interfaz gráfica JavaFX.
 * <p>
 * Ventana principal que gestiona la navegación entre las diferentes vistas
 * (gastos, categorías, alertas, cuentas compartidas, estadísticas).
 * Incluye barra superior con notificaciones y menú lateral de navegación.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class MainApp extends Application {
    
    private FachadaAplicacion fachada;
    private BorderPane rootPane;
    private Label lblNotificaciones;
    
    private GastosView gastosView;
    private CategoriasView categoriasView;
    private AlertasView alertasView;
    private CuentasView cuentasView;
    private EstadisticasView estadisticasView;
    
    @Override
    public void start(Stage primaryStage) {
        fachada = FachadaAplicacion.getInstancia();
        
        primaryStage.setTitle("Gestion de Gastos - TDS 2025");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);
        
        rootPane = new BorderPane();
        rootPane.setTop(crearBarraSuperior());
        rootPane.setLeft(crearMenuLateral());
        
        mostrarVistaGastos();
        
        Scene scene = new Scene(rootPane);
        
        try {
            String css = getClass().getResource("/estilos.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se encontro archivo CSS");
        }
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        actualizarNotificaciones();
    }
    
    private HBox crearBarraSuperior() {
        HBox barra = new HBox(20);
        barra.setPadding(new Insets(15, 20, 15, 20));
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setStyle("-fx-background-color: #2C3E50; -fx-border-color: #34495E; -fx-border-width: 0 0 2 0;");
        
        Label titulo = new Label("GESTION DE GASTOS");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnNotificaciones = new Button("Notificaciones");
        btnNotificaciones.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        btnNotificaciones.setTooltip(new Tooltip("Ver notificaciones"));
        btnNotificaciones.setOnAction(e -> mostrarNotificaciones());
        
        lblNotificaciones = new Label("0");
        lblNotificaciones.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        barra.getChildren().addAll(titulo, spacer, lblNotificaciones, btnNotificaciones);
        return barra;
    }
    
    private VBox crearMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(200);
        menu.setStyle("-fx-background-color: #34495E;");
        
        Button btnGastos = crearBotonMenu("Gastos", () -> mostrarVistaGastos());
        Button btnCategorias = crearBotonMenu("Categorias", () -> mostrarVistaCategorias());
        Button btnAlertas = crearBotonMenu("Alertas", () -> mostrarVistaAlertas());
        Button btnCuentas = crearBotonMenu("Cuentas Compartidas", () -> mostrarVistaCuentas());
        Button btnEstadisticas = crearBotonMenu("Estadisticas", () -> mostrarVistaEstadisticas());
        
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #7F8C8D;");
        
        Button btnImportar = crearBotonMenu("Importar Datos", () -> importarDatos());
        Button btnSalir = crearBotonMenu("Salir", () -> System.exit(0));
        
        menu.getChildren().addAll(
            btnGastos, btnCategorias, btnAlertas, btnCuentas, btnEstadisticas,
            sep, btnImportar, btnSalir
        );
        
        return menu;
    }
    
    private Button crearBotonMenu(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10;"));
        btn.setOnAction(e -> accion.run());
        return btn;
    }
    
    private void mostrarVistaGastos() {
        if (gastosView == null) {
            gastosView = new GastosView(fachada, this);
        }
        gastosView.actualizar();
        rootPane.setCenter(gastosView.getView());
    }
    
    private void mostrarVistaCategorias() {
        if (categoriasView == null) {
            categoriasView = new CategoriasView(fachada);
        }
        categoriasView.actualizar();
        rootPane.setCenter(categoriasView.getView());
    }
    
    private void mostrarVistaAlertas() {
        if (alertasView == null) {
            alertasView = new AlertasView(fachada, this);
        }
        alertasView.actualizar();
        rootPane.setCenter(alertasView.getView());
    }
    
    private void mostrarVistaCuentas() {
        if (cuentasView == null) {
            cuentasView = new CuentasView(fachada);
        }
        cuentasView.actualizar();
        rootPane.setCenter(cuentasView.getView());
    }
    
    private void mostrarVistaEstadisticas() {
        if (estadisticasView == null) {
            estadisticasView = new EstadisticasView(fachada);
        }
        estadisticasView.actualizar();
        rootPane.setCenter(estadisticasView.getView());
    }
    
    public void actualizarNotificaciones() {
        long count = fachada.getControladorAlertas().contarNotificacionesNoLeidas();
        lblNotificaciones.setText(String.valueOf(count));
    }
    
    private void mostrarNotificaciones() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Notificaciones");
        dialog.setHeaderText("Gestión de Notificaciones");

        TabPane tabPane = new TabPane();

        Tab tabNoLeidas = new Tab("No leidas (" + 
            fachada.getControladorAlertas().contarNotificacionesNoLeidas() + ")");
        tabNoLeidas.setClosable(false);
        
        VBox contenidoNoLeidas = new VBox(10);
        contenidoNoLeidas.setPadding(new Insets(15));
        
        ListView<Notificacion> listaNoLeidas = crearListaNotificaciones(
            fachada.getControladorAlertas().obtenerNotificacionesNoLeidas()
        );
        
        Button btnMarcarLeidas = new Button("Marcar todas como leidas");
        btnMarcarLeidas.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        btnMarcarLeidas.setOnAction(e -> {
            fachada.getControladorAlertas().marcarTodasLasNotificacionesComoLeidas();
            dialog.close();
            actualizarNotificaciones();
        });
        
        contenidoNoLeidas.getChildren().addAll(listaNoLeidas, btnMarcarLeidas);
        VBox.setVgrow(listaNoLeidas, Priority.ALWAYS);
        tabNoLeidas.setContent(contenidoNoLeidas);

        Tab tabHistorial = new Tab("Historial completo");
        tabHistorial.setClosable(false);
        
        ListView<Notificacion> listaHistorial = crearListaNotificaciones(
            fachada.getControladorAlertas().obtenerTodasLasNotificaciones()
        );
        
        tabHistorial.setContent(listaHistorial);
        
        tabPane.getTabs().addAll(tabNoLeidas, tabHistorial);
        
        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().setPrefSize(600, 400);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
        actualizarNotificaciones();
    }

    private ListView<Notificacion> crearListaNotificaciones(List<Notificacion> notificaciones) {
        ListView<Notificacion> lista = new ListView<>();
        lista.setPrefSize(550, 300);
        lista.setCellFactory(param -> new ListCell<Notificacion>() {
            @Override
            protected void updateItem(Notificacion notif, boolean empty) {
                super.updateItem(notif, empty);
                if (empty || notif == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String fechaStr = notif.getFechaGeneracion().toLocalDate().toString() + " " +
                                     notif.getFechaGeneracion().toLocalTime().toString().substring(0, 5);
                    setText("[" + fechaStr + "] " + notif.getMensaje());
                    
                    if (notif.isLeida()) {
                        setStyle("-fx-text-fill: gray; -fx-padding: 10;");
                    } else {
                        setStyle("-fx-font-weight: bold; -fx-background-color: #FFF3CD; " +
                                "-fx-padding: 10; -fx-border-color: #FFE082; -fx-border-width: 0 0 1 0;");
                    }
                }
            }
        });
        
        lista.setItems(javafx.collections.FXCollections.observableArrayList(notificaciones));
        return lista;
    }

    private void marcarTodasNotificacionesComoLeidas() {

        fachada.getControladorAlertas().marcarTodasLasNotificacionesComoLeidas();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificaciones");
        alert.setContentText("Todas las notificaciones han sido marcadas como leidas");
        alert.showAndWait();
    }

    
    private void importarDatos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de gastos");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
            new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        
        File archivo = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        
        if (archivo != null) {
            try {
                Path rutaArchivo = archivo.toPath();
                fachada.getControladorImportador().importarDesdeArchivo(rutaArchivo);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Importacion exitosa");
                alert.setContentText("Datos importados correctamente desde: " + archivo.getName());
                alert.showAndWait();
                
                if (gastosView != null) {
                    gastosView.actualizar();
                }
                
                actualizarNotificaciones();
                
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de importacion");
                alert.setHeaderText("No se pudo importar el archivo");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
