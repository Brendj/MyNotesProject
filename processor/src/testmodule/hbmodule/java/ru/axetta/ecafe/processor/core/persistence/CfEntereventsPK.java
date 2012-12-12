package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfEntereventsPK implements Serializable {

    private long idofenterevent;

    public long getIdofenterevent() {
        return idofenterevent;
    }

    public void setIdofenterevent(long idofenterevent) {
        this.idofenterevent = idofenterevent;
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

        CfEntereventsPK that = (CfEntereventsPK) o;

        if (idofenterevent != that.idofenterevent) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofenterevent ^ (idofenterevent >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        return result;
    }
}
