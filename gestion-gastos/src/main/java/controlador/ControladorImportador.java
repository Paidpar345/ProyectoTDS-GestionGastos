package controlador;

import dominio.CatalogoGastos;
import dominio.Gasto;
import importador.ImportadorDatos;
import repositorio.Repositorio;
import java.nio.file.Path;
import java.util.List;

/**
 * Controlador para la importación de ficheros de gasto externo al sistema.
 * <p>
 * Coordina la lógica necesaria para importar datos de gastos desde archivos en diferentes formatos (como CSV o TXT) usando el patrón Adapter y delegando la
 * verificación de alertas tras la inserción de los nuevos datos.
 * </p>
 * @version 1.0
 * @since 2025-01-01
 */


public class ControladorImportador {
    private Repositorio repositorio;
    private CatalogoGastos catalogoGastos;
    private ImportadorDatos importador;
    private ControladorAlertas controladorAlertas;
    
    public ControladorImportador(Repositorio repositorio, CatalogoGastos catalogoGastos,
                                ControladorAlertas controladorAlertas) {
        this.repositorio = repositorio;
        this.catalogoGastos = catalogoGastos;
        this.controladorAlertas = controladorAlertas;
        this.importador = new ImportadorDatos();
    }
    
    
    public List<Gasto> importarDesdeArchivo(Path rutaArchivo) {
        try {

            List<Gasto> gastosImportados = importador.importarDesdeArchivo(rutaArchivo);

            gastosImportados.forEach(catalogoGastos::agregarGasto);

            repositorio.guardarGastos(catalogoGastos.obtenerTodos());

            controladorAlertas.verificarAlertas(catalogoGastos.obtenerTodos());
            
            return gastosImportados;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al importar archivo: " + e.getMessage(), e);
        }
    }
}
