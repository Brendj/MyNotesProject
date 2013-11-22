/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.feed_plan;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.feed.FeedPlanPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 05.09.13
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ReplaceClientPanel extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ReplaceClientPanel.class);
    private List<ReplaceClientListener> listeners;
    private List<FeedPlanPage.ReplaceClient> replaceClients;
    private FeedPlanPage.Client targetClient;





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

    public void doSelect(FeedPlanPage.ReplaceClient selected) {
        //  Если это какой-то клиент, у которого уже есть привязка к другому клиенту, то сне даем его выбирать
        if (selected.getIdOfTargetClient() != null &&
            selected.getIdOfTargetClient().longValue() != targetClient.getIdofclient()) {
            return;
        }

        //  Ищем сейчас выбранного клиента
        for (FeedPlanPage.ReplaceClient cl : replaceClients) {
            if (cl.getIdofclient() == selected.getIdofclient()) {
                continue;
            }
            if (cl.getIdOfTargetClient() != null &&
                cl.getIdOfTargetClient().longValue() == targetClient.getIdofclient()) {
                cl.setIdOfTargetClient(null);
            }
        }

        if (selected.getIdOfTargetClient() == null) {
            selected.setIdOfTargetClient(targetClient.getIdofclient());
        } else {
            selected.setIdOfTargetClient(null);
        }
    }

    public void doReset () {
        for (FeedPlanPage.ReplaceClient cl : replaceClients) {
            if (cl.getIdOfTargetClient() != null &&
                cl.getIdOfTargetClient().longValue() == targetClient.getIdofclient()) {
                cl.setIdOfTargetClient(null);
                break;
            }
        }
    }

    public void doClose () {
        FeedPlanPage.ReplaceClient selected = null;
        for (FeedPlanPage.ReplaceClient cl : replaceClients) {
            if (cl.getIdOfTargetClient() != null &&
                cl.getIdOfTargetClient().longValue() == targetClient.getIdofclient()) {
                selected = cl;
                break;
            }
        }

        //  Отсылаем событие
        ReplaceClientEvent event = new ReplaceClientEvent(selected, targetClient);
        for (ReplaceClientListener l : listeners) {
            l.onReplaceClientEvent(event);
        }
    }
    
    public String getStyleClass(FeedPlanPage.ReplaceClient cl) {
        if (cl.getIdOfTargetClient() != null &&
            cl.getIdOfTargetClient().longValue() == targetClient.getIdofclient()) {
            return "selected";
        } else if (cl.getIdOfTargetClient() != null &&
                   cl.getIdOfTargetClient().longValue() != targetClient.getIdofclient()) {
            return "disabled";
        }
        return "";
    }
    
    public String getReplaceTargetName (FeedPlanPage.ReplaceClient cl) {
        if (cl.getIdOfTargetClient() != null &&
            cl.getIdOfTargetClient().longValue() != targetClient.getIdofclient()) {
            return cl.getNameOfTargetClient();
        }
        return "";
    }

    public void setClients(List<FeedPlanPage.ReplaceClient> replaceClients,
                           FeedPlanPage.Client targetClient) {
        this.replaceClients = replaceClients;
        this.targetClient = targetClient;
    }

    public String getNameOfTargetClient() {
        if (targetClient == null) {
            return "";
        }
        return targetClient.getFullNameWithoutBreaks();
    }

    public List<FeedPlanPage.ReplaceClient> getReplaceClients() {
        return replaceClients;
    }





    /**
     * ****************************************************************************************************************
     * Вспомогательные методы
     * ****************************************************************************************************************
     */
    public void addCallbackListener (BasicWorkspacePage page) {
        if (!(page instanceof ReplaceClientListener)) {
            logger.error("Trying to add not listener for ReplaceClientListener");
            return;
        }
        if (listeners == null) {
            listeners = new ArrayList<ReplaceClientListener>();
        }
        listeners.clear();
        listeners.add((ReplaceClientListener) page);
    }
}
