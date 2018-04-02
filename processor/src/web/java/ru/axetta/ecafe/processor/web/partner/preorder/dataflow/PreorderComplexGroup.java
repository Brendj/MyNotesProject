/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import java.util.ArrayList;
import java.util.List;

public class PreorderComplexGroup implements Comparable {
    private String name;
    private final List<PreorderComplexItemExt> items = new ArrayList<PreorderComplexItemExt>();

    public PreorderComplexGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PreorderComplexItemExt> getItems() {
        return items;
    }

    public void addItem(PreorderComplexItemExt item) {
        items.add(item);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof PreorderComplexGroup)) {
            return 1;
        }
        PreorderComplexGroup ext = (PreorderComplexGroup) o;
        return this.name.compareTo(ext.getName());
    }
}
