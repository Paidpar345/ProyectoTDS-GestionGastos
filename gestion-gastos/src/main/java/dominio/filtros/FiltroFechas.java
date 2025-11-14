package dominio.filtros;

import dominio.Gasto;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro para seleccionar gastos dentro de un rango de fechas dado (inclusive).
 * <p>
 * Filtra aquellos gastos cuya fecha esté dentro del periodo especificado (incluyendo extremos).
 * Útil para análisis temporales personalizados.
 * </p>
 * @since 2025-11-14
 */
public class FiltroFechas implements Filtro {
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    
    public FiltroFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        return gastos.stream()
                .filter(g -> g.estaEnRango(fechaInicio, fechaFin))
                .collect(Collectors.toList());
    }
}
