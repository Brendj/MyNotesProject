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
        if(this.getName().length() < o.getName().length()) {
            return -1;
        }else if(this.getName().length() < o.getName().length()) {
            return 1;
        }

        String oOrgName = o.name;
        String thisOrgName = this.name;
        String numThisString = thisOrgName.replaceAll("[^\\d]", "");
        String numOString = oOrgName.replaceAll("[^\\d]", "");
        int stringCompareResult = ((Integer)numThisString.length()).compareTo(numOString.length());
        if( stringCompareResult!= 0){
            return stringCompareResult;
        }

        Integer numThis = Integer.valueOf(numThisString);
        Integer numO = Integer.valueOf(numOString);
        if(numThis.equals(numO)){
            String sThis = thisOrgName.replaceAll("[^\\D]", "").toUpperCase();
            String sO = oOrgName.replaceAll("[^\\D]", "").toUpperCase();
            Integer letterThis = printSum(sThis);
            Integer letterO = printSum(sO);
            return  letterThis.compareTo(letterO);
        }else{
            return numThis.compareTo(numO);
        }
    }

    private static int printSum(String original){
        int sum = 0;
        if(original!=null){
            char[] arr = original.toLowerCase().toCharArray();
            for(int x :arr){
                sum+= (x-96);
            }
        }
        return sum;
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
