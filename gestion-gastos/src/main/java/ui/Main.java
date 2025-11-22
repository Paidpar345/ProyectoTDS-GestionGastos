package ui;

import controlador.FachadaAplicacion;
/**
 * Clase principal de entrada a la aplicación.
 * <p>
 * Determina si se ejecuta en modo CLI (con argumento --cli) o en modo GUI
 * (interfaz gráfica JavaFX por defecto). Inicializa la fachada de la aplicación.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-14
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando aplicacion de gestion de gastos...\n");
        
        FachadaAplicacion fachada = FachadaAplicacion.getInstancia();

        if (args.length > 0 && args[0].equals("--cli")) {

            CommandLineInterface cli = new CommandLineInterface(fachada);
            cli.iniciar();
        } else {

            javafx.application.Application.launch(MainApp.class, args);
        }
    }
}
