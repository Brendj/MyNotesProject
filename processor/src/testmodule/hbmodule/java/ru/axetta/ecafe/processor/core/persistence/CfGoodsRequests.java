package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfGoodsRequests {

    private long idofgoodsrequest;

    public long getIdofgoodsrequest() {
        return idofgoodsrequest;
    }

    public void setIdofgoodsrequest(long idofgoodsrequest) {
        this.idofgoodsrequest = idofgoodsrequest;
    }

    private long idofstaff;

    public long getIdofstaff() {
        return idofstaff;
    }

    public void setIdofstaff(long idofstaff) {
        this.idofstaff = idofstaff;
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

    private String numberofgoodsrequest;

    public String getNumberofgoodsrequest() {
        return numberofgoodsrequest;
    }

    public void setNumberofgoodsrequest(String numberofgoodsrequest) {
        this.numberofgoodsrequest = numberofgoodsrequest;
    }

    private long dateofgoodsrequest;

    public long getDateofgoodsrequest() {
        return dateofgoodsrequest;
    }

    public void setDateofgoodsrequest(long dateofgoodsrequest) {
        this.dateofgoodsrequest = dateofgoodsrequest;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private long donedate;

    public long getDonedate() {
        return donedate;
    }

    public void setDonedate(long donedate) {
        this.donedate = donedate;
    }

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

        CfGoodsRequests that = (CfGoodsRequests) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (dateofgoodsrequest != that.dateofgoodsrequest) {
            return false;
        }
        if (deletedate != that.deletedate) {
            return false;
        }
        if (deletedstate != that.deletedstate) {
            return false;
        }
        if (donedate != that.donedate) {
            return false;
        }
        if (globalversion != that.globalversion) {
            return false;
        }
        if (idofgoodsrequest != that.idofgoodsrequest) {
            return false;
        }
        if (idofstaff != that.idofstaff) {
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
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (numberofgoodsrequest != null ? !numberofgoodsrequest.equals(that.numberofgoodsrequest)
                : that.numberofgoodsrequest != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofgoodsrequest ^ (idofgoodsrequest >>> 32));
        result = 31 * result + (int) (idofstaff ^ (idofstaff >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (numberofgoodsrequest != null ? numberofgoodsrequest.hashCode() : 0);
        result = 31 * result + (int) (dateofgoodsrequest ^ (dateofgoodsrequest >>> 32));
        result = 31 * result + state;
        result = 31 * result + (int) (donedate ^ (donedate >>> 32));
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
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

    private Collection<CfGoodsRequestsPositions> cfGoodsRequestsPositionsesByIdofgoodsrequest;

    public Collection<CfGoodsRequestsPositions> getCfGoodsRequestsPositionsesByIdofgoodsrequest() {
        return cfGoodsRequestsPositionsesByIdofgoodsrequest;
    }

    public void setCfGoodsRequestsPositionsesByIdofgoodsrequest(
            Collection<CfGoodsRequestsPositions> cfGoodsRequestsPositionsesByIdofgoodsrequest) {
        this.cfGoodsRequestsPositionsesByIdofgoodsrequest = cfGoodsRequestsPositionsesByIdofgoodsrequest;
    }
}
