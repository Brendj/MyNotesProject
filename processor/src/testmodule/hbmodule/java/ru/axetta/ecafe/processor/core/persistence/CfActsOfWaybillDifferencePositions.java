package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfActsOfWaybillDifferencePositions {

    private long idofactofdifferenceposition;

    public long getIdofactofdifferenceposition() {
        return idofactofdifferenceposition;
    }

    public void setIdofactofdifferenceposition(long idofactofdifferenceposition) {
        this.idofactofdifferenceposition = idofactofdifferenceposition;
    }

    private long idofactofdifference;

    public long getIdofactofdifference() {
        return idofactofdifference;
    }

    public void setIdofactofdifference(long idofactofdifference) {
        this.idofactofdifference = idofactofdifference;
    }

    private long idofgood;

    public long getIdofgood() {
        return idofgood;
    }

    public void setIdofgood(long idofgood) {
        this.idofgood = idofgood;
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

    private int unitsscale;

    public int getUnitsscale() {
        return unitsscale;
    }

    public void setUnitsscale(int unitsscale) {
        this.unitsscale = unitsscale;
    }

    private long totalcount;

    public long getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(long totalcount) {
        this.totalcount = totalcount;
    }

    private long netweight;

    public long getNetweight() {
        return netweight;
    }

    public void setNetweight(long netweight) {
        this.netweight = netweight;
    }

    private long grossweight;

    public long getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(long grossweight) {
        this.grossweight = grossweight;
    }

    private long goodscreationdate;

    public long getGoodscreationdate() {
        return goodscreationdate;
    }

    public void setGoodscreationdate(long goodscreationdate) {
        this.goodscreationdate = goodscreationdate;
    }

    private long lifetime;

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    private long price;

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    private long nds;

    public long getNds() {
        return nds;
    }

    public void setNds(long nds) {
        this.nds = nds;
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

        CfActsOfWaybillDifferencePositions that = (CfActsOfWaybillDifferencePositions) o;

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
        if (goodscreationdate != that.goodscreationdate) {
            return false;
        }
        if (grossweight != that.grossweight) {
            return false;
        }
        if (idofactofdifference != that.idofactofdifference) {
            return false;
        }
        if (idofactofdifferenceposition != that.idofactofdifferenceposition) {
            return false;
        }
        if (idofgood != that.idofgood) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (lifetime != that.lifetime) {
            return false;
        }
        if (nds != that.nds) {
            return false;
        }
        if (netweight != that.netweight) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (price != that.price) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (totalcount != that.totalcount) {
            return false;
        }
        if (unitsscale != that.unitsscale) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofactofdifferenceposition ^ (idofactofdifferenceposition >>> 32));
        result = 31 * result + (int) (idofactofdifference ^ (idofactofdifference >>> 32));
        result = 31 * result + (int) (idofgood ^ (idofgood >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + unitsscale;
        result = 31 * result + (int) (totalcount ^ (totalcount >>> 32));
        result = 31 * result + (int) (netweight ^ (netweight >>> 32));
        result = 31 * result + (int) (grossweight ^ (grossweight >>> 32));
        result = 31 * result + (int) (goodscreationdate ^ (goodscreationdate >>> 32));
        result = 31 * result + (int) (lifetime ^ (lifetime >>> 32));
        result = 31 * result + (int) (price ^ (price >>> 32));
        result = 31 * result + (int) (nds ^ (nds >>> 32));
        result = 31 * result + sendall;
        return result;
    }

    private CfActsOfWaybillDifference cfActsOfWaybillDifferenceByIdofactofdifference;

    public CfActsOfWaybillDifference getCfActsOfWaybillDifferenceByIdofactofdifference() {
        return cfActsOfWaybillDifferenceByIdofactofdifference;
    }

    public void setCfActsOfWaybillDifferenceByIdofactofdifference(
            CfActsOfWaybillDifference cfActsOfWaybillDifferenceByIdofactofdifference) {
        this.cfActsOfWaybillDifferenceByIdofactofdifference = cfActsOfWaybillDifferenceByIdofactofdifference;
    }

    private CfGoods cfGoodsByIdofgood;

    public CfGoods getCfGoodsByIdofgood() {
        return cfGoodsByIdofgood;
    }

    public void setCfGoodsByIdofgood(CfGoods cfGoodsByIdofgood) {
        this.cfGoodsByIdofgood = cfGoodsByIdofgood;
    }
}
