package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public enum OperationtypeForJournalApplication {

    DECLINE(0, "Отклонение заявление ЛП"),
    CONFIRM_DOCUMENTS(1, "Подтверждение предоставления документов"),
    CONFIRM(2, "Подтверждение заявления ЛП"),
    ARCHIVED(3, "Архивация заявления ЛП"),
    CHANGED_DATE(4, "Изменение дат заявления ЛП");

    private final Integer code;
    private final String description;


    static Map<Integer, OperationtypeForJournalApplication> map = new HashMap<Integer, OperationtypeForJournalApplication>();
    static {
        for (OperationtypeForJournalApplication questionaryStatus : OperationtypeForJournalApplication.values()) {
            map.put(questionaryStatus.getCode(), questionaryStatus);
        }
    }

    private OperationtypeForJournalApplication(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static OperationtypeForJournalApplication fromInteger(Integer value){
        return map.get(value);
    }
}
