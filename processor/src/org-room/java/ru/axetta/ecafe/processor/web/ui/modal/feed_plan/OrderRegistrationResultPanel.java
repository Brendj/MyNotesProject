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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 03.09.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class OrderRegistrationResultPanel extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(OrderRegistrationResultPanel.class);
    private List<OrderRegistrationResultListener> listeners;
    private List<Info> infos;





    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    public void fill () {
    }

    public void setClientSaveMessages(Map<FeedPlanPage.Client, String> result) {
        if (infos == null) {
            infos = new ArrayList<Info>();
        }
        infos.clear();

        for (FeedPlanPage.Client cl : result.keySet()) {
            infos.add(new Info(cl.getIdofclient(), cl.getFullName(), result.get(cl), cl.getIdoforder() != null));
        }
    }




    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(OrderRegistrationResultPanel.class).fill();
    }

    public void doClose () {

    }

    public List<Info> getInfos() {
        if (infos == null) {
            return Collections.EMPTY_LIST;
        }
        return infos;
    }

    public int getTotalCount() {
        if (infos == null) {
            return 0;
        }
        return infos.size();
    }
    
    public int getSuccessCount() {
        if (infos == null) {
            return 0;
        }

        int res = 0;
        for (Info i : infos) {
            if (i.isSuccess()) {
                res++;
            }
        }
        return res;
    }

    public int getFailCount() {
        if (infos == null) {
            return 0;
        }

        int res = 0;
        for (Info i : infos) {
            if (!i.isSuccess()) {
                res++;
            }
        }
        return res;
    }





    /**
     * ****************************************************************************************************************
     * Вспомогательные методы
     * ****************************************************************************************************************
     */
    public void addCallbackListener (BasicWorkspacePage page) {
        if (!(page instanceof OrderRegistrationResultListener)) {
            logger.error("Trying to add not listener for OrderRegistrationResultListener");
            return;
        }
        if (listeners == null) {
            listeners = new ArrayList<OrderRegistrationResultListener>();
        }
        listeners.clear();
        listeners.add((OrderRegistrationResultListener) page);
    }


    public class Info {
        private long idofclient;
        private String name;
        private String message;
        private boolean success;


        public Info(long idofclient, String name, String message, boolean success) {
            this.idofclient = idofclient;
            this.name = name;
            this.message = message;
            this.success = success;
        }

        public long getIdofclient() {
            return idofclient;
        }

        public void setIdofclient(long idofclient) {
            this.idofclient = idofclient;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getStyleClass() {
            return success ? "successMessage" : "failMessage";
        }
    }
}
