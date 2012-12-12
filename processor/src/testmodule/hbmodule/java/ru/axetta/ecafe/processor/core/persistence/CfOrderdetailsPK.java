package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfOrderdetailsPK implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfOrderdetailsPK that = (CfOrderdetailsPK) o;

        if (idoforderdetail != that.idoforderdetail) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idoforderdetail ^ (idoforderdetail >>> 32));
        return result;
    }
}
