/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.CryptoPro.JCP.VMInspector.Private;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 25.10.13
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class BlockUnblockItem {
    private String requestId;
    private String shortname;
    private String address;
    private String firstname;
    private String lastname;
    private String middlename;
    private String groupname;
    private String firp;
    private String lastp;
    private String middp;
    private String cardstate;
    private Long cardno;
    private Long cardprintedno;
    private Date blockdate;
    private Date unblockdate;


    public BlockUnblockItem(String requestId, String shortname, String address, String firstname, String lastname, String middlename,
            String groupname, String firp, String lastp, String middp, String cardstate, Long cardno,
            Long cardprintedno, Date blockdate,Date unblockdate) {
        this.requestId = requestId;
        this.shortname=shortname;
        this.address=address;
        this.firstname=firstname;
        this.lastname=lastname;
        this.middlename=middlename;
        this.groupname=groupname;
        if (firp == null)
            firp = "";
        if (lastp == null)
            lastp = "";
        if (middp == null)
            middp = "";
        this.firp=firp;
        this.lastp=lastp;
        this.middp=middp;
        this.cardstate=cardstate;
        this.cardno=cardno;
        this.cardprintedno=cardprintedno;
        this.blockdate=blockdate;
        this.unblockdate=unblockdate;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}