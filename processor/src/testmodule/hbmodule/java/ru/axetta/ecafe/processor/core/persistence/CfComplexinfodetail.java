package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfComplexinfodetail {

    private long idofcomplexinfodetail;

    public long getIdofcomplexinfodetail() {
        return idofcomplexinfodetail;
    }

    public void setIdofcomplexinfodetail(long idofcomplexinfodetail) {
        this.idofcomplexinfodetail = idofcomplexinfodetail;
    }

    private long idofcomplexinfo;

    public long getIdofcomplexinfo() {
        return idofcomplexinfo;
    }

    public void setIdofcomplexinfo(long idofcomplexinfo) {
        this.idofcomplexinfo = idofcomplexinfo;
    }

    private long idofmenudetail;

    public long getIdofmenudetail() {
        return idofmenudetail;
    }

    public void setIdofmenudetail(long idofmenudetail) {
        this.idofmenudetail = idofmenudetail;
    }

    private long idofitem;

    public long getIdofitem() {
        return idofitem;
    }

    public void setIdofitem(long idofitem) {
        this.idofitem = idofitem;
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfComplexinfodetail that = (CfComplexinfodetail) o;

        if (count != that.count) {
            return false;
        }
        if (idofcomplexinfo != that.idofcomplexinfo) {
            return false;
        }
        if (idofcomplexinfodetail != that.idofcomplexinfodetail) {
            return false;
        }
        if (idofitem != that.idofitem) {
            return false;
        }
        if (idofmenudetail != that.idofmenudetail) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcomplexinfodetail ^ (idofcomplexinfodetail >>> 32));
        result = 31 * result + (int) (idofcomplexinfo ^ (idofcomplexinfo >>> 32));
        result = 31 * result + (int) (idofmenudetail ^ (idofmenudetail >>> 32));
        result = 31 * result + (int) (idofitem ^ (idofitem >>> 32));
        result = 31 * result + count;
        return result;
    }

    private CfMenudetails cfMenudetailsByIdofmenudetail;

    public CfMenudetails getCfMenudetailsByIdofmenudetail() {
        return cfMenudetailsByIdofmenudetail;
    }

    public void setCfMenudetailsByIdofmenudetail(CfMenudetails cfMenudetailsByIdofmenudetail) {
        this.cfMenudetailsByIdofmenudetail = cfMenudetailsByIdofmenudetail;
    }
}
