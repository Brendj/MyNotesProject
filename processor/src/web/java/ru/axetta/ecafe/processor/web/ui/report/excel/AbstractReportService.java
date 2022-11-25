package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
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

    public static void mergeExcels(Workbook sourceExcel, Workbook destExcel) {
        for(int sheetIndex = 0; sheetIndex < sourceExcel.getNumberOfSheets(); ++sheetIndex) {
            Sheet sheet = sourceExcel.getSheetAt(sheetIndex);
            if (destExcel.getSheet(sheet.getSheetName()) != null) {
                destExcel.removeSheetAt(destExcel.getSheetIndex(sheet.getSheetName()));
            }

            Sheet outputSheet = destExcel.createSheet(sheet.getSheetName());
            copySheets(outputSheet, sheet, true);
        }

    }

    public static void copySheets(Sheet newSheet, Sheet sheet, boolean copyStyle) {
        int maxColumnNum = 0;
        Map<Integer, CellStyle> styleMap = copyStyle ? new HashMap() : null;

        int i;
        List<CellRangeAddress> mergedRegions = new ArrayList<CellRangeAddress>();
        for(i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); ++i) {
            Row srcRow = sheet.getRow(i);
            Row destRow = newSheet.createRow(i);
            if (srcRow != null) {
                copyRow(sheet, newSheet, srcRow, destRow, styleMap, mergedRegions);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }

        for(i = 0; i <= maxColumnNum; ++i) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }

    }

    public static void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow, Map<Integer, CellStyle> styleMap, List<CellRangeAddress> mergedRegions ) {
        destRow.setHeight(srcRow.getHeight());

        for(int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); ++j) {
            if (j >= 0) {
                Cell oldCell = srcRow.getCell(j);
                Cell newCell = destRow.getCell(j);
                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = destRow.createCell(j);
                    }

                    copyCell(oldCell, newCell, styleMap);
                    CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), (short)oldCell.getColumnIndex());
                    if (mergedRegion != null) {
                        CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(),  mergedRegion.getLastRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());
                        //if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
                            mergedRegions.add(newMergedRegion);
                            destSheet.addMergedRegion(newMergedRegion);
                        //}
                    }
                }
            }
        }

    }

    public static void copyCell(Cell oldCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
        if (styleMap != null) {
            if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
                newCell.setCellStyle(oldCell.getCellStyle());
            } else {
                int stHashCode = oldCell.getCellStyle().hashCode();
                CellStyle newCellStyle = (CellStyle)styleMap.get(stHashCode);
                if (newCellStyle == null) {
                    newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                    styleMap.put(stHashCode, newCellStyle);
                }

                newCell.setCellStyle(newCellStyle);
            }
        }

        switch(oldCell.getCellType()) {
            case 0:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case 1:
                newCell.setCellValue(oldCell.getStringCellValue());
                break;
            case 2:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            case 3:
                newCell.setCellType(3);
                break;
            case 4:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case 5:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
        }

    }

    public static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, short cellNum) {
        for(int i = 0; i < sheet.getNumMergedRegions(); ++i) {
            CellRangeAddress merged = sheet.getMergedRegion(i);
            if (merged.isInRange(rowNum, cellNum)) {
                return merged;
            }
        }

        return null;
    }
}
