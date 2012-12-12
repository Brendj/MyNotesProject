package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class CfAccountRefund {

    private long idofaccountrefund;

    public long getIdofaccountrefund() {
        return idofaccountrefund;
    }

    public void setIdofaccountrefund(long idofaccountrefund) {
        this.idofaccountrefund = idofaccountrefund;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private long createdby;

    public long getCreatedby() {
        return createdby;
    }

    public void setCreatedby(long createdby) {
        this.createdby = createdby;
    }

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private long refundsum;

    public long getRefundsum() {
        return refundsum;
    }

    public void setRefundsum(long refundsum) {
        this.refundsum = refundsum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfAccountRefund that = (CfAccountRefund) o;

        if (createdby != that.createdby) {
            return false;
        }
        if (createddate != that.createddate) {
            return false;
        }
        if (idofaccountrefund != that.idofaccountrefund) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idoftransaction != that.idoftransaction) {
            return false;
        }
        if (refundsum != that.refundsum) {
            return false;
        }
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofaccountrefund ^ (idofaccountrefund >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (int) (createdby ^ (createdby >>> 32));
        result = 31 * result + (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + (int) (refundsum ^ (refundsum >>> 32));
        return result;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }

    private CfTransactions cfTransactionsByIdoftransaction;

    public CfTransactions getCfTransactionsByIdoftransaction() {
        return cfTransactionsByIdoftransaction;
    }

    public void setCfTransactionsByIdoftransaction(CfTransactions cfTransactionsByIdoftransaction) {
        this.cfTransactionsByIdoftransaction = cfTransactionsByIdoftransaction;
    }
}
