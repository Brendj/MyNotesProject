package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSubscriptionfee {

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

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private long subscriptionsum;

    public long getSubscriptionsum() {
        return subscriptionsum;
    }

    public void setSubscriptionsum(long subscriptionsum) {
        this.subscriptionsum = subscriptionsum;
    }

    private long createtime;

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfSubscriptionfee that = (CfSubscriptionfee) o;

        if (createtime != that.createtime) {
            return false;
        }
        if (idoftransaction != that.idoftransaction) {
            return false;
        }
        if (periodno != that.periodno) {
            return false;
        }
        if (subscriptionsum != that.subscriptionsum) {
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
        result = 31 * result + (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + (int) (subscriptionsum ^ (subscriptionsum >>> 32));
        result = 31 * result + (int) (createtime ^ (createtime >>> 32));
        return result;
    }

    private CfTransactions cfTransactionsByIdoftransaction;

    public CfTransactions getCfTransactionsByIdoftransaction() {
        return cfTransactionsByIdoftransaction;
    }

    public void setCfTransactionsByIdoftransaction(CfTransactions cfTransactionsByIdoftransaction) {
        this.cfTransactionsByIdoftransaction = cfTransactionsByIdoftransaction;
    }
}
