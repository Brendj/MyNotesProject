/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.ClientAllocationRule;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 03.09.13
 * Time: 12:15
 */

public class ClientAllocationRuleItem implements Serializable {

    private Long id;
    private Long idOfSourceOrg;
    private Long idOfDestOrg;
    private String sourceOrgName;
    private String destOrgName;
    private String groupFilter;
    private boolean tempClient;
    private boolean editable;

    public ClientAllocationRuleItem() {
    }

    public ClientAllocationRuleItem(ClientAllocationRule rule) {
        this.id = rule.getId();
        this.idOfSourceOrg = rule.getSourceOrg().getIdOfOrg();
        this.idOfDestOrg = rule.getDestinationOrg().getIdOfOrg();
        this.sourceOrgName = rule.getSourceOrg().getShortName();
        this.destOrgName = rule.getDestinationOrg().getShortName();
        this.groupFilter = rule.getGroupFilter();
        this.tempClient = rule.isTempClient();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOfSourceOrg() {
        return idOfSourceOrg;
    }

    public void setIdOfSourceOrg(Long idOfSourceOrg) {
        this.idOfSourceOrg = idOfSourceOrg;
    }

    public Long getIdOfDestOrg() {
        return idOfDestOrg;
    }

    public void setIdOfDestOrg(Long idOfDestOrg) {
        this.idOfDestOrg = idOfDestOrg;
    }

    public String getSourceOrgName() {
        return sourceOrgName;
    }

    public void setSourceOrgName(String sourceOrgName) {
        this.sourceOrgName = sourceOrgName;
    }

    public String getDestOrgName() {
        return destOrgName;
    }

    public void setDestOrgName(String destOrgName) {
        this.destOrgName = destOrgName;
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }

    public boolean isTempClient() {
        return tempClient;
    }

    public void setTempClient(boolean tempClient) {
        this.tempClient = tempClient;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
