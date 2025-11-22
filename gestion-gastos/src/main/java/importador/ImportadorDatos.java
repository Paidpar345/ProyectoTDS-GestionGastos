package importador;

import dominio.Gasto;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;



/**
 * Coordinador de importación de gastos desde archivos externos.
 * <p>
 * Lee archivos, detecta formato automáticamente y convierte
 * los datos en objetos Gasto. Actúa como fachada del subsistema.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */
public class ImportadorDatos {
    private FabricaImportadores fabrica;
    
    public ImportadorDatos() {
        this.fabrica = FabricaImportadores.getInstancia();
    }
    
    
    public List<Gasto> importarDesdeArchivo(Path rutaArchivo) throws IOException {

        String contenido = Files.readString(rutaArchivo);

        AdaptadorFormato adaptador = fabrica.obtenerAdaptador(contenido);

        List<Gasto> gastosImportados = adaptador.parsear(contenido);
        
        System.out.println("✓ Importados " + gastosImportados.size() + " gastos desde " + rutaArchivo.getFileName());
        
        return gastosImportados;
    }
    
    
    public void registrarAdaptador(AdaptadorFormato adaptador) {
        fabrica.registrarAdaptador(adaptador);
    }
}
