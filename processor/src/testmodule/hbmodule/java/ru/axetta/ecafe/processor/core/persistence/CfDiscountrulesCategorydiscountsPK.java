package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiscountrulesCategorydiscountsPK implements Serializable {

    private long idofrule;

    public long getIdofrule() {
        return idofrule;
    }

    public void setIdofrule(long idofrule) {
        this.idofrule = idofrule;
    }

    private long idofcategorydiscount;

    public long getIdofcategorydiscount() {
        return idofcategorydiscount;
    }

    public void setIdofcategorydiscount(long idofcategorydiscount) {
        this.idofcategorydiscount = idofcategorydiscount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiscountrulesCategorydiscountsPK that = (CfDiscountrulesCategorydiscountsPK) o;

        if (idofcategorydiscount != that.idofcategorydiscount) {
            return false;
        }
        if (idofrule != that.idofrule) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofrule ^ (idofrule >>> 32));
        result = 31 * result + (int) (idofcategorydiscount ^ (idofcategorydiscount >>> 32));
        return result;
    }
}
