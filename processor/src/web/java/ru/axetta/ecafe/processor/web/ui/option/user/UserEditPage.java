/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserEditPage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler{

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
    private String contragentFilter;
    private String contragentIds;
    private Boolean blocked;
    private SelectItem[] regions;
    private String region;

    public void setFunctionSelector(FunctionSelector functionSelector) {
        this.functionSelector = functionSelector;
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        fill(session, user);
    }

    public void updateUser(Session session, Long idOfUser) throws Exception {
        if (StringUtils.isEmpty(userName)) {
            this.printError("Заполните имя пользователя");
            throw new RuntimeException("Username field is null");
        }
        User user = (User) session.load(User.class, idOfUser);
        user.setUserName(userName);
        if (changePassword) {
            user.setPassword(plainPassword);
        }
        user.setPhone(phone);
        user.setEmail(email);
        user.setUpdateTime(new Date());
        user.setBlocked(blocked);
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
        if (User.DefaultRole.DEFAULT.equals(role)) {
            if (StringUtils.isEmpty(roleName)) {
                this.printError("Заполните имя роли");
                throw new RuntimeException("Role name fields is null");
            }
            user.setFunctions(functionSelector.getSelected(session));
            user.setRoleName(this.roleName);
        }
        if (region != null && region.length() > 0) {
            user.setRegion(region);
        } else {
            user.setRegion(null);
        }
        session.update(user);
        fill(session, user);
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

    public Boolean getIsDefault(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
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
        for (ContragentItem it : contragentItems) {
            if (str.length() > 0) {
                str.append("; ");
                ids.append(",");
            }
            str.append(it.getContragentName());
            ids.append(it.getIdOfContragent());
        }
        contragentFilter = str.toString();
        contragentIds = ids.toString();
    }

    private void fill(Session session, User user) throws Exception {
        this.contragentItems.clear();
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.functionSelector.fill(session, user.getFunctions());
        for (Contragent c : user.getContragents()) {
            this.contragentItems.add(new ContragentItem(c));
        }
        setContragentFilterInfo(contragentItems);
        this.idOfRole = user.getIdOfRole();
        this.roleName = user.getRoleName();
        this.blocked = user.isBlocked();
        this.region = user.getRegion();
        initRegions(session);
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
}