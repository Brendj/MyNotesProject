package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 * Date: 23.09.14
 * Time: 19:17
 */
public class StClass implements Comparable<StClass> {

    private String name;

    private List<ShortBuilding> shortBuildingList = new LinkedList<ShortBuilding>();
    private List<Data> dataList = new LinkedList<Data>();

    public StClass(String name,  List<ShortBuilding> shortBuildingList, List<Data> dataList) {
        this.name = name;
        this.shortBuildingList = shortBuildingList;
        this.dataList = dataList;
    }

    public StClass() {
    }

    public StClass(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(StClass o) {
        String numThisString = getName().replaceAll("[^\\d]", "");
        String numOString = o.getName().replaceAll("[^\\d]", "");
        if(numThisString.length() < 1) return -1;
        if(numOString.length() < 1) return 1;


        Integer numThis = Integer.valueOf(numThisString);
        Integer numO = Integer.valueOf(numOString);
        String sThis = getName().replaceAll("[^\\D]", "").toUpperCase();
        String sO = o.getName().replaceAll("[^\\D]", "").toUpperCase();
        if (numThis.compareTo(numO) == -1) {
            return -1;
        }else if(numThis.compareTo(numO) == 1){
            return 1;
        }

        return Character.getNumericValue(sThis.charAt(0)) > Character.getNumericValue(sO.charAt(0)) ? -1 : 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
