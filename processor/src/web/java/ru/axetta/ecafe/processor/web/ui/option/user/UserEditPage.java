/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserEditPage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList{

    private Long idOfUser;
    private String userName;
    private boolean changePassword = false;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    //private ContragentItem contragentItem;
    private List<ContragentItem> contragentItems = new ArrayList<ContragentItem>();
    private Integer idOfRole;
    private String roleName;
    private final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    private FunctionSelector functionSelector = new FunctionSelector();
    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private String orgIds;
    private String orgIdsCanceled;
    private Boolean blocked;
    private SelectItem[] regions;
    private String region;
    private String orgFilter = "Не выбрано";
    private String orgFilterCanceled = "Не выбрано";
    private Boolean needChangePassword;
    private Date blockedUntilDate;

    private UserNotificationType selectOrgType;

    protected List<OrgItem> orgItems = new ArrayList<OrgItem>(0);
    protected List<OrgItem> orgItemsCanceled = new ArrayList<OrgItem>(0);

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
        HttpServletRequest request = SecurityContextAssociationValve.getActiveRequest().getRequest();
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

            User user = (User) session.load(User.class, idOfUser);
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
            }
            Boolean successChangeGrants = getChangeGrantsMode(session, user);
            user.setPhone(phone);
            user.setEmail(email);
            user.setUpdateTime(new Date());
            if (user.isBlocked() && !blocked) {
                //Если пользователь был заблокирован и новое значение флага блокировки = false, то меняем дату последней активности пользователя
                user.setLastEntryTime(new Date());
            }
            user.setBlocked(blocked);
            user.setBlockedUntilDate(blockedUntilDate);
            User.DefaultRole role = User.DefaultRole.parse(idOfRole);
            user.setIdOfRole(role.getIdentification());
            if (User.DefaultRole.SUPPLIER.equals(role)) {
                user.setFunctions(functionSelector.getSupplierFunctions(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагентов пуст.");
                    throw new RuntimeException("Contragent list is empty");
                }
            }
            user.getContragents().clear();
            for (ContragentItem it : this.contragentItems) {
                Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
                user.getContragents().add(contragent);
            }

            if(role.equals(User.DefaultRole.MONITORING)){
                user.setFunctions(functionSelector.getMonitoringFunctions(session));
                user.setRoleName(role.toString());
            }
            if(role.equals(User.DefaultRole.ADMIN)){
                user.setFunctions(functionSelector.getAdminFunctions(session));
                user.setRoleName(role.toString());
            }
            if(role.equals(User.DefaultRole.ADMIN_SECURITY)){
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
            if (region != null && region.length() > 0) {
                user.setRegion(region);
            } else {
                user.setRegion(null);
            }
            user.setNeedChangePassword(needChangePassword);
            user.getUserOrgses().clear();
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

        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        if(role.equals(User.DefaultRole.MONITORING) && !user.getFunctions().equals(functionSelector.getMonitoringFunctions(session))) {
            return true;
        }
        if(role.equals(User.DefaultRole.ADMIN) && !user.getFunctions().equals(functionSelector.getAdminFunctions(session))) {
            return true;
        }
        if(role.equals(User.DefaultRole.ADMIN_SECURITY) && !user.getFunctions().equals(functionSelector.getSecurityAdminFunctions(session))) {
            return true;
        }
        if (User.DefaultRole.DEFAULT.equals(role) && !user.getFunctions().equals(functionSelector.getSelected(session))) {
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

    public void blockedChange(ValueChangeEvent e){
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

    public void setSelectOrgType(int id) {
        this.selectOrgType = UserNotificationType.values()[id];
    }


    public Boolean getIsDefault(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
    }

    public Boolean getIsSecurityAdmin(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.ADMIN_SECURITY);
    }

    public Boolean getIsSupplier() {
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SUPPLIER);
    }

    public Boolean getIsSupplierReport() {
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SUPPLIER_REPORT);
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

    private void fill(Session session, User user) throws Exception {
        this.contragentItems.clear();
        this.orgItems.clear();
        this.orgItemsCanceled.clear();
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
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

        this.idOfRole = user.getIdOfRole();
        this.roleName = user.getRoleName();
        this.blocked = user.isBlocked();
        this.blockedUntilDate = user.getBlockedUntilDate();
        this.region = user.getRegion();
        this.needChangePassword = user.getNeedChangePassword();
        initRegions(session);
    }

    public Boolean getNeedChangePassword() {
        return needChangePassword;
    }

    public void setNeedChangePassword(Boolean needChangePassword) {
        this.needChangePassword = needChangePassword;
    }

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
        private final Long idOfOrg;
        private final String shortName;

        OrgItem(Org org) {
            this(org.getIdOfOrg(), org.getShortName());
        }

        public OrgItem(Long idOfOrg, String shortName) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
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
}