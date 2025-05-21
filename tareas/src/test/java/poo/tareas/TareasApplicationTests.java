package poo.tareas;

// Importación de las clases necesarias para realizar pruebas unitarias
import org.junit.jupiter.api.Test;   // Permite usar la anotación @Test para marcar métodos como pruebas
import org.springframework.boot.test.context.SpringBootTest;   // Proporciona la configuración para pruebas de Spring Boot

/**
 * Clase de prueba para la aplicación de tareas
 * 
 * Esta clase verifica que el contexto de Spring se carga correctamente.
 * La anotación @SpringBootTest inicializa el contexto completo de la aplicación
 * durante las pruebas, simulando el comportamiento real de la aplicación.
 */
@SpringBootTest  // Carga y configura el contexto completo de Spring Boot para pruebas
class TareasApplicationTests {

	/**
	 * Prueba que verifica la carga correcta del contexto de la aplicación
	 * 
	 * Esta prueba no contiene aserciones explícitas, pero falla si ocurre
	 * algún problema durante la inicialización del contexto de Spring.
	 * Es una forma de asegurar que todos los componentes de la aplicación
	 * se configuran y conectan correctamente.
	 */
	@Test
	void contextLoads() {
		// No se necesita implementación adicional
		// Spring verifica automáticamente que el contexto cargue correctamente
	}

}
