package poo.tareas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import poo.tareas.modelo.Tarea;

public interface tareaRepositorio extends JpaRepository<Tarea, Integer> {

}
