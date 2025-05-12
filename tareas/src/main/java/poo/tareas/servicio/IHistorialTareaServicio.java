package poo.tareas.servicio;

import poo.tareas.modelo.HistorialTarea;
import poo.tareas.modelo.Tarea;

import java.util.List;

public interface IHistorialTareaServicio {
    List<HistorialTarea> listarHistorial();
    void registrarEliminacion(Tarea tarea);
    Tarea recuperarTarea(HistorialTarea historial);
    void eliminarDefinitivamente(HistorialTarea historial);
} 