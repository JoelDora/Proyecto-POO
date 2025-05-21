package poo.tareas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import poo.tareas.modelo.Tarea;

/**
 * Interfaz tareaRepositorio
 * 
 * Esta interfaz define el repositorio para la entidad Tarea en esta aplicación.
 * Extiende JpaRepository para heredar métodos CRUD básicos (Create, Read, Update, Delete)
 * y otras operaciones relacionadas con la persistencia.
 * 
 * Al extender JpaRepository<Tarea, Integer>:
 * - El primer parámetro (Tarea) indica la entidad con la que trabaja este repositorio
 * - El segundo parámetro (Integer) especifica el tipo de dato del identificador (ID) de la entidad
 * 
 * Spring Data JPA se encargará de implementar automáticamente esta interfaz,
 * proporcionando métodos como:
 * - save(Tarea tarea): para guardar una tarea
 * - findById(Integer id): para buscar una tarea por su id
 * - findAll(): para obtener todas las tareas
 * - delete(Tarea tarea): para eliminar una tarea
 * - y otros métodos útiles
 * 
 * No se necesita implementar manualmente estos métodos ya que Spring los
 * proporciona automáticamente al momento de la ejecución.
 */
public interface tareaRepositorio extends JpaRepository<Tarea, Integer> {
}
