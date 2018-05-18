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
        int stringCompareResult = ((Integer)this.getName().replaceAll("[\\s|-]+", "").length()).compareTo(o.getName().replaceAll("[\\s|-]+", "").length());
        if( stringCompareResult!= 0){
            return stringCompareResult;
        }

        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
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
