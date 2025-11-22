package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vista de línea temporal para visualizar gastos cronológicamente.
 * Muestra gastos agrupados por fecha en orden vertical.
 */
public class TimelineGastosView {
    private FachadaAplicacion fachada;
    private BorderPane view;
    private VBox timelineContainer;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public TimelineGastosView(FachadaAplicacion fachada) {
        this.fachada = fachada;
        crearVista();
    }
    
    private void crearVista() {
        view = new BorderPane();
        view.setPadding(new Insets(20));
        
        // Título
        Label titulo = new Label("LÍNEA TEMPORAL DE GASTOS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        // Controles superiores
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(0, 0, 20, 0));
        
        ComboBox<String> cbPeriodo = new ComboBox<>();
        cbPeriodo.getItems().addAll("Últimos 7 días", "Último mes", "Últimos 3 meses", "Todo");
        cbPeriodo.setValue("Último mes");
        cbPeriodo.setOnAction(e -> actualizar(cbPeriodo.getValue()));
        
        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> actualizar(cbPeriodo.getValue()));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(titulo, spacer, new Label("Período:"), cbPeriodo, btnActualizar);
        
        // Contenedor de la timeline
        timelineContainer = new VBox(15);
        timelineContainer.setPadding(new Insets(20));
        
        ScrollPane scrollPane = new ScrollPane(timelineContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5F6FA; -fx-background-color: #F5F6FA;");
        
        view.setTop(topBar);
        view.setCenter(scrollPane);
        
        actualizar("Último mes");
    }
    
    public void actualizar(String periodo) {
        timelineContainer.getChildren().clear();
        
        List<Gasto> gastos = obtenerGastosPorPeriodo(periodo);
        
        // Agrupar por fecha
        Map<LocalDate, List<Gasto>> gastosPorFecha = gastos.stream()
            .collect(Collectors.groupingBy(Gasto::getFecha, TreeMap::new, Collectors.toList()));
        
        // Crear entradas de timeline
        for (Map.Entry<LocalDate, List<Gasto>> entrada : gastosPorFecha.entrySet()) {
            timelineContainer.getChildren().add(crearEntradaTimeline(entrada.getKey(), entrada.getValue()));
        }
        
        if (gastos.isEmpty()) {
            Label lblVacio = new Label("No hay gastos en este período");
            lblVacio.setStyle("-fx-font-size: 16px; -fx-text-fill: #7F8C8D; -fx-padding: 50;");
            timelineContainer.getChildren().add(lblVacio);
        }
    }
    
    private HBox crearEntradaTimeline(LocalDate fecha, List<Gasto> gastos) {
        HBox entrada = new HBox(20);
        entrada.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        // Columna de fecha
        VBox columnaFecha = new VBox(5);
        columnaFecha.setPrefWidth(120);
        columnaFecha.setStyle("-fx-alignment: center; -fx-border-color: #ECF0F1; -fx-border-width: 0 2 0 0; -fx-padding: 0 20 0 0;");
        
        String diaSemana = fecha.getDayOfWeek().toString().substring(0, 3);
        Label lblDia = new Label(String.valueOf(fecha.getDayOfMonth()));
        lblDia.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #3498DB;");
        
        Label lblMes = new Label(fecha.getMonth().toString().substring(0, 3) + " " + fecha.getYear());
        lblMes.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        Label lblDiaSemana = new Label(diaSemana);
        lblDiaSemana.setStyle("-fx-font-size: 14px; -fx-text-fill: #95A5A6;");
        
        columnaFecha.getChildren().addAll(lblDia, lblMes, lblDiaSemana);
        
        // Columna de gastos
        VBox columnaGastos = new VBox(10);
        HBox.setHgrow(columnaGastos, Priority.ALWAYS);
        
        double totalDia = 0;
        for (Gasto gasto : gastos) {
            totalDia += gasto.getCantidad();
            columnaGastos.getChildren().add(crearTarjetaGasto(gasto));
        }
        
        // Total del día
        HBox totalBox = new HBox(10);
        totalBox.setStyle("-fx-alignment: center-right; -fx-padding: 10 0 0 0; -fx-border-color: #ECF0F1; -fx-border-width: 1 0 0 0;");
        Label lblTotal = new Label(String.format("Total del día: %.2f €", totalDia));
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");
        totalBox.getChildren().add(lblTotal);
        
        columnaGastos.getChildren().add(totalBox);
        
        entrada.getChildren().addAll(columnaFecha, columnaGastos);
        return entrada;
    }
    
    private HBox crearTarjetaGasto(Gasto gasto) {
        HBox tarjeta = new HBox(15);
        tarjeta.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 12; -fx-background-radius: 5;");
        
        // Círculo de categoría
        Circle circulo = new Circle(8);
        circulo.setFill(obtenerColorCategoria(gasto.getCategoria()));
        VBox circBox = new VBox(circulo);
        circBox.setStyle("-fx-alignment: center;");
        
        // Información del gasto
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Label lblDescripcion = new Label(gasto.getDescripcion());
        lblDescripcion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        Label lblCategoria = new Label(gasto.getCategoria() != null ? gasto.getCategoria().getNombre() : "Sin categoría");
        lblCategoria.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        info.getChildren().addAll(lblDescripcion, lblCategoria);
        
        // Cantidad
        Label lblCantidad = new Label(String.format("%.2f €", gasto.getCantidad()));
        lblCantidad.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");
        
        tarjeta.getChildren().addAll(circBox, info, lblCantidad);
        return tarjeta;
    }
    
    private Color obtenerColorCategoria(Categoria categoria) {
        if (categoria == null) return Color.GRAY;
        
        int hash = categoria.getNombre().hashCode();
        Color[] colores = {
            Color.web("#3498DB"), Color.web("#E74C3C"), Color.web("#27AE60"),
            Color.web("#F39C12"), Color.web("#9B59B6"), Color.web("#1ABC9C"),
            Color.web("#34495E"), Color.web("#E67E22")
        };
        
        return colores[Math.abs(hash) % colores.length];
    }
    
    private List<Gasto> obtenerGastosPorPeriodo(String periodo) {
        LocalDate fechaInicio = switch (periodo) {
            case "Últimos 7 días" -> LocalDate.now().minusDays(7);
            case "Último mes" -> LocalDate.now().minusMonths(1);
            case "Últimos 3 meses" -> LocalDate.now().minusMonths(3);
            default -> LocalDate.of(2000, 1, 1);
        };
        
        return fachada.getControladorGastos().obtenerTodosLosGastos().stream()
            .filter(g -> !g.getFecha().isBefore(fechaInicio))
            .sorted(Comparator.comparing(Gasto::getFecha).reversed())
            .collect(Collectors.toList());
    }
    
    public BorderPane getView() {
        return view;
    }
}
