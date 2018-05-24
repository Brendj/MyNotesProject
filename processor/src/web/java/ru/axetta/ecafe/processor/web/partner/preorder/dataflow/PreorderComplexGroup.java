/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderComplexGroup")
public class PreorderComplexGroup implements Comparable {
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "complexItem")
    private List<PreorderComplexItemExt> items;

    public PreorderComplexGroup() {
        items = new ArrayList<PreorderComplexItemExt>();
    }

    public PreorderComplexGroup(String name) {
        this.name = name;
        items = new ArrayList<PreorderComplexItemExt>();
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
