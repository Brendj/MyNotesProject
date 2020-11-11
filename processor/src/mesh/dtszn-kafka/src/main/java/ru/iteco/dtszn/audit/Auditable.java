package ru.iteco.dtszn.audit;

public interface Auditable {
    AuditEntity getAudit();
    void setAudit(AuditEntity audit);
}
