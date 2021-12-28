package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractReportService<T> {

    protected abstract String fileName();

    protected abstract String name();

    protected abstract String[] columns();

    protected abstract void buildReportBody(Sheet sheet, T data, int currentRow);

    public Workbook buildReport(T data) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(fileName());
        int currentRow;
        currentRow = this.buildReportHeader(sheet);
        currentRow = this.buildReportColumns(sheet, currentRow);
        this.buildReportBody(sheet, data, currentRow);
        this.setAutoSizeColumns(sheet);
        return wb;
    }

    protected int buildReportHeader(Sheet sheet) {
        CellStyle cs = buildBoldStyle(sheet.getWorkbook(), true);
        String[] rows = {
                name()
        };
        int currentRow = 0;
        for (; currentRow < rows.length; currentRow += 1) {
            Cell cell = sheet.createRow(currentRow).createCell(0);
            cell.setCellValue(rows[currentRow]);
            cell.setCellStyle(cs);
        }
        return currentRow;
    }

    private int buildReportColumns(Sheet sheet, int currentRow) {
        CellStyle cs = buildBoldStyle(sheet.getWorkbook(), false);
        String[] columns = columns();
        for (int i = 0; i < currentRow; i += 1) {
            sheet.addMergedRegion(new CellRangeAddress(i, i, 0, columns.length - 1));
        }
        if (Arrays.stream(columns).anyMatch(Objects::nonNull)) {
            Row row = sheet.createRow(currentRow++);
            for (int i = 0; i < columns.length; i += 1) {
                Cell cell = row.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(cs);
            }
        }
        return currentRow;
    }

    protected CellStyle buildBoldStyle(Workbook wb, boolean isCenter) {
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle cs = wb.createCellStyle();
        cs.setFont(font);
        cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cs.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
        if (isCenter)
            cs.setAlignment(CellStyle.ALIGN_CENTER);
        return cs;
    }

    protected CellStyle buildTableStyle(Workbook wb) {
        Font font = wb.createFont();
        CellStyle cs = wb.createCellStyle();
        cs.setFont(font);
        cs.setAlignment(CellStyle.ALIGN_CENTER);
        cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cs.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
        return cs;
    }

    protected void setAutoSizeColumns(Sheet sheet) {
        int columnsSize = columns().length;
        for (int i = 0; i < columnsSize; i += 1) {
            sheet.autoSizeColumn(i, true);
        }
    }

    protected void printReportBody(Sheet sheet, int currentRow, List<Function<Integer, String>> columnFillers, int size) {
        CellStyle cs = buildTableStyle(sheet.getWorkbook());

        for (int i = 0; i < size; i++) {
            Row row = sheet.createRow(currentRow++);
            int selectedCell = 0;
            for (Function<Integer, String> columnFiller : columnFillers) {
                Cell cell = row.createCell(selectedCell++);
                try {
                    cell.setCellValue(columnFiller.apply(i));
                } catch (NullPointerException ignored) {
                    cell.setCellValue("");
                }
                cell.setCellStyle(cs);
            }
        }
    }
}
