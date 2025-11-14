package importador;

import dominio.Categoria;
import dominio.CatalogoCategorias;
import dominio.Gasto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para parsear archivos CSV en formato bancario.
 * <p>
 * Convierte archivos CSV con formato fecha;cantidad;descripcion;categoria
 * en objetos Gasto. Crea automáticamente categorías no existentes.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */


public class AdaptadorBancario implements AdaptadorFormato {
    private CatalogoCategorias catalogoCategorias;
    
    public AdaptadorBancario(CatalogoCategorias catalogoCategorias) {
        this.catalogoCategorias = catalogoCategorias;
    }
    
    @Override
    public List<Gasto> parsear(String contenidoArchivo) {
        List<Gasto> gastos = new ArrayList<>();
        String[] lineas = contenidoArchivo.split("\n");


        
        for (int i = 1; i < lineas.length; i++) { // Saltar cabecera
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;
            
            String[] campos = linea.split(";");
            if (campos.length >= 4) {
                try {
                    LocalDate fecha = LocalDate.parse(campos[0], DateTimeFormatter.ISO_LOCAL_DATE);
                    double cantidad = Double.parseDouble(campos[1].replace(",", "."));
                    String descripcion = campos[2];
                    String nombreCategoria = campos[3];
                    
                    Categoria categoria = buscarOCrearCategoria(nombreCategoria);
                    Gasto gasto = new Gasto(cantidad, fecha, descripcion, categoria);
                    gastos.add(gasto);
                    
                } catch (Exception e) {
                    System.err.println("⚠ Error al parsear línea: " + linea + " - " + e.getMessage());
                }
            }
        }
        
        return gastos;
    }
    
    @Override
    public boolean puedeManear(String contenidoArchivo) {

        return contenidoArchivo.contains(";") && 
               contenidoArchivo.split("\n").length > 1;
    }
    
    
    private Categoria buscarOCrearCategoria(String nombre) {
        return catalogoCategorias.buscarPorNombre(nombre)
                .orElseGet(() -> {
                    Categoria nueva = new Categoria(nombre, "Importada automáticamente");
                    catalogoCategorias.agregarCategoria(nueva);
                    return nueva;
                });
    }
}
