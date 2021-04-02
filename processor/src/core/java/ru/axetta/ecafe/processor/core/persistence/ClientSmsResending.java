/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 12.12.14
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public class ClientSmsResending {
    private String idOfSms;
    private long version;
    private Client client;
    private String phone;
    private String serviceName;
    private Long contentsId;
    private Integer contentsType;
    private String textContents;
    private String paramsContents;
    private Date createDate;
    private Date lastResendingDate;
    private Date eventTime;
    private Long idOfOrg;
    private String nodeName;

    public ClientSmsResending() {
        // For Hibernate only
    }

    public ClientSmsResending(String idOfSms, long version, Client client, String phone, String serviceName,
            Long contentsId, Integer contentsType, String textContents, String paramsContents, Date createDate,
            Date lastResendingDate, Date eventTime, Long idOfOrg) {
        this.idOfSms = idOfSms;
        this.version = version;
        this.client = client;
        this.phone = phone;
        this.serviceName = serviceName;
        this.contentsId = contentsId;
        this.contentsType = contentsType;
        this.textContents = textContents;
        this.paramsContents = paramsContents;
        this.createDate = createDate;
        this.lastResendingDate = lastResendingDate;
        this.eventTime = eventTime;
        this.idOfOrg = idOfOrg;
    }

    public String getIdOfSms() {
        return idOfSms;
    }

    public void setIdOfSms(String idOfSms) {
        this.idOfSms = idOfSms;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getContentsId() {
        return contentsId;
    }

    public void setContentsId(Long contentsId) {
        this.contentsId = contentsId;
    }

    public Integer getContentsType() {
        return contentsType;
    }

    public void setContentsType(Integer contentsType) {
        this.contentsType = contentsType;
    }

    public String getTextContents() {
        return textContents;
    }

    public void setTextContents(String textContents) {
        this.textContents = textContents;
    }

    public String getParamsContents() {
        return paramsContents;
    }

    public void setParamsContents(String paramsContents) {
        this.paramsContents = paramsContents;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastResendingDate() {
        return lastResendingDate;
    }

    public void setLastResendingDate(Date lastResendingDate) {
        this.lastResendingDate = lastResendingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClientSmsResending that = (ClientSmsResending) o;

        if (version != that.version) {
            return false;
        }
        if (client != null ? !client.equals(that.client) : that.client != null) {
            return false;
        }
        if (contentsId != null ? !contentsId.equals(that.contentsId) : that.contentsId != null) {
            return false;
        }
        if (contentsType != null ? !contentsType.equals(that.contentsType) : that.contentsType != null) {
            return false;
        }
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) {
            return false;
        }
        if (idOfSms != null ? !idOfSms.equals(that.idOfSms) : that.idOfSms != null) {
            return false;
        }
        if (lastResendingDate != null ? !lastResendingDate.equals(that.lastResendingDate)
                : that.lastResendingDate != null) {
            return false;
        }
        if (paramsContents != null ? !paramsContents.equals(that.paramsContents) : that.paramsContents != null) {
            return false;
        }
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) {
            return false;
        }
        if (serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null) {
            return false;
        }
        if (textContents != null ? !textContents.equals(that.textContents) : that.textContents != null) {
            return false;
        }

        return true;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    @Override
    public int hashCode() {
        int result = idOfSms != null ? idOfSms.hashCode() : 0;
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
        result = 31 * result + (contentsId != null ? contentsId.hashCode() : 0);
        result = 31 * result + (contentsType != null ? contentsType.hashCode() : 0);
        result = 31 * result + (textContents != null ? textContents.hashCode() : 0);
        result = 31 * result + (paramsContents != null ? paramsContents.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (lastResendingDate != null ? lastResendingDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientSmsResending{" +
                "idOfSms='" + idOfSms + '\'' +
                ", version=" + version +
                ", client=" + client +
                ", phone='" + phone + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", contentsId=" + contentsId +
                ", contentsType=" + contentsType +
                ", textContents='" + textContents + '\'' +
                ", paramsContents='" + paramsContents + '\'' +
                ", createDate=" + createDate +
                ", lastResendingDate=" + lastResendingDate +
                '}';
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}