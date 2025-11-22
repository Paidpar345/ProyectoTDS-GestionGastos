package repositorio;

import dominio.*;
import java.util.List;

/**
 * Interfaz que define las operaciones de persistencia para la aplicación.
 * <p>
 * Establece el contrato para guardar y recuperar gastos, categorías, alertas
 * 
 * y cuentas compartidas. Permite cambiar la implementación sin afectar al resto del sistema.
 * </p>
 * @version 1.0
 * @since 2025-11-14
*/
public interface Repositorio {

    void guardarGastos(List<Gasto> gastos);
    List<Gasto> obtenerTodosLosGastos();

    void guardarCategorias(List<Categoria> categorias);
    List<Categoria> obtenerTodasLasCategorias();

    void guardarAlertas(List<Alerta> alertas);
    List<Alerta> obtenerTodasLasAlertas();

    void guardarCuentasCompartidas(List<CuentaCompartida> cuentas);
    List<CuentaCompartida> obtenerTodasLasCuentas();

}
