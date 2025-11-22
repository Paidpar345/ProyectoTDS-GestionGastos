package dominio.filtros;

import dominio.Gasto;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro para seleccionar gastos realizados en uno o varios meses concretos del año, sin importar el año.
 * <p>
 * Filtra los gastos cuya fecha caiga en uno de los meses especificados.
 * </p>
 * @since 2025-11-14
 */

public class FiltroMeses implements Filtro {
    private final Set<Month> meses;
    
    public FiltroMeses(Set<Month> meses) {
        this.meses = Set.copyOf(meses);
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        return gastos.stream()
                .filter(g -> meses.contains(g.getFecha().getMonth()))
                .collect(Collectors.toList());
    }
}
