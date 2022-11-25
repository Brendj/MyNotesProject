package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.card.CardNoFormat;
import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AccountTransactionListService extends AbstractReportService<List<AccountTransaction>>{

    private final String fileName;
    private final String name;

    public AccountTransactionListService(String fileName, String name) {
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
        return new String[]{"Идентификатор", "Номер счета", "Карта", "Время транзакции", "Входящий баланс",
                "Субс. АП", "Сумма", "Субс. АП", "Ссылка", "Тип"
       };
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<AccountTransaction> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<AccountTransaction> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getIdOfTransaction().toString(),
                c -> (data.get(c).getSourceBalanceNumber()) != null ? data.get(c).getSourceBalanceNumberFormat() : data.get(c).getClient().getContractIdFormat(),
                c -> data.get(c).getCard().getCardNo() == null ? null : CardNoFormat.format(data.get(c).getCard().getCardNo()),
                c -> dateFormat.format(data.get(c).getTransactionTime()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getBalanceBeforeTransaction()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getSubBalance1BeforeTransaction()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getTransactionSum()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getTransactionSubBalance1Sum()),
                c -> data.get(c).getSource(),
                c -> data.get(c).getSourceTypeAsString()
                );
    }
}
