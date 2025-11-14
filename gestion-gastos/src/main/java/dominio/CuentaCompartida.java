package dominio;

import dominio.enums.TipoDistribucion;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representa una cuenta grupal de gastos compartidos entre varias personas.
 * <p>
 * Gestiona el reparto de gastos según distribución equitativa o porcentual, la adición/eliminación de gastos y el cálculo de los saldos individuales de cada persona según los gastos realizados y la participación en la cuenta.<br>
 * Aplican los patrones GRASP High Cohesion y Creator.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */

public class CuentaCompartida {
    private String id;
    private String nombre;
    private TipoDistribucion tipoDistribucion;
    private List<Persona> personas;
    private List<Gasto> gastos;
    
    public CuentaCompartida() {
        this.id = UUID.randomUUID().toString();
        this.personas = new ArrayList<>();
        this.gastos = new ArrayList<>();
    }
    
    public CuentaCompartida(String nombre, TipoDistribucion tipo, List<Persona> personas) {
        this();
        this.nombre = nombre;
        this.tipoDistribucion = tipo;
        configurarPersonas(personas);
    }

    private void configurarPersonas(List<Persona> personas) {
        validarMinimoPersonas(personas);
        
        if (tipoDistribucion == TipoDistribucion.PERSONALIZADA) {
            validarPorcentajes(personas);
        } else {
            asignarPorcentajesEquitativos(personas);
        }
        
        this.personas.addAll(personas);
    }
    
    private void validarMinimoPersonas(List<Persona> personas) {
        if (personas == null || personas.size() < 2) {
            throw new IllegalArgumentException("Mínimo 2 personas en cuenta compartida");
        }
    }

    private void validarPorcentajes(List<Persona> personas) {
        double suma = personas.stream()
                .mapToDouble(Persona::getPorcentajeGasto)
                .sum();
        
        if (Math.abs(suma - 100.0) > 0.01) {
            throw new IllegalArgumentException("Los porcentajes deben sumar 100%");
        }
    }
    
    private void asignarPorcentajesEquitativos(List<Persona> personas) {
        double porcentaje = 100.0 / personas.size();
        personas.forEach(p -> p.setPorcentajeGasto(porcentaje));
    }

    public void agregarGasto(Gasto gasto, Persona pagador) {
        validarPagador(pagador);
        gasto.setPagador(pagador);
        gastos.add(gasto);
        recalcularSaldos();
    }
    
    private void validarPagador(Persona pagador) {
        if (!personas.contains(pagador)) {
            throw new IllegalArgumentException("El pagador debe pertenecer a la cuenta");
        }
    }

    private void recalcularSaldos() {

        personas.forEach(p -> p.setSaldo(0.0));

        gastos.forEach(gasto -> {
            Persona pagador = gasto.getPagador();
            if (pagador == null) return;
            
            double cantidad = gasto.getCantidad();
            
            personas.forEach(persona -> {
                double porcion = gasto.calcularAporte(persona.getPorcentajeGasto());
                
                if (persona.equals(pagador)) {
                    persona.setSaldo(persona.getSaldo() + cantidad - porcion);
                } else {
                    persona.setSaldo(persona.getSaldo() - porcion);
                }
            });
        });
    }
    
    public void eliminarGasto(Gasto gasto) {
        gastos.remove(gasto);
        recalcularSaldos();
    }

    public double calcularTotalGastos() {
        return gastos.stream()
                .mapToDouble(Gasto::getCantidad)
                .sum();
    }

    public List<Gasto> obtenerGastosDe(Persona persona) {
        return gastos.stream()
                .filter(g -> g.getPagador() != null && g.getPagador().equals(persona))
                .collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public TipoDistribucion getTipoDistribucion() {
        return tipoDistribucion;
    }
    
    public void setTipoDistribucion(TipoDistribucion tipoDistribucion) {
        this.tipoDistribucion = tipoDistribucion;
    }
    
    public List<Persona> getPersonas() {
        return Collections.unmodifiableList(personas);
    }
    
    public void setPersonas(List<Persona> personas) {
        this.personas = personas;
    }
    
    public List<Gasto> getGastos() {
        return new ArrayList<>(gastos);
    }
    
    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
