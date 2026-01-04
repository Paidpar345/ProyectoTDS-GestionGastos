package importador;

import dominio.Categoria;
import dominio.Gasto;
import catalogos.CatalogoCategorias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Adaptador para parsear archivos CSV en formato bancario.
 * <p>
 * Utiliza Java Streams para procesamiento funcional del archivo.
 * Patrón Adapter: convierte formato externo (CSV) a objetos del dominio.
 * </p>
 * @version 2.0 - Optimizado con Streams
 * @since 2025-11-22
 */
public class AdaptadorBancario implements AdaptadorFormato {
    private static final Logger logger = LoggerFactory.getLogger(AdaptadorBancario.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORMATTER_ALT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
    
    private final CatalogoCategorias catalogoCategorias;
    
    public AdaptadorBancario(CatalogoCategorias catalogoCategorias) {
        this.catalogoCategorias = Objects.requireNonNull(catalogoCategorias, 
            "El catálogo de categorías no puede ser nulo");
    }
    
    @Override
    public List<Gasto> parsear(String contenidoArchivo) {
        logger.info("Iniciando parseo de archivo CSV con formato bancario");
        
        List<Gasto> gastosParseados = Arrays.stream(contenidoArchivo.split("\r?\n"))
                .skip(1) // Saltar cabecera (primera línea)
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .map(this::parsearLinea)
                .filter(Objects::nonNull) // Filtrar líneas con error
                .collect(Collectors.toList());
        
        logger.info("Parseados {} gastos correctamente", gastosParseados.size());
        return gastosParseados;
    }
    
    /**
     * Parsea una línea CSV y retorna un Gasto.
     * Formato esperado: Date,Account,Category,Subcategory,Note,Payer,Amount,Currency
     * Retorna null si hay error de formato (se filtra después).
     */
    private Gasto parsearLinea(String linea) {
        try {
            String[] campos = linea.split(",");
            if (campos.length < 8) {
                logger.warn("Línea con formato incorrecto (menos de 8 campos): {}", linea);
                return null;
            }
            
            // Date,Account,Category,Subcategory,Note,Payer,Amount,Currency
            LocalDate fecha = parsearFecha(campos[0].trim());
            double cantidad = parsearCantidad(campos[6].trim()); // Amount
            String descripcion = campos[4].trim(); // Note
            String nombreCategoria = campos[3].trim(); // Subcategory
            
            Categoria categoria = buscarOCrearCategoria(nombreCategoria);
            return new Gasto(cantidad, fecha, descripcion, categoria);
            
        } catch (DateTimeParseException e) {
            logger.warn("Error al parsear fecha en línea: {} - {}", linea, e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            logger.warn("Error al parsear cantidad en línea: {} - {}", linea, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error inesperado al parsear línea: {} - {}", linea, e.getMessage());
            return null;
        }
    }
    
    /**
     * Parsea fecha soportando múltiples formatos.
     */
    private LocalDate parsearFecha(String fechaStr) {
        try {
            return LocalDate.parse(fechaStr, FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(fechaStr, FORMATTER_ALT);
            } catch (DateTimeParseException e2) {
                // Formato con hora: M/d/yyyy H:mm
                LocalDateTime dateTime = LocalDateTime.parse(fechaStr, FORMATTER_DATETIME);
                return dateTime.toLocalDate();
            }
        }
    }
    
    /**
     * Parsea cantidad soportando diferentes separadores decimales.
     */
    private double parsearCantidad(String cantidadStr) {
        String normalizada = cantidadStr.replace(",", ".");
        return Double.parseDouble(normalizada);
    }
    
    @Override
    public boolean puedeManejar(String contenidoArchivo) {
        return contenidoArchivo != null && 
               contenidoArchivo.contains(",") && 
               contenidoArchivo.split("\r?\n").length > 1;
    }
    
    /**
     * Busca categoría existente o crea una nueva si no existe.
     * Usa Optional para manejo funcional.
     */
    private Categoria buscarOCrearCategoria(String nombre) {
        return catalogoCategorias.buscarPorNombre(nombre)
                .orElseGet(() -> {
                    logger.debug("Creando nueva categoría automáticamente: {}", nombre);
                    Categoria nueva = new Categoria(nombre, "Importada automáticamente");
                    catalogoCategorias.agregarCategoria(nueva);
                    return nueva;
                });
    }
    
    @Override
    public String toString() {
        return "AdaptadorBancario[formato=CSV, separador=',']";
    }
}
