package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.report.PayComplexReport;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class PayComplexReportService extends AbstractReportService<List<PayComplexReport.ComplexItem>> {
    @Override
    protected String fileName() {
        return "pay_complexes.xlsx";
    }

    @Override
    protected String name() {
        return "Отчет по платным комплексам";
    }

    @Override
    protected String[] columns() {
        return new String[]{"Организация", "Название", "Цена за ед", "Скидка на ед", "Кол-во",
                "Сумма без скидки", "Сумма скидки", "Итоговая сумма", "Время первой продажи", "Время последней продажи"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<PayComplexReport.ComplexItem> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<PayComplexReport.ComplexItem> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getOfficialName(),
                c -> data.get(c).getMenuDetailName(),
                c -> data.get(c).getrPrice(),
                c -> data.get(c).getDiscount(),
                c -> data.get(c).getQty().toString(),
                c -> data.get(c).getSumPrice(),
                c -> data.get(c).getSumPriceDiscount(),
                c -> data.get(c).getTotal(),
                c -> dateFormat.format(data.get(c).getFirstTimeSale()),
                c -> dateFormat.format(data.get(c).getLastTimeSale()));
    }
}
