package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfOrders {

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idoforder;

    public long getIdoforder() {
        return idoforder;
    }

    public void setIdoforder(long idoforder) {
        this.idoforder = idoforder;
    }

    private long idofcard;

    public long getIdofcard() {
        return idofcard;
    }

    public void setIdofcard(long idofcard) {
        this.idofcard = idofcard;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private long idofcashier;

    public long getIdofcashier() {
        return idofcashier;
    }

    public void setIdofcashier(long idofcashier) {
        this.idofcashier = idofcashier;
    }

    private long socdiscount;

    public long getSocdiscount() {
        return socdiscount;
    }

    public void setSocdiscount(long socdiscount) {
        this.socdiscount = socdiscount;
    }

    private long grantsum;

    public long getGrantsum() {
        return grantsum;
    }

    public void setGrantsum(long grantsum) {
        this.grantsum = grantsum;
    }

    private long rsum;

    public long getRsum() {
        return rsum;
    }

    public void setRsum(long rsum) {
        this.rsum = rsum;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long sumbycard;

    public long getSumbycard() {
        return sumbycard;
    }

    public void setSumbycard(long sumbycard) {
        this.sumbycard = sumbycard;
    }

    private long sumbycash;

    public long getSumbycash() {
        return sumbycash;
    }

    public void setSumbycash(long sumbycash) {
        this.sumbycash = sumbycash;
    }

    private long idofpos;

    public long getIdofpos() {
        return idofpos;
    }

    public void setIdofpos(long idofpos) {
        this.idofpos = idofpos;
    }

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private long trddiscount;

    public long getTrddiscount() {
        return trddiscount;
    }

    public void setTrddiscount(long trddiscount) {
        this.trddiscount = trddiscount;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfOrders cfOrders = (CfOrders) o;

        if (createddate != cfOrders.createddate) {
            return false;
        }
        if (grantsum != cfOrders.grantsum) {
            return false;
        }
        if (idofcard != cfOrders.idofcard) {
            return false;
        }
        if (idofcashier != cfOrders.idofcashier) {
            return false;
        }
        if (idofclient != cfOrders.idofclient) {
            return false;
        }
        if (idofcontragent != cfOrders.idofcontragent) {
            return false;
        }
        if (idoforder != cfOrders.idoforder) {
            return false;
        }
        if (idoforg != cfOrders.idoforg) {
            return false;
        }
        if (idofpos != cfOrders.idofpos) {
            return false;
        }
        if (idoftransaction != cfOrders.idoftransaction) {
            return false;
        }
        if (rsum != cfOrders.rsum) {
            return false;
        }
        if (socdiscount != cfOrders.socdiscount) {
            return false;
        }
        if (state != cfOrders.state) {
            return false;
        }
        if (sumbycard != cfOrders.sumbycard) {
            return false;
        }
        if (sumbycash != cfOrders.sumbycash) {
            return false;
        }
        if (trddiscount != cfOrders.trddiscount) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idoforder ^ (idoforder >>> 32));
        result = 31 * result + (int) (idofcard ^ (idofcard >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + (int) (idofcashier ^ (idofcashier >>> 32));
        result = 31 * result + (int) (socdiscount ^ (socdiscount >>> 32));
        result = 31 * result + (int) (grantsum ^ (grantsum >>> 32));
        result = 31 * result + (int) (rsum ^ (rsum >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (sumbycard ^ (sumbycard >>> 32));
        result = 31 * result + (int) (sumbycash ^ (sumbycash >>> 32));
        result = 31 * result + (int) (idofpos ^ (idofpos >>> 32));
        result = 31 * result + (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (int) (trddiscount ^ (trddiscount >>> 32));
        result = 31 * result + state;
        return result;
    }

    private Collection<CfOrderdetails> cfOrderdetailses;

    public Collection<CfOrderdetails> getCfOrderdetailses() {
        return cfOrderdetailses;
    }

    public void setCfOrderdetailses(Collection<CfOrderdetails> cfOrderdetailses) {
        this.cfOrderdetailses = cfOrderdetailses;
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

    private CfContragents cfContragentsByIdofcontragent;

    public CfContragents getCfContragentsByIdofcontragent() {
        return cfContragentsByIdofcontragent;
    }

    public void setCfContragentsByIdofcontragent(CfContragents cfContragentsByIdofcontragent) {
        this.cfContragentsByIdofcontragent = cfContragentsByIdofcontragent;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }

    private CfPos cfPosByIdofpos;

    public CfPos getCfPosByIdofpos() {
        return cfPosByIdofpos;
    }

    public void setCfPosByIdofpos(CfPos cfPosByIdofpos) {
        this.cfPosByIdofpos = cfPosByIdofpos;
    }

    private CfTransactions cfTransactionsByIdoftransaction;

    public CfTransactions getCfTransactionsByIdoftransaction() {
        return cfTransactionsByIdoftransaction;
    }

    public void setCfTransactionsByIdoftransaction(CfTransactions cfTransactionsByIdoftransaction) {
        this.cfTransactionsByIdoftransaction = cfTransactionsByIdoftransaction;
    }
}
