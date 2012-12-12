package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDishProhibitions {

    private long idofprohibition;

    public long getIdofprohibition() {
        return idofprohibition;
    }

    public void setIdofprohibition(long idofprohibition) {
        this.idofprohibition = idofprohibition;
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

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDishProhibitions that = (CfDishProhibitions) o;

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
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idofgood != that.idofgood) {
            return false;
        }
        if (idofgoodsgroup != that.idofgoodsgroup) {
            return false;
        }
        if (idofproductgroups != that.idofproductgroups) {
            return false;
        }
        if (idofproducts != that.idofproducts) {
            return false;
        }
        if (idofprohibition != that.idofprohibition) {
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofprohibition ^ (idofprohibition >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + sendall;
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idofproducts ^ (idofproducts >>> 32));
        result = 31 * result + (int) (idofproductgroups ^ (idofproductgroups >>> 32));
        result = 31 * result + (int) (idofgood ^ (idofgood >>> 32));
        result = 31 * result + (int) (idofgoodsgroup ^ (idofgoodsgroup >>> 32));
        return result;
    }

    private Collection<CfDishProhibitionExclusions> cfDishProhibitionExclusionsesByIdofprohibition;

    public Collection<CfDishProhibitionExclusions> getCfDishProhibitionExclusionsesByIdofprohibition() {
        return cfDishProhibitionExclusionsesByIdofprohibition;
    }

    public void setCfDishProhibitionExclusionsesByIdofprohibition(
            Collection<CfDishProhibitionExclusions> cfDishProhibitionExclusionsesByIdofprohibition) {
        this.cfDishProhibitionExclusionsesByIdofprohibition = cfDishProhibitionExclusionsesByIdofprohibition;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }

    private CfGoods cfGoodsByIdofgood;

    public CfGoods getCfGoodsByIdofgood() {
        return cfGoodsByIdofgood;
    }

    public void setCfGoodsByIdofgood(CfGoods cfGoodsByIdofgood) {
        this.cfGoodsByIdofgood = cfGoodsByIdofgood;
    }

    private CfGoodsGroups cfGoodsGroupsByIdofgoodsgroup;

    public CfGoodsGroups getCfGoodsGroupsByIdofgoodsgroup() {
        return cfGoodsGroupsByIdofgoodsgroup;
    }

    public void setCfGoodsGroupsByIdofgoodsgroup(CfGoodsGroups cfGoodsGroupsByIdofgoodsgroup) {
        this.cfGoodsGroupsByIdofgoodsgroup = cfGoodsGroupsByIdofgoodsgroup;
    }

    private CfProductGroups cfProductGroupsByIdofproductgroups;

    public CfProductGroups getCfProductGroupsByIdofproductgroups() {
        return cfProductGroupsByIdofproductgroups;
    }

    public void setCfProductGroupsByIdofproductgroups(CfProductGroups cfProductGroupsByIdofproductgroups) {
        this.cfProductGroupsByIdofproductgroups = cfProductGroupsByIdofproductgroups;
    }

    private CfProducts cfProductsByIdofproducts;

    public CfProducts getCfProductsByIdofproducts() {
        return cfProductsByIdofproducts;
    }

    public void setCfProductsByIdofproducts(CfProducts cfProductsByIdofproducts) {
        this.cfProductsByIdofproducts = cfProductsByIdofproducts;
    }
}
