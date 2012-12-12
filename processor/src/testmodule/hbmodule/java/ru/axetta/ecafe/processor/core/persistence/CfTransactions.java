package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfTransactions {

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofcard;

    public long getIdofcard() {
        return idofcard;
    }

    public void setIdofcard(long idofcard) {
        this.idofcard = idofcard;
    }

    private long transactionsum;

    public long getTransactionsum() {
        return transactionsum;
    }

    public void setTransactionsum(long transactionsum) {
        this.transactionsum = transactionsum;
    }

    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private int sourcetype;

    public int getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(int sourcetype) {
        this.sourcetype = sourcetype;
    }

    private long transactiondate;

    public long getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(long transactiondate) {
        this.transactiondate = transactiondate;
    }

    private long balancebefore;

    public long getBalancebefore() {
        return balancebefore;
    }

    public void setBalancebefore(long balancebefore) {
        this.balancebefore = balancebefore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfTransactions that = (CfTransactions) o;

        if (balancebefore != that.balancebefore) {
            return false;
        }
        if (idofcard != that.idofcard) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idoftransaction != that.idoftransaction) {
            return false;
        }
        if (sourcetype != that.sourcetype) {
            return false;
        }
        if (transactiondate != that.transactiondate) {
            return false;
        }
        if (transactionsum != that.transactionsum) {
            return false;
        }
        if (source != null ? !source.equals(that.source) : that.source != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofcard ^ (idofcard >>> 32));
        result = 31 * result + (int) (transactionsum ^ (transactionsum >>> 32));
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + sourcetype;
        result = 31 * result + (int) (transactiondate ^ (transactiondate >>> 32));
        result = 31 * result + (int) (balancebefore ^ (balancebefore >>> 32));
        return result;
    }

    private Collection<CfAccountRefund> cfAccountRefundsByIdoftransaction;

    public Collection<CfAccountRefund> getCfAccountRefundsByIdoftransaction() {
        return cfAccountRefundsByIdoftransaction;
    }

    public void setCfAccountRefundsByIdoftransaction(Collection<CfAccountRefund> cfAccountRefundsByIdoftransaction) {
        this.cfAccountRefundsByIdoftransaction = cfAccountRefundsByIdoftransaction;
    }

    private Collection<CfAccountTransfers> cfAccountTransfersesByIdoftransaction;

    public Collection<CfAccountTransfers> getCfAccountTransfersesByIdoftransaction() {
        return cfAccountTransfersesByIdoftransaction;
    }

    public void setCfAccountTransfersesByIdoftransaction(
            Collection<CfAccountTransfers> cfAccountTransfersesByIdoftransaction) {
        this.cfAccountTransfersesByIdoftransaction = cfAccountTransfersesByIdoftransaction;
    }

    private Collection<CfAccountTransfers> cfAccountTransfersesByIdoftransaction_0;

    public Collection<CfAccountTransfers> getCfAccountTransfersesByIdoftransaction_0() {
        return cfAccountTransfersesByIdoftransaction_0;
    }

    public void setCfAccountTransfersesByIdoftransaction_0(
            Collection<CfAccountTransfers> cfAccountTransfersesByIdoftransaction_0) {
        this.cfAccountTransfersesByIdoftransaction_0 = cfAccountTransfersesByIdoftransaction_0;
    }

    private Collection<CfClientpayments> cfClientpaymentsesByIdoftransaction;

    public Collection<CfClientpayments> getCfClientpaymentsesByIdoftransaction() {
        return cfClientpaymentsesByIdoftransaction;
    }

    public void setCfClientpaymentsesByIdoftransaction(
            Collection<CfClientpayments> cfClientpaymentsesByIdoftransaction) {
        this.cfClientpaymentsesByIdoftransaction = cfClientpaymentsesByIdoftransaction;
    }

    private Collection<CfClientsms> cfClientsmsesByIdoftransaction;

    public Collection<CfClientsms> getCfClientsmsesByIdoftransaction() {
        return cfClientsmsesByIdoftransaction;
    }

    public void setCfClientsmsesByIdoftransaction(Collection<CfClientsms> cfClientsmsesByIdoftransaction) {
        this.cfClientsmsesByIdoftransaction = cfClientsmsesByIdoftransaction;
    }

    private Collection<CfContragentpayments> cfContragentpaymentsesByIdoftransaction;

    public Collection<CfContragentpayments> getCfContragentpaymentsesByIdoftransaction() {
        return cfContragentpaymentsesByIdoftransaction;
    }

    public void setCfContragentpaymentsesByIdoftransaction(
            Collection<CfContragentpayments> cfContragentpaymentsesByIdoftransaction) {
        this.cfContragentpaymentsesByIdoftransaction = cfContragentpaymentsesByIdoftransaction;
    }

    private Collection<CfOrders> cfOrdersesByIdoftransaction;

    public Collection<CfOrders> getCfOrdersesByIdoftransaction() {
        return cfOrdersesByIdoftransaction;
    }

    public void setCfOrdersesByIdoftransaction(Collection<CfOrders> cfOrdersesByIdoftransaction) {
        this.cfOrdersesByIdoftransaction = cfOrdersesByIdoftransaction;
    }

    private Collection<CfSubscriptionfee> cfSubscriptionfeesByIdoftransaction;

    public Collection<CfSubscriptionfee> getCfSubscriptionfeesByIdoftransaction() {
        return cfSubscriptionfeesByIdoftransaction;
    }

    public void setCfSubscriptionfeesByIdoftransaction(
            Collection<CfSubscriptionfee> cfSubscriptionfeesByIdoftransaction) {
        this.cfSubscriptionfeesByIdoftransaction = cfSubscriptionfeesByIdoftransaction;
    }

    private CfCards cfCardsByIdofcard;

    public CfCards getCfCardsByIdofcard() {
        return cfCardsByIdofcard;
    }

    public void setCfCardsByIdofcard(CfCards cfCardsByIdofcard) {
        this.cfCardsByIdofcard = cfCardsByIdofcard;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }
}
