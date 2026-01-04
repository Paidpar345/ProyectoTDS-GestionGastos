package ui;

import controlador.FachadaAplicacion;
import dominio.Notificacion;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import controlador.NotificacionManager;
import ui.vistas.*;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Aplicación principal con iconos PNG reales.
 * 
 * @version 1.6
 * @since 2025-11-15
 */
public class MainApp extends Application {
    private FachadaAplicacion fachada;
    private BorderPane rootPane;
    private Label lblNotificaciones;
    private NotificacionManager notificacionManager;
    private GastosView gastosView;
    private CategoriasView categoriasView;
    private AlertasView alertasView;
    private CuentasView cuentasView;
    private EstadisticasView estadisticasView;
    private TimelineGastosView timelineView;
    private Set<String> notificacionesMostradas = new HashSet<>();
    private CalendarioGastosView calendarioView;


    @Override
    public void start(Stage primaryStage) {
        fachada = FachadaAplicacion.getInstancia();
        
        // Configurar icono de la aplicación
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            
            // Para MacOS - icono en el dock
            if (java.awt.Taskbar.isTaskbarSupported()) {
                java.awt.Taskbar taskbar = java.awt.Taskbar.getTaskbar();
                if (taskbar.isSupported(java.awt.Taskbar.Feature.ICON_IMAGE)) {
                    java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
                    java.awt.Image dockIcon = toolkit.getImage(getClass().getResource("/images/logo.png"));
                    taskbar.setIconImage(dockIcon);
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono de la aplicación: " + e.getMessage());
        }
        
        notificacionManager = new NotificacionManager(primaryStage);
        notificacionManager.setPosicion(Pos.TOP_RIGHT);
        
        primaryStage.setTitle("Gestión de Gastos - TDS 2025");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);

        rootPane = new BorderPane();
        rootPane.setTop(crearBarraSuperior());
        rootPane.setLeft(crearMenuLateral());

        mostrarVistaGastos();

        Scene scene = new Scene(rootPane);
       

        primaryStage.setScene(scene);
        primaryStage.show();
        
        verificarYMostrarAlertasIniciales();
        actualizarContadorNotificaciones();
        

    }

    private HBox crearBarraSuperior() {
        HBox barra = new HBox(20);
        barra.setPadding(new Insets(15, 20, 15, 20));
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setStyle("-fx-background-color: #2C3E50; -fx-border-color: #34495E; -fx-border-width: 0 0 2 0;");

        Label titulo = new Label("GESTIÓN DE GASTOS");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botón de notificaciones con icono
        ImageView iconoNotif = crearIcono("/images/senal-de-alerta.png", 20);
        Button btnNotificaciones = new Button(" Notificaciones");
        btnNotificaciones.setGraphic(iconoNotif);
        btnNotificaciones.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 8 15;");
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
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: #34495E;");

        // Botones con iconos PNG
        Button btnGastos = crearBotonMenuConIcono("Gastos", "/images/gastos.png", () -> mostrarVistaGastos());
        Button btnCategorias = crearBotonMenuConIcono("Categorías", "/images/menu.png", () -> mostrarVistaCategorias());
        Button btnAlertas = crearBotonMenuConIcono("Alertas", "/images/senal-de-alerta.png", () -> mostrarVistaAlertas());
        Button btnCuentas = crearBotonMenuConIcono("Cuentas Compartidas", "/images/cuenta-compartida.png", () -> mostrarVistaCuentas());
        Button btnEstadisticas = crearBotonMenuConIcono("Estadísticas", "/images/grafico.png", () -> mostrarVistaEstadisticas());
        Button btnTimeline = crearBotonMenuConIcono("Línea Temporal", "/images/linea-temporal.png", () -> mostrarVistaTimeline());
        

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #7F8C8D;");

        // Botones sin iconos específicos (usar emojis Unicode)
        Button btnImportar = crearBotonMenu(" Importar Datos", () -> importarDatos());
        Button btnSalir = crearBotonMenu(" Salir", () -> System.exit(0));
        Button btnCalendario = crearBotonMenuConIcono(
        	    "Calendario",
        	    "/images/calendario.png",
        	    () -> mostrarVistaCalendario()
        	);

        menu.getChildren().addAll(
        	    btnGastos,
        	    btnCategorias,
        	    btnAlertas,
        	    btnCuentas,
        	    btnEstadisticas,
        	    btnTimeline,
        	    btnCalendario,
        	    sep,
        	    btnImportar,
        	    btnSalir
        	);

        return menu;
    }

    /**
     * Crea un ImageView para usar como icono en botones.
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
            return new ImageView(); // Retorna ImageView vacío si falla
        }
    }

    /**
     * Crea un botón del menú con icono PNG.
     */
    private Button crearBotonMenuConIcono(String texto, String rutaIcono, Runnable accion) {
        ImageView icono = crearIcono(rutaIcono, 24);
        
        Button btn = new Button(" " + texto);
        btn.setGraphic(icono);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-cursor: hand; -fx-padding: 12 15;");
        
        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-padding: 12 15; -fx-background-radius: 5;"));
        
        btn.setOnMouseExited(e -> 
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-padding: 12 15;"));
        
        btn.setOnAction(e -> accion.run());
        return btn;
    }

    /**
     * Crea un botón del menú sin icono (para Importar y Salir).
     */
    private Button crearBotonMenu(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-cursor: hand; -fx-padding: 12 15;");
        
        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-padding: 12 15; -fx-background-radius: 5;"));
        
        btn.setOnMouseExited(e -> 
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-padding: 12 15;"));
        
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
    
    private void mostrarVistaCalendario() {
        if (calendarioView == null) {
            calendarioView = new CalendarioGastosView(fachada);
        } else {
            calendarioView.actualizar();
        }
        rootPane.setCenter(calendarioView.getView());
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

    private void mostrarVistaTimeline() {
        if (timelineView == null) {
            timelineView = new TimelineGastosView(fachada);
        }
        timelineView.actualizar("Último mes");
        rootPane.setCenter(timelineView.getView());
    }

    private void verificarYMostrarAlertasIniciales() {
        fachada.getControladorAlertas().verificarAlertas(
            fachada.getControladorGastos().obtenerTodosLosGastos()
        );
        
        mostrarNotificacionesPendientes();
    }

    public void verificarNuevasAlertas() {
        long contadorAntes = fachada.getControladorAlertas().contarNotificacionesNoLeidas();
        
        fachada.getControladorAlertas().verificarAlertas(
            fachada.getControladorGastos().obtenerTodosLosGastos()
        );
        
        long contadorDespues = fachada.getControladorAlertas().contarNotificacionesNoLeidas();
        
        if (contadorDespues > contadorAntes) {
            mostrarNotificacionesPendientes();
        }
        
        actualizarContadorNotificaciones();
    }

    private void mostrarNotificacionesPendientes() {
        List<Notificacion> noLeidas = fachada.getControladorAlertas().obtenerNotificacionesNoLeidas();
        
        List<Notificacion> nuevas = noLeidas.stream()
            .filter(notif -> !notificacionesMostradas.contains(notif.getId()))
            .toList();
        
        if (nuevas.isEmpty()) {
            return;
        }
        
        if (nuevas.size() > 3) {
            notificacionManager.mostrarResumenAlertas(
                nuevas.size(),
                this::mostrarNotificaciones
            );
            nuevas.forEach(n -> notificacionesMostradas.add(n.getId()));
        } else {
            for (Notificacion notif : nuevas) {
                notificacionManager.mostrarAlertaGasto(notif);
                notificacionesMostradas.add(notif.getId());
            }
        }
    }

    public void actualizarNotificaciones() {
        actualizarContadorNotificaciones();
    }
    
    private void actualizarContadorNotificaciones() {
        long count = fachada.getControladorAlertas().contarNotificacionesNoLeidas();
        lblNotificaciones.setText(String.valueOf(count));
    }
    
    public NotificacionManager getNotificacionManager() {
        return notificacionManager;
    }

    private void mostrarNotificaciones() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("🔔 Notificaciones");
        dialog.setHeaderText("Gestión de Notificaciones");

        TabPane tabPane = new TabPane();

        Tab tabNoLeidas = new Tab("📬 No leídas (" + fachada.getControladorAlertas().contarNotificacionesNoLeidas() + ")");
        tabNoLeidas.setClosable(false);

        VBox contenidoNoLeidas = new VBox(10);
        contenidoNoLeidas.setPadding(new Insets(15));

        ListView<Notificacion> listaNoLeidas = crearListaNotificaciones(
            fachada.getControladorAlertas().obtenerNotificacionesNoLeidas()
        );

        Button btnMarcarLeidas = new Button("✓ Marcar todas como leídas");
        btnMarcarLeidas.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        btnMarcarLeidas.setOnAction(e -> {
            List<String> idsAMarcar = fachada.getControladorAlertas()
                .obtenerNotificacionesNoLeidas()
                .stream()
                .map(Notificacion::getId)
                .toList();
            
            fachada.getControladorAlertas().marcarTodasLasNotificacionesComoLeidas();
            notificacionesMostradas.removeAll(idsAMarcar);
            
            dialog.close();
            actualizarContadorNotificaciones();
        });

        contenidoNoLeidas.getChildren().addAll(listaNoLeidas, btnMarcarLeidas);
        VBox.setVgrow(listaNoLeidas, Priority.ALWAYS);
        tabNoLeidas.setContent(contenidoNoLeidas);

        Tab tabHistorial = new Tab("📜 Historial completo");
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

        actualizarContadorNotificaciones();
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
                    setGraphic(null);
                } else {
                    String fechaStr = notif.getFechaGeneracion().toLocalDate().toString() + " " +
                                     notif.getFechaGeneracion().toLocalTime().toString().substring(0, 5);
                    
                    // Icono según tipo de notificación
                    ImageView icono;
                    if (notif.getMensaje().contains("superado")) {
                        icono = crearIcono("/images/senal-de-alerta.png", 16);
                    } else {
                        icono = crearIcono("/images/senal-de-alerta.png", 16);
                    }
                    
                    setGraphic(icono);
                    setText(fechaStr + " - " + notif.getMensaje());
                    
                    if (notif.isLeida()) {
                        setStyle("-fx-text-fill: gray; -fx-padding: 10;");
                    } else {
                        setStyle("-fx-font-weight: bold; -fx-background-color: #FFF3CD; -fx-padding: 10; " +
                                "-fx-border-color: #FFE082; -fx-border-width: 0 0 1 0;");
                    }
                }
            }
        });

        lista.setItems(javafx.collections.FXCollections.observableArrayList(notificaciones));
        return lista;
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

                notificacionManager.mostrarExito(
                    "✅ Importación Exitosa",
                    String.format("Importados datos desde %s", archivo.getName())
                );

                if (gastosView != null) {
                    gastosView.actualizar();
                }
                
                if (timelineView != null) {
                    timelineView.actualizar("Último mes");
                }
                
                verificarNuevasAlertas();
                
            } catch (Exception e) {
                notificacionManager.mostrarError(
                    "❌ Error de Importación",
                    "No se pudo importar: " + e.getMessage()
                );
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
