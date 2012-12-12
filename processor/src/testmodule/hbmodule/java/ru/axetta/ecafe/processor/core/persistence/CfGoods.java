package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfGoods {

    private long idofgood;

    public long getIdofgood() {
        return idofgood;
    }

    public void setIdofgood(long idofgood) {
        this.idofgood = idofgood;
    }

    private long idofgoodsgroup;

    public long getIdofgoodsgroup() {
        return idofgoodsgroup;
    }

    public void setIdofgoodsgroup(long idofgoodsgroup) {
        this.idofgoodsgroup = idofgoodsgroup;
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

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    private boolean deletedstate;

    public boolean isDeletedstate() {
        return deletedstate;
    }

    public void setDeletedstate(boolean deletedstate) {
        this.deletedstate = deletedstate;
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

    private String nameofgood;

    public String getNameofgood() {
        return nameofgood;
    }

    public void setNameofgood(String nameofgood) {
        this.nameofgood = nameofgood;
    }

    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    private String goodscode;

    public String getGoodscode() {
        return goodscode;
    }

    public void setGoodscode(String goodscode) {
        this.goodscode = goodscode;
    }

    private int unitsscale;

    public int getUnitsscale() {
        return unitsscale;
    }

    public void setUnitsscale(int unitsscale) {
        this.unitsscale = unitsscale;
    }

    private long netweight;

    public long getNetweight() {
        return netweight;
    }

    public void setNetweight(long netweight) {
        this.netweight = netweight;
    }

    private long lifetime;

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    private long margin;

    public long getMargin() {
        return margin;
    }

    public void setMargin(long margin) {
        this.margin = margin;
    }

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfGoods cfGoods = (CfGoods) o;

        if (createddate != cfGoods.createddate) {
            return false;
        }
        if (deletedate != cfGoods.deletedate) {
            return false;
        }
        if (deletedstate != cfGoods.deletedstate) {
            return false;
        }
        if (globalversion != cfGoods.globalversion) {
            return false;
        }
        if (idofgood != cfGoods.idofgood) {
            return false;
        }
        if (idofgoodsgroup != cfGoods.idofgoodsgroup) {
            return false;
        }
        if (idofproducts != cfGoods.idofproducts) {
            return false;
        }
        if (idoftechnologicalmaps != cfGoods.idoftechnologicalmaps) {
            return false;
        }
        if (idofusercreate != cfGoods.idofusercreate) {
            return false;
        }
        if (idofuserdelete != cfGoods.idofuserdelete) {
            return false;
        }
        if (idofuseredit != cfGoods.idofuseredit) {
            return false;
        }
        if (lastupdate != cfGoods.lastupdate) {
            return false;
        }
        if (lifetime != cfGoods.lifetime) {
            return false;
        }
        if (margin != cfGoods.margin) {
            return false;
        }
        if (netweight != cfGoods.netweight) {
            return false;
        }
        if (orgowner != cfGoods.orgowner) {
            return false;
        }
        if (sendall != cfGoods.sendall) {
            return false;
        }
        if (unitsscale != cfGoods.unitsscale) {
            return false;
        }
        if (fullname != null ? !fullname.equals(cfGoods.fullname) : cfGoods.fullname != null) {
            return false;
        }
        if (goodscode != null ? !goodscode.equals(cfGoods.goodscode) : cfGoods.goodscode != null) {
            return false;
        }
        if (guid != null ? !guid.equals(cfGoods.guid) : cfGoods.guid != null) {
            return false;
        }
        if (nameofgood != null ? !nameofgood.equals(cfGoods.nameofgood) : cfGoods.nameofgood != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofgood ^ (idofgood >>> 32));
        result = 31 * result + (int) (idofgoodsgroup ^ (idofgoodsgroup >>> 32));
        result = 31 * result + (int) (idoftechnologicalmaps ^ (idoftechnologicalmaps >>> 32));
        result = 31 * result + (int) (idofproducts ^ (idofproducts >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (nameofgood != null ? nameofgood.hashCode() : 0);
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        result = 31 * result + (goodscode != null ? goodscode.hashCode() : 0);
        result = 31 * result + unitsscale;
        result = 31 * result + (int) (netweight ^ (netweight >>> 32));
        result = 31 * result + (int) (lifetime ^ (lifetime >>> 32));
        result = 31 * result + (int) (margin ^ (margin >>> 32));
        result = 31 * result + sendall;
        result = 31 * result + (int) (idofusercreate ^ (idofusercreate >>> 32));
        result = 31 * result + (int) (idofuseredit ^ (idofuseredit >>> 32));
        result = 31 * result + (int) (idofuserdelete ^ (idofuserdelete >>> 32));
        return result;
    }

    private Collection<CfActsOfWaybillDifferencePositions> cfActsOfWaybillDifferencePositionsesByIdofgood;

    public Collection<CfActsOfWaybillDifferencePositions> getCfActsOfWaybillDifferencePositionsesByIdofgood() {
        return cfActsOfWaybillDifferencePositionsesByIdofgood;
    }

    public void setCfActsOfWaybillDifferencePositionsesByIdofgood(
            Collection<CfActsOfWaybillDifferencePositions> cfActsOfWaybillDifferencePositionsesByIdofgood) {
        this.cfActsOfWaybillDifferencePositionsesByIdofgood = cfActsOfWaybillDifferencePositionsesByIdofgood;
    }

    private Collection<CfComplexinfo> cfComplexinfosByIdofgood;

    public Collection<CfComplexinfo> getCfComplexinfosByIdofgood() {
        return cfComplexinfosByIdofgood;
    }

    public void setCfComplexinfosByIdofgood(Collection<CfComplexinfo> cfComplexinfosByIdofgood) {
        this.cfComplexinfosByIdofgood = cfComplexinfosByIdofgood;
    }

    private Collection<CfDishProhibitionExclusions> cfDishProhibitionExclusionsesByIdofgood;

    public Collection<CfDishProhibitionExclusions> getCfDishProhibitionExclusionsesByIdofgood() {
        return cfDishProhibitionExclusionsesByIdofgood;
    }

    public void setCfDishProhibitionExclusionsesByIdofgood(
            Collection<CfDishProhibitionExclusions> cfDishProhibitionExclusionsesByIdofgood) {
        this.cfDishProhibitionExclusionsesByIdofgood = cfDishProhibitionExclusionsesByIdofgood;
    }

    private Collection<CfDishProhibitions> cfDishProhibitionsesByIdofgood;

    public Collection<CfDishProhibitions> getCfDishProhibitionsesByIdofgood() {
        return cfDishProhibitionsesByIdofgood;
    }

    public void setCfDishProhibitionsesByIdofgood(Collection<CfDishProhibitions> cfDishProhibitionsesByIdofgood) {
        this.cfDishProhibitionsesByIdofgood = cfDishProhibitionsesByIdofgood;
    }

    private CfGoodsGroups cfGoodsGroupsByIdofgoodsgroup;

    public CfGoodsGroups getCfGoodsGroupsByIdofgoodsgroup() {
        return cfGoodsGroupsByIdofgoodsgroup;
    }

    public void setCfGoodsGroupsByIdofgoodsgroup(CfGoodsGroups cfGoodsGroupsByIdofgoodsgroup) {
        this.cfGoodsGroupsByIdofgoodsgroup = cfGoodsGroupsByIdofgoodsgroup;
    }

    private Collection<CfInternalDisposingDocumentPositions> cfInternalDisposingDocumentPositionsesByIdofgood;

    public Collection<CfInternalDisposingDocumentPositions> getCfInternalDisposingDocumentPositionsesByIdofgood() {
        return cfInternalDisposingDocumentPositionsesByIdofgood;
    }

    public void setCfInternalDisposingDocumentPositionsesByIdofgood(
            Collection<CfInternalDisposingDocumentPositions> cfInternalDisposingDocumentPositionsesByIdofgood) {
        this.cfInternalDisposingDocumentPositionsesByIdofgood = cfInternalDisposingDocumentPositionsesByIdofgood;
    }

    private Collection<CfInternalIncomingDocumentPositions> cfInternalIncomingDocumentPositionsesByIdofgood;

    public Collection<CfInternalIncomingDocumentPositions> getCfInternalIncomingDocumentPositionsesByIdofgood() {
        return cfInternalIncomingDocumentPositionsesByIdofgood;
    }

    public void setCfInternalIncomingDocumentPositionsesByIdofgood(
            Collection<CfInternalIncomingDocumentPositions> cfInternalIncomingDocumentPositionsesByIdofgood) {
        this.cfInternalIncomingDocumentPositionsesByIdofgood = cfInternalIncomingDocumentPositionsesByIdofgood;
    }

    private Collection<CfMenudetails> cfMenudetailsesByIdofgood;

    public Collection<CfMenudetails> getCfMenudetailsesByIdofgood() {
        return cfMenudetailsesByIdofgood;
    }

    public void setCfMenudetailsesByIdofgood(Collection<CfMenudetails> cfMenudetailsesByIdofgood) {
        this.cfMenudetailsesByIdofgood = cfMenudetailsesByIdofgood;
    }

    private Collection<CfTradeMaterialGoods> cfTradeMaterialGoodsesByIdofgood;

    public Collection<CfTradeMaterialGoods> getCfTradeMaterialGoodsesByIdofgood() {
        return cfTradeMaterialGoodsesByIdofgood;
    }

    public void setCfTradeMaterialGoodsesByIdofgood(Collection<CfTradeMaterialGoods> cfTradeMaterialGoodsesByIdofgood) {
        this.cfTradeMaterialGoodsesByIdofgood = cfTradeMaterialGoodsesByIdofgood;
    }

    private Collection<CfWaybillsPositions> cfWaybillsPositionsesByIdofgood;

    public Collection<CfWaybillsPositions> getCfWaybillsPositionsesByIdofgood() {
        return cfWaybillsPositionsesByIdofgood;
    }

    public void setCfWaybillsPositionsesByIdofgood(Collection<CfWaybillsPositions> cfWaybillsPositionsesByIdofgood) {
        this.cfWaybillsPositionsesByIdofgood = cfWaybillsPositionsesByIdofgood;
    }
}
