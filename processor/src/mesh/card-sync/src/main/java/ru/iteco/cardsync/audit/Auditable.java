package ru.iteco.cardsync.audit;

public interface Auditable {
    AuditEntity getAudit();
    void setAudit(AuditEntity audit);
}
