package dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dominio.enums.PeriodoTemporal;
import dominio.estrategias.AlertaMensual;
import dominio.estrategias.AlertaSemanal;
import dominio.estrategias.EstrategiaAlerta;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Representa una alerta configurable para controlar si se sobrepasa un límite de gasto personal o por categoría en un periodo temporal definido.
 * <p>
 * Utiliza el patrón Strategy para delegar el cálculo del gasto según el periodo (`AlertaSemanal`, `AlertaMensual`). Genera notificaciones cuando se detecta que el límite ha sido superado.
 * Una alerta puede estar activa o inactiva y mantiene el historial de notificaciones generadas por su superación.<br>
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class Alerta {
    private String id;
    private double limiteGasto;
    private PeriodoTemporal periodo;
    private Categoria categoria;
    private boolean activa;
    private List<Notificacion> notificaciones;

    @JsonIgnore
    private EstrategiaAlerta estrategia;
    
    public Alerta() {
        this.id = UUID.randomUUID().toString();
        this.notificaciones = new ArrayList<>();
        this.activa = true;
    }
    
    public Alerta(double limiteGasto, PeriodoTemporal periodo, Categoria categoria, 
                  EstrategiaAlerta estrategia) {
        this();
        validarLimite(limiteGasto);
        this.limiteGasto = limiteGasto;
        this.periodo = periodo;
        this.categoria = categoria;
        this.estrategia = estrategia;
    }

    public void verificarLimite(List<Gasto> gastos) {
        if (!activa) return;

        if (estrategia == null && periodo != null) {
            recrearEstrategia();
        }
        
        if (estrategia == null) return;
        
        double totalGastos = estrategia.calcularGastoEnPeriodo(gastos, categoria);
        
        if (totalGastos > limiteGasto) {
            crearNotificacion(totalGastos);
        }
    }
    
    
    private void recrearEstrategia() {
        if (periodo == null) return;
        
        this.estrategia = switch (periodo) {
            case SEMANAL -> new AlertaSemanal();
            case MENSUAL -> new AlertaMensual();
        };
    }

    private void crearNotificacion(double totalGastos) {
        String mensaje = construirMensaje(totalGastos);
        Notificacion notificacion = new Notificacion(mensaje, this);

        boolean existe = notificaciones.stream()
                .anyMatch(n -> n.getMensaje().equals(mensaje) && !n.isLeida());
        
        if (!existe) {
            notificaciones.add(notificacion);
        }
    }
    
    private String construirMensaje(double totalGastos) {
        String tipoPeriodo = periodo != null ? periodo.getDescripcion() : "Desconocido";
        String infoCategoria = categoria != null ? " en " + categoria.getNombre() : "";
        
        return String.format(
            "¡Alerta %s! Límite superado: %.2f€ / %.2f€%s",
            tipoPeriodo, totalGastos, limiteGasto, infoCategoria
        );
    }

    public List<Notificacion> obtenerNotificacionesNoLeidas() {
        if (notificaciones == null) {
            notificaciones = new ArrayList<>();
        }
        return notificaciones.stream()
                .filter(n -> !n.isLeida())
                .collect(Collectors.toList());
    }
    
    private void validarLimite(double limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor que 0");
        }
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public double getLimiteGasto() {
        return limiteGasto;
    }
    
    public void setLimiteGasto(double limiteGasto) {
        validarLimite(limiteGasto);
        this.limiteGasto = limiteGasto;
    }
    
    public PeriodoTemporal getPeriodo() {
        return periodo;
    }
    
    public void setPeriodo(PeriodoTemporal periodo) {
        this.periodo = periodo;

        recrearEstrategia();
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public boolean isActiva() {
        return activa;
    }
    
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    public List<Notificacion> getNotificaciones() {
        if (notificaciones == null) {
            notificaciones = new ArrayList<>();
        }
        return notificaciones;
    }
    
    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones != null ? notificaciones : new ArrayList<>();
    }
    
    @JsonIgnore
    public EstrategiaAlerta getEstrategia() {
        if (estrategia == null && periodo != null) {
            recrearEstrategia();
        }
        return estrategia;
    }
    
    public void setEstrategia(EstrategiaAlerta estrategia) {
        this.estrategia = estrategia;
    }
}
