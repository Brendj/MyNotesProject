package ru.iteco.meshsync.models;

import ru.iteco.meshsync.audit.AuditEntity;
import ru.iteco.meshsync.audit.AuditEntityListener;
import ru.iteco.meshsync.audit.Auditable;
import ru.iteco.meshsync.enums.ActionType;
import ru.iteco.meshsync.enums.EntityType;
import ru.iteco.meshsync.kafka.dto.EntityChangeEventDTO;

import org.apache.logging.log4j.util.Strings;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_mh_entity_changes", uniqueConstraints={
        @UniqueConstraint(columnNames={"personGUID", "entity"})
})
public class EntityChanges implements Serializable, Auditable { // legacy code
    @GenericGenerator(
            name = "cf_mh_EntityChanges_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "cf_mh_EntityChanges_id_seq"),
                    @Parameter(name = "INCREMENT", value = "1"),
                    @Parameter(name = "MINVALUE", value = "1"),
                    @Parameter(name = "MAXVALUE", value = "2147483647"),
                    @Parameter(name = "CACHE", value = "1")
            }
    )

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_mh_EntityChanges_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "entityid")
    private String entityId;

    @Column(name = "personguid", nullable = false)
    private String personGUID;

    @Column(name = "mergedpersonids")
    private String mergedPersonIds;

    @Column(name = "entity", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private EntityType entity;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ActionType action;

    @Column(name = "uid", length = 36)
    private String uid;

    @Embedded
    private AuditEntity audit;

    public EntityChanges(String entityId, String personGUID, String mergedPersonIds, EntityType entity, ActionType action, AuditEntity audit) {
        this.entityId = entityId;
        this.personGUID = personGUID;
        this.mergedPersonIds = mergedPersonIds;
        this.entity = entity;
        this.action = action;
        this.audit = audit;
    }

    public EntityChanges() {
    }

    public static EntityChanges buildFromDTO(EntityChangeEventDTO dto){
        String entityId = dto.getEntity_id();
        String personGUID = dto.getPerson_id();
        String mergedPersonIds = Strings.join(dto.getMerged_person_ids(), ',');
        EntityType entity = dto.getEntity_name();
        ActionType action = dto.getAction();
        return new EntityChanges(entityId, personGUID, mergedPersonIds, entity, action, null);
    }

    @Override
    public AuditEntity getAudit() {
        return audit;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPersonGUID() {
        return personGUID;
    }

    public void setPersonGUID(String personGUID) {
        this.personGUID = personGUID;
    }

    public String getMergedPersonIds() {
        return mergedPersonIds;
    }

    public void setMergedPersonIds(String mergedPersonIds) {
        this.mergedPersonIds = mergedPersonIds;
    }

    public EntityType getEntity() {
        return entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityChanges that = (EntityChanges) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
