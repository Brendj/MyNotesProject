package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import ru.axetta.ecafe.processor.core.report.ClientReport;
import ru.axetta.ecafe.processor.core.report.TotalServicesReport;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TotalServicesReportService extends AbstractReportService<List<TotalServicesReport.TotalEntry>> {

    @Override
    protected String fileName() {
        return "total_services.xlsx";
    }

    @Override
    protected String name() {
        return "Свод по услугам";
    }

    @Override
    protected String[] columns() {
        return new String[]{"Организация", "Число учащихся", "Число льготников", "", "Зафиксирован проход", "",
                "Получили льготное питание всего", "", "Получили комплексное питание", "", "Получили питание в буфете",
                "", "Получили питание (льготное + платное)", ""};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<TotalServicesReport.TotalEntry> data, int currentRow) {

        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        addMergeRegion(sheet, new int[]{2, 4, 6, 8, 10, 12});
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private void addMergeRegion(Sheet sheet, int[] region) {
        for (int i : region) {
            sheet.addMergedRegion(new CellRangeAddress(1, 1, i, i + 1));
        }
    }

    private List<Function<Integer, String>> getColumnFillers(List<TotalServicesReport.TotalEntry> data) {
        return Arrays.asList(
                c -> data.get(c).getShortName(),
                c -> data.get(c).getTotalClientsCount().toString(),
                c -> data.get(c).getPlanBenefitClientsCount().toString(),
                c -> data.get(c).getPer_planBenefitClientsCount(),
                c -> data.get(c).getCurrentClientsCount().toString(),
                c -> data.get(c).getPer_currentClientsCount(),
                c -> data.get(c).getRealBenefitClientsCount().toString(),
                c -> data.get(c).getPer_realBenefitClientsCount(),
                c -> data.get(c).getRealPaidClientsCount().toString(),
                c -> data.get(c).getPer_realPaidClientsCount(),
                c -> data.get(c).getRealSnackPaidClientsCount().toString(),
                c -> data.get(c).getPer_realSnackPaidClientsCount(),
                c -> data.get(c).getUniqueClientsCount().toString(),
                c -> data.get(c).getPer_uniqueClientsCount());
    }
}
