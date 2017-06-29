package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.06.13
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class Visitor {

    public static final Integer DEFAULT_TYPE = 0;
    public static final Integer EMPLOYEE_TYPE = 1;
    public static final Integer VISITORDOGM_TYPE = 2;
    private Long idOfVisitor;
    private Person person;
    private String passportNumber;
    private Date passportDate;
    private String driverLicenceNumber;
    private Date driverLicenceDate;
    private String warTicketNumber;
    private Date warTicketDate;
    //private VisitorType visitorType;
    private Integer visitorType;
    private Set<CardTemp> cards = new HashSet<CardTemp>();
    private Set<Card> cardsInternal = new HashSet<Card>();
    private String position;
    private boolean deleted;

    private String freeDocName;
    private String freeDocNumber;
    private Date freeDocDate;

    protected Visitor() {}

    public Visitor(Person person) {
        this.person = person;
    }

    public Visitor(Person person, String passportNumber, Date passportDate, String driverLicenceNumber,
            Date driverLicenceDate, String warTicketNumber, Date warTicketDate, Integer visitorType, String position) {
        this.person = person;
        this.passportNumber = passportNumber;
        this.passportDate = passportDate;
        this.driverLicenceNumber = driverLicenceNumber;
        this.driverLicenceDate = driverLicenceDate;
        this.warTicketNumber = warTicketNumber;
        this.warTicketDate = warTicketDate;
        this.visitorType = visitorType;
        this.position = position;
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

    public Set<CardTemp> getCards() {
        return cards;
    }

    public void setCards(Set<CardTemp> cards) {
        this.cards = cards;
    }

    public Integer getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(Integer visitorType) {
        this.visitorType = visitorType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    @Override
    public String toString() {
        return "Visitor{idOfVisitor=" + idOfVisitor +'}';
    }

    public Set<Card> getCardsInternal() {
        return cardsInternal;
    }

    public void setCardsInternal(Set<Card> cardsInternal) {
        this.cardsInternal = cardsInternal;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Visitor visitor = (Visitor) o;

        if (!idOfVisitor.equals(visitor.idOfVisitor)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfVisitor.hashCode();
    }

    public static boolean isEmptyDocumentParams(String number, Date validDate) {
        return (validDate==null || StringUtils.isEmpty(number));
    }

    public static boolean isEmptyFreeDocumentParams(String name, String number, Date validDate) {
        return (validDate==null || StringUtils.isEmpty(name) || StringUtils.isEmpty(number));
    }
}
