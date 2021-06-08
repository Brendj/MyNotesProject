package ru.iteco.restservice.model.preorder;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.enums.PreorderState;
import ru.iteco.restservice.model.wt.WtComplexesItem;
import ru.iteco.restservice.model.wt.WtDish;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "cf_preorder_menudetail")
public class PreorderMenuDetail {
    @Id
    @GenericGenerator(
            name = "cf_preorder_menudetail_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "cf_preorder_menudetail_idofpreordermenudetail_seq"),
                    @Parameter(name = "INCREMENT", value = "1"),
                    @Parameter(name = "MINVALUE", value = "1"),
                    @Parameter(name = "MAXVALUE", value = "2147483647"),
                    @Parameter(name = "CACHE", value = "1")
            })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_preorder_menudetail_seq")
    @Column(name = "idOfPreorderMenuDetail")
    private Long idOfPreorderMenuDetail;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "preorderDate")
    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    private Date preorderDate;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "deletedState")
    private Integer deletedState;

    @Column(name = "guid")
    private String guid;

    @Column(name = "armIdOfMenu")
    private Long armIdOfMenu;

    @ManyToOne
    @JoinColumn(name = "idofpreordercomplex")
    private PreorderComplex preorderComplex;

    @Column(name = "menuDetailName")
    private String menuDetailName;

    @Column(name = "menuDetailPrice")
    private Long menuDetailPrice;

    @Column(name = "itemCode")
    private String itemCode;

    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    private PreorderState state;

    @Column(name = "idOfGoodsRequestPosition")
    private Long idOfGoodsRequestPosition;

    @ManyToOne
    @JoinColumn(name = "idofregularpreorder")
    private RegularPreorder regularPreorder;

    @Column(name = "menuDetailOutput")
    private String menuDetailOutput;

    @Column(name = "protein")
    private Double protein;

    @Column(name = "fat")
    private Double fat;

    @Column(name = "carbohydrates")
    private Double carbohydrates;

    @Column(name = "calories")
    private Double calories;

    @Column(name = "groupName")
    private String groupName;

    @Column(name = "availableNow")
    private Integer availableNow;

    @Column(name = "shortName")
    private String shortName;

    @Column(name = "idOfGood")
    private Long idOfGood;

    @Column(name = "usedSum")
    private Long usedSum;

    @Column(name = "usedAmount")
    private Long usedAmount;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "mobileGroupOnCreate")
    @Enumerated(EnumType.ORDINAL)
    private PreorderMobileGroupOnCreateType mobileGroupOnCreate;

    @Column(name = "idOfDish")
    private Long idOfDish;

    public PreorderMenuDetail() { }

    public PreorderMenuDetail(PreorderComplex preorderComplex, WtDish wtDish, Client client, Date date,
                              Integer amount, String groupName, String guardianMobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.preorderComplex = preorderComplex;
        this.client = client;
        this.preorderDate = date;
        this.amount = amount;
        this.deletedState = 0;
        this.state = PreorderState.OK;
        this.usedSum = 0L;
        this.usedAmount = 0L;
        this.setMenuDetailName(wtDish.getComponentsOfDish());
        this.setMenuDetailPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
        this.setGroupName(groupName);
        this.setItemCode(wtDish.getCode());
        this.setAvailableNow(0);
        this.setCalories(wtDish.getCalories() == null ? (double) 0 : wtDish.getCalories().doubleValue());
        this.setCarbohydrates(wtDish.getCarbohydrates() == null ? (double) 0 :
                wtDish.getCarbohydrates().doubleValue());
        this.setFat(wtDish.getFat() == null ? (double) 0 : wtDish.getFat().doubleValue());
        this.setMenuDetailOutput(wtDish.getQty() == null ? "" : wtDish.getQty());
        this.setProtein(wtDish.getProtein() == null ? (double) 0 : wtDish.getProtein().doubleValue());
        this.setShortName(wtDish.getDishName());
        this.setIdOfDish(wtDish.getIdOfDish());
        this.setMobile(guardianMobile);
        this.setMobileGroupOnCreate(mobileGroupOnCreate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreorderMenuDetail preorderMenuDetail = (PreorderMenuDetail) o;
        return preorderComplex.getGuid().equals(preorderMenuDetail.getPreorderComplex().getGuid())
                && itemCode.equals(preorderMenuDetail.getItemCode());
    }

    @Override
    public int hashCode() {
        return idOfPreorderMenuDetail != null ? idOfPreorderMenuDetail.hashCode() : 0;
    }

    public Long getIdOfPreorderMenuDetail() {
        return idOfPreorderMenuDetail;
    }

    public void setIdOfPreorderMenuDetail(Long idOfPreorderMenuDetail) {
        this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
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

    public Long getArmIdOfMenu() {
        return armIdOfMenu;
    }

    public void setArmIdOfMenu(Long armIdOfMenu) {
        this.armIdOfMenu = armIdOfMenu;
    }

    public PreorderComplex getPreorderComplex() {
        return preorderComplex;
    }

    public void setPreorderComplex(PreorderComplex preorderComplex) {
        this.preorderComplex = preorderComplex;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public Long getMenuDetailPrice() {
        return menuDetailPrice;
    }

    public void setMenuDetailPrice(Long menuDetailPrice) {
        this.menuDetailPrice = menuDetailPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
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

    public String getMenuDetailOutput() {
        return menuDetailOutput;
    }

    public void setMenuDetailOutput(String menuDetailOutput) {
        this.menuDetailOutput = menuDetailOutput;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(Integer availableNow) {
        this.availableNow = availableNow;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getIdOfGood() {
        return idOfGood;
    }

    public void setIdOfGood(Long idOfGood) {
        this.idOfGood = idOfGood;
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

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }
}
