/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 17.11.11
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
public class POS {
    private long idOfPos;
    private Contragent contragent;
    private String name;
    private String description;
    private Date createdDate;
    private int state;
    private int flags;
    private String publicKey;
    private Set<Order> orders = new HashSet<Order>();

    public POS() {
        // For Hibernate only
    }

    public POS(long idOfPos, Contragent contragent, String name, String description, Date createdDate, int state,
            int flags, String publicKey) {
        this.idOfPos = idOfPos;
        this.contragent = contragent;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.state = state;
        this.flags = flags;
        this.publicKey = publicKey;
    }

    public long getIdOfPos() {
        return idOfPos;
    }

    public void setIdOfPos(long idOfPos) {
        this.idOfPos = idOfPos;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    private Set<Order> getOrdersInternal() {
        // For Hibernate only
        return orders;
    }

    private void setOrdersInternal(Set<Order> orders) {
        // For Hibernate only
        this.orders = orders;
    }

    public Set<Order> getOrders() {
        return Collections.unmodifiableSet(getOrdersInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        POS pos = (POS) o;

        if (idOfPos != pos.idOfPos) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfPos ^ (idOfPos >>> 32));
    }

    @Override
    public String toString() {
        return "POS{" + "idOfPos=" + idOfPos + ", contragent=" + contragent + ", name='" + name + '\''
                + ", description='" + description + '\'' + ", createdDate=" + createdDate + ", state=" + state
                + ", flags=" + flags + ", publicKey=" + publicKey + '}';
    }
}
