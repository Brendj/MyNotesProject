/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class User {

    public enum DefaultRole{
        DEFAULT(0,"настраиваемая роль"),
        ADMIN(1,"администратор"),
        SUPPLIER(2,"отчетность поставщика питания"),
        MONITORING(3,"мониторинг");

        private Integer identification;
        private String description;

        static Map<Integer,DefaultRole> integerDefaultRoleMap = new HashMap<Integer, DefaultRole>();
        static {
            for (DefaultRole defaultRole: DefaultRole.values()){
                integerDefaultRoleMap.put(defaultRole.identification,defaultRole);
            }
        }

        private DefaultRole(Integer identification, String description) {
            this.description = description;
            this.identification = identification;
        }

        public static DefaultRole parse(Integer identification){
            return integerDefaultRoleMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        public static Boolean isDefault(Integer identification){
            return DEFAULT.equals(integerDefaultRoleMap.get(identification));
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private Long idOfUser;
    private long version;
    private String userName;
    private String cypheredPassword;
    private String phone;
    private Date updateTime;
    private Set<Contragent> contragents = new HashSet<Contragent>();
    private Set<Function> functions = new HashSet<Function>();
    private String email;
    private Integer idOfRole;
    private String roleName;
    private String lastEntryIP;
    private Date lastEntryTime;
    private Boolean blocked;
    private String region;
    private Set<UserOrgs> userOrgses = new HashSet<UserOrgs>();

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public void setIdOfRole(Integer idOfRole) {
        this.idOfRole = idOfRole;
    }

    public Boolean isDefaultRole(){
         return DefaultRole.isDefault(idOfRole);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected User() {
        // For Hibernate
    }

    public User(String userName, String plainPassword, String phone, Date updateTime) throws Exception {
        this.userName = userName;
        this.cypheredPassword = encryptPassword(plainPassword);
        this.phone = phone;
        this.updateTime = updateTime;
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    private void setIdOfUser(Long idOfUser) {
        // For Hibernate
        this.idOfUser = idOfUser;
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String getCypheredPassword() {
        // For Hibernate
        return cypheredPassword;
    }

    private void setCypheredPassword(String cypheredPassword) throws Exception {
        // For Hibernate
        this.cypheredPassword = cypheredPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Set<Contragent> getContragents() {
        return contragents;
    }

    public void setContragents(Set<Contragent> contragents) {
        this.contragents = contragents;
    }

    private Set<Function> getFunctionsInternal() {
        // For Hibernate only 
        return functions;
    }

    private void setFunctionsInternal(Set<Function> functions) {
        // For Hibernate only
        this.functions = functions;
    }

    public Set<Function> getFunctions() {
        return getFunctionsInternal();
    }

    public void setFunctions(Set<Function> functions) {
        this.functions = functions;
    }

    public void setPassword(String plainPassword) throws Exception {
        this.cypheredPassword = encryptPassword(plainPassword);
    }

    public String getLastEntryIP() {
        return lastEntryIP;
    }

    public void setLastEntryIP(String lastEntryIP) {
        this.lastEntryIP = lastEntryIP;
    }

    public Date getLastEntryTime() {
        return lastEntryTime;
    }

    public void setLastEntryTime(Date lastEntryTime) {
        this.lastEntryTime = lastEntryTime;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean hasPassword(String plainPassword) throws Exception {
        return StringUtils.equals(this.cypheredPassword, encryptPassword(plainPassword));
    }

    public boolean hasFunction(Session session, String functionName) throws Exception {
        List functions = session.createFilter(getFunctionsInternal(), "where this.functionName = ?")
                .setString(0, functionName).list();
        return !functions.isEmpty();
    }

    public boolean hasFunction(String functionName) throws Exception {
        Set<Function> functions = getFunctionsInternal();
        for (Function f : functions) {
            if (f.getFunctionName().equals(functionName)) {
                return true;
            }
        }
        return false;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Set<UserOrgs> getUserOrgses() {
        return userOrgses;
    }

    public void setUserOrgses(Set<UserOrgs> userOrgses) {
        this.userOrgses = userOrgses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        final User user = (User) o;
        return idOfUser.equals(user.getIdOfUser());
    }

    @Override
    public int hashCode() {
        return idOfUser.hashCode();
    }

    @Override
    public String toString() {
        return "User{" + "idOfUser=" + idOfUser + ", userName='" + userName + '\'' + ", cypheredPassword='"
                + cypheredPassword + '\'' + ", phone='" + phone + '\'' + ", updateTime=" + updateTime + ", contragents="
                + contragents.toString() + '}';
    }

    private static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(plainPassword.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }
}
