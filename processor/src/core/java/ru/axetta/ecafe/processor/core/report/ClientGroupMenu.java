/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 09.09.13
 * Time: 16:51
 */

public class ClientGroupMenu {

    private static final Map<String, Long> items = new HashMap<String, Long>();
    public static final Long CLIENT_STUDENTS = ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue();
    public static final Long CLIENT_DELETED = ClientGroup.Predefined.CLIENT_DELETED.getValue();
    public static final Long CLIENT_LEAVING = ClientGroup.Predefined.CLIENT_LEAVING.getValue();
    public static final Long CLIENT_ALL = -1L;

    public static Map<String, Long> getItems() {
        return items;
    }

    static {
        for (ClientGroup.Predefined predefined : ClientGroup.Predefined.values()) {
            if (!predefined.getValue().equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())) {
                items.put(predefined.getNameOfGroup(), predefined.getValue());
            }
        }
        items.put("Все учащиеся", CLIENT_STUDENTS);
        items.put("", CLIENT_ALL); // означает, выбраны все группы.
    }

    public static List<Long> getNotStudent() {
        List<Long> res = new ArrayList<Long>();
        for (Long id : items.values()) {
            if (!id.equals(CLIENT_STUDENTS) && !id.equals(CLIENT_ALL)) {
                res.add(id);
            }
        }
        return res;
    }
}
