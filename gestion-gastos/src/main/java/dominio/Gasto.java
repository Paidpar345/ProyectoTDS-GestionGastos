package dominio;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

/**
 * Representa un gasto individual registrado en el sistema.
 * <p>
 * Un gasto tiene cantidad, fecha, categoría, descripción y puede estar asociado a un pagador si pertenece a una cuenta compartida. Proporciona métodos para validación, cálculo de aportes y pertenencia a categoría/mes/intervalo.<br>
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */

public class Gasto {


    private String id;
    private double cantidad;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    private String descripcion;
    private Categoria categoria;
    private Persona pagador; // null si es gasto personal
    
    public Gasto() {
        this.id = UUID.randomUUID().toString();
    }
    
    public Gasto(double cantidad, LocalDate fecha, String descripcion, Categoria categoria) {
        this();
        validarCantidad(cantidad);
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public boolean perteneceAlMes(Month mes) {
        return this.fecha.getMonth() == mes;
    }

    public boolean estaEnRango(LocalDate inicio, LocalDate fin) {
        return !this.fecha.isBefore(inicio) && !this.fecha.isAfter(fin);
    }
    
    
    public boolean esDeCategoria(Categoria categoria) {
        if (this.categoria == null || categoria == null) {
            return false;
        }

        return this.categoria.getNombre().equalsIgnoreCase(categoria.getNombre());
    }

    public double calcularAporte(double porcentaje) {
        return this.cantidad * (porcentaje / 100.0);
    }

    private void validarCantidad(double cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public double getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(double cantidad) {
        validarCantidad(cantidad);
        this.cantidad = cantidad;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public Persona getPagador() {
        return pagador;
    }
    
    public void setPagador(Persona pagador) {
        this.pagador = pagador;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Gasto)) return false;
        Gasto other = (Gasto) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Gasto{cantidad=%.2f, fecha=%s, categoria=%s}",
                cantidad, fecha, categoria != null ? categoria.getNombre() : "Sin categoría");
    }
}
