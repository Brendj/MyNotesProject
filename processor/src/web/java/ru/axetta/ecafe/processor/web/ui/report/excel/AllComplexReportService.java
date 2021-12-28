package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.report.AllComplexReport;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AllComplexReportService extends AbstractReportService<List<AllComplexReport.ComplexItem>> {

    private final String fileName;
    private final String name;

    public AllComplexReportService(String fileName, String name) {
        this.fileName = fileName;
        this.name = name;
    }

    @Override
    protected String fileName() {
        return fileName;
    }

    @Override
    protected String name() {
        return name;
    }

    @Override
    protected String[] columns() {
        return new String[]{"Организация", "Название", "Цена за ед", "Скидка на ед", "Кол-во",
                "Сумма без скидки", "Сумма скидки", "Итоговая сумма", "Кол-во",
                "Сумма без скидки", "Сумма скидки", "Итоговая сумма",
                "Время первой продажи", "Время последней продажи"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<AllComplexReport.ComplexItem> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<AllComplexReport.ComplexItem> data) {
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
                c -> data.get(c).getQtyTemp().toString(),
                c -> data.get(c).getSumPriceTemp(),
                c -> data.get(c).getSumPriceDiscountTemp(),
                c -> data.get(c).getTotalTemp(),
                c -> dateFormat.format(data.get(c).getFirstTimeSale()),
                c -> dateFormat.format(data.get(c).getLastTimeSale()));
    }

}
