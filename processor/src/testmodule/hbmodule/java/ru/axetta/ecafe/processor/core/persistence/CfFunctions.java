package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfFunctions {

    private long idoffunction;

    public long getIdoffunction() {
        return idoffunction;
    }

    public void setIdoffunction(long idoffunction) {
        this.idoffunction = idoffunction;
    }

    private String functionname;

    public String getFunctionname() {
        return functionname;
    }

    public void setFunctionname(String functionname) {
        this.functionname = functionname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfFunctions that = (CfFunctions) o;

        if (idoffunction != that.idoffunction) {
            return false;
        }
        if (functionname != null ? !functionname.equals(that.functionname) : that.functionname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoffunction ^ (idoffunction >>> 32));
        result = 31 * result + (functionname != null ? functionname.hashCode() : 0);
        return result;
    }

    private Collection<CfPermissions> cfPermissionsesByIdoffunction;

    public Collection<CfPermissions> getCfPermissionsesByIdoffunction() {
        return cfPermissionsesByIdoffunction;
    }

    public void setCfPermissionsesByIdoffunction(Collection<CfPermissions> cfPermissionsesByIdoffunction) {
        this.cfPermissionsesByIdoffunction = cfPermissionsesByIdoffunction;
    }
}
