/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;
import ru.axetta.ecafe.processor.core.persistence.Org;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "cf_wt_complexes")
public class WtComplex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfComplex")
    private Long idOfComplex;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "beginDate")
    private Date beginDate;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "cycleMotion")
    private Integer cycleMotion;

    @Column(name = "dayInCycle")
    private Integer dayInCycle;

    @Column(name = "version")
    private Long version;

    @Column(name = "guid")
    private String guid;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    @Column(name = "deleteState")
    private Integer deleteState;

    @Column(name = "composite")
    private Boolean composite;

    @Column(name = "is_portal")
    private Boolean isPortal;

    @Column(name = "start_cycle_day")
    private Integer startCycleDay;

    @ManyToOne
    @JoinColumn(name = "idOfComplexGroupItem")
    private WtComplexGroupItem wtComplexGroupItem;

    @ManyToOne
    @JoinColumn(name = "idOfAgeGroupItem")
    private WtAgeGroupItem wtAgeGroupItem;

    @ManyToMany
    @JoinTable(name = "cf_wt_complexes_org",
            joinColumns = @JoinColumn(name = "IdOfComplex"),
            inverseJoinColumns = @JoinColumn(name = "IdOfOrg"))
    private Set<Org> orgs = new HashSet<>();

    @OneToMany(mappedBy = "complex")
    private List<MenuSupplier> menuSupplierList;

    public Boolean getIsPortal() {
        return isPortal;
    }

    public void setIsPortal(Boolean isPortal) {
        this.isPortal = isPortal;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getCycleMotion() {
        return cycleMotion;
    }

    public void setCycleMotion(Integer cycleMotion) {
        this.cycleMotion = cycleMotion;
    }

    public Integer getDayInCycle() {
        return dayInCycle;
    }

    public void setDayInCycle(Integer dayInCycle) {
        this.dayInCycle = dayInCycle;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public Boolean getComposite() {
        return composite;
    }

    public void setComposite(Boolean composite) {
        this.composite = composite;
    }

    public Boolean getPortal() {
        return isPortal;
    }

    public void setPortal(Boolean portal) {
        isPortal = portal;
    }

    public Integer getStartCycleDay() {
        return startCycleDay;
    }

    public void setStartCycleDay(Integer startCycleDay) {
        this.startCycleDay = startCycleDay;
    }

    public WtComplexGroupItem getWtComplexGroupItem() {
        return wtComplexGroupItem;
    }

    public void setWtComplexGroupItem(
            WtComplexGroupItem wtComplexGroupItemByIdofcomplexgroupitem) {
        this.wtComplexGroupItem = wtComplexGroupItemByIdofcomplexgroupitem;
    }

    public WtAgeGroupItem getWtAgeGroupItem() {
        return wtAgeGroupItem;
    }

    public void setWtAgeGroupItem(WtAgeGroupItem wtAgeGroupItem) {
        this.wtAgeGroupItem = wtAgeGroupItem;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }

    public List<MenuSupplier> getMenuSupplierList() {
        return menuSupplierList;
    }

    public void setMenuSupplierList(List<MenuSupplier> menuSupplierList) {
        this.menuSupplierList = menuSupplierList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplex that = (WtComplex) o;
        return Objects.equals(idOfComplex, that.idOfComplex) && Objects.equals(name, that.name) && Objects
                .equals(price, that.price) && Objects.equals(beginDate, that.beginDate) && Objects
                .equals(endDate, that.endDate) && Objects.equals(cycleMotion, that.cycleMotion) && Objects
                .equals(dayInCycle, that.dayInCycle) && Objects.equals(version, that.version) && Objects
                .equals(guid, that.guid) && Objects.equals(createDate, that.createDate) && Objects
                .equals(lastUpdate, that.lastUpdate) && Objects.equals(deleteState, that.deleteState) && Objects
                .equals(composite, that.composite) && Objects.equals(startCycleDay, that.startCycleDay);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(idOfComplex, name, price, beginDate, endDate, cycleMotion, dayInCycle, version, guid, createDate,
                        lastUpdate, deleteState, composite, startCycleDay);
    }
}
