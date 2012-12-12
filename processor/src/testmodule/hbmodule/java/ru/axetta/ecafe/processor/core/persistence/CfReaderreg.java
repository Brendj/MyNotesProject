package ru.axetta.ecafe.processor.core.persistence;

import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfReaderreg {

    private long idofreg;

    public long getIdofreg() {
        return idofreg;
    }

    public void setIdofreg(long idofreg) {
        this.idofreg = idofreg;
    }

    private long idofreader;

    public long getIdofreader() {
        return idofreader;
    }

    public void setIdofreader(long idofreader) {
        this.idofreader = idofreader;
    }

    private long idofclientgrouphist;

    public long getIdofclientgrouphist() {
        return idofclientgrouphist;
    }

    public void setIdofclientgrouphist(long idofclientgrouphist) {
        this.idofclientgrouphist = idofclientgrouphist;
    }

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

        CfReaderreg that = (CfReaderreg) o;

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
        if (idofclientgrouphist != that.idofclientgrouphist) {
            return false;
        }
        if (idofreader != that.idofreader) {
            return false;
        }
        if (idofreg != that.idofreg) {
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofreg ^ (idofreg >>> 32));
        result = 31 * result + (int) (idofreader ^ (idofreader >>> 32));
        result = 31 * result + (int) (idofclientgrouphist ^ (idofclientgrouphist >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
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

    private CfReaders cfReadersByIdofreader;

    public CfReaders getCfReadersByIdofreader() {
        return cfReadersByIdofreader;
    }

    public void setCfReadersByIdofreader(CfReaders cfReadersByIdofreader) {
        this.cfReadersByIdofreader = cfReadersByIdofreader;
    }
}
