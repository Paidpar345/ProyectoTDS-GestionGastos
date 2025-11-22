package catalogos;

import java.util.ArrayList;
import java.util.List;

import dominio.CuentaCompartida;

/**
 * Catálogo encargado de la gestión centralizada de cuentas compartidas de gasto del usuario.
 * <p>
 * Permite añadir, buscar y eliminar cuentas y recuperar la colección completa. Aplica GRASP Information Expert para centralizar la gestión del ciclo de vida de cuentas compartidas.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */


public class CatalogoCuentasCompartidas {
    private List<CuentaCompartida> cuentas;
    
    public CatalogoCuentasCompartidas() {
        this.cuentas = new ArrayList<>();
    }
    
    public void agregarCuenta(CuentaCompartida cuenta) {
        cuentas.add(cuenta);
    }
    
    public void eliminarCuenta(CuentaCompartida cuenta) {
        cuentas.remove(cuenta);
    }
    
    public CuentaCompartida buscarPorId(String id) {
        return cuentas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<CuentaCompartida> obtenerTodas() {
        return new ArrayList<>(cuentas);
    }
}
