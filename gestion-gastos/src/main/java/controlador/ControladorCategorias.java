package controlador;

import dominio.Categoria;
import repositorio.Repositorio;
import java.util.List;
import java.util.Optional;

import catalogos.CatalogoAlertas;
import catalogos.CatalogoCategorias;
import catalogos.CatalogoGastos;

/**
 * Controlador para la gestión y mantenimiento de categorías de gasto.
 * <p>
 * Coordina todas las operaciones de creación, búsqueda, eliminación y obtención de categorías
 * a través del catálogo de categorías y asegura la persistencia de los cambios en la base de
 * datos o repositorio de categorías utilizando el patrón GRASP Controller.
 * </p>
 * @version 1.1
 * @since 2025-01-01
 */
public class ControladorCategorias {
    private Repositorio repositorio;
    private CatalogoCategorias catalogoCategorias;
    private CatalogoGastos catalogoGastos;
    private CatalogoAlertas catalogoAlertas;
    
    public ControladorCategorias(Repositorio repositorio, 
                                CatalogoCategorias catalogoCategorias,
                                CatalogoGastos catalogoGastos,
                                CatalogoAlertas catalogoAlertas) {
        this.repositorio = repositorio;
        this.catalogoCategorias = catalogoCategorias;
        this.catalogoGastos = catalogoGastos;
        this.catalogoAlertas = catalogoAlertas;
    }
    
    /**
     * Crea una nueva categoría.
     */
    public void crearCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria(nombre, descripcion);
        catalogoCategorias.agregarCategoria(categoria);
        repositorio.guardarCategorias(catalogoCategorias.obtenerTodas());
    }
    
    /**
     * Elimina una categoría verificando que no tenga gastos ni alertas asociadas.
     * @throws IllegalStateException si la categoría tiene gastos o alertas asociadas
     */
    public void eliminarCategoria(String nombreCategoria) {
        catalogoCategorias.buscarPorNombre(nombreCategoria)
            .ifPresent(cat -> {
                // Verificar gastos asociados
                long gastosConCategoria = catalogoGastos.obtenerTodos().stream()
                    .filter(g -> g.getCategoria() != null && 
                                g.getCategoria().getId().equals(cat.getId()))
                    .count();
                
                if (gastosConCategoria > 0) {
                    throw new IllegalStateException(
                        String.format("No se puede eliminar la categoría '%s': tiene %d gastos asociados. " +
                                     "Elimine o reasigne los gastos primero.", 
                                     nombreCategoria, gastosConCategoria));
                }
                
                // Verificar alertas asociadas
                long alertasConCategoria = catalogoAlertas.obtenerTodas().stream()
                    .filter(a -> a.getCategoria() != null && 
                                a.getCategoria().getId().equals(cat.getId()))
                    .count();
                
                if (alertasConCategoria > 0) {
                    throw new IllegalStateException(
                        String.format("No se puede eliminar la categoría '%s': tiene %d alertas asociadas. " +
                                     "Elimine o reasigne las alertas primero.", 
                                     nombreCategoria, alertasConCategoria));
                }
                
                // Si todo OK, eliminar
                catalogoCategorias.eliminarCategoria(cat);
                repositorio.guardarCategorias(catalogoCategorias.obtenerTodas());
            });
    }
    
    /**
     * Elimina una categoría sin validaciones (uso interno, con precaución).
     */
    public void eliminarCategoriaForzado(String nombreCategoria) {
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
    
    /**
     * Cuenta cuántos gastos tiene asignada una categoría.
     */
    public long contarGastosPorCategoria(String nombreCategoria) {
        return catalogoCategorias.buscarPorNombre(nombreCategoria)
            .map(cat -> catalogoGastos.obtenerTodos().stream()
                .filter(g -> g.getCategoria() != null && 
                            g.getCategoria().getId().equals(cat.getId()))
                .count())
            .orElse(0L);
    }
    
    /**
     * Cuenta cuántas alertas tiene asignada una categoría.
     */
    public long contarAlertasPorCategoria(String nombreCategoria) {
        return catalogoCategorias.buscarPorNombre(nombreCategoria)
            .map(cat -> catalogoAlertas.obtenerTodas().stream()
                .filter(a -> a.getCategoria() != null && 
                            a.getCategoria().getId().equals(cat.getId()))
                .count())
            .orElse(0L);
    }
}
