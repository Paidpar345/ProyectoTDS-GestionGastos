package controlador;

import dominio.*;
import dominio.filtros.*;
import repositorio.Repositorio;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import catalogos.CatalogoCategorias;
import catalogos.CatalogoGastos;

/**
 * Controlador principal para la gestión de gastos personales y familiares.
 * <p>
 * Se encarga de coordinar el registro, modificación y eliminación de gastos, así como ofrecer
 * diversas formas de filtrado y agrupamiento de los mismos. Aplica los patrones GRASP Controller,
 * Creator al crear objetos Gasto y Categoria, y colabora con el catálogo de gastos y controlador
 * de alertas. Ofrece métodos para consultas estadsticas y filtrados compuestos según las
 * necesidades del usuario.
 * </p>
 * @version 1.1
 * @since 2025-01-01
 */
public class ControladorGastos {
    private Repositorio repositorio;
    private CatalogoGastos catalogoGastos;
    private CatalogoCategorias catalogoCategorias;
    private ControladorAlertas controladorAlertas;
    
    public ControladorGastos(Repositorio repositorio, CatalogoGastos catalogoGastos,
                            CatalogoCategorias catalogoCategorias, ControladorAlertas controladorAlertas) {
        this.repositorio = repositorio;
        this.catalogoGastos = catalogoGastos;
        this.catalogoCategorias = catalogoCategorias;
        this.controladorAlertas = controladorAlertas;
    }
    
    /**
     * Registra un nuevo gasto personal (no compartido).
     */
    public void registrarGasto(double cantidad, LocalDate fecha, String descripcion, String nombreCategoria) {
        Categoria categoria = catalogoCategorias.buscarPorNombre(nombreCategoria)
            .orElseGet(() -> {
                Categoria nueva = new Categoria(nombreCategoria, "");
                catalogoCategorias.agregarCategoria(nueva);
                return nueva;
            });
        
        Gasto gasto = new Gasto(cantidad, fecha, descripcion, categoria);
        catalogoGastos.agregarGasto(gasto);
        persistir();
        
        controladorAlertas.verificarAlertas(catalogoGastos.obtenerTodos());
    }
    
    /**
     * Modifica un gasto existente, validando que no sea de cuenta compartida.
     * @throws IllegalStateException si el gasto pertenece a una cuenta compartida
     */
    public void modificarGasto(String idGasto, double cantidad, LocalDate fecha, 
                              String descripcion, String nombreCategoria) {
        Gasto gasto = catalogoGastos.buscarPorId(idGasto);
        if (gasto == null) {
            throw new IllegalArgumentException("Gasto no encontrado");
        }
        
        // Validar que NO sea un gasto compartido
        if (gasto.getPagador() != null) {
            throw new IllegalStateException(
                "Este gasto pertenece a una cuenta compartida. " +
                "Debe modificarse desde la gestión de cuentas compartidas para mantener " +
                "la coherencia de los saldos.");
        }
        
        Categoria categoria = catalogoCategorias.buscarPorNombre(nombreCategoria)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + nombreCategoria));
        
        gasto.setCantidad(cantidad);
        gasto.setFecha(fecha);
        gasto.setDescripcion(descripcion);
        gasto.setCategoria(categoria);
        
        persistir();
        controladorAlertas.verificarAlertas(catalogoGastos.obtenerTodos());
    }
    
    /**
     * Elimina un gasto, validando que no sea de cuenta compartida.
     * @throws IllegalStateException si el gasto pertenece a una cuenta compartida
     */
    public void eliminarGasto(String idGasto) {
        Gasto gasto = catalogoGastos.buscarPorId(idGasto);
        if (gasto != null) {
            // Validar que NO sea un gasto compartido
            if (gasto.getPagador() != null) {
                throw new IllegalStateException(
                    "Este gasto pertenece a una cuenta compartida. " +
                    "Debe eliminarse desde la gestión de cuentas compartidas para mantener " +
                    "la coherencia de los saldos.");
            }
            
            catalogoGastos.eliminarGasto(gasto);
            persistir();
        }
    }
    
    /**
     * Elimina un gasto sin validaciones (uso interno).
     */
    public void eliminarGastoForzado(String idGasto) {
        Gasto gasto = catalogoGastos.buscarPorId(idGasto);
        if (gasto != null) {
            catalogoGastos.eliminarGasto(gasto);
            persistir();
        }
    }
    
    public List<Gasto> filtrarPorCategorias(List<String> nombresCategoria) {
        if (nombresCategoria == null || nombresCategoria.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una categoría");
        }
        
        Set<Categoria> categorias = new HashSet<>();
        for (String nombre : nombresCategoria) {
            catalogoCategorias.buscarPorNombre(nombre).ifPresent(categorias::add);
        }
        
        if (categorias.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron las categorías especificadas");
        }
        
        Filtro filtro = new FiltroCategorias(categorias);
        return catalogoGastos.filtrar(filtro);
    }
    
    public List<Gasto> filtrarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }
        
        Filtro filtro = new FiltroFechas(fechaInicio, fechaFin);
        return catalogoGastos.filtrar(filtro);
    }
    
    public List<Gasto> filtrarPorMeses(List<String> nombresMeses) {
        if (nombresMeses == null || nombresMeses.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos un mes");
        }
        
        Set<Month> meses = nombresMeses.stream()
            .map(this::convertirNombreMes)
            .collect(Collectors.toSet());
        
        Filtro filtro = new FiltroMeses(meses);
        return catalogoGastos.filtrar(filtro);
    }
    
    public List<Gasto> filtrarCompuesto(List<String> nombresCategoria, LocalDate fechaInicio, LocalDate fechaFin) {
        if (nombresCategoria == null || nombresCategoria.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una categoría");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        
        Set<Categoria> categorias = nombresCategoria.stream()
            .map(nombre -> catalogoCategorias.buscarPorNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + nombre)))
            .collect(Collectors.toSet());
        
        Filtro filtroCategorias = new FiltroCategorias(categorias);
        Filtro filtroFecha = new FiltroFechas(fechaInicio, fechaFin);
        
        FiltroCompuesto filtroCompuesto = new FiltroCompuesto();
        filtroCompuesto.agregarFiltro(filtroCategorias);
        filtroCompuesto.agregarFiltro(filtroFecha);
        
        return catalogoGastos.filtrar(filtroCompuesto);
    }
    
    private Month convertirNombreMes(String nombreMes) {
        return switch (nombreMes.toLowerCase()) {
            case "enero" -> Month.JANUARY;
            case "febrero" -> Month.FEBRUARY;
            case "marzo" -> Month.MARCH;
            case "abril" -> Month.APRIL;
            case "mayo" -> Month.MAY;
            case "junio" -> Month.JUNE;
            case "julio" -> Month.JULY;
            case "agosto" -> Month.AUGUST;
            case "septiembre" -> Month.SEPTEMBER;
            case "octubre" -> Month.OCTOBER;
            case "noviembre" -> Month.NOVEMBER;
            case "diciembre" -> Month.DECEMBER;
            default -> throw new IllegalArgumentException("Mes inválido: " + nombreMes);
        };
    }
    
    public List<Gasto> obtenerTodosLosGastos() {
        return catalogoGastos.obtenerTodos();
    }
    
    public double calcularTotalGastos() {
        return catalogoGastos.obtenerTodos().stream()
            .mapToDouble(Gasto::getCantidad)
            .sum();
    }
    
    public Map<Categoria, List<Gasto>> agruparPorCategoria() {
        return catalogoGastos.agruparPorCategoria();
    }
    
    public Map<Month, List<Gasto>> agruparPorMes() {
        return catalogoGastos.agruparPorMes();
    }
    
    /**
     * Verifica si un gasto es de una cuenta compartida.
     */
    public boolean esGastoCompartido(String idGasto) {
        Gasto gasto = catalogoGastos.buscarPorId(idGasto);
        return gasto != null && gasto.getPagador() != null;
    }
    
    private void persistir() {
        repositorio.guardarGastos(catalogoGastos.obtenerTodos());
        repositorio.guardarCategorias(catalogoCategorias.obtenerTodas());
    }
}