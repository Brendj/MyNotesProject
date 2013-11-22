/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.Visitor;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 13:04
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
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public Date getPassportDate() {
        return passportDate;
    }

    public void setPassportDate(Date passportDate) {
        this.passportDate = passportDate;
    }

    public String getDriverLicenceNumber() {
        return driverLicenceNumber;
    }

    public void setDriverLicenceNumber(String driverLicenceNumber) {
        this.driverLicenceNumber = driverLicenceNumber;
    }

    public Date getDriverLicenceDate() {
        return driverLicenceDate;
    }

    public void setDriverLicenceDate(Date driverLicenceDate) {
        this.driverLicenceDate = driverLicenceDate;
    }

    public String getWarTicketNumber() {
        return warTicketNumber;
    }

    public void setWarTicketNumber(String warTicketNumber) {
        this.warTicketNumber = warTicketNumber;
    }

    public Date getWarTicketDate() {
        return warTicketDate;
    }

    public void setWarTicketDate(Date warTicketDate) {
        this.warTicketDate = warTicketDate;
    }

    public VisitorItem() {
    }
}
