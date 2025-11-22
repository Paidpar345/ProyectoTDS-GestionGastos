package controlador;

import dominio.Categoria;
import repositorio.Repositorio;
import java.util.List;
import java.util.Optional;

import catalogos.CatalogoCategorias;

/**
 * Controlador para la gestión y mantenimiento de categorías de gasto.
 * <p>
 * Coordina todas las operaciones de creación, búsqueda, eliminación y obtención de categorías a través del catálogo de categorías y
 * asegura la persistencia de los cambios en la base de datos o repositorio de categorías utilizando el patrón GRASP Controller.
 * </p>
 * @version 1.0
 * @since 2025-01-01
 */


public class ControladorCategorias {
    private Repositorio repositorio;
    private CatalogoCategorias catalogoCategorias;
    
    public ControladorCategorias(Repositorio repositorio, CatalogoCategorias catalogoCategorias) {
        this.repositorio = repositorio;
        this.catalogoCategorias = catalogoCategorias;
    }
    
    
    public void crearCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria(nombre, descripcion);
        catalogoCategorias.agregarCategoria(categoria);
        repositorio.guardarCategorias(catalogoCategorias.obtenerTodas());
    }
    
    
    public void eliminarCategoria(String nombreCategoria) {
        catalogoCategorias.buscarPorNombre(nombreCategoria)
                .ifPresent(cat -> {
                    catalogoCategorias.eliminarCategoria(cat);
                    repositorio.guardarCategorias(catalogoCategorias.obtenerTodas());
                });
    }
    
    
    public Optional<Categoria> buscarCategoriaPorNombre(String nombre) {
        return catalogoCategorias.buscarPorNombre(nombre);
    }
    
    
    public List<Categoria> obtenerTodasLasCategorias() {
        return catalogoCategorias.obtenerTodas();
    }
}
