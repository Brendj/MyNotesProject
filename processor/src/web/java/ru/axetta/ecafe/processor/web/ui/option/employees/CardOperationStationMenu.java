/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.persistence.CardOperationStation;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.08.13
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class CardOperationStationMenu {

    private List<SelectItem> items = readAllItems();

    private static List<SelectItem> readAllItems() {
        CardOperationStation[] cardOperationStations = CardOperationStation.values();
        List<SelectItem> items = new ArrayList<SelectItem>(cardOperationStations.length);
        for (CardOperationStation cardOperationStation: cardOperationStations){
            items.add(new SelectItem(cardOperationStation, cardOperationStation.toString()));
        }
        return items;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }
}
