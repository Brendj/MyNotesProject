/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 27.08.12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */

@Table(name = "cf_users")
@Entity
public class User {
    @Id
    @Column(name = "idofuser")
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator=" cf_users_idofuser_seq")
    @SequenceGenerator(name=" cf_users_idofuser_seq", sequenceName=" cf_users_idofuser_seq", allocationSize = 1)
  private Long idOfUser;
    @Column(name = "username", nullable = true, insertable = true, updatable = true, length = 64, precision = 0)
    @Basic
  private String userName;
    @Column(name = "phone", nullable = true, insertable = true, updatable = true, length = 32, precision = 0)
    @Basic
  private String phone;
    @Column(name = "email", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
  private String email;
    @Column(name = "password", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
  private String cypheredPassword;

    @Column(name = "lastchange", nullable = true, insertable = true, updatable = true,  precision = 0)
    @Basic
    @Type(type = "ru.axetta.ecafe.processor.core.persistence.DateType")
    private Date updateTime;
    @Column(name = "version", nullable = true, insertable = true, updatable = true,  precision = 0)
    @Basic
    @Type(type="long")
    private long version;


    @JoinTable(name="cf_permissions",
            joinColumns=@JoinColumn(name="idofuser"),
            inverseJoinColumns=@JoinColumn(name="idoffunction") )
    @ManyToMany(targetEntity = Function.class,fetch = FetchType.EAGER)
    private Set<Function> functions = new HashSet<Function>();

    User() {
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
                + cypheredPassword + '\'' + ", phone='" + phone + '\'' + ", updateTime=" + updateTime + ", contragent="
                +'}';
    }

    /*TODO: replase has method*/
    //private static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
    //    MessageDigest hash = MessageDigest.getInstance("SHA1");
    //    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(plainPassword.getBytes());
    //    DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
    //    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    //    IOUtils.copy(digestInputStream, arrayOutputStream);
    //    return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    //}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
        final byte[] plainPasswordBytes = plainPassword.getBytes(CharEncoding.UTF_8);
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        return new String(Base64.encodeBase64(messageDigest.digest(plainPasswordBytes)), CharEncoding.US_ASCII);
    }

}
