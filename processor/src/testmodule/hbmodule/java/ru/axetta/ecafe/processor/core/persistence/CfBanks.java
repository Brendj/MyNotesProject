package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfBanks {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String logourl;

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    private String terminalsurl;

    public String getTerminalsurl() {
        return terminalsurl;
    }

    public void setTerminalsurl(String terminalsurl) {
        this.terminalsurl = terminalsurl;
    }

    private String enrollmenttype;

    public String getEnrollmenttype() {
        return enrollmenttype;
    }

    public void setEnrollmenttype(String enrollmenttype) {
        this.enrollmenttype = enrollmenttype;
    }

    private long idofbank;

    public long getIdofbank() {
        return idofbank;
    }

    public void setIdofbank(long idofbank) {
        this.idofbank = idofbank;
    }

    private double minrate;

    public double getMinrate() {
        return minrate;
    }

    public void setMinrate(double minrate) {
        this.minrate = minrate;
    }

    private double rate;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfBanks cfBanks = (CfBanks) o;

        if (idofbank != cfBanks.idofbank) {
            return false;
        }
        if (Double.compare(cfBanks.minrate, minrate) != 0) {
            return false;
        }
        if (Double.compare(cfBanks.rate, rate) != 0) {
            return false;
        }
        if (enrollmenttype != null ? !enrollmenttype.equals(cfBanks.enrollmenttype) : cfBanks.enrollmenttype != null) {
            return false;
        }
        if (logourl != null ? !logourl.equals(cfBanks.logourl) : cfBanks.logourl != null) {
            return false;
        }
        if (name != null ? !name.equals(cfBanks.name) : cfBanks.name != null) {
            return false;
        }
        if (terminalsurl != null ? !terminalsurl.equals(cfBanks.terminalsurl) : cfBanks.terminalsurl != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + (logourl != null ? logourl.hashCode() : 0);
        result = 31 * result + (terminalsurl != null ? terminalsurl.hashCode() : 0);
        result = 31 * result + (enrollmenttype != null ? enrollmenttype.hashCode() : 0);
        result = 31 * result + (int) (idofbank ^ (idofbank >>> 32));
        temp = minrate != +0.0d ? Double.doubleToLongBits(minrate) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = rate != +0.0d ? Double.doubleToLongBits(rate) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
