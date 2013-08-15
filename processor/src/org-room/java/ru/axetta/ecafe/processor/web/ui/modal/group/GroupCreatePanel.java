/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.richfaces.component.UIModalPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 12.08.13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class GroupCreatePanel extends BasicWorkspacePage {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(GroupCreatePanel.class);
    private Org org;
    private String errorMessages;
    private String infoMessages;
    private String groupName;
    private boolean created;
    private GroupCreateEvent event;
    private List<GroupCreateListener> listeners;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public Org getOrg() {
        if (org != null) {
            return org;
        }
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return getOrg(session);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Org getOrg(Session session) {
        if (org != null) {
            return org;
        }
        org = (Org) session.get(Org.class, 0L);
        return org;
    }
    
    
    public void fill () {
        event = null;
        listeners = new ArrayList<GroupCreateListener>();
        groupName = "";
        created = false;
        resetMessages ();
        sendInfo("Введите наименование группы в формате 1А или 1-А");
    }


    @Transactional
    public void createClientGroup() {
        created = false;
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            event = createClientGroup(session);
            sendInfo("Группа '" + groupName + "' успешно создана");
            created = true;
            groupName = "";
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
            sendError("Неудалось создать группу: " + e.getMessage());
            event = new GroupCreateEvent(groupName, false);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    private GroupCreateEvent createClientGroup (Session session) throws Exception {
        resetMessages();

        if (groupName == null || groupName.length() < 1) {
            throw new Exception("Необходимо указать наименование группы");
        }
        if (!groupName.matches("[0-9]{1,2}-?[а-яА-Я]")) {
            throw new Exception("Неправильный формат наименования группы");
        }
        groupName = groupName.toUpperCase();
        groupName = groupName.replaceAll("-", "");

        ClientGroup grp = DAOUtils.createClientGroup(session, getOrg(session).getIdOfOrg(), groupName);
        if (grp == null) {
            throw new Exception("Неудалось создать группу '" + groupName + "', попробуйте повторить попытку позже");
        }

        //  Для каждого листнера сообщаем, что было выполнено событие добавления группы
        GroupCreateEvent event = new GroupCreateEvent(groupName, true);
        return event;
    }




    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(GroupCreatePanel.class).fill();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void doApply () {
        RuntimeContext.getAppContext().getBean(GroupCreatePanel.class).createClientGroup();
    }

    public void doClose () {
        if (event != null) {
            for (GroupCreateListener listener : listeners) {
                listener.onGroupCreateEvent(event);
            }
        }
    }

    public void doOnShow () {
        RuntimeContext.getAppContext().getBean(GroupCreatePanel.class).fill();
    }

    public boolean getCreated() {
        return created;
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }




    /**
     * ****************************************************************************************************************
     * Вспомогательные методы
     * ****************************************************************************************************************
     */
    public void addCallbackListener (BasicWorkspacePage page) {
        if (!(page instanceof GroupCreateListener)) {
            logger.error("Trying to add not listener for GroupCreatePanel");
            return;
        }
        listeners.add((GroupCreateListener) page);
    }
}