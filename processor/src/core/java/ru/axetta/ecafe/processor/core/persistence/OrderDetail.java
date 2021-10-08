/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodComplaintOrders;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class OrderDetail {
    public static final int STATE_COMMITED=0, STATE_CANCELED=1;

    public static final int TYPE_DISH_ITEM = 0;
    public static final int TYPE_COMPLEX_MIN = 50;
    public static final int TYPE_COMPLEX_MAX = 99;
    public static final int TYPE_COMPLEX_0 = 50;
    public static final int TYPE_COMPLEX_1 = 51;
    public static final int TYPE_COMPLEX_2 = 52;
    public static final int TYPE_COMPLEX_3 = 53;
    public static final int TYPE_COMPLEX_4 = 54;
    public static final int TYPE_COMPLEX_5 = 55;
    public static final int TYPE_COMPLEX_6 = 56;
    public static final int TYPE_COMPLEX_7 = 57;
    public static final int TYPE_COMPLEX_8 = 58;
    public static final int TYPE_COMPLEX_9 = 59;
    public static final int TYPE_COMPLEX_ITEM_MIN = 150;
    public static final int TYPE_COMPLEX_ITEM_MAX = 199;
    //public static final int TYPE_COMPLEX_ITEM_0 = 150;
    //public static final int TYPE_COMPLEX_ITEM_1 = 151;
    //public static final int TYPE_COMPLEX_ITEM_2 = 152;
    //public static final int TYPE_COMPLEX_ITEM_3 = 153;
    //public static final int TYPE_COMPLEX_ITEM_4 = 154;
    //public static final int TYPE_COMPLEX_ITEM_5 = 155;
    //public static final int TYPE_COMPLEX_ITEM_6 = 156;
    //public static final int TYPE_COMPLEX_ITEM_7 = 157;
    //public static final int TYPE_COMPLEX_ITEM_8 = 158;
    //public static final int TYPE_COMPLEX_ITEM_9 = 159;
    public static final int TYPE_COMPLEX_LAST = TYPE_COMPLEX_ITEM_MAX+1;           // неодбходи мо обновлять, если добавится новый комплекс! Используется в BeneficiarySummaryReport

    private CompositeIdOfOrderDetail compositeIdOfOrderDetail;
    private Long idOfOrder;
    private Org org;
    private Order order;
    private Long qty;
    private Long discount;
    private Long socDiscount;
    private Long rPrice;
    private String menuDetailName;
    private String rootMenu;
    private int menuType;
    private String menuOutput;
    private int menuOrigin;
    private String menuGroup;
    private int state;
    private String itemCode;
    private Long idOfRule;
    private Good good;
    private Set<GoodComplaintOrders> goodComplaintOrdersInternal;
    private Long idOfMenuFromSync;
    private String manufacturer;
    private boolean sendToExternal;
    private Integer fRation;
    //private Long idOfComplex;
    private Long idOfDish;
    private WtComplex wtComplex;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Long getIdOfMenuFromSync() {
        return idOfMenuFromSync;
    }

    public void setIdOfMenuFromSync(Long idOfMenuFromSync) {
        this.idOfMenuFromSync = idOfMenuFromSync;
    }

    public Set<GoodComplaintOrders> getGoodComplaintOrdersInternal() {
        return goodComplaintOrdersInternal;
    }

    public void setGoodComplaintOrdersInternal(Set<GoodComplaintOrders> goodComplaintOrdersInternal) {
        this.goodComplaintOrdersInternal = goodComplaintOrdersInternal;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getMenuGroup() {
        return menuGroup;
    }

    public void setMenuGroup(String menuGroup) {
        this.menuGroup = menuGroup;
    }

    public int getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(int menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public String getMenuOutput() {
        return menuOutput;
    }

    public void setMenuOutput(String menuOutput) {
        this.menuOutput = menuOutput;
    }

    public int getMenuType() {
        return menuType;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    protected OrderDetail() {
        // For Hibernate only
    }

    public OrderDetail(CompositeIdOfOrderDetail compositeIdOfOrderDetail, long idOfOrder, long qty, long discount,
            long socDiscount, long rPrice, String menuDetailName, String rootMenu, String menuGroup, int menuOrigin,
            String menuOutput, int menuType, Long idOfMenuFromSync, String manufacturer, boolean sendToExternal,
            String itemCode, Long idOfRule, Integer fRation) {
        this.compositeIdOfOrderDetail = compositeIdOfOrderDetail;
        this.idOfOrder = idOfOrder;
        this.qty = qty;
        this.discount = discount;
        this.socDiscount = socDiscount;
        this.rPrice = rPrice;
        this.menuDetailName = menuDetailName;
        this.rootMenu = rootMenu;
        this.menuGroup = menuGroup;
        this.menuOrigin = menuOrigin;
        this.menuOutput = menuOutput;
        this.menuType = menuType;
        this.idOfMenuFromSync = idOfMenuFromSync;
        this.manufacturer = manufacturer;
        this.sendToExternal = sendToExternal;
        this.itemCode = itemCode;
        this.idOfRule = idOfRule;
        this.fRation = fRation;
    }

    public boolean isFRationSpecified() {
        return fRation != null && OrderDetailFRationTypeWTdiet.getValues().get(fRation) == null;
    }

    public CompositeIdOfOrderDetail getCompositeIdOfOrderDetail() {
        return compositeIdOfOrderDetail;
    }

    private void setCompositeIdOfOrderDetail(CompositeIdOfOrderDetail compositeIdOfOrderDetail) {
        // For Hibernate only
        this.compositeIdOfOrderDetail = compositeIdOfOrderDetail;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    private void setIdOfOrder(Long idOfOrder) {
        // For Hibernate only
        this.idOfOrder = idOfOrder;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public Order getOrder() {
        return order;
    }

    private void setOrder(Order order) {
        // For Hibernate only
        this.order = order;
    }

    public Long getQty() {
        return qty;
    }

    private void setQty(Long qty) {
        // For Hibernate only
        this.qty = qty;
    }

    public Long getDiscount() {
        return discount;
    }

    private void setDiscount(Long discount) {
        // For Hibernate only
        this.discount = discount;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public void setSocDiscount(Long socDiscount) {
        this.socDiscount = socDiscount;
    }

    public Long getRPrice() {
        return rPrice;
    }

    private void setRPrice(Long rPrice) {
        // For Hibernate only
        this.rPrice = rPrice;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    private void setMenuDetailName(String menuDetailName) {
        // For Hibernate only
        this.menuDetailName = menuDetailName;
    }

    public String getRootMenu() {
        return rootMenu;
    }

    public void setRootMenu(String rootMenu) {
        this.rootMenu = rootMenu;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public boolean isSendToExternal() {
        return sendToExternal;
    }

    public void setSendToExternal(boolean sendToExternal) {
        this.sendToExternal = sendToExternal;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public WtComplex getWtComplex() {
        return wtComplex;
    }

    public void setWtComplex(WtComplex wtComplex) {
        this.wtComplex = wtComplex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDetail)) {
            return false;
        }
        final OrderDetail that = (OrderDetail) o;
        return compositeIdOfOrderDetail.equals(that.getCompositeIdOfOrderDetail());
    }

    @Override
    public int hashCode() {
        return compositeIdOfOrderDetail.hashCode();
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "compositeIdOfOrderDetail=" + compositeIdOfOrderDetail + ", idOfOrder=" + idOfOrder
                + ", org=" + org + ", order=" + order + ", qty=" + qty + ", discount=" + discount
                + ", rPrice=" + rPrice + ", socDiscount=" + socDiscount + ", menuDetailName='" + menuDetailName
                + '\'' + ", rootMenu='" + rootMenu + '\'' + ", menuType=" + menuType + ", menuOutput='"
                + menuOutput + '\'' + ", menuOrigin=" + menuOrigin + '}';
    }

    public final static int PRODUCT_OWN = 0, PRODUCT_CENTRALIZE = 1, PRODUCT_CENTRALIZE_COOK = 2, PRODUCT_PURCHASE = 10, PRODUCT_VENDING = 11, PRODUCT_COMMERCIAL = 20;
    public final static String[] PRODUCTION_NAMES_TYPES = {
            "Собственное", "Централизованное", "Централизованное с доготовкой", "Закупленное", "Вендинг",
            "Коммерческое питание"};
    public final static String[] PRODUCTION_CODE_TYPES = {
            "own", "centralize", "centralize_cook", "purchase", "vending", "commercial"};

    public static String getMenuOriginAsString(int menuOrigin) {
        if (menuOrigin==PRODUCT_OWN) return PRODUCTION_NAMES_TYPES[0];
        else if (menuOrigin==PRODUCT_CENTRALIZE) return PRODUCTION_NAMES_TYPES[1];
        else if (menuOrigin==PRODUCT_CENTRALIZE_COOK) return PRODUCTION_NAMES_TYPES[2];
        else if (menuOrigin==PRODUCT_PURCHASE) return PRODUCTION_NAMES_TYPES[3];
        else if (menuOrigin==PRODUCT_VENDING) return PRODUCTION_NAMES_TYPES[4];
        else if (menuOrigin==PRODUCT_COMMERCIAL) return PRODUCTION_NAMES_TYPES[5];
        return "Неизвестное";
    }

    public static String getMenuOriginAsCode(int menuOrigin) {
        switch (menuOrigin) {
            case PRODUCT_OWN:
                return PRODUCTION_CODE_TYPES[0];
            case PRODUCT_CENTRALIZE:
                return PRODUCTION_CODE_TYPES[1];
            case PRODUCT_CENTRALIZE_COOK:
                return PRODUCTION_CODE_TYPES[2];
            case PRODUCT_PURCHASE:
                return PRODUCTION_CODE_TYPES[3];
            case PRODUCT_VENDING:
                return PRODUCTION_CODE_TYPES[4];
            case PRODUCT_COMMERCIAL:
                return PRODUCTION_CODE_TYPES[5];
            default:
                return "unknown";
        }
    }

    public boolean isComplex() {
        return menuType>=TYPE_COMPLEX_MIN && menuType<=TYPE_COMPLEX_MAX;
    }
    public boolean isComplexItem() {
        return menuType>=TYPE_COMPLEX_ITEM_MIN && menuType<=TYPE_COMPLEX_ITEM_MAX;
    }

    public Integer getfRation() {
        return fRation;
    }

    public void setfRation(Integer fRation) {
        this.fRation = fRation;
    }
}