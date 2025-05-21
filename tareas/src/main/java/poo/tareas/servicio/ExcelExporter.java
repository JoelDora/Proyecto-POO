package poo.tareas.servicio;

// Importaciones necesarias para la manipulación de tablas JavaFX
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
// Importaciones para la API Apache POI que permite la manipulación de archivos Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Importaciones para manejo de archivos y excepciones
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase ExcelExporter
 * Esta clase proporciona funcionalidad para exportar datos desde un TableView de JavaFX
 * a un archivo Excel utilizando la biblioteca Apache POI.
 */
public class ExcelExporter {

    /**
     * Método para exportar datos de un TableView a un archivo Excel
     * 
     * @param tableView El TableView que contiene los datos a exportar
     * @param fileName Nombre del archivo Excel donde se guardarán los datos
     * @throws IOException Si ocurre un error durante la escritura del archivo
     */
    public static void exportTableToExcel(TableView<?> tableView, String fileName) throws IOException {
        // Crear un nuevo libro de trabajo Excel (formato .xlsx)
        Workbook workbook = new XSSFWorkbook();
        // Crear una hoja dentro del libro llamada "Tareas"
        Sheet sheet = workbook.createSheet("Tareas");

        // Crear la fila de encabezado con los nombres de las columnas
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < tableView.getColumns().size(); i++) {
            TableColumn<?, ?> column = tableView.getColumns().get(i);
            Cell cell = headerRow.createCell(i);
            // Establecer el nombre de la columna como valor de la celda
            cell.setCellValue(column.getText());
        }

        // Llenar el Excel con los datos de la tabla
        for (int i = 0; i < tableView.getItems().size(); i++) {
            // Crear una nueva fila por cada elemento en la tabla (se empieza desde la fila 1 porque la 0 es el encabezado)
            Row row = sheet.createRow(i + 1);
            // Obtener el objeto correspondiente a esta fila en el TableView
            Object rowData = tableView.getItems().get(i);

            // Recorrer cada columna para obtener los datos de la celda
            for (int j = 0; j < tableView.getColumns().size(); j++) {
                TableColumn col = tableView.getColumns().get(j);
                // Obtener el valor de la celda utilizando el CellValueFactory de la columna
                Object cellData = col.getCellObservableValue(rowData).getValue();
                // Crear la celda y convertir el dato a String (si es null, se usa una cadena vacía)
                row.createCell(j).setCellValue(cellData != null ? cellData.toString() : "");
            }
        }

        // Guardar el archivo Excel en el sistema de archivos
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            // Escribir el contenido del workbook al archivo de salida
            workbook.write(fileOut);
        }
        // Cerrar el workbook para liberar recursos
        workbook.close();
    }
}
