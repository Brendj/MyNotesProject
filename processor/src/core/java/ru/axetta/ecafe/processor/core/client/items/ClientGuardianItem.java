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
    private ClientCreatedFromType createdWhereClientGuardian;
    private ClientCreatedFromType createdWhereGuardian;
    private String createdWhereGuardianDesc;
    private Boolean informedSpecialMenu;
    private Boolean allowedPreorders;
    private Boolean isLegalRepresentative;

    public ClientGuardianItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.mobile = client.getMobile();
        this.disabled = false;
        isNew = true;
        informedSpecialMenu = false;
        allowedPreorders = false;
        isLegalRepresentative = false;
    }

    public ClientGuardianItem(Client client, Boolean disabled, ClientGuardianRelationType relation,
            List notificationSettings, ClientCreatedFromType createdWhereClientGuardian,
            ClientCreatedFromType createdWhereGuardian, String createdWhereGuardianDesc,
            Boolean informedSpecialMenu, Boolean isLegalRepresentative, Boolean allowedPreorders) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.disabled = disabled;
        this.mobile = client.getMobile();
        this.relation = relation == null ? null : relation.ordinal();
        this.notificationItems = notificationSettings;
        isNew = false;
        this.createdWhereClientGuardian = createdWhereClientGuardian;
        this.createdWhereGuardian = createdWhereGuardian;
        this.createdWhereGuardianDesc = createdWhereGuardianDesc;
        this.informedSpecialMenu = informedSpecialMenu;
        this.isLegalRepresentative = isLegalRepresentative;
        this.allowedPreorders = allowedPreorders;
    }

    public String getCreatedWhereClientGuardianStr() {
        switch (createdWhereClientGuardian) {
            case DEFAULT : return "";
            case ARM: return "Создано в АРМ";
            case BACK_OFFICE: return String.format("Создано в веб (пользователь: %s)", createdWhereGuardianDesc);
            case MPGU: return String.format("Создано на mos.ru (%s)", createdWhereGuardianDesc);
            case REGISTRY: return "Создано в АИС НСИ Реестр";
        }
        return "";
    }

    public String getCreatedWhereGuardianStr() {
        switch (createdWhereGuardian) {
            case DEFAULT : return "";
            case ARM: return "Создано в АРМ";
            case BACK_OFFICE: return String.format("Создано в веб (пользователь: %s)", createdWhereGuardianDesc);
            case MPGU: return String.format("Создано на mos.ru (%s)", createdWhereGuardianDesc);
            case REGISTRY: return "Создано в АИС НСИ Реестр";
        }
        return "";
    }

    public boolean getIsCreatedWhereDefault() {
        return createdWhereClientGuardian.equals(ClientCreatedFromType.DEFAULT);
    }
    public boolean getIsMoskvenok() {
        return createdWhereClientGuardian.equals(ClientCreatedFromType.MPGU);
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

    public ClientCreatedFromType getCreatedWhereGuardian() {
        return createdWhereGuardian;
    }

    public void setCreatedWhereGuardian(ClientCreatedFromType createdWhereGuardian) {
        this.createdWhereGuardian = createdWhereGuardian;
    }

    public String getCreatedWhereGuardianDesc() {
        return createdWhereGuardianDesc;
    }

    public void setCreatedWhereGuardianDesc(String createdWhereGuardianDesc) {
        this.createdWhereGuardianDesc = createdWhereGuardianDesc;
    }

    public Boolean getInformedSpecialMenu() {
        return informedSpecialMenu;
    }

    public void setInformedSpecialMenu(Boolean informedSpecialMenu) {
        this.informedSpecialMenu = informedSpecialMenu;
    }

    public Boolean getLegalRepresentative() {
        return isLegalRepresentative;
    }

    public void setLegalRepresentative(Boolean legalRepresentative) {
        isLegalRepresentative = legalRepresentative;
    }

    public Boolean getAllowedPreorders() {
        return allowedPreorders;
    }

    public void setAllowedPreorders(Boolean allowedPreorders) {
        this.allowedPreorders = allowedPreorders;
    }
}
