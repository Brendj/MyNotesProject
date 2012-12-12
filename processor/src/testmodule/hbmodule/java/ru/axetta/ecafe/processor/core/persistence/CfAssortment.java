package ru.axetta.ecafe.processor.core.persistence;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfAssortment {

    private long idofast;

    public long getIdofast() {
        return idofast;
    }

    public void setIdofast(long idofast) {
        this.idofast = idofast;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long begindate;

    public long getBegindate() {
        return begindate;
    }

    public void setBegindate(long begindate) {
        this.begindate = begindate;
    }

    private String shortname;

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    private String groupname;

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    private int menuorigin;

    public int getMenuorigin() {
        return menuorigin;
    }

    public void setMenuorigin(int menuorigin) {
        this.menuorigin = menuorigin;
    }

    private String menuoutput;

    public String getMenuoutput() {
        return menuoutput;
    }

    public void setMenuoutput(String menuoutput) {
        this.menuoutput = menuoutput;
    }

    private long price;

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    private BigDecimal protein;

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    private BigDecimal fat;

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    private BigDecimal carbohydrates;

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    private BigDecimal calories;

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    private BigDecimal vitb1;

    public BigDecimal getVitb1() {
        return vitb1;
    }

    public void setVitb1(BigDecimal vitb1) {
        this.vitb1 = vitb1;
    }

    private BigDecimal vitc;

    public BigDecimal getVitc() {
        return vitc;
    }

    public void setVitc(BigDecimal vitc) {
        this.vitc = vitc;
    }

    private BigDecimal vita;

    public BigDecimal getVita() {
        return vita;
    }

    public void setVita(BigDecimal vita) {
        this.vita = vita;
    }

    private BigDecimal vite;

    public BigDecimal getVite() {
        return vite;
    }

    public void setVite(BigDecimal vite) {
        this.vite = vite;
    }

    private BigDecimal minca;

    public BigDecimal getMinca() {
        return minca;
    }

    public void setMinca(BigDecimal minca) {
        this.minca = minca;
    }

    private BigDecimal minp;

    public BigDecimal getMinp() {
        return minp;
    }

    public void setMinp(BigDecimal minp) {
        this.minp = minp;
    }

    private BigDecimal minmg;

    public BigDecimal getMinmg() {
        return minmg;
    }

    public void setMinmg(BigDecimal minmg) {
        this.minmg = minmg;
    }

    private BigDecimal minfe;

    public BigDecimal getMinfe() {
        return minfe;
    }

    public void setMinfe(BigDecimal minfe) {
        this.minfe = minfe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfAssortment that = (CfAssortment) o;

        if (begindate != that.begindate) {
            return false;
        }
        if (idofast != that.idofast) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (menuorigin != that.menuorigin) {
            return false;
        }
        if (price != that.price) {
            return false;
        }
        if (calories != null ? !calories.equals(that.calories) : that.calories != null) {
            return false;
        }
        if (carbohydrates != null ? !carbohydrates.equals(that.carbohydrates) : that.carbohydrates != null) {
            return false;
        }
        if (fat != null ? !fat.equals(that.fat) : that.fat != null) {
            return false;
        }
        if (fullname != null ? !fullname.equals(that.fullname) : that.fullname != null) {
            return false;
        }
        if (groupname != null ? !groupname.equals(that.groupname) : that.groupname != null) {
            return false;
        }
        if (menuoutput != null ? !menuoutput.equals(that.menuoutput) : that.menuoutput != null) {
            return false;
        }
        if (minca != null ? !minca.equals(that.minca) : that.minca != null) {
            return false;
        }
        if (minfe != null ? !minfe.equals(that.minfe) : that.minfe != null) {
            return false;
        }
        if (minmg != null ? !minmg.equals(that.minmg) : that.minmg != null) {
            return false;
        }
        if (minp != null ? !minp.equals(that.minp) : that.minp != null) {
            return false;
        }
        if (protein != null ? !protein.equals(that.protein) : that.protein != null) {
            return false;
        }
        if (shortname != null ? !shortname.equals(that.shortname) : that.shortname != null) {
            return false;
        }
        if (vita != null ? !vita.equals(that.vita) : that.vita != null) {
            return false;
        }
        if (vitb1 != null ? !vitb1.equals(that.vitb1) : that.vitb1 != null) {
            return false;
        }
        if (vitc != null ? !vitc.equals(that.vitc) : that.vitc != null) {
            return false;
        }
        if (vite != null ? !vite.equals(that.vite) : that.vite != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofast ^ (idofast >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (begindate ^ (begindate >>> 32));
        result = 31 * result + (shortname != null ? shortname.hashCode() : 0);
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        result = 31 * result + (groupname != null ? groupname.hashCode() : 0);
        result = 31 * result + menuorigin;
        result = 31 * result + (menuoutput != null ? menuoutput.hashCode() : 0);
        result = 31 * result + (int) (price ^ (price >>> 32));
        result = 31 * result + (protein != null ? protein.hashCode() : 0);
        result = 31 * result + (fat != null ? fat.hashCode() : 0);
        result = 31 * result + (carbohydrates != null ? carbohydrates.hashCode() : 0);
        result = 31 * result + (calories != null ? calories.hashCode() : 0);
        result = 31 * result + (vitb1 != null ? vitb1.hashCode() : 0);
        result = 31 * result + (vitc != null ? vitc.hashCode() : 0);
        result = 31 * result + (vita != null ? vita.hashCode() : 0);
        result = 31 * result + (vite != null ? vite.hashCode() : 0);
        result = 31 * result + (minca != null ? minca.hashCode() : 0);
        result = 31 * result + (minp != null ? minp.hashCode() : 0);
        result = 31 * result + (minmg != null ? minmg.hashCode() : 0);
        result = 31 * result + (minfe != null ? minfe.hashCode() : 0);
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
