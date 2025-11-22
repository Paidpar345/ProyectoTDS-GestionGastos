package controlador;

import dominio.Alerta;
import dominio.Notificacion;
import dominio.enums.PeriodoTemporal;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Gestor centralizado de notificaciones pop-up usando ControlsFX.
 * Adaptado específicamente para el sistema de alertas de gastos.
 * 
 * @version 1.0
 * @since 2025-11-15
 */
public class NotificacionManager {
    
    private Stage ownerStage;
    private Pos posicionPorDefecto = Pos.TOP_RIGHT;
    
    public NotificacionManager(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }
    
    /**
     * Muestra una notificación de información general.
     */
    public void mostrarInfo(String titulo, String mensaje) {
        Notifications.create()
            .title(titulo)
            .text(mensaje)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(4))
            .owner(ownerStage)
            .darkStyle()
            .showInformation();
    }
    
    /**
     * Muestra una notificación de éxito.
     */
    public void mostrarExito(String titulo, String mensaje) {
        Notifications.create()
            .title(titulo)
            .text(mensaje)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(3))
            .owner(ownerStage)
            .darkStyle()
            .showConfirm();
    }
    
    /**
     * Muestra una notificación de advertencia.
     */
    public void mostrarAdvertencia(String titulo, String mensaje) {
        Notifications.create()
            .title(titulo)
            .text(mensaje)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(6))
            .owner(ownerStage)
            .darkStyle()
            .showWarning();
    }
    
    /**
     * Muestra una notificación de error.
     */
    public void mostrarError(String titulo, String mensaje) {
        Notifications.create()
            .title(titulo)
            .text(mensaje)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(7))
            .owner(ownerStage)
            .darkStyle()
            .showError();
    }
    
    /**
     * Muestra una alerta de gasto basada en una Notificacion del dominio.
     * Analiza el mensaje para determinar la severidad y muestra el pop-up apropiado.
     * 
     * @param notificacion Notificación generada por el sistema de alertas
     */
    public void mostrarAlertaGasto(Notificacion notificacion) {
        String mensaje = notificacion.getMensaje();
        Alerta alerta = notificacion.getAlerta();
        
        // Determinar tipo de alerta y emoji
        String emoji;
        boolean esLimiteSuperado = mensaje.contains("superado") || mensaje.contains("excedido");
        boolean esAlto = mensaje.contains("80%") || mensaje.contains("alcanzado");
        
        if (esLimiteSuperado) {
            emoji = "🚨";
        } else if (esAlto) {
            emoji = "⚠️";
        } else {
            emoji = "ℹ️";
        }
        
        // Crear gráfico de alerta
        Label grafico = new Label(emoji);
        grafico.setStyle("-fx-font-size: 48px;");
        
        // Construir título según el tipo de periodo
        String titulo;
        if (alerta != null && alerta.getPeriodo() != null) {
            PeriodoTemporal periodo = alerta.getPeriodo();
            if (periodo == PeriodoTemporal.SEMANAL) {
                titulo = esLimiteSuperado ? "🚨 ALERTA SEMANAL - LÍMITE SUPERADO" : "⚠️ Alerta Semanal";
            } else if (periodo == PeriodoTemporal.MENSUAL) {
                titulo = esLimiteSuperado ? "🚨 ALERTA MENSUAL - LÍMITE SUPERADO" : "⚠️ Alerta Mensual";
            } else {
                titulo = "ℹ️ Alerta de Gasto";
            }
        } else {
            titulo = "⚠️ Alerta de Gasto";
        }
        
        Notifications notif = Notifications.create()
            .title(titulo)
            .text(mensaje)
            .graphic(grafico)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(esLimiteSuperado ? 10 : 7))
            .owner(ownerStage)
            .darkStyle();
        
        // Mostrar según severidad
        if (esLimiteSuperado) {
            notif.showError();
        } else if (esAlto) {
            notif.showWarning();
        } else {
            notif.showInformation();
        }
    }
    
    /**
     * Muestra una alerta de gasto con información detallada y progreso.
     * 
     * @param alerta Alerta del sistema
     * @param gastoActual Gasto acumulado actual
     * @param accion Acción a ejecutar al hacer clic (opcional)
     */
    public void mostrarAlertaDetallada(Alerta alerta, double gastoActual, Runnable accion) {
        double limite = alerta.getLimiteGasto();
        double porcentaje = (gastoActual / limite) * 100;
        
        // Determinar emoji y color según severidad
        String emoji;
        String colorBarra;
        if (porcentaje >= 100) {
            emoji = "🚨";
            colorBarra = "#E74C3C";
        } else if (porcentaje >= 80) {
            emoji = "⚠️";
            colorBarra = "#F39C12";
        } else if (porcentaje >= 60) {
            emoji = "📊";
            colorBarra = "#3498DB";
        } else {
            emoji = "ℹ️";
            colorBarra = "#27AE60";
        }
        
        // Construir contenido personalizado
        VBox contenido = new VBox(8);
        contenido.setStyle("-fx-padding: 10;");
        
        // Información de periodo
        String tipoPeriodo = alerta.getPeriodo() != null 
            ? alerta.getPeriodo().getDescripcion() 
            : "Global";
        Label lblPeriodo = new Label("Periodo: " + tipoPeriodo);
        lblPeriodo.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
        
        // Información de categoría
        String infoCategoria = alerta.getCategoria() != null 
            ? "Categoría: " + alerta.getCategoria().getNombre()
            : "Todas las categorías";
        Label lblCategoria = new Label(infoCategoria);
        lblCategoria.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
        
        // Barra de progreso visual
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(
            Math.min(porcentaje / 100.0, 1.0)
        );
        progressBar.setPrefWidth(250);
        progressBar.setStyle(String.format("-fx-accent: %s;", colorBarra));
        
        // Texto de progreso
        String textoProgreso = String.format("%.2f€ / %.2f€ (%.1f%%)", 
            gastoActual, limite, porcentaje);
        Label lblProgreso = new Label(textoProgreso);
        lblProgreso.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        contenido.getChildren().addAll(lblPeriodo, lblCategoria, progressBar, lblProgreso);
        
        // Gráfico emoji
        Label grafico = new Label(emoji);
        grafico.setStyle("-fx-font-size: 40px;");
        
        // Título según severidad
        String titulo;
        if (porcentaje >= 100) {
            titulo = "🚨 LÍMITE SUPERADO";
        } else if (porcentaje >= 80) {
            titulo = "⚠️ ADVERTENCIA - Cerca del límite";
        } else {
            titulo = "📊 Progreso de Gasto";
        }
        
        Notifications notificacion = Notifications.create()
            .title(titulo)
            .graphic(contenido)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(porcentaje >= 80 ? 10 : 6))
            .owner(ownerStage)
            .darkStyle();
        
        // Añadir acción si se proporciona
        if (accion != null) {
            notificacion.onAction(e -> accion.run());
        }
        
        // Mostrar según severidad
        if (porcentaje >= 100) {
            notificacion.showError();
        } else if (porcentaje >= 80) {
            notificacion.showWarning();
        } else {
            notificacion.showInformation();
        }
    }
    
    /**
     * Muestra resumen de múltiples alertas cuando hay muchas pendientes.
     */
    public void mostrarResumenAlertas(int cantidadAlertas, Runnable accionVerTodas) {
        VBox contenido = new VBox(8);
        contenido.setStyle("-fx-padding: 10;");
        
        Label lblInfo = new Label(
            String.format("Tienes %d alertas de gasto activas", cantidadAlertas)
        );
        lblInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        
        Label lblAccion = new Label("Haz clic para ver todas");
        lblAccion.setStyle("-fx-font-size: 11px; -fx-text-fill: #BDC3C7;");
        
        contenido.getChildren().addAll(lblInfo, lblAccion);
        
        Label grafico = new Label("📋");
        grafico.setStyle("-fx-font-size: 40px;");
        
        Notifications notif = Notifications.create()
            .title("⚠️ Múltiples Alertas Activas")
            .graphic(contenido)
            .position(posicionPorDefecto)
            .hideAfter(Duration.seconds(8))
            .owner(ownerStage)
            .darkStyle();
        
        if (accionVerTodas != null) {
            notif.onAction(e -> accionVerTodas.run());
        }
        
        notif.showWarning();
    }
    
    /**
     * Cambia la posición de las notificaciones.
     */
    public void setPosicion(Pos posicion) {
        this.posicionPorDefecto = posicion;
    }
}
