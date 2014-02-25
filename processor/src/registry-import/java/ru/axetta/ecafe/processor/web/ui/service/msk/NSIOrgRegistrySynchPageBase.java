/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import generated.registry.manual_synch.FrontController;
import generated.registry.manual_synch.FrontControllerService;
import generated.registry.manual_synch.RegistryChangeItem;
import generated.registry.manual_synch.RegistryChangeErrorItem;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 01.10.13
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgRegistrySynchPageBase extends BasicWorkspacePage/* implements CompleteOrgSelectHandler*/{
    private static final int DISPLAY_ALL_MODE = 1;
    private static final int DISPLAY_COMMENTED_MODE = 2;
    private static final int DISPLAY_NON_COMMENTED_MODE = 3;
    private static final boolean ALLOW_TO_APPLY_PREVIOS_REVISIONS = false;
    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private boolean fullNameValidation = true;
    private String errorMessages;
    private String infoMessages;
    Logger logger = LoggerFactory.getLogger(NSIOrgRegistrySynchPageBase.class);
    private long revisionCreateDate;
    private List<Long> revisions;
    private List<RegistryChangeErrorItem> errors;
    private long idOfSelectedError;
    List <WebRegistryChangeItem> items;
    private String errorComment;
    private List<SelectItem> displayModes;
    private int displayMode;

    public String getPageFilename() {
        return "service/msk/nsi_org_registry_sync_page";
    }

    public String getPageDirectoryRoot() {
        return "/back-office/include";
    }

    public String getPageTitle() {
        return "Синхронизация организации с Реестрами";
    }

    public List<WebRegistryChangeItem> getItems() {
        return items;
    }
    
    public String getLineStyleClass(WebRegistryChangeItem item) {
        /*if (item.isApplied()) {
            return "disabledClientRow";
        }*/
        switch (item.getOperation()) {
            case ImportRegisterClientsService.CREATE_OPERATION:
                return "createClientRow";
            case ImportRegisterClientsService.DELETE_OPERATION:
                return "deleteClientRow";
            case ImportRegisterClientsService.MOVE_OPERATION:
                return "moveClientRow";
            case ImportRegisterClientsService.MODIFY_OPERATION:
                return "";
        }
        return "";
    }

    public void setItems(List<WebRegistryChangeItem> items) {
        this.items = items;
    }

    public List<SelectItem> getErrors() {
        if (errors == null || errors.size() < 1) {
            return Collections.EMPTY_LIST;
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for (RegistryChangeErrorItem i : errors) {
            if (displayMode == DISPLAY_COMMENTED_MODE &&
                (i.getComment() == null || i.getComment().length() < 1)) {
                continue;
            } else if (displayMode == DISPLAY_NON_COMMENTED_MODE &&
                       (i.getComment() != null && i.getComment().length() > 0)) {
                continue;
            }
            String msg = "№" + Org.extractOrgNumberFromName(i.getOrgName()) + " " +
                         df.format(new Date(i.getCreateDate()));
            items.add(new SelectItem(i.getIdOfRegistryChangeError(), msg));
        }
        return items;
    }

    public List<SelectItem> getRevisions() {
        if (revisions == null || revisions.size() < 1) {
            revisions = loadRevisions();
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for (long date : revisions) {
            if (items.size() < 1) {
                items.add(new SelectItem(date, "Последняя"));
            } else {
                items.add(new SelectItem(date, df.format(new Date(date))));
            }
        }
        return items;
    }

    @Override
    public void onShow() {
        revisions = loadRevisions();
        doUpdate();
        displayMode = DISPLAY_NON_COMMENTED_MODE;
    }

    public void doApply() {
        resetMessages();
        FrontController controller = createController(logger);
        if (controller == null) {
            logger.error("FrontControllerService is null");
            return;
        }
        
        
        List<Long> list = new ArrayList<Long>();
        for (WebRegistryChangeItem i : items) {
            if (i.isSelected()) {
                list.add(i.getIdOfRegistryChange());
            }
        }
        if (list.size() < 1) {
            return;
        }
        String error = controller.proceedRegitryChangeItemInternal(list, ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE, fullNameValidation);
        doUpdate();
        if (error == null) {
        } else {
            //  Ошибка
            errorMessages = error;
        }
    }

    public void doRefresh() {
        load(true);
    }

    public void doUpdate() {
        load(false);
        loadErrors();
    }

    public void doChangePanel(ValueChangeEvent event) {
        if (event.getNewValue().toString().equals("editErrorsPanel")) {
            loadErrors();
        }
    }

    public void doChangeErrorQuestion(ActionEvent actionEvent) {
        errorComment = "";
        RegistryChangeErrorItem e = getSelectedError();
        if (e != null && e.getComment() != null && e.getComment().length() > 0) {
            errorComment = e.getComment();
        }
    }

    public void doComment() {
        resetMessages();
        if (idOfSelectedError < 1L) {
            errorMessages = "Необходимо выбрать ошибку";
            return;
        }
        if (errorComment == null || errorComment.length() < 1) {
            errorMessages = "Необходимо заполнить комментарий";
            return;
        }
        FrontController controller = NSIOrgRegistrySynchPageBase.createController(logger);
        String author = "";
        try {
            author = MainPage.getSessionInstance().getCurrentUser().getUserName();
        } catch (Exception e) {
        }
        String error = controller.commentRegistryChangeErrorInternal(idOfSelectedError, errorComment, author);
        idOfSelectedError = -1L;
        errorComment = "";
        loadErrors();
        if (error == null) {
        } else {
            //  Ошибка
            errorMessages = error;
        }
    }
    
    public void doChangeDisplayMode(javax.faces.event.ActionEvent event) {
        idOfSelectedError = -1L;
        errorComment = "";
    }

    public void doMarkAll() {
        for (WebRegistryChangeItem i : items) {
            if (i.isApplied()) {
                continue;
            }
            i.setSelected(true);
        }
    }

    public void doUnmarkAll() {
        for (WebRegistryChangeItem i : items) {
            if (i.isApplied()) {
                continue;
            }
            i.setSelected(false);
        }
    }

    private void load (boolean refresh) {
        resetMessages();
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 1L) {
            if (items == null) {
                items = new ArrayList<WebRegistryChangeItem>();
            }
            items.clear();
            return;
        }


        //  Создание соединения со службой
        FrontController controller = createController(logger);
        if (controller == null) {
            logger.error("FrontControllerService is null");
            errorMessages = "Не удалось подключиться в веб-службе";
            return;
        }

        //  Выполнение запроса к службе
        List<RegistryChangeItem> changedItems = null;
        if (!refresh) {
            changedItems = controller.loadRegistryChangeItemsInternal(getIdOfOrg(), revisionCreateDate);
        } else {
            changedItems = controller.refreshRegistryChangeItemsInternal(getIdOfOrg());
        }
        items = new ArrayList<WebRegistryChangeItem>();
        for (RegistryChangeItem i : changedItems) {
            items.add(new WebRegistryChangeItem(i));
        }
        if (this.items.size() > 0) {
            revisionCreateDate = this.items.get(0).getCreateDate();
        } else {
            revisionCreateDate = -1L;
        }

        //  Если заново было произведена загрузка из Реестров, то обновляем список рагрузок
        if (refresh) {
            revisions = loadRevisions();
        }
    }

    private void loadErrors() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 0L) {
            if (errors == null) {
                errors = new ArrayList<RegistryChangeErrorItem>();
            }
            errors.clear();
        }


        //  Создание соединения со службой
        FrontController controller = createController(logger);
        if (controller == null) {
            logger.error("FrontControllerService is null");
            errorMessages = "Не удалось подключиться в веб-службе";
            return;
        }
        errors = controller.loadRegistryChangeErrorItemsInternal(getIdOfOrg());
    }

    private List<Long> loadRevisions () {
        //  Создание соединения со службой
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 1L) {
            if (items == null) {
                items = new ArrayList<WebRegistryChangeItem>();
            }
            items.clear();
            return Collections.EMPTY_LIST;
        }

        FrontController controller = createController(logger);
        if (controller == null) {
            logger.error("FrontControllerService is null");
            errorMessages = "Не удалось подключиться в веб-службе";
            return Collections.EMPTY_LIST;
        }

        //  Выполнение запроса к службе
        return controller.loadRegistryChangeRevisionsInternal(getIdOfOrg());
    }


    public static FrontController createController(Logger logger) {
        FrontController controller = null;
        try {
            FrontControllerService service = new FrontControllerService(new URL("http://localhost:8080/processor/soap/front?wsdl"),
                    new QName("http://ru.axetta.ecafe", "FrontControllerService"));
            controller = service.getFrontControllerPort();

            Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(10 * 60 * 1000);
            policy.setConnectionTimeout(10 * 60 * 1000);
            return controller;
        } catch (Exception e) {
            logger.error("Failed to intialize FrontControllerService", e);
            return null;
        }
    }


    public long getIdOfOrg() {
        return -1L;
    }

    public boolean getDisplayOrgSelection() {
        return false;
    }
    
    public int getTotalCount() {
        return items == null ? 0 : items.size();
    }

    public int getCreationsCount() {
        return getCountOfOperation(ImportRegisterClientsService.CREATE_OPERATION);
    }

    public int getDeletionsCount() {
        return getCountOfOperation(ImportRegisterClientsService.DELETE_OPERATION);
    }

    public int getMovesCount() {
        return getCountOfOperation(ImportRegisterClientsService.MOVE_OPERATION);
    }

    public int getModificationsCount() {
        return getCountOfOperation(ImportRegisterClientsService.MODIFY_OPERATION);
    }

    public Boolean isApplied(WebRegistryChangeItem item, boolean isTextMessage) {
        if (isPermittedRevision()) {
            //  если это строка
            if (isTextMessage) {
                return item.isApplied();
            } else {
                return true;
            }
        }
        return item.isApplied();
    }

    private boolean isPermittedRevision() {
        return (revisionCreateDate < 0 || revisions == null || revisions.get(0) == null ||
                revisionCreateDate != revisions.get(0)) && !ALLOW_TO_APPLY_PREVIOS_REVISIONS;
    }
    
    private int getCountOfOperation(int operation) {
        if (items == null) {
            return 0;
        }
        int count = 0;
        for (WebRegistryChangeItem i : items) {
            if (!i.isApplied() && i.getOperation() == operation) {
                count++;
            }
        }
        return count;
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

    public boolean getFullNameValidation() {
        return fullNameValidation;
    }

    public void setFullNameValidation(boolean fullNameValidation) {
        this.fullNameValidation = fullNameValidation;
    }

    public class WebRegistryChangeItem extends RegistryChangeItem {
        protected boolean selected;

        public WebRegistryChangeItem() {
            super();
            selected = false;
        }

        public WebRegistryChangeItem(RegistryChangeItem parent) {
            super();
            idOfRegistryChange = parent.getIdOfRegistryChange();
            idOfOrg            = parent.getIdOfOrg();
            firstName          = parent.getFirstName();
            secondName         = parent.getSecondName();
            surname            = parent.getSurname();
            groupName          = parent.getGroupName();

            idOfMigrateOrgFrom = parent.getIdOfMigrateOrgFrom();
            idOfMigrateOrgTo   = parent.getIdOfMigrateOrgTo();
            firstNameFrom      = parent.getFirstNameFrom();
            secondNameFrom     = parent.getSecondNameFrom();
            surnameFrom        = parent.getSurnameFrom();
            groupNameFrom      = parent.getGroupNameFrom();

            idOfClient         = parent.getIdOfClient();
            clientGUID         = parent.getClientGUID();
            createDate         = parent.getCreateDate();
            operation          = parent.getOperation();
            applied            = parent.isApplied();
            selected           = false;
        }

        public boolean isSelected() {
            return selected;
        }

        public boolean getSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean getFullnameChangeExists() {
            return operation == ImportRegisterClientsService.MODIFY_OPERATION &&
                   (!firstName.equals(firstNameFrom) ||
                    !secondName.equals(secondNameFrom) ||
                    !surname.equals(surnameFrom));
        }

        public boolean getGroupChangeExists() {
            return operation == ImportRegisterClientsService.MODIFY_OPERATION &&
                   !groupName.equals(groupNameFrom);
        }
        
        public String getFullname() {
            return surname + " " + firstName + " " + secondName;
        }
        
        public String getPrevFullname() {
            return surnameFrom + " " + firstNameFrom + " " + secondNameFrom;
        }
        
        public String getOperationName() {
            switch (operation) {
                case ImportRegisterClientsService.CREATE_OPERATION:
                    return "создание";
                case ImportRegisterClientsService.DELETE_OPERATION:
                    return "удаление";
                case ImportRegisterClientsService.MODIFY_OPERATION:
                    return "изменение";
                case ImportRegisterClientsService.MOVE_OPERATION:
                    return "премещение";
                default:
                    return "неизвестно";
            }
        }

        public String getMigrateFromOrgName() {
            return operation == ImportRegisterClientsService.MOVE_OPERATION ?
                        DAOService.getInstance().findOrById(getIdOfMigrateOrgFrom()).getOfficialName() : "";
        }
    }

    public long getRevisionCreateDate() {
        return revisionCreateDate;
    }

    public void setRevisionCreateDate(long revisionCreateDate) {
        this.revisionCreateDate = revisionCreateDate;
    }

    public boolean getShowErrorEditPanel () {
        return true;
    }

    public long getIdOfSelectedError() {
        return idOfSelectedError;
    }

    public void setIdOfSelectedError(long idOfSelectedError) {
        this.idOfSelectedError = idOfSelectedError;
    }
    
    public String getErrorMessage() {
        RegistryChangeErrorItem sel = getSelectedError();
        if (sel == null) {
            return "";
        }
        return "Описание: " + sel.getError() + "\r\n\r\nКомментарии: " + sel.getErrorDetail();
    }

    public boolean getSelectedErrorEditable() {
        RegistryChangeErrorItem sel = getSelectedError();
        return sel != null && (sel.getComment() == null || sel.getComment().length() < 1) ? true : false;
    }

    public String getErrorComment() {
        return errorComment;
    }

    public void setErrorComment(String errorComment) {
        this.errorComment = errorComment;
    }
    
    public String getCommentInfo() {
        RegistryChangeErrorItem error = getSelectedError();
        if (error == null || error.getComment() == null || error.getComment().length() < 1) {
            return "";
        }
        return " исправлено " + error.getCommentAuthor() + ", " +
               df.format(new Date(error.getCommentCreateDate()));
    }

    public RegistryChangeErrorItem getSelectedError() {
        if (errors == null || errors.size() < 1) {
            return null;
        }
        RegistryChangeErrorItem sel = null;
        for (RegistryChangeErrorItem i : errors) {
            if (i.getIdOfRegistryChangeError() == idOfSelectedError) {
                sel = i;
                break;
            }
        }
        return sel;
    }
    
    public List<SelectItem> getDisplayModes() {
        if (displayModes != null) {
            return displayModes;
        }
        displayModes = new ArrayList<SelectItem>();
        displayModes.add(new SelectItem(DISPLAY_ALL_MODE, "Все"));
        displayModes.add(new SelectItem(DISPLAY_COMMENTED_MODE, "Обработанные"));
        displayModes.add(new SelectItem(DISPLAY_NON_COMMENTED_MODE, "Не обработанные"));
        return displayModes;
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }

    public String getResultTitle() {
        if (revisions == null || revisions.size() < 1 || revisionCreateDate < 1) {
            return "";
        }

        if (revisions.get(0).longValue() == revisionCreateDate) {
            return "последней сверки";
        } else {
            return "сверки от " + df.format(new Date(revisionCreateDate));
        }
    }
}