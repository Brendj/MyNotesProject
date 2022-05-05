package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianRepresentType;

import java.util.Date;

public class StudentItem {
    private Long idOfClient;
    private String name;
    private Date birthDate;
    private String className;
    private String orgShortName;

    public StudentItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.name = client.getPerson().getFullName();
        this.className = client.getClientGroup().getGroupName();
        this.birthDate = client.getBirthDate();
        this.orgShortName = client.getOrg().getShortName();
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }
}
