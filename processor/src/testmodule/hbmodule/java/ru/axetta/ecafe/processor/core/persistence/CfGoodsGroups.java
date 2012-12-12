package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfGoodsGroups {

    private long idofgoodsgroup;

    public long getIdofgoodsgroup() {
        return idofgoodsgroup;
    }

    public void setIdofgoodsgroup(long idofgoodsgroup) {
        this.idofgoodsgroup = idofgoodsgroup;
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

    private String nameofgoodsgroup;

    public String getNameofgoodsgroup() {
        return nameofgoodsgroup;
    }

    public void setNameofgoodsgroup(String nameofgoodsgroup) {
        this.nameofgoodsgroup = nameofgoodsgroup;
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

        CfGoodsGroups that = (CfGoodsGroups) o;

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
        if (idofgoodsgroup != that.idofgoodsgroup) {
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
        if (nameofgoodsgroup != null ? !nameofgoodsgroup.equals(that.nameofgoodsgroup)
                : that.nameofgoodsgroup != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofgoodsgroup ^ (idofgoodsgroup >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (nameofgoodsgroup != null ? nameofgoodsgroup.hashCode() : 0);
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfDishProhibitionExclusions> cfDishProhibitionExclusionsesByIdofgoodsgroup;

    public Collection<CfDishProhibitionExclusions> getCfDishProhibitionExclusionsesByIdofgoodsgroup() {
        return cfDishProhibitionExclusionsesByIdofgoodsgroup;
    }

    public void setCfDishProhibitionExclusionsesByIdofgoodsgroup(
            Collection<CfDishProhibitionExclusions> cfDishProhibitionExclusionsesByIdofgoodsgroup) {
        this.cfDishProhibitionExclusionsesByIdofgoodsgroup = cfDishProhibitionExclusionsesByIdofgoodsgroup;
    }

    private Collection<CfDishProhibitions> cfDishProhibitionsesByIdofgoodsgroup;

    public Collection<CfDishProhibitions> getCfDishProhibitionsesByIdofgoodsgroup() {
        return cfDishProhibitionsesByIdofgoodsgroup;
    }

    public void setCfDishProhibitionsesByIdofgoodsgroup(
            Collection<CfDishProhibitions> cfDishProhibitionsesByIdofgoodsgroup) {
        this.cfDishProhibitionsesByIdofgoodsgroup = cfDishProhibitionsesByIdofgoodsgroup;
    }

    private Collection<CfGoods> cfGoodsesByIdofgoodsgroup;

    public Collection<CfGoods> getCfGoodsesByIdofgoodsgroup() {
        return cfGoodsesByIdofgoodsgroup;
    }

    public void setCfGoodsesByIdofgoodsgroup(Collection<CfGoods> cfGoodsesByIdofgoodsgroup) {
        this.cfGoodsesByIdofgoodsgroup = cfGoodsesByIdofgoodsgroup;
    }
}
