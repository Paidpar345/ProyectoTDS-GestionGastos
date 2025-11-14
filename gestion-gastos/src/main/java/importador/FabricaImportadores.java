package importador;

import dominio.CatalogoCategorias;
import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica Singleton de adaptadores de formato para importación.
 * <p>
 * Gestiona el registro y selección automática de adaptadores según
 * el formato del archivo. Implementa Singleton y Factory Method.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class FabricaImportadores {
    private static FabricaImportadores instancia;
    private List<AdaptadorFormato> adaptadores;
    
    
    private FabricaImportadores() {
        this.adaptadores = new ArrayList<>();
    }
    
    
    public static synchronized FabricaImportadores getInstancia() {
        if (instancia == null) {
            instancia = new FabricaImportadores();
        }
        return instancia;
    }
    
    
    public void registrarAdaptador(AdaptadorFormato adaptador) {
        adaptadores.add(adaptador);
    }
    
    
    public AdaptadorFormato obtenerAdaptador(String contenidoArchivo) {
        return adaptadores.stream()
                .filter(a -> a.puedeManear(contenidoArchivo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró un adaptador para este formato"));
    }
    
    
    public void limpiarAdaptadores() {
        adaptadores.clear();
    }
}
