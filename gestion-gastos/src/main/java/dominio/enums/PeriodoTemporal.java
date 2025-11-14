package dominio.enums;

/**
 * Enumera los posibles periodos temporales para una alerta de gasto.
 * <p>
 * Define los periodos que pueden ser utilizados en las alertas (semanal o mensual).
 * Cada valor incluye una descripción amigable para su presentación en la interfaz.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public enum PeriodoTemporal {
    SEMANAL("Semanal"),
    MENSUAL("Mensual");
    
    private final String descripcion;
    
    PeriodoTemporal(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
