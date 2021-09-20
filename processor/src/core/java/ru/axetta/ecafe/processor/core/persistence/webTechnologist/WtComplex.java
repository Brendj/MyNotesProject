/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @ManyToOne
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

    @Column(name = "deleteState")
    private Integer deleteState;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfComplexGroupItem")
    private WtComplexGroupItem wtComplexGroupItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfAgeGroupItem")
    private WtAgeGroupItem wtAgeGroupItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfDietType")
    private WtDietType wtDietType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfOrgGroup")
    private WtOrgGroup wtOrgGroup;

    @Column(name = "composite")
    private Boolean composite;

    @Column(name = "is_portal")
    private Boolean isPortal;

    @Column(name = "start_cycle_day")
    private Integer startCycleDay;

    private Long idOfParentComplex;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cf_wt_complexes_org",
            joinColumns = @JoinColumn(name = "IdOfComplex"),
            inverseJoinColumns = @JoinColumn(name = "IdOfOrg"))
    private Set<Org> orgs = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_discountrules_complexes",
            joinColumns = @JoinColumn(name = "idOfComplex"),
            inverseJoinColumns = @JoinColumn(name = "idOfRule"))
    private Set<WtDiscountRule> discountRules = new HashSet<>();

    @OneToMany(mappedBy = "wtComplex")
    private Set<WtComplexesItem> wtComplexesItems = new HashSet<>();

    @Column(name = "idOfOrgGroup", insertable = false, updatable = false)
    private Long idOfOrgGroup;

    public WtComplex(WtComplex complex) {
        this.idOfComplex = complex.idOfComplex;
        this.name = complex.name;
        this.price = complex.price;
        this.beginDate = complex.beginDate;
        this.endDate = complex.endDate;
        this.cycleMotion = complex.cycleMotion;
        this.dayInCycle = complex.dayInCycle;
        this.version = complex.version;
        this.guid = complex.guid;
        this.createDate = complex.createDate;
        this.lastUpdate = complex.lastUpdate;
        this.createdUser = complex.createdUser;
        this.updatedUser = complex.updatedUser;
        this.deleteState = complex.deleteState;
        this.wtComplexGroupItem = complex.wtComplexGroupItem;
        this.wtAgeGroupItem = complex.wtAgeGroupItem;
        this.wtDietType = complex.wtDietType;
        this.contragent = complex.contragent;
        this.wtOrgGroup = complex.wtOrgGroup;
        this.composite = complex.composite;
        this.isPortal = complex.isPortal;
        this.startCycleDay = complex.startCycleDay;
        this.orgs = complex.orgs;
        this.discountRules = complex.discountRules;
        this.wtComplexesItems = complex.wtComplexesItems;
        this.comment = complex.comment;
        this.idOfOrgGroup = complex.idOfOrgGroup;
    }

    public WtComplex() {
    }

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

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
    }

    public WtDietType getWtDietType() {
        return wtDietType;
    }

    public void setWtDietType(WtDietType wtDietType) {
        this.wtDietType = wtDietType;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public WtOrgGroup getWtOrgGroup() {
        return wtOrgGroup;
    }

    public void setWtOrgGroup(WtOrgGroup wtOrgGroup) {
        this.wtOrgGroup = wtOrgGroup;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }

    public Set<WtComplexesItem> getWtComplexesItems() {
        return wtComplexesItems;
    }

    public void setWtComplexesItems(Set<WtComplexesItem> wtComplexesItems) {
        this.wtComplexesItems = wtComplexesItems;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Set<WtDiscountRule> getDiscountRules() {
        return discountRules;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDiscountRules(Set<WtDiscountRule> discountRules) {
        this.discountRules = discountRules;
    }

    public Long getIdOfOrgGroup() {
        return idOfOrgGroup;
    }

    public void setIdOfOrgGroup(Long idOfOrgGroup) {
        this.idOfOrgGroup = idOfOrgGroup;
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
        return idOfComplex.equals(that.getIdOfComplex());
    }

    @Override
    public int hashCode() {
        return idOfComplex.hashCode();
    }

    public Long getIdOfParentComplex() {
        return idOfParentComplex;
    }

    public void setIdOfParentComplex(Long idOfParentComplex) {
        this.idOfParentComplex = idOfParentComplex;
    }
}
