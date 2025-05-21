package poo.tareas.servicio;

import poo.tareas.modelo.HistorialTarea;
import poo.tareas.modelo.Tarea;

import java.util.List;

/**
 * Interfaz que define los servicios relacionados con el historial de tareas.
 * Esta interfaz proporciona métodos para gestionar el ciclo de vida de las tareas eliminadas,
 * permitiendo su recuperación o eliminación definitiva del sistema.
 */
public interface IHistorialTareaServicio {
    /**
     * Obtiene la lista completa del historial de tareas eliminadas.
     * 
     * @return Lista de objetos HistorialTarea que representan el registro de tareas eliminadas
     */
    List<HistorialTarea> listarHistorial();
    
    /**
     * Registra una tarea en el historial cuando es eliminada.
     * Esta operación permite mantener un registro de las tareas eliminadas
     * para su posible recuperación posterior.
     * 
     * @param tarea La tarea que se va a registrar como eliminada
     */
    void registrarEliminacion(Tarea tarea);
    
    /**
     * Recupera una tarea previamente eliminada a partir de su registro en el historial.
     * Este método permite restaurar tareas que fueron eliminadas por error.
     * 
     * @param historial El registro de historial que contiene la información de la tarea eliminada
     * @return La tarea recuperada del historial
     */
    Tarea recuperarTarea(HistorialTarea historial);
    
    /**
     * Elimina permanentemente una tarea del historial.
     * Esta operación es irreversible y elimina completamente el registro de la tarea
     * del sistema.
     * 
     * @param historial El registro de historial que se va a eliminar definitivamente
     */
    void eliminarDefinitivamente(HistorialTarea historial);
} 