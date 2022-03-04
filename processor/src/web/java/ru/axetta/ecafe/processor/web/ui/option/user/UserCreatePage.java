/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserCreatePage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList,
        OrgMainBuildingListSelectPage.CompleteHandler, OrgSelectPage.CompleteHandler, ClientSelectPage.CompleteHandler  {

    protected Long idOfUser;
    protected String userName;
    protected String plainPassword;
    protected String plainPasswordConfirmation;
    protected String phone;
    protected String email;
    protected List<ContragentItem> contragentItems = new ArrayList<ContragentItem> ();
    protected final FunctionSelector functionSelector = new FunctionSelector();
    protected Integer idOfRole;
    protected String roleName;
    protected final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    //private Contragent currentContragent;
    protected String contragentFilter = "Не выбрано";
    protected String contragentIds;
    protected SelectItem[] regions;
    protected String region;
    protected String orgItemsForUserFilter = "Не выбрано";
    protected String orgFilter = "Не выбрано";
    protected String orgFilterCanceled = "Не выбрано";
    protected String organizationsFilter = "Не выбрано";
    protected UserNotificationType selectOrgType;
    protected String orgIds;
    protected Boolean needChangePassword;
    protected List<OrgItem> orgItemsForUser = new ArrayList<OrgItem>(0);
    protected List<OrgItem> orgItems = new ArrayList<OrgItem>(0);
    protected List<OrgItem> orgItemsCanceled = new ArrayList<OrgItem>(0);
    protected List<OrgItem> organizationItems = new ArrayList<OrgItem>(0);
    protected Long organizationId;
    protected String firstName;
    protected String surname;
    protected String secondName;
    protected String department;
    protected OrgItem userOrg = new OrgItem(null, null);
    protected String userOrgName = "Не выбрано";
    protected Long userIdOfClient;
    protected String userClientName = "Не выбрано";
    protected Date deleteDate;

    public void setIdOfRole(Integer idOfRole) {
        this.idOfRole = idOfRole;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public UserRoleEnumTypeMenu getUserRoleEnumTypeMenu() {
        return userRoleEnumTypeMenu;
    }

    public String getPageFilename() {
        return "option/user/create";
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

    /*public ContragentItem getContragentItem() {
        return contragentItem;
    }

    public void setContragentItem(ContragentItem contragentItem) {
        this.contragentItem = contragentItem;
    }*/

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

    public SelectItem[] getRegions() {
        return regions;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOrgItemsForUserFilter() {
        return orgItemsForUserFilter;
    }

    public void setOrgItemsForUserFilter(String orgItemsForUserFilter) {
        this.orgItemsForUserFilter = orgItemsForUserFilter;
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

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public void fill(Session session) throws Exception {
        this.needChangePassword = true;
        this.functionSelector.fill(session);
        this.idOfRole = User.DefaultRole.DEFAULT.getIdentification();
        initRegions(session);
    }

    private void initRegions (Session session) {
        List<String> regions = DAOUtils.getRegions(session);
        regions.add(0, "");
        this.regions = new SelectItem[regions.size()];
        for (int i=0; i<regions.size(); i++) {
            this.regions [i] = new SelectItem(regions.get(i));
        }
    }

    public void createUser(Session session) throws Exception {
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
                    throw new Exception("Неверный формат контактного (мобильного) телефона");
                }
                phone = mobile;
            }
            if (!StringUtils.equals(plainPassword, plainPasswordConfirmation)) {
                throw new Exception("Пароль и подтверждение пароля не совпадают");
            } else if (!User.passwordIsEnoughComplex(plainPassword)) {
                this.printError("Пароль не удовлетворяет требованиям безопасности: ");
                this.printError("- минимальная длина - 6 символов, ");
                this.printError("- должны присутствовать прописные и заглавные латинские буквы + хотя бы одна цифра или спецсимвол");
                throw new RuntimeException("Bad password");
            }
            User user = new User(userName, plainPassword, phone, new Date());
            user.setEmail(email);
            user.setDeleteDate(deleteDate);
            User.DefaultRole role = null;
            if (idOfRole < UserRoleEnumTypeMenu.OFFSET) {
                role = User.DefaultRole.parse(idOfRole);
            }
            user.setIdOfRole(idOfRole);
            user.setBlocked(false);
            user.setPasswordDate(new Date(System.currentTimeMillis()));
            if(!firstName.isEmpty() || !surname.isEmpty() || !secondName.isEmpty()) {
                Person person = new Person(firstName, surname, secondName);
                user.setPerson(person);
                session.save(person);
            }
            if(userIdOfClient != null && !userIdOfClient.equals(0L)){
                Client userClient = (Client) session.load(Client.class, userIdOfClient);
                user.setClient(userClient);
                if(userClient != null && userClient.getOrg() != null)
                    user.setOrg(userClient.getOrg());
                else if(userOrg != null && userOrg.getIdOfOrg() != null)
                    user.setOrg((Org) session.load(Org.class, userOrg.getIdOfOrg()));
            }
            else if(userOrg != null && userOrg.getIdOfOrg() != null) {
                user.setOrg((Org) session.load(Org.class, userOrg.getIdOfOrg()));
            }

            if (role != null && User.DefaultRole.DEFAULT.equals(role)) {
                if (StringUtils.isEmpty(roleName)) {
                    this.printError("Заполните имя роли");
                    throw new Exception("Не заполнено имя роли");
                }
                user.setRoleName(this.roleName);
                user.setFunctions(functionSelector.getSelected(session));
            }
            if (role != null && role.equals(User.DefaultRole.SUPPLIER)) {
                user.setFunctions(functionSelector.getSupplierFunctions(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагентов пуст.");
                    throw new RuntimeException("Contragent list is empty");
                }
            }
            if (role != null && role.equals(User.DefaultRole.SUPPLIER_REPORT)) {
                user.setFunctions(functionSelector.getSupplierReportFunctions(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагентов пуст.");
                    throw new RuntimeException("Contragent list is empty");
                }
            }
            if(role != null && (role.equals(User.DefaultRole.CLASSROOM_TEACHER)
                    || role.equals(User.DefaultRole.CLASSROOM_TEACHER_WITH_FOOD_PAYMENT)
                    || role.equals(User.DefaultRole.INFORMATION_SYSTEM_OPERATOR)
                    || role.equals(User.DefaultRole.WA_ADMIN_SECURITY))) {
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

            User u = DAOUtils.findUser(session, userName);
            if (u != null) {
                User.testAndMoveToArchieve(u, session);
            }

            user.getContragents().clear();
            for (ContragentItem it : this.contragentItems) {
                Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
                user.getContragents().add(contragent);
            }
            if (region != null && region.length() > 0) {
                user.setRegion(region);
            } else {
                user.setRegion(null);
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
            if (role != null && role.equals(User.DefaultRole.SUPPLIER_REPORT)) {
                user.setFunctions(functionSelector.getSupplierReportFunctions(session));
                user.setRoleName(role.toString());
            }
            if (role != null && role.equals(User.DefaultRole.CARD_OPERATOR)){
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
            user.setNeedChangePassword(needChangePassword);
            session.save(user);
            for (OrgItem orgItem : orgItems) {
                Org org = (Org) session.load(Org.class, orgItem.idOfOrg);
                UserOrgs userOrgs = new UserOrgs(user, org, UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY);
                session.save(userOrgs);
                //user.getUserOrgses().add(new UserOrgs(orgItem.idOfOrg, user.getIdOfUser()));
            }
            for (OrgItem orgItem : orgItemsCanceled) {
                Org org = (Org) session.load(Org.class, orgItem.idOfOrg);
                UserOrgs userOrgs = new UserOrgs(user, org, UserNotificationType.ORDER_STATE_CHANGE_NOTIFY);
                session.save(userOrgs);
            }
            for (OrgItem orgItem : orgItemsForUser) {
                Org org = (Org) session.load(Org.class, orgItem.idOfOrg);
                SfcUserOrgs sfcUserOrgs = new SfcUserOrgs(user, org);
                session.save(sfcUserOrgs);
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

            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CREATE_USER, request.getRemoteAddr(), currentUserName,
                            currentUser, true, null, String.format("Создан пользователь %s", userName));
            DAOService.getInstance().writeAuthJournalRecord(record);
        } catch (Exception e) {
            String comment = String.format("Ошибка при создании пользователя с именем %s. Текст ошибки: %s", userName, e.getMessage());
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CREATE_USER, request.getRemoteAddr(),
                            currentUserName, currentUser, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw e;
        }
    }

    public Boolean getIsDefault(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
    }

    public Boolean getIsDefaultOrAdmin(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT) || role.equals(User.DefaultRole.ADMIN);
    }

    public Boolean getIsSecurityAdmin(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.ADMIN_SECURITY);
    }

    public Boolean getIsWebArmUser(){
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.WA_ADMIN_SECURITY);
    }

    public Boolean getRenderContragent() {
        return !getIsSecurityAdmin() && !getIsWebArmUser() && !getIsDirector();
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

    public Boolean getIsDirector() {
        if (idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DIRECTOR);
    }

    public Boolean getIsProductionDirector() {
        if(idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.PRODUCTION_DIRECTOR);
    }

    public Boolean getIsInformationSystemOperator() {
        if(idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.INFORMATION_SYSTEM_OPERATOR);
    }

    public Boolean getIsClassroomTeacher() {
        if(idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.CLASSROOM_TEACHER) || role.equals(User.DefaultRole.CLASSROOM_TEACHER_WITH_FOOD_PAYMENT);
    }

    public Boolean getIsSchoolApiUser() {
        return getIsProductionDirector() || getIsInformationSystemOperator() || getIsClassroomTeacher();
    }

    public Boolean getIsSfc() {
        if(idOfRole > UserRoleEnumTypeMenu.OFFSET) return false;
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SFC);
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
        private final String shortName;
        private final Boolean mainBuilding;

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

    public String getGetStringIdOfOrgList() {
        switch (selectOrgType){
            case GOOD_REQUEST_CHANGE_NOTIFY: return orgItems.stream().map(o -> o.idOfOrg.toString()).collect(Collectors.joining(","));
            case ORDER_STATE_CHANGE_NOTIFY: return orgItemsCanceled.stream().map(o -> o.idOfOrg.toString()).collect(Collectors.joining(","));
            }
        return "";
    }

    public String getStringSfcIdOfOrgString() {
        return orgItemsForUser.stream().map(o -> o.idOfOrg.toString()).collect(Collectors.joining(","));
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (selectOrgType == null) {
            if (orgMap != null) {
                orgItemsForUser = new ArrayList<OrgItem>();
                if (orgMap.isEmpty()) {
                    orgItemsForUserFilter = "Не выбрано";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Long idOfOrg : orgMap.keySet()) {
                        orgItemsForUser.add(new OrgItem(idOfOrg, orgMap.get(idOfOrg)));
                        stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                    }
                    orgItemsForUserFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
                }
            }
        } else
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

    public Object showSelectOrgListPage(){
        selectOrgType = null;
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
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

    public Object showOrgListPage(){
        MainPage.getSessionInstance().showOrgMainBuildingListSelectPage();
        return null;
    }

    public String getOrganizationsFilter() {
        return organizationsFilter;
    }

    public void setOrganizationsFilter(String organizationsFilter) {
        this.organizationsFilter = organizationsFilter;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public OrgItem getUserOrg() { return userOrg; }

    public void setUserOrg(OrgItem userOrg) { this.userOrg = userOrg; }

    public String getUserOrgName() { return this.userOrgName; }

    public void setUserOrgName(String userOrgName) { this.userOrgName = userOrgName; }

    public Long getUserIdOfClient() { return userIdOfClient; }

    public void setUserIdOfClient(Long userIdOfClient) { this.userIdOfClient = userIdOfClient; }

    public String getUserClientName() { return userClientName; }

    public void setUserClientName(String userClientName) { this.userClientName = userClientName; }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Override
    public void completeOrgMainBuildingListSelection(Session session, List<Org> orgs) throws Exception {
        if (getIsDirector()) {
            if (null != orgs) {
                organizationItems = new ArrayList<UserCreatePage.OrgItem>();
                if (orgs.isEmpty()) {
                    organizationsFilter = "Не выбрано";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Org org : orgs) {
                        organizationItems.add(new UserCreatePage.OrgItem(org.getIdOfOrg(), org.getShortName(), org.isMainBuilding()));
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
            this.firstName = null;
            this.secondName = null;
            this.surname = null;
            this.userOrg = new OrgItem(null,null);
            this.userOrgName = "Не выбрано";
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