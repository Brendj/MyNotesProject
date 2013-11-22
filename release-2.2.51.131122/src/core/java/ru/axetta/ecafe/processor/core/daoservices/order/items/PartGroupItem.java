package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.05.13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class PartGroupItem {

    private Long numCount;
    private String name;
    //private List<PartGroupItem> partGroupItem=null;
    //
    //public Integer getPartGroupItemCount() {
    //    return partGroupItem.size();
    //}
    //
    //public List<PartGroupItem> getPartGroupItem() {
    //    return partGroupItem;
    //}

    //public void setPartGroupItem(List<PartGroupItem> partGroupItem) {
    //    this.partGroupItem = partGroupItem;
    //}

    public Long getNumCount() {
        return numCount;
    }

    public void setNumCount(Long numCount) {
        this.numCount = numCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
