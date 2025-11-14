package dominio;

import java.util.UUID;

/**
 * Representa una persona participante en una cuenta compartida de gastos.
 * <p>
 * Almacena el nombre, porcentaje asignado de gasto y saldo calculado en la cuenta. Incluye validación de datos y métodos para gestión de saldo.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */

public class Persona {
    private String id;
    private String nombre;
    private double porcentajeGasto;
    private double saldo;
    
    public Persona() {
        this.id = UUID.randomUUID().toString();
        this.saldo = 0.0;
    }
    
    public Persona(String nombre) {
        this();
        validarNombre(nombre);
        this.nombre = nombre;
    }
    
    public Persona(String nombre, double porcentajeGasto) {
        this(nombre);
        setPorcentajeGasto(porcentajeGasto);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre;
    }
    
    public double getPorcentajeGasto() {
        return porcentajeGasto;
    }
    
    public void setPorcentajeGasto(double porcentajeGasto) {
        if (porcentajeGasto < 0 || porcentajeGasto > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        this.porcentajeGasto = porcentajeGasto;
    }
    
    public double getSaldo() {
        return saldo;
    }
    
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Persona)) return false;
        Persona other = (Persona) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s (Saldo: %.2f€)", nombre, saldo);
    }
}
