package ru.iteco.emias.audit;

import ru.iteco.emias.models.EMIAS;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

public class AuditEntityListener {
    @PrePersist
    public void setCreatedOn(Auditable auditable) {
        AuditEntity audit = auditable.getAudit();
        if(audit == null) {
            audit = new AuditEntity();
            auditable.setAudit(audit);
        }
        audit.setCreateDate(new Date().getTime());
        audit.setUpdateDate(new Date().getTime());
    }

    @PreUpdate
    public void setUpdatedOn(Auditable auditable) {
        AuditEntity audit = auditable.getAudit();
        audit.setUpdateDate(new Date().getTime());
    }
}
