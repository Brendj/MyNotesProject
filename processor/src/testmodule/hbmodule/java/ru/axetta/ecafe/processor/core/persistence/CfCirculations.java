package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCirculations {

    private long idofcirculation;

    public long getIdofcirculation() {
        return idofcirculation;
    }

    public void setIdofcirculation(long idofcirculation) {
        this.idofcirculation = idofcirculation;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofparentcirculation;

    public long getIdofparentcirculation() {
        return idofparentcirculation;
    }

    public void setIdofparentcirculation(long idofparentcirculation) {
        this.idofparentcirculation = idofparentcirculation;
    }

    private long idofissuable;

    public long getIdofissuable() {
        return idofissuable;
    }

    public void setIdofissuable(long idofissuable) {
        this.idofissuable = idofissuable;
    }

    private long issuancedate;

    public long getIssuancedate() {
        return issuancedate;
    }

    public void setIssuancedate(long issuancedate) {
        this.issuancedate = issuancedate;
    }

    private long refunddate;

    public long getRefunddate() {
        return refunddate;
    }

    public void setRefunddate(long refunddate) {
        this.refunddate = refunddate;
    }

    private long realrefunddate;

    public long getRealrefunddate() {
        return realrefunddate;
    }

    public void setRealrefunddate(long realrefunddate) {
        this.realrefunddate = realrefunddate;
    }

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

        CfCirculations that = (CfCirculations) o;

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
        if (idofcirculation != that.idofcirculation) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idofissuable != that.idofissuable) {
            return false;
        }
        if (idofparentcirculation != that.idofparentcirculation) {
            return false;
        }
        if (issuancedate != that.issuancedate) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (quantity != that.quantity) {
            return false;
        }
        if (realrefunddate != that.realrefunddate) {
            return false;
        }
        if (refunddate != that.refunddate) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (status != that.status) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcirculation ^ (idofcirculation >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofparentcirculation ^ (idofparentcirculation >>> 32));
        result = 31 * result + (int) (idofissuable ^ (idofissuable >>> 32));
        result = 31 * result + (int) (issuancedate ^ (issuancedate >>> 32));
        result = 31 * result + (int) (refunddate ^ (refunddate >>> 32));
        result = 31 * result + (int) (realrefunddate ^ (realrefunddate >>> 32));
        result = 31 * result + status;
        result = 31 * result + quantity;
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

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }
}
