package dominio;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa una categoría a la que se pueden asociar gastos (por ejemplo, alimentación, transporte).
 * <p>
 * Cada categoría tiene un nombre y una descripción y se identifica mediante un identificador único. Contiene validaciones para evitar nombres no válidos y sobrecarga equals/hashCode basada en el nombre.
 * </p>
 * @version 1.0
 * @since 2025-11-14
 */

public class Categoria {
    private String id;
    private String nombre;
    private String descripcion;
    
    public Categoria() {
        this.id = UUID.randomUUID().toString();
    }
    
    public Categoria(String nombre, String descripcion) {
        this();
        validarNombre(nombre);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return Objects.equals(nombre, categoria.nombre);
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
