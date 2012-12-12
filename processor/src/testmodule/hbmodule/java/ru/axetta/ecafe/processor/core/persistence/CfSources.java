package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSources {

    private long idofsource;

    public long getIdofsource() {
        return idofsource;
    }

    public void setIdofsource(long idofsource) {
        this.idofsource = idofsource;
    }

    private String sourcename;

    public String getSourcename() {
        return sourcename;
    }

    public void setSourcename(String sourcename) {
        this.sourcename = sourcename;
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

        CfSources cfSources = (CfSources) o;

        if (createddate != cfSources.createddate) {
            return false;
        }
        if (deletedate != cfSources.deletedate) {
            return false;
        }
        if (deletedstate != cfSources.deletedstate) {
            return false;
        }
        if (globalversion != cfSources.globalversion) {
            return false;
        }
        if (hashcode != cfSources.hashcode) {
            return false;
        }
        if (idofsource != cfSources.idofsource) {
            return false;
        }
        if (lastupdate != cfSources.lastupdate) {
            return false;
        }
        if (orgowner != cfSources.orgowner) {
            return false;
        }
        if (sendall != cfSources.sendall) {
            return false;
        }
        if (guid != null ? !guid.equals(cfSources.guid) : cfSources.guid != null) {
            return false;
        }
        if (sourcename != null ? !sourcename.equals(cfSources.sourcename) : cfSources.sourcename != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofsource ^ (idofsource >>> 32));
        result = 31 * result + (sourcename != null ? sourcename.hashCode() : 0);
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

    private Collection<CfAccompanyingdocuments> cfAccompanyingdocumentsesByIdofsource;

    public Collection<CfAccompanyingdocuments> getCfAccompanyingdocumentsesByIdofsource() {
        return cfAccompanyingdocumentsesByIdofsource;
    }

    public void setCfAccompanyingdocumentsesByIdofsource(
            Collection<CfAccompanyingdocuments> cfAccompanyingdocumentsesByIdofsource) {
        this.cfAccompanyingdocumentsesByIdofsource = cfAccompanyingdocumentsesByIdofsource;
    }
}
