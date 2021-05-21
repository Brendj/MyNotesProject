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
    private Long recordID;
    private Long orgID;
    private String orgName;
    private String orgAdress;
    private String clientGroup;
    private String firstname;
    private String lastname;
    private String middlename;
    private Long contractID;
    private String benefits;
    private Long emiasID;
    private String emiasDate;
    private String dateStartLiberation;
    private String dateEndLiberation;
    private String emiasDatearchived;
    private String status;
    private Boolean accepted;
    private String acceptedinOO;

    private Format formatter = new SimpleDateFormat("dd.MM.yyyy");

    public EmiasItem(Long recordID, Long orgID, String orgName, String orgAdress, String clientGroup,String firstname,
            String lastname, String middlename, Long contractID, String benefits, Long emiasID, Date emiasDate,
            Date dateStartLiberation, Date dateEndLiberation, Date emiasDatearchived, String status,
            Boolean accepted, Date acceptedinOO)
    {
        this.recordID = recordID;
        this.orgID = orgID;
        this.orgName = orgName;
        this.orgAdress = orgAdress;
        this.clientGroup = clientGroup;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.contractID = contractID;
        this.benefits = benefits;
        this.emiasID = emiasID;
        if (emiasDate != null)
            this.emiasDate = formatter.format(emiasDate);
        if (dateStartLiberation != null)
            this.dateStartLiberation = formatter.format(dateStartLiberation);
        if (dateEndLiberation != null)
            this.dateEndLiberation = formatter.format(dateEndLiberation);
        if (emiasDatearchived != null)
            this.emiasDatearchived = formatter.format(emiasDatearchived);
        this.status = status;
        this.accepted = accepted;
        if (acceptedinOO != null)
            this.acceptedinOO = formatter.format(acceptedinOO);
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

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Long getRecordID() {
        return recordID;
    }

    public void setRecordID(Long recordID) {
        this.recordID = recordID;
    }

    public Long getOrgID() {
        return orgID;
    }

    public void setOrgID(Long orgID) {
        this.orgID = orgID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAdress() {
        return orgAdress;
    }

    public void setOrgAdress(String orgAdress) {
        this.orgAdress = orgAdress;
    }

    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }

    public Long getContractID() {
        return contractID;
    }

    public void setContractID(Long contractID) {
        this.contractID = contractID;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getEmiasDate() {
        return emiasDate;
    }

    public void setEmiasDate(String emiasDate) {
        this.emiasDate = emiasDate;
    }

    public String getEmiasDatearchived() {
        return emiasDatearchived;
    }

    public void setEmiasDatearchived(String emiasDatearchived) {
        this.emiasDatearchived = emiasDatearchived;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptedinOO() {
        return acceptedinOO;
    }

    public void setAcceptedinOO(String acceptedinOO) {
        this.acceptedinOO = acceptedinOO;
    }
}