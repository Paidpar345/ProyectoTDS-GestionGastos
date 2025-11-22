package dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de caja blanca para la clase Gasto.
 * Prueba la lógica interna y flujos de control.
 */
@DisplayName("Tests de Caja Blanca - Gasto")
class GastoTest {
    
    private Categoria categoria;
    private Gasto gasto;
    
    @BeforeEach
    void setUp() {
        categoria = new Categoria("Alimentación", "Comida y bebida");
        gasto = new Gasto(50.0, LocalDate.of(2025, 11, 15), "Supermercado", categoria);
    }
    
    @Test
    @DisplayName("Validar cantidad positiva")
    void testCantidadPositiva() {
        assertThat(gasto.getCantidad()).isEqualTo(50.0);
    }
    
    @Test
    @DisplayName("Rechazar cantidad negativa")
    void testCantidadNegativa() {
        assertThatThrownBy(() -> new Gasto(-10.0, LocalDate.now(), "Test", categoria))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no puede ser negativa");
    }
    
    @Test
    @DisplayName("Calcular aporte según porcentaje")
    void testCalcularAporte() {
        double aporte = gasto.calcularAporte(50.0); // 50%
        assertThat(aporte).isEqualTo(25.0);
        
        double aporte100 = gasto.calcularAporte(100.0); // 100%
        assertThat(aporte100).isEqualTo(50.0);
        
        double aporte0 = gasto.calcularAporte(0.0); // 0%
        assertThat(aporte0).isZero();
    }
    
    @Test
    @DisplayName("Verificar pertenencia al mes")
    void testPerteneceAlMes() {
        assertThat(gasto.perteneceAlMes(Month.NOVEMBER)).isTrue();
        assertThat(gasto.perteneceAlMes(Month.DECEMBER)).isFalse();
    }
    
    @Test
    @DisplayName("Verificar rango de fechas - camino true")
    void testEstaEnRango_True() {
        LocalDate inicio = LocalDate.of(2025, 11, 1);
        LocalDate fin = LocalDate.of(2025, 11, 30);
        
        assertThat(gasto.estaEnRango(inicio, fin)).isTrue();
    }
    
    @Test
    @DisplayName("Verificar rango de fechas - camino false (antes)")
    void testEstaEnRango_FalseAntes() {
        LocalDate inicio = LocalDate.of(2025, 12, 1);
        LocalDate fin = LocalDate.of(2025, 12, 31);
        
        assertThat(gasto.estaEnRango(inicio, fin)).isFalse();
    }
    
    @Test
    @DisplayName("Verificar pertenencia a categoría - nombre correcto")
    void testEsDeCategoria_True() {
        Categoria categoriaBuscar = new Categoria("Alimentación", "Desc");
        assertThat(gasto.esDeCategoria(categoriaBuscar)).isTrue();
    }
    
    @Test
    @DisplayName("Verificar pertenencia a categoría - nombre diferente")
    void testEsDeCategoria_False() {
        Categoria otraCategoria = new Categoria("Transporte", "Desc");
        assertThat(gasto.esDeCategoria(otraCategoria)).isFalse();
    }
    
    @Test
    @DisplayName("Verificar pertenencia a categoría - null")
    void testEsDeCategoria_Null() {
        assertThat(gasto.esDeCategoria(null)).isFalse();
    }
    
    @Test
    @DisplayName("Modificar cantidad actualiza valor")
    void testSetCantidad() {
        gasto.setCantidad(75.0);
        assertThat(gasto.getCantidad()).isEqualTo(75.0);
    }
    
    @Test
    @DisplayName("ID único entre instancias")
    void testIdsUnicos() {
        Gasto gasto2 = new Gasto(30.0, LocalDate.now(), "Otro gasto", categoria);
        assertThat(gasto.getId()).isNotEqualTo(gasto2.getId());
    }
}
