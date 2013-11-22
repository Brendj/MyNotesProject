package ru.axetta.ecafe.processor.core.daoservices.client.items;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.03.13
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class ReportOnNutritionItem {

    private static Date truncate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    private Integer menuType;
    private String  surname;
    private String  firstName;
    private String  secondName;
    private Date createTime;
    private String groupName;
    private Long price;
    private Long balance;

    public String fullName(){
        final StringBuilder sb = new StringBuilder();
        sb.append(surname).append(' ');
        sb.append(firstName).append(' ');
        sb.append(secondName);
        return sb.toString();
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = truncate(createTime);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ReportOnNutritionItem");
        sb.append("{menuType=").append(menuType);
        sb.append(", surname='").append(surname).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", secondName='").append(secondName).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", price=").append(price);
        sb.append(", balance=").append(balance);
        sb.append('}');
        return sb.toString();
    }
}
