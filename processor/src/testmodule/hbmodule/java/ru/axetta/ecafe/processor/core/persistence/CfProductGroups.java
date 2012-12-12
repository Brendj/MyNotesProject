package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfProductGroups {

    private long idofproductgroups;

    public long getIdofproductgroups() {
        return idofproductgroups;
    }

    public void setIdofproductgroups(long idofproductgroups) {
        this.idofproductgroups = idofproductgroups;
    }

    private String nameofgroup;

    public String getNameofgroup() {
        return nameofgroup;
    }

    public void setNameofgroup(String nameofgroup) {
        this.nameofgroup = nameofgroup;
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

    private boolean deletedstate;

    public boolean isDeletedstate() {
        return deletedstate;
    }

    public void setDeletedstate(boolean deletedstate) {
        this.deletedstate = deletedstate;
    }

    private String classificationcode;

    public String getClassificationcode() {
        return classificationcode;
    }

    public void setClassificationcode(String classificationcode) {
        this.classificationcode = classificationcode;
    }

    private long idofconfigurationprovider;

    public long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
    }

    private long orgowner;

    public long getOrgowner() {
        return orgowner;
    }

    public void setOrgowner(long orgowner) {
        this.orgowner = orgowner;
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

        CfProductGroups that = (CfProductGroups) o;

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
        if (idofconfigurationprovider != that.idofconfigurationprovider) {
            return false;
        }
        if (idofproductgroups != that.idofproductgroups) {
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
        if (classificationcode != null ? !classificationcode.equals(that.classificationcode)
                : that.classificationcode != null) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (nameofgroup != null ? !nameofgroup.equals(that.nameofgroup) : that.nameofgroup != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofproductgroups ^ (idofproductgroups >>> 32));
        result = 31 * result + (nameofgroup != null ? nameofgroup.hashCode() : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (classificationcode != null ? classificationcode.hashCode() : 0);
        result = 31 * result + (int) (idofconfigurationprovider ^ (idofconfigurationprovider >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfDishProhibitions> cfDishProhibitionsesByIdofproductgroups;

    public Collection<CfDishProhibitions> getCfDishProhibitionsesByIdofproductgroups() {
        return cfDishProhibitionsesByIdofproductgroups;
    }

    public void setCfDishProhibitionsesByIdofproductgroups(
            Collection<CfDishProhibitions> cfDishProhibitionsesByIdofproductgroups) {
        this.cfDishProhibitionsesByIdofproductgroups = cfDishProhibitionsesByIdofproductgroups;
    }

    private Collection<CfProducts> cfProductsesByIdofproductgroups;

    public Collection<CfProducts> getCfProductsesByIdofproductgroups() {
        return cfProductsesByIdofproductgroups;
    }

    public void setCfProductsesByIdofproductgroups(Collection<CfProducts> cfProductsesByIdofproductgroups) {
        this.cfProductsesByIdofproductgroups = cfProductsesByIdofproductgroups;
    }
}
