/**
 * Este paquete contiene las clases principales de la aplicación de tareas.
 */
package poo.tareas;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import poo.tareas.presentacion.SistemasTareasFx;

/**
 * Clase principal de la aplicación de gestión de tareas.
 * 
 * @SpringBootApplication - Esta anotación configura automáticamente Spring Boot.
 * Combina @Configuration, @EnableAutoConfiguration y @ComponentScan.
 * 
 * @EnableScheduling - Habilita la capacidad de programar tareas en la aplicación.
 * Permite ejecutar métodos a intervalos específicos usando @Scheduled.
 */
@SpringBootApplication
@EnableScheduling
public class TareasApplication {

	/**
	 * Método principal que inicia la aplicación.
	 * En lugar de iniciar directamente SpringApplication, lanzamos la aplicación
	 * JavaFX (SistemasTareasFx) que servirá como interfaz gráfica.
	 * 
	 * @param args Argumentos de línea de comandos que se pasan a la aplicación JavaFX
	 */
	public static void main(String[] args) {
		Application.launch(SistemasTareasFx.class, args);
	}

}
