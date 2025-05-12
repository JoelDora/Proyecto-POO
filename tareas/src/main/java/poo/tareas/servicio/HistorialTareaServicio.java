package poo.tareas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poo.tareas.modelo.HistorialTarea;
import poo.tareas.modelo.Tarea;
import poo.tareas.repositorio.HistorialTareaRepositorio;

import java.util.List;

@Service
public class HistorialTareaServicio implements IHistorialTareaServicio {

    @Autowired
    private HistorialTareaRepositorio historialTareaRepositorio;

    @Override
    public List<HistorialTarea> listarHistorial() {
        return historialTareaRepositorio.findAll();
    }

    @Override
    public void registrarEliminacion(Tarea tarea) {
        HistorialTarea historial = new HistorialTarea(tarea, "ELIMINACIÓN");
        historialTareaRepositorio.save(historial);
    }
    
    @Override
    public Tarea recuperarTarea(HistorialTarea historial) {
        // Crear una nueva tarea a partir del historial
        Tarea tarea = new Tarea();
        tarea.setNombreTarea(historial.getNombreTarea());
        tarea.setDescripcionTarea(historial.getDescripcionTarea());
        tarea.setResponsableTarea(historial.getResponsableTarea());
        tarea.setEstadoTarea(historial.getEstadoTarea());
        tarea.setPrioridadTarea(historial.getPrioridadTarea());
        tarea.setFechaInicioTarea(historial.getFechaInicioTarea());
        tarea.setFechaFinTarea(historial.getFechaFinTarea());
        
        // Eliminar el registro del historial
        eliminarDefinitivamente(historial);
        
        return tarea;
    }
    
    @Override
    public void eliminarDefinitivamente(HistorialTarea historial) {
        // Eliminar permanentemente el registro del historial
        historialTareaRepositorio.delete(historial);
    }
} 