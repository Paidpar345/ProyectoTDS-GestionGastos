package controlador;

import dominio.*;
import dominio.enums.PeriodoTemporal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import catalogos.CatalogoAlertas;
import catalogos.CatalogoCategorias;
import repositorio.Repositorio;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Tests de integración para el sistema de alertas.
 */
@DisplayName("Tests de Integración - Sistema de Alertas")
class ControladorAlertasIntegrationTest {
    
    @Mock
    private Repositorio repositorio;
    
    private CatalogoAlertas catalogoAlertas;
    private CatalogoCategorias catalogoCategorias;
    private ControladorAlertas controlador;
    private Categoria alimentacion;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        catalogoAlertas = new CatalogoAlertas();
        catalogoCategorias = new CatalogoCategorias();
        alimentacion = catalogoCategorias.buscarPorNombre("Alimentación").get();
        
        controlador = new ControladorAlertas(
            repositorio,
            catalogoAlertas,
            catalogoCategorias
        );
    }
    
    @Test
    @DisplayName("Crear alerta semanal y verificar superación")
    void testAlertaSemanalSuperada() {
        // Crear alerta semanal de 100€
        controlador.crearAlerta(100.0, PeriodoTemporal.SEMANAL, null);
        
        // Crear gastos que superen el límite
        List<Gasto> gastos = List.of(
            new Gasto(60.0, LocalDate.now(), "Gasto 1", alimentacion),
            new Gasto(50.0, LocalDate.now(), "Gasto 2", alimentacion)
        );
        
        // Verificar alertas
        controlador.verificarAlertas(gastos);
        
        // Comprobar que se generó notificación
        List<Notificacion> notificaciones = controlador.obtenerNotificacionesNoLeidas();
        assertThat(notificaciones).isNotEmpty();
        assertThat(notificaciones.get(0).getMensaje()).contains("superado");
    }
    
    @Test
    @DisplayName("Alerta mensual por categoría")
    void testAlertaMensualPorCategoria() {
        // Crear alerta mensual de 200€ para Alimentación
        controlador.crearAlerta(200.0, PeriodoTemporal.MENSUAL, "Alimentación");
        
        Categoria transporte = catalogoCategorias.buscarPorNombre("Transporte").get();
        
        List<Gasto> gastos = List.of(
            new Gasto(150.0, LocalDate.now(), "Comida", alimentacion),
            new Gasto(100.0, LocalDate.now(), "Más comida", alimentacion),
            new Gasto(500.0, LocalDate.now(), "Gasolina", transporte) // No cuenta
        );
        
        controlador.verificarAlertas(gastos);
        
        List<Notificacion> notificaciones = controlador.obtenerNotificacionesNoLeidas();
        assertThat(notificaciones).isNotEmpty();
        assertThat(notificaciones.get(0).getMensaje()).contains("Alimentación");
    }
    
    @Test
    @DisplayName("Modificar alerta actualiza persistencia")
    void testModificarAlertaActualizaPersistencia() {
        controlador.crearAlerta(100.0, PeriodoTemporal.SEMANAL, null);
        Alerta alerta = catalogoAlertas.obtenerTodas().get(0);
        
        reset(repositorio);
        
        controlador.modificarAlerta(alerta.getId(), 150.0, true);
        
        verify(repositorio, times(1)).guardarAlertas(anyList());
        assertThat(alerta.getLimiteGasto()).isEqualTo(150.0);
    }
    
    @Test
    @DisplayName("Marcar notificaciones como leídas")
    void testMarcarNotificacionesLeidas() {
        controlador.crearAlerta(50.0, PeriodoTemporal.SEMANAL, null);
        
        List<Gasto> gastos = List.of(
            new Gasto(100.0, LocalDate.now(), "Gasto", alimentacion)
        );
        
        controlador.verificarAlertas(gastos);
        assertThat(controlador.contarNotificacionesNoLeidas()).isGreaterThan(0);
        
        controlador.marcarTodasLasNotificacionesComoLeidas();
        
        assertThat(controlador.contarNotificacionesNoLeidas()).isZero();
    }
}
