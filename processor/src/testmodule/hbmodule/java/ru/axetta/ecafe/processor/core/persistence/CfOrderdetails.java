package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfOrderdetails {

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idoforderdetail;

    public long getIdoforderdetail() {
        return idoforderdetail;
    }

    public void setIdoforderdetail(long idoforderdetail) {
        this.idoforderdetail = idoforderdetail;
    }

    private long idoforder;

    public long getIdoforder() {
        return idoforder;
    }

    public void setIdoforder(long idoforder) {
        this.idoforder = idoforder;
    }

    private int qty;

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    private long discount;

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    private long rprice;

    public long getRprice() {
        return rprice;
    }

    public void setRprice(long rprice) {
        this.rprice = rprice;
    }

    private String menudetailname;

    public String getMenudetailname() {
        return menudetailname;
    }

    public void setMenudetailname(String menudetailname) {
        this.menudetailname = menudetailname;
    }

    private String rootmenu;

    public String getRootmenu() {
        return rootmenu;
    }

    public void setRootmenu(String rootmenu) {
        this.rootmenu = rootmenu;
    }

    private String menugroup;

    public String getMenugroup() {
        return menugroup;
    }

    public void setMenugroup(String menugroup) {
        this.menugroup = menugroup;
    }

    private int menutype;

    public int getMenutype() {
        return menutype;
    }

    public void setMenutype(int menutype) {
        this.menutype = menutype;
    }

    private String menuoutput;

    public String getMenuoutput() {
        return menuoutput;
    }

    public void setMenuoutput(String menuoutput) {
        this.menuoutput = menuoutput;
    }

    private int menuorigin;

    public int getMenuorigin() {
        return menuorigin;
    }

    public void setMenuorigin(int menuorigin) {
        this.menuorigin = menuorigin;
    }

    private long socdiscount;

    public long getSocdiscount() {
        return socdiscount;
    }

    public void setSocdiscount(long socdiscount) {
        this.socdiscount = socdiscount;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String itemcode;

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfOrderdetails that = (CfOrderdetails) o;

        if (discount != that.discount) {
            return false;
        }
        if (idoforder != that.idoforder) {
            return false;
        }
        if (idoforderdetail != that.idoforderdetail) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (menuorigin != that.menuorigin) {
            return false;
        }
        if (menutype != that.menutype) {
            return false;
        }
        if (qty != that.qty) {
            return false;
        }
        if (rprice != that.rprice) {
            return false;
        }
        if (socdiscount != that.socdiscount) {
            return false;
        }
        if (state != that.state) {
            return false;
        }
        if (itemcode != null ? !itemcode.equals(that.itemcode) : that.itemcode != null) {
            return false;
        }
        if (menudetailname != null ? !menudetailname.equals(that.menudetailname) : that.menudetailname != null) {
            return false;
        }
        if (menugroup != null ? !menugroup.equals(that.menugroup) : that.menugroup != null) {
            return false;
        }
        if (menuoutput != null ? !menuoutput.equals(that.menuoutput) : that.menuoutput != null) {
            return false;
        }
        if (rootmenu != null ? !rootmenu.equals(that.rootmenu) : that.rootmenu != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idoforderdetail ^ (idoforderdetail >>> 32));
        result = 31 * result + (int) (idoforder ^ (idoforder >>> 32));
        result = 31 * result + qty;
        result = 31 * result + (int) (discount ^ (discount >>> 32));
        result = 31 * result + (int) (rprice ^ (rprice >>> 32));
        result = 31 * result + (menudetailname != null ? menudetailname.hashCode() : 0);
        result = 31 * result + (rootmenu != null ? rootmenu.hashCode() : 0);
        result = 31 * result + (menugroup != null ? menugroup.hashCode() : 0);
        result = 31 * result + menutype;
        result = 31 * result + (menuoutput != null ? menuoutput.hashCode() : 0);
        result = 31 * result + menuorigin;
        result = 31 * result + (int) (socdiscount ^ (socdiscount >>> 32));
        result = 31 * result + state;
        result = 31 * result + (itemcode != null ? itemcode.hashCode() : 0);
        return result;
    }

    private CfOrders cfOrders;

    public CfOrders getCfOrders() {
        return cfOrders;
    }

    public void setCfOrders(CfOrders cfOrders) {
        this.cfOrders = cfOrders;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
