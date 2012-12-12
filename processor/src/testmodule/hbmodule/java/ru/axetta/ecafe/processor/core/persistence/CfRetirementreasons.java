package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfRetirementreasons {

    private long idofretirementreason;

    public long getIdofretirementreason() {
        return idofretirementreason;
    }

    public void setIdofretirementreason(long idofretirementreason) {
        this.idofretirementreason = idofretirementreason;
    }

    private String retirementreasonname;

    public String getRetirementreasonname() {
        return retirementreasonname;
    }

    public void setRetirementreasonname(String retirementreasonname) {
        this.retirementreasonname = retirementreasonname;
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

    private int hashcode;

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
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

        CfRetirementreasons that = (CfRetirementreasons) o;

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
        if (hashcode != that.hashcode) {
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
        if (sendall != that.sendall) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (retirementreasonname != null ? !retirementreasonname.equals(that.retirementreasonname)
                : that.retirementreasonname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofretirementreason ^ (idofretirementreason >>> 32));
        result = 31 * result + (retirementreasonname != null ? retirementreasonname.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + hashcode;
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfKsu2Records> cfKsu2RecordsesByIdofretirementreason;

    public Collection<CfKsu2Records> getCfKsu2RecordsesByIdofretirementreason() {
        return cfKsu2RecordsesByIdofretirementreason;
    }

    public void setCfKsu2RecordsesByIdofretirementreason(
            Collection<CfKsu2Records> cfKsu2RecordsesByIdofretirementreason) {
        this.cfKsu2RecordsesByIdofretirementreason = cfKsu2RecordsesByIdofretirementreason;
    }
}
