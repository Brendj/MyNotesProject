package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfJournals {

    private long idofjournal;

    public long getIdofjournal() {
        return idofjournal;
    }

    public void setIdofjournal(long idofjournal) {
        this.idofjournal = idofjournal;
    }

    private long idoffund;

    public long getIdoffund() {
        return idoffund;
    }

    public void setIdoffund(long idoffund) {
        this.idoffund = idoffund;
    }

    private boolean isnewspaper;

    public boolean isIsnewspaper() {
        return isnewspaper;
    }

    public void setIsnewspaper(boolean isnewspaper) {
        this.isnewspaper = isnewspaper;
    }

    private long idofpublication;

    public long getIdofpublication() {
        return idofpublication;
    }

    public void setIdofpublication(long idofpublication) {
        this.idofpublication = idofpublication;
    }

    private int monthcount;

    public int getMonthcount() {
        return monthcount;
    }

    public void setMonthcount(int monthcount) {
        this.monthcount = monthcount;
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private long globalversion;

    public long getGlobalversion() {
        return globalversion;
    }

    public void setGlobalversion(long globalversion) {
        this.globalversion = globalversion;
    }

    private long orgowner;

    public long getOrgowner() {
        return orgowner;
    }

    public void setOrgowner(long orgowner) {
        this.orgowner = orgowner;
    }

    private boolean deletedstate;

    public boolean isDeletedstate() {
        return deletedstate;
    }

    public void setDeletedstate(boolean deletedstate) {
        this.deletedstate = deletedstate;
    }

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    private long lastupdate;

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    private long deletedate;

    public long getDeletedate() {
        return deletedate;
    }

    public void setDeletedate(long deletedate) {
        this.deletedate = deletedate;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfJournals that = (CfJournals) o;

        if (count != that.count) {
            return false;
        }
        if (createddate != that.createddate) {
            return false;
        }
        if (deletedate != that.deletedate) {
            return false;
        }
        if (deletedstate != that.deletedstate) {
            return false;
        }
        if (globalversion != that.globalversion) {
            return false;
        }
        if (idoffund != that.idoffund) {
            return false;
        }
        if (idofjournal != that.idofjournal) {
            return false;
        }
        if (idofpublication != that.idofpublication) {
            return false;
        }
        if (isnewspaper != that.isnewspaper) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (monthcount != that.monthcount) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofjournal ^ (idofjournal >>> 32));
        result = 31 * result + (int) (idoffund ^ (idoffund >>> 32));
        result = 31 * result + (isnewspaper ? 1 : 0);
        result = 31 * result + (int) (idofpublication ^ (idofpublication >>> 32));
        result = 31 * result + monthcount;
        result = 31 * result + count;
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfJournalitems> cfJournalitemsesByIdofjournal;

    public Collection<CfJournalitems> getCfJournalitemsesByIdofjournal() {
        return cfJournalitemsesByIdofjournal;
    }

    public void setCfJournalitemsesByIdofjournal(Collection<CfJournalitems> cfJournalitemsesByIdofjournal) {
        this.cfJournalitemsesByIdofjournal = cfJournalitemsesByIdofjournal;
    }

    private CfFunds cfFundsByIdoffund;

    public CfFunds getCfFundsByIdoffund() {
        return cfFundsByIdoffund;
    }

    public void setCfFundsByIdoffund(CfFunds cfFundsByIdoffund) {
        this.cfFundsByIdoffund = cfFundsByIdoffund;
    }

    private CfPublications cfPublicationsByIdofpublication;

    public CfPublications getCfPublicationsByIdofpublication() {
        return cfPublicationsByIdofpublication;
    }

    public void setCfPublicationsByIdofpublication(CfPublications cfPublicationsByIdofpublication) {
        this.cfPublicationsByIdofpublication = cfPublicationsByIdofpublication;
    }
}
