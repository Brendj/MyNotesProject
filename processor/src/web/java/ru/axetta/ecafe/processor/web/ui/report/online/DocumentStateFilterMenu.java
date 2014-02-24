/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.statistics.good.request.DocumentStateFilter;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 26.06.2009
 * Time: 11:12:14
 * To change this template use File | Settings | File Templates.
 */
public class DocumentStateFilterMenu {

    private DocumentStateFilter documentStateFilter;
    private List<SelectItem> items = readAllItems();

    public DocumentStateFilterMenu() {
        this(DocumentStateFilter.FOLLOW);
    }

    public DocumentStateFilterMenu(DocumentStateFilter documentStateFilter) {
        this.documentStateFilter = documentStateFilter;
    }

    private static List<SelectItem> readAllItems() {
        DocumentStateFilter[] documentStateFilters = DocumentStateFilter.values();
        List<SelectItem> items = new ArrayList<SelectItem>(documentStateFilters.length);
        for (DocumentStateFilter documentStateFilter : documentStateFilters) {
            items.add(new SelectItem(documentStateFilter, documentStateFilter.toString()));
        }
        return items;
    }

    public DocumentStateFilter getDocumentStateFilter() {
        return documentStateFilter;
    }

    public void setDocumentStateFilter(DocumentStateFilter documentStateFilter) {
        this.documentStateFilter = documentStateFilter;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }
}
