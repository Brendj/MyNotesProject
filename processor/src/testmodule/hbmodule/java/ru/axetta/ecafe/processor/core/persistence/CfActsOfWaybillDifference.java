package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfActsOfWaybillDifference {

    private long idofactofdifference;

    public long getIdofactofdifference() {
        return idofactofdifference;
    }

    public void setIdofactofdifference(long idofactofdifference) {
        this.idofactofdifference = idofactofdifference;
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

    private long dateofactofdifference;

    public long getDateofactofdifference() {
        return dateofactofdifference;
    }

    public void setDateofactofdifference(long dateofactofdifference) {
        this.dateofactofdifference = dateofactofdifference;
    }

    private String numberofactofdifference;

    public String getNumberofactofdifference() {
        return numberofactofdifference;
    }

    public void setNumberofactofdifference(String numberofactofdifference) {
        this.numberofactofdifference = numberofactofdifference;
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

        CfActsOfWaybillDifference that = (CfActsOfWaybillDifference) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (dateofactofdifference != that.dateofactofdifference) {
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
        if (numberofactofdifference != null ? !numberofactofdifference.equals(that.numberofactofdifference)
                : that.numberofactofdifference != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofactofdifference ^ (idofactofdifference >>> 32));
        result = 31 * result + (int) (idofstaff ^ (idofstaff >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (dateofactofdifference ^ (dateofactofdifference >>> 32));
        result = 31 * result + (numberofactofdifference != null ? numberofactofdifference.hashCode() : 0);
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

    private Collection<CfActsOfWaybillDifferencePositions> cfActsOfWaybillDifferencePositionsesByIdofactofdifference;

    public Collection<CfActsOfWaybillDifferencePositions> getCfActsOfWaybillDifferencePositionsesByIdofactofdifference() {
        return cfActsOfWaybillDifferencePositionsesByIdofactofdifference;
    }

    public void setCfActsOfWaybillDifferencePositionsesByIdofactofdifference(
            Collection<CfActsOfWaybillDifferencePositions> cfActsOfWaybillDifferencePositionsesByIdofactofdifference) {
        this.cfActsOfWaybillDifferencePositionsesByIdofactofdifference = cfActsOfWaybillDifferencePositionsesByIdofactofdifference;
    }
}
