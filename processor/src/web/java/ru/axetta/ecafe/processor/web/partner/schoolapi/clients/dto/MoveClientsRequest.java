/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MoveClientsRequest implements Serializable {
    private List<MoveClientToGroup> moveClients;

    public MoveClientsRequest() {
        this.moveClients = new ArrayList<>();
    }

    public List<MoveClientToGroup> getMoveClients() {
        return moveClients;
    }

    public void setMoveClients(List<MoveClientToGroup> moveClients) {
        this.moveClients = moveClients;
    }
}
