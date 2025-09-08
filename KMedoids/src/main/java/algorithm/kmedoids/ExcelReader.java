package algorithm.kmedoids;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
    public static List<double[]> readAllDataFromFile(String filePath) throws IOException {
        List<double[]> dataPoints = new ArrayList<>();
        FileInputStream file = null;
        Workbook workbook = null;

        try {
            file = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                List<Double> numericValues = new ArrayList<>();

                for (Cell cell : row) {
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        numericValues.add(cell.getNumericCellValue());
                    }
                }

                if (!numericValues.isEmpty()) {
                    double[] data = new double[numericValues.size()];
                    for (int i = 0; i < numericValues.size(); i++) {
                        data[i] = numericValues.get(i);
                    }
                    dataPoints.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (file != null) {
                file.close();
            }
        }

        return dataPoints;
    }
}
