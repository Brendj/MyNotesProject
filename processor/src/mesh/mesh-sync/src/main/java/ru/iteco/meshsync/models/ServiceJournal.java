package ru.iteco.meshsync.models;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import ru.iteco.meshsync.audit.AuditEntity;
import ru.iteco.meshsync.audit.AuditEntityListener;
import ru.iteco.meshsync.audit.Auditable;

import javax.persistence.*;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_mh_service_journal")
public class ServiceJournal implements Auditable {
    @GenericGenerator(
            name = "cf_mh_service_journal_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "cf_mh_service_journal_id_seq"),
                    @Parameter(name = "INCREMENT", value = "1"),
                    @Parameter(name = "MINVALUE", value = "1"),
                    @Parameter(name = "MAXVALUE", value = "2147483647"),
                    @Parameter(name = "CACHE", value = "1")
            }
    )

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_mh_service_journal_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "exceptionclass", length = 128)
    private String exceptionClass;

    @Column(name = "personguid", length = 36, nullable = false)
    private String personGUID;

    @Column(name = "decided", nullable = false)
    private Boolean decided = true;

    @Embedded
    private AuditEntity audit;

    public ServiceJournal() {
    }

    public ServiceJournal(String message, String exceptionClass, String personGUID, Boolean decided) {
        this.message = message;
        this.exceptionClass = exceptionClass;
        this.personGUID = personGUID;
        this.decided = decided;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getPersonGUID() {
        return personGUID;
    }

    public void setPersonGUID(String personGUID) {
        this.personGUID = personGUID;
    }

    public Boolean getDecided() {
        return decided;
    }

    public void setDecided(Boolean decided) {
        this.decided = decided;
    }

    @Override
    public AuditEntity getAudit() {
        return this.audit;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }
}
