package ru.iteco.meshsync.audit;

public interface Auditable {
    AuditEntity getAudit();
    void setAudit(AuditEntity audit);
}
