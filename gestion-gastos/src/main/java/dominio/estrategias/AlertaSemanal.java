package dominio.estrategias;

import dominio.Categoria;
import dominio.Gasto;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

/**
 * Estrategia de alerta para el cálculo de gasto acumulado en la semana actual (ISO 8601).
 * <p>
 * Implementa el patrón Strategy y define cómo sumar los gastos de la semana corriente
 * (de lunes a domingo), opcionalmente filtrando por categoría.
 * </p>
 * 
 * @version 1.1
 * @since 2025-11-15
 */
public class AlertaSemanal implements EstrategiaAlerta {
    
    public AlertaSemanal() {}
    
    @Override
    public double calcularGastoEnPeriodo(List<Gasto> gastos, Categoria categoria) {
        LocalDate hoy = LocalDate.now();
        
        // Obtener el primer día de la semana actual (lunes)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate inicioSemana = hoy.with(weekFields.dayOfWeek(), 1);
        
        // Obtener el último día de la semana actual (domingo)
        LocalDate finSemana = inicioSemana.plusDays(6);
        
        return gastos.stream()
            .filter(g -> !g.getFecha().isBefore(inicioSemana) && !g.getFecha().isAfter(finSemana))
            .filter(g -> categoria == null || g.esDeCategoria(categoria))
            .mapToDouble(Gasto::getCantidad)
            .sum();
    }
}
