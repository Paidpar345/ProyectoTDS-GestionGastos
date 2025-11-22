package ui.vistas;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import controlador.FachadaAplicacion;
import dominio.Categoria;
import dominio.Gasto;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Vista de calendario para visualizar gastos con CalendarFX.
 */
public class CalendarioGastosView {

    private final FachadaAplicacion fachada;
    private final CalendarView calendarView;
    private final BorderPane root;

    // Opcional: un calendario por categoría
    private final Map<String, Calendar> calendariosPorCategoria;

    public CalendarioGastosView(FachadaAplicacion fachada) {
        this.fachada = fachada;

        // Crear CalendarView principal
        this.calendarView = new CalendarView();
        this.calendarView.setShowAddCalendarButton(false);
        this.calendarView.setShowPrintButton(false);
        this.calendarView.setShowSearchField(false);
        this.calendarView.setShowSourceTray(false);

        // Calendars por categoría (o uno solo "Gastos")
        List<Categoria> categorias =
                fachada.getControladorCategorias().obtenerTodasLasCategorias();

        calendariosPorCategoria = categorias.stream()
            .collect(Collectors.toMap(
                Categoria::getNombre,
                cat -> {
                    Calendar cal = new Calendar(cat.getNombre());
                    cal.setStyle(Calendar.Style.getStyle(
                            Math.abs(cat.getNombre().hashCode()) % 8));
                    return cal;
                }
            ));

        Calendar calendarioGlobal = new Calendar("Otros");
        calendarioGlobal.setStyle(Calendar.Style.STYLE1);

        CalendarSource source = new CalendarSource("Gastos");
        source.getCalendars().addAll(calendariosPorCategoria.values());
        source.getCalendars().add(calendarioGlobal);

        calendarView.getCalendarSources().add(source);

        // Configurar fecha "hoy"
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
        calendarView.showMonthPage(); // vista mensual por defecto

        // Contenedor raíz
        this.root = new BorderPane();
        VBox wrapper = new VBox(calendarView);
        VBox.setVgrow(calendarView, Priority.ALWAYS);
        wrapper.setPadding(new Insets(10));
        root.setCenter(wrapper);

        // Cargar datos iniciales
        actualizar();
    }

    public void actualizar() {
        // Limpiar todas las entradas
        calendariosPorCategoria.values().forEach(Calendar::clear);
        // Si usas calendarioGlobal, también limpiarlo

        List<Gasto> gastos = fachada.getControladorGastos().obtenerTodosLosGastos();

        for (Gasto g : gastos) {
            Entry<Gasto> entry = new Entry<>(
                String.format("%s (%.2f €)",
                    g.getDescripcion() != null ? g.getDescripcion() : "Sin descripción",
                    g.getCantidad())
            );
            entry.setUserObject(g);

            // Solo fecha, lo tratamos como evento de día completo
            entry.setFullDay(true);
            entry.changeStartDate(g.getFecha());
            entry.changeEndDate(g.getFecha());

            String nombreCat = g.getCategoria() != null ? g.getCategoria().getNombre() : null;
            Calendar cal = nombreCat != null && calendariosPorCategoria.containsKey(nombreCat)
                    ? calendariosPorCategoria.get(nombreCat)
                    : calendarView.getCalendarSources()
                          .get(0).getCalendars().get(0); // p.ej. calendarioGlobal

            cal.addEntry(entry);
        }
    }

    public BorderPane getView() {
        return root;
    }
}
