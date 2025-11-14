package repositorio;

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
    
    private List<Gasto> gastos;
    private List<Categoria> categorias;
    private List<Alerta> alertas;
    private List<CuentaCompartida> cuentasCompartidas;
    
    private ObjectMapper objectMapper;
    
    
    private RepositorioJSON() {
        this.gastos = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        
        configurarObjectMapper();
        cargarDatos();
    }
    
    
    public static synchronized RepositorioJSON getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioJSON();
        }
        return instancia;
    }
    
    
    private void configurarObjectMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    
    @Override
    public void guardarGastos(List<Gasto> gastos) {
        this.gastos = gastos != null ? new ArrayList<>(gastos) : new ArrayList<>();
        guardarDatos();
    }
    
    @Override
    public List<Gasto> obtenerTodosLosGastos() {
        return new ArrayList<>(gastos);
    }

    
    @Override
    public void guardarCategorias(List<Categoria> categorias) {
        this.categorias = categorias != null ? new ArrayList<>(categorias) : new ArrayList<>();
        guardarDatos();
    }
    
    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return new ArrayList<>(categorias);
    }

    
    @Override
    public void guardarAlertas(List<Alerta> alertas) {
        this.alertas = alertas != null ? new ArrayList<>(alertas) : new ArrayList<>();
        guardarDatos();
    }
    
    @Override
    public List<Alerta> obtenerTodasLasAlertas() {
        return new ArrayList<>(alertas);
    }

    
    @Override
    public void guardarCuentasCompartidas(List<CuentaCompartida> cuentas) {
        this.cuentasCompartidas = cuentas != null ? new ArrayList<>(cuentas) : new ArrayList<>();
        guardarDatos();
    }
    
    @Override
    public List<CuentaCompartida> obtenerTodasLasCuentas() {
        return new ArrayList<>(cuentasCompartidas);
    }

    
    @Override
    public void cargarDatos() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try {
                DatosAplicacion datos = objectMapper.readValue(archivo, DatosAplicacion.class);
                this.gastos = datos.getGastos() != null ? datos.getGastos() : new ArrayList<>();
                this.categorias = datos.getCategorias() != null ? datos.getCategorias() : new ArrayList<>();
                this.alertas = datos.getAlertas() != null ? datos.getAlertas() : new ArrayList<>();
                this.cuentasCompartidas = datos.getCuentasCompartidas() != null ? 
                        datos.getCuentasCompartidas() : new ArrayList<>();

                alertas.forEach(alerta -> alerta.getEstrategia());
                
                System.out.println("✓ Datos cargados correctamente desde " + ARCHIVO_DATOS);
            } catch (IOException e) {
                System.err.println("✗ Error al cargar datos: " + e.getMessage());

                this.gastos = new ArrayList<>();
                this.categorias = new ArrayList<>();
                this.alertas = new ArrayList<>();
                this.cuentasCompartidas = new ArrayList<>();
            }
        } else {
            System.out.println("⚠ No se encontró archivo de datos. Se iniciará con datos vacíos.");
        }
    }
    
    @Override
    public void guardarDatos() {
        try {
            DatosAplicacion datos = new DatosAplicacion(gastos, categorias, alertas, cuentasCompartidas);
            objectMapper.writeValue(new File(ARCHIVO_DATOS), datos);
        } catch (IOException e) {
            System.err.println("✗ Error al guardar datos: " + e.getMessage());
            e.printStackTrace(); // Para debugging
        }
    }

    
    
    private static class DatosAplicacion {
        private List<Gasto> gastos;
        private List<Categoria> categorias;
        private List<Alerta> alertas;
        private List<CuentaCompartida> cuentasCompartidas;
        
        public DatosAplicacion() {}
        
        public DatosAplicacion(List<Gasto> gastos, List<Categoria> categorias,
                              List<Alerta> alertas, List<CuentaCompartida> cuentasCompartidas) {
            this.gastos = gastos;
            this.categorias = categorias;
            this.alertas = alertas;
            this.cuentasCompartidas = cuentasCompartidas;
        }

        public List<Gasto> getGastos() {
            return gastos;
        }
        
        public void setGastos(List<Gasto> gastos) {
            this.gastos = gastos;
        }
        
        public List<Categoria> getCategorias() {
            return categorias;
        }
        
        public void setCategorias(List<Categoria> categorias) {
            this.categorias = categorias;
        }
        
        public List<Alerta> getAlertas() {
            return alertas;
        }
        
        public void setAlertas(List<Alerta> alertas) {
            this.alertas = alertas;
        }
        
        public List<CuentaCompartida> getCuentasCompartidas() {
            return cuentasCompartidas;
        }
        
        public void setCuentasCompartidas(List<CuentaCompartida> cuentasCompartidas) {
            this.cuentasCompartidas = cuentasCompartidas;
        }
    }
}
