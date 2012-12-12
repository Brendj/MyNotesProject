package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfWaybills {

    private long idofwaybill;

    public long getIdofwaybill() {
        return idofwaybill;
    }

    public void setIdofwaybill(long idofwaybill) {
        this.idofwaybill = idofwaybill;
    }

    private long idofstaff;

    public long getIdofstaff() {
        return idofstaff;
    }

    public void setIdofstaff(long idofstaff) {
        this.idofstaff = idofstaff;
    }

    private long idofactofdifference;

    public long getIdofactofdifference() {
        return idofactofdifference;
    }

    public void setIdofactofdifference(long idofactofdifference) {
        this.idofactofdifference = idofactofdifference;
    }

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
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

    private String numberofwaybill;

    public String getNumberofwaybill() {
        return numberofwaybill;
    }

    public void setNumberofwaybill(String numberofwaybill) {
        this.numberofwaybill = numberofwaybill;
    }

    private long dateofwaybill;

    public long getDateofwaybill() {
        return dateofwaybill;
    }

    public void setDateofwaybill(long dateofwaybill) {
        this.dateofwaybill = dateofwaybill;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String shipper;

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    private String receiver;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

        CfWaybills that = (CfWaybills) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (dateofwaybill != that.dateofwaybill) {
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
        if (idofactofdifference != that.idofactofdifference) {
            return false;
        }
        if (idofstaff != that.idofstaff) {
            return false;
        }
        if (idofwaybill != that.idofwaybill) {
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
        if (state != that.state) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (numberofwaybill != null ? !numberofwaybill.equals(that.numberofwaybill) : that.numberofwaybill != null) {
            return false;
        }
        if (receiver != null ? !receiver.equals(that.receiver) : that.receiver != null) {
            return false;
        }
        if (shipper != null ? !shipper.equals(that.shipper) : that.shipper != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofwaybill ^ (idofwaybill >>> 32));
        result = 31 * result + (int) (idofstaff ^ (idofstaff >>> 32));
        result = 31 * result + (int) (idofactofdifference ^ (idofactofdifference >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (numberofwaybill != null ? numberofwaybill.hashCode() : 0);
        result = 31 * result + (int) (dateofwaybill ^ (dateofwaybill >>> 32));
        result = 31 * result + state;
        result = 31 * result + (shipper != null ? shipper.hashCode() : 0);
        result = 31 * result + (receiver != null ? receiver.hashCode() : 0);
        result = 31 * result + sendall;
        return result;
    }

    private CfStaffs cfStaffsByIdofstaff;

    public CfStaffs getCfStaffsByIdofstaff() {
        return cfStaffsByIdofstaff;
    }

    public void setCfStaffsByIdofstaff(CfStaffs cfStaffsByIdofstaff) {
        this.cfStaffsByIdofstaff = cfStaffsByIdofstaff;
    }

    private Collection<CfWaybillsPositions> cfWaybillsPositionsesByIdofwaybill;

    public Collection<CfWaybillsPositions> getCfWaybillsPositionsesByIdofwaybill() {
        return cfWaybillsPositionsesByIdofwaybill;
    }

    public void setCfWaybillsPositionsesByIdofwaybill(
            Collection<CfWaybillsPositions> cfWaybillsPositionsesByIdofwaybill) {
        this.cfWaybillsPositionsesByIdofwaybill = cfWaybillsPositionsesByIdofwaybill;
    }
}
