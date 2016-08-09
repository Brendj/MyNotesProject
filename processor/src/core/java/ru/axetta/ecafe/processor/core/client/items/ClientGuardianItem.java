package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianRelationType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.12.13
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianItem {
    private Long idOfClient;
    private Long contractId;
    private String personName;
    private Boolean disabled;
    private String mobile;
    private boolean isNew;
    private Integer relation;

    public ClientGuardianItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.mobile = client.getMobile();
        this.disabled = false;
        isNew = true;
    }

    public ClientGuardianItem(Client client, Boolean disabled, ClientGuardianRelationType relation) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.disabled = disabled;
        this.mobile = client.getMobile();
        this.relation = relation == null ? null : relation.ordinal();
        isNew = false;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getPersonName() {
        return personName;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getEnabled() {
        return !disabled;
    }

    public void setEnabled(Boolean enabled) {
        this.disabled = !enabled;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getMobile() {
        return mobile;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    public String getRelationStr() {
        return relation == null ? "" : ClientGuardianRelationType.fromInteger(relation).toString();
    }
}
