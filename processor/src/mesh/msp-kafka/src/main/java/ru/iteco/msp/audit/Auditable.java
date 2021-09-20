package ru.iteco.msp.audit;

public interface Auditable {
    AuditEntity getAudit();
    void setAudit(AuditEntity audit);
}
