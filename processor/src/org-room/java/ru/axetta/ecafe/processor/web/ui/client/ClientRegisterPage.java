/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.dao.DAOServices;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.OrgRoomMainPage;
import ru.axetta.ecafe.processor.web.ui.auth.LoginBean;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.ExternalContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 07.08.13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ClientRegisterPage extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ClientListEditPage.class);
    private String errorMessages;
    private String infoMessages;
    private Org org;
    private List<String> groups;
    private List<RegisterClient> clientsForRegister;

    private boolean showAddress;
    private boolean showPhone;
    private boolean showMobile;
    private boolean showFax;
    private boolean showEmail;
    private boolean showRemarks;
    private boolean registerTwins;


    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public void fill(boolean reset) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session, reset);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session, boolean reset) throws Exception {
        clientsForRegister = new ArrayList<RegisterClient>();
        clientsForRegister.add(new RegisterClient());
        loadGroups(session);
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
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        groups = DAOServices.getInstance().loadGroups(session, org.getIdOfOrg());
        Collections.sort(groups, new ClientListEditPage.ClientComparator ());
    }

    @Transactional
    public void insertClients () {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
            for (RegisterClient client : clientsForRegister) {
                if (!isClientModified(client) || client.isAdded()) {
                    continue;
                }
                client.removeMessages();
                try {
                    ClientsMobileHistory clientsMobileHistory =
                            new ClientsMobileHistory("регистрация клиетов через Клиенты/регистрация (org-room)");
                    registerClient(session, client, org, registerTwins, clientsMobileHistory);
                    client.setAdded(true);
                    client.setInfo("Клиент успешно зарегистрирован");
                    //session.flush();
                } catch (Exception e) {
                    client.setError(e.getMessage());
                    logger.error("Не удалось добавить клиента " + client, e);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ClientRegisterPage.class).fill(true);
    }

    public List<RegisterClient> getClientsForRegister() {
        return clientsForRegister;
    }

    public void doModifyClient(ActionEvent actionEvent) {
        /*RegisterClient lastClient = clientsForRegister.get(clientsForRegister.size() - 1);
        if (!lastClient.isNewClientAdded()) {
            if (isClientModified(lastClient)) {
                clientsForRegister.add(new RegisterClient());
                lastClient.setNewClientAdded(true);
            }
        }*/
    }

    public void doRemoveClient (RegisterClient client) {
        if (isClientModified(client)) {
            clientsForRegister.remove(client);
        }
    }

    public void doApply () {
        RuntimeContext.getAppContext().getBean(ClientRegisterPage.class).insertClients();
    }

    public void doAddEmptyClient() {
        clientsForRegister.add(new RegisterClient());
    }

    public void doClearClients() {
        clientsForRegister.clear();
        clientsForRegister.add(new RegisterClient());
    }

    private boolean isClientModified (RegisterClient client) {
        if (client == null) {
            return false;
        }
        return emptyIfNull(client.getSurname()).length() > 0 ||
                emptyIfNull(client.getFirstName()).length() > 0 ||
                emptyIfNull(client.getSecondName()).length() > 0 ||
                emptyIfNull(client.getClientGroup()).length() > 0 ||
                emptyIfNull(client.getAddress()).length() > 0 ||
                emptyIfNull(client.getPhone()).length() > 0 ||
                emptyIfNull(client.getMobile()).length() > 0 ||
                emptyIfNull(client.getFax()).length() > 0 ||
                emptyIfNull(client.getEmail()).length() > 0 ||
                emptyIfNull(client.getRemarks()).length() > 0;
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

    public boolean getShowAddress() {
        return showAddress;
    }

    public void setShowAddress(boolean showAddress) {
        this.showAddress = showAddress;
    }

    public boolean getShowPhone() {
        return showPhone;
    }

    public void setShowPhone(boolean showPhone) {
        this.showPhone = showPhone;
    }

    public boolean getShowMobile() {
        return showMobile;
    }

    public void setShowMobile(boolean showMobile) {
        this.showMobile = showMobile;
    }

    public boolean getShowFax() {
        return showFax;
    }

    public void setShowFax(boolean showFax) {
        this.showFax = showFax;
    }

    public boolean getShowEmail() {
        return showEmail;
    }

    public void setShowEmail(boolean showEmail) {
        this.showEmail = showEmail;
    }

    public boolean getShowRemarks() {
        return showRemarks;
    }

    public void setShowRemarks(boolean showRemarks) {
        this.showRemarks = showRemarks;
    }

    public boolean isRegisterTwins() {
        return registerTwins;
    }

    public void setRegisterTwins(boolean registerTwins) {
        this.registerTwins = registerTwins;
    }





    /**
     * ****************************************************************************************************************
     * Работа с данными
     * ****************************************************************************************************************
     */
    public static void registerClient (Session session, ClientListEditPage.SelectedClient client, Org org,
            ClientsMobileHistory clientsMobileHistory) throws Exception {
        registerClient(session, client, org, false, clientsMobileHistory);
    }

    public static void registerClient (Session session, ClientListEditPage.SelectedClient client, Org org,
            boolean checkFullname, ClientsMobileHistory clientsMobileHistory) throws Exception {
        if (org == null) {
            org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);
        }
        if (session == null || !session.isConnected()) {
            throw new Exception("Отсутствует соединение с базой данных");
        }
        if (client == null || org == null) {
            throw new Exception("Отсутствуют обязательные данные");
        }


        ru.axetta.ecafe.processor.core.persistence.Client cl = null;
        if (client.getIdOfClient() != null) {
            cl = (ru.axetta.ecafe.processor.core.persistence.Client) session
                    .get(ru.axetta.ecafe.processor.core.persistence.Client.class, client.getIdOfClient());
        }


        boolean updateRequired = false;
        FieldProcessor.Config fieldConfig;
        if (cl == null) {
            fieldConfig = new ClientManager.ClientFieldConfig();
        } else {
            fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
        }


        //  Вставляем поля в структуру обновления и одновременно проверяем, надо ли обновлять клиента
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.SURNAME, client.getSurname(),
                cl == null ? null : cl.getPerson().getSurname(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.NAME, client.getFirstName(),
                cl == null ? null : cl.getPerson().getFirstName(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.SECONDNAME, client.getSecondName(),
                cl == null ? null : cl.getPerson().getSecondName(), updateRequired);
        if (client.getClientGroup() != null) {
            updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP, client.getClientGroup(),
                    cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(), updateRequired);
        }
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.ADDRESS, client.getAddress(),
                cl == null ? null : cl.getAddress(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.PHONE, client.getPhone(),
                cl == null ? null : cl.getPhone(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.FAX, client.getFax(),
                cl == null ? null : cl.getFax(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.MOBILE_PHONE, client.getMobile(),
                cl == null ? null : cl.getMobile(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.EMAIL, client.getEmail(),
                cl == null ? null : cl.getEmail(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.COMMENTS, client.getRemarks(),
                cl == null ? null : cl.getRemarks(), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.NOTIFY_BY_EMAIL,
                (client.getNotifyViaEmail() != null && client.getNotifyViaEmail()) ? "1" : "0",
                cl == null ? null : (cl.isNotifyViaEmail() ? "1" : "0"), updateRequired);
        updateRequired = doClientUpdate(fieldConfig, ClientManager.FieldId.NOTIFY_BY_SMS,
                (client.getNotifyViaSMS() != null && client.getNotifyViaSMS()) ? "1" : "0",
                cl == null ? null : (cl.isNotifyViaSMS() ? "1" : "0"), updateRequired);

        Long newIdOfClient = null;
        try {
            if (cl == null) {
                Client clientNew = ClientManager
                        .registerClientTransactionFree(org.getIdOfOrg(), (ClientManager.ClientFieldConfig) fieldConfig,
                                !checkFullname, session, null, clientsMobileHistory);
                newIdOfClient = clientNew.getIdOfClient();
            } else {
                newIdOfClient = ClientManager
                        .modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) fieldConfig, org, "",
                                cl, session, clientsMobileHistory);
            }
        } catch (Exception e) {
            throw e;
        }


        if (newIdOfClient != null && cl == null) {
            cl = (ru.axetta.ecafe.processor.core.persistence.Client) session
                    .get(ru.axetta.ecafe.processor.core.persistence.Client.class, newIdOfClient);
        }
        List<Long> idOfCategoryList = new ArrayList<Long>();
        if (client.getDiscounts() == null || client.getDiscounts().size() < 1) {
            client.initDiscounts(ClientListEditPage.loadDiscounts(session));
        }
        for (Long idofcategorydiscount : client.getDiscounts().keySet()) {
            if (client.getDiscounts().get(idofcategorydiscount).equals(Boolean.FALSE)) {
                continue;
            }
            idOfCategoryList.add(idofcategorydiscount);
        }
        if (idOfCategoryList.size() > 0) {
            //  Если льготы есть, то устанавливаем их и свойство discountmode
            ClientManager.setCategories(session, cl, idOfCategoryList, Client.DISCOUNT_MODE_BY_CATEGORY);
        } else {
            //  Если льгот нет, то скидываем их и меняем свойство discountmode
            ClientManager.setCategories(session, cl, Collections.EMPTY_LIST, Client.DISCOUNT_MODE_NONE);
        }
    }

    public static boolean doClientUpdate(FieldProcessor.Config fieldConfig, Object fieldID, String reesterValue,
            String currentValue, boolean doClientUpdate) throws Exception {
        reesterValue = emptyIfNull(reesterValue);
        currentValue = emptyIfNull(currentValue);
        fieldConfig.setValue(fieldID, reesterValue);
        return doClientUpdate || !currentValue.trim().equals(reesterValue.trim());
    }

    private static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }








    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "client/client_register";
    }

    public String getPageTitle() {
        return "Регистрация клиентов";
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


    public static class RegisterClient extends ClientListEditPage.SelectedClient {
        private boolean newClientAdded = false;
        private String error;
        private String info;
        private boolean isAdded;

        public boolean isNewClientAdded() {
            return newClientAdded;
        }

        public void setNewClientAdded(boolean newClientAdded) {
            this.newClientAdded = newClientAdded;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void removeMessages () {
            error = null;
            info = null;
        }

        public List<Message> getMessages() {
            ArrayList<Message> list = new ArrayList<Message>();
            if (error != null && error.length() > 0) {
                list.add(new Message("Ошибка: " + error));
            }
            if (info != null && info.length() > 0) {
                list.add(new Message(info));
            }
            return list;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getColor () {
            if (emptyIfNull(error).length() > 0) {
                return "#FFD6D6";
            } else if (emptyIfNull(info).length() > 0) {
                return "#D6FFD6";
            } else {
                return "";
            }
        }

        public boolean isAdded() {
            return isAdded;
        }

        public void setAdded(boolean added) {
            isAdded = added;
        }
    }

    public static class Message {
        private String message;
        
        public Message (String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
