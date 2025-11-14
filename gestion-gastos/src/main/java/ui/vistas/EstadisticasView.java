package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class EstadisticasView {
    private FachadaAplicacion fachada;
    private ScrollPane scrollPane;
    private VBox view;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;
    private Label lblResumen;
    
    public EstadisticasView(FachadaAplicacion fachada) {
        this.fachada = fachada;
        crearVista();
    }
    
    private void crearVista() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #F5F6FA;");
        
        Label titulo = new Label("ESTADISTICAS DE GASTOS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        Button btnActualizar = new Button("Actualizar Estadisticas");
        btnActualizar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> actualizar());
        
        lblResumen = new Label();
        lblResumen.setStyle("-fx-font-size: 16px; -fx-padding: 15; -fx-background-color: white; " +
                           "-fx-border-color: #DFE4EA; -fx-border-width: 1; -fx-border-radius: 5; " +
                           "-fx-background-radius: 5;");
        lblResumen.setMaxWidth(Double.MAX_VALUE);
        lblResumen.setWrapText(true);
        
        HBox panelGraficos = new HBox(20);
        panelGraficos.setAlignment(Pos.CENTER);
        HBox.setHgrow(panelGraficos, Priority.ALWAYS);
        
        pieChart = crearGraficoPastel();
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        
        barChart = crearGraficoBarras();
        HBox.setHgrow(barChart, Priority.ALWAYS);
        
        panelGraficos.getChildren().addAll(pieChart, barChart);
        VBox.setVgrow(panelGraficos, Priority.ALWAYS);
        
        view.getChildren().addAll(titulo, btnActualizar, lblResumen, panelGraficos);
        
        scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
    
    private PieChart crearGraficoPastel() {
        PieChart chart = new PieChart();
        chart.setTitle("Distribucion por Categoria");
        chart.setLabelLineLength(30);
        chart.setLegendVisible(true);
        chart.setLabelsVisible(false); // Sin etiquetas en el gráfico
        chart.setStartAngle(90);
        chart.setMinSize(450, 450);
        chart.setPrefSize(500, 500);
        chart.setMaxSize(600, 600);
        
        return chart;
    }
    
    private BarChart<String, Number> crearGraficoBarras() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mes");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Gasto (EUR)");
        yAxis.setTickLabelRotation(0);
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Gastos por Mes");
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setMinSize(450, 450);
        chart.setPrefSize(500, 500);
        chart.setMaxSize(600, 600);
        
        return chart;
    }
    
    public void actualizar() {
        actualizarResumenNumerico();
        actualizarGraficoPastel();
        actualizarGraficoBarras();
    }
    
    private void actualizarResumenNumerico() {
        List<Gasto> todosGastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        double total = fachada.getControladorGastos().calcularTotalGastos();
        
        if (todosGastos.isEmpty()) {
            lblResumen.setText("No hay gastos registrados para mostrar estadisticas");
            return;
        }
        
        double promedio = total / todosGastos.size();
        double maximo = todosGastos.stream()
                .mapToDouble(Gasto::getCantidad)
                .max()
                .orElse(0.0);
        double minimo = todosGastos.stream()
                .mapToDouble(Gasto::getCantidad)
                .min()
                .orElse(0.0);
        
        String resumen = String.format(
            "Total Gastos: %d  |  Importe Total: %.2f EUR  |  Promedio: %.2f EUR  |  Maximo: %.2f EUR  |  Minimo: %.2f EUR",
            todosGastos.size(), total, promedio, maximo, minimo
        );
        
        lblResumen.setText(resumen);
    }
    
    private void actualizarGraficoPastel() {
        Map<Categoria, List<Gasto>> porCategoria = 
            fachada.getControladorGastos().agruparPorCategoria();
        
        pieChart.getData().clear();
        
        if (porCategoria.isEmpty()) {
            pieChart.setTitle("No hay datos disponibles");
            return;
        }
        
        double totalGeneral = fachada.getControladorGastos().calcularTotalGastos();

        List<PieChart.Data> datos = porCategoria.entrySet().stream()
            .map(entry -> {
                Categoria categoria = entry.getKey();
                List<Gasto> gastos = entry.getValue();
                
                double totalCategoria = gastos.stream()
                    .mapToDouble(Gasto::getCantidad)
                    .sum();
                
                double porcentaje = (totalCategoria / totalGeneral) * 100;
                
                String etiqueta = String.format("%s (%.1f%%)", categoria.getNombre(), porcentaje);
                
                return new PieChart.Data(etiqueta, totalCategoria);
            })
            .sorted((d1, d2) -> Double.compare(d2.getPieValue(), d1.getPieValue()))
            .collect(Collectors.toList());
        
        pieChart.getData().addAll(datos);



    }

    
    private void actualizarGraficoBarras() {
        Map<Month, List<Gasto>> porMes = 
            fachada.getControladorGastos().agruparPorMes();
        
        barChart.getData().clear();
        
        if (porMes.isEmpty()) {
            barChart.setTitle("No hay datos disponibles");
            return;
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Gastos");
        
        List<Month> mesesOrdenados = porMes.keySet().stream()
            .sorted(Comparator.comparingInt(Month::getValue))
            .collect(Collectors.toList());
        
        for (Month mes : mesesOrdenados) {
            List<Gasto> gastos = porMes.get(mes);
            double total = gastos.stream()
                .mapToDouble(Gasto::getCantidad)
                .sum();
            
            String nombreMes = obtenerNombreMes(mes);
            series.getData().add(new XYChart.Data<>(nombreMes, total));
        }
        
        barChart.getData().add(series);
        
        barChart.lookupAll(".default-color0.chart-bar")
            .forEach(node -> node.setStyle("-fx-bar-fill: #3498DB;"));
    }
    
    private String obtenerNombreMes(Month mes) {
        return switch (mes) {
            case JANUARY -> "Ene";
            case FEBRUARY -> "Feb";
            case MARCH -> "Mar";
            case APRIL -> "Abr";
            case MAY -> "May";
            case JUNE -> "Jun";
            case JULY -> "Jul";
            case AUGUST -> "Ago";
            case SEPTEMBER -> "Sep";
            case OCTOBER -> "Oct";
            case NOVEMBER -> "Nov";
            case DECEMBER -> "Dic";
        };
    }
    
    public ScrollPane getView() {
        return scrollPane;
    }
}
