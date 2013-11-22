/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.ClientMigrationItemInfo;
import ru.axetta.ecafe.processor.core.client.ClientService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.01.13
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ClientMigrationPage extends BasicWorkspacePage{

    private Long idOfClient;
    protected List<ClientMigrationItemInfo> clientMigrationItemInfoList;
    @Autowired
    private ClientService clientService;

    @Override
    public void onShow() throws Exception {
         reload();
    }

    protected void reload(){
        clientMigrationItemInfoList = clientService.reloadMigrationInfoByClient(idOfClient);
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public List<ClientMigrationItemInfo> getClientMigrationItemInfoList() {
        return clientMigrationItemInfoList;
    }

    @Override
    public String getPageFilename() {
        return "client/client_migration";
    }
}
