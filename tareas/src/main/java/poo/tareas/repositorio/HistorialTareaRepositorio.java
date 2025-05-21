package poo.tareas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import poo.tareas.modelo.HistorialTarea;

/**
 * Repositorio para gestionar el historial de tareas en la base de datos.
 * 
 * Esta interfaz se extiende de JpaRepository, que es parte del framework Spring Data JPA.
 * JpaRepository proporciona métodos CRUD (Crear, Leer, Actualizar, Eliminar) predefinidos
 * para interactuar con la tabla de la base de datos asociada a la entidad HistorialTarea.
 * 
 * Los parámetros genéricos son:
 * - HistorialTarea: La entidad que este repositorio va a gestionar.
 * - Integer: El tipo de dato de la clave primaria de la entidad HistorialTarea.
 * 
 * Al extender JpaRepository, esta interfaz hereda automáticamente métodos como:
 * - save(): Para guardar una entidad
 * - findById(): Para buscar una entidad por su ID
 * - findAll(): Para obtener todas las entidades
 * - delete(): Para eliminar una entidad
 * - entre otros
 * 
 * Spring Data JPA implementará esta interfaz automáticamente en tiempo de ejecución,
 * por lo que no es necesario crear una clase que implemente los métodos.
 */
public interface HistorialTareaRepositorio extends JpaRepository<HistorialTarea, Integer> {
}