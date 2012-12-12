package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfTechnologicalMapProducts {

    private long idoftechnomapproducts;

    public long getIdoftechnomapproducts() {
        return idoftechnomapproducts;
    }

    public void setIdoftechnomapproducts(long idoftechnomapproducts) {
        this.idoftechnomapproducts = idoftechnomapproducts;
    }

    private long idoftechnologicalmaps;

    public long getIdoftechnologicalmaps() {
        return idoftechnologicalmaps;
    }

    public void setIdoftechnologicalmaps(long idoftechnologicalmaps) {
        this.idoftechnologicalmaps = idoftechnologicalmaps;
    }

    private long idofproducts;

    public long getIdofproducts() {
        return idofproducts;
    }

    public void setIdofproducts(long idofproducts) {
        this.idofproducts = idofproducts;
    }

    private int netweight;

    public int getNetweight() {
        return netweight;
    }

    public void setNetweight(int netweight) {
        this.netweight = netweight;
    }

    private int grossweight;

    public int getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(int grossweight) {
        this.grossweight = grossweight;
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

    private long idofconfigurationprovider;

    public long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
    }

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
    }

    private int numbergroupreplace;

    public int getNumbergroupreplace() {
        return numbergroupreplace;
    }

    public void setNumbergroupreplace(int numbergroupreplace) {
        this.numbergroupreplace = numbergroupreplace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfTechnologicalMapProducts that = (CfTechnologicalMapProducts) o;

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
        if (grossweight != that.grossweight) {
            return false;
        }
        if (idofconfigurationprovider != that.idofconfigurationprovider) {
            return false;
        }
        if (idofproducts != that.idofproducts) {
            return false;
        }
        if (idoftechnologicalmaps != that.idoftechnologicalmaps) {
            return false;
        }
        if (idoftechnomapproducts != that.idoftechnomapproducts) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (netweight != that.netweight) {
            return false;
        }
        if (numbergroupreplace != that.numbergroupreplace) {
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoftechnomapproducts ^ (idoftechnomapproducts >>> 32));
        result = 31 * result + (int) (idoftechnologicalmaps ^ (idoftechnologicalmaps >>> 32));
        result = 31 * result + (int) (idofproducts ^ (idofproducts >>> 32));
        result = 31 * result + netweight;
        result = 31 * result + grossweight;
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (idofconfigurationprovider ^ (idofconfigurationprovider >>> 32));
        result = 31 * result + sendall;
        result = 31 * result + numbergroupreplace;
        return result;
    }

    private CfProducts cfProductsByIdofproducts;

    public CfProducts getCfProductsByIdofproducts() {
        return cfProductsByIdofproducts;
    }

    public void setCfProductsByIdofproducts(CfProducts cfProductsByIdofproducts) {
        this.cfProductsByIdofproducts = cfProductsByIdofproducts;
    }

    private CfTechnologicalMap cfTechnologicalMapByIdoftechnologicalmaps;

    public CfTechnologicalMap getCfTechnologicalMapByIdoftechnologicalmaps() {
        return cfTechnologicalMapByIdoftechnologicalmaps;
    }

    public void setCfTechnologicalMapByIdoftechnologicalmaps(
            CfTechnologicalMap cfTechnologicalMapByIdoftechnologicalmaps) {
        this.cfTechnologicalMapByIdoftechnologicalmaps = cfTechnologicalMapByIdoftechnologicalmaps;
    }
}
