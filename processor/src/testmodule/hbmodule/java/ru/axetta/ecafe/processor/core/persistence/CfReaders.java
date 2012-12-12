package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfReaders {

    private long idofreader;

    public long getIdofreader() {
        return idofreader;
    }

    public void setIdofreader(long idofreader) {
        this.idofreader = idofreader;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
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

        CfReaders cfReaders = (CfReaders) o;

        if (createddate != cfReaders.createddate) {
            return false;
        }
        if (deletedate != cfReaders.deletedate) {
            return false;
        }
        if (deletedstate != cfReaders.deletedstate) {
            return false;
        }
        if (globalversion != cfReaders.globalversion) {
            return false;
        }
        if (idofclient != cfReaders.idofclient) {
            return false;
        }
        if (idofreader != cfReaders.idofreader) {
            return false;
        }
        if (lastupdate != cfReaders.lastupdate) {
            return false;
        }
        if (orgowner != cfReaders.orgowner) {
            return false;
        }
        if (sendall != cfReaders.sendall) {
            return false;
        }
        if (guid != null ? !guid.equals(cfReaders.guid) : cfReaders.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofreader ^ (idofreader >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
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

    private Collection<CfReaderreg> cfReaderregsByIdofreader;

    public Collection<CfReaderreg> getCfReaderregsByIdofreader() {
        return cfReaderregsByIdofreader;
    }

    public void setCfReaderregsByIdofreader(Collection<CfReaderreg> cfReaderregsByIdofreader) {
        this.cfReaderregsByIdofreader = cfReaderregsByIdofreader;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }
}
