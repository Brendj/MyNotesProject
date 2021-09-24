/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianHistory;
import ru.axetta.ecafe.processor.core.persistence.Person;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientCreateByCardOperatorPage extends ClientCreatePage {

    private Long newContractId;
    public String getPageFilename() {
        return "client/createByCardOperator";
    }
    public Long BALANCE_TO_NOTIFY_DEFAULT = 15000L;

    public Client createClient(Session persistenceSession, ClientGuardianHistory clientGuardianHistory) throws Exception {
        if (getIdOfClientGroup() == null) {
            throw new Exception("Выберите группу!");
        }
        this.setPlainPassword("");
        PersonItem pItem = new PersonItem(new Person("", "", ""));
        pItem.setIdDocument("");
        setContractPerson(pItem);
        setAddress("");
        setBalanceToNotify(BALANCE_TO_NOTIFY_DEFAULT);
        getPerson().setIdDocument("");
        Client client = super.createClient(persistenceSession, clientGuardianHistory);
        client.setCypheredPasswordByCardOperator(Client.encryptPassword("" + client.getContractId()));
        newContractId = client.getContractId();
        return client;
    }

    public Long getNewContractId() {
        return newContractId;
    }

    public void setNewContractId(Long newContractId) {
        this.newContractId = newContractId;
    }

    public Boolean getAllowRegisterCard() {
        return newContractId != null;
    }

}