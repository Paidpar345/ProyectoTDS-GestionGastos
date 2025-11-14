package dominio.estrategias;

import dominio.Categoria;
import dominio.Gasto;
import java.time.LocalDate;
import java.util.List;

/**
 * Estrategia de alerta para el cálculo de gasto acumulado en los últimos 7 días.
 * <p>
 * Implementa el patrón Strategy y define cómo sumar los gastos de la última semana, opcionalmente filtrando por categoría.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class AlertaSemanal implements EstrategiaAlerta {

    public AlertaSemanal() {
    }
    
    @Override
    public double calcularGastoEnPeriodo(List<Gasto> gastos, Categoria categoria) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(7);
        
        return gastos.stream()
                .filter(g -> !g.getFecha().isBefore(inicioSemana))
                .filter(g -> categoria == null || g.esDeCategoria(categoria))
                .mapToDouble(Gasto::getCantidad)
                .sum();
    }
}
