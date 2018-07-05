/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryType;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.01.13
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class SettingsIdEnumTypeMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SettingsIds[] settingsIdses = SettingsIds.values();
        SelectItem[] items = new SelectItem[settingsIdses.length-1];
        int j = 0;
        for (int i = 0; i < settingsIdses.length; ++i) {
            if(i == 5) continue;
            items[j] = new SelectItem(settingsIdses[i].getId(), settingsIdses[i].toString());
            j++;
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
