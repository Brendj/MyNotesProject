package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfComplexinfoDiscountdetail {

    private long idofdiscountdetail;

    public long getIdofdiscountdetail() {
        return idofdiscountdetail;
    }

    public void setIdofdiscountdetail(long idofdiscountdetail) {
        this.idofdiscountdetail = idofdiscountdetail;
    }

    private double size;

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    private int isallgroups;

    public int getIsallgroups() {
        return isallgroups;
    }

    public void setIsallgroups(int isallgroups) {
        this.isallgroups = isallgroups;
    }

    private long idofclientgroup;

    public long getIdofclientgroup() {
        return idofclientgroup;
    }

    public void setIdofclientgroup(long idofclientgroup) {
        this.idofclientgroup = idofclientgroup;
    }

    private int maxcount;

    public int getMaxcount() {
        return maxcount;
    }

    public void setMaxcount(int maxcount) {
        this.maxcount = maxcount;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfComplexinfoDiscountdetail that = (CfComplexinfoDiscountdetail) o;

        if (idofclientgroup != that.idofclientgroup) {
            return false;
        }
        if (idofdiscountdetail != that.idofdiscountdetail) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (isallgroups != that.isallgroups) {
            return false;
        }
        if (maxcount != that.maxcount) {
            return false;
        }
        if (Double.compare(that.size, size) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (idofdiscountdetail ^ (idofdiscountdetail >>> 32));
        temp = size != +0.0d ? Double.doubleToLongBits(size) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + isallgroups;
        result = 31 * result + (int) (idofclientgroup ^ (idofclientgroup >>> 32));
        result = 31 * result + maxcount;
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        return result;
    }

    private CfClientgroups cfClientgroups;

    public CfClientgroups getCfClientgroups() {
        return cfClientgroups;
    }

    public void setCfClientgroups(CfClientgroups cfClientgroups) {
        this.cfClientgroups = cfClientgroups;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
