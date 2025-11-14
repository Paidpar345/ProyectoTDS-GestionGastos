package dominio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa una notificación generada tras la superación de una alerta de gasto.
 * <p>
 * Incluye el mensaje informativo, la fecha de generación, el estado de leído y una referencia a la alerta que la generó. 
 * Es utilizada en el historial de notificaciones del usuario.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */


public class Notificacion {
    private String id;
    private String mensaje;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaGeneracion;
    
    private boolean leida;

    @JsonIgnore
    private Alerta alerta;
    
    public Notificacion() {
        this.id = UUID.randomUUID().toString();
        this.fechaGeneracion = LocalDateTime.now();
        this.leida = false;
    }
    
    public Notificacion(String mensaje, Alerta alerta) {
        this();
        this.mensaje = mensaje;
        this.alerta = alerta;
    }
    
    public void marcarComoLeida() {
        this.leida = true;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    public boolean isLeida() {
        return leida;
    }
    
    public void setLeida(boolean leida) {
        this.leida = leida;
    }
    
    @JsonIgnore
    public Alerta getAlerta() {
        return alerta;
    }
    
    public void setAlerta(Alerta alerta) {
        this.alerta = alerta;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s %s",
                fechaGeneracion.toString(),
                mensaje,
                leida ? "(Leída)" : "(No leída)");
    }
}
