package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfMenuexchange {

    private long menudate;

    public long getMenudate() {
        return menudate;
    }

    public void setMenudate(long menudate) {
        this.menudate = menudate;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private String menudata;

    public String getMenudata() {
        return menudata;
    }

    public void setMenudata(String menudata) {
        this.menudata = menudata;
    }

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfMenuexchange that = (CfMenuexchange) o;

        if (flags != that.flags) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (menudate != that.menudate) {
            return false;
        }
        if (menudata != null ? !menudata.equals(that.menudata) : that.menudata != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (menudate ^ (menudate >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (menudata != null ? menudata.hashCode() : 0);
        result = 31 * result + flags;
        return result;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
