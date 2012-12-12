package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSynchistory {

    private long idofsync;

    public long getIdofsync() {
        return idofsync;
    }

    public void setIdofsync(long idofsync) {
        this.idofsync = idofsync;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long syncstarttime;

    public long getSyncstarttime() {
        return syncstarttime;
    }

    public void setSyncstarttime(long syncstarttime) {
        this.syncstarttime = syncstarttime;
    }

    private long syncendtime;

    public long getSyncendtime() {
        return syncendtime;
    }

    public void setSyncendtime(long syncendtime) {
        this.syncendtime = syncendtime;
    }

    private int syncresult;

    public int getSyncresult() {
        return syncresult;
    }

    public void setSyncresult(int syncresult) {
        this.syncresult = syncresult;
    }

    private long idofpacket;

    public long getIdofpacket() {
        return idofpacket;
    }

    public void setIdofpacket(long idofpacket) {
        this.idofpacket = idofpacket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfSynchistory that = (CfSynchistory) o;

        if (idoforg != that.idoforg) {
            return false;
        }
        if (idofpacket != that.idofpacket) {
            return false;
        }
        if (idofsync != that.idofsync) {
            return false;
        }
        if (syncendtime != that.syncendtime) {
            return false;
        }
        if (syncresult != that.syncresult) {
            return false;
        }
        if (syncstarttime != that.syncstarttime) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofsync ^ (idofsync >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (syncstarttime ^ (syncstarttime >>> 32));
        result = 31 * result + (int) (syncendtime ^ (syncendtime >>> 32));
        result = 31 * result + syncresult;
        result = 31 * result + (int) (idofpacket ^ (idofpacket >>> 32));
        return result;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
