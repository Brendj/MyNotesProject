package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientscomplexdiscounts {

    private long idofclientcomplexdiscount;

    public long getIdofclientcomplexdiscount() {
        return idofclientcomplexdiscount;
    }

    public void setIdofclientcomplexdiscount(long idofclientcomplexdiscount) {
        this.idofclientcomplexdiscount = idofclientcomplexdiscount;
    }

    private long createdate;

    public long getCreatedate() {
        return createdate;
    }

    public void setCreatedate(long createdate) {
        this.createdate = createdate;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofrule;

    public long getIdofrule() {
        return idofrule;
    }

    public void setIdofrule(long idofrule) {
        this.idofrule = idofrule;
    }

    private long idofcategoryorg;

    public long getIdofcategoryorg() {
        return idofcategoryorg;
    }

    public void setIdofcategoryorg(long idofcategoryorg) {
        this.idofcategoryorg = idofcategoryorg;
    }

    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    private int operationar;

    public int getOperationar() {
        return operationar;
    }

    public void setOperationar(int operationar) {
        this.operationar = operationar;
    }

    private int idofcomplex;

    public int getIdofcomplex() {
        return idofcomplex;
    }

    public void setIdofcomplex(int idofcomplex) {
        this.idofcomplex = idofcomplex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientscomplexdiscounts that = (CfClientscomplexdiscounts) o;

        if (createdate != that.createdate) {
            return false;
        }
        if (idofcategoryorg != that.idofcategoryorg) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idofclientcomplexdiscount != that.idofclientcomplexdiscount) {
            return false;
        }
        if (idofcomplex != that.idofcomplex) {
            return false;
        }
        if (idofrule != that.idofrule) {
            return false;
        }
        if (operationar != that.operationar) {
            return false;
        }
        if (priority != that.priority) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofclientcomplexdiscount ^ (idofclientcomplexdiscount >>> 32));
        result = 31 * result + (int) (createdate ^ (createdate >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofrule ^ (idofrule >>> 32));
        result = 31 * result + (int) (idofcategoryorg ^ (idofcategoryorg >>> 32));
        result = 31 * result + priority;
        result = 31 * result + operationar;
        result = 31 * result + idofcomplex;
        return result;
    }
}
