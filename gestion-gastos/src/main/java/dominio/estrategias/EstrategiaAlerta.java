package dominio.estrategias;

import dominio.Categoria;
import dominio.Gasto;
import java.util.List;

/**
 * Interfaz para la estrategia de cálculo de gasto en un periodo definido para alertas.
 * <p>
 * Permite implementar distintas estrategias para determinar el gasto relevante según el contexto (semanal, mensual, personalizada...).
 * Aplica el patrón Strategy.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */

public interface EstrategiaAlerta {
    double calcularGastoEnPeriodo(List<Gasto> gastos, Categoria categoria);
}
