package poo.tareas.servicio;

import poo.tareas.modelo.Tarea;

import java.util.List;

/**
 * Interfaz que define las operaciones del servicio de tareas
 * Esta interfaz sigue el patrón de diseño Service para separar la lógica de negocio
 * de la capa de persistencia y controladores.
 */
public interface ITareaServicio {
    /**
     * Método que obtiene todas las tareas almacenadas
     * @return Lista de objetos Tarea
     */
    public List<Tarea> listarTareas();
    
    /**
     * Método que busca una tarea específica por su identificador
     * @param idTarea Identificador único de la tarea a buscar
     * @return Objeto Tarea si se encuentra, null en caso contrario
     */
    public Tarea buscarTareaPorId(Integer idTarea);
    
    /**
     * Método que almacena una nueva tarea en el sistema
     * @param tarea Objeto Tarea a guardar
     */
    public void guardarTarea(Tarea tarea);
    
    /**
     * Método que elimina una tarea existente del sistema
     * @param tarea Objeto Tarea a eliminar
     */
    public void eliminarTarea(Tarea tarea);
    
    /**
     * Método que actualiza los datos de una tarea existente
     * @param tarea Objeto Tarea con los nuevos datos
     * @return Objeto Tarea actualizado
     */
    public Tarea actualizarTarea(Tarea tarea);

}
