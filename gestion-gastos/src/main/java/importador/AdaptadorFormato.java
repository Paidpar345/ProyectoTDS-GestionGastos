package importador;

import dominio.Gasto;
import java.util.List;

/**
 * Interfaz para adaptadores que convierten diferentes formatos de archivo en objetos Gasto.
 * <p>
 * Define el contrato que deben cumplir todos los adaptadores de formato (CSV, JSON, XML, etc.).
 * Aplica el patrón Adapter para permitir importar datos de múltiples fuentes externas.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */
public interface AdaptadorFormato {
    
    List<Gasto> parsear(String contenidoArchivo);
    
    
    boolean puedeManear(String contenidoArchivo);
}
