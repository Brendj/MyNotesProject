package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfTypesofaccompanyingdocuments {

    private long idoftypeofaccompanyingdocument;

    public long getIdoftypeofaccompanyingdocument() {
        return idoftypeofaccompanyingdocument;
    }

    public void setIdoftypeofaccompanyingdocument(long idoftypeofaccompanyingdocument) {
        this.idoftypeofaccompanyingdocument = idoftypeofaccompanyingdocument;
    }

    private String typeofaccompanyingdocumentname;

    public String getTypeofaccompanyingdocumentname() {
        return typeofaccompanyingdocumentname;
    }

    public void setTypeofaccompanyingdocumentname(String typeofaccompanyingdocumentname) {
        this.typeofaccompanyingdocumentname = typeofaccompanyingdocumentname;
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

    private int hashcode;

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
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

        CfTypesofaccompanyingdocuments that = (CfTypesofaccompanyingdocuments) o;

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
        if (hashcode != that.hashcode) {
            return false;
        }
        if (idoftypeofaccompanyingdocument != that.idoftypeofaccompanyingdocument) {
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
        if (typeofaccompanyingdocumentname != null ? !typeofaccompanyingdocumentname
                .equals(that.typeofaccompanyingdocumentname) : that.typeofaccompanyingdocumentname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoftypeofaccompanyingdocument ^ (idoftypeofaccompanyingdocument >>> 32));
        result = 31 * result + (typeofaccompanyingdocumentname != null ? typeofaccompanyingdocumentname.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + hashcode;
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfAccompanyingdocuments> cfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument;

    public Collection<CfAccompanyingdocuments> getCfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument() {
        return cfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument;
    }

    public void setCfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument(
            Collection<CfAccompanyingdocuments> cfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument) {
        this.cfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument = cfAccompanyingdocumentsesByIdoftypeofaccompanyingdocument;
    }
}
