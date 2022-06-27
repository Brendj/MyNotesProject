package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;

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
    private Integer representativeType;
    private Integer role;
    private String meshGuid;

    public ClientGuardianItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
        this.mobile = client.getMobile();
        this.disabled = false;
        isNew = true;
        informedSpecialMenu = false;
        allowedPreorders = false;
        representativeType = ClientGuardianRepresentType.UNKNOWN.getCode();
    }

    public ClientGuardianItem(Client client, Boolean disabled, ClientGuardianRelationType relation,
                              List<NotificationSettingItem> notificationSettings, ClientCreatedFromType createdWhereClientGuardian,
                              ClientCreatedFromType createdWhereGuardian, String createdWhereGuardianDesc,
                              Boolean informedSpecialMenu, ClientGuardianRepresentType representativeType,
                              Boolean allowedPreorders, Boolean getFullName, ClientGuardianRoleType role){
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = getFullName != null && getFullName ? client.getPerson().getFullName() : client.getPerson().getSurnameAndFirstLetters();
        this.disabled = disabled;
        this.mobile = client.getMobile();
        this.relation = relation == null ? null : relation.getCode();
        this.role = role == null ? null : role.getCode();
        this.notificationItems = notificationSettings;
        isNew = false;
        this.createdWhereClientGuardian = createdWhereClientGuardian;
        this.createdWhereGuardian = createdWhereGuardian;
        this.createdWhereGuardianDesc = createdWhereGuardianDesc;
        this.informedSpecialMenu = informedSpecialMenu;
        this.representativeType = representativeType == null ? ClientGuardianRepresentType.UNKNOWN.getCode() : representativeType.getCode();
        this.allowedPreorders = allowedPreorders;
        this.meshGuid = client.getMeshGUID();
    }

    public ClientGuardianItem(Client client, Boolean disabled, ClientGuardianRelationType relation,
            List<NotificationSettingItem> notificationSettings, ClientCreatedFromType createdWhereClientGuardian,
            ClientCreatedFromType createdWhereGuardian, String createdWhereGuardianDesc,
            Boolean informedSpecialMenu, ClientGuardianRepresentType representativeType, Boolean allowedPreorders, ClientGuardianRoleType role) {
        this(client, disabled, relation, notificationSettings, createdWhereClientGuardian, createdWhereGuardian,
                createdWhereGuardianDesc, informedSpecialMenu, representativeType, allowedPreorders, false, role);
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

    public String getRepresentativeStr() {
        return representativeType == null ? ClientGuardianRepresentType.UNKNOWN.toString() : ClientGuardianRepresentType.fromInteger(representativeType).toString();
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

    public Boolean getAllowedPreorders() {
        return allowedPreorders;
    }

    public void setAllowedPreorders(Boolean allowedPreorders) {
        this.allowedPreorders = allowedPreorders;
    }

    public Integer getRepresentativeType() {
        return representativeType;
    }

    public void setRepresentativeType(Integer representativeType) {
        this.representativeType = representativeType;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }

    public String getRoleStr() {
        return role == null ? "" : ClientGuardianRoleType.fromInteger(role).toString();
    }

    public void activateNotificationSpecial() {
        if(this.getEnabled() && RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL)){
            for(NotificationSettingItem i : notificationItems){
                if(i.getNotifyType().equals(ClientNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue())){
                    i.setEnabled(true);
                }
            }
        }
    }
}
