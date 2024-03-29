/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.feed_plan;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.modal.group.GroupCreateListener;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 01.09.13
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ClientFeedActionPanel extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ClientFeedActionPanel.class);
    private List<ClientFeedActionListener> listeners;





    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    public void fill () {
    }




    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ClientFeedActionPanel.class).fill();
    }

    public void doPay() {
        /*ClientFeedActionEvent event = new ClientFeedActionEvent(ClientFeedActionEvent.PAY_CLIENT);
        for (ClientFeedActionListener l : listeners) {
            l.onClientFeedActionEvent(event);
        }*/
    }

    public void doBlock() {
        /*ClientFeedActionEvent event = new ClientFeedActionEvent(ClientFeedActionEvent.BLOCK_CLIENT);
        for (ClientFeedActionListener l : listeners) {
            l.onClientFeedActionEvent(event);
        }*/
    }

    public void doRelease() {
        /*ClientFeedActionEvent event = new ClientFeedActionEvent(ClientFeedActionEvent.RELEASE_CLIENT);
        for (ClientFeedActionListener l : listeners) {
            l.onClientFeedActionEvent(event);
        }*/
    }

    public void doPayAllClients() {
        /*ClientFeedActionEvent event = new ClientFeedActionEvent(ClientFeedActionEvent.PAY_CLIENT + ClientFeedActionEvent.ALL_CLIENTS);
        for (ClientFeedActionListener l : listeners) {
            l.onClientFeedActionEvent(event);
        }*/
    }

    public void doBlockAllClients() {
        /*ClientFeedActionEvent event = new ClientFeedActionEvent(ClientFeedActionEvent.BLOCK_CLIENT + ClientFeedActionEvent.ALL_CLIENTS);
        for (ClientFeedActionListener l : listeners) {
            l.onClientFeedActionEvent(event);
        }*/
    }

    public void doReleaseAllClients() {
        /*ClientFeedActionEvent event = new ClientFeedActionEvent(ClientFeedActionEvent.RELEASE_CLIENT + ClientFeedActionEvent.ALL_CLIENTS);
        for (ClientFeedActionListener l : listeners) {
            l.onClientFeedActionEvent(event);
        }*/
    }





    /**
     * ****************************************************************************************************************
     * Вспомогательные методы
     * ****************************************************************************************************************
     */
    public void addCallbackListener (BasicWorkspacePage page) {
        if (!(page instanceof ClientFeedActionListener)) {
            logger.error("Trying to add not listener for ClientFeedActionListener");
            return;
        }
        if (listeners == null) {
            listeners = new ArrayList<ClientFeedActionListener>();
        }
        listeners.clear();
        listeners.add((ClientFeedActionListener) page);
    }
}