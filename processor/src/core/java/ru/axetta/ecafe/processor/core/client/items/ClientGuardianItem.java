package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientCreatedFromType;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianRelationType;

import java.util.ArrayList;
import java.util.List;

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
    private List<NotificationSettingItem> notificationItems = new ArrayList<NotificationSettingItem>();
    private ClientCreatedFromType createdWhere;

    public ClientGuardianItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.mobile = client.getMobile();
        this.disabled = false;
        isNew = true;
    }

    public ClientGuardianItem(Client client, Boolean disabled, ClientGuardianRelationType relation,
            List notificationSettings, ClientCreatedFromType createdWhere) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.disabled = disabled;
        this.mobile = client.getMobile();
        this.relation = relation == null ? null : relation.ordinal();
        this.notificationItems = notificationSettings;
        isNew = false;
        this.createdWhere = createdWhere;
        if (createdWhere.equals(ClientCreatedFromType.MPGU)) {

        }
    }

    public boolean getIsMoskvenok() {
        return createdWhere.equals(ClientCreatedFromType.MPGU);
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

    public List<NotificationSettingItem> getNotificationItems() {
        return notificationItems;
    }

    public void setNotificationItems(List<NotificationSettingItem> items) {
        this.notificationItems = items;
    }
}
