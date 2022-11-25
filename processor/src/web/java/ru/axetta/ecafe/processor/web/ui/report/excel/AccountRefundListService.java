package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.persistence.AccountRefund;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AccountRefundListService extends AbstractReportService<List<AccountRefund>>{

    private final String fileName;
    private final String name;

    public AccountRefundListService(String fileName, String name) {
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
        return new String[]{"Ид. транзакции", "Время", "Сумма", "Причина", "Пользователь"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<AccountRefund> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<AccountRefund> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getTransaction().getIdOfTransaction().toString(),
                c -> dateFormat.format(data.get(c).getCreateTime()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getRefundSum()),
                c -> data.get(c).getReason(),
                c -> data.get(c).getCreatedBy().getUserName()
        );
    }
}
