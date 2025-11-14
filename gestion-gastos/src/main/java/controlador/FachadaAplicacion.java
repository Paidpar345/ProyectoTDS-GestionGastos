package controlador;

import dominio.*;
import importador.AdaptadorBancario;
import importador.FabricaImportadores;
import repositorio.Repositorio;
import repositorio.RepositorioJSON;

/**
 * Fachada principal para el acceso unificado a todos los controladores de la aplicación.
 * <p>
 * Implementa el patrón Singleton para garantizar una única instancia. Inicializa y provee acceso a todos los controladores del sistema,
 * como gastos, categorías, alertas, cuentas compartidas e importador, así como la inicialización persistente de los catálogos de dominio.<br>
 * Aplica el patrón GRASP Facade.
 * </p>
 * @version 1.0
 * @since 2025-01-01
 */


public class FachadaAplicacion {
    private static FachadaAplicacion instancia;

    private Repositorio repositorio;

    private ControladorGastos controladorGastos;
    private ControladorCategorias controladorCategorias;
    private ControladorAlertas controladorAlertas;
    private ControladorCuentasCompartidas controladorCuentas;
    private ControladorImportador controladorImportador;
    
    
    private FachadaAplicacion() {
        inicializar();
    }
    
    
    public static synchronized FachadaAplicacion getInstancia() {
        if (instancia == null) {
            instancia = new FachadaAplicacion();
        }
        return instancia;
    }
    
    
    private void inicializar() {
        System.out.println("=== Iniciando Aplicación Gestión de Gastos ===");

        repositorio = RepositorioJSON.getInstancia();

        CatalogoGastos catalogoGastos = new CatalogoGastos();
        CatalogoCategorias catalogoCategorias = new CatalogoCategorias();
        CatalogoAlertas catalogoAlertas = new CatalogoAlertas();
        CatalogoCuentasCompartidas catalogoCuentas = new CatalogoCuentasCompartidas();

        cargarDatos(catalogoGastos, catalogoCategorias, catalogoAlertas, catalogoCuentas);

        controladorAlertas = new ControladorAlertas(repositorio, catalogoAlertas, catalogoCategorias);
        controladorGastos = new ControladorGastos(repositorio, catalogoGastos, catalogoCategorias, controladorAlertas);
        controladorCategorias = new ControladorCategorias(repositorio, catalogoCategorias);
        controladorCuentas = new ControladorCuentasCompartidas(repositorio, catalogoCuentas, catalogoCategorias);
        controladorImportador = new ControladorImportador(repositorio, catalogoGastos, controladorAlertas);

        configurarImportador(catalogoCategorias);
        
        System.out.println("✓ Aplicación inicializada correctamente");
        System.out.println("  - " + catalogoGastos.cantidadGastos() + " gastos cargados");
        System.out.println("  - " + catalogoCategorias.cantidadCategorias() + " categorías disponibles");
        System.out.println("==========================================\n");
    }
    
    
    private void cargarDatos(CatalogoGastos catalogoGastos, CatalogoCategorias catalogoCategorias,
                            CatalogoAlertas catalogoAlertas, CatalogoCuentasCompartidas catalogoCuentas) {

        repositorio.obtenerTodosLosGastos().forEach(catalogoGastos::agregarGasto);

        repositorio.obtenerTodasLasCategorias().forEach(cat -> {
            if (catalogoCategorias.buscarPorNombre(cat.getNombre()).isEmpty()) {
                catalogoCategorias.agregarCategoria(cat);
            }
        });

        repositorio.obtenerTodasLasAlertas().forEach(catalogoAlertas::agregarAlerta);

        repositorio.obtenerTodasLasCuentas().forEach(catalogoCuentas::agregarCuenta);
    }
    
    
    private void configurarImportador(CatalogoCategorias catalogoCategorias) {
        FabricaImportadores fabrica = FabricaImportadores.getInstancia();
        fabrica.limpiarAdaptadores();
        fabrica.registrarAdaptador(new AdaptadorBancario(catalogoCategorias));
    }

    
    public ControladorGastos getControladorGastos() {
        return controladorGastos;
    }
    
    public ControladorCategorias getControladorCategorias() {
        return controladorCategorias;
    }
    
    public ControladorAlertas getControladorAlertas() {
        return controladorAlertas;
    }
    
    public ControladorCuentasCompartidas getControladorCuentas() {
        return controladorCuentas;
    }
    
    public ControladorImportador getControladorImportador() {
        return controladorImportador;
    }
}
