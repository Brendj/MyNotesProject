package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientpaymentorders {

    private long idofclientpaymentorder;

    public long getIdofclientpaymentorder() {
        return idofclientpaymentorder;
    }

    public void setIdofclientpaymentorder(long idofclientpaymentorder) {
        this.idofclientpaymentorder = idofclientpaymentorder;
    }

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private int paymentmethod;

    public int getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(int paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    private int orderstatus;

    public int getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(int orderstatus) {
        this.orderstatus = orderstatus;
    }

    private long paysum;

    public long getPaysum() {
        return paysum;
    }

    public void setPaysum(long paysum) {
        this.paysum = paysum;
    }

    private long contragentsum;

    public long getContragentsum() {
        return contragentsum;
    }

    public void setContragentsum(long contragentsum) {
        this.contragentsum = contragentsum;
    }

    private long createtime;

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    private String idofpayment;

    public String getIdofpayment() {
        return idofpayment;
    }

    public void setIdofpayment(String idofpayment) {
        this.idofpayment = idofpayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientpaymentorders that = (CfClientpaymentorders) o;

        if (contragentsum != that.contragentsum) {
            return false;
        }
        if (createtime != that.createtime) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idofclientpaymentorder != that.idofclientpaymentorder) {
            return false;
        }
        if (idofcontragent != that.idofcontragent) {
            return false;
        }
        if (orderstatus != that.orderstatus) {
            return false;
        }
        if (paymentmethod != that.paymentmethod) {
            return false;
        }
        if (paysum != that.paysum) {
            return false;
        }
        if (idofpayment != null ? !idofpayment.equals(that.idofpayment) : that.idofpayment != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofclientpaymentorder ^ (idofclientpaymentorder >>> 32));
        result = 31 * result + (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + paymentmethod;
        result = 31 * result + orderstatus;
        result = 31 * result + (int) (paysum ^ (paysum >>> 32));
        result = 31 * result + (int) (contragentsum ^ (contragentsum >>> 32));
        result = 31 * result + (int) (createtime ^ (createtime >>> 32));
        result = 31 * result + (idofpayment != null ? idofpayment.hashCode() : 0);
        return result;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }

    private CfContragents cfContragentsByIdofcontragent;

    public CfContragents getCfContragentsByIdofcontragent() {
        return cfContragentsByIdofcontragent;
    }

    public void setCfContragentsByIdofcontragent(CfContragents cfContragentsByIdofcontragent) {
        this.cfContragentsByIdofcontragent = cfContragentsByIdofcontragent;
    }

    private Collection<CfClientpayments> cfClientpaymentsesByIdofclientpaymentorder;

    public Collection<CfClientpayments> getCfClientpaymentsesByIdofclientpaymentorder() {
        return cfClientpaymentsesByIdofclientpaymentorder;
    }

    public void setCfClientpaymentsesByIdofclientpaymentorder(
            Collection<CfClientpayments> cfClientpaymentsesByIdofclientpaymentorder) {
        this.cfClientpaymentsesByIdofclientpaymentorder = cfClientpaymentsesByIdofclientpaymentorder;
    }
}
