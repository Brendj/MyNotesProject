/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.jboss.as.web.security.SecurityContextAssociationValve;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserCreatePage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList{

    private Long idOfUser;
    private String userName;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    private List<ContragentItem> contragentItems = new ArrayList<ContragentItem> ();
    private final FunctionSelector functionSelector = new FunctionSelector();
    private Integer idOfRole;
    private String roleName;
    private final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    //private Contragent currentContragent;
    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private SelectItem[] regions;
    private String region;
    private String orgFilter = "Не выбрано";
    private String orgFilterCanceled = "Не выбрано";
    private UserNotificationType selectOrgType;
    private String orgIds;
    private Boolean needChangePassword;
    protected List<OrgItem> orgItems = new ArrayList<OrgItem>(0);
    protected List<OrgItem> orgItemsCanceled = new ArrayList<OrgItem>(0);

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
            User.DefaultRole role = User.DefaultRole.parse(idOfRole);
            user.setIdOfRole(idOfRole);
            user.setBlocked(false);
            user.setPasswordDate(new Date(System.currentTimeMillis()));
            if (User.DefaultRole.DEFAULT.equals(role)) {
                if (StringUtils.isEmpty(roleName)) {
                    this.printError("Заполните имя роли");
                    throw new Exception("Не заполнено имя роли");
                }
                user.setRoleName(this.roleName);
                user.setFunctions(functionSelector.getSelected(session));
            }
            if (role.equals(User.DefaultRole.SUPPLIER)) {
                user.setFunctions(functionSelector.getSupplierFunctions(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагентов пуст.");
                    throw new RuntimeException("Contragent list is empty");
                }
            }
            if (role.equals(User.DefaultRole.SUPPLIER_REPORT)) {
                user.setFunctions(functionSelector.getSupplierReportFunctions(session));
                user.setRoleName(role.toString());
                if (contragentItems.isEmpty()) {
                    this.printError("Список контрагенотов пуст.");
                    throw new RuntimeException("Contragent list is empty");
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
            if (role.equals(User.DefaultRole.SUPPLIER_REPORT)) {
                user.setFunctions(functionSelector.getSupplierReportFunctions(session));
                user.setRoleName(role.toString());
            }
            if (role.equals(User.DefaultRole.CARD_OPERATOR)){
                user.setFunctions(functionSelector.getCardOperatorFunctions(session));
                user.setRoleName(role.toString());
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

    public Boolean getIsCardOperator() {
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.CARD_OPERATOR);
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
            case GOOD_REQUEST_CHANGE_NOTIFY: return orgItems.toString().replaceAll("[^0-9,]","");
            case ORDER_STATE_CHANGE_NOTIFY: return orgItemsCanceled.toString().replaceAll("[^0-9,]","");
        }
        return "";
    }

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
}