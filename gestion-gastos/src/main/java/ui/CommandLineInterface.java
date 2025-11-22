package ui;

import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de línea de comandos (CLI) para la gestión de gastos.
 * <p>
 * Proporciona un menú interactivo por consola para realizar todas las operaciones
 * del sistema: registrar gastos, listar, modificar, borrar, gestionar categorías
 * y ver estadísticas. Permite ejecutar la aplicación sin interfaz gráfica.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class CommandLineInterface {
    private FachadaAplicacion fachada;
    private Scanner scanner;
    private boolean running;
    private DateTimeFormatter dateFormatter;
    
    public CommandLineInterface(FachadaAplicacion fachada) {
        this.fachada = fachada;
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
    
    public void iniciar() {
        System.out.println("==============================================");
        System.out.println("  SISTEMA DE GESTION DE GASTOS - CLI");
        System.out.println("==============================================\n");
        
        while (running) {
            mostrarMenu();
            procesarComando();
        }
        
        scanner.close();
    }
    
    private void mostrarMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Registrar nuevo gasto");
        System.out.println("2. Listar gastos");
        System.out.println("3. Modificar gasto");
        System.out.println("4. Borrar gasto");
        System.out.println("5. Listar categorias");
        System.out.println("6. Crear categoria");
        System.out.println("7. Ver estadisticas");
        System.out.println("8. Abrir interfaz grafica");
        System.out.println("0. Salir");
        System.out.print("\nSeleccione opcion: ");
    }
    
    private void procesarComando() {
        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea
            
            switch (opcion) {
                case 1 -> registrarGasto();
                case 2 -> listarGastos();
                case 3 -> modificarGasto();
                case 4 -> borrarGasto();
                case 5 -> listarCategorias();
                case 6 -> crearCategoria();
                case 7 -> verEstadisticas();
                case 8 -> abrirInterfazGrafica();
                case 0 -> {
                    running = false;
                    System.out.println("\nCerrando aplicacion...");
                }
                default -> System.out.println("\nOpcion no valida. Intente de nuevo.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            scanner.nextLine(); // Limpiar buffer
        }
    }
    
    private void registrarGasto() {
        System.out.println("\n=== REGISTRAR NUEVO GASTO ===");
        
        try {
            System.out.print("Cantidad (EUR): ");
            double cantidad = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Fecha (dd/MM/yyyy) [Enter para hoy]: ");
            String fechaStr = scanner.nextLine();
            LocalDate fecha = fechaStr.isEmpty() ? LocalDate.now() : 
                             LocalDate.parse(fechaStr, dateFormatter);
            
            System.out.print("Descripcion: ");
            String descripcion = scanner.nextLine();

            List<Categoria> categorias = fachada.getControladorCategorias().obtenerTodasLasCategorias();
            System.out.println("\nCategorias disponibles:");
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println((i + 1) + ". " + categorias.get(i).getNombre());
            }
            
            System.out.print("Seleccione categoria (numero): ");
            int catIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (catIndex < 0 || catIndex >= categorias.size()) {
                System.out.println("Categoria invalida.");
                return;
            }
            
            String nombreCategoria = categorias.get(catIndex).getNombre();
            
            fachada.getControladorGastos().registrarGasto(cantidad, fecha, descripcion, nombreCategoria);
            
            System.out.println("\n✓ Gasto registrado correctamente.");
            
        } catch (DateTimeParseException e) {
            System.out.println("Error: Formato de fecha incorrecto. Use dd/MM/yyyy");
        } catch (Exception e) {
            System.out.println("Error al registrar gasto: " + e.getMessage());
        }
    }
    
    private void listarGastos() {
        System.out.println("\n=== LISTA DE GASTOS ===\n");
        
        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        
        if (gastos.isEmpty()) {
            System.out.println("No hay gastos registrados.");
            return;
        }
        
        System.out.printf("%-5s %-12s %-12s %-20s %-30s%n", 
                         "ID", "FECHA", "CANTIDAD", "CATEGORIA", "DESCRIPCION");
        System.out.println("-".repeat(80));
        
        int id = 1;
        for (Gasto g : gastos) {
            System.out.printf("%-5d %-12s %,12.2f EUR %-20s %-30s%n",
                id++,
                g.getFecha().format(dateFormatter),
                g.getCantidad(),
                g.getCategoria() != null ? g.getCategoria().getNombre() : "Sin categoria",
                g.getDescripcion()
            );
        }
        
        double total = fachada.getControladorGastos().calcularTotalGastos();
        System.out.println("-".repeat(80));
        System.out.printf("TOTAL: %,12.2f EUR%n", total);
    }
    
    private void modificarGasto() {
        System.out.println("\n=== MODIFICAR GASTO ===");
        
        listarGastos();
        
        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        if (gastos.isEmpty()) return;
        
        try {
            System.out.print("\nSeleccione ID del gasto a modificar: ");
            int idSeleccionado = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (idSeleccionado < 0 || idSeleccionado >= gastos.size()) {
                System.out.println("ID invalido.");
                return;
            }
            
            Gasto gasto = gastos.get(idSeleccionado);
            
            System.out.print("Nueva cantidad [" + gasto.getCantidad() + "]: ");
            String cantidadStr = scanner.nextLine();
            double cantidad = cantidadStr.isEmpty() ? gasto.getCantidad() : Double.parseDouble(cantidadStr);
            
            System.out.print("Nueva fecha [" + gasto.getFecha().format(dateFormatter) + "]: ");
            String fechaStr = scanner.nextLine();
            LocalDate fecha = fechaStr.isEmpty() ? gasto.getFecha() : 
                             LocalDate.parse(fechaStr, dateFormatter);
            
            System.out.print("Nueva descripcion [" + gasto.getDescripcion() + "]: ");
            String descripcion = scanner.nextLine();
            if (descripcion.isEmpty()) descripcion = gasto.getDescripcion();
            
            String nombreCategoria = gasto.getCategoria().getNombre();
            
            fachada.getControladorGastos().modificarGasto(
                gasto.getId(), cantidad, fecha, descripcion, nombreCategoria
            );
            
            System.out.println("\n✓ Gasto modificado correctamente.");
            
        } catch (Exception e) {
            System.out.println("Error al modificar gasto: " + e.getMessage());
        }
    }
    
    private void borrarGasto() {
        System.out.println("\n=== BORRAR GASTO ===");
        
        listarGastos();
        
        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        if (gastos.isEmpty()) return;
        
        try {
            System.out.print("\nSeleccione ID del gasto a borrar: ");
            int idSeleccionado = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (idSeleccionado < 0 || idSeleccionado >= gastos.size()) {
                System.out.println("ID invalido.");
                return;
            }
            
            Gasto gasto = gastos.get(idSeleccionado);
            
            System.out.print("¿Confirma borrado? (S/N): ");
            String confirmacion = scanner.nextLine();
            
            if (confirmacion.equalsIgnoreCase("S")) {
                fachada.getControladorGastos().eliminarGasto(gasto.getId());
                System.out.println("\n✓ Gasto borrado correctamente.");
            } else {
                System.out.println("\nOperacion cancelada.");
            }
            
        } catch (Exception e) {
            System.out.println("Error al borrar gasto: " + e.getMessage());
        }
    }
    
    private void listarCategorias() {
        System.out.println("\n=== CATEGORIAS ===\n");
        
        List<Categoria> categorias = fachada.getControladorCategorias().obtenerTodasLasCategorias();
        
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias registradas.");
            return;
        }
        
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNombre());
        }
    }
    
    private void crearCategoria() {
        System.out.println("\n=== CREAR CATEGORIA ===");
        
        try {
            System.out.print("Nombre de la categoria: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Descripcion (opcional): ");
            String descripcion = scanner.nextLine();
            
            fachada.getControladorCategorias().crearCategoria(nombre, descripcion);
            
            System.out.println("\n✓ Categoria creada correctamente.");
            
        } catch (Exception e) {
            System.out.println("Error al crear categoria: " + e.getMessage());
        }
    }
    
    private void verEstadisticas() {
        System.out.println("\n=== ESTADISTICAS ===\n");
        
        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();
        double total = fachada.getControladorGastos().calcularTotalGastos();
        
        System.out.println("Total gastos registrados: " + gastos.size());
        System.out.printf("Importe total: %,.2f EUR%n", total);
        
        if (!gastos.isEmpty()) {
            double promedio = total / gastos.size();
            System.out.printf("Gasto promedio: %,.2f EUR%n", promedio);
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    private void abrirInterfazGrafica() {
        System.out.println("\nAbriendo interfaz grafica...");
        System.out.println("(La interfaz CLI seguira activa en esta terminal)");

        new Thread(() -> {
            javafx.application.Application.launch(MainApp.class);
        }).start();
    }
}
