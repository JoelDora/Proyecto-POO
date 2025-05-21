package poo.tareas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poo.tareas.modelo.HistorialTarea;
import poo.tareas.modelo.Tarea;
import poo.tareas.repositorio.HistorialTareaRepositorio;

import java.util.List;

/**
 * Clase de servicio para gestionar el historial de tareas.
 * Implementa la interfaz IHistorialTareaServicio.
 * Contiene métodos para listar, registrar, recuperar y eliminar registros del historial.
 * 
 * La anotación @Service identifica esta clase como un componente de servicio en Spring,
 * lo que permite que sea inyectada automáticamente en otros componentes que la necesiten.
 */
@Service
public class HistorialTareaServicio implements IHistorialTareaServicio {

    /**
     * Repositorio para acceder a la base de datos y realizar operaciones CRUD 
     * sobre la entidad HistorialTarea.
     * 
     * La anotación @Autowired permite que Spring inyecte automáticamente
     * una instancia del repositorio en esta clase (inyección de dependencias).
     */
    @Autowired
    private HistorialTareaRepositorio historialTareaRepositorio;

    /**
     * Obtiene todos los registros del historial de tareas almacenados en la base de datos.
     * 
     * @return List<HistorialTarea> - Una lista con todos los registros del historial.
     */
    @Override
    public List<HistorialTarea> listarHistorial() {
        // Utiliza el método findAll() heredado de JpaRepository para obtener todos los registros
        return historialTareaRepositorio.findAll();
    }

    /**
     * Registra la eliminación de una tarea en el historial.
     * Crea un nuevo registro de historial con los datos de la tarea eliminada.
     * 
     * @param tarea - La tarea que ha sido eliminada y se quiere registrar en el historial.
     */
    @Override
    public void registrarEliminacion(Tarea tarea) {
        // Crea un nuevo objeto HistorialTarea con la tarea eliminada y el tipo de acción
        HistorialTarea historial = new HistorialTarea(tarea, "ELIMINACIÓN");
        // Guarda el registro de historial en la base de datos
        historialTareaRepositorio.save(historial);
    }
    
    /**
     * Recupera una tarea desde un registro del historial.
     * Este método crea una nueva tarea con los datos almacenados en el historial
     * y elimina el registro del historial una vez recuperada la tarea.
     * 
     * @param historial - El registro del historial desde el que se recuperará la tarea.
     * @return Tarea - La tarea recuperada del historial.
     */
    @Override
    public Tarea recuperarTarea(HistorialTarea historial) {
        // Crea una instancia vacía de Tarea
        Tarea tarea = new Tarea();
        
        // Copia todos los atributos desde el historial a la nueva tarea
        // Esto transfiere la información almacenada en el historial a un nuevo objeto Tarea
        tarea.setNombreTarea(historial.getNombreTarea());
        tarea.setDescripcionTarea(historial.getDescripcionTarea());
        tarea.setResponsableTarea(historial.getResponsableTarea());
        tarea.setEstadoTarea(historial.getEstadoTarea());
        tarea.setPrioridadTarea(historial.getPrioridadTarea());
        tarea.setFechaInicioTarea(historial.getFechaInicioTarea());
        tarea.setFechaFinTarea(historial.getFechaFinTarea());
        
        // Elimina el registro del historial 
        // Ya no es necesario mantenerlo en el historial porque la tarea ha sido recuperada
        eliminarDefinitivamente(historial);
        
        // Devuelve la nueva tarea creada
        // Esta tarea deberá ser guardada por quien llame a esta función
        return tarea;
    }
    
    /**
     * Elimina permanentemente un registro del historial de tareas.
     * Esta operación no puede deshacerse y el registro se pierde definitivamente.
     * 
     * @param historial - El registro del historial que se desea eliminar permanentemente.
     */
    @Override
    public void eliminarDefinitivamente(HistorialTarea historial) {
        // Utiliza el método delete() del repositorio para eliminar el registro
        // Esta operación elimina el registro de forma permanente de la base de datos
        historialTareaRepositorio.delete(historial);
    }
} 