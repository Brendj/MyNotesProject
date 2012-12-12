package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiaryvalues {

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofclass;

    public long getIdofclass() {
        return idofclass;
    }

    public void setIdofclass(long idofclass) {
        this.idofclass = idofclass;
    }

    private long recdate;

    public long getRecdate() {
        return recdate;
    }

    public void setRecdate(long recdate) {
        this.recdate = recdate;
    }

    private int vtype;

    public int getVtype() {
        return vtype;
    }

    public void setVtype(int vtype) {
        this.vtype = vtype;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiaryvalues that = (CfDiaryvalues) o;

        if (idofclass != that.idofclass) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (recdate != that.recdate) {
            return false;
        }
        if (vtype != that.vtype) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofclass ^ (idofclass >>> 32));
        result = 31 * result + (int) (recdate ^ (recdate >>> 32));
        result = 31 * result + vtype;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }

    private CfDiaryclasses cfDiaryclasses;

    public CfDiaryclasses getCfDiaryclasses() {
        return cfDiaryclasses;
    }

    public void setCfDiaryclasses(CfDiaryclasses cfDiaryclasses) {
        this.cfDiaryclasses = cfDiaryclasses;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
