package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfProducts {

    private long idofproducts;

    public long getIdofproducts() {
        return idofproducts;
    }

    public void setIdofproducts(long idofproducts) {
        this.idofproducts = idofproducts;
    }

    private long idofproductgroups;

    public long getIdofproductgroups() {
        return idofproductgroups;
    }

    public void setIdofproductgroups(long idofproductgroups) {
        this.idofproductgroups = idofproductgroups;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    private String productname;

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    private String okpcode;

    public String getOkpcode() {
        return okpcode;
    }

    public void setOkpcode(String okpcode) {
        this.okpcode = okpcode;
    }

    private String classificationcode;

    public String getClassificationcode() {
        return classificationcode;
    }

    public void setClassificationcode(String classificationcode) {
        this.classificationcode = classificationcode;
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

    private long idofusercreate;

    public long getIdofusercreate() {
        return idofusercreate;
    }

    public void setIdofusercreate(long idofusercreate) {
        this.idofusercreate = idofusercreate;
    }

    private long idofuseredit;

    public long getIdofuseredit() {
        return idofuseredit;
    }

    public void setIdofuseredit(long idofuseredit) {
        this.idofuseredit = idofuseredit;
    }

    private long idofuserdelete;

    public long getIdofuserdelete() {
        return idofuserdelete;
    }

    public void setIdofuserdelete(long idofuserdelete) {
        this.idofuserdelete = idofuserdelete;
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

    private double density;

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfProducts that = (CfProducts) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (deletedate != that.deletedate) {
            return false;
        }
        if (deletedstate != that.deletedstate) {
            return false;
        }
        if (Double.compare(that.density, density) != 0) {
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
        if (idofproducts != that.idofproducts) {
            return false;
        }
        if (idofusercreate != that.idofusercreate) {
            return false;
        }
        if (idofuserdelete != that.idofuserdelete) {
            return false;
        }
        if (idofuseredit != that.idofuseredit) {
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
        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }
        if (fullname != null ? !fullname.equals(that.fullname) : that.fullname != null) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (okpcode != null ? !okpcode.equals(that.okpcode) : that.okpcode != null) {
            return false;
        }
        if (productname != null ? !productname.equals(that.productname) : that.productname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (idofproducts ^ (idofproducts >>> 32));
        result = 31 * result + (int) (idofproductgroups ^ (idofproductgroups >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        result = 31 * result + (productname != null ? productname.hashCode() : 0);
        result = 31 * result + (okpcode != null ? okpcode.hashCode() : 0);
        result = 31 * result + (classificationcode != null ? classificationcode.hashCode() : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (idofusercreate ^ (idofusercreate >>> 32));
        result = 31 * result + (int) (idofuseredit ^ (idofuseredit >>> 32));
        result = 31 * result + (int) (idofuserdelete ^ (idofuserdelete >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (idofconfigurationprovider ^ (idofconfigurationprovider >>> 32));
        result = 31 * result + sendall;
        temp = density != +0.0d ? Double.doubleToLongBits(density) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    private Collection<CfDishProhibitions> cfDishProhibitionsesByIdofproducts;

    public Collection<CfDishProhibitions> getCfDishProhibitionsesByIdofproducts() {
        return cfDishProhibitionsesByIdofproducts;
    }

    public void setCfDishProhibitionsesByIdofproducts(
            Collection<CfDishProhibitions> cfDishProhibitionsesByIdofproducts) {
        this.cfDishProhibitionsesByIdofproducts = cfDishProhibitionsesByIdofproducts;
    }

    private CfProductGroups cfProductGroupsByIdofproductgroups;

    public CfProductGroups getCfProductGroupsByIdofproductgroups() {
        return cfProductGroupsByIdofproductgroups;
    }

    public void setCfProductGroupsByIdofproductgroups(CfProductGroups cfProductGroupsByIdofproductgroups) {
        this.cfProductGroupsByIdofproductgroups = cfProductGroupsByIdofproductgroups;
    }

    private Collection<CfTechnologicalMapProducts> cfTechnologicalMapProductsesByIdofproducts;

    public Collection<CfTechnologicalMapProducts> getCfTechnologicalMapProductsesByIdofproducts() {
        return cfTechnologicalMapProductsesByIdofproducts;
    }

    public void setCfTechnologicalMapProductsesByIdofproducts(
            Collection<CfTechnologicalMapProducts> cfTechnologicalMapProductsesByIdofproducts) {
        this.cfTechnologicalMapProductsesByIdofproducts = cfTechnologicalMapProductsesByIdofproducts;
    }
}
