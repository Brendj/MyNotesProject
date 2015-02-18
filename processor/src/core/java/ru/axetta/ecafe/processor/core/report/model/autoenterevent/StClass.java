package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

import ru.axetta.ecafe.processor.core.report.model.ClientGroupSortByName;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 * Date: 23.09.14
 * Time: 19:17
 */
public class StClass extends ClientGroupSortByName {

    private List<ShortBuilding> shortBuildingList = new LinkedList<ShortBuilding>();
    private List<Data> dataList = new LinkedList<Data>();

    public StClass(String name, List<ShortBuilding> shortBuildingList, List<Data> dataList) {
        this.setName(name);
        this.shortBuildingList = shortBuildingList;
        this.dataList = dataList;
    }

    public StClass() {
    }

    public StClass(String name) {
        this.setName(name);
    }


    public List<ShortBuilding> getShortBuildingList() {
        return shortBuildingList;
    }

    public void setShortBuildingList(List<ShortBuilding> shortBuildingList) {
        this.shortBuildingList = shortBuildingList;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }
}
