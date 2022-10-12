package ru.axetta.ecafe.processor.core.persistence.proactive;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import java.util.Date;

public class ProactiveMessageStatus {
    private Long idofproaktivmessagestatus;
    private ProactiveMessage proactiveMessage;
    private StatusETPMessageType status;
    private Date createddate;

    public Long getIdofproaktivmessagestatus() {
        return idofproaktivmessagestatus;
    }

    public void setIdofproaktivmessagestatus(Long idofproaktivmessagestatus) {
        this.idofproaktivmessagestatus = idofproaktivmessagestatus;
    }

    public ProactiveMessage getProactiveMessage() {
        return proactiveMessage;
    }

    public void setProactiveMessage(ProactiveMessage proactiveMessage) {
        this.proactiveMessage = proactiveMessage;
    }

    public StatusETPMessageType getStatus() {
        return status;
    }

    public void setStatus(StatusETPMessageType status) {
        this.status = status;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }
}
