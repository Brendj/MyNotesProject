package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientgroups {

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofclientgroup;

    public long getIdofclientgroup() {
        return idofclientgroup;
    }

    public void setIdofclientgroup(long idofclientgroup) {
        this.idofclientgroup = idofclientgroup;
    }

    private String groupname;

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientgroups that = (CfClientgroups) o;

        if (idofclientgroup != that.idofclientgroup) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (groupname != null ? !groupname.equals(that.groupname) : that.groupname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofclientgroup ^ (idofclientgroup >>> 32));
        result = 31 * result + (groupname != null ? groupname.hashCode() : 0);
        return result;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }

    private Collection<CfComplexinfoDiscountdetail> cfComplexinfoDiscountdetails;

    public Collection<CfComplexinfoDiscountdetail> getCfComplexinfoDiscountdetails() {
        return cfComplexinfoDiscountdetails;
    }

    public void setCfComplexinfoDiscountdetails(Collection<CfComplexinfoDiscountdetail> cfComplexinfoDiscountdetails) {
        this.cfComplexinfoDiscountdetails = cfComplexinfoDiscountdetails;
    }

}
