package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientsCategorydiscounts {

    private long idofclienscategorydiscount;

    public long getIdofclienscategorydiscount() {
        return idofclienscategorydiscount;
    }

    public void setIdofclienscategorydiscount(long idofclienscategorydiscount) {
        this.idofclienscategorydiscount = idofclienscategorydiscount;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofcategorydiscount;

    public long getIdofcategorydiscount() {
        return idofcategorydiscount;
    }

    public void setIdofcategorydiscount(long idofcategorydiscount) {
        this.idofcategorydiscount = idofcategorydiscount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientsCategorydiscounts that = (CfClientsCategorydiscounts) o;

        if (idofcategorydiscount != that.idofcategorydiscount) {
            return false;
        }
        if (idofclienscategorydiscount != that.idofclienscategorydiscount) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofclienscategorydiscount ^ (idofclienscategorydiscount >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofcategorydiscount ^ (idofcategorydiscount >>> 32));
        return result;
    }

    private CfCategorydiscounts cfCategorydiscountsByIdofcategorydiscount;

    public CfCategorydiscounts getCfCategorydiscountsByIdofcategorydiscount() {
        return cfCategorydiscountsByIdofcategorydiscount;
    }

    public void setCfCategorydiscountsByIdofcategorydiscount(
            CfCategorydiscounts cfCategorydiscountsByIdofcategorydiscount) {
        this.cfCategorydiscountsByIdofcategorydiscount = cfCategorydiscountsByIdofcategorydiscount;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }
}
