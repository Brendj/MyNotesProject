package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfInstances {

    private long idofinstance;

    public long getIdofinstance() {
        return idofinstance;
    }

    public void setIdofinstance(long idofinstance) {
        this.idofinstance = idofinstance;
    }

    private long idofpublication;

    public long getIdofpublication() {
        return idofpublication;
    }

    public void setIdofpublication(long idofpublication) {
        this.idofpublication = idofpublication;
    }

    private boolean ingroup;

    public boolean isIngroup() {
        return ingroup;
    }

    public void setIngroup(boolean ingroup) {
        this.ingroup = ingroup;
    }

    private long idoffund;

    public long getIdoffund() {
        return idoffund;
    }

    public void setIdoffund(long idoffund) {
        this.idoffund = idoffund;
    }

    private String invnumber;

    public String getInvnumber() {
        return invnumber;
    }

    public void setInvnumber(String invnumber) {
        this.invnumber = invnumber;
    }

    private long invbook;

    public long getInvbook() {
        return invbook;
    }

    public void setInvbook(long invbook) {
        this.invbook = invbook;
    }

    private long idofksu1Record;

    public long getIdofksu1Record() {
        return idofksu1Record;
    }

    public void setIdofksu1Record(long idofksu1Record) {
        this.idofksu1Record = idofksu1Record;
    }

    private long idofksu2Record;

    public long getIdofksu2Record() {
        return idofksu2Record;
    }

    public void setIdofksu2Record(long idofksu2Record) {
        this.idofksu2Record = idofksu2Record;
    }

    private int cost;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
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

        CfInstances that = (CfInstances) o;

        if (cost != that.cost) {
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
        if (idofinstance != that.idofinstance) {
            return false;
        }
        if (idofksu1Record != that.idofksu1Record) {
            return false;
        }
        if (idofksu2Record != that.idofksu2Record) {
            return false;
        }
        if (idofpublication != that.idofpublication) {
            return false;
        }
        if (ingroup != that.ingroup) {
            return false;
        }
        if (invbook != that.invbook) {
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
        if (invnumber != null ? !invnumber.equals(that.invnumber) : that.invnumber != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofinstance ^ (idofinstance >>> 32));
        result = 31 * result + (int) (idofpublication ^ (idofpublication >>> 32));
        result = 31 * result + (ingroup ? 1 : 0);
        result = 31 * result + (int) (idoffund ^ (idoffund >>> 32));
        result = 31 * result + (invnumber != null ? invnumber.hashCode() : 0);
        result = 31 * result + (int) (invbook ^ (invbook >>> 32));
        result = 31 * result + (int) (idofksu1Record ^ (idofksu1Record >>> 32));
        result = 31 * result + (int) (idofksu2Record ^ (idofksu2Record >>> 32));
        result = 31 * result + cost;
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

    private CfFunds cfFundsByIdoffund;

    public CfFunds getCfFundsByIdoffund() {
        return cfFundsByIdoffund;
    }

    public void setCfFundsByIdoffund(CfFunds cfFundsByIdoffund) {
        this.cfFundsByIdoffund = cfFundsByIdoffund;
    }

    private CfInventorybooks cfInventorybooksByInvbook;

    public CfInventorybooks getCfInventorybooksByInvbook() {
        return cfInventorybooksByInvbook;
    }

    public void setCfInventorybooksByInvbook(CfInventorybooks cfInventorybooksByInvbook) {
        this.cfInventorybooksByInvbook = cfInventorybooksByInvbook;
    }

    private CfKsu1Records cfKsu1RecordsByIdofksu1Record;

    public CfKsu1Records getCfKsu1RecordsByIdofksu1Record() {
        return cfKsu1RecordsByIdofksu1Record;
    }

    public void setCfKsu1RecordsByIdofksu1Record(CfKsu1Records cfKsu1RecordsByIdofksu1Record) {
        this.cfKsu1RecordsByIdofksu1Record = cfKsu1RecordsByIdofksu1Record;
    }

    private CfKsu2Records cfKsu2RecordsByIdofksu2Record;

    public CfKsu2Records getCfKsu2RecordsByIdofksu2Record() {
        return cfKsu2RecordsByIdofksu2Record;
    }

    public void setCfKsu2RecordsByIdofksu2Record(CfKsu2Records cfKsu2RecordsByIdofksu2Record) {
        this.cfKsu2RecordsByIdofksu2Record = cfKsu2RecordsByIdofksu2Record;
    }
}
