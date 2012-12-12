package ru.axetta.ecafe.processor.core.persistence;

import java.sql.Date;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfKsu1Records {

    private long idofksu1Record;

    public long getIdofksu1Record() {
        return idofksu1Record;
    }

    public void setIdofksu1Record(long idofksu1Record) {
        this.idofksu1Record = idofksu1Record;
    }

    private int recordnumber;

    public int getRecordnumber() {
        return recordnumber;
    }

    public void setRecordnumber(int recordnumber) {
        this.recordnumber = recordnumber;
    }

    private long idoffund;

    public long getIdoffund() {
        return idoffund;
    }

    public void setIdoffund(long idoffund) {
        this.idoffund = idoffund;
    }

    private Date incomedate;

    public Date getIncomedate() {
        return incomedate;
    }

    public void setIncomedate(Date incomedate) {
        this.incomedate = incomedate;
    }

    private long accompanyingdocument;

    public long getAccompanyingdocument() {
        return accompanyingdocument;
    }

    public void setAccompanyingdocument(long accompanyingdocument) {
        this.accompanyingdocument = accompanyingdocument;
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

        CfKsu1Records that = (CfKsu1Records) o;

        if (accompanyingdocument != that.accompanyingdocument) {
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
        if (idofksu1Record != that.idofksu1Record) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (recordnumber != that.recordnumber) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (incomedate != null ? !incomedate.equals(that.incomedate) : that.incomedate != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofksu1Record ^ (idofksu1Record >>> 32));
        result = 31 * result + recordnumber;
        result = 31 * result + (int) (idoffund ^ (idoffund >>> 32));
        result = 31 * result + (incomedate != null ? incomedate.hashCode() : 0);
        result = 31 * result + (int) (accompanyingdocument ^ (accompanyingdocument >>> 32));
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

    private Collection<CfInstances> cfInstancesesByIdofksu1Record;

    public Collection<CfInstances> getCfInstancesesByIdofksu1Record() {
        return cfInstancesesByIdofksu1Record;
    }

    public void setCfInstancesesByIdofksu1Record(Collection<CfInstances> cfInstancesesByIdofksu1Record) {
        this.cfInstancesesByIdofksu1Record = cfInstancesesByIdofksu1Record;
    }

    private Collection<CfJournalitems> cfJournalitemsesByIdofksu1Record;

    public Collection<CfJournalitems> getCfJournalitemsesByIdofksu1Record() {
        return cfJournalitemsesByIdofksu1Record;
    }

    public void setCfJournalitemsesByIdofksu1Record(Collection<CfJournalitems> cfJournalitemsesByIdofksu1Record) {
        this.cfJournalitemsesByIdofksu1Record = cfJournalitemsesByIdofksu1Record;
    }

    private CfAccompanyingdocuments cfAccompanyingdocumentsByAccompanyingdocument;

    public CfAccompanyingdocuments getCfAccompanyingdocumentsByAccompanyingdocument() {
        return cfAccompanyingdocumentsByAccompanyingdocument;
    }

    public void setCfAccompanyingdocumentsByAccompanyingdocument(
            CfAccompanyingdocuments cfAccompanyingdocumentsByAccompanyingdocument) {
        this.cfAccompanyingdocumentsByAccompanyingdocument = cfAccompanyingdocumentsByAccompanyingdocument;
    }

    private CfFunds cfFundsByIdoffund;

    public CfFunds getCfFundsByIdoffund() {
        return cfFundsByIdoffund;
    }

    public void setCfFundsByIdoffund(CfFunds cfFundsByIdoffund) {
        this.cfFundsByIdoffund = cfFundsByIdoffund;
    }
}
