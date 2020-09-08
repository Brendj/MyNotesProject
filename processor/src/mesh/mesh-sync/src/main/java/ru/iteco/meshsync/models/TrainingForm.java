package ru.iteco.meshsync.models;

import ru.iteco.meshsync.audit.AuditEntity;
import ru.iteco.meshsync.audit.AuditEntityListener;
import ru.iteco.meshsync.audit.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_kf_training_form")
public class TrainingForm implements Serializable, Auditable {
    @Id
    @Column(name = "global_id")
    private Long globalId;

    @Column(name = "system_object_id")
    private Long systemObjectId;

    @Column(name = "id")
    private Integer id;

    @Column(name = "code", length = 36)
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "education_form")
    private String educationForm;

    @Column(name = "archive", nullable = false)
    private Boolean archive;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Embedded
    private AuditEntity audit;

    public TrainingForm(){

    }

    @Override
    public AuditEntity getAudit() {
        return audit;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public Long getSystemObjectId() {
        return systemObjectId;
    }

    public void setSystemObjectId(Long systemObjectId) {
        this.systemObjectId = systemObjectId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEducationForm() {
        return educationForm;
    }

    public void setEducationForm(String educationForm) {
        this.educationForm = educationForm;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainingForm)) return false;
        TrainingForm that = (TrainingForm) o;
        return Objects.equals(getGlobalId(), that.getGlobalId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGlobalId());
    }
}
