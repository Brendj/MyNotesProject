package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCards {

    private long idofcard;

    public long getIdofcard() {
        return idofcard;
    }

    public void setIdofcard(long idofcard) {
        this.idofcard = idofcard;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long cardno;

    public long getCardno() {
        return cardno;
    }

    public void setCardno(long cardno) {
        this.cardno = cardno;
    }

    private int cardtype;

    public int getCardtype() {
        return cardtype;
    }

    public void setCardtype(int cardtype) {
        this.cardtype = cardtype;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long lastupdate;

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String lockreason;

    public String getLockreason() {
        return lockreason;
    }

    public void setLockreason(String lockreason) {
        this.lockreason = lockreason;
    }

    private long validdate;

    public long getValiddate() {
        return validdate;
    }

    public void setValiddate(long validdate) {
        this.validdate = validdate;
    }

    private long issuedate;

    public long getIssuedate() {
        return issuedate;
    }

    public void setIssuedate(long issuedate) {
        this.issuedate = issuedate;
    }

    private int lifestate;

    public int getLifestate() {
        return lifestate;
    }

    public void setLifestate(int lifestate) {
        this.lifestate = lifestate;
    }

    private long cardprintedno;

    public long getCardprintedno() {
        return cardprintedno;
    }

    public void setCardprintedno(long cardprintedno) {
        this.cardprintedno = cardprintedno;
    }

    private String externalid;

    public String getExternalid() {
        return externalid;
    }

    public void setExternalid(String externalid) {
        this.externalid = externalid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfCards cfCards = (CfCards) o;

        if (cardno != cfCards.cardno) {
            return false;
        }
        if (cardprintedno != cfCards.cardprintedno) {
            return false;
        }
        if (cardtype != cfCards.cardtype) {
            return false;
        }
        if (createddate != cfCards.createddate) {
            return false;
        }
        if (idofcard != cfCards.idofcard) {
            return false;
        }
        if (idofclient != cfCards.idofclient) {
            return false;
        }
        if (issuedate != cfCards.issuedate) {
            return false;
        }
        if (lastupdate != cfCards.lastupdate) {
            return false;
        }
        if (lifestate != cfCards.lifestate) {
            return false;
        }
        if (state != cfCards.state) {
            return false;
        }
        if (validdate != cfCards.validdate) {
            return false;
        }
        if (version != cfCards.version) {
            return false;
        }
        if (externalid != null ? !externalid.equals(cfCards.externalid) : cfCards.externalid != null) {
            return false;
        }
        if (lockreason != null ? !lockreason.equals(cfCards.lockreason) : cfCards.lockreason != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcard ^ (idofcard >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (cardno ^ (cardno >>> 32));
        result = 31 * result + cardtype;
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + state;
        result = 31 * result + (lockreason != null ? lockreason.hashCode() : 0);
        result = 31 * result + (int) (validdate ^ (validdate >>> 32));
        result = 31 * result + (int) (issuedate ^ (issuedate >>> 32));
        result = 31 * result + lifestate;
        result = 31 * result + (int) (cardprintedno ^ (cardprintedno >>> 32));
        result = 31 * result + (externalid != null ? externalid.hashCode() : 0);
        return result;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }

    private Collection<CfOrders> cfOrdersesByIdofcard;

    public Collection<CfOrders> getCfOrdersesByIdofcard() {
        return cfOrdersesByIdofcard;
    }

    public void setCfOrdersesByIdofcard(Collection<CfOrders> cfOrdersesByIdofcard) {
        this.cfOrdersesByIdofcard = cfOrdersesByIdofcard;
    }

    private Collection<CfTransactions> cfTransactionsesByIdofcard;

    public Collection<CfTransactions> getCfTransactionsesByIdofcard() {
        return cfTransactionsesByIdofcard;
    }

    public void setCfTransactionsesByIdofcard(Collection<CfTransactions> cfTransactionsesByIdofcard) {
        this.cfTransactionsesByIdofcard = cfTransactionsesByIdofcard;
    }
}
