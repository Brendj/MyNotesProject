package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientPaymentList;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class СlientPaymentListService extends AbstractReportService<List<ClientPaymentList.Item>>{

    private final String fileName;
    private final String name;

    public СlientPaymentListService(String fileName, String name) {
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
        return new String[]{"Ид. транзакции", "Время платежа", "Контрагент", "Идентификатор платежа в системе контрагента",
                "Сумма", "Метод оплаты", "Метод оплаты (доп.)", "Идентификатор платежа (доп.)"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<ClientPaymentList.Item> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<ClientPaymentList.Item> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getIdOfTransaction().toString(),
                c -> dateFormat.format(data.get(c).getCreateTime()),
                c -> data.get(c).getContragentName(),
                c -> data.get(c).getIdOfPayment(),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getPaySum()),
                c -> data.get(c).getPaymentMethod(),
                c -> data.get(c).getAddPaymentMethod(),
                c -> data.get(c).getAddIdOfPayment()
        );
    }
}
