package dominio.filtros;

import dominio.Gasto;
import java.util.List;

/**
 * Interfaz general para aplicar criterios de filtrado a colecciones de gastos.
 * <p>
 * Este contrato permite implementar distintos filtros que extraigan subconjuntos de gastos según diferentes reglas o atributos,
 * como por categoría, por fechas, por meses, o mediante composición de múltiples filtros.
 * </p>
 * @since 2025-11-14
 */

public interface Filtro {
    List<Gasto> aplicar(List<Gasto> gastos);
}
