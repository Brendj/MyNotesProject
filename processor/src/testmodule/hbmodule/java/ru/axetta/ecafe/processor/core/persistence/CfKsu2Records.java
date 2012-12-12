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
public class CfKsu2Records {

    private long idofksu2Record;

    public long getIdofksu2Record() {
        return idofksu2Record;
    }

    public void setIdofksu2Record(long idofksu2Record) {
        this.idofksu2Record = idofksu2Record;
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

    private Date retirementdate;

    public Date getRetirementdate() {
        return retirementdate;
    }

    public void setRetirementdate(Date retirementdate) {
        this.retirementdate = retirementdate;
    }

    private long idofretirementreason;

    public long getIdofretirementreason() {
        return idofretirementreason;
    }

    public void setIdofretirementreason(long idofretirementreason) {
        this.idofretirementreason = idofretirementreason;
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

        CfKsu2Records that = (CfKsu2Records) o;

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
        if (idofksu2Record != that.idofksu2Record) {
            return false;
        }
        if (idofretirementreason != that.idofretirementreason) {
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
        if (retirementdate != null ? !retirementdate.equals(that.retirementdate) : that.retirementdate != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofksu2Record ^ (idofksu2Record >>> 32));
        result = 31 * result + recordnumber;
        result = 31 * result + (int) (idoffund ^ (idoffund >>> 32));
        result = 31 * result + (retirementdate != null ? retirementdate.hashCode() : 0);
        result = 31 * result + (int) (idofretirementreason ^ (idofretirementreason >>> 32));
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

    private Collection<CfInstances> cfInstancesesByIdofksu2Record;

    public Collection<CfInstances> getCfInstancesesByIdofksu2Record() {
        return cfInstancesesByIdofksu2Record;
    }

    public void setCfInstancesesByIdofksu2Record(Collection<CfInstances> cfInstancesesByIdofksu2Record) {
        this.cfInstancesesByIdofksu2Record = cfInstancesesByIdofksu2Record;
    }

    private Collection<CfJournalitems> cfJournalitemsesByIdofksu2Record;

    public Collection<CfJournalitems> getCfJournalitemsesByIdofksu2Record() {
        return cfJournalitemsesByIdofksu2Record;
    }

    public void setCfJournalitemsesByIdofksu2Record(Collection<CfJournalitems> cfJournalitemsesByIdofksu2Record) {
        this.cfJournalitemsesByIdofksu2Record = cfJournalitemsesByIdofksu2Record;
    }

    private CfFunds cfFundsByIdoffund;

    public CfFunds getCfFundsByIdoffund() {
        return cfFundsByIdoffund;
    }

    public void setCfFundsByIdoffund(CfFunds cfFundsByIdoffund) {
        this.cfFundsByIdoffund = cfFundsByIdoffund;
    }

    private CfRetirementreasons cfRetirementreasonsByIdofretirementreason;

    public CfRetirementreasons getCfRetirementreasonsByIdofretirementreason() {
        return cfRetirementreasonsByIdofretirementreason;
    }

    public void setCfRetirementreasonsByIdofretirementreason(
            CfRetirementreasons cfRetirementreasonsByIdofretirementreason) {
        this.cfRetirementreasonsByIdofretirementreason = cfRetirementreasonsByIdofretirementreason;
    }
}
