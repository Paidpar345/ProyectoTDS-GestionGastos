package dominio.filtros;

import dominio.Gasto;
import java.util.ArrayList;
import java.util.List;


/**
 * Filtro que permite combinar múltiples criterios de filtrado aplicados en "and".
 * <p>
 * Implementa el patrón Composite, permitiendo encadenar varios filtros y aplicar todos en cadena sobre la lista de gastos.
 * El resultado final es la intersección de todos los filtros en el orden dado.
 * </p>
 * @since 2025-11-14
 * @see Filtro
 */

public class FiltroCompuesto implements Filtro {
    private final List<Filtro> filtros;
    
    public FiltroCompuesto() {
        this.filtros = new ArrayList<>();
    }
    
    public void agregarFiltro(Filtro filtro) {
        filtros.add(filtro);
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        return filtros.stream()
                .reduce(gastos,
                        (gastosActuales, filtro) -> filtro.aplicar(gastosActuales),
                        (g1, g2) -> g1);
    }
}
