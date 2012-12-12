package ru.axetta.ecafe.processor.core.persistence;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfMenudetails {

    private long idofmenudetail;

    public long getIdofmenudetail() {
        return idofmenudetail;
    }

    public void setIdofmenudetail(long idofmenudetail) {
        this.idofmenudetail = idofmenudetail;
    }

    private long idofmenu;

    public long getIdofmenu() {
        return idofmenu;
    }

    public void setIdofmenu(long idofmenu) {
        this.idofmenu = idofmenu;
    }

    private String menupath;

    public String getMenupath() {
        return menupath;
    }

    public void setMenupath(String menupath) {
        this.menupath = menupath;
    }

    private String menudetailname;

    public String getMenudetailname() {
        return menudetailname;
    }

    public void setMenudetailname(String menudetailname) {
        this.menudetailname = menudetailname;
    }

    private String groupname;

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    private String menudetailoutput;

    public String getMenudetailoutput() {
        return menudetailoutput;
    }

    public void setMenudetailoutput(String menudetailoutput) {
        this.menudetailoutput = menudetailoutput;
    }

    private long price;

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    private int menuorigin;

    public int getMenuorigin() {
        return menuorigin;
    }

    public void setMenuorigin(int menuorigin) {
        this.menuorigin = menuorigin;
    }

    private int availablenow;

    public int getAvailablenow() {
        return availablenow;
    }

    public void setAvailablenow(int availablenow) {
        this.availablenow = availablenow;
    }

    private long localidofmenu;

    public long getLocalidofmenu() {
        return localidofmenu;
    }

    public void setLocalidofmenu(long localidofmenu) {
        this.localidofmenu = localidofmenu;
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

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

        CfMenudetails that = (CfMenudetails) o;

        if (availablenow != that.availablenow) {
            return false;
        }
        if (flags != that.flags) {
            return false;
        }
        if (idofgood != that.idofgood) {
            return false;
        }
        if (idofmenu != that.idofmenu) {
            return false;
        }
        if (idofmenudetail != that.idofmenudetail) {
            return false;
        }
        if (localidofmenu != that.localidofmenu) {
            return false;
        }
        if (menuorigin != that.menuorigin) {
            return false;
        }
        if (price != that.price) {
            return false;
        }
        if (priority != that.priority) {
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
        if (groupname != null ? !groupname.equals(that.groupname) : that.groupname != null) {
            return false;
        }
        if (menudetailname != null ? !menudetailname.equals(that.menudetailname) : that.menudetailname != null) {
            return false;
        }
        if (menudetailoutput != null ? !menudetailoutput.equals(that.menudetailoutput)
                : that.menudetailoutput != null) {
            return false;
        }
        if (menupath != null ? !menupath.equals(that.menupath) : that.menupath != null) {
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
        int result = (int) (idofmenudetail ^ (idofmenudetail >>> 32));
        result = 31 * result + (int) (idofmenu ^ (idofmenu >>> 32));
        result = 31 * result + (menupath != null ? menupath.hashCode() : 0);
        result = 31 * result + (menudetailname != null ? menudetailname.hashCode() : 0);
        result = 31 * result + (groupname != null ? groupname.hashCode() : 0);
        result = 31 * result + (menudetailoutput != null ? menudetailoutput.hashCode() : 0);
        result = 31 * result + (int) (price ^ (price >>> 32));
        result = 31 * result + menuorigin;
        result = 31 * result + availablenow;
        result = 31 * result + (int) (localidofmenu ^ (localidofmenu >>> 32));
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
        result = 31 * result + flags;
        result = 31 * result + priority;
        result = 31 * result + (int) (idofgood ^ (idofgood >>> 32));
        return result;
    }

    private Collection<CfComplexinfo> cfComplexinfosByIdofmenudetail;

    public Collection<CfComplexinfo> getCfComplexinfosByIdofmenudetail() {
        return cfComplexinfosByIdofmenudetail;
    }

    public void setCfComplexinfosByIdofmenudetail(Collection<CfComplexinfo> cfComplexinfosByIdofmenudetail) {
        this.cfComplexinfosByIdofmenudetail = cfComplexinfosByIdofmenudetail;
    }

    private Collection<CfComplexinfodetail> cfComplexinfodetailsByIdofmenudetail;

    public Collection<CfComplexinfodetail> getCfComplexinfodetailsByIdofmenudetail() {
        return cfComplexinfodetailsByIdofmenudetail;
    }

    public void setCfComplexinfodetailsByIdofmenudetail(
            Collection<CfComplexinfodetail> cfComplexinfodetailsByIdofmenudetail) {
        this.cfComplexinfodetailsByIdofmenudetail = cfComplexinfodetailsByIdofmenudetail;
    }

    private CfGoods cfGoodsByIdofgood;

    public CfGoods getCfGoodsByIdofgood() {
        return cfGoodsByIdofgood;
    }

    public void setCfGoodsByIdofgood(CfGoods cfGoodsByIdofgood) {
        this.cfGoodsByIdofgood = cfGoodsByIdofgood;
    }

    private CfMenu cfMenuByIdofmenu;

    public CfMenu getCfMenuByIdofmenu() {
        return cfMenuByIdofmenu;
    }

    public void setCfMenuByIdofmenu(CfMenu cfMenuByIdofmenu) {
        this.cfMenuByIdofmenu = cfMenuByIdofmenu;
    }
}
