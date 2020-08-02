package ru.iteco.nsisync.nsi.regisry.models;

import ru.iteco.nsisync.audit.AuditEntity;
import ru.iteco.nsisync.audit.AuditEntityListener;
import ru.iteco.nsisync.audit.Auditable;
import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public abstract class AbstractRegistry implements Serializable, Auditable {
    @Id
    @Column(name = "global_id")
    protected Long globalId;

    @Column(name = "system_object_id")
    protected String systemObjectId;

    @Embedded
    protected AuditEntity audit;

    public AbstractRegistry(Long globalId, String systemObjectId, AuditEntity audit) {
        this.globalId = globalId;
        this.systemObjectId = systemObjectId;
        this.audit = audit;
    }

    public AbstractRegistry() {
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public String getSystemObjectId() {
        return systemObjectId;
    }

    public void setSystemObjectId(String systemObjectId) {
        this.systemObjectId = systemObjectId;
    }

    @Override
    public AuditEntity getAudit() {
        return audit;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractRegistry)) return false;
        AbstractRegistry that = (AbstractRegistry) o;
        return getGlobalId().equals(that.getGlobalId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGlobalId());
    }

    public enum AbstractRegistryJsonFieldsEnum implements JsonFieldDescriptor {
        GLOBAL_ID("global_id", "Глобальный ID для Kafka"),
        SYSTEM_OBJECT_ID("system_object_id", "Системный идентификатор");

        AbstractRegistryJsonFieldsEnum(String jsonFieldName, String description){
            this.jsonFieldName = jsonFieldName;
            this.description = description;
        }

        private String jsonFieldName;
        private String description;

        @Override
        public String getJsonFieldName() {
            return jsonFieldName;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
