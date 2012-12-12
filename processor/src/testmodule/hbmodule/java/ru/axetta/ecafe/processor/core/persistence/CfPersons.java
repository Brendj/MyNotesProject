package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfPersons {

    private long idofperson;

    public long getIdofperson() {
        return idofperson;
    }

    public void setIdofperson(long idofperson) {
        this.idofperson = idofperson;
    }

    private String firstname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    private String surname;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    private String secondname;

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    private String iddocument;

    public String getIddocument() {
        return iddocument;
    }

    public void setIddocument(String iddocument) {
        this.iddocument = iddocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfPersons cfPersons = (CfPersons) o;

        if (idofperson != cfPersons.idofperson) {
            return false;
        }
        if (firstname != null ? !firstname.equals(cfPersons.firstname) : cfPersons.firstname != null) {
            return false;
        }
        if (iddocument != null ? !iddocument.equals(cfPersons.iddocument) : cfPersons.iddocument != null) {
            return false;
        }
        if (secondname != null ? !secondname.equals(cfPersons.secondname) : cfPersons.secondname != null) {
            return false;
        }
        if (surname != null ? !surname.equals(cfPersons.surname) : cfPersons.surname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofperson ^ (idofperson >>> 32));
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (secondname != null ? secondname.hashCode() : 0);
        result = 31 * result + (iddocument != null ? iddocument.hashCode() : 0);
        return result;
    }

    private Collection<CfClients> cfClientsesByIdofperson;

    public Collection<CfClients> getCfClientsesByIdofperson() {
        return cfClientsesByIdofperson;
    }

    public void setCfClientsesByIdofperson(Collection<CfClients> cfClientsesByIdofperson) {
        this.cfClientsesByIdofperson = cfClientsesByIdofperson;
    }

    private Collection<CfClients> cfClientsesByIdofperson_0;

    public Collection<CfClients> getCfClientsesByIdofperson_0() {
        return cfClientsesByIdofperson_0;
    }

    public void setCfClientsesByIdofperson_0(Collection<CfClients> cfClientsesByIdofperson_0) {
        this.cfClientsesByIdofperson_0 = cfClientsesByIdofperson_0;
    }

    private Collection<CfContragents> cfContragentsesByIdofperson;

    public Collection<CfContragents> getCfContragentsesByIdofperson() {
        return cfContragentsesByIdofperson;
    }

    public void setCfContragentsesByIdofperson(Collection<CfContragents> cfContragentsesByIdofperson) {
        this.cfContragentsesByIdofperson = cfContragentsesByIdofperson;
    }

    private Collection<CfOrgs> cfOrgsesByIdofperson;

    public Collection<CfOrgs> getCfOrgsesByIdofperson() {
        return cfOrgsesByIdofperson;
    }

    public void setCfOrgsesByIdofperson(Collection<CfOrgs> cfOrgsesByIdofperson) {
        this.cfOrgsesByIdofperson = cfOrgsesByIdofperson;
    }
}
