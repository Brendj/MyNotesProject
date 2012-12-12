package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiaryvaluesPK implements Serializable {

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofclass;

    public long getIdofclass() {
        return idofclass;
    }

    public void setIdofclass(long idofclass) {
        this.idofclass = idofclass;
    }

    private long recdate;

    public long getRecdate() {
        return recdate;
    }

    public void setRecdate(long recdate) {
        this.recdate = recdate;
    }

    private int vtype;

    public int getVtype() {
        return vtype;
    }

    public void setVtype(int vtype) {
        this.vtype = vtype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiaryvaluesPK that = (CfDiaryvaluesPK) o;

        if (idofclass != that.idofclass) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (recdate != that.recdate) {
            return false;
        }
        if (vtype != that.vtype) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofclass ^ (idofclass >>> 32));
        result = 31 * result + (int) (recdate ^ (recdate >>> 32));
        result = 31 * result + vtype;
        return result;
    }
}
