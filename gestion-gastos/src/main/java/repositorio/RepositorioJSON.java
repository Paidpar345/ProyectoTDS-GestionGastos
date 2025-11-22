package repositorio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dominio.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Implementación Singleton de repositorio que persiste datos en formato JSON.
 * <p>
 * Utiliza Jackson para serializar/deserializar objetos del dominio y los almacena
 * en un archivo JSON local. Mantiene los datos en memoria y sincroniza con el archivo
 * en cada operación de guardado.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class RepositorioJSON implements Repositorio {
    private static RepositorioJSON instancia;
    private static final String ARCHIVO_DATOS = "datos_gastos.json";
    
    // Colecciones EN MEMORIA (Patrón Repositorio)
    private List<Gasto> gastos;
    private List<Categoria> categorias;
    private List<Alerta> alertas;
    private List<CuentaCompartida> cuentasCompartidas;
    
    // Dependencia de Jackson (detalle de implementación)
    private final ObjectMapper objectMapper;
    
    private RepositorioJSON() {
        this.gastos = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        
        this.objectMapper = configurarObjectMapper();
        cargarDatosDesdeArchivo(); // Carga inicial al instanciar
    }
    
    public static synchronized RepositorioJSON getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioJSON();
        }
        return instancia;
    }
    
    private ObjectMapper configurarObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    // ========== OPERACIONES CRUD (Interfaz pública) ==========
    
    @Override
    public void guardarGastos(List<Gasto> gastos) {
        this.gastos = gastos != null ? new ArrayList<>(gastos) : new ArrayList<>();
        persistirEnArchivo(); // Auto-persistencia tras modificación
    }
    
    @Override
    public List<Gasto> obtenerTodosLosGastos() {
        return new ArrayList<>(gastos); // Copia defensiva
    }

    @Override
    public void guardarCategorias(List<Categoria> categorias) {
        this.categorias = categorias != null ? new ArrayList<>(categorias) : new ArrayList<>();
        persistirEnArchivo();
    }
    
    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return new ArrayList<>(categorias);
    }

    @Override
    public void guardarAlertas(List<Alerta> alertas) {
        this.alertas = alertas != null ? new ArrayList<>(alertas) : new ArrayList<>();
        persistirEnArchivo();
    }
    
    @Override
    public List<Alerta> obtenerTodasLasAlertas() {
        return new ArrayList<>(alertas);
    }

    @Override
    public void guardarCuentasCompartidas(List<CuentaCompartida> cuentas) {
        this.cuentasCompartidas = cuentas != null ? new ArrayList<>(cuentas) : new ArrayList<>();
        persistirEnArchivo();
    }
    
    @Override
    public List<CuentaCompartida> obtenerTodasLasCuentas() {
        return new ArrayList<>(cuentasCompartidas);
    }

    // ========== DETALLES DE IMPLEMENTACIÓN (privados) ==========
    
    /**
     * Carga datos desde archivo JSON (solo durante inicialización).
     * PRIVADO: No expuesto en interfaz Repositorio.
     */
    private void cargarDatosDesdeArchivo() {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) {
            System.out.println("⚠ No se encontró archivo de datos. Iniciando con datos vacíos.");
            return;
        }
        
        try {
            DatosAplicacion datos = objectMapper.readValue(archivo, DatosAplicacion.class);
            this.gastos = datos.getGastos() != null ? datos.getGastos() : new ArrayList<>();
            this.categorias = datos.getCategorias() != null ? datos.getCategorias() : new ArrayList<>();
            this.alertas = datos.getAlertas() != null ? datos.getAlertas() : new ArrayList<>();
            this.cuentasCompartidas = datos.getCuentasCompartidas() != null ? 
                    datos.getCuentasCompartidas() : new ArrayList<>();

            // Recrear estrategias de alertas (transitorias, no serializadas)
            alertas.forEach(alerta -> alerta.getEstrategia());
            
            System.out.println("✓ Datos cargados: " + gastos.size() + " gastos, " + 
                             categorias.size() + " categorías");
        } catch (IOException e) {
            System.err.println("✗ Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Persiste el estado actual en archivo JSON.
     * PRIVADO: Invocado automáticamente tras cada operación de escritura.
     */
    private void persistirEnArchivo() {
        try {
            DatosAplicacion datos = new DatosAplicacion(gastos, categorias, alertas, cuentasCompartidas);
            objectMapper.writeValue(new File(ARCHIVO_DATOS), datos);
        } catch (IOException e) {
            System.err.println("✗ Error al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ========== DTO para serialización (clase interna privada) ==========
    

    private static class DatosAplicacion {
        private List<Gasto> gastos;
        private List<Categoria> categorias;
        private List<Alerta> alertas;
        private List<CuentaCompartida> cuentasCompartidas;
        

        
        @JsonCreator
        public DatosAplicacion(
            @JsonProperty("gastos") List<Gasto> gastos,
            @JsonProperty("categorias") List<Categoria> categorias,
            @JsonProperty("alertas") List<Alerta> alertas,
            @JsonProperty("cuentasCompartidas") List<CuentaCompartida> cuentasCompartidas) {
            this.gastos = gastos;
            this.categorias = categorias;
            this.alertas = alertas;
            this.cuentasCompartidas = cuentasCompartidas;
        }

        // Getters y setters para Jackson
        public List<Gasto> getGastos() { return gastos; }
        
        public List<Categoria> getCategorias() { return categorias; }
        
        public List<Alerta> getAlertas() { return alertas; }
        
        public List<CuentaCompartida> getCuentasCompartidas() { return cuentasCompartidas; }
      
    }
}