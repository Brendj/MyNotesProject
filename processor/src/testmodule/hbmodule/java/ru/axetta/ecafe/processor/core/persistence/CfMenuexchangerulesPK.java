package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfMenuexchangerulesPK implements Serializable {

    private long idofsourceorg;

    public long getIdofsourceorg() {
        return idofsourceorg;
    }

    public void setIdofsourceorg(long idofsourceorg) {
        this.idofsourceorg = idofsourceorg;
    }

    private long idofdestorg;

    public long getIdofdestorg() {
        return idofdestorg;
    }

    public void setIdofdestorg(long idofdestorg) {
        this.idofdestorg = idofdestorg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfMenuexchangerulesPK that = (CfMenuexchangerulesPK) o;

        if (idofdestorg != that.idofdestorg) {
            return false;
        }
        if (idofsourceorg != that.idofsourceorg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofsourceorg ^ (idofsourceorg >>> 32));
        result = 31 * result + (int) (idofdestorg ^ (idofdestorg >>> 32));
        return result;
    }
}
