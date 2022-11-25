package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.core.report.ClientSmsList;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ClientSmsListService extends AbstractReportService<List<ClientSmsList.Item>>{
    private final String fileName;
    private final String name;

    public ClientSmsListService(String fileName, String name) {
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
        return new String[]{"Ид. транзакции", "Идентификатор", "Телефонный номер", "Тип содержимого",
                "Статус доставки", "Время события", "Время отправки в шлюз", "Время доставки",
                "Стоимость", "Идентификатор события"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<ClientSmsList.Item> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<ClientSmsList.Item> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> data.get(c).getIdOfTransaction().toString(),
                c -> data.get(c).getIdOfSms(),
                c -> data.get(c).getPhone(),
                c -> data.get(c).getContentsTypeAsString(),
                c -> data.get(c).getDeliveryStatusAsString(),
                c -> dateFormat.format(data.get(c).getEventTime()),
                c -> dateFormat.format(data.get(c).getServiceSendTime()),
                c -> dateFormat.format(data.get(c).getDeliveryTime()),
                c -> CurrencyStringUtils.copecksToRubles(data.get(c).getPrice()),
                c -> data.get(c).getEventId().toString()
        );
    }
}
