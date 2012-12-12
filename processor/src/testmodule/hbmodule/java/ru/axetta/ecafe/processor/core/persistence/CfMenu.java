package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfMenu {

    private long idofmenu;

    public long getIdofmenu() {
        return idofmenu;
    }

    public void setIdofmenu(long idofmenu) {
        this.idofmenu = idofmenu;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long menudate;

    public long getMenudate() {
        return menudate;
    }

    public void setMenudate(long menudate) {
        this.menudate = menudate;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private int menusource;

    public int getMenusource() {
        return menusource;
    }

    public void setMenusource(int menusource) {
        this.menusource = menusource;
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

        CfMenu cfMenu = (CfMenu) o;

        if (createddate != cfMenu.createddate) {
            return false;
        }
        if (flags != cfMenu.flags) {
            return false;
        }
        if (idofmenu != cfMenu.idofmenu) {
            return false;
        }
        if (idoforg != cfMenu.idoforg) {
            return false;
        }
        if (menudate != cfMenu.menudate) {
            return false;
        }
        if (menusource != cfMenu.menusource) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofmenu ^ (idofmenu >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (menudate ^ (menudate >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + menusource;
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

    private Collection<CfMenudetails> cfMenudetailsesByIdofmenu;

    public Collection<CfMenudetails> getCfMenudetailsesByIdofmenu() {
        return cfMenudetailsesByIdofmenu;
    }

    public void setCfMenudetailsesByIdofmenu(Collection<CfMenudetails> cfMenudetailsesByIdofmenu) {
        this.cfMenudetailsesByIdofmenu = cfMenudetailsesByIdofmenu;
    }
}
