/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class JournalItem extends DistributedObject {

    private Journal journal;
    private Fund fund;
    private Ksu1Record ksu1Record;
    private Ksu2Record ksu2Record;

    private Date date;
    private String number;
    private int cost;

    private String guidJournal;
    private String guidFund;
    private String guidKsu1Record;
    private String guidKsu2Record;


    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public JournalItem parseAttributes(Node node) throws Exception {

        guidFund = getStringAttributeValue(node, "guidFund", 1024);
        guidJournal = getStringAttributeValue(node, "guidJournal", 1024);
        guidKsu1Record = getStringAttributeValue(node, "guidKsu1Record", 1024);
        guidKsu2Record = getStringAttributeValue(node, "guidKsu2Record", 1024);

        date = getDateTimeAttributeValue(node, "date");
        number = getStringAttributeValue(node, "number", 10);
        cost = getIntegerAttributeValue(node, "cost");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setJournal(daoService.findDistributedObjectByRefGUID(Journal.class, guidJournal));
        setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
        setKsu1Record(daoService.findDistributedObjectByRefGUID(Ksu1Record.class, guidKsu1Record));
        setKsu2Record(daoService.findDistributedObjectByRefGUID(Ksu2Record.class, guidKsu2Record));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFund(((JournalItem) distributedObject).getFund());
        setJournal(((JournalItem) distributedObject).getJournal());
        setKsu1Record(((JournalItem) distributedObject).getKsu1Record());
        setKsu2Record(((JournalItem) distributedObject).getKsu2Record());

        setDate(((JournalItem) distributedObject).getDate());
        setNumber(((JournalItem) distributedObject).getNumber());
        setCost(((JournalItem) distributedObject).getCost());
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Ksu1Record getKsu1Record() {
        return ksu1Record;
    }

    public void setKsu1Record(Ksu1Record ksu1Record) {
        this.ksu1Record = ksu1Record;
    }

    public Ksu2Record getKsu2Record() {
        return ksu2Record;
    }

    public void setKsu2Record(Ksu2Record ksu2Record) {
        this.ksu2Record = ksu2Record;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "JournalItem{" +
                "journal=" + journal +
                ", fund=" + fund +
                ", ksu1Record=" + ksu1Record +
                ", ksu2Record=" + ksu2Record +
                ", date=" + date +
                ", number='" + number + '\'' +
                ", cost=" + cost +
                '}';
    }
}
