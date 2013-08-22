/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.discount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.dao.DAOServices;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.ClientListEditPage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.07.13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SetupDiscountPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(SetupDiscountPage.class);

    @PersistenceContext
    private EntityManager entityManager;
    private Org org;
    private String errorMessages;
    private String infoMessages;
    private List<String> groups;
    private String group;
    private List<Client> clients;
    private List<DiscountColumn> columns;




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

    @Transactional
    public void fill() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load discounts data", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session) throws Exception {
        loadGroups(session);
        buildColumns(session);
    }

    @Transactional
    public void loadGroups() throws Exception {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadGroups(session);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadGroups(Session session) {
        groups = DAOServices.getInstance().loadGroups(session, getOrg().getIdOfOrg(), true);
        Collections.sort(groups, new ClientListEditPage.ClientComparator ());
    }

    public void buildColumns (Session session) {
        if (columns != null) {
            return;
        }

        columns = new ArrayList<DiscountColumn>();
        Map<Long, String> categories = DAOServices.getInstance().loadDiscountCategories(session);
        for (Long id : categories.keySet()) {
            String title = categories.get(id);
            columns.add(new DiscountColumn(id, title));
        }
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).fill();
    }

    public void doApply () {

    }

    public void doCancel () {

    }

    public void doExportToExcel () {

    }

    public void doChangeGroup () {

    }

    public List<SelectItem> getGroups() throws Exception {
        if (groups == null) {
            loadGroups();
        }
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (String group : groups) {
            res.add(new SelectItem(group, group));
        }
        return res;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<DiscountColumn> getColumns() {
        return columns;
    }






    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "discount/setup_discount";
    }

    public String getPageTitle() {
        return "Заявки на питание";
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }


    public static class Client {
        private String firstName;
        private String secondName;
        private String surname;
        private Map<Long, Boolean> rules;
        
        public Client (String firstName, String secondName, String surname) {
            this.firstName = firstName;
            this.secondName = secondName;
            this.surname = surname;
        }
        
        public String getFullName () {
            String n = surname;
            if (firstName != null && firstName.length() > 0) {
                n += " " + firstName;
            }
            if (secondName != null && secondName.length() > 0) {
                n += " " + secondName;
            }
            return n;
        }

        public Map<Long, Boolean> getRules() {
            return rules;
        }

        public void setRules(Map<Long, Boolean> rules) {
            this.rules = rules;
        }
    }


    public static class DiscountColumn {
        private long id;
        private String title;

        public DiscountColumn (long id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
