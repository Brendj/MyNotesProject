/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Voinov
 * Date: 07.09.20
 * To change this template use File | Settings | File Templates.
 */
public class EmiasItem {
    private Long emiasID;
    private String firstname;
    private String lastname;
    private String middlename;
    private String dateLiberation;
    private String dateStartLiberation;
    private String dateEndLiberation;
    private String accepted;

    private Format formatter = new SimpleDateFormat("dd.MM.yyyy");

    public EmiasItem(Long emiasID, String firstname, String lastname, String middlename,
            Date dateLiberation, Date dateStartLiberation, Date dateEndLiberation, Boolean accepted)
    {
        this.emiasID = emiasID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.dateLiberation = formatter.format(dateLiberation);
        this.dateStartLiberation = formatter.format(dateStartLiberation);
        this.dateEndLiberation = formatter.format(dateEndLiberation);
        this.accepted = accepted.toString();
    }


    public Long getEmiasID() {
        return emiasID;
    }

    public void setEmiasID(Long emiasID) {
        this.emiasID = emiasID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getDateLiberation() {
        return dateLiberation;
    }

    public void setDateLiberation(String dateLiberation) {
        this.dateLiberation = dateLiberation;
    }

    public String getDateStartLiberation() {
        return dateStartLiberation;
    }

    public void setDateStartLiberation(String dateStartLiberation) {
        this.dateStartLiberation = dateStartLiberation;
    }

    public String getDateEndLiberation() {
        return dateEndLiberation;
    }

    public void setDateEndLiberation(String dateEndLiberation) {
        this.dateEndLiberation = dateEndLiberation;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }
}