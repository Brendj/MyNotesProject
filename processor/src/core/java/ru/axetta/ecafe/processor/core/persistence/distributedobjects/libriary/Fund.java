/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class Fund extends DistributedObject {

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public Fund parseAttributes(Node node) throws Exception{

        String fundName = getStringAttributeValue(node, "FundName", 128);
        if (fundName != null) {
            setFundName(fundName);
        }

        Boolean bollStud =  getBollAttributeValue(node, "Stud");
        if(bollStud != null){
            setStud(bollStud);
        }

        setSendAll(SendToAssociatedOrgs.DontSend);

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFundName(((Fund) distributedObject).getFundName());
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Override
    public String toString() {
        return String.format("Fund{fundName='%s'}", fundName);
    }

    private String fundName;
    private Boolean stud;
    private Set<Ksu2Record> ksu2RecordInternal;
    private Set<Ksu1Record> ksu1RecordInternal;
    private Set<JournalItem> journalItemInternal;
    private Set<Journal> journalInternal;
    private Set<Instance> instanceInternal;

    public Set<Instance> getInstanceInternal() {
        return instanceInternal;
    }

    public void setInstanceInternal(Set<Instance> instanceInternal) {
        this.instanceInternal = instanceInternal;
    }

    public Set<Journal> getJournalInternal() {
        return journalInternal;
    }

    public void setJournalInternal(Set<Journal> journalInternal) {
        this.journalInternal = journalInternal;
    }

    public Set<JournalItem> getJournalItemInternal() {
        return journalItemInternal;
    }

    public void setJournalItemInternal(Set<JournalItem> journalItemInternal) {
        this.journalItemInternal = journalItemInternal;
    }

    public Set<Ksu1Record> getKsu1RecordInternal() {
        return ksu1RecordInternal;
    }

    public void setKsu1RecordInternal(Set<Ksu1Record> ksu1RecordInternal) {
        this.ksu1RecordInternal = ksu1RecordInternal;
    }

    public Set<Ksu2Record> getKsu2RecordInternal() {
        return ksu2RecordInternal;
    }

    public void setKsu2RecordInternal(Set<Ksu2Record> ksu2RecordInternal) {
        this.ksu2RecordInternal = ksu2RecordInternal;
    }

    public Boolean getStud() {
        return stud;
    }

    public void setStud(Boolean stud) {
        this.stud = stud;
    }
}
