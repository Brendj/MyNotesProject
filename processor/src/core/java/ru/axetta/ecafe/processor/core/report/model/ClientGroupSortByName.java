package ru.axetta.ecafe.processor.core.report.model;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 18.02.15
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class ClientGroupSortByName<T extends ClientGroupSortByNameItem> extends ClientGroupSortByNameItem
        implements Comparable<T> {

    @Override
    public int compareTo(T o) {
        int stringCompareResult = ((Integer)this.getName().length()).compareTo(o.getName().length());
        if( stringCompareResult!= 0){
            return stringCompareResult;
        }

        String oOrgName = o.getName();
        String thisOrgName = this.getName();
        String numThisString = thisOrgName.replaceAll("[^\\d]", "");
        String numOString = oOrgName.replaceAll("[^\\d]", "");
        stringCompareResult = ((Integer)numThisString.length()).compareTo(numOString.length());
        if( stringCompareResult!= 0){
            return stringCompareResult;
        }

        if(numThisString.length()==0){
            return 0;
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

}
