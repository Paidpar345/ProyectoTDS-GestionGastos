package dominio;

import java.util.ArrayList;
import java.util.List;


/**
 * Catálogo que mantiene y gestiona todas las alertas del sistema.
 * <p>
 * Permite agregar, eliminar, buscar y verificar alertas sobre una lista de gastos. 
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */


public class CatalogoAlertas {
    private List<Alerta> alertas;
    
    public CatalogoAlertas() {
        this.alertas = new ArrayList<>();
    }
    
    public void agregarAlerta(Alerta alerta) {
        if (alerta != null) {

            alerta.getEstrategia(); // Esto fuerza la recreación si es null
            alertas.add(alerta);
        }
    }
    
    public void eliminarAlerta(Alerta alerta) {
        alertas.remove(alerta);
    }
    
    public Alerta buscarPorId(String id) {
        return alertas.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<Alerta> obtenerTodas() {
        return new ArrayList<>(alertas);
    }
    
    
    public void verificarTodasLasAlertas(List<Gasto> gastos) {
        if (gastos == null) return;
        
        alertas.stream()
                .filter(Alerta::isActiva)
                .forEach(alerta -> {

                    alerta.getEstrategia();
                    alerta.verificarLimite(gastos);
                });
    }
}
