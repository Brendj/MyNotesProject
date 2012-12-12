package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfContragentpayments {

    private long idofcontragentpayment;

    public long getIdofcontragentpayment() {
        return idofcontragentpayment;
    }

    public void setIdofcontragentpayment(long idofcontragentpayment) {
        this.idofcontragentpayment = idofcontragentpayment;
    }

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private long paysum;

    public long getPaysum() {
        return paysum;
    }

    public void setPaysum(long paysum) {
        this.paysum = paysum;
    }

    private int paytype;

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long paymentdate;

    public long getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(long paymentdate) {
        this.paymentdate = paymentdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfContragentpayments that = (CfContragentpayments) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (idofcontragent != that.idofcontragent) {
            return false;
        }
        if (idofcontragentpayment != that.idofcontragentpayment) {
            return false;
        }
        if (idoftransaction != that.idoftransaction) {
            return false;
        }
        if (paymentdate != that.paymentdate) {
            return false;
        }
        if (paysum != that.paysum) {
            return false;
        }
        if (paytype != that.paytype) {
            return false;
        }
        if (state != that.state) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcontragentpayment ^ (idofcontragentpayment >>> 32));
        result = 31 * result + (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + (int) (paysum ^ (paysum >>> 32));
        result = 31 * result + paytype;
        result = 31 * result + state;
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (paymentdate ^ (paymentdate >>> 32));
        return result;
    }

    private CfContragents cfContragentsByIdofcontragent;

    public CfContragents getCfContragentsByIdofcontragent() {
        return cfContragentsByIdofcontragent;
    }

    public void setCfContragentsByIdofcontragent(CfContragents cfContragentsByIdofcontragent) {
        this.cfContragentsByIdofcontragent = cfContragentsByIdofcontragent;
    }

    private CfTransactions cfTransactionsByIdoftransaction;

    public CfTransactions getCfTransactionsByIdoftransaction() {
        return cfTransactionsByIdoftransaction;
    }

    public void setCfTransactionsByIdoftransaction(CfTransactions cfTransactionsByIdoftransaction) {
        this.cfTransactionsByIdoftransaction = cfTransactionsByIdoftransaction;
    }
}
