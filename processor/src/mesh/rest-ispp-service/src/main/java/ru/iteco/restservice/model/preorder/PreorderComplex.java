package ru.iteco.restservice.model.preorder;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.enums.PreorderState;
import ru.iteco.restservice.model.wt.WtComplex;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cf_preorder_complex")
public class PreorderComplex {
    public static final Integer COMPLEX_TYPE2 = 2;
    public static final Integer COMPLEX_TYPE4 = 4;

    @Id
    @GeneratedValue(generator = "cf_preorder_complex-seq")
    @GenericGenerator(
            name = "cf_preorder_complex-seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "cf_preorder_complex_Id_Gen_seq"),
                    @Parameter(name = "optimizer", value = "pooled-lo"),
                    @Parameter(name = "increment_size", value = "8")
            }
    )
    @Column(name = "idOfPreorderComplex")
    private Long idOfPreorderComplex;

    @Column(name = "armComplexId")
    private Integer armComplexId;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "preorderDate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date preorderDate;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "version")
    private Long version;

    @Column(name = "deletedState")
    private Integer deletedState;

    @Column(name = "guid")
    private String guid;

    @Column(name = "usedSum")
    private Long usedSum;

    @Column(name = "usedAmount")
    private Long usedAmount;

    @OneToMany
    @JoinColumn(name = "idofpreordercomplex")
    private Set<PreorderMenuDetail> preorderMenuDetails;

    @Column(name = "complexName")
    private String complexName;

    @Column(name = "complexPrice")
    private Long complexPrice;

    @Column(name = "createdDate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date createdDate;

    @Column(name = "lastUpdate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date lastUpdate;

    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    private PreorderState state;

    @Column(name = "idOfGoodsRequestPosition")
    private Long idOfGoodsRequestPosition;

    @ManyToOne
    @JoinColumn(name = "idofregularpreorder")
    private RegularPreorder regularPreorder;

    @Column(name = "modeOfAdd")
    private Integer modeOfAdd;

    @Column(name = "modeFree")
    private Integer modeFree;

    @Column(name = "idOfOrgOnCreate")
    private Long idOfOrgOnCreate;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "mobileGroupOnCreate")
    @Enumerated(EnumType.ORDINAL)
    private PreorderMobileGroupOnCreateType mobileGroupOnCreate;

    @Column(name = "cancelnotification")
    private Integer cancelnotification;

    public PreorderComplex() { }

    public PreorderComplex(Client client, Date date, WtComplex wtComplex, Integer amount, Long version,
                           String guardianMobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.client = client;
        this.preorderDate = date;
        this.armComplexId = wtComplex.getIdOfComplex().intValue();
        this.amount = amount;
        this.version = version;
        this.deletedState = 0;
        this.guid = UUID.randomUUID().toString();
        this.usedSum = 0L;
        this.usedAmount = 0L;
        this.createdDate = new Date();
        this.lastUpdate = new Date();
        this.state = PreorderState.OK;
        this.idOfOrgOnCreate = client.getOrg().getIdOfOrg();
        this.mobile = guardianMobile;
        this.mobileGroupOnCreate = mobileGroupOnCreate;
        this.complexName = wtComplex.getName();
        this.complexPrice = wtComplex.getPrice().longValue();
        this.modeFree = 0;
        this.modeOfAdd = wtComplex.getComposite() ? COMPLEX_TYPE4 : COMPLEX_TYPE2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreorderComplex that = (PreorderComplex) o;
        return idOfPreorderComplex.equals(that.getIdOfPreorderComplex());
    }

    @Override
    public int hashCode() {
        return idOfPreorderComplex.hashCode();
    }

    public static int getDaysOfRegularPreorders() {
        //return Integer.parseInt(RuntimeContext
        //        .getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.daysToGenerate", "14"));
        return 14;
    }

    public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
        this.idOfPreorderComplex = idOfPreorderComplex;
    }

    public Integer getArmComplexId() {
        return armComplexId;
    }

    public void setArmComplexId(Integer armComplexId) {
        this.armComplexId = armComplexId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Integer deletedState) {
        this.deletedState = deletedState;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getUsedSum() {
        return usedSum;
    }

    public void setUsedSum(Long usedSum) {
        this.usedSum = usedSum;
    }

    public Long getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Long usedAmount) {
        this.usedAmount = usedAmount;
    }

    public Set<PreorderMenuDetail> getPreorderMenuDetails() {
        return preorderMenuDetails;
    }

    public void setPreorderMenuDetails(Set<PreorderMenuDetail> preorderMenuDetails) {
        this.preorderMenuDetails = preorderMenuDetails;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getComplexPrice() {
        return complexPrice;
    }

    public void setComplexPrice(Long complexPrice) {
        this.complexPrice = complexPrice;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public PreorderState getState() {
        return state;
    }

    public void setState(PreorderState state) {
        this.state = state;
    }

    public Long getIdOfGoodsRequestPosition() {
        return idOfGoodsRequestPosition;
    }

    public void setIdOfGoodsRequestPosition(Long idOfGoodsRequestPosition) {
        this.idOfGoodsRequestPosition = idOfGoodsRequestPosition;
    }

    public RegularPreorder getRegularPreorder() {
        return regularPreorder;
    }

    public void setRegularPreorder(RegularPreorder regularPreorder) {
        this.regularPreorder = regularPreorder;
    }

    public Integer getModeOfAdd() {
        return modeOfAdd;
    }

    public void setModeOfAdd(Integer modeOfAdd) {
        this.modeOfAdd = modeOfAdd;
    }

    public Integer getModeFree() {
        return modeFree;
    }

    public void setModeFree(Integer modeFree) {
        this.modeFree = modeFree;
    }

    public Long getIdOfOrgOnCreate() {
        return idOfOrgOnCreate;
    }

    public void setIdOfOrgOnCreate(Long idOfOrgOnCreate) {
        this.idOfOrgOnCreate = idOfOrgOnCreate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public PreorderMobileGroupOnCreateType getMobileGroupOnCreate() {
        return mobileGroupOnCreate;
    }

    public void setMobileGroupOnCreate(PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.mobileGroupOnCreate = mobileGroupOnCreate;
    }

    public Integer getCancelnotification() {
        return cancelnotification;
    }

    public void setCancelnotification(Integer cancelnotification) {
        this.cancelnotification = cancelnotification;
    }
}
