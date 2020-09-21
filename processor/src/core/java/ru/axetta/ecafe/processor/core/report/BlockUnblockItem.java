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
public class BlockUnblockItem {
    private String requestId;
    private Date blockdate;
    private Date unblockdate;
    private String blockdateString;
    private String unblockdateString;
    private String operation;
    private String extClientId;
    private String firstname;
    private String lastname;
    private String middlename;
    private String groupname;
    private Long contractIdp;
    private String firp;
    private String lastp;
    private String middp;
    private String shortname;
    private String address;
    private String cardstate;
    private Long cardno;
    private Long cardprintedno;
    private Long idofclient;
    private Long idoforg;
    private Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");


    public BlockUnblockItem(String requestId, Date blockdate,Date unblockdate, String operation, String extClientId,
            String firstname, String lastname, String middlename, String groupname, Long contractIdp, String firp, String lastp,
            String middp,  String shortname, String address, String cardstate, Long cardno,
            Long cardprintedno, Long idofclient, Long idoforg) {
        this.idofclient = idofclient;
        this.idoforg = idoforg;
        this.requestId = requestId;
        this.blockdate=blockdate;
        this.unblockdate=unblockdate;
        this.blockdateString=formatter.format(blockdate);
        if (unblockdate != null)
        {
            Long dateL = unblockdate.getTime();
            if (dateL.equals(0L))
                this.unblockdateString="";
            else
                this.unblockdateString=formatter.format(unblockdate);
        }
        else
            this.unblockdateString="";
        this.operation=operation;
        this.extClientId=extClientId;
        this.firstname=firstname;
        this.lastname=lastname;
        this.middlename=middlename;
        this.groupname=groupname;
        this.contractIdp=contractIdp;
        this.firp=firp;
        this.lastp=lastp;
        this.middp=middp;
        if (firp == null)
            this.firp = "";
        if (lastp == null)
            this.lastp = "";
        if (middp == null)
            this.middp = "";
        this.shortname=shortname;
        this.address=address;
        this.cardstate=cardstate;
        this.cardno=cardno;
        this.cardprintedno=cardprintedno;
    }

    public BlockUnblockItem(String requestId, Date blockdate,Date unblockdate, String operation, String extClientId,
            String firstname, String lastname, String middlename, String groupname, Long contractIdp, String firp, String lastp,
            String middp,  String shortname, String address, String cardstate, Long cardno, Long cardprintedno) {
        this.requestId = requestId;
        this.blockdate=blockdate;
        this.unblockdate=unblockdate;
        this.blockdateString=formatter.format(blockdate);
        if (unblockdate != null)
        {
            Long dateL = unblockdate.getTime();
            if (dateL.equals(0L))
                this.unblockdateString="";
            else
                this.unblockdateString=formatter.format(unblockdate);
        }
        else
            this.unblockdateString="";
        this.operation=operation;
        this.extClientId=extClientId;
        this.firstname=firstname;
        this.lastname=lastname;
        this.middlename=middlename;
        this.groupname=groupname;
        this.contractIdp=contractIdp;
        this.firp=firp;
        this.lastp=lastp;
        this.middp=middp;
        if (firp == null)
            this.firp = "";
        if (lastp == null)
            this.lastp = "";
        if (middp == null)
            this.middp = "";
        this.shortname=shortname;
        this.address=address;
        this.cardstate=cardstate;
        this.cardno=cardno;
        this.cardprintedno=cardprintedno;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
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

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getFirp() {
        return firp;
    }

    public void setFirp(String firp) {
        this.firp = firp;
    }

    public String getLastp() {
        return lastp;
    }

    public void setLastp(String lastp) {
        this.lastp = lastp;
    }

    public String getMiddp() {
        return middp;
    }

    public void setMiddp(String middp) {
        this.middp = middp;
    }

    public String getCardstate() {
        return cardstate;
    }

    public void setCardstate(String cardstate) {
        this.cardstate = cardstate;
    }

    public Long getCardno() {
        return cardno;
    }

    public void setCardno(Long cardno) {
        this.cardno = cardno;
    }

    public Long getCardprintedno() {
        return cardprintedno;
    }

    public void setCardprintedno(Long cardprintedno) {
        this.cardprintedno = cardprintedno;
    }

    public Date getBlockdate() {
        return blockdate;
    }

    public void setBlockdate(Date blockdate) {
        this.blockdate = blockdate;
    }

    public Date getUnblockdate() {
        return unblockdate;
    }

    public void setUnblockdate(Date unblockdate) {
        this.unblockdate = unblockdate;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getExtClientId() {
        return extClientId;
    }

    public void setExtClientId(String extClientId) {
        this.extClientId = extClientId;
    }

    public Long getContractIdp() {
        return contractIdp;
    }

    public void setContractIdp(Long contractIdp) {
        this.contractIdp = contractIdp;
    }

    public String getBlockdateString() {
        return blockdateString;
    }

    public void setBlockdateString(String blockdateString) {
        this.blockdateString = blockdateString;
    }

    public String getUnblockdateString() {
        return unblockdateString;
    }

    public void setUnblockdateString(String unblockdateString) {
        this.unblockdateString = unblockdateString;
    }

    public Long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(Long idofclient) {
        this.idofclient = idofclient;
    }

    public Long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(Long idoforg) {
        this.idoforg = idoforg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }
}