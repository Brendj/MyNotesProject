package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfComplexinfo {

    private long idofcomplexinfo;

    public long getIdofcomplexinfo() {
        return idofcomplexinfo;
    }

    public void setIdofcomplexinfo(long idofcomplexinfo) {
        this.idofcomplexinfo = idofcomplexinfo;
    }

    private int idofcomplex;

    public int getIdofcomplex() {
        return idofcomplex;
    }

    public void setIdofcomplex(int idofcomplex) {
        this.idofcomplex = idofcomplex;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private String complexname;

    public String getComplexname() {
        return complexname;
    }

    public void setComplexname(String complexname) {
        this.complexname = complexname;
    }

    private long menudate;

    public long getMenudate() {
        return menudate;
    }

    public void setMenudate(long menudate) {
        this.menudate = menudate;
    }

    private int modefree;

    public int getModefree() {
        return modefree;
    }

    public void setModefree(int modefree) {
        this.modefree = modefree;
    }

    private int modegrant;

    public int getModegrant() {
        return modegrant;
    }

    public void setModegrant(int modegrant) {
        this.modegrant = modegrant;
    }

    private int modeofadd;

    public int getModeofadd() {
        return modeofadd;
    }

    public void setModeofadd(int modeofadd) {
        this.modeofadd = modeofadd;
    }

    private int usetrdiscount;

    public int getUsetrdiscount() {
        return usetrdiscount;
    }

    public void setUsetrdiscount(int usetrdiscount) {
        this.usetrdiscount = usetrdiscount;
    }

    private long idofdiscountdetail;

    public long getIdofdiscountdetail() {
        return idofdiscountdetail;
    }

    public void setIdofdiscountdetail(long idofdiscountdetail) {
        this.idofdiscountdetail = idofdiscountdetail;
    }

    private long idofmenudetail;

    public long getIdofmenudetail() {
        return idofmenudetail;
    }

    public void setIdofmenudetail(long idofmenudetail) {
        this.idofmenudetail = idofmenudetail;
    }

    private long currentprice;

    public long getCurrentprice() {
        return currentprice;
    }

    public void setCurrentprice(long currentprice) {
        this.currentprice = currentprice;
    }

    private long idofgood;

    public long getIdofgood() {
        return idofgood;
    }

    public void setIdofgood(long idofgood) {
        this.idofgood = idofgood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfComplexinfo that = (CfComplexinfo) o;

        if (currentprice != that.currentprice) {
            return false;
        }
        if (idofcomplex != that.idofcomplex) {
            return false;
        }
        if (idofcomplexinfo != that.idofcomplexinfo) {
            return false;
        }
        if (idofdiscountdetail != that.idofdiscountdetail) {
            return false;
        }
        if (idofgood != that.idofgood) {
            return false;
        }
        if (idofmenudetail != that.idofmenudetail) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (menudate != that.menudate) {
            return false;
        }
        if (modefree != that.modefree) {
            return false;
        }
        if (modegrant != that.modegrant) {
            return false;
        }
        if (modeofadd != that.modeofadd) {
            return false;
        }
        if (usetrdiscount != that.usetrdiscount) {
            return false;
        }
        if (complexname != null ? !complexname.equals(that.complexname) : that.complexname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcomplexinfo ^ (idofcomplexinfo >>> 32));
        result = 31 * result + idofcomplex;
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (complexname != null ? complexname.hashCode() : 0);
        result = 31 * result + (int) (menudate ^ (menudate >>> 32));
        result = 31 * result + modefree;
        result = 31 * result + modegrant;
        result = 31 * result + modeofadd;
        result = 31 * result + usetrdiscount;
        result = 31 * result + (int) (idofdiscountdetail ^ (idofdiscountdetail >>> 32));
        result = 31 * result + (int) (idofmenudetail ^ (idofmenudetail >>> 32));
        result = 31 * result + (int) (currentprice ^ (currentprice >>> 32));
        result = 31 * result + (int) (idofgood ^ (idofgood >>> 32));
        return result;
    }

    private CfGoods cfGoodsByIdofgood;

    public CfGoods getCfGoodsByIdofgood() {
        return cfGoodsByIdofgood;
    }

    public void setCfGoodsByIdofgood(CfGoods cfGoodsByIdofgood) {
        this.cfGoodsByIdofgood = cfGoodsByIdofgood;
    }

    private CfMenudetails cfMenudetailsByIdofmenudetail;

    public CfMenudetails getCfMenudetailsByIdofmenudetail() {
        return cfMenudetailsByIdofmenudetail;
    }

    public void setCfMenudetailsByIdofmenudetail(CfMenudetails cfMenudetailsByIdofmenudetail) {
        this.cfMenudetailsByIdofmenudetail = cfMenudetailsByIdofmenudetail;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
