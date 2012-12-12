package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfPermissions {

    private long idofuser;

    public long getIdofuser() {
        return idofuser;
    }

    public void setIdofuser(long idofuser) {
        this.idofuser = idofuser;
    }

    private long idoffunction;

    public long getIdoffunction() {
        return idoffunction;
    }

    public void setIdoffunction(long idoffunction) {
        this.idoffunction = idoffunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfPermissions that = (CfPermissions) o;

        if (idoffunction != that.idoffunction) {
            return false;
        }
        if (idofuser != that.idofuser) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofuser ^ (idofuser >>> 32));
        result = 31 * result + (int) (idoffunction ^ (idoffunction >>> 32));
        return result;
    }

    private CfFunctions cfFunctionsByIdoffunction;

    public CfFunctions getCfFunctionsByIdoffunction() {
        return cfFunctionsByIdoffunction;
    }

    public void setCfFunctionsByIdoffunction(CfFunctions cfFunctionsByIdoffunction) {
        this.cfFunctionsByIdoffunction = cfFunctionsByIdoffunction;
    }

    private CfUsers cfUsersByIdofuser;

    public CfUsers getCfUsersByIdofuser() {
        return cfUsersByIdofuser;
    }

    public void setCfUsersByIdofuser(CfUsers cfUsersByIdofuser) {
        this.cfUsersByIdofuser = cfUsersByIdofuser;
    }
}
