package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiarytimesheet {

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

    private long recdate;

    public long getRecdate() {
        return recdate;
    }

    public void setRecdate(long recdate) {
        this.recdate = recdate;
    }

    private long c0;

    public long getC0() {
        return c0;
    }

    public void setC0(long c0) {
        this.c0 = c0;
    }

    private long c1;

    public long getC1() {
        return c1;
    }

    public void setC1(long c1) {
        this.c1 = c1;
    }

    private long c2;

    public long getC2() {
        return c2;
    }

    public void setC2(long c2) {
        this.c2 = c2;
    }

    private long c3;

    public long getC3() {
        return c3;
    }

    public void setC3(long c3) {
        this.c3 = c3;
    }

    private long c4;

    public long getC4() {
        return c4;
    }

    public void setC4(long c4) {
        this.c4 = c4;
    }

    private long c5;

    public long getC5() {
        return c5;
    }

    public void setC5(long c5) {
        this.c5 = c5;
    }

    private long c6;

    public long getC6() {
        return c6;
    }

    public void setC6(long c6) {
        this.c6 = c6;
    }

    private long c7;

    public long getC7() {
        return c7;
    }

    public void setC7(long c7) {
        this.c7 = c7;
    }

    private long c8;

    public long getC8() {
        return c8;
    }

    public void setC8(long c8) {
        this.c8 = c8;
    }

    private long c9;

    public long getC9() {
        return c9;
    }

    public void setC9(long c9) {
        this.c9 = c9;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiarytimesheet that = (CfDiarytimesheet) o;

        if (c0 != that.c0) {
            return false;
        }
        if (c1 != that.c1) {
            return false;
        }
        if (c2 != that.c2) {
            return false;
        }
        if (c3 != that.c3) {
            return false;
        }
        if (c4 != that.c4) {
            return false;
        }
        if (c5 != that.c5) {
            return false;
        }
        if (c6 != that.c6) {
            return false;
        }
        if (c7 != that.c7) {
            return false;
        }
        if (c8 != that.c8) {
            return false;
        }
        if (c9 != that.c9) {
            return false;
        }
        if (idofclientgroup != that.idofclientgroup) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (recdate != that.recdate) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofclientgroup ^ (idofclientgroup >>> 32));
        result = 31 * result + (int) (recdate ^ (recdate >>> 32));
        result = 31 * result + (int) (c0 ^ (c0 >>> 32));
        result = 31 * result + (int) (c1 ^ (c1 >>> 32));
        result = 31 * result + (int) (c2 ^ (c2 >>> 32));
        result = 31 * result + (int) (c3 ^ (c3 >>> 32));
        result = 31 * result + (int) (c4 ^ (c4 >>> 32));
        result = 31 * result + (int) (c5 ^ (c5 >>> 32));
        result = 31 * result + (int) (c6 ^ (c6 >>> 32));
        result = 31 * result + (int) (c7 ^ (c7 >>> 32));
        result = 31 * result + (int) (c8 ^ (c8 >>> 32));
        result = 31 * result + (int) (c9 ^ (c9 >>> 32));
        return result;
    }

    private CfClientgroups cfClientgroups;

    public CfClientgroups getCfClientgroups() {
        return cfClientgroups;
    }

    public void setCfClientgroups(CfClientgroups cfClientgroups) {
        this.cfClientgroups = cfClientgroups;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC2;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC2() {
        return cfDiaryclassesByCfDiarytimesheetC2;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC2(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC2) {
        this.cfDiaryclassesByCfDiarytimesheetC2 = cfDiaryclassesByCfDiarytimesheetC2;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC0;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC0() {
        return cfDiaryclassesByCfDiarytimesheetC0;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC0(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC0) {
        this.cfDiaryclassesByCfDiarytimesheetC0 = cfDiaryclassesByCfDiarytimesheetC0;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC1;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC1() {
        return cfDiaryclassesByCfDiarytimesheetC1;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC1(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC1) {
        this.cfDiaryclassesByCfDiarytimesheetC1 = cfDiaryclassesByCfDiarytimesheetC1;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC3;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC3() {
        return cfDiaryclassesByCfDiarytimesheetC3;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC3(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC3) {
        this.cfDiaryclassesByCfDiarytimesheetC3 = cfDiaryclassesByCfDiarytimesheetC3;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC4;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC4() {
        return cfDiaryclassesByCfDiarytimesheetC4;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC4(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC4) {
        this.cfDiaryclassesByCfDiarytimesheetC4 = cfDiaryclassesByCfDiarytimesheetC4;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC5;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC5() {
        return cfDiaryclassesByCfDiarytimesheetC5;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC5(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC5) {
        this.cfDiaryclassesByCfDiarytimesheetC5 = cfDiaryclassesByCfDiarytimesheetC5;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC6;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC6() {
        return cfDiaryclassesByCfDiarytimesheetC6;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC6(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC6) {
        this.cfDiaryclassesByCfDiarytimesheetC6 = cfDiaryclassesByCfDiarytimesheetC6;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC7;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC7() {
        return cfDiaryclassesByCfDiarytimesheetC7;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC7(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC7) {
        this.cfDiaryclassesByCfDiarytimesheetC7 = cfDiaryclassesByCfDiarytimesheetC7;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC8;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC8() {
        return cfDiaryclassesByCfDiarytimesheetC8;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC8(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC8) {
        this.cfDiaryclassesByCfDiarytimesheetC8 = cfDiaryclassesByCfDiarytimesheetC8;
    }

    private CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC9;

    public CfDiaryclasses getCfDiaryclassesByCfDiarytimesheetC9() {
        return cfDiaryclassesByCfDiarytimesheetC9;
    }

    public void setCfDiaryclassesByCfDiarytimesheetC9(CfDiaryclasses cfDiaryclassesByCfDiarytimesheetC9) {
        this.cfDiaryclassesByCfDiarytimesheetC9 = cfDiaryclassesByCfDiarytimesheetC9;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
