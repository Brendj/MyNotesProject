package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDoConflicts {

    private long idofdoconflict;

    public long getIdofdoconflict() {
        return idofdoconflict;
    }

    public void setIdofdoconflict(long idofdoconflict) {
        this.idofdoconflict = idofdoconflict;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private String distributedobjectclassname;

    public String getDistributedobjectclassname() {
        return distributedobjectclassname;
    }

    public void setDistributedobjectclassname(String distributedobjectclassname) {
        this.distributedobjectclassname = distributedobjectclassname;
    }

    private long createconflictdate;

    public long getCreateconflictdate() {
        return createconflictdate;
    }

    public void setCreateconflictdate(long createconflictdate) {
        this.createconflictdate = createconflictdate;
    }

    private long gversionInc;

    public long getGversionInc() {
        return gversionInc;
    }

    public void setGversionInc(long gversionInc) {
        this.gversionInc = gversionInc;
    }

    private long gversionCur;

    public long getGversionCur() {
        return gversionCur;
    }

    public void setGversionCur(long gversionCur) {
        this.gversionCur = gversionCur;
    }

    private String valInc;

    public String getValInc() {
        return valInc;
    }

    public void setValInc(String valInc) {
        this.valInc = valInc;
    }

    private String valCur;

    public String getValCur() {
        return valCur;
    }

    public void setValCur(String valCur) {
        this.valCur = valCur;
    }

    private long gversionResult;

    public long getGversionResult() {
        return gversionResult;
    }

    public void setGversionResult(long gversionResult) {
        this.gversionResult = gversionResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDoConflicts that = (CfDoConflicts) o;

        if (createconflictdate != that.createconflictdate) {
            return false;
        }
        if (gversionCur != that.gversionCur) {
            return false;
        }
        if (gversionInc != that.gversionInc) {
            return false;
        }
        if (gversionResult != that.gversionResult) {
            return false;
        }
        if (idofdoconflict != that.idofdoconflict) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (distributedobjectclassname != null ? !distributedobjectclassname.equals(that.distributedobjectclassname)
                : that.distributedobjectclassname != null) {
            return false;
        }
        if (valCur != null ? !valCur.equals(that.valCur) : that.valCur != null) {
            return false;
        }
        if (valInc != null ? !valInc.equals(that.valInc) : that.valInc != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofdoconflict ^ (idofdoconflict >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (distributedobjectclassname != null ? distributedobjectclassname.hashCode() : 0);
        result = 31 * result + (int) (createconflictdate ^ (createconflictdate >>> 32));
        result = 31 * result + (int) (gversionInc ^ (gversionInc >>> 32));
        result = 31 * result + (int) (gversionCur ^ (gversionCur >>> 32));
        result = 31 * result + (valInc != null ? valInc.hashCode() : 0);
        result = 31 * result + (valCur != null ? valCur.hashCode() : 0);
        result = 31 * result + (int) (gversionResult ^ (gversionResult >>> 32));
        return result;
    }
}
