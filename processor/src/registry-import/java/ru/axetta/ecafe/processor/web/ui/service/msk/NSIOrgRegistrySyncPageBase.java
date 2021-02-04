/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshPersonsSyncService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.internal.FrontController.FrontControllerException;
import ru.axetta.ecafe.processor.web.internal.FrontControllerProcessor;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeErrorItem;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItemV2;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeRevisionItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.xml.ws.soap.SOAPFaultException;
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
@DependsOn("runtimeContext")
public class NSIOrgRegistrySyncPageBase extends BasicWorkspacePage {
    private static final int DISPLAY_ALL_MODE = 1;
    private static final int DISPLAY_COMMENTED_MODE = 2;
    private static final int DISPLAY_NON_COMMENTED_MODE = 3;
    private static final boolean ALLOW_TO_APPLY_PREVIOS_REVISIONS = false;
    private static final int ALL_OPERATIONS = 0;
    private static final Logger logger = LoggerFactory.getLogger(NSIOrgRegistrySyncPageBase.class);
    protected final FrontControllerProcessor frontControllerProcessor = getFrontControllerProcessor();
    private final static List<SelectItem> displayModes = Arrays.asList(
            new SelectItem(DISPLAY_ALL_MODE, "Все"),
            new SelectItem(DISPLAY_COMMENTED_MODE, "Обработанные"),
            new SelectItem(DISPLAY_NON_COMMENTED_MODE, "Не обработанные")
    );

    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private boolean fullNameValidation = false;
    protected String errorMessages;
    protected String infoMessages;
    protected long revisionCreateDate;
    protected int actionFilter;
    protected List<RevisionItem> revisions;
    private List<RegistryChangeErrorItem> errors;
    private static Map<Integer, String> ACTION_FILTERS = new HashMap<Integer, String>();
    private long idOfSelectedError;
    private List <WebRegistryChangeItem> items;
    private Map<Long, CategoryDiscount> categoryMap;
    private Map<Integer, CategoryDiscountDSZN> categoryDSZNMap;
    private String errorComment;
    private int displayMode;
    protected String nameFilter;
    private long loadedOrgRevisions = -1L;
    boolean showOnlyClientGoups = false;
    protected String firstName;
    protected String lastName;
    protected String patronymic;

    private FrontControllerProcessor getFrontControllerProcessor(){
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class);
    }

    public String getPageFilename() {
        return "service/msk/nsi_org_registry_sync_page";
    }

    public String getPageDirectoryRoot() {
        return "/back-office/include";
    }

    public boolean isShowOnlyClientGoups() {
        return showOnlyClientGoups;
    }

    public void setShowOnlyClientGoups(boolean showOnlyClientGoups) {
        this.showOnlyClientGoups = showOnlyClientGoups;
    }

    public String getPageTitle() {
        return "Синхронизация организации с Реестрами";
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public int getActionFilter() {
        return actionFilter;
    }

    public void setActionFilter(int actionFilter) {
        this.actionFilter = actionFilter;
    }

    public List<WebRegistryChangeItem> getItems() {
        return items;
    }
    
    public String getLineStyleClass(WebRegistryChangeItem item) {
        switch (item.getOperation()) {
            case ImportRegisterMSKClientsService.CREATE_OPERATION:
                return "createClientRow";
            case ImportRegisterMSKClientsService.DELETE_OPERATION:
                return "deleteClientRow";
            case ImportRegisterMSKClientsService.MOVE_OPERATION:
                return "moveClientRow";
            case ImportRegisterMSKClientsService.MODIFY_OPERATION:
                return "";
        }
        return "";
    }

    public void setItems(List<WebRegistryChangeItem> items) {
        this.items = items;
    }

    public List<SelectItem> getErrors() {
        if (CollectionUtils.isEmpty(errors)) {
            return Collections.EMPTY_LIST;
        }

        List<SelectItem> items = new LinkedList<>();
        for (RegistryChangeErrorItem i : errors) {
            if (displayMode == DISPLAY_COMMENTED_MODE && StringUtils.isEmpty(i.getComment())) {
                continue;
            } else if (displayMode == DISPLAY_NON_COMMENTED_MODE && StringUtils.isNotEmpty(i.getComment())) {
                continue;
            }
            String msg = "№" + Org.extractOrgNumberFromName(i.getOrgName()) + " " +
                         df.format(new Date(i.getCreateDate()));
            items.add(new SelectItem(i.getIdOfRegistryChangeError(), msg));
        }
        return items;
    }

    public List<SelectItem> getRevisions() {
        if (CollectionUtils.isEmpty(revisions) || getIdOfOrg() != loadedOrgRevisions) {
            revisions = loadRevisions();
            loadedOrgRevisions = getIdOfOrg();
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for (RevisionItem item : revisions) {
            long date = item.getDate();
            String type = null;
            if(item.getType() == RegistryChange.CHANGES_UPDATE) {
                type = "Загрузка обновлений";
            } else if(item.getType() == RegistryChange.FULL_COMPARISON) {
                type = "Полная сверка";
            }
            if (items.size() < 1) {
                items.add(new SelectItem(date, df.format(new Date(date)) + " - Последняя " + " {" + type + "}"));
            } else {
                items.add(new SelectItem(date, df.format(new Date(date))+ " {" + type + "}"));
            }
        }
        return items;
    }

    public List<SelectItem> getActionFilters() {
        if(ACTION_FILTERS.isEmpty()) {
            ACTION_FILTERS.put(ImportRegisterMSKClientsService.CREATE_OPERATION, "Создание");
            ACTION_FILTERS.put(ImportRegisterMSKClientsService.DELETE_OPERATION, "Удаление");
            ACTION_FILTERS.put(ImportRegisterMSKClientsService.MODIFY_OPERATION, "Изменение");
            ACTION_FILTERS.put(ImportRegisterMSKClientsService.MOVE_OPERATION, "Перемещение");
            ACTION_FILTERS.put(ALL_OPERATIONS, "Все");
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for(Integer k : ACTION_FILTERS.keySet()) {
            items.add(new SelectItem(k, ACTION_FILTERS.get(k)));
        }
        return items;
    }

    @Override
    public void onShow() {
        revisions = loadRevisions();
        displayMode = DISPLAY_NON_COMMENTED_MODE;
    }

    public void doApply() throws Exception {
        resetMessages();

        List<Long> list = new ArrayList<Long>();
        for (WebRegistryChangeItem i : items) {
            if (i.isSelected()) {
                list.add(i.getIdOfRegistryChange());
            }
        }
        if (list.size() < 1) {
            return;
        }
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("интерактивная сверка (Синхронизация организации с Реестрами)");
        User user = MainPage.getSessionInstance().getCurrentUser();
        clientsMobileHistory.setUser(user);
        clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
        List<RegistryChangeCallback> result = proceedRegistryChangeItemInternal(list,
                RegistryChangeItem.APPLY_REGISTRY_CHANGE, fullNameValidation, clientsMobileHistory);
        doUpdate();
        if (result != null) {
            //  Ошибка
            //errorMessages = error;
            errorMessages = "";
            List<String> allErrors = new LinkedList<>();

            for(RegistryChangeCallback cb : result) {
                if(StringUtils.isNotEmpty(cb.getError())){
                    allErrors.add(cb.getError());
                }
            }
            if(!allErrors.isEmpty()) {
                errorMessages = StringUtils.join(allErrors, "; ");
            }
        }
    }

    protected List<RegistryChangeCallback> proceedRegistryChangeItemInternal(List<Long> list, int operation,
            boolean fullNameValidation, ClientsMobileHistory clientsMobileHistory) {
        return frontControllerProcessor.proceedRegistryChangeItem(list, operation, fullNameValidation, clientsMobileHistory);
    }

    public void doRefresh() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg != -1) {
            nameFilter = "";
            actionFilter = ALL_OPERATIONS;
            load(true);
        } else {
            errorMessages = "Выберите организацию";
        }
    }

    public void doRefreshMeshRest() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg != -1) {
            nameFilter = "";
            actionFilter = ALL_OPERATIONS;
            loadMeshRest(idOfOrg);
        } else {
            errorMessages = "Выберите организацию";
        }
        doRefresh();
    }

    public void loadMeshRest(long idOfOrg) {
        try {
            RuntimeContext.getAppContext().getBean(MeshPersonsSyncService.class).loadPersons(idOfOrg, null, lastName, firstName, patronymic);
        } catch (Exception e) {
            errorMessages = e.getMessage();
            return;
        }
    }

    public boolean orgSelected() {
        return getIdOfOrg() > -1L;
    }

    public void doUpdate() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg != -1) {
            load(false);
            loadErrors();
        } else {
            errorMessages = "Выберите организацию";
        }
    }

    public void doChangePanel(ValueChangeEvent event) {
        if (event.getNewValue().toString().equals("editErrorsPanel")) {
            loadErrors();
        }
    }

    public void doChangeErrorQuestion(ActionEvent actionEvent) {
        errorComment = "";
        RegistryChangeErrorItem e = getSelectedError();
        if (e != null && StringUtils.isNotEmpty(e.getComment())) {
            errorComment = e.getComment();
        }
    }

    public void doComment() {
        resetMessages();
        if (idOfSelectedError < 1L) {
            errorMessages = "Необходимо выбрать ошибку";
            return;
        }
        if (StringUtils.isEmpty(errorComment)) {
            errorMessages = "Необходимо заполнить комментарий";
            return;
        }
        String author = "";
        try {
            author = MainPage.getSessionInstance().getCurrentUser().getUserName();
        } catch (Exception ignored) {
        }
        String error = frontControllerProcessor.commentRegistryChangeError(idOfSelectedError, errorComment, author);
        idOfSelectedError = -1L;
        errorComment = "";
        loadErrors();
        if (error != null) {
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

    protected void load(boolean refresh) {
        resetMessages();
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 0L) {
            if (items == null) {
                items = new ArrayList<WebRegistryChangeItem>();
            }
            items.clear();
            return;
        }

        List<RegistryChangeItemV2> changedItems = null;
        if (!refresh) {
            changedItems = loadChangedItems();
        } else {
            try {
                changedItems = refreshRegistryChangeItemsV2(idOfOrg);
            } catch (ServiceTemporaryUnavailableException |
                    FrontControllerException |
                    RegistryTimeDeltaException |
                    BadOrgGuidsException e) {
                errorMessages = e.getMessage();
                return;
            } catch (Exception e) {
                if (e instanceof SOAPFaultException) {
                    errorMessages = e.getMessage();
                    return;
                }
            }
            if (CollectionUtils.isEmpty(changedItems)) {
                errorMessages = "Не получено разногласий";
                return;
            }
        }

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryMap = ImportRegisterMSKClientsService.getCategoriesMap(persistenceSession);
            categoryDSZNMap = ImportRegisterMSKClientsService.getCategoriesDSZNMap(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            errorMessages = e.getMessage();
            return;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        items = new ArrayList<>();
        for (RegistryChangeItemV2 i : changedItems) {
            if (showOnlyClientGoups) {
                if (!i.getList().get(9).getFieldValueParam().matches("^[0-9].*")) {
                    continue;
                }
            }
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

    protected List<ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItemV2> refreshRegistryChangeItemsV2(long idOfOrg) throws Exception {
        return frontControllerProcessor.refreshRegistryChangeItemsV2(idOfOrg);
    }

    protected List<ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItemV2> loadChangedItems() {
        return frontControllerProcessor.loadRegistryChangeItemsV2_WithFullFIO(getIdOfOrg(), revisionCreateDate,
                actionFilter, lastName, firstName, patronymic);
    }

    private void loadErrors() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 0L) {
            if (errors == null) {
                errors = new ArrayList<>();
            }
            errors.clear();
        }

        errors = frontControllerProcessor.loadRegistryChangeErrorItems(getIdOfOrg());
    }

    protected List<RevisionItem> loadRevisions () {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 1L) {
            if (items == null) {
                items = new ArrayList<WebRegistryChangeItem>();
            }
            items.clear();
            return Collections.EMPTY_LIST;
        }

        List<RegistryChangeRevisionItem> res = loadRevisionsFromController(getIdOfOrg());
        List<RevisionItem> result = new ArrayList<RevisionItem>();
        for(RegistryChangeRevisionItem i : res) {
            result.add(new RevisionItem(i.getDate(), i.getType()));
        }
        return result;
    }

    protected List<RegistryChangeRevisionItem> loadRevisionsFromController (Long idOfOrg) {
        return frontControllerProcessor.loadRegistryChangeRevisions(idOfOrg);
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
        return getCountOfOperation(ImportRegisterMSKClientsService.CREATE_OPERATION);
    }

    public int getDeletionsCount() {
        return getCountOfOperation(ImportRegisterMSKClientsService.DELETE_OPERATION);
    }

    public int getMovesCount() {
        return getCountOfOperation(ImportRegisterMSKClientsService.MOVE_OPERATION);
    }

    public int getModificationsCount() {
        return getCountOfOperation(ImportRegisterMSKClientsService.MODIFY_OPERATION);
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

    public Boolean isError(WebRegistryChangeItem item) {
        return item.getError() != null && item.getError().length() > 0;
    }

    private boolean isPermittedRevision() {
        return (revisionCreateDate < 0 || revisions == null || revisions.size() < 1 || revisions.get(0) == null ||
                revisionCreateDate != revisions.get(0).getDate()) && !ALLOW_TO_APPLY_PREVIOS_REVISIONS;
    }

    private int getCountOfOperation() {
        return items.size();
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public class WebRegistryChangeItem extends RegistryChangeItemV2 {

        protected Long idOfOrg;
        protected Long idOfMigrateOrgTo;
        protected Long idOfMigrateOrgFrom;
        protected Long createDate;
        protected Long idOfRegistryChange;
        protected String clientGUID;
        protected String firstName;
        protected String secondName;
        protected String surname;
        protected String groupName;
        protected String parallel;
        protected String parallelFrom;
        protected String firstNameFrom;
        protected String secondNameFrom;
        protected String surnameFrom;
        protected String groupNameFrom;
        protected Long idOfClient;
        protected Integer operation;
        protected Boolean applied;
        protected String error;

        protected boolean selected;

        protected String gender;
        protected String genderFrom;
        protected String birthDate;
        protected String birthDateFrom;
        protected String guardiansCount;

        protected String ageTypeGroup;

        protected String ageTypeGroupFrom;

        public WebRegistryChangeItem() {
            super();
            selected = false;
        }

        public WebRegistryChangeItem(RegistryChangeItemV2 parent) {
            super();
            idOfOrg            = Long.parseLong(parent.getList().get(0).getFieldValueParam());
            idOfMigrateOrgTo   = Long.parseLong(parent.getList().get(1).getFieldValueParam());
            idOfMigrateOrgFrom = Long.parseLong(parent.getList().get(2).getFieldValueParam());
            createDate         = Long.parseLong(parent.getList().get(3).getFieldValueParam());
            idOfRegistryChange = Long.parseLong(parent.getList().get(4).getFieldValueParam());
            clientGUID         = parent.getList().get(5).getFieldValueParam();
            firstName          = parent.getList().get(6).getFieldValueParam();
            secondName         = parent.getList().get(7).getFieldValueParam();
            surname            = parent.getList().get(8).getFieldValueParam();
            groupName          = parent.getList().get(9).getFieldValueParam();
            firstNameFrom      = parent.getList().get(10).getFieldValueParam();
            secondNameFrom     = parent.getList().get(11).getFieldValueParam();
            surnameFrom        = parent.getList().get(12).getFieldValueParam();
            groupNameFrom      = parent.getList().get(13).getFieldValueParam();
            idOfClient         = Long.parseLong(parent.getList().get(14).getFieldValueParam());

            operation          = Integer.parseInt(parent.getList().get(15).getFieldValueParam());
            applied            = Boolean.parseBoolean(parent.getList().get(16).getFieldValueParam());
            error              = parent.getList().get(17).getFieldValueParam();
            selected           = false;

            if (parent.getList().get(18).getFieldValueParam().equals("")) {
                gender = "";
            } else {
                int genderInt = Integer.parseInt(parent.getList().get(18).getFieldValueParam());
                if (genderInt == 1) {
                    gender = "Мужской";
                } else {
                    gender = "Женский";
                }
            }

            if (parent.getList().get(19).getFieldValueParam().equals("")) {
                birthDate = "";
            } else {
                birthDate = parent.getList().get(19).getFieldValueParam();
            }

            if (parent.getList().get(21).getFieldValueParam().equals("")) {
                genderFrom = "";
            } else {
                int genderInt = Integer.parseInt(parent.getList().get(21).getFieldValueParam());
                if (genderInt == 1) {
                    genderFrom = "Мужской";
                } else {
                    genderFrom = "Женский";
                }
            }

            if (parent.getList().get(22).getFieldValueParam().equals("")) {
                birthDateFrom = "";
            } else {
                birthDateFrom = parent.getList().get(22).getFieldValueParam();
            }

            guardiansCount = parent.getList().get(24).getFieldValueParam();

            ageTypeGroup = parent.getList().get(25).getFieldValueParam();
            ageTypeGroupFrom = parent.getList().get(26).getFieldValueParam();

            parallel = parent.getList().get(29).getFieldValueParam();
            parallelFrom = parent.getList().get(30).getFieldValueParam();
        }

        public Long getIdOfMigrateOrgFrom() {
            return idOfMigrateOrgFrom;
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

        private String getCategoriesDSZNString(String categoiesDSZN) {
            if(StringUtils.isEmpty(categoiesDSZN)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for(String c : categoiesDSZN.split(",")) {
                if(StringUtils.isNotEmpty(c)) {
                    int code = Integer.valueOf(c);
                    if(categoryDSZNMap.get(code) != null) {
                        sb.append(c);
                        sb.append(" - ");
                        sb.append(categoryDSZNMap.get(code).getDescription());
                        sb.append("; ");
                    } else {
                        sb.append(c);
                        sb.append("; ");
                    }
                }
            }
            return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
        }

        private String getCategoriesString(String categoies) {
            if(StringUtils.isEmpty(categoies)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for(String c : categoies.split(",")) {
                if(StringUtils.isNotEmpty(c)) {
                    long id = Integer.valueOf(c);
                    if(categoryMap.get(id) != null) {
                        sb.append(c);
                        sb.append(" - ");
                        sb.append(categoryMap.get(id).getCategoryName());
                        sb.append("; ");
                    } else {
                        sb.append(c);
                        sb.append("; ");
                    }
                }
            }
            return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
        }

        public boolean getFullnameChangeExists() {
            return operation == ImportRegisterMSKClientsService.MODIFY_OPERATION &&
                   (!firstName.equals(firstNameFrom) ||
                    !secondName.equals(secondNameFrom) ||
                    !surname.equals(surnameFrom));
        }

        public boolean getGenderFromChangeExists() {
            return operation == ImportRegisterMSKClientsService.MODIFY_OPERATION &&
                    !gender.equals(genderFrom);
        }

        public boolean getBirthDateFromChangeExists() {
            return operation == ImportRegisterMSKClientsService.MODIFY_OPERATION &&
                    !birthDate.equals(birthDateFrom);
        }

        public boolean getGroupChangeExists() {
            return (operation == ImportRegisterMSKClientsService.MODIFY_OPERATION ||operation == ImportRegisterMSKClientsService.MOVE_OPERATION ) &&
                   !groupName.equals(groupNameFrom);
        }

        public boolean getParallelChangeExists() {
            return (operation == ImportRegisterMSKClientsService.MODIFY_OPERATION ||operation == ImportRegisterMSKClientsService.MOVE_OPERATION ) &&
                    !parallel.equals(parallelFrom);
        }
        
        public String getFullname() {
            return surname + " " + firstName + " " + secondName;
        }
        
        public String getPrevFullname() {
            return surnameFrom + " " + firstNameFrom + " " + secondNameFrom;
        }
        
        public String getOperationName() {
            switch (operation) {
                case ImportRegisterMSKClientsService.CREATE_OPERATION:
                    return "создание";
                case ImportRegisterMSKClientsService.DELETE_OPERATION:
                    return "удаление";
                case ImportRegisterMSKClientsService.MODIFY_OPERATION:
                    return "изменение";
                case ImportRegisterMSKClientsService.MOVE_OPERATION:
                    return "перемещение";
                default:
                    return "неизвестно";
            }
        }

        public String getMigrateFromOrgName() {
            return operation == ImportRegisterMSKClientsService.MOVE_OPERATION ?
                        DAOService.getInstance().findOrById(getIdOfMigrateOrgFrom()).getOfficialName() : "";
        }

        public boolean isApplied() {
            return applied;
        }

        public Integer getOperation() {
            return operation;
        }

        public String getError() {
            return error;
        }

        public Long getCreateDate() {
            return createDate;
        }

        public Long getIdOfRegistryChange() {
            return idOfRegistryChange;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getIdOfMigrateOrgTo() {
            return idOfMigrateOrgTo;
        }

        public String getClientGUID() {
            return clientGUID;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getSurname() {
            return surname;
        }

        public String getParallel() {
            return parallel;
        }

        public String getParallelFrom() {
            return parallelFrom;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getFirstNameFrom() {
            return firstNameFrom;
        }

        public String getSecondNameFrom() {
            return secondNameFrom;
        }

        public String getSurnameFrom() {
            return surnameFrom;
        }

        public String getGroupNameFrom() {
            return groupNameFrom;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public Boolean getApplied() {
            return applied;
        }

        public String getGender() {
            return gender;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public String getBirthDateFrom() {
            return birthDateFrom;
        }

        public void setBirthDateFrom(String birthDateFrom) {
            this.birthDateFrom = birthDateFrom;
        }

        public String getGenderFrom() {
            return genderFrom;
        }

        public void setGenderFrom(String genderFrom) {
            this.genderFrom = genderFrom;
        }

        public String getGuardiansCount() {
            return guardiansCount;
        }

        public void setGuardiansCount(String guardiansCount) {
            this.guardiansCount = guardiansCount;
        }

        public String getAgeTypeGroup() {
            return ageTypeGroup;
        }

        public void setAgeTypeGroup(String ageTypeGroup) {
            this.ageTypeGroup = ageTypeGroup;
        }

        public String getAgeTypeGroupFrom() {
            return ageTypeGroupFrom;
        }

        public void setAgeTypeGroupFrom(String ageTypeGroupFrom) {
            this.ageTypeGroupFrom = ageTypeGroupFrom;
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
        return sel != null && StringUtils.isEmpty(sel.getComment());
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
        if (CollectionUtils.isEmpty(errors)) {
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

        if (revisions.get(0).getDate() == revisionCreateDate) {
            return "последней сверки";
        } else {
            return "сверки от " + df.format(new Date(revisionCreateDate));
        }
    }

    public static final class RevisionItem {
        protected long date;
        protected int type;

        public RevisionItem(long date, int type) {
            this.date = date;
            this.type = type;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}