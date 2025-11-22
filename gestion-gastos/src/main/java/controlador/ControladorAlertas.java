package controlador;

import dominio.*;
import dominio.enums.PeriodoTemporal;
import dominio.estrategias.AlertaMensual;
import dominio.estrategias.AlertaSemanal;
import dominio.estrategias.EstrategiaAlerta;
import repositorio.Repositorio;
import java.util.List;
import java.util.stream.Collectors;

import catalogos.CatalogoAlertas;
import catalogos.CatalogoCategorias;

/**
 * Controlador general para las operaciones relacionadas con el sistema de alertas de gasto.
 * <p>
 * Esta clase aplica los patrones GRASP  y utiliza Polimorfismo para gestionar la lógica de creación y validación
 * de alertas periódicas (semanales, mensuales y por categoría). Además, gestiona el historial de notificaciones asociado a las alertas y
 * delega el almacenamiento de alertas en el repositorio correspondiente.<br>
 * Las estrategias de verificación de alertas utilizan el patrón Strategy.
 * </p>
 * @version 1.0
 * @since 2025-01-01
 */


public class ControladorAlertas {
    private Repositorio repositorio;
    private CatalogoAlertas catalogoAlertas;
    private CatalogoCategorias catalogoCategorias;
    
    public ControladorAlertas(Repositorio repositorio, CatalogoAlertas catalogoAlertas,
                             CatalogoCategorias catalogoCategorias) {
        this.repositorio = repositorio;
        this.catalogoAlertas = catalogoAlertas;
        this.catalogoCategorias = catalogoCategorias;
    }
    
    
    public void crearAlerta(double limite, PeriodoTemporal periodo, String nombreCategoria) {
        Categoria categoria = nombreCategoria != null ?
                catalogoCategorias.buscarPorNombre(nombreCategoria).orElse(null) : null;

        EstrategiaAlerta estrategia = crearEstrategia(periodo);

        Alerta alerta = new Alerta(limite, periodo, categoria, estrategia);
        catalogoAlertas.agregarAlerta(alerta);
        repositorio.guardarAlertas(catalogoAlertas.obtenerTodas());
    }
    
    
    
    private EstrategiaAlerta crearEstrategia(PeriodoTemporal periodo) {
        return switch (periodo) {
            case SEMANAL -> new AlertaSemanal();
            case MENSUAL -> new AlertaMensual();
        };
    }
    
    
    public void modificarAlerta(String idAlerta, double nuevoLimite, boolean activa) {
        Alerta alerta = catalogoAlertas.buscarPorId(idAlerta);
        if (alerta != null) {
            alerta.setLimiteGasto(nuevoLimite);
            alerta.setActiva(activa);
            repositorio.guardarAlertas(catalogoAlertas.obtenerTodas());
        }
    }
    
    
    public void eliminarAlerta(String idAlerta) {
        Alerta alerta = catalogoAlertas.buscarPorId(idAlerta);
        if (alerta != null) {
            catalogoAlertas.eliminarAlerta(alerta);
            repositorio.guardarAlertas(catalogoAlertas.obtenerTodas());
        }
    }
    
    
    public void verificarAlertas(List<Gasto> gastos) {
        catalogoAlertas.verificarTodasLasAlertas(gastos);
        repositorio.guardarAlertas(catalogoAlertas.obtenerTodas());
    }
    
    
    public List<Notificacion> obtenerNotificacionesNoLeidas() {
        return catalogoAlertas.obtenerTodas().stream()
                .flatMap(alerta -> alerta.obtenerNotificacionesNoLeidas().stream())
                .collect(Collectors.toList());
    }
    
    
    public long contarNotificacionesNoLeidas() {
        return obtenerNotificacionesNoLeidas().size();
    }
    
    
    public List<Notificacion> obtenerTodasLasNotificaciones() {
        return catalogoAlertas.obtenerTodas().stream()
                .flatMap(alerta -> alerta.getNotificaciones().stream())
                .collect(Collectors.toList());
    }
    
    
    public void marcarTodasLasNotificacionesComoLeidas() {
        catalogoAlertas.obtenerTodas().forEach(alerta -> 
            alerta.getNotificaciones().forEach(Notificacion::marcarComoLeida)
        );
        repositorio.guardarAlertas(catalogoAlertas.obtenerTodas());
    }
    
    
    public List<Alerta> obtenerTodasLasAlertas() {
        return catalogoAlertas.obtenerTodas();
    }
}
