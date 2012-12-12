package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfFunds {

    private long idoffund;

    public long getIdoffund() {
        return idoffund;
    }

    public void setIdoffund(long idoffund) {
        this.idoffund = idoffund;
    }

    private String fundname;

    public String getFundname() {
        return fundname;
    }

    public void setFundname(String fundname) {
        this.fundname = fundname;
    }

    private boolean stud;

    public boolean isStud() {
        return stud;
    }

    public void setStud(boolean stud) {
        this.stud = stud;
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

        CfFunds cfFunds = (CfFunds) o;

        if (createddate != cfFunds.createddate) {
            return false;
        }
        if (deletedate != cfFunds.deletedate) {
            return false;
        }
        if (deletedstate != cfFunds.deletedstate) {
            return false;
        }
        if (globalversion != cfFunds.globalversion) {
            return false;
        }
        if (idoffund != cfFunds.idoffund) {
            return false;
        }
        if (lastupdate != cfFunds.lastupdate) {
            return false;
        }
        if (orgowner != cfFunds.orgowner) {
            return false;
        }
        if (sendall != cfFunds.sendall) {
            return false;
        }
        if (stud != cfFunds.stud) {
            return false;
        }
        if (fundname != null ? !fundname.equals(cfFunds.fundname) : cfFunds.fundname != null) {
            return false;
        }
        if (guid != null ? !guid.equals(cfFunds.guid) : cfFunds.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoffund ^ (idoffund >>> 32));
        result = 31 * result + (fundname != null ? fundname.hashCode() : 0);
        result = 31 * result + (stud ? 1 : 0);
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

    private Collection<CfInstances> cfInstancesesByIdoffund;

    public Collection<CfInstances> getCfInstancesesByIdoffund() {
        return cfInstancesesByIdoffund;
    }

    public void setCfInstancesesByIdoffund(Collection<CfInstances> cfInstancesesByIdoffund) {
        this.cfInstancesesByIdoffund = cfInstancesesByIdoffund;
    }

    private Collection<CfJournalitems> cfJournalitemsesByIdoffund;

    public Collection<CfJournalitems> getCfJournalitemsesByIdoffund() {
        return cfJournalitemsesByIdoffund;
    }

    public void setCfJournalitemsesByIdoffund(Collection<CfJournalitems> cfJournalitemsesByIdoffund) {
        this.cfJournalitemsesByIdoffund = cfJournalitemsesByIdoffund;
    }

    private Collection<CfJournals> cfJournalsesByIdoffund;

    public Collection<CfJournals> getCfJournalsesByIdoffund() {
        return cfJournalsesByIdoffund;
    }

    public void setCfJournalsesByIdoffund(Collection<CfJournals> cfJournalsesByIdoffund) {
        this.cfJournalsesByIdoffund = cfJournalsesByIdoffund;
    }

    private Collection<CfKsu1Records> cfKsu1RecordsesByIdoffund;

    public Collection<CfKsu1Records> getCfKsu1RecordsesByIdoffund() {
        return cfKsu1RecordsesByIdoffund;
    }

    public void setCfKsu1RecordsesByIdoffund(Collection<CfKsu1Records> cfKsu1RecordsesByIdoffund) {
        this.cfKsu1RecordsesByIdoffund = cfKsu1RecordsesByIdoffund;
    }

    private Collection<CfKsu2Records> cfKsu2RecordsesByIdoffund;

    public Collection<CfKsu2Records> getCfKsu2RecordsesByIdoffund() {
        return cfKsu2RecordsesByIdoffund;
    }

    public void setCfKsu2RecordsesByIdoffund(Collection<CfKsu2Records> cfKsu2RecordsesByIdoffund) {
        this.cfKsu2RecordsesByIdoffund = cfKsu2RecordsesByIdoffund;
    }
}
