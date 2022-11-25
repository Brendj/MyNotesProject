package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import ru.axetta.ecafe.processor.web.ui.client.items.ClientPassItem;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ClientPassesListService extends AbstractReportService<List<ClientPassItem>>{
    private final String fileName;
    private final String name;

    public ClientPassesListService(String fileName, String name) {
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
        return new String[]{"№", "ID ОО", "Название ОО", "Адрес", "Наименование события", "Дата и время",
                    "Тип карты", "Направление", "Кто отметил | Группа | Л/с"};
    }

    @Override
    protected void buildReportBody(Sheet sheet, List<ClientPassItem> data, int currentRow) {
        List<Function<Integer, String>> columnFillers = getColumnFillers(data);
        printReportBody(sheet, currentRow, columnFillers, data.size());
    }

    private List<Function<Integer, String>> getColumnFillers(List<ClientPassItem> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return Arrays.asList(
                c -> String.valueOf(c + 1),
                c -> data.get(c).getIdOfOrg().toString(),
                c -> data.get(c).getOrgName(),
                c -> data.get(c).getShortAddress(),
                c -> data.get(c).getEnterName(),
                c -> dateFormat.format(data.get(c).getEnterTime()),
                c -> data.get(c).getCardType(),
                c -> data.get(c).getDirection(),
                c -> data.get(c).getChekerItemListAtString()
        );
    }
}
