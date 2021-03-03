package ru.iteco.emias.audit;

import ru.iteco.emias.models.EMIAS;
import ru.iteco.emias.repo.EMIASRepository;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

public class AuditEntityListener {
    @PrePersist
    public void setCreatedOn(EMIAS emias) {
        AuditEntity audit = emias.getAuditEntity();
        if(audit == null) {
            audit = new AuditEntity();
            emias.setAuditEntity(audit);
        }
        audit.setCreateDate(new Date().getTime());
        audit.setUpdateDate(new Date().getTime());
        emias.setKafka(true);

    }

    @PreUpdate
    public void setUpdatedOn(EMIAS emias) {
        AuditEntity audit = emias.getAuditEntity();
        audit.setUpdateDate(new Date().getTime());
    }
}
