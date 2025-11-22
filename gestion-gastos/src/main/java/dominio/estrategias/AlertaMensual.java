package dominio.estrategias;

import dominio.Categoria;
import dominio.Gasto;
import java.time.LocalDate;
import java.util.List;

/**
 * Estrategia de alerta para el cálculo de gasto acumulado en el mes actual.
 * <p>
 * Implementa el patrón Strategy y define cómo sumar los gastos del mes corriente,
 * opcionalmente filtrando por categoría.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */

public class AlertaMensual implements EstrategiaAlerta {

    public AlertaMensual() {
    }
    
    @Override
    public double calcularGastoEnPeriodo(List<Gasto> gastos, Categoria categoria) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        
        return gastos.stream()
                .filter(g -> !g.getFecha().isBefore(inicioMes))
                .filter(g -> categoria == null || g.esDeCategoria(categoria))
                .mapToDouble(Gasto::getCantidad)
                .sum();
    }
}
