package poo.tareas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import poo.tareas.modelo.HistorialTarea;

public interface HistorialTareaRepositorio extends JpaRepository<HistorialTarea, Integer> {
}