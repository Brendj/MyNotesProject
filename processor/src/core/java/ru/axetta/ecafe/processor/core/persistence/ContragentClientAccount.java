/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class ContragentClientAccount {

    private CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount;
    private Contragent contragent;
    private Client client;
    private Long idOfAccount;

    ContragentClientAccount() {
        // For Hibernate only
    }

    public ContragentClientAccount(CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount,
            Client client) {
        this.compositeIdOfContragentClientAccount = compositeIdOfContragentClientAccount;
        this.client = client;
    }

    public CompositeIdOfContragentClientAccount getCompositeIdOfContragentClientAccount() {
        return compositeIdOfContragentClientAccount;
    }

    private void setCompositeIdOfContragentClientAccount(
            CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount) {
        // For Hibernate only
        this.compositeIdOfContragentClientAccount = compositeIdOfContragentClientAccount;
    }

    public Contragent getContragent() {
        return contragent;
    }

    private void setContragent(Contragent contragent) {
        // For Hibernate only
        this.contragent = contragent;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public Long getIdOfAccount() {
        return idOfAccount;
    }

    private void setIdOfAccount(Long idOfAccount) {
        // For Hibernate only
        this.idOfAccount = idOfAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContragentClientAccount)) {
            return false;
        }
        final ContragentClientAccount that = (ContragentClientAccount) o;
        return compositeIdOfContragentClientAccount.equals(that.getCompositeIdOfContragentClientAccount());
    }

    @Override
    public int hashCode() {
        return compositeIdOfContragentClientAccount.hashCode();
    }

    @Override
    public String toString() {
        return "ContragentClientAccount{" + "compositeIdOfContragentClientAccount="
                + compositeIdOfContragentClientAccount + ", client=" + client + '}';
    }
}