package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.12.13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardian {

    private Long idOfClientGuardian;
    private Long version;
    private Long idOfChildren;
    private Long idOfGuardian;
    private Integer guardianType;
    private Boolean disabled;
    private Boolean deletedState;
    private Date deleteDate;
    private ClientGuardianRelationType relation;
    private Set<ClientGuardianNotificationSetting> notificationSettings = new HashSet<ClientGuardianNotificationSetting>();

    protected ClientGuardian() {}

    public ClientGuardian(Long idOfChildren, Long idOfGuardian) {
        this.idOfChildren = idOfChildren;
        this.idOfGuardian = idOfGuardian;
        // При создании опекунской связи проставляем ей настройки оповещений по умолчанию.
        /*for (ClientGuardianNotificationSetting.Predefined predefined : ClientGuardianNotificationSetting.Predefined.values()) {
            if (predefined.isEnabledAtDefault()) {
                ClientGuardian clientGuardian = DAOReadonlyService.getInstance().findClientGuardianById(idOfChildren, idOfGuardian);
                notificationSettings.add(new ClientGuardianNotificationSetting(clientGuardian, predefined.getValue()));
            }
        }*/
    }

    public Long getIdOfClientGuardian() {
        return idOfClientGuardian;
    }

    public void setIdOfClientGuardian(Long idOfClientGuardian) {
        this.idOfClientGuardian = idOfClientGuardian;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdOfChildren() {
        return idOfChildren;
    }

    public void setIdOfChildren(Long idOfChildren) {
        this.idOfChildren = idOfChildren;
    }

    public Long getIdOfGuardian() {
        return idOfGuardian;
    }

    public void setIdOfGuardian(Long idOfGuardian) {
        this.idOfGuardian = idOfGuardian;
    }

    public Integer getGuardianType() {
        return guardianType;
    }

    public void setGuardianType(Integer guardianType) {
        this.guardianType = guardianType;
    }

    public void delete(Long version) {
        this.setDeletedState(true);
        this.setDisabled(true);
        this.setDeleteDate(RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime());
        this.setVersion(version);
    }

    public void restore(Long version) {
        this.setDeletedState(false);
        this.setDisabled(false);
        this.setDeleteDate(null);
        this.setVersion(version);
    }

    public Boolean isDisabled() {
        if (disabled == null) {
            return false;
        } else {
            return disabled;
        }
    }

    public void setDisabled(Boolean disabled) {
        if (disabled == null) {
            this.disabled = false;
        } else {
            this.disabled = disabled;
        }
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public ClientGuardianRelationType getRelation() {
        return relation;
    }

    public void setRelation(ClientGuardianRelationType relation) {
        this.relation = relation;
    }

    public Set<ClientGuardianNotificationSetting> getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(Set<ClientGuardianNotificationSetting> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    @Override
    public int hashCode() {
        return idOfClientGuardian.hashCode();
    }
}
