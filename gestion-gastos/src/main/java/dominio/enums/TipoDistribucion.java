package dominio.enums;

/**
 * Tipos de distribución posibles en una cuenta compartida.
 * <p>
 * Determina cómo se reparte el gasto entre las personas de una cuenta: equitativamente o con porcentajes personalizados.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public enum TipoDistribucion {
    EQUITATIVA("Equitativa"),
    PERSONALIZADA("Personalizada");
    
    private final String descripcion;
    
    TipoDistribucion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
