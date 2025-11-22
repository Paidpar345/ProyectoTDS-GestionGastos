package dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import catalogos.CatalogoGastos;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de caja negra para CatalogoGastos.
 * Prueba entradas/salidas sin conocer la implementación interna.
 */
@DisplayName("Tests de Caja Negra - CatalogoGastos")
class CatalogoGastosTest {
    
    private CatalogoGastos catalogo;
    private Categoria alimentacion;
    private Categoria transporte;
    
    @BeforeEach
    void setUp() {
        catalogo = new CatalogoGastos();
        alimentacion = new Categoria("Alimentación", "Comida");
        transporte = new Categoria("Transporte", "Movilidad");
    }
    
    @Test
    @DisplayName("Catálogo vacío inicialmente")
    void testCatalogoVacioInicial() {
        assertThat(catalogo.obtenerTodos()).isEmpty();
        assertThat(catalogo.cantidadGastos()).isZero();
    }
    
    @Test
    @DisplayName("Agregar gasto aumenta cantidad")
    void testAgregarGasto() {
        Gasto gasto = new Gasto(50.0, LocalDate.now(), "Test", alimentacion);
        
        catalogo.agregarGasto(gasto);
        
        assertThat(catalogo.cantidadGastos()).isEqualTo(1);
        assertThat(catalogo.obtenerTodos()).contains(gasto);
    }
    
    @Test
    @DisplayName("Agregar null lanza excepción")
    void testAgregarGastoNull() {
        assertThatThrownBy(() -> catalogo.agregarGasto(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no puede ser null");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    @DisplayName("Agregar múltiples gastos")
    void testAgregarMultiplesGastos(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            catalogo.agregarGasto(new Gasto(10.0, LocalDate.now(), "Gasto " + i, alimentacion));
        }
        
        assertThat(catalogo.cantidadGastos()).isEqualTo(cantidad);
    }
    
    @Test
    @DisplayName("Calcular total de gastos")
    void testCalcularTotal() {
        catalogo.agregarGasto(new Gasto(50.0, LocalDate.now(), "Gasto 1", alimentacion));
        catalogo.agregarGasto(new Gasto(30.0, LocalDate.now(), "Gasto 2", transporte));
        catalogo.agregarGasto(new Gasto(20.0, LocalDate.now(), "Gasto 3", alimentacion));
        
        assertThat(catalogo.calcularTotal()).isEqualTo(100.0);
    }
    
    @Test
    @DisplayName("Buscar gasto por ID existente")
    void testBuscarPorId() {
        Gasto gasto = new Gasto(50.0, LocalDate.now(), "Test", alimentacion);
        catalogo.agregarGasto(gasto);
        
        Gasto encontrado = catalogo.buscarPorId(gasto.getId());
        
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getId()).isEqualTo(gasto.getId());
    }
    
    @Test
    @DisplayName("Buscar gasto por ID inexistente retorna null")
    void testBuscarPorIdInexistente() {
        Gasto encontrado = catalogo.buscarPorId("id-inexistente");
        assertThat(encontrado).isNull();
    }
    
    @Test
    @DisplayName("Eliminar gasto reduce cantidad")
    void testEliminarGasto() {
        Gasto gasto = new Gasto(50.0, LocalDate.now(), "Test", alimentacion);
        catalogo.agregarGasto(gasto);
        
        catalogo.eliminarGasto(gasto);
        
        assertThat(catalogo.cantidadGastos()).isZero();
        assertThat(catalogo.obtenerTodos()).doesNotContain(gasto);
    }
    
    @Test
    @DisplayName("Agrupar gastos por categoría")
    void testAgruparPorCategoria() {
        catalogo.agregarGasto(new Gasto(50.0, LocalDate.now(), "Comida", alimentacion));
        catalogo.agregarGasto(new Gasto(30.0, LocalDate.now(), "Bus", transporte));
        catalogo.agregarGasto(new Gasto(20.0, LocalDate.now(), "Cena", alimentacion));
        
        Map<Categoria, List<Gasto>> agrupados = catalogo.agruparPorCategoria();
        
        assertThat(agrupados).hasSize(2);
        assertThat(agrupados.get(alimentacion)).hasSize(2);
        assertThat(agrupados.get(transporte)).hasSize(1);
    }
    
    @Test
    @DisplayName("Agrupar gastos por mes")
    void testAgruparPorMes() {
        catalogo.agregarGasto(new Gasto(50.0, LocalDate.of(2025, 11, 15), "Nov1", alimentacion));
        catalogo.agregarGasto(new Gasto(30.0, LocalDate.of(2025, 11, 20), "Nov2", transporte));
        catalogo.agregarGasto(new Gasto(20.0, LocalDate.of(2025, 12, 5), "Dic1", alimentacion));
        
        Map<Month, List<Gasto>> agrupados = catalogo.agruparPorMes();
        
        assertThat(agrupados).hasSize(2);
        assertThat(agrupados.get(Month.NOVEMBER)).hasSize(2);
        assertThat(agrupados.get(Month.DECEMBER)).hasSize(1);
    }
    
    @Test
    @DisplayName("Obtener gastos por categoría")
    void testObtenerPorCategoria() {
        catalogo.agregarGasto(new Gasto(50.0, LocalDate.now(), "Comida", alimentacion));
        catalogo.agregarGasto(new Gasto(30.0, LocalDate.now(), "Bus", transporte));
        catalogo.agregarGasto(new Gasto(20.0, LocalDate.now(), "Cena", alimentacion));
        
        List<Gasto> gastosAlimentacion = catalogo.obtenerPorCategoria(alimentacion);
        
        assertThat(gastosAlimentacion).hasSize(2);
        assertThat(gastosAlimentacion).allMatch(g -> g.esDeCategoria(alimentacion));
    }
}
