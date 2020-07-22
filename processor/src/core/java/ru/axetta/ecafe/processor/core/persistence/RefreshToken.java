/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class RefreshToken {
    private String refreshTokenHash;
    private User user;
    private String ipAddress;
    private Date expiresIn;
    private Date createdAt;

    public RefreshToken(){

    }

    public RefreshToken(String refreshTokenHash, User user, String ipAddress, Date expiresIn, Date createdAt){
        this.refreshTokenHash = refreshTokenHash;
        this.user = user;
        this.ipAddress = ipAddress;
        this.expiresIn = expiresIn;
        this.createdAt = createdAt;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Date expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
