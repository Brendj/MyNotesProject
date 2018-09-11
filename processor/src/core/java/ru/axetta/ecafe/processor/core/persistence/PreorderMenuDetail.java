/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 12.03.2018.
 */
public class PreorderMenuDetail {
    private Long idOfPreorderMenuDetail;
    private Client client;
    private Date preorderDate;
    private Integer amount;
    private Boolean deletedState;
    private String guid;
    private Long armIdOfMenu;
    private PreorderComplex preorderComplex;
    private String menuDetailName;
    private Long menuDetailPrice;
    private String itemCode;
    private PreorderState state;
    private Long idOfGoodsRequestPosition;
    private RegularPreorder regularPreorder;
    private String menuDetailOutput;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
    private Double calories;
    private String groupName;
    private Integer availableNow;
    private String shortName;

    public PreorderMenuDetail() {

    }

    public void deleteBySupplier(Long nextVersion, boolean doDelete) {
        doDelete(nextVersion, doDelete, PreorderState.DELETED);
    }

    public void changeBySupplier(Long nextVersion, boolean doDelete) {
        doDelete(nextVersion, doDelete, PreorderState.CHANGED_PRICE);
    }

    public void deleteByChangeOrg(Long nextVersion, boolean doDelete) {
        doDelete(nextVersion, doDelete, PreorderState.CHANGE_ORG);
    }

    private void doDelete(Long nextVersion, boolean doDelete, PreorderState state) {
        this.preorderComplex.setVersion(nextVersion);
        this.deletedState = doDelete;
        if (doDelete) this.amount = 0;
        this.state = state;
        this.preorderComplex.setLastUpdate(new Date());
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

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
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
}
