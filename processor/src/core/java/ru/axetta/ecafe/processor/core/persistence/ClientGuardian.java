package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.12.13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardian {

    private static final Logger logger = LoggerFactory.getLogger(ClientGuardian.class);

    private Long idOfClientGuardian;
    private Long version;
    private Long idOfChildren;
    private Long idOfGuardian;
    private Integer guardianType;
    private Boolean disabled;
    private Boolean deletedState;
    private Date deleteDate;
    private ClientGuardianRelationType relation;
    private Set<ClientGuardianNotificationSetting> notificationSettings = new HashSet<>();
    private ClientCreatedFromType createdFrom;
    private Date lastUpdate;
    private CardRequest cardRequest;
    private ClientGuardianRepresentType representType;
    private ClientGuardianHistory clientGuardianHistory = null;

    public void initializateClientGuardianHistory(ClientGuardianHistory clientGuardianHistory)
    {
        this.clientGuardianHistory = clientGuardianHistory;
        clientGuardianHistory.setClientGuardian(this);
    }

    protected ClientGuardian() {
    }

    public ClientGuardian(Long idOfChildren, Long idOfGuardian) {
        this.idOfChildren = idOfChildren;
        this.idOfGuardian = idOfGuardian;
        this.createdFrom = ClientCreatedFromType.DEFAULT;
    }

    public ClientGuardian(Long idOfChildren, Long idOfGuardian, ClientCreatedFromType createdFrom) {
        this.idOfChildren = idOfChildren;
        this.idOfGuardian = idOfGuardian;
        this.createdFrom = createdFrom;
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
         if (clientGuardianHistory != null) {
             if (this.version == null || version == null || !version.equals(this.version)) {
                 createNewClientGuardianHistory(clientGuardianHistory, ClientGuardionHistoryAction.VERSION.getNativedescription(),
                         ClientGuardionHistoryAction.VERSION.getDescription(), this.version == null ? null : this.version.toString(),
                         version == null ? null : version.toString());
             }
         }
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

    public void disable(Long version) {
        this.setDeletedState(true);
        this.setDisabled(true);
        this.setVersion(version);
        this.setLastUpdate(new Date());
    }

    public void delete(Long version) {
        this.setDeletedState(true);
        this.setDisabled(true);
        this.setDeleteDate(RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime());
        this.setVersion(version);
        this.setLastUpdate(new Date());
    }

    public void restore(Long version, boolean enableSpecialNotification) {
        this.setDeletedState(false);
        this.setDisabled(false);
        this.setDeleteDate(null);
        this.setVersion(version);
        this.setLastUpdate(new Date());
        if (enableSpecialNotification) {
            getNotificationSettings().add(new ClientGuardianNotificationSetting(this,
                    ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue()));
        }
    }

    public Boolean isDisabled() {
        if (disabled == null) {
            return false;
        } else {
            return disabled;
        }
    }

    public void setDisabled(Boolean disabled) {
        if (clientGuardianHistory != null) {
            if (this.isDisabled() == null || !this.isDisabled()) {
                createNewClientGuardianHistory(clientGuardianHistory, ClientGuardionHistoryAction.DISABLED.getNativedescription(),
                        ClientGuardionHistoryAction.DISABLED.getDescription(),
                        this.isDisabled() == null ? null : this.isDisabled().toString(), "true");
            }
        }
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
        if (clientGuardianHistory != null) {
            if (this.getDeletedState() == null || this.getDeletedState()) {
                createNewClientGuardianHistory(clientGuardianHistory, ClientGuardionHistoryAction.DELETED.getNativedescription(),
                        ClientGuardionHistoryAction.DELETED.getDescription(),
                        this.getDeletedState() == null ? null : this.getDeletedState().toString(), "false");
            }
        }
        this.deletedState = deletedState;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        if (clientGuardianHistory != null) {
            if (this.getDeleteDate() != null) {
                createNewClientGuardianHistory(clientGuardianHistory, ClientGuardionHistoryAction.DELETED_DATE.getNativedescription(),
                        ClientGuardionHistoryAction.DELETED_DATE.getDescription(),
                        this.getDeleteDate() == null ? null : this.getDeleteDate().toString(), null);
            }
        }
        this.deleteDate = deleteDate;
    }

    public ClientGuardianRelationType getRelation() {
        return relation;
    }

    public void setRelation(ClientGuardianRelationType relation) {
        if (clientGuardianHistory != null) {
            if (this.relation == null || relation == null || !relation.equals(this.relation)) {
                createNewClientGuardianHistory(clientGuardianHistory, ClientGuardionHistoryAction.RELATION.nativedescription,
                        ClientGuardionHistoryAction.RELATION.description, this.relation == null ? null : String.valueOf(this.relation.getCode()),
                        relation == null ? null : String.valueOf(relation.getCode()));
            }
        }
        this.relation = relation;
    }

    public Set<ClientGuardianNotificationSetting> getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(Set<ClientGuardianNotificationSetting> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int hashCode() {
        return idOfClientGuardian.hashCode();
    }

    public ClientCreatedFromType getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(ClientCreatedFromType createdFrom) {
        if (clientGuardianHistory != null) {
            if (this.createdFrom == null || createdFrom == null || !createdFrom.equals(this.createdFrom)) {
                createNewClientGuardianHistory(clientGuardianHistory, ClientGuardionHistoryAction.RELATION.nativedescription,
                        ClientGuardionHistoryAction.RELATION.description, this.createdFrom == null ? null : String.valueOf(this.createdFrom.getValue()),
                        createdFrom == null ? null : String.valueOf(createdFrom.getValue()));
            }
        }
        this.createdFrom = createdFrom;
    }

    public CardRequest getCardRequest() {
        return cardRequest;
    }

    public void setCardRequest(CardRequest cardRequest) {
        this.cardRequest = cardRequest;
    }

    public ClientGuardianRepresentType getRepresentType() {
        return representType;
    }

    public void setRepresentType(ClientGuardianRepresentType representType) {
        this.representType = representType;
    }

    public void createNewClientGuardianHistory(ClientGuardianHistory clientGuardianHistory, String action,
            String changeParam, String oldValue, String newValue) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            ClientGuardianHistory clientGuardianHistoryChanged = clientGuardianHistory
                    .getCopyClientGuardionHistory(clientGuardianHistory);
            clientGuardianHistoryChanged.setChangeDate(new Date());
            clientGuardianHistoryChanged.setAction(action);
            clientGuardianHistoryChanged.setChangeParam(changeParam);
            clientGuardianHistoryChanged.setOldValue(oldValue);
            clientGuardianHistoryChanged.setNewValue(newValue);
            session.persist(clientGuardianHistoryChanged);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Can't get all Vendors:", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public enum ClientGuardionHistoryAction {
        VERSION(0,"version", "Изменение версии"),
        DISABLED(1, "disabled", "Изменение флага \"показывать/не показывать\" опекуна внешним системам"),
        DELETED(2, "deletedState", "Изменение флага \"Статус удаления записи\""),
        REPRESENT_TYPE(3, "representType", "Изменение флага \"Законный представитель\""),
        RELATION(4, "relation", "Изменение типа родственной связи"),
        DELETED_DATE(5, "deleteDate", "Изменение Даты удаления");

        private final int code;
        private final String description;
        private final String nativedescription;

        static Map<Integer,ClientGuardionHistoryAction> map = new HashMap<Integer,ClientGuardionHistoryAction>();
        static {
            for (ClientGuardionHistoryAction questionaryStatus : ClientGuardionHistoryAction.values()) {
                map.put(questionaryStatus.code, questionaryStatus);
            }
        }

        private ClientGuardionHistoryAction(int code, String description, String nativedescription){
            this.code = code;
            this.description = description;
            this.nativedescription = nativedescription;
        }

        @Override
        public String toString() {
            return description;
        }

        public static ClientGuardionHistoryAction fromInteger(Integer value){
            if (value == null)
                return map.get(-1);
            return map.get(value);
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }


        public String getNativedescription() {
            return nativedescription;
        }
    }
}
