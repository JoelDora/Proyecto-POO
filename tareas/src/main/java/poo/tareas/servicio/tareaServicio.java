package poo.tareas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poo.tareas.modelo.Tarea;
import poo.tareas.repositorio.tareaRepositorio;

import java.util.List;

/**
 * Clase de servicio que implementa la interfaz ITareaServicio
 * Esta capa actúa como intermediaria entre el controlador y el repositorio,
 * manejando la lógica de negocio relacionada con las tareas.
 */
@Service // Anotación que indica a Spring que esta clase es un componente de servicio
public class tareaServicio implements ITareaServicio {

    /**
     * Inyección de dependencia del repositorio de tareas.
     * @Autowired permite que Spring inyecte automáticamente la implementación
     * del repositorio en esta clase.
     */
    @Autowired
    private tareaRepositorio TareaRepositorio;

    /**
     * Método que obtiene todas las tareas almacenadas en la base de datos.
     * @return Lista con todas las tareas encontradas
     */
    @Override
    public List<Tarea> listarTareas() {
        return TareaRepositorio.findAll(); // Utiliza el método findAll() de JpaRepository
    }

    /**
     * Busca una tarea específica por su ID
     * @param idTarea Identificador único de la tarea
     * @return La tarea encontrada o null si no existe
     */
    @Override
    public Tarea buscarTareaPorId(Integer idTarea) {
        Tarea tarea = TareaRepositorio.findById(idTarea).orElse(null);
        // findById devuelve un Optional, si no encuentra la tarea retorna null
        return tarea;
    }

    /**
     * Guarda una nueva tarea en la base de datos
     * @param tarea Objeto Tarea que se quiere persistir
     */
    @Override
    public void guardarTarea(Tarea tarea) {
        TareaRepositorio.save(tarea); // Utiliza el método save() de JpaRepository
    }

    /**
     * Elimina una tarea existente de la base de datos
     * @param tarea Objeto Tarea que se desea eliminar
     */
    @Override
    public void eliminarTarea(Tarea tarea) {
        TareaRepositorio.delete(tarea); // Utiliza el método delete() de JpaRepository
    }

    /**
     * Actualiza los datos de una tarea existente
     * @param tarea Objeto Tarea con los datos actualizados
     * @return El objeto Tarea actualizado
     */
    @Override
    public Tarea actualizarTarea(Tarea tarea) {
        return TareaRepositorio.save(tarea); 
        // El método save() de JPA también sirve para actualizar si el ID ya existe
    }
}
