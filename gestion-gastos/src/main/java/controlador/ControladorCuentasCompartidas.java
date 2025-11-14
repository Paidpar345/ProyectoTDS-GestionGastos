package controlador;

import dominio.*;
import dominio.enums.TipoDistribucion;
import repositorio.Repositorio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador de cuentas compartidas de gasto.
 * <p>
 * Gestiona la creación de cuentas, registro y eliminación de gastos compartidos, y cálculo de las deudas individuales.
 * Aplica los patrones GRASP Controller, Creator (al crear objetos Persona) y High Cohesion. Facilita tanto la distribución equitativa
 * de gastos como personalizada en porcentajes, y coordina el almacenamiento de cuentas en el repositorio.
 * </p>
 * @version 1.0
 * @since 2025-01-01
 */


public class ControladorCuentasCompartidas {
    private Repositorio repositorio;
    private CatalogoCuentasCompartidas catalogoCuentas;
    private CatalogoCategorias catalogoCategorias;
    
    public ControladorCuentasCompartidas(Repositorio repositorio, 
                                        CatalogoCuentasCompartidas catalogoCuentas,
                                        CatalogoCategorias catalogoCategorias) {
        this.repositorio = repositorio;
        this.catalogoCuentas = catalogoCuentas;
        this.catalogoCategorias = catalogoCategorias;
    }
    
    
    public void crearCuentaEquitativa(String nombreCuenta, List<String> nombresPersonas) {
        if (nombresPersonas.size() < 2) {
            throw new IllegalArgumentException("Debe haber al menos 2 personas");
        }

        List<Persona> personas = nombresPersonas.stream()
                .map(Persona::new)
                .collect(Collectors.toList());
        
        CuentaCompartida cuenta = new CuentaCompartida(nombreCuenta, TipoDistribucion.EQUITATIVA, personas);
        catalogoCuentas.agregarCuenta(cuenta);
        repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
    }
    
    
    public void crearCuentaPorcentual(String nombreCuenta, Map<String, Double> porcentajes) {
        if (porcentajes.size() < 2) {
            throw new IllegalArgumentException("Debe haber al menos 2 personas");
        }

        double suma = porcentajes.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        if (Math.abs(suma - 100.0) > 0.01) {
            throw new IllegalArgumentException("La suma de porcentajes debe ser 100%");
        }

        List<Persona> personas = porcentajes.entrySet().stream()
                .map(entry -> new Persona(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        
        CuentaCompartida cuenta = new CuentaCompartida(nombreCuenta, TipoDistribucion.PERSONALIZADA, personas);
        catalogoCuentas.agregarCuenta(cuenta);
        repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
    }
    
    
    public void registrarGastoEnCuenta(String idCuenta, double cantidad, LocalDate fecha,
                                       String descripcion, String nombrePagador) {
        CuentaCompartida cuenta = catalogoCuentas.buscarPorId(idCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }
        
        Persona pagador = cuenta.getPersonas().stream()
                .filter(p -> p.getNombre().equals(nombrePagador))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pagador no encontrado"));

        Categoria categoriaDefecto = catalogoCategorias.buscarPorNombre("Compartido")
                .orElseGet(() -> {
                    Categoria nueva = new Categoria("Compartido", "Gastos compartidos");
                    catalogoCategorias.agregarCategoria(nueva);
                    return nueva;
                });
        
        Gasto gasto = new Gasto(cantidad, fecha, descripcion, categoriaDefecto);
        cuenta.agregarGasto(gasto, pagador);
        
        repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
    }
    
    
    public void crearCuentaCompartida(String nombre, TipoDistribucion tipo,
                                     List<String> nombresPersonas, List<Double> porcentajes) {
        List<Persona> personas = crearPersonas(nombresPersonas, porcentajes, tipo);
        
        CuentaCompartida cuenta = new CuentaCompartida(nombre, tipo, personas);
        catalogoCuentas.agregarCuenta(cuenta);
        repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
    }
    
    
    private List<Persona> crearPersonas(List<String> nombres, List<Double> porcentajes,
                                       TipoDistribucion tipo) {
        List<Persona> personas = new ArrayList<>();
        
        for (int i = 0; i < nombres.size(); i++) {
            Persona persona = tipo == TipoDistribucion.PERSONALIZADA ?
                    new Persona(nombres.get(i), porcentajes.get(i)) :
                    new Persona(nombres.get(i));
            personas.add(persona);
        }
        
        return personas;
    }
    
    
    public void registrarGastoCompartido(String idCuenta, double cantidad, LocalDate fecha,
                                        String descripcion, String nombreCategoria,
                                        String nombrePagador) {
        CuentaCompartida cuenta = catalogoCuentas.buscarPorId(idCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }
        
        Categoria categoria = catalogoCategorias.buscarPorNombre(nombreCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        
        Persona pagador = cuenta.getPersonas().stream()
                .filter(p -> p.getNombre().equals(nombrePagador))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pagador no encontrado"));
        
        Gasto gasto = new Gasto(cantidad, fecha, descripcion, categoria);
        cuenta.agregarGasto(gasto, pagador);
        
        repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
    }
    
    
    public void eliminarGastoCompartido(String idCuenta, String idGasto) {
        CuentaCompartida cuenta = catalogoCuentas.buscarPorId(idCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }
        
        Gasto gasto = cuenta.getGastos().stream()
                .filter(g -> g.getId().equals(idGasto))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado"));
        
        cuenta.eliminarGasto(gasto);
        repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
    }
    
    
    public List<String> calcularResumenDeudas(String idCuenta) {
        CuentaCompartida cuenta = catalogoCuentas.buscarPorId(idCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }
        
        return cuenta.getPersonas().stream()
                .map(persona -> {
                    double saldo = persona.getSaldo();
                    if (saldo > 0) {
                        return String.format("%s debe recibir %.2f€", persona.getNombre(), saldo);
                    } else if (saldo < 0) {
                        return String.format("%s debe pagar %.2f€", persona.getNombre(), Math.abs(saldo));
                    } else {
                        return String.format("%s está al día", persona.getNombre());
                    }
                })
                .collect(Collectors.toList());
    }
    
    
    public void eliminarCuentaCompartida(String idCuenta) {
        CuentaCompartida cuenta = catalogoCuentas.buscarPorId(idCuenta);
        if (cuenta != null) {
            catalogoCuentas.eliminarCuenta(cuenta);
            repositorio.guardarCuentasCompartidas(catalogoCuentas.obtenerTodas());
        }
    }
    
    
    public List<CuentaCompartida> obtenerTodasLasCuentas() {
        return catalogoCuentas.obtenerTodas();
    }
}
