package poo.tareas.servicio;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter {

    public static void exportTableToExcel(TableView<?> tableView, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tareas");

        // Crear fila de encabezado
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < tableView.getColumns().size(); i++) {
            TableColumn<?, ?> column = tableView.getColumns().get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(column.getText());
        }

        // Llenar datos
        for (int i = 0; i < tableView.getItems().size(); i++) {
            Row row = sheet.createRow(i + 1);
            Object rowData = tableView.getItems().get(i);

            for (int j = 0; j < tableView.getColumns().size(); j++) {
                TableColumn col = tableView.getColumns().get(j);
                Object cellData = col.getCellObservableValue(rowData).getValue();
                row.createCell(j).setCellValue(cellData != null ? cellData.toString() : "");
            }
        }

        // Guardar el archivo
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
