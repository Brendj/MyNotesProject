package ru.iteco.restservice.model.preorder;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.enums.RegularPreorderState;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cf_regular_preorders")
public class RegularPreorder {
    @Id
    @GeneratedValue(generator = "cf_regular_preorder-seq")
    @GenericGenerator(
            name = "cf_regular_preorder-seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "cf_regular_preorders_Id_Gen_seq"),
                    @Parameter(name = "optimizer", value = "pooled-lo"),
                    @Parameter(name = "increment_size", value = "32")
            }
    )
    @Column(name = "idOfRegularPreorder")
    private Long idOfRegularPreorder;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "startDate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date startDate;

    @Column(name = "endDate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date endDate;

    @Column(name = "itemCode")
    private String itemCode;

    @Column(name = "idOfComplex")
    private Integer idOfComplex;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "monday")
    private Integer monday;

    @Column(name = "tuesday")
    private Integer tuesday;

    @Column(name = "wednesday")
    private Integer wednesday;

    @Column(name = "thursday")
    private Integer thursday;

    @Column(name = "friday")
    private Integer friday;

    @Column(name = "saturday")
    private Integer saturday;

    @Column(name = "price")
    private Long price;

    @Column(name = "createdDate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date createdDate;

    @Column(name = "lastUpdate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date lastUpdate;

    @Column(name = "deletedState")
    private Integer deletedState;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    private RegularPreorderState state;

    @Column(name = "mobileGroupOnCreate")
    @Enumerated(EnumType.ORDINAL)
    private PreorderMobileGroupOnCreateType mobileGroupOnCreate;

    @Column(name = "sendeddailynotification")
    private Boolean sendeddailynotification;

    @Column(name = "idOfDish")
    private Long idOfDish;

    @Column(name = "cancelnotification")
    private Boolean cancelnotification;

    public Long getIdOfRegularPreorder() {
        return idOfRegularPreorder;
    }

    public void setIdOfRegularPreorder(Long idOfRegularPreorder) {
        this.idOfRegularPreorder = idOfRegularPreorder;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Integer idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getMonday() {
        return monday;
    }

    public void setMonday(Integer monday) {
        this.monday = monday;
    }

    public Integer getTuesday() {
        return tuesday;
    }

    public void setTuesday(Integer tuesday) {
        this.tuesday = tuesday;
    }

    public Integer getWednesday() {
        return wednesday;
    }

    public void setWednesday(Integer wednesday) {
        this.wednesday = wednesday;
    }

    public Integer getThursday() {
        return thursday;
    }

    public void setThursday(Integer thursday) {
        this.thursday = thursday;
    }

    public Integer getFriday() {
        return friday;
    }

    public void setFriday(Integer friday) {
        this.friday = friday;
    }

    public Integer getSaturday() {
        return saturday;
    }

    public void setSaturday(Integer saturday) {
        this.saturday = saturday;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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

    public Integer getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Integer deletedState) {
        this.deletedState = deletedState;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public RegularPreorderState getState() {
        return state;
    }

    public void setState(RegularPreorderState state) {
        this.state = state;
    }

    public PreorderMobileGroupOnCreateType getMobileGroupOnCreate() {
        return mobileGroupOnCreate;
    }

    public void setMobileGroupOnCreate(PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.mobileGroupOnCreate = mobileGroupOnCreate;
    }

    public Boolean getSendeddailynotification() {
        return sendeddailynotification;
    }

    public void setSendeddailynotification(Boolean sendeddailynotification) {
        this.sendeddailynotification = sendeddailynotification;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Boolean getCancelnotification() {
        return cancelnotification;
    }

    public void setCancelnotification(Boolean cancelnotification) {
        this.cancelnotification = cancelnotification;
    }
}
