package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfStaffs {

    private long idofstaff;

    public long getIdofstaff() {
        return idofstaff;
    }

    public void setIdofstaff(long idofstaff) {
        this.idofstaff = idofstaff;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idofrole;

    public long getIdofrole() {
        return idofrole;
    }

    public void setIdofrole(long idofrole) {
        this.idofrole = idofrole;
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

    private long parentid;

    public long getParentid() {
        return parentid;
    }

    public void setParentid(long parentid) {
        this.parentid = parentid;
    }

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    private String surname;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    private String firstname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    private String secondname;

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    private String staffposition;

    public String getStaffposition() {
        return staffposition;
    }

    public void setStaffposition(String staffposition) {
        this.staffposition = staffposition;
    }

    private String personalcode;

    public String getPersonalcode() {
        return personalcode;
    }

    public void setPersonalcode(String personalcode) {
        this.personalcode = personalcode;
    }

    private String rights;

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
    }

    private int hashcode;

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfStaffs cfStaffs = (CfStaffs) o;

        if (createddate != cfStaffs.createddate) {
            return false;
        }
        if (deletedate != cfStaffs.deletedate) {
            return false;
        }
        if (deletedstate != cfStaffs.deletedstate) {
            return false;
        }
        if (flags != cfStaffs.flags) {
            return false;
        }
        if (globalversion != cfStaffs.globalversion) {
            return false;
        }
        if (hashcode != cfStaffs.hashcode) {
            return false;
        }
        if (idofclient != cfStaffs.idofclient) {
            return false;
        }
        if (idofrole != cfStaffs.idofrole) {
            return false;
        }
        if (idofstaff != cfStaffs.idofstaff) {
            return false;
        }
        if (lastupdate != cfStaffs.lastupdate) {
            return false;
        }
        if (orgowner != cfStaffs.orgowner) {
            return false;
        }
        if (parentid != cfStaffs.parentid) {
            return false;
        }
        if (sendall != cfStaffs.sendall) {
            return false;
        }
        if (firstname != null ? !firstname.equals(cfStaffs.firstname) : cfStaffs.firstname != null) {
            return false;
        }
        if (guid != null ? !guid.equals(cfStaffs.guid) : cfStaffs.guid != null) {
            return false;
        }
        if (personalcode != null ? !personalcode.equals(cfStaffs.personalcode) : cfStaffs.personalcode != null) {
            return false;
        }
        if (rights != null ? !rights.equals(cfStaffs.rights) : cfStaffs.rights != null) {
            return false;
        }
        if (secondname != null ? !secondname.equals(cfStaffs.secondname) : cfStaffs.secondname != null) {
            return false;
        }
        if (staffposition != null ? !staffposition.equals(cfStaffs.staffposition) : cfStaffs.staffposition != null) {
            return false;
        }
        if (surname != null ? !surname.equals(cfStaffs.surname) : cfStaffs.surname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofstaff ^ (idofstaff >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofrole ^ (idofrole >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (parentid ^ (parentid >>> 32));
        result = 31 * result + flags;
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (secondname != null ? secondname.hashCode() : 0);
        result = 31 * result + (staffposition != null ? staffposition.hashCode() : 0);
        result = 31 * result + (personalcode != null ? personalcode.hashCode() : 0);
        result = 31 * result + (rights != null ? rights.hashCode() : 0);
        result = 31 * result + sendall;
        result = 31 * result + hashcode;
        return result;
    }

    private Collection<CfActsOfWaybillDifference> cfActsOfWaybillDifferencesByIdofstaff;

    public Collection<CfActsOfWaybillDifference> getCfActsOfWaybillDifferencesByIdofstaff() {
        return cfActsOfWaybillDifferencesByIdofstaff;
    }

    public void setCfActsOfWaybillDifferencesByIdofstaff(
            Collection<CfActsOfWaybillDifference> cfActsOfWaybillDifferencesByIdofstaff) {
        this.cfActsOfWaybillDifferencesByIdofstaff = cfActsOfWaybillDifferencesByIdofstaff;
    }

    private Collection<CfGoodsRequests> cfGoodsRequestsesByIdofstaff;

    public Collection<CfGoodsRequests> getCfGoodsRequestsesByIdofstaff() {
        return cfGoodsRequestsesByIdofstaff;
    }

    public void setCfGoodsRequestsesByIdofstaff(Collection<CfGoodsRequests> cfGoodsRequestsesByIdofstaff) {
        this.cfGoodsRequestsesByIdofstaff = cfGoodsRequestsesByIdofstaff;
    }

    private Collection<CfInternalDisposingDocuments> cfInternalDisposingDocumentsesByIdofstaff;

    public Collection<CfInternalDisposingDocuments> getCfInternalDisposingDocumentsesByIdofstaff() {
        return cfInternalDisposingDocumentsesByIdofstaff;
    }

    public void setCfInternalDisposingDocumentsesByIdofstaff(
            Collection<CfInternalDisposingDocuments> cfInternalDisposingDocumentsesByIdofstaff) {
        this.cfInternalDisposingDocumentsesByIdofstaff = cfInternalDisposingDocumentsesByIdofstaff;
    }

    private Collection<CfInternalIncomingDocuments> cfInternalIncomingDocumentsesByIdofstaff;

    public Collection<CfInternalIncomingDocuments> getCfInternalIncomingDocumentsesByIdofstaff() {
        return cfInternalIncomingDocumentsesByIdofstaff;
    }

    public void setCfInternalIncomingDocumentsesByIdofstaff(
            Collection<CfInternalIncomingDocuments> cfInternalIncomingDocumentsesByIdofstaff) {
        this.cfInternalIncomingDocumentsesByIdofstaff = cfInternalIncomingDocumentsesByIdofstaff;
    }

    private Collection<CfStateChanges> cfStateChangesesByIdofstaff;

    public Collection<CfStateChanges> getCfStateChangesesByIdofstaff() {
        return cfStateChangesesByIdofstaff;
    }

    public void setCfStateChangesesByIdofstaff(Collection<CfStateChanges> cfStateChangesesByIdofstaff) {
        this.cfStateChangesesByIdofstaff = cfStateChangesesByIdofstaff;
    }

    private Collection<CfWaybills> cfWaybillsesByIdofstaff;

    public Collection<CfWaybills> getCfWaybillsesByIdofstaff() {
        return cfWaybillsesByIdofstaff;
    }

    public void setCfWaybillsesByIdofstaff(Collection<CfWaybills> cfWaybillsesByIdofstaff) {
        this.cfWaybillsesByIdofstaff = cfWaybillsesByIdofstaff;
    }
}
