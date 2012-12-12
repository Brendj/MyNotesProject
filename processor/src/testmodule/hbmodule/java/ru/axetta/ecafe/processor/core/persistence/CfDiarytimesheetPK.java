package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiarytimesheetPK implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiarytimesheetPK that = (CfDiarytimesheetPK) o;

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
        return result;
    }
}
