/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import java.util.List;

/**
 * Created by i.semenov on 23.05.2018.
 */
public class ClientGuardianResult extends Result {
    private List<ClientGuardian> clientGuardian;

    public List<ClientGuardian> getClientGuardian() {
        return clientGuardian;
    }

    public void setClientGuardian(List<ClientGuardian> clientGuardian) {
        this.clientGuardian = clientGuardian;
    }
}
