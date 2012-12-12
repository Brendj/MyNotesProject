package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class CfAccountTransfers {

    private long idofaccounttransfer;

    public long getIdofaccounttransfer() {
        return idofaccounttransfer;
    }

    public void setIdofaccounttransfer(long idofaccounttransfer) {
        this.idofaccounttransfer = idofaccounttransfer;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long idofclientbenefactor;

    public long getIdofclientbenefactor() {
        return idofclientbenefactor;
    }

    public void setIdofclientbenefactor(long idofclientbenefactor) {
        this.idofclientbenefactor = idofclientbenefactor;
    }

    private long idofclientbeneficiary;

    public long getIdofclientbeneficiary() {
        return idofclientbeneficiary;
    }

    public void setIdofclientbeneficiary(long idofclientbeneficiary) {
        this.idofclientbeneficiary = idofclientbeneficiary;
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

    private long idoftransactiononbenefactor;

    public long getIdoftransactiononbenefactor() {
        return idoftransactiononbenefactor;
    }

    public void setIdoftransactiononbenefactor(long idoftransactiononbenefactor) {
        this.idoftransactiononbenefactor = idoftransactiononbenefactor;
    }

    private long idoftransactiononbeneficiary;

    public long getIdoftransactiononbeneficiary() {
        return idoftransactiononbeneficiary;
    }

    public void setIdoftransactiononbeneficiary(long idoftransactiononbeneficiary) {
        this.idoftransactiononbeneficiary = idoftransactiononbeneficiary;
    }

    private long transfersum;

    public long getTransfersum() {
        return transfersum;
    }

    public void setTransfersum(long transfersum) {
        this.transfersum = transfersum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfAccountTransfers that = (CfAccountTransfers) o;

        if (createdby != that.createdby) {
            return false;
        }
        if (createddate != that.createddate) {
            return false;
        }
        if (idofaccounttransfer != that.idofaccounttransfer) {
            return false;
        }
        if (idofclientbenefactor != that.idofclientbenefactor) {
            return false;
        }
        if (idofclientbeneficiary != that.idofclientbeneficiary) {
            return false;
        }
        if (idoftransactiononbenefactor != that.idoftransactiononbenefactor) {
            return false;
        }
        if (idoftransactiononbeneficiary != that.idoftransactiononbeneficiary) {
            return false;
        }
        if (transfersum != that.transfersum) {
            return false;
        }
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofaccounttransfer ^ (idofaccounttransfer >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (idofclientbenefactor ^ (idofclientbenefactor >>> 32));
        result = 31 * result + (int) (idofclientbeneficiary ^ (idofclientbeneficiary >>> 32));
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (int) (createdby ^ (createdby >>> 32));
        result = 31 * result + (int) (idoftransactiononbenefactor ^ (idoftransactiononbenefactor >>> 32));
        result = 31 * result + (int) (idoftransactiononbeneficiary ^ (idoftransactiononbeneficiary >>> 32));
        result = 31 * result + (int) (transfersum ^ (transfersum >>> 32));
        return result;
    }

    private CfClients cfClientsByIdofclientbeneficiary;

    public CfClients getCfClientsByIdofclientbeneficiary() {
        return cfClientsByIdofclientbeneficiary;
    }

    public void setCfClientsByIdofclientbeneficiary(CfClients cfClientsByIdofclientbeneficiary) {
        this.cfClientsByIdofclientbeneficiary = cfClientsByIdofclientbeneficiary;
    }

    private CfClients cfClientsByIdofclientbenefactor;

    public CfClients getCfClientsByIdofclientbenefactor() {
        return cfClientsByIdofclientbenefactor;
    }

    public void setCfClientsByIdofclientbenefactor(CfClients cfClientsByIdofclientbenefactor) {
        this.cfClientsByIdofclientbenefactor = cfClientsByIdofclientbenefactor;
    }

    private CfTransactions cfTransactionsByIdoftransactiononbeneficiary;

    public CfTransactions getCfTransactionsByIdoftransactiononbeneficiary() {
        return cfTransactionsByIdoftransactiononbeneficiary;
    }

    public void setCfTransactionsByIdoftransactiononbeneficiary(
            CfTransactions cfTransactionsByIdoftransactiononbeneficiary) {
        this.cfTransactionsByIdoftransactiononbeneficiary = cfTransactionsByIdoftransactiononbeneficiary;
    }

    private CfTransactions cfTransactionsByIdoftransactiononbenefactor;

    public CfTransactions getCfTransactionsByIdoftransactiononbenefactor() {
        return cfTransactionsByIdoftransactiononbenefactor;
    }

    public void setCfTransactionsByIdoftransactiononbenefactor(
            CfTransactions cfTransactionsByIdoftransactiononbenefactor) {
        this.cfTransactionsByIdoftransactiononbenefactor = cfTransactionsByIdoftransactiononbenefactor;
    }
}
