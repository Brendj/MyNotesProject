package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.persistence.AccountTransfer;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AccountTransferListService extends AbstractReportService<List<AccountTransfer>>{

    private final String fileName;
    private final String name;

    public AccountTransferListService(String fileName, String name) {
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
        return new String[]{"Ид. транзакции списания", "Ид. транзакции зачисления", "Время перевода", "Плательщик",
                "Получатель", "Сумма", "Причина", "Пользователь"
        };
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<AccountTransfer> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<AccountTransfer> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getTransactionOnBenefactor().getIdOfTransaction().toString(),
                c -> data.get(c).getTransactionOnBeneficiary().getIdOfTransaction().toString(),
                c -> dateFormat.format(data.get(c).getCreateTime()),
                c -> data.get(c).getClientBenefactor().getContractId().toString() + " (" +
                        data.get(c).getClientBenefactor().getPerson().getFullName() + ")",
                c -> data.get(c).getClientBeneficiary().getContractId().toString() + " (" +
                        data.get(c).getClientBeneficiary().getPerson().getFullName() + ")",
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getTransferSum()),
                c -> data.get(c).getReason(),
                c -> data.get(c).getCreatedBy().getUserName()
        );
    }
}
