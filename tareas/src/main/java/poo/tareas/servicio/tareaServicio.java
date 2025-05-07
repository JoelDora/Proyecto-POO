package poo.tareas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poo.tareas.modelo.Tarea;
import poo.tareas.repositorio.tareaRepositorio;

import java.util.List;

@Service
public class tareaServicio implements ITareaServicio {

    @Autowired
    private tareaRepositorio TareaRepositorio;

    @Override
    public List<Tarea> listarTareas() {
        return TareaRepositorio.findAll();
    }

    @Override
    public Tarea buscarTareaPorId(Integer idTarea) {
        Tarea tarea = TareaRepositorio.findById(idTarea).orElse(null);
        return tarea;
    }

    @Override
    public void guardarTarea(Tarea tarea) {
        TareaRepositorio.save(tarea);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {
        TareaRepositorio.delete(tarea);

    }

    @Override
    public Tarea actualizarTarea(Tarea tarea) {
        return TareaRepositorio.save(tarea);
    }
}
