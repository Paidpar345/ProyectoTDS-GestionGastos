package catalogos;

import dominio.Categoria;
import dominio.Gasto;
import dominio.filtros.Filtro;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Catálogo responsable de la gestión de todos los gastos individuales del sistema.
 * <p>
 * Permite añadir, eliminar, buscar y agrupar gastos, así como aplicar filtrados avanzados, agrupamiento por mes y categoría, y operaciones de resumen. 
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */


public class CatalogoGastos {
    private List<Gasto> gastos;
    
    public CatalogoGastos() {
        this.gastos = new ArrayList<>();
    }
    
    public void agregarGasto(Gasto gasto) {
        if (gasto == null) {
            throw new IllegalArgumentException("El gasto no puede ser null");
        }
        gastos.add(gasto);
    }
    
    public void eliminarGasto(Gasto gasto) {
        gastos.remove(gasto);
    }
    
    public Gasto buscarPorId(String id) {
        return gastos.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<Gasto> obtenerTodos() {
        return new ArrayList<>(gastos);
    }

    public double calcularTotal() {
        return gastos.stream()
                .mapToDouble(Gasto::getCantidad)
                .sum();
    }

    public List<Gasto> filtrar(Filtro filtro) {
        return filtro.aplicar(gastos);
    }

    public Map<Categoria, List<Gasto>> agruparPorCategoria() {
        return gastos.stream()
                .collect(Collectors.groupingBy(Gasto::getCategoria));
    }

    public Map<Month, List<Gasto>> agruparPorMes() {
        return gastos.stream()
                .collect(Collectors.groupingBy(g -> g.getFecha().getMonth()));
    }
    
    public List<Gasto> obtenerPorCategoria(Categoria categoria) {
        return gastos.stream()
                .filter(g -> g.esDeCategoria(categoria))
                .collect(Collectors.toList());
    }
    
    public int cantidadGastos() {
        return gastos.size();
    }
}
