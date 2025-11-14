package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DialogoFiltros extends Dialog<GastosView.ResultadoFiltro> {
    private FachadaAplicacion fachada;
    
    private ComboBox<String> cbTipoFiltro;
    private VBox panelFiltrosDinamico;

    private VBox vboxCategorias;
    private GridPane gridMeses;
    private DatePicker dpFechaInicio;
    private DatePicker dpFechaFin;
    private VBox vboxCategoriasComp;
    
    public DialogoFiltros(FachadaAplicacion fachada) {
        this.fachada = fachada;
        
        setTitle("Filtrar Gastos");
        setHeaderText("Seleccione el tipo de filtro y sus criterios");
        setWidth(600);
        setHeight(500);

        ButtonType btnAplicar = new ButtonType("Aplicar Filtro", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnTodos = new ButtonType("Ver Todos", ButtonBar.ButtonData.OTHER);
        getDialogPane().getButtonTypes().addAll(btnAplicar, btnTodos, ButtonType.CANCEL);

        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(20));

        HBox selectorTipo = new HBox(10);
        Label lblTipo = new Label("Tipo de filtro:");
        lblTipo.setStyle("-fx-font-weight: bold;");
        
        cbTipoFiltro = new ComboBox<>();
        cbTipoFiltro.setItems(FXCollections.observableArrayList(
            "Por categorias",
            "Por fecha",
            "Por meses",
            "Compuesto (categorias + fecha)"
        ));
        cbTipoFiltro.setValue("Por categorias");
        cbTipoFiltro.setPrefWidth(250);
        cbTipoFiltro.setOnAction(e -> cambiarTipoFiltro());
        
        selectorTipo.getChildren().addAll(lblTipo, cbTipoFiltro);

        Separator separador = new Separator();

        panelFiltrosDinamico = new VBox(15);
        panelFiltrosDinamico.setStyle("-fx-padding: 15; -fx-background-color: #F8F9FA; " +
                                     "-fx-border-color: #DFE4EA; -fx-border-width: 1; " +
                                     "-fx-border-radius: 5; -fx-background-radius: 5;");
        panelFiltrosDinamico.setPrefHeight(300);
        
        cambiarTipoFiltro();
        
        contenido.getChildren().addAll(selectorTipo, separador, panelFiltrosDinamico);
        
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        
        getDialogPane().setContent(scroll);

        setResultConverter(dialogButton -> {
            if (dialogButton == btnAplicar) {
                return aplicarFiltro();
            } else if (dialogButton == btnTodos) {
                List<Gasto> todos = fachada.getControladorGastos().obtenerTodosLosGastos();
                return new GastosView.ResultadoFiltro(todos, "");
            }
            return null;
        });
    }
    
    private void cambiarTipoFiltro() {
        panelFiltrosDinamico.getChildren().clear();
        
        vboxCategorias = null;
        gridMeses = null;
        dpFechaInicio = null;
        dpFechaFin = null;
        vboxCategoriasComp = null;
        
        String tipoSeleccionado = cbTipoFiltro.getValue();
        
        switch (tipoSeleccionado) {
            case "Por categorias":
                crearFiltroCategoria();
                break;
            case "Por fecha":
                crearFiltroFecha();
                break;
            case "Por meses":
                crearFiltroMeses();
                break;
            case "Compuesto (categorias + fecha)":
                crearFiltroCompuesto();
                break;
        }
    }
    
    private void crearFiltroCategoria() {
        Label lblCat = new Label("Seleccione una o mas categorias:");
        lblCat.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        vboxCategorias = new VBox(8);
        vboxCategorias.setPadding(new Insets(10));
        
        List<Categoria> categorias = fachada.getControladorCategorias().obtenerTodasLasCategorias();
        for (Categoria cat : categorias) {
            CheckBox cb = new CheckBox(cat.getNombre());
            cb.setStyle("-fx-font-size: 13px;");
            vboxCategorias.getChildren().add(cb);
        }
        
        ScrollPane scroll = new ScrollPane(vboxCategorias);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: white; -fx-border-color: #DFE4EA;");
        
        panelFiltrosDinamico.getChildren().addAll(lblCat, scroll);
    }
    
    private void crearFiltroFecha() {
        Label lblInfo = new Label("Seleccione el rango de fechas:");
        lblInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        Label lblInicio = new Label("Fecha inicio:");
        dpFechaInicio = new DatePicker();
        dpFechaInicio.setPromptText("dd/mm/aaaa");
        dpFechaInicio.setPrefWidth(180);
        
        Label lblFin = new Label("Fecha fin:");
        dpFechaFin = new DatePicker();
        dpFechaFin.setPromptText("dd/mm/aaaa");
        dpFechaFin.setPrefWidth(180);
        
        grid.add(lblInicio, 0, 0);
        grid.add(dpFechaInicio, 1, 0);
        grid.add(lblFin, 0, 1);
        grid.add(dpFechaFin, 1, 1);
        
        panelFiltrosDinamico.getChildren().addAll(lblInfo, grid);
    }
    
    private void crearFiltroMeses() {
        Label lblMeses = new Label("Seleccione uno o mas meses:");
        lblMeses.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        gridMeses = new GridPane();
        gridMeses.setHgap(15);
        gridMeses.setVgap(10);
        gridMeses.setPadding(new Insets(15));
        
        String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        
        for (int i = 0; i < nombresMeses.length; i++) {
            CheckBox cb = new CheckBox(nombresMeses[i]);
            cb.setStyle("-fx-font-size: 13px;");
            gridMeses.add(cb, i % 3, i / 3);
        }
        
        panelFiltrosDinamico.getChildren().addAll(lblMeses, gridMeses);
    }
    
    private void crearFiltroCompuesto() {
        Label lblTitulo = new Label("Filtro Compuesto: Categorias + Fechas");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #9B59B6;");

        Label lblCat = new Label("Categorias:");
        lblCat.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        
        vboxCategoriasComp = new VBox(5);
        vboxCategoriasComp.setPadding(new Insets(10));
        
        List<Categoria> categorias = fachada.getControladorCategorias().obtenerTodasLasCategorias();
        for (Categoria cat : categorias) {
            CheckBox cb = new CheckBox(cat.getNombre());
            cb.setStyle("-fx-font-size: 12px;");
            vboxCategoriasComp.getChildren().add(cb);
        }
        
        ScrollPane scrollCat = new ScrollPane(vboxCategoriasComp);
        scrollCat.setFitToWidth(true);
        scrollCat.setPrefHeight(120);
        scrollCat.setStyle("-fx-background: white; -fx-border-color: #DFE4EA;");

        Label lblFechas = new Label("Rango de fechas:");
        lblFechas.setStyle("-fx-font-weight: bold; -fx-padding: 15 0 5 0;");
        
        GridPane gridFechas = new GridPane();
        gridFechas.setHgap(15);
        gridFechas.setVgap(10);
        gridFechas.setPadding(new Insets(10));
        
        Label lblInicio = new Label("Desde:");
        dpFechaInicio = new DatePicker();
        dpFechaInicio.setPromptText("dd/mm/aaaa");
        dpFechaInicio.setPrefWidth(150);
        
        Label lblFin = new Label("Hasta:");
        dpFechaFin = new DatePicker();
        dpFechaFin.setPromptText("dd/mm/aaaa");
        dpFechaFin.setPrefWidth(150);
        
        gridFechas.add(lblInicio, 0, 0);
        gridFechas.add(dpFechaInicio, 1, 0);
        gridFechas.add(lblFin, 0, 1);
        gridFechas.add(dpFechaFin, 1, 1);
        
        panelFiltrosDinamico.getChildren().addAll(lblTitulo, lblCat, scrollCat, lblFechas, gridFechas);
    }
    
    
    private GastosView.ResultadoFiltro aplicarFiltro() {
        String tipoSeleccionado = cbTipoFiltro.getValue();
        
        try {
            switch (tipoSeleccionado) {
                case "Por categorias":
                    return aplicarFiltroCategoria();
                    
                case "Por fecha":
                    return aplicarFiltroFecha();
                    
                case "Por meses":
                    return aplicarFiltroMeses();
                    
                case "Compuesto (categorias + fecha)":
                    return aplicarFiltroCompuesto();
            }
        } catch (Exception e) {
            mostrarError("Error al aplicar filtro: " + e.getMessage());
        }
        
        return null;
    }
    
    private GastosView.ResultadoFiltro aplicarFiltroCategoria() {
        List<String> categoriasSeleccionadas = new ArrayList<>();
        
        for (var node : vboxCategorias.getChildren()) {
            CheckBox cb = (CheckBox) node;
            if (cb.isSelected()) {
                categoriasSeleccionadas.add(cb.getText());
            }
        }
        
        if (categoriasSeleccionadas.isEmpty()) {
            mostrarError("Debe seleccionar al menos una categoria");
            return null;
        }
        
        List<Gasto> gastos = fachada.getControladorGastos().filtrarPorCategorias(categoriasSeleccionadas);
        String descripcion = "Categorias: " + String.join(", ", categoriasSeleccionadas);
        
        return new GastosView.ResultadoFiltro(gastos, descripcion);
    }
    
    private GastosView.ResultadoFiltro aplicarFiltroFecha() {
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarError("Debe seleccionar ambas fechas");
            return null;
        }
        
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();
        
        List<Gasto> gastos = fachada.getControladorGastos().filtrarPorFecha(inicio, fin);
        String descripcion = String.format("Fecha: %s a %s", inicio, fin);
        
        return new GastosView.ResultadoFiltro(gastos, descripcion);
    }
    
    private GastosView.ResultadoFiltro aplicarFiltroMeses() {
        List<String> mesesSeleccionados = new ArrayList<>();
        
        for (var node : gridMeses.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                if (cb.isSelected()) {
                    mesesSeleccionados.add(cb.getText());
                }
            }
        }
        
        if (mesesSeleccionados.isEmpty()) {
            mostrarError("Debe seleccionar al menos un mes");
            return null;
        }
        
        List<Gasto> gastos = fachada.getControladorGastos().filtrarPorMeses(mesesSeleccionados);
        String descripcion = "Meses: " + String.join(", ", mesesSeleccionados);
        
        return new GastosView.ResultadoFiltro(gastos, descripcion);
    }
    
    private GastosView.ResultadoFiltro aplicarFiltroCompuesto() {
        List<String> categoriasSeleccionadas = new ArrayList<>();
        
        for (var node : vboxCategoriasComp.getChildren()) {
            CheckBox cb = (CheckBox) node;
            if (cb.isSelected()) {
                categoriasSeleccionadas.add(cb.getText());
            }
        }
        
        if (categoriasSeleccionadas.isEmpty()) {
            mostrarError("Debe seleccionar al menos una categoria");
            return null;
        }
        
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarError("Debe seleccionar ambas fechas");
            return null;
        }
        
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();
        
        List<Gasto> gastos = fachada.getControladorGastos().filtrarCompuesto(
            categoriasSeleccionadas, inicio, fin
        );
        
        String descripcion = String.format("Compuesto: %s (%s a %s)", 
            String.join(", ", categoriasSeleccionadas), inicio, fin);
        
        return new GastosView.ResultadoFiltro(gastos, descripcion);
    }
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
