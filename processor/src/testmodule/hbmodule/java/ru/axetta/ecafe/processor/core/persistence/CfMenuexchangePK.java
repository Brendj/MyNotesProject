package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfMenuexchangePK implements Serializable {

    private long menudate;

    public long getMenudate() {
        return menudate;
    }

    public void setMenudate(long menudate) {
        this.menudate = menudate;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfMenuexchangePK that = (CfMenuexchangePK) o;

        if (idoforg != that.idoforg) {
            return false;
        }
        if (menudate != that.menudate) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (menudate ^ (menudate >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        return result;
    }
}
