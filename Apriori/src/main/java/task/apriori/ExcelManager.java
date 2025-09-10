package task.apriori;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelManager {

    private static final String filePath = "src/main/resources/task/apriori/result/transactions.xlsx";

    public static boolean saveToExcel(List<Map<String, Double>> transactions) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");
        boolean isSavedSuccessful = false;
        int rowNum = 1;
        String[] headers =  {
                "Transaction", "Product", "Price"
        };

        createHeaderRow(sheet, headers);

        int transactionNumber = 1;
        for (Map<String, Double> transaction : transactions) {
            for (Map.Entry<String, Double> entry : transaction.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transactionNumber);
                row.createCell(1).setCellValue(entry.getKey());
                row.createCell(2).setCellValue(entry.getValue());
            }
            transactionNumber++;
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
            isSavedSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isSavedSuccessful;
    }

    public static List<Transaction> readFromExcel() throws IOException {
        List<Transaction> dataPoints = new ArrayList<>();
        FileInputStream file = null;
        Workbook workbook = null;

        try {
            file = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell transactionIdCell = row.getCell(0);
                Cell productNameCell = row.getCell(1);

                if (transactionIdCell != null && productNameCell != null) {
                    if (transactionIdCell.getCellType() == CellType.NUMERIC && productNameCell.getCellType() == CellType.STRING) {
                        int transactionId = (int) transactionIdCell.getNumericCellValue();
                        String productName = productNameCell.getStringCellValue();
                        dataPoints.add(new Transaction(transactionId, productName));
                    }
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


    private static void createHeaderRow(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        Workbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }
}
