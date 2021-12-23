package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.report.SalesReport;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class SalesReportService extends AbstractReportService<List<SalesReport.SalesItem>> {
    @Override
    protected String name() {
        return "Отчет по продажам";
    }

    @Override
    protected String[] columns() {
        return new String[]{"№", "Организация", "Название", "Цена за ед",
                "Количество", "Сумма без скидки", "Сумма скидки", "Количество со скидкой",
                "Итоговая сумма", "Время первой продажи", "Время последней продажи"};
    }

    @Override
    protected String shortName() {
        return "sales.xlsx";
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<SalesReport.SalesItem> data, int currentRow) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setNumber(i + 1);
        }
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        CellStyle cs = buildTableStyle(sheet.getWorkbook());

        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(currentRow++);
            int selectedCell = 0;
            for (Function<Integer, String> columnFiller : columnFillers) {
                Cell cell = row.createCell(selectedCell++);
                cell.setCellValue(columnFiller.apply(i));
                cell.setCellStyle(cs);
            }
        }

    }

    private List<Function<Integer, String>> getColumnFillers(List<SalesReport.SalesItem> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getNumber().toString(),
                c -> data.get(c).getOfficialName(),
                c -> data.get(c).getMenuDetailName(),
                c -> data.get(c).getrPrice(),
                c -> data.get(c).getQty().toString(),
                c -> data.get(c).getSumPriceStr(),
                c -> data.get(c).getDiscountStr(),
                c -> data.get(c).getQtyDiscount().toString(),
                c -> data.get(c).getTotalStr(),
                c -> dateFormat.format(data.get(c).getFirstTimeSale()),
                c -> dateFormat.format(data.get(c).getLastTimeSale()));
    }
}