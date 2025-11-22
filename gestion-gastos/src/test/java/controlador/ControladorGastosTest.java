package controlador;

import dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import catalogos.CatalogoCategorias;
import catalogos.CatalogoGastos;
import repositorio.Repositorio;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests con Mocks para ControladorGastos.
 * Usa Mockito para simular dependencias.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests con Mocks - ControladorGastos")
class ControladorGastosTest {
    
    @Mock
    private Repositorio repositorio;
    
    @Mock
    private ControladorAlertas controladorAlertas;
    
    private CatalogoGastos catalogoGastos;
    private CatalogoCategorias catalogoCategorias;
    private ControladorGastos controlador;
    private Categoria alimentacion;
    
    @BeforeEach
    void setUp() {
        catalogoGastos = new CatalogoGastos();
        catalogoCategorias = new CatalogoCategorias();
        alimentacion = catalogoCategorias.buscarPorNombre("Alimentación").get();
        
        controlador = new ControladorGastos(
            repositorio,
            catalogoGastos,
            catalogoCategorias,
            controladorAlertas
        );
    }
    
    @Test
    @DisplayName("Registrar gasto invoca persistencia")
    void testRegistrarGastoInvocaPersistencia() {
        controlador.registrarGasto(50.0, LocalDate.now(), "Test", "Alimentación");
        
        verify(repositorio, times(1)).guardarGastos(anyList());
        verify(repositorio, times(1)).guardarCategorias(anyList());
        verify(controladorAlertas, times(1)).verificarAlertas(anyList());
    }
    
    @Test
    @DisplayName("Registrar gasto crea categoría si no existe")
    void testRegistrarGastoCreaCategoriaNoExistente() {
        String categoriaNueva = "CategoríaNueva";
        
        controlador.registrarGasto(50.0, LocalDate.now(), "Test", categoriaNueva);
        
        Optional<Categoria> categoria = catalogoCategorias.buscarPorNombre(categoriaNueva);
        assertThat(categoria).isPresent();
        assertThat(categoria.get().getNombre()).isEqualTo(categoriaNueva);
    }
    
    @Test
    @DisplayName("Registrar gasto agrega al catálogo")
    void testRegistrarGastoAgregaAlCatalogo() {
        int cantidadInicial = catalogoGastos.cantidadGastos();
        
        controlador.registrarGasto(50.0, LocalDate.now(), "Test", "Alimentación");
        
        assertThat(catalogoGastos.cantidadGastos()).isEqualTo(cantidadInicial + 1);
    }
    
    @Test
    @DisplayName("Modificar gasto invoca persistencia")
    void testModificarGastoInvocaPersistencia() {
        controlador.registrarGasto(50.0, LocalDate.now(), "Original", "Alimentación");
        Gasto gasto = catalogoGastos.obtenerTodos().get(0);
        
        // CORREGIDO: clearInvocations en lugar de reset
        clearInvocations(repositorio, controladorAlertas);
        
        controlador.modificarGasto(
            gasto.getId(), 75.0, LocalDate.now(), "Modificado", "Alimentación"
        );
        
        verify(repositorio, times(1)).guardarGastos(anyList());
        verify(controladorAlertas, times(1)).verificarAlertas(anyList());
    }

    
    @Test
    @DisplayName("Modificar gasto inexistente lanza excepción")
    void testModificarGastoInexistente() {
        assertThatThrownBy(() -> 
            controlador.modificarGasto(
                "id-inexistente", 50.0, LocalDate.now(), "Test", "Alimentación"
            )
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("no encontrado");
    }
    
    @Test
    @DisplayName("Eliminar gasto invoca persistencia")
    void testEliminarGastoInvocaPersistencia() {
        controlador.registrarGasto(50.0, LocalDate.now(), "Test", "Alimentación");
        Gasto gasto = catalogoGastos.obtenerTodos().get(0);
        
        reset(repositorio);
        
        controlador.eliminarGasto(gasto.getId());
        
        verify(repositorio, times(1)).guardarGastos(anyList());
        assertThat(catalogoGastos.cantidadGastos()).isZero();
    }
    
    @Test
    @DisplayName("Filtrar por categorías con categorías válidas")
    void testFiltrarPorCategorias() {
        controlador.registrarGasto(50.0, LocalDate.now(), "Comida", "Alimentación");
        controlador.registrarGasto(30.0, LocalDate.now(), "Bus", "Transporte");
        
        List<Gasto> filtrados = controlador.filtrarPorCategorias(
            Arrays.asList("Alimentación")
        );
        
        assertThat(filtrados).hasSize(1);
        assertThat(filtrados.get(0).getCategoria().getNombre()).isEqualTo("Alimentación");
    }
    
    @Test
    @DisplayName("Filtrar por categorías vacías lanza excepción")
    void testFiltrarPorCategoriasVacia() {
        assertThatThrownBy(() -> controlador.filtrarPorCategorias(Arrays.asList()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("al menos una categoria");
    }
    
    @Test
    @DisplayName("Filtrar por fechas válidas")
    void testFiltrarPorFecha() {
        LocalDate hoy = LocalDate.now();
        controlador.registrarGasto(50.0, hoy, "Hoy", "Alimentación");
        controlador.registrarGasto(30.0, hoy.minusDays(10), "Hace 10 días", "Transporte");
        
        List<Gasto> filtrados = controlador.filtrarPorFecha(
            hoy.minusDays(5), hoy
        );
        
        assertThat(filtrados).hasSize(1);
    }
    
    @Test
    @DisplayName("Calcular total de gastos")
    void testCalcularTotalGastos() {
        controlador.registrarGasto(50.0, LocalDate.now(), "G1", "Alimentación");
        controlador.registrarGasto(30.0, LocalDate.now(), "G2", "Transporte");
        
        double total = controlador.calcularTotalGastos();
        
        assertThat(total).isEqualTo(80.0);
    }
    
    @Test
    @DisplayName("Verificar que alertas se invocan en cada operación")
    void testVerificarAlertasEnOperaciones() {
        // Registrar
        controlador.registrarGasto(50.0, LocalDate.now(), "Test", "Alimentación");
        verify(controladorAlertas, times(1)).verificarAlertas(anyList());
        
        // Modificar
        Gasto gasto = catalogoGastos.obtenerTodos().get(0);
        controlador.modificarGasto(gasto.getId(), 60.0, LocalDate.now(), "Mod", "Alimentación");
        verify(controladorAlertas, times(2)).verificarAlertas(anyList());
    }
}
