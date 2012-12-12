package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSubscriptionfeePK implements Serializable {

    private int subscriptionyear;

    public int getSubscriptionyear() {
        return subscriptionyear;
    }

    public void setSubscriptionyear(int subscriptionyear) {
        this.subscriptionyear = subscriptionyear;
    }

    private int periodno;

    public int getPeriodno() {
        return periodno;
    }

    public void setPeriodno(int periodno) {
        this.periodno = periodno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfSubscriptionfeePK that = (CfSubscriptionfeePK) o;

        if (periodno != that.periodno) {
            return false;
        }
        if (subscriptionyear != that.subscriptionyear) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = subscriptionyear;
        result = 31 * result + periodno;
        return result;
    }
}
