/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import java.io.Serializable;

public class MoveClientToGroup implements Serializable {
    private Long idOfClient;
    private Long idOfClientGroup;
    private Long idOfOrg;
    private Long idOfMiddleGroup;

    public Long getIdOfClient() { return idOfClient; }

    public void setIdOfClient(Long idOfClient) { this.idOfClient = idOfClient; }

    public Long getIdOfClientGroup() { return idOfClientGroup; }

    public void setIdOfClientGroup(Long idOfClientGroup) { this.idOfClientGroup = idOfClientGroup; }

    public Long getIdOfOrg() { return idOfOrg; }

    public void setIdOfOrg(Long idOfOrg) { this.idOfOrg = idOfOrg; }

    public Long getIdOfMiddleGroup() { return idOfMiddleGroup; }

    public void setIdOfMiddleGroup(Long idOfMiddleGroup) { this.idOfMiddleGroup = idOfMiddleGroup; }

    @Override
    public String toString() {
        return "MoveClientToGroup{" + "idOfClient=" + idOfClient + ", idOfClientGroup=" + idOfClientGroup + ", idOfOrg=" + idOfOrg
                + ", idOfMiddleGroup=" + idOfMiddleGroup + '}';
    }
}
