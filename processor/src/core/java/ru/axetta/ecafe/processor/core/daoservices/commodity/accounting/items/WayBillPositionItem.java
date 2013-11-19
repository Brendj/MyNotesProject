package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.items;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBill;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.11.13
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class WayBillPositionItem {

    private Long totalCount;
    private Long netWeight;
    private Long grossWeight;
    private Date goodsCreationDate;
    private Long lifeTime;
    private Long price;
    private Long nds;
    private Good good;
    private Long orgOwner;
    private String orgOwnerShortName;
    //private Long idOfGood;
    //private String goodName;
    private UnitScale unitsScale;
    private Boolean deletedState;

    public String getOrgOwnerShortName() {
        return orgOwnerShortName;
    }

    public void setOrgOwnerShortName(String orgOwnerShortName) {
        this.orgOwnerShortName = orgOwnerShortName;
    }

    //public Long getIdOfGood() {
    //    return idOfGood;
    //}
    //
    //public void setIdOfGood(Long idOfGood) {
    //    this.idOfGood = idOfGood;
    //}
    //
    //public String getGoodName() {
    //    return goodName;
    //}
    //
    //public void setGoodName(String goodName) {
    //    this.goodName = goodName;
    //}

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public Long getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Long grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Date getGoodsCreationDate() {
        return goodsCreationDate;
    }

    public void setGoodsCreationDate(Date goodsCreationDate) {
        this.goodsCreationDate = goodsCreationDate;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    //public String getGuidOfG() {
    //    return guidOfG;
    //}
    //
    //public void setGuidOfG(String guidOfG) {
    //    this.guidOfG = guidOfG;
    //}
    //
    //public WayBill getWayBill() {
    //    return wayBill;
    //}
    //
    //public void setWayBill(WayBill wayBill) {
    //    this.wayBill = wayBill;
    //}
    //
    //public String getGuidOfWB() {
    //    return guidOfWB;
    //}
    //
    //public void setGuidOfWB(String guidOfWB) {
    //    this.guidOfWB = guidOfWB;
    //}
}
