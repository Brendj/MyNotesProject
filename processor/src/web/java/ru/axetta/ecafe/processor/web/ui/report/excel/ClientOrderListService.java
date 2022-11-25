package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.card.CardNoFormat;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientOrderList;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ClientOrderListService extends AbstractReportService<List<ClientOrderList.Item>>{
    private final String fileName;
    private final String name;

    public ClientOrderListService(String fileName, String name) {
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
        return new String[]{"Ид. транзакции", "Организация", "Ид. заказа", "Карта",
                "Время покупки", "Время транзакции", "Время пробития", "Сумма", "Социальная скидка",
                "Скидка поставщика", "Дотация", "Состав"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<ClientOrderList.Item> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<ClientOrderList.Item> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getIdOfTransaction().toString(),
                c -> data.get(c).getOrg().getShortName(),
                c -> data.get(c).getIdOfOrder().toString(),
                c -> CardNoFormat.format(data.get(c).getCardNo()),
                c -> dateFormat.format(data.get(c).getCreateTime()),
                c -> dateFormat.format(data.get(c).getTransactionTime()),
                c -> dateFormat.format(data.get(c).getOrderDate()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getRSum()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getSocDiscount()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getTradeDiscount()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getGrantSum()),
                c -> data.get(c).listDetailsAtString() + data.get(c).getState()
        );
    }
}
