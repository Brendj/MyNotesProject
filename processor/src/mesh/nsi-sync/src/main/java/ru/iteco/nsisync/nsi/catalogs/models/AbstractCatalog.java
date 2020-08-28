package ru.iteco.nsisync.nsi.catalogs.models;

import ru.iteco.nsisync.audit.AuditEntity;
import ru.iteco.nsisync.audit.AuditEntityListener;
import ru.iteco.nsisync.audit.Auditable;
import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public abstract class AbstractCatalog implements Serializable, Auditable {
    public static final Integer DELETE = 1;
    public static final Integer ACTIVE = 0;

    @Id
    @Column(name = "global_ID")
    protected Long globalID;

    @Column(name = "systemObjectId")
    protected Long systemObjectId;

    @Column(name = "title")
    protected String title;

    @Column(name = "is_deleted", columnDefinition = "integer default 0")
    protected Integer isDelete;

    @Embedded
    protected AuditEntity audit;

    public Long getGlobalID() {
        return globalID;
    }

    public void setGlobalID(Long globalID) {
        this.globalID = globalID;
    }

    public Long getSystemObjectId() {
        return systemObjectId;
    }

    public void setSystemObjectId(Long systemObjectId) {
        this.systemObjectId = systemObjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
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
        if (!(o instanceof AbstractCatalog)) return false;
        AbstractCatalog that = (AbstractCatalog) o;
        return Objects.equals(getGlobalID(), that.getGlobalID()) &&
                Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGlobalID(), getTitle());
    }

    public enum AbstractCatalogEnumJsonFields implements JsonFieldDescriptor {
        GLOBAL_ID("global_id", "Глобальный идентификатор Kafka"),
        SYSTEM_OBJECT_ID("system_object_id", "Системный идентификатор"),
        TITLE("title", "Описание");

        AbstractCatalogEnumJsonFields(String jsonFieldName, String description){
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
