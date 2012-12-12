package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientpayments {

    private long idofclientpayment;

    public long getIdofclientpayment() {
        return idofclientpayment;
    }

    public void setIdofclientpayment(long idofclientpayment) {
        this.idofclientpayment = idofclientpayment;
    }

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private int paymentmethod;

    public int getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(int paymentmethod) {
        this.paymentmethod = paymentmethod;
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

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private String idofpayment;

    public String getIdofpayment() {
        return idofpayment;
    }

    public void setIdofpayment(String idofpayment) {
        this.idofpayment = idofpayment;
    }

    private long idofclientpaymentorder;

    public long getIdofclientpaymentorder() {
        return idofclientpaymentorder;
    }

    public void setIdofclientpaymentorder(long idofclientpaymentorder) {
        this.idofclientpaymentorder = idofclientpaymentorder;
    }

    private String addpaymentmethod;

    public String getAddpaymentmethod() {
        return addpaymentmethod;
    }

    public void setAddpaymentmethod(String addpaymentmethod) {
        this.addpaymentmethod = addpaymentmethod;
    }

    private String addidofpayment;

    public String getAddidofpayment() {
        return addidofpayment;
    }

    public void setAddidofpayment(String addidofpayment) {
        this.addidofpayment = addidofpayment;
    }

    private long idofcontragentreceiver;

    public long getIdofcontragentreceiver() {
        return idofcontragentreceiver;
    }

    public void setIdofcontragentreceiver(long idofcontragentreceiver) {
        this.idofcontragentreceiver = idofcontragentreceiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientpayments that = (CfClientpayments) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (idofclientpayment != that.idofclientpayment) {
            return false;
        }
        if (idofclientpaymentorder != that.idofclientpaymentorder) {
            return false;
        }
        if (idofcontragent != that.idofcontragent) {
            return false;
        }
        if (idofcontragentreceiver != that.idofcontragentreceiver) {
            return false;
        }
        if (idoftransaction != that.idoftransaction) {
            return false;
        }
        if (paymentmethod != that.paymentmethod) {
            return false;
        }
        if (paysum != that.paysum) {
            return false;
        }
        if (paytype != that.paytype) {
            return false;
        }
        if (addidofpayment != null ? !addidofpayment.equals(that.addidofpayment) : that.addidofpayment != null) {
            return false;
        }
        if (addpaymentmethod != null ? !addpaymentmethod.equals(that.addpaymentmethod)
                : that.addpaymentmethod != null) {
            return false;
        }
        if (idofpayment != null ? !idofpayment.equals(that.idofpayment) : that.idofpayment != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofclientpayment ^ (idofclientpayment >>> 32));
        result = 31 * result + (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + paymentmethod;
        result = 31 * result + (int) (paysum ^ (paysum >>> 32));
        result = 31 * result + paytype;
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (idofpayment != null ? idofpayment.hashCode() : 0);
        result = 31 * result + (int) (idofclientpaymentorder ^ (idofclientpaymentorder >>> 32));
        result = 31 * result + (addpaymentmethod != null ? addpaymentmethod.hashCode() : 0);
        result = 31 * result + (addidofpayment != null ? addidofpayment.hashCode() : 0);
        result = 31 * result + (int) (idofcontragentreceiver ^ (idofcontragentreceiver >>> 32));
        return result;
    }

    private CfClientpaymentorders cfClientpaymentordersByIdofclientpaymentorder;

    public CfClientpaymentorders getCfClientpaymentordersByIdofclientpaymentorder() {
        return cfClientpaymentordersByIdofclientpaymentorder;
    }

    public void setCfClientpaymentordersByIdofclientpaymentorder(
            CfClientpaymentorders cfClientpaymentordersByIdofclientpaymentorder) {
        this.cfClientpaymentordersByIdofclientpaymentorder = cfClientpaymentordersByIdofclientpaymentorder;
    }

    private CfContragents cfContragentsByIdofcontragent;

    public CfContragents getCfContragentsByIdofcontragent() {
        return cfContragentsByIdofcontragent;
    }

    public void setCfContragentsByIdofcontragent(CfContragents cfContragentsByIdofcontragent) {
        this.cfContragentsByIdofcontragent = cfContragentsByIdofcontragent;
    }

    private CfContragents cfContragentsByIdofcontragentreceiver;

    public CfContragents getCfContragentsByIdofcontragentreceiver() {
        return cfContragentsByIdofcontragentreceiver;
    }

    public void setCfContragentsByIdofcontragentreceiver(CfContragents cfContragentsByIdofcontragentreceiver) {
        this.cfContragentsByIdofcontragentreceiver = cfContragentsByIdofcontragentreceiver;
    }

    private CfTransactions cfTransactionsByIdoftransaction;

    public CfTransactions getCfTransactionsByIdoftransaction() {
        return cfTransactionsByIdoftransaction;
    }

    public void setCfTransactionsByIdoftransaction(CfTransactions cfTransactionsByIdoftransaction) {
        this.cfTransactionsByIdoftransaction = cfTransactionsByIdoftransaction;
    }
}
