package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientgroupsPK implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientgroupsPK that = (CfClientgroupsPK) o;

        if (idofclientgroup != that.idofclientgroup) {
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
        result = 31 * result + (int) (idofclientgroup ^ (idofclientgroup >>> 32));
        return result;
    }
}
