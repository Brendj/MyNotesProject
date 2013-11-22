package ru.axetta.ecafe.processor.core.daoservices.client.items;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.03.13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class ReportOnNutritionResultItem {

    private String clientInfo;
    private String groupname;
    private Long balance;
    private HashMap<Date, DayInfo> dateDayInfoHashMap = new HashMap<Date, DayInfo>(6);
    private List<Date> dates = new ArrayList<Date>(6);
    private List<Long> dayInfoType0 = new ArrayList<Long>(6);
    private List<Long> dayInfoType1 = new ArrayList<Long>(6);
    private DayInfo total;
    private Integer count=0;
    private Date createDate;

    public ReportOnNutritionResultItem(ReportOnNutritionItem reportOnNutritionItem, Date startDate, Date endDate) {
        clientInfo = reportOnNutritionItem.fullName();
        groupname = reportOnNutritionItem.getGroupName();
        balance = reportOnNutritionItem.getBalance();
        createDate = reportOnNutritionItem.getCreateTime();
        if(dateDayInfoHashMap.isEmpty()){
            Date currentDate = startDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            int position=0;
            dates.add(position,currentDate);
            dateDayInfoHashMap.put(currentDate,new DayInfo());
            do{
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                currentDate = calendar.getTime();
                position++;
                dates.add(position,currentDate);
                dateDayInfoHashMap.put(currentDate,new DayInfo());
            }while (!currentDate.equals(endDate));
        }
        total = new DayInfo();
    }

    public void addDateInfoByDate(Date date, DayInfo dayInfo){
        dateDayInfoHashMap.get(date).addDayInfo(dayInfo);
        total.addDayInfo(dayInfo);
    }

    public void checkCount(){
        for (Date date: dates){
            if(dateDayInfoHashMap.get(date).getRealPriceType1()>=0){
                count++;
            }
            dayInfoType0.add(dates.indexOf(date),dateDayInfoHashMap.get(date).getPriceType0());
            dayInfoType1.add(dates.indexOf(date),dateDayInfoHashMap.get(date).getPriceType1());
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportOnNutritionResultItem item = (ReportOnNutritionResultItem) o;

        if (clientInfo != null ? !clientInfo.equals(item.clientInfo) : item.clientInfo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return clientInfo != null ? clientInfo.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(groupname);
        sb.append(" {clientInfo='").append(clientInfo).append('\'');
        sb.append(", balance=").append(balance).append('\n');
        for (Date date: dates){
            sb.append("{");
            sb.append(date);
            sb.append("\n map:");
            sb.append(dateDayInfoHashMap.get(date).toString());
            sb.append("}").append('\n');
        }
        sb.append(", Total {");
        sb.append(total.toString());
        sb.append(" count=").append(count);
        sb.append("}");
        sb.append(", createDate=").append(createDate);
        sb.append('}');
        return sb.toString();
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public HashMap<Date, DayInfo> getDateDayInfoHashMap() {
        return dateDayInfoHashMap;
    }

    public void setDateDayInfoHashMap(HashMap<Date, DayInfo> dateDayInfoHashMap) {
        this.dateDayInfoHashMap = dateDayInfoHashMap;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public List<Long> getDayInfoType0() {
        return dayInfoType0;
    }

    public void setDayInfoType0(List<Long> dayInfoType0) {
        this.dayInfoType0 = dayInfoType0;
    }

    public List<Long> getDayInfoType1() {
        return dayInfoType1;
    }

    public void setDayInfoType1(List<Long> dayInfoType1) {
        this.dayInfoType1 = dayInfoType1;
    }

    public DayInfo getTotal() {
        return total;
    }

    public void setTotal(DayInfo total) {
        this.total = total;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}

