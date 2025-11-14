package ui.vistas;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CategoriasView {
    private FachadaAplicacion fachada;
    private VBox view;
    private ListView<Categoria> listaCategorias;
    
    public CategoriasView(FachadaAplicacion fachada) {
        this.fachada = fachada;
        crearVista();
    }
    
    private void crearVista() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label titulo = new Label("GESTION DE CATEGORIAS");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        HBox botones = new HBox(10);
        Button btnNueva = new Button("+ Nueva Categoria");
        btnNueva.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnNueva.setOnAction(e -> mostrarDialogoNuevaCategoria());
        
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 20;");
        btnEliminar.setOnAction(e -> eliminarCategoriaSeleccionada());
        
        botones.getChildren().addAll(btnNueva, btnEliminar);
        
        listaCategorias = new ListView<>();
        listaCategorias.setPrefHeight(400);
        VBox.setVgrow(listaCategorias, Priority.ALWAYS);
        
        view.getChildren().addAll(titulo, botones, listaCategorias);
    }
    
    public void actualizar() {
        listaCategorias.setItems(FXCollections.observableArrayList(
            fachada.getControladorCategorias().obtenerTodasLasCategorias()
        ));
    }
    
    private void mostrarDialogoNuevaCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Categoria");
        dialog.setHeaderText("Crear nueva categoria");
        dialog.setContentText("Nombre:");
        
        dialog.showAndWait().ifPresent(nombre -> {
            try {
                fachada.getControladorCategorias().crearCategoria(nombre, "");
                actualizar();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Categoria creada correctamente");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
    
    private void eliminarCategoriaSeleccionada() {
        Categoria seleccionada = listaCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Debe seleccionar una categoria");
            alert.showAndWait();
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setContentText("¿Eliminar la categoria " + seleccionada.getNombre() + "?");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    fachada.getControladorCategorias().eliminarCategoria(seleccionada.getNombre());
                    actualizar();
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Categoria eliminada");
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }
    
    public VBox getView() {
        return view;
    }
}
