package ru.axetta.ecafe.processor.core.persistence.proactive;

import ru.axetta.ecafe.processor.core.partner.etpmv.enums.MessageType;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Date;

public class ProactiveMessage {
    private Long idofproaktivmessage;
    private Client client;
    private Client guardian;
    private Integer dtisznCode;
    private String servicenumber;
    private String ssoid;
    private StatusETPMessageType status;
    private MessageType message_type;
    private Date createddate;
    private Date lastupdate;

    public Long getIdofproaktivmessage() {
        return idofproaktivmessage;
    }

    public void setIdofproaktivmessage(Long idofproaktivmessage) {
        this.idofproaktivmessage = idofproaktivmessage;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getGuardian() {
        return guardian;
    }

    public void setGuardian(Client guardian) {
        this.guardian = guardian;
    }

    public String getServicenumber() {
        return servicenumber;
    }

    public void setServicenumber(String servicenumber) {
        this.servicenumber = servicenumber;
    }

    public String getSsoid() {
        return ssoid;
    }

    public void setSsoid(String ssoid) {
        this.ssoid = ssoid;
    }

    public StatusETPMessageType getStatus() {
        return status;
    }

    public void setStatus(StatusETPMessageType status) {
        this.status = status;
    }

    public MessageType getMessage_type() {
        return message_type;
    }

    public void setMessage_type(MessageType message_type) {
        this.message_type = message_type;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    public Integer getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Integer dtisznCode) {
        this.dtisznCode = dtisznCode;
    }
}
