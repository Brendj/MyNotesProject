package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 26.04.13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRuleMenu {
    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[50];
        for (int i=0;i<49;i++){
            items[i] = new SelectItem(0,"Комплекс "+i);
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
