package dominio.filtros;

import dominio.Categoria;
import dominio.Gasto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro que selecciona gastos que pertenecen a una o más categorías concretas.
 * <p>
 * Implementa la interfaz {@link Filtro} y permite seleccionar todos los gastos cuya categoría coincida con cualquiera de las especificadas.
 * Útil para analizar un subconjunto temático de gastos.
 * </p>
 * @since 2025-11-14
 */

public class FiltroCategorias implements Filtro {
    private final Set<Categoria> categorias;
    
    public FiltroCategorias(Set<Categoria> categorias) {
        this.categorias = Set.copyOf(categorias);
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        return gastos.stream()
                .filter(g -> categorias.stream().anyMatch(g::esDeCategoria))
                .collect(Collectors.toList());
    }
}
