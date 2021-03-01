package ru.iteco.emias.audit;

public interface Auditable {
    AuditEntity getAudit();
    void setAudit(AuditEntity audit);
}
