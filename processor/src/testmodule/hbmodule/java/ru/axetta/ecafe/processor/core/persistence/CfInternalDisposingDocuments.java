package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfInternalDisposingDocuments {

    private long idofinternaldisposingdocument;

    public long getIdofinternaldisposingdocument() {
        return idofinternaldisposingdocument;
    }

    public void setIdofinternaldisposingdocument(long idofinternaldisposingdocument) {
        this.idofinternaldisposingdocument = idofinternaldisposingdocument;
    }

    private long idofstaff;

    public long getIdofstaff() {
        return idofstaff;
    }

    public void setIdofstaff(long idofstaff) {
        this.idofstaff = idofstaff;
    }

    private long idofactofinventarization;

    public long getIdofactofinventarization() {
        return idofactofinventarization;
    }

    public void setIdofactofinventarization(long idofactofinventarization) {
        this.idofactofinventarization = idofactofinventarization;
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

    private int typeofinternaldisposingdocument;

    public int getTypeofinternaldisposingdocument() {
        return typeofinternaldisposingdocument;
    }

    public void setTypeofinternaldisposingdocument(int typeofinternaldisposingdocument) {
        this.typeofinternaldisposingdocument = typeofinternaldisposingdocument;
    }

    private long dateofinternaldisposingdocument;

    public long getDateofinternaldisposingdocument() {
        return dateofinternaldisposingdocument;
    }

    public void setDateofinternaldisposingdocument(long dateofinternaldisposingdocument) {
        this.dateofinternaldisposingdocument = dateofinternaldisposingdocument;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

        CfInternalDisposingDocuments that = (CfInternalDisposingDocuments) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (dateofinternaldisposingdocument != that.dateofinternaldisposingdocument) {
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
        if (idofactofinventarization != that.idofactofinventarization) {
            return false;
        }
        if (idofinternaldisposingdocument != that.idofinternaldisposingdocument) {
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
        if (typeofinternaldisposingdocument != that.typeofinternaldisposingdocument) {
            return false;
        }
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofinternaldisposingdocument ^ (idofinternaldisposingdocument >>> 32));
        result = 31 * result + (int) (idofstaff ^ (idofstaff >>> 32));
        result = 31 * result + (int) (idofactofinventarization ^ (idofactofinventarization >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + typeofinternaldisposingdocument;
        result = 31 * result + (int) (dateofinternaldisposingdocument ^ (dateofinternaldisposingdocument >>> 32));
        result = 31 * result + state;
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfInternalDisposingDocumentPositions> cfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument;

    public Collection<CfInternalDisposingDocumentPositions> getCfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument() {
        return cfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument;
    }

    public void setCfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument(
            Collection<CfInternalDisposingDocumentPositions> cfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument) {
        this.cfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument = cfInternalDisposingDocumentPositionsesByIdofinternaldisposingdocument;
    }

    private CfStaffs cfStaffsByIdofstaff;

    public CfStaffs getCfStaffsByIdofstaff() {
        return cfStaffsByIdofstaff;
    }

    public void setCfStaffsByIdofstaff(CfStaffs cfStaffsByIdofstaff) {
        this.cfStaffsByIdofstaff = cfStaffsByIdofstaff;
    }
}
