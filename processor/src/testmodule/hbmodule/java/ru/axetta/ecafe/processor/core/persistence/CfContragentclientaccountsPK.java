package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfContragentclientaccountsPK implements Serializable {

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private long idofaccount;

    public long getIdofaccount() {
        return idofaccount;
    }

    public void setIdofaccount(long idofaccount) {
        this.idofaccount = idofaccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfContragentclientaccountsPK that = (CfContragentclientaccountsPK) o;

        if (idofaccount != that.idofaccount) {
            return false;
        }
        if (idofcontragent != that.idofcontragent) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (int) (idofaccount ^ (idofaccount >>> 32));
        return result;
    }
}
