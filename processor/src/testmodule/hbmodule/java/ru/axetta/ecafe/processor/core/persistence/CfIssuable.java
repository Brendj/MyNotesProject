package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfIssuable {

    private long idofissuable;

    public long getIdofissuable() {
        return idofissuable;
    }

    public void setIdofissuable(long idofissuable) {
        this.idofissuable = idofissuable;
    }

    private long barcode;

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    private String typeofissuable;

    public String getTypeofissuable() {
        return typeofissuable;
    }

    public void setTypeofissuable(String typeofissuable) {
        this.typeofissuable = typeofissuable;
    }

    private long idofinstance;

    public long getIdofinstance() {
        return idofinstance;
    }

    public void setIdofinstance(long idofinstance) {
        this.idofinstance = idofinstance;
    }

    private long idofjournalitem;

    public long getIdofjournalitem() {
        return idofjournalitem;
    }

    public void setIdofjournalitem(long idofjournalitem) {
        this.idofjournalitem = idofjournalitem;
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

        CfIssuable that = (CfIssuable) o;

        if (barcode != that.barcode) {
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
        if (idofinstance != that.idofinstance) {
            return false;
        }
        if (idofissuable != that.idofissuable) {
            return false;
        }
        if (idofjournalitem != that.idofjournalitem) {
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
        if (typeofissuable != null ? !typeofissuable.equals(that.typeofissuable) : that.typeofissuable != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofissuable ^ (idofissuable >>> 32));
        result = 31 * result + (int) (barcode ^ (barcode >>> 32));
        result = 31 * result + (typeofissuable != null ? typeofissuable.hashCode() : 0);
        result = 31 * result + (int) (idofinstance ^ (idofinstance >>> 32));
        result = 31 * result + (int) (idofjournalitem ^ (idofjournalitem >>> 32));
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
}
