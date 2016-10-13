/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.employees;

import ru.axetta.ecafe.processor.core.persistence.Visitor;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.08.13
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class VisitorItem {

    private Long idOfVisitor;
    private String firstName;
    private String surname;
    private String secondName;
    private String passportNumber;
    private Date passportDate;
    private String driverLicenceNumber;
    private Date driverLicenceDate;
    private String warTicketNumber;
    private Date warTicketDate;
    private String position;
    private boolean deleted;
    private List<CardItem> cardItems = new ArrayList<CardItem>();

    private Date operationDate;
    private List<CardEventOperationItem> operationItemList = new ArrayList<CardEventOperationItem>();

    private String freeDocName;
    private String freeDocNumber;
    private Date freeDocDate;

    public VisitorItem(VisitorItem item) {
        this.idOfVisitor = item.getIdOfVisitor();
        this.firstName = item.getFirstName();
        this.surname = item.getSurname();
        this.secondName = item.getSecondName();
        this.passportNumber = item.getPassportNumber();
        this.passportDate = item.getPassportDate();
        this.driverLicenceNumber = item.getDriverLicenceNumber();
        this.driverLicenceDate = item.getDriverLicenceDate();
        this.warTicketNumber = item.getWarTicketNumber();
        this.position = item.getPosition();
        this.warTicketDate = item.getWarTicketDate();
        this.operationDate = item.getOperationDate();
        this.deleted = item.isDeleted();
        this.freeDocName = item.getFreeDocName();
        this.freeDocNumber = item.getFreeDocNumber();
        this.freeDocDate = item.getFreeDocDate();
    }

    public VisitorItem() {
        firstName="";
        surname="";
        secondName="";
        passportNumber="";
        driverLicenceNumber="";
        warTicketNumber="";
        position="";
        freeDocName="";
        freeDocNumber="";
    }

    public VisitorItem(Visitor visitor) {
        this.idOfVisitor = visitor.getIdOfVisitor();
        this.firstName = visitor.getPerson().getFirstName();
        this.surname = visitor.getPerson().getSurname();
        this.secondName = visitor.getPerson().getSecondName();
        this.passportNumber = visitor.getPassportNumber();
        this.passportDate = visitor.getPassportDate();
        this.driverLicenceNumber = visitor.getDriverLicenceNumber();
        this.driverLicenceDate = visitor.getDriverLicenceDate();
        this.warTicketNumber = visitor.getWarTicketNumber();
        this.warTicketDate = visitor.getWarTicketDate();
        this.position = visitor.getPosition();
        this.deleted = visitor.isDeleted();
        this.freeDocName = visitor.getFreeDocName();
        this.freeDocNumber = visitor.getFreeDocNumber();
        this.freeDocDate = visitor.getFreeDocDate();
    }

    public String getFullName(){
        return surname+" "+firstName + " " + secondName;
    }

    public String getShortFullName(){
        StringBuilder fullName = new StringBuilder(surname);
        if(StringUtils.isNotEmpty(firstName)) {
            fullName.append(" ");
            fullName.append(firstName.trim().substring(0,1));
            fullName.append(".");
        }
        if(StringUtils.isNotEmpty(secondName)) {
            fullName.append(" ");
            fullName.append(secondName.trim().substring(0,1));
            fullName.append(".");
        }
        return fullName.toString();
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public List<CardEventOperationItem> getOperationItemList() {
        return operationItemList;
    }

    public Integer getOperationItemListCount() {
        return operationItemList.size()+1;
    }

    public void addOperationItem(List<CardEventOperationItem> cardEventOperationItem){
        operationItemList.addAll(cardEventOperationItem);
    }

    public String getFreeDocName() {
        return freeDocName;
    }

    public void setFreeDocName(String freeDocName) {
        this.freeDocName = freeDocName;
    }

    public String getFreeDocNumber() {
        return freeDocNumber;
    }

    public void setFreeDocNumber(String freeDocNumber) {
        this.freeDocNumber = freeDocNumber;
    }

    public Date getFreeDocDate() {
        return freeDocDate;
    }

    public void setFreeDocDate(Date freeDocDate) {
        this.freeDocDate = freeDocDate;
    }

    public List<CardItem> getCardItems() {
        return cardItems;
    }

    public void clearCardItems() {
        cardItems.clear();
    }

    public void addCard(CardItem cardItem){
        cardItems.add(cardItem);
    }

    public void addCard(List<CardItem> cardItem){
        cardItems.addAll(cardItem);
    }

    public void removeCard(CardItem cardItem){
        cardItems.remove(cardItem);
    }

    public Integer getCountCardItems() {
        return cardItems.size();
    }

    public void removeCard(List<CardItem> cardItem){
        cardItems.removeAll(cardItem);
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public Date getPassportDate() {
        return passportDate;
    }

    public String getDriverLicenceNumber() {
        return driverLicenceNumber;
    }

    public Date getDriverLicenceDate() {
        return driverLicenceDate;
    }

    public String getWarTicketNumber() {
        return warTicketNumber;
    }

    public Date getWarTicketDate() {
        return warTicketDate;
    }

    public void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPassportDate(Date passportDate) {
        this.passportDate = passportDate;
    }

    public void setDriverLicenceNumber(String driverLicenceNumber) {
        this.driverLicenceNumber = driverLicenceNumber;
    }

    public void setDriverLicenceDate(Date driverLicenceDate) {
        this.driverLicenceDate = driverLicenceDate;
    }

    public void setWarTicketNumber(String warTicketNumber) {
        this.warTicketNumber = warTicketNumber;
    }

    public void setWarTicketDate(Date warTicketDate) {
        this.warTicketDate = warTicketDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "VisitorItem{" +
                "idOfVisitor=" + idOfVisitor +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", secondName='" + secondName + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportDate=" + passportDate +
                ", driverLicenceNumber='" + driverLicenceNumber + '\'' +
                ", driverLicenceDate=" + driverLicenceDate +
                ", warTicketNumber='" + warTicketNumber + '\'' +
                ", warTicketDate=" + warTicketDate +
                ", position=" + position +
                ", freeDocName=" + freeDocName +
                ", freeDocNumber=" + freeDocNumber +
                ", freeDocDate=" + freeDocDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VisitorItem that = (VisitorItem) o;

        if (driverLicenceDate != null ? !driverLicenceDate.equals(that.driverLicenceDate)
                : that.driverLicenceDate != null) {
            return false;
        }
        if (driverLicenceNumber != null ? !driverLicenceNumber.equals(that.driverLicenceNumber)
                : that.driverLicenceNumber != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) {
            return false;
        }
        if (idOfVisitor != null ? !idOfVisitor.equals(that.idOfVisitor) : that.idOfVisitor != null) {
            return false;
        }
        if (operationDate != null ? !operationDate.equals(that.operationDate) : that.operationDate != null) {
            return false;
        }
        if (passportDate != null ? !passportDate.equals(that.passportDate) : that.passportDate != null) {
            return false;
        }
        if (passportNumber != null ? !passportNumber.equals(that.passportNumber) : that.passportNumber != null) {
            return false;
        }
        if (secondName != null ? !secondName.equals(that.secondName) : that.secondName != null) {
            return false;
        }
        if (surname != null ? !surname.equals(that.surname) : that.surname != null) {
            return false;
        }
        if (warTicketDate != null ? !warTicketDate.equals(that.warTicketDate) : that.warTicketDate != null) {
            return false;
        }
        if (warTicketNumber != null ? !warTicketNumber.equals(that.warTicketNumber) : that.warTicketNumber != null) {
            return false;
        }
        if (position != null ? !position.equals(that.position) : that.position != null) {
            return false;
        }
        if (freeDocName != null ? !freeDocName.equals(that.freeDocName) : that.freeDocName != null) {
            return false;
        }
        if (freeDocNumber != null ? !freeDocNumber.equals(that.freeDocNumber) : that.freeDocNumber != null) {
            return false;
        }
        if (freeDocDate != null ? !freeDocDate.equals(that.freeDocDate) : that.freeDocDate != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfVisitor != null ? idOfVisitor.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (secondName != null ? secondName.hashCode() : 0);
        result = 31 * result + (passportNumber != null ? passportNumber.hashCode() : 0);
        result = 31 * result + (passportDate != null ? passportDate.hashCode() : 0);
        result = 31 * result + (driverLicenceNumber != null ? driverLicenceNumber.hashCode() : 0);
        result = 31 * result + (driverLicenceDate != null ? driverLicenceDate.hashCode() : 0);
        result = 31 * result + (warTicketNumber != null ? warTicketNumber.hashCode() : 0);
        result = 31 * result + (warTicketDate != null ? warTicketDate.hashCode() : 0);
        result = 31 * result + (operationDate != null ? operationDate.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (freeDocName != null ? freeDocName.hashCode() : 0);
        result = 31 * result + (freeDocNumber != null ? freeDocNumber.hashCode() : 0);
        result = 31 * result + (freeDocDate != null ? freeDocDate.hashCode() : 0);
        return result;
    }
}
