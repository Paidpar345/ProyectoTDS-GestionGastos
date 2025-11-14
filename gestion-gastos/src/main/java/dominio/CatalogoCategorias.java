package dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Catálogo responsable de la gestión centralizada de todas las categorías del sistema.
 * <p>
 * Permite agregar nuevas categorías evitando duplicados, buscar categorías por nombre o id, eliminarlas,
 * obtener todas las categorías y conocer la cantidad existente. Al inicializarse, crea algunas categorías por defecto típicas de uso personal.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class CatalogoCategorias {
    private List<Categoria> categorias;
    
    public CatalogoCategorias() {
        this.categorias = new ArrayList<>();
        inicializarCategoriasDefault();
    }

    private void inicializarCategoriasDefault() {
        agregarCategoria(new Categoria("Alimentación", "Compras de comida y bebida"));
        agregarCategoria(new Categoria("Transporte", "Gasolina, transporte público"));
        agregarCategoria(new Categoria("Entretenimiento", "Ocio, cine, eventos"));
        agregarCategoria(new Categoria("Salud", "Farmacia, médico"));
        agregarCategoria(new Categoria("Educación", "Libros, cursos"));
    }
    
    public void agregarCategoria(Categoria categoria) {
        validarNombreUnico(categoria.getNombre());
        categorias.add(categoria);
    }

    private void validarNombreUnico(String nombre) {
        boolean existe = categorias.stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));
        
        if (existe) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
    }
    
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }
    
    public Categoria buscarPorId(String id) {
        return categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public void eliminarCategoria(Categoria categoria) {
        categorias.remove(categoria);
    }
    
    public List<Categoria> obtenerTodas() {
        return new ArrayList<>(categorias);
    }
    
    public int cantidadCategorias() {
        return categorias.size();
    }
}
