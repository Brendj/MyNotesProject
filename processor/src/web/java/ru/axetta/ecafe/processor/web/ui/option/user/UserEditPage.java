/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgMainBuildingListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserEditPage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList,
        OrgMainBuildingListSelectPage.CompleteHandler, OrgSelectPage.CompleteHandler, ClientSelectPage.CompleteHandler {

    protected Long idOfUser;
    protected String userName;
    protected boolean changePassword = false;
    protected String plainPassword;
    protected String plainPasswordConfirmation;
    protected String phone;
    protected String email;
    //private ContragentItem contragentItem;
    protected List<ContragentItem> contragentItems = new ArrayList<ContragentItem>();
    protected Integer idOfRole;
    protected String roleName;
    protected final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    protected FunctionSelector functionSelector = new FunctionSelector();
    protected String contragentFilter = "Не выбрано";
    protected String contragentIds;
    protected String orgIds;
    protected String orgIdsCanceled;
    protected Long organizationId;
    protected Boolean blocked;
    protected SelectItem[] regions;
    protected String region;
    protected String orgFilter = "Не выбрано";
    protected String orgFilterCanceled = "Не выбрано";
    protected Boolean needChangePassword;
    protected Date blockedUntilDate;
    protected String organizationsFilter = "Не выбрано";
    protected String firstName;
    protected String surname;
    protected String secondName;
    protected String department;
    protected OrgItem userOrg = new OrgItem(null, null);
    protected String userOrgName = "Не выбрано";
    protected Long userIdOfClient;
    protected String userClientName = "Не выбрано";

    protected UserNotificationType selectOrgType;

    protected List<OrgItem> orgItems = new ArrayList<OrgItem>(0);
    protected List<OrgItem> orgItemsCanceled = new ArrayList<OrgItem>(0);
    protected List<OrgItem> organizationItems = new ArrayList<OrgItem>(0);

    private final static Logger logger = LoggerFactory.getLogger(UserEditPage.class);

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        switch (selectOrgType){
            case GOOD_REQUEST_CHANGE_NOTIFY: {
                if (orgMap != null) {
                    orgItems = new ArrayList<OrgItem>();
                    if (orgMap.isEmpty()) {
                        orgFilter = "Не выбрано";
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Long idOfOrg : orgMap.keySet()) {
                            orgItems.add(new OrgItem(idOfOrg, orgMap.get(idOfOrg)));
                            stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                        }
                        orgFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
                    }
                }
            } break;
            case ORDER_STATE_CHANGE_NOTIFY: {
                if (orgMap != null) {
                    orgItemsCanceled = new ArrayList<OrgItem>();
                    if (orgMap.isEmpty()) {
                        orgFilterCanceled = "Не выбрано";
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Long idOfOrg : orgMap.keySet()) {
                            orgItemsCanceled.add(new OrgItem(idOfOrg, orgMap.get(idOfOrg)));
                            stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                        }
                        orgFilterCanceled = stringBuilder.substring(0, stringBuilder.length() - 2);
                    }
                }
            } break;
        }
    }

    public void setFunctionSelector(FunctionSelector functionSelector) {
        this.functionSelector = functionSelector;
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        fill(session, user);
    }

    public void updateUser(Session session, Long idOfUser) throws Exception {
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        User currentUser = DAOReadonlyService.getInstance().getUserFromSession();
        String currentUserName = (currentUser == null) ? null : currentUser.getUserName();

        try {
            if (StringUtils.isEmpty(userName)) {
                this.printError("Заполните имя пользователя");
                throw new RuntimeException("Username field is null");
            }
            if (StringUtils.isEmpty(phone)) {
                this.printError("Заполните поле контактного телефона");
                throw new RuntimeException("Phone field is null");
            } else {
                String mobile = Client.checkAndConvertMobile(this.phone);
                if (mobile == null) {
                    throw new RuntimeException("Неверный формат контактного (мобильного) телефона");
                }
                phone = mobile;
            }

            User user = (User) session.get(User.class, idOfUser);
            user.setUserName(userName);
            if (changePassword) {
                if (!StringUtils.equals(plainPassword, plainPasswordConfirmation)) {
                    throw new UserChangeGrantsException("Пароль и подтверждение пароля не совпадают");
                } else if (!User.passwordIsEnoughComplex(plainPassword)) {
                    this.printError("Пароль не удовлетворяет требованиям безопасности: ");
                    this.printError("- минимальная длина - 6 символов");
                    this.printError("- должны присутствовать прописные и заглавные латинские буквы + хотя бы одна цифра или спецсимвол");
                    throw new UserChangeGrantsException("Bad password");
                }
                user.setPassword(plainPassword);
                user.setPasswordDate(new Date(System.currentTimeMillis()));
                user.setSmsCodeGenerateDate(null);
            }
            Boolean successChangeGrants = getChangeGrantsMode(session, user);
            if(user.getPerson() != null) {
                user.getPerson().setSurname(surname);
                user.getPerson().setSecondName(secondName);
                user.getPerson().setFirstName(firstName);
                session.update(user.getPerson());
            } else if(!firstName.isEmpty() || !surname.isEmpty() || !secondName.isEmpty()) {
                Person person = new Person(firstName, surname, secondName);
                user.setPerson(person);
                session.save(person);
            }
            if(userIdOfClient != null) {
                Client userClient = (Client) session.load(Client.class, userIdOfClient);
                user.setClient(userClient);
                if(userClient != null && userClient.getOrg() != null)
                    user.setOrg(userClient.getOrg());
                else {
                    if(userOrg.idOfOrg != null)
                        user.setOrg((Org) session.load(Org.class, userOrg.idOfOrg));
                    else
                        user.setOrg(null);
                }

            }
            else
                user.setClient(null);
            user.setPhone(phone);
            user.setEmail(email);
            user.setUpdateTime(new Date());
            if (user.isBlocked() && !blocked) {
                //Если пользователь был заблокирован и новое значение флага блокировки = false, то меняем дату последней активности пользователя
                user.setLastEntryTime(new Date());
            }
            //Если мы разблокируем пользователя, то нужно отправить sms
            if (user.isBlocked() && !blocked)
                user.setSmsCodeGenerateDate(null);
            user.setBlocked(blocked);
            user.setBlockedUntilDate(blockedUntilDate);
            User.DefaultRole role = null;
            if (idOfRole < UserRoleEnumTypeMenu.OFFSET) {
                role = User.DefaultRole.parse(idOfRole);
            }
            user.setIdOfRole(idOfRole);
            if (role != null && User.DefaultRole.SUPPLIER.equals(role)) {
                //user.setFunctions(functionSelector.getSupplierFunctions(session));
                user.setFunctions(functionSelector.getSelected(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагентов пуст.");
                    throw new RuntimeException("Contragent list is empty");
                }
            }
            if (role != null && role.equals(User.DefaultRole.SUPPLIER_REPORT)) {
                user.setFunctions(functionSelector.getSelectedForForSupplierReportFunction(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагентов пуст.");
                    throw new RuntimeException("Contragent list is empty");
                }
            }
            if(role != null && (role.equals(User.DefaultRole.CLASSROOM_TEACHER)
                    || role.equals(User.DefaultRole.CLASSROOM_TEACHER_WITH_FOOD_PAYMENT)
                    || role.equals(User.DefaultRole.INFORMATION_SYSTEM_OPERATOR))) {
                user.setRoleName(role.toString());
                if(user.getClient() == null){
                    this.printError("Выберите клиента.");
                    throw new RuntimeException("Client field is null");
                }
            }
            if(role != null && role.equals(User.DefaultRole.PRODUCTION_DIRECTOR)){
                user.setRoleName(role.toString());
                if(user.getOrg() == null){
                    this.printError("Выберите организацию.");
                    throw new RuntimeException("Org field is null");
                }
            }
            user.getContragents().clear();
            for (ContragentItem it : this.contragentItems) {
                Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
                user.getContragents().add(contragent);
            }

            if(role != null && role.equals(User.DefaultRole.MONITORING)){
                user.setFunctions(functionSelector.getMonitoringFunctions(session));
                user.setRoleName(role.toString());
            }
            if(role != null && role.equals(User.DefaultRole.ADMIN)){
                user.setFunctions(functionSelector.getAdminFunctions(session));
                user.setRoleName(role.toString());
            }
            if(role != null && role.equals(User.DefaultRole.ADMIN_SECURITY)){
                user.setFunctions(functionSelector.getSecurityAdminFunctions(session));
                user.setRoleName(role.toString());
            }
            if (User.DefaultRole.DEFAULT.equals(role)) {
                if (StringUtils.isEmpty(roleName)) {
                    this.printError("Заполните имя роли");
                    throw new RuntimeException("Не заполнено имя роли");
                }
                user.setFunctions(functionSelector.getSelected(session));
                user.setRoleName(this.roleName);
            }
            if (role != null && role.equals(User.DefaultRole.CARD_OPERATOR)) {
                user.setFunctions(functionSelector.getCardOperatorFunctions(session));
                user.setRoleName(role.toString());
                user.setDepartment(department);
            }
            if (role == null) {
                User userRole = (User)session.get(User.class, idOfRole - UserRoleEnumTypeMenu.OFFSET);
                Set<Function> set = new HashSet<Function>(userRole.getFunctions());
                user.setFunctions(set);
                user.setRoleName(userRole.getUserName());
                user.setIdOfGroup(userRole.getIdOfUser());
            }
            if (region != null && region.length() > 0) {
                user.setRegion(region);
            } else {
                user.setRegion(null);
            }
            user.setNeedChangePassword(needChangePassword);
            for (UserOrgs uo : user.getUserOrgses()) {
                session.delete(uo);
            }
            session.update(user);
            session.flush();
            for (OrgItem orgItem : orgItems) {
                Org org = (Org) session.load(Org.class, orgItem.idOfOrg);
                UserOrgs userOrgs = new UserOrgs(user, org, UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY);
                session.save(userOrgs);
            }
            for (OrgItem orgItem : orgItemsCanceled) {
                Org org = (Org) session.load(Org.class, orgItem.idOfOrg);
                UserOrgs userOrgs = new UserOrgs(user, org, UserNotificationType.ORDER_STATE_CHANGE_NOTIFY);
                session.save(userOrgs);
            }

            for (OrgItem orgItem : organizationItems) {
                Org org = (Org)session.get(Org.class, orgItem.idOfOrg);
                if (org.isMainBuilding()) {
                    Criteria criteria = session.createCriteria(UserDirectorOrg.class);
                    //criteria.add(Restrictions.eq("org", org));
                    criteria.add(Restrictions.eq("user", user));
                    UserDirectorOrg userDirectorOrg = (UserDirectorOrg) criteria.uniqueResult();

                    if (null != userDirectorOrg) {
                        userDirectorOrg.setUser(user);
                        userDirectorOrg.setOrg(org);
                    } else {
                        userDirectorOrg = new UserDirectorOrg(user, org);
                    }

                    session.saveOrUpdate(userDirectorOrg);
                }
            }

            // если не выбрано организаций - удаляем из базы привязку
            if (organizationItems.isEmpty()) {
                UserDirectorOrg userDirectorOrg = (UserDirectorOrg)session.createCriteria(UserDirectorOrg.class)
                        .add(Restrictions.eq("user", user)).uniqueResult();

                if (null != userDirectorOrg) {
                    session.delete(userDirectorOrg);
                }
            }

            SecurityJournalAuthenticate.EventType eventType;
            String comment;
            if (successChangeGrants) {
                eventType = SecurityJournalAuthenticate.EventType.CHANGE_GRANTS;
                comment = String.format("Изменены права доступа пользователя %s", userName);
            } else {
                eventType = SecurityJournalAuthenticate.EventType.MODIFY_USER;
                comment = String.format("Отредактированы данные пользователя %s", userName);
            }
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(eventType, request.getRemoteAddr(), currentUserName,
                            currentUser, true, null, comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
        } catch (RuntimeException e) {
            String comment = String.format("Ошибка при изменении данных пользователя с именем %s. Текст ошибки: %s", userName, e.getMessage());
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.MODIFY_USER, request.getRemoteAddr(),
                            currentUserName, currentUser, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw e;
        } catch (UserChangeGrantsException e) {
            String comment = String.format("Ошибка при изменении прав доступа пользователя с именем %s. Текст ошибки: %s", userName, e.getMessage());
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CHANGE_GRANTS, request.getRemoteAddr(),
                            currentUserName, currentUser, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw e;
        }
    }

    //Изменялся ли набор полей, по которым считается, что было не редактирование, а изменение прав доступа
    private boolean getChangeGrantsMode(Session session, User user) {
        if (!user.isBlocked().equals(blocked) || changePassword) {
            return true;
        }

        User.DefaultRole role = null;
        if (idOfRole < UserRoleEnumTypeMenu.OFFSET ) {
            role = User.DefaultRole.parse(idOfRole);
        }
        if(role != null && role.equals(User.DefaultRole.MONITORING) && !user.getFunctions().equals(functionSelector.getMonitoringFunctions(session))) {
            return true;
        }
        if(role != null && role.equals(User.DefaultRole.ADMIN) && !user.getFunctions().equals(functionSelector.getAdminFunctions(session))) {
            return true;
        }
        if(role != null && role.equals(User.DefaultRole.ADMIN_SECURITY) && !user.getFunctions().equals(functionSelector.getSecurityAdminFunctions(session))) {
            return true;
        }
        if (role != null && User.DefaultRole.DEFAULT.equals(role) && !user.getFunctions().equals(functionSelector.getSelected(session))) {
            return true;
        }
        if (role != null && role.equals(User.DefaultRole.CARD_OPERATOR) && !user.getFunctions().equals(functionSelector.getCardOperatorFunctions(session))) {
            return true;
        }

        Set<Contragent> contragents = new HashSet<Contragent>();
        for (ContragentItem it : this.contragentItems) {
            Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
            contragents.add(contragent);
        }
        if (user.getContragents().hashCode() != contragents.hashCode()) {
            return true;
        }
        return false;
    }

    public void blockedToChange(ValueChangeEvent e){
        Boolean newBlockedValue = (Boolean)e.getNewValue();
        if(newBlockedValue) {
            blockedUntilDate = new Date(System.currentTimeMillis() + CalendarUtils.FIFTY_YEARS_MILLIS);
        } else {
            blockedUntilDate = null;
        }
    }

    public void blockedDateChange(ValueChangeEvent e) {
        Date newBlockedUntilDate = (Date)e.getNewValue();
        if(newBlockedUntilDate == null) {
            blocked = false;
            return;
        }
        blocked = new Date().after(newBlockedUntilDate) ? false : true;
    }

    private void initRegions (Session session) {
        List<String> regions = DAOUtils.getRegions(session);
        regions.add(0, "");
        this.regions = new SelectItem[regions.size()];
        for (int i=0; i<regions.size(); i++) {
            this.regions [i] = new SelectItem(regions.get(i));
        }
    }
    
    public SelectItem[] getRegions() {
        return regions;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

    public String getOrgFilterCanceled() {
        return orgFilterCanceled;
    }

    public void setOrgFilterCanceled(String orgFilterCanceled) {
        this.orgFilterCanceled = orgFilterCanceled;
    }


    public String getOrganizationsFilter() {
        return organizationsFilter;
    }

    public void setOrganizationsFilter(String organizationsFilter) {
        this.organizationsFilter = organizationsFilter;
    }

    @Override
    public void onShow() throws Exception {
        orgFilter = "Не выбрано";
        orgItems.clear();

        orgFilterCanceled = "Не выбрано";
        orgItemsCanceled.clear();
    }

    public Object showOrgListSelectPage(){
        selectOrgType = UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY;
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object showOrgListSelectCancelPage(){
        selectOrgType = UserNotificationType.ORDER_STATE_CHANGE_NOTIFY;
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object showOrgListPage(){
        MainPage.getSessionInstance().showOrgMainBuildingListSelectPage();
        return null;
    }

    public void setSelectOrgType(int id) {
        this.selectOrgType = UserNotificationType.values()[id];
    }


    public Boolean getIsDefault(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
    }

    public Boolean getIsSecurityAdmin(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.ADMIN_SECURITY);
    }

    public Boolean getIsSupplier() {
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SUPPLIER);
    }

    public Boolean getIsSupplierReport() {
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SUPPLIER_REPORT);
    }

    public Boolean getIsCardOperator() {
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.CARD_OPERATOR);
    }

    public Boolean getIsDirector(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DIRECTOR);
    }

    @Override
    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag,
            String classTypes) throws Exception {
        contragentItems.clear();
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            ContragentItem contragentItem = new ContragentItem(currentContragent);
            contragentItems.add(contragentItem);
        }
        setContragentFilterInfo(contragentItems);
    }


    private void setContragentFilterInfo(List<ContragentItem> contragentItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (contragentItems.isEmpty()) {
            contragentFilter = "Не выбрано";
        } else {
            for (ContragentItem it : contragentItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(it.getContragentName());
                ids.append(it.getIdOfContragent());
            }
            contragentFilter = str.toString();
        }
        contragentIds = ids.toString();
    }

    //
    private void setOrgFilterInfo(List<OrgItem> orgItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (orgItems.isEmpty()) {
            orgFilter = "Не выбрано";
        } else {
            for (OrgItem ot : orgItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(ot.getShortName());
                ids.append(ot.getIdOfOrg());
            }
            orgFilter = str.toString();
        }
        orgIds = ids.toString();
    }

    private void setOrgFilterCanceledInfo(List<OrgItem> orgItemsCanceled) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (orgItemsCanceled.isEmpty()) {
            orgFilterCanceled = "Не выбрано";
        } else {
            for (OrgItem ot : orgItemsCanceled) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(ot.getShortName());
                ids.append(ot.getIdOfOrg());
            }
            orgFilterCanceled = str.toString();
        }
        orgIdsCanceled = ids.toString();
    }

    private void setOrganizationsFilterInfo(List<OrgItem> orgItems) {
        StringBuilder str = new StringBuilder();
        if (orgItems.isEmpty()) {
            organizationsFilter = "Не выбрано";
        } else {
            for (OrgItem orgItem : orgItems) {
                if (str.length() > 0) {
                    str.append("; ");
                }
                str.append(orgItem.getShortName());
                if (orgItem.getMainBuilding())
                    organizationId = orgItem.getIdOfOrg();
            }
            organizationsFilter = str.toString();
        }
    }

    /*public Object showContragentListSelectPageOwn() {
        BasicPage currentTopMostPage = MainPage.getSessionInstance().getTopMostPage();
        if (currentTopMostPage instanceof ContragentListSelectPage.CompleteHandler) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                //contragentListSelectPage.fill(persistenceSession, multiContrFlag, classTypes);
                MainPage.getSessionInstance().getContragentListSelectPage().setItems(retrieveContragents(persistenceSession));
                persistenceTransaction.commit();
                persistenceTransaction = null;
                MainPage.getSessionInstance().getContragentListSelectPage()
                        .pushCompleteHandler((ContragentListSelectPage.CompleteHandler) currentTopMostPage);
                MainPage.getSessionInstance().getModalPages().push(
                        MainPage.getSessionInstance().getContragentListSelectPage());
            } catch (Exception e) {
                logger.error("Failed to fill contragents list selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора списка контрагентов: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    private List<ContragentListSelectPage.Item> retrieveContragents(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", 2));
        criteria.addOrder(org.hibernate.criterion.Order.asc("contragentName"));
        List contragents = criteria.list();
        List<ContragentListSelectPage.Item> items = new LinkedList<ContragentListSelectPage.Item>();
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            ContragentListSelectPage.Item item = new ContragentListSelectPage.Item(contragent);
            items.add(item);
            if (contragentIds.contains(item.getIdOfContragent().toString())) {
                item.setSelected(true);
            }
        }
        return items;
    } */

    protected void fill(Session session, User user) throws Exception {
        this.contragentItems.clear();
        this.orgItems.clear();
        this.orgItemsCanceled.clear();
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.department = user.getDepartment();
        if(user.getPerson() != null){
            this.firstName = user.getPerson().getFirstName();
            this.surname = user.getPerson().getSurname();
            this.secondName = user.getPerson().getSecondName();
        } else {
            this.firstName = this.surname = this.secondName = "";
        }
        this.functionSelector.fill(session, user.getFunctions());
        for (Contragent c : user.getContragents()) {
            this.contragentItems.add(new ContragentItem(c));
        }
        for (UserOrgs o : user.getUserOrgses()) {
            //Org org = (Org) session.load(Org.class, o.getOrg().getIdOfOrg());
            //this.orgItems.add(o.getOrg().getIdOfOrg());
            switch (o.getUserNotificationType()) {
                case GOOD_REQUEST_CHANGE_NOTIFY: {
                    this.orgItems.add(new OrgItem(o.getOrg()));
                }break;
                case ORDER_STATE_CHANGE_NOTIFY: {
                    this.orgItemsCanceled.add(new OrgItem(o.getOrg()));
                }break;
            }
        }
        setContragentFilterInfo(contragentItems);
        setOrgFilterInfo(orgItems);
        setOrgFilterCanceledInfo(orgItemsCanceled);

        this.organizationItems.clear();
        String sqlQuery =
                "SELECT o.idoforg, o.shortname, o.mainbuilding "
              + "FROM cf_user_director_org udo "
              + "INNER JOIN cf_friendly_organization fo ON fo.currentorg=udo.idoforg "
              + "INNER JOIN cf_orgs o ON fo.friendlyorg=o.idoforg "
              + "WHERE udo.idofuser=:idOfUser "
              + "ORDER BY o.mainbuilding";

        Query query = session.createSQLQuery(sqlQuery);
        query.setParameter("idOfUser", user.getIdOfUser());

        List list = query.list();

        for (Object o : list) {
            Object vals[] = (Object[])o;
            organizationItems.add(new OrgItem(((BigInteger)vals[0]).longValue(),
                    vals[1].toString(), ((Integer)vals[2]) == 1 ? Boolean.TRUE : Boolean.FALSE));
        }

        setOrganizationsFilterInfo(organizationItems);

        if (user.getIdOfRole() < User.DefaultRole.values().length) {
            this.idOfRole = user.getIdOfRole();
        } else {
            this.idOfRole = UserRoleEnumTypeMenu.OFFSET.intValue() + user.getIdOfGroup().intValue();
        }
        this.roleName = user.getRoleName();
        this.blocked = user.isBlocked();
        this.blockedUntilDate = user.getBlockedUntilDate();
        this.region = user.getRegion();
        this.needChangePassword = user.getNeedChangePassword();
        this.changePassword = false;
        if(user.getClient() != null){
            this.userClientName = user.getClient().getPerson().getFullName();
            this.userIdOfClient = user.getClient().getIdOfClient();
        }
        if(user.getOrg() != null){
            this.userOrg = new OrgItem(user.getOrg().getIdOfOrg(), user.getOrg().getShortName());
            this.userOrgName = userOrg.getShortName();
        }
        initRegions(session);
    }

    public Boolean getNeedChangePassword() {
        return needChangePassword;
    }

    public void setNeedChangePassword(Boolean needChangePassword) {
        this.needChangePassword = needChangePassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserOrgName() { return userOrgName; }

    public void setUserOrgName(String userOrgName) { this.userOrgName = userOrgName; }

    public String getUserClientName() { return  userClientName; }

    public void setUserClientName(String userClientName) { this.userClientName = userClientName; }

    public static class ContragentItem {

        private final Long idOfContragent;

        private final String contragentName;
        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }

    }

    protected static class OrgItem {
        protected final Long idOfOrg;
        protected final String shortName;
        protected final Boolean mainBuilding;

        OrgItem(Org org) {
            this(org.getIdOfOrg(), org.getShortName(), org.isMainBuilding());
        }

        public OrgItem(Long idOfOrg, String shortName) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.mainBuilding = Boolean.FALSE;
        }

        public OrgItem(Long idOfOrg, String shortName, Boolean mainBuilding) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.mainBuilding = mainBuilding;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public Boolean getMainBuilding() {
            return mainBuilding;
        }
    }

    /* getters and setters */
    public UserRoleEnumTypeMenu getUserRoleEnumTypeMenu() {
        return userRoleEnumTypeMenu;
    }

    public String getPageFilename() {
        return "option/user/edit";
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    public void setIdOfUser(Long idOfUser) {
        this.idOfUser = idOfUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getPlainPasswordConfirmation() {
        return plainPasswordConfirmation;
    }

    public void setPlainPasswordConfirmation(String plainPasswordConfirmation) {
        this.plainPasswordConfirmation = plainPasswordConfirmation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void setContragentFilter(String contragentFilter) {
        this.contragentFilter = contragentFilter;
    }

    public String getContragentIds() {
        return contragentIds;
    }

    public void setContragentIds(String contragentIds) {
        this.contragentIds = contragentIds;
    }

    public String getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(String orgIds) {
        this.orgIds = orgIds;
    }

    public String getOrgIdsCanceled() {
        return orgIdsCanceled;
    }

    public void setOrgIdsCanceled(String orgIdsCanceled) {
        this.orgIdsCanceled = orgIdsCanceled;
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public void setIdOfRole(Integer idOfRole) {
        this.idOfRole = idOfRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Date getBlockedUntilDate() {
        return blockedUntilDate;
    }

    public void setBlockedUntilDate(Date blockedUntilDate) {
        this.blockedUntilDate = blockedUntilDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public void completeOrgMainBuildingListSelection(Session session, List<Org> orgs) throws Exception {
        if (getIsDirector()) {
            if (null != orgs) {
                organizationItems = new ArrayList<OrgItem>();
                if (orgs.isEmpty()) {
                    organizationsFilter = "Не выбрано";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Org org : orgs) {
                        organizationItems.add(new OrgItem(org.getIdOfOrg(), org.getShortName(), org.isMainBuilding()));
                        stringBuilder.append(org.getShortName()).append("; ");
                    }
                    organizationsFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
                }
            }
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if(idOfOrg == null){
            this.userOrg = new OrgItem(null, "Не выбрано");
            this.userOrgName = "Не выбрано";
        }
        else {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.userOrg = new OrgItem(org.getIdOfOrg(), org.getShortName());
            this.userOrgName = org.getShortName();
        }
    }

    @Override
    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if(idOfClient == null) {
            this.userIdOfClient = null;
            this.userClientName = "Не выбрано";
        }
        else {
            Client client = (Client) session.load(Client.class,idOfClient);
            this.userIdOfClient = client.getIdOfClient();
            this.userClientName = client.getPerson().getFullName();
            this.firstName = client.getPerson().getFirstName();
            this.secondName = client.getPerson().getSecondName();
            this.surname = client.getPerson().getSurname();
            if(client.getOrg() != null)
            {
                this.userOrg = new OrgItem(client.getOrg().getIdOfOrg(), client.getOrg().getShortName());
                this.userOrgName = this.userOrg.getShortName();
            }
        }
    }
}