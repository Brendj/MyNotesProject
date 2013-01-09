/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryType;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.01.13
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class QuestionaryEnumTypeMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        QuestionaryType[] questionaryTypes = QuestionaryType.values();
        SelectItem[] items = new SelectItem[questionaryTypes.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(i, questionaryTypes[i].toString());
        }
        return items;
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

}
