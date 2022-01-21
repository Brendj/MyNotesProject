package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.report.AllComplexReport;
import ru.axetta.ecafe.processor.core.report.ClientReport;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ClientReportService extends AbstractReportService<List<ClientReport.ClientItem>> {


    @Override
    protected String fileName() {
        return "client_org.xlsx";
    }

    @Override
    protected String name() {
        return "Статистика по балансам клиентов";
    }

    @Override
    protected String[] columns() {
        return new String[]{"Номер учреждения", "Название учреждения", "Количество учащихся",
                "Количество учащихся (balance больше 0)", "Количество учащихся (balance равно 0)",
                "Количество учащихся (balance меньше 0)", "Сумма денег",
                "Сумма положительных балансов", "Сумма отрицательных балансов"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<ClientReport.ClientItem> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<ClientReport.ClientItem> data) {
        return Arrays.asList(
                c -> data.get(c).getIdOfOrg().toString(),
                c -> data.get(c).getOfficialName(),
                c -> data.get(c).getClientCount().toString(),
                c -> data.get(c).getClientWithPositiveBalanceCount().toString(),
                c -> data.get(c).getClientWithNullBalanceCount().toString(),
                c -> data.get(c).getClientWithNegativeBalanceCount().toString(),
                c -> data.get(c).getBalanceSum(),
                c -> data.get(c).getPosBalanceSum(),
                c -> data.get(c).getNegBalanceSum());
    }
}
