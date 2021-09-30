/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class Person {

    private Long idOfPerson;
    private String firstName;
    private String surname;
    private String secondName;
    private String idDocument;
    private Set<Contragent> contragents = new HashSet<Contragent>();
    //private Set<Org> orgs = new HashSet<Org>();
    private Set<Client> clients = new HashSet<Client>();
    private Set<Client> contractClients = new HashSet<Client>();

    Person() {
        // For Hibernate only
    }

    public Person(String firstName, String surname, String secondName) {
        this.firstName = firstName;
        this.surname = surname;
        this.secondName = secondName;
    }

    public Long getIdOfPerson() {
        return idOfPerson;
    }

    private void setIdOfPerson(Long idOfPerson) {
        // For Hibernate only
        this.idOfPerson = idOfPerson;
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

    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    private Set<Contragent> getContragentsInternal() {
        // For Hibernate only
        return contragents;
    }

    private void setContragentsInternal(Set<Contragent> contragents) {
        // For Hibernate only
        this.contragents = contragents;
    }

    public Set<Contragent> getContragents() {
        return Collections.unmodifiableSet(getContragentsInternal());
    }

    //private Set<Org> getOrgsInternal() {
    //    // For Hibernate only
    //    return orgs;
    //}
    //
    //private void setOrgsInternal(Set<Org> orgs) {
    //    // For Hibernate only
    //    this.orgs = orgs;
    //}
    //
    //public Set<Org> getOrgs() {
    //    return Collections.unmodifiableSet(getOrgsInternal());
    //}

    private Set<Client> getClientsInternal() {
        // For Hibernate only
        return clients;
    }

    private void setClientsInternal(Set<Client> clients) {
        // For Hibernate only
        this.clients = clients;
    }

    public Set<Client> getClients() {
        return Collections.unmodifiableSet(getClientsInternal());
    }

    private Set<Client> getContractClientsInternal() {
        // For Hibernate only
        return contractClients;
    }

    private void setContractClientsInternal(Set<Client> contractClients) {
        // For Hibernate only
        this.contractClients = contractClients;
    }

    public Set<Client> getContractClients() {
        return Collections.unmodifiableSet(getContractClientsInternal());
    }

    //public boolean isRelatedOnlyWith(Org org) {
    //    if (orgs.isEmpty()) {
    //        return false;
    //    }
    //    if (!contragents.isEmpty()) {
    //        return false;
    //    }
    //    if (!clients.isEmpty()) {
    //        return false;
    //    }
    //    if (!contractClients.isEmpty()) {
    //        return false;
    //    }
    //    return 1 == orgs.size() && orgs.contains(org);
    //}

    public boolean isRelatedOnlyWith(Client client) {
        if (!contragents.isEmpty()) {
            return false;
        }
        //if (!orgs.isEmpty()) {
        //    return false;
        //}
        boolean hasClients = null != clients && !clients.isEmpty();
        boolean clientsContains = false;
        if (hasClients) {
            clientsContains = 1 == clients.size() && clients.contains(client);
        }
        boolean hasContractClients = null != contractClients && !contractClients.isEmpty();
        boolean contractClientsContains = false;
        if (hasContractClients) {
            contractClientsContains = 1 == contractClients.size() && contractClients.contains(client);
        }
        return (hasClients || hasContractClients) && (clientsContains || !hasClients) && (contractClientsContains
                || !hasContractClients);
    }

    public boolean isRelatedOnlyWith(Contragent contragent) {
        if (null == contragents) {
            return false;
        }
        //if (null != orgs && !orgs.isEmpty()) {
        //    return false;
        //}
        if (null != clients && !clients.isEmpty()) {
            return false;
        }
        if (null != contractClients && !contractClients.isEmpty()) {
            return false;
        }
        return 1 == contragents.size() && contragents.contains(contragent);
    }

    public String getSurnameAndFirstLetters() {

        String n = getSurname();
        String fn = getFirstName();
        if (fn.length()==0) return n;
        fn = fn.substring(0, 1)+".";
        String sn = getSecondName();
        if (sn.length()!=0) sn=sn.substring(0, 1)+".";
        return n+" "+fn+sn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        final Person person = (Person) o;
        return idOfPerson.equals(person.getIdOfPerson());
    }

    @Override
    public int hashCode() {
        return idOfPerson.hashCode();
    }

    @Override
    public String toString() {
        return "Person{" + "idOfPerson=" + idOfPerson + ", firstName='" + firstName + '\'' + ", surname='" + surname
                + '\'' + ", secondName='" + secondName + '\'' + ", idDocument='" + idDocument + '\'' + '}';
    }
}