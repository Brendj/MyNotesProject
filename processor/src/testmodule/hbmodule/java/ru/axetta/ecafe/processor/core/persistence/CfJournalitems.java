package ru.axetta.ecafe.processor.core.persistence;

import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfJournalitems {

    private long idofjournalitem;

    public long getIdofjournalitem() {
        return idofjournalitem;
    }

    public void setIdofjournalitem(long idofjournalitem) {
        this.idofjournalitem = idofjournalitem;
    }

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

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private int cost;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    private long idofksu1Record;

    public long getIdofksu1Record() {
        return idofksu1Record;
    }

    public void setIdofksu1Record(long idofksu1Record) {
        this.idofksu1Record = idofksu1Record;
    }

    private long idofksu2Record;

    public long getIdofksu2Record() {
        return idofksu2Record;
    }

    public void setIdofksu2Record(long idofksu2Record) {
        this.idofksu2Record = idofksu2Record;
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

        CfJournalitems that = (CfJournalitems) o;

        if (cost != that.cost) {
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
        if (idofjournalitem != that.idofjournalitem) {
            return false;
        }
        if (idofksu1Record != that.idofksu1Record) {
            return false;
        }
        if (idofksu2Record != that.idofksu2Record) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (number != null ? !number.equals(that.number) : that.number != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofjournalitem ^ (idofjournalitem >>> 32));
        result = 31 * result + (int) (idofjournal ^ (idofjournal >>> 32));
        result = 31 * result + (int) (idoffund ^ (idoffund >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + cost;
        result = 31 * result + (int) (idofksu1Record ^ (idofksu1Record >>> 32));
        result = 31 * result + (int) (idofksu2Record ^ (idofksu2Record >>> 32));
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

    private CfFunds cfFundsByIdoffund;

    public CfFunds getCfFundsByIdoffund() {
        return cfFundsByIdoffund;
    }

    public void setCfFundsByIdoffund(CfFunds cfFundsByIdoffund) {
        this.cfFundsByIdoffund = cfFundsByIdoffund;
    }

    private CfJournals cfJournalsByIdofjournal;

    public CfJournals getCfJournalsByIdofjournal() {
        return cfJournalsByIdofjournal;
    }

    public void setCfJournalsByIdofjournal(CfJournals cfJournalsByIdofjournal) {
        this.cfJournalsByIdofjournal = cfJournalsByIdofjournal;
    }

    private CfKsu1Records cfKsu1RecordsByIdofksu1Record;

    public CfKsu1Records getCfKsu1RecordsByIdofksu1Record() {
        return cfKsu1RecordsByIdofksu1Record;
    }

    public void setCfKsu1RecordsByIdofksu1Record(CfKsu1Records cfKsu1RecordsByIdofksu1Record) {
        this.cfKsu1RecordsByIdofksu1Record = cfKsu1RecordsByIdofksu1Record;
    }

    private CfKsu2Records cfKsu2RecordsByIdofksu2Record;

    public CfKsu2Records getCfKsu2RecordsByIdofksu2Record() {
        return cfKsu2RecordsByIdofksu2Record;
    }

    public void setCfKsu2RecordsByIdofksu2Record(CfKsu2Records cfKsu2RecordsByIdofksu2Record) {
        this.cfKsu2RecordsByIdofksu2Record = cfKsu2RecordsByIdofksu2Record;
    }
}
