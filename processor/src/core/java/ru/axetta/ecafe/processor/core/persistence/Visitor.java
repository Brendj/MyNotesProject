package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.06.13
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class Visitor {

    private Long idOfVisitor;
    private Person person;
    private String passportNumber;
    private Date passportDate;
    private String driverLicenceNumber;
    private Date driverLicenceDate;
    private String warTicketNumber;
    private Date warTicketDate;

    protected Visitor() {}

    public Visitor(Person person) {
        this.person = person;
    }

    public Date getWarTicketDate() {
        return warTicketDate;
    }

    public void setWarTicketDate(Date warTicketDate) {
        this.warTicketDate = warTicketDate;
    }

    public String getWarTicketNumber() {
        return warTicketNumber;
    }

    public void setWarTicketNumber(String warTicketNumber) {
        this.warTicketNumber = warTicketNumber;
    }

    public Date getDriverLicenceDate() {
        return driverLicenceDate;
    }

    public void setDriverLicenceDate(Date driverLicenceDate) {
        this.driverLicenceDate = driverLicenceDate;
    }

    public String getDriverLicenceNumber() {
        return driverLicenceNumber;
    }

    public void setDriverLicenceNumber(String driverLicenceNumber) {
        this.driverLicenceNumber = driverLicenceNumber;
    }

    public Date getPassportDate() {
        return passportDate;
    }

    public void setPassportDate(Date passportDate) {
        this.passportDate = passportDate;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "idOfVisitor=" + idOfVisitor +
                '}';
    }
}
