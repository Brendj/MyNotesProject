/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class OrderDetail {
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

    //private CompositeIdOfOrderDetail compositeIdOfOrderDetail;
    private Long idOfOrderDetail;
    private Long idOfOrder;
    //private Org org;
    //private Order order;
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

    OrderDetail() {
        // For Hibernate only
    }



    public OrderDetail(Long idOfOrderDetail, long idOfOrder, long qty, long discount,
            long socDiscount, long rPrice, String menuDetailName, String rootMenu, String menuGroup, int menuOrigin,
            String menuOutput, int menuType) {
        this.idOfOrderDetail = idOfOrderDetail;
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
    }

     //public OrderDetail(CompositeIdOfOrderDetail compositeIdOfOrderDetail, long idOfOrder, long qty, long discount,
    //        long socDiscount, long rPrice, String menuDetailName, String rootMenu, String menuGroup, int menuOrigin,
    //        String menuOutput, int menuType) {
    //    this.compositeIdOfOrderDetail = compositeIdOfOrderDetail;
    //    this.idOfOrder = idOfOrder;
    //    this.qty = qty;
    //    this.discount = discount;
    //    this.socDiscount = socDiscount;
    //    this.rPrice = rPrice;
    //    this.menuDetailName = menuDetailName;
    //    this.rootMenu = rootMenu;
    //    this.menuGroup = menuGroup;
    //    this.menuOrigin = menuOrigin;
    //    this.menuOutput = menuOutput;
    //    this.menuType = menuType;
    //}

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    //public CompositeIdOfOrderDetail getCompositeIdOfOrderDetail() {
    //    return compositeIdOfOrderDetail;
    //}
    //
    //private void setCompositeIdOfOrderDetail(CompositeIdOfOrderDetail compositeIdOfOrderDetail) {
    //    // For Hibernate only
    //    this.compositeIdOfOrderDetail = compositeIdOfOrderDetail;
    //}

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    private void setIdOfOrder(Long idOfOrder) {
        // For Hibernate only
        this.idOfOrder = idOfOrder;
    }

    //public Org getOrg() {
    //    return org;
    //}
    //
    //private void setOrg(Org org) {
    //    // For Hibernate only
    //    this.org = org;
    //}
    //
    //public Order getOrder() {
    //    return order;
    //}
    //
    //private void setOrder(Order order) {
    //    // For Hibernate only
    //    this.order = order;
    //}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDetail)) {
            return false;
        }
        final OrderDetail that = (OrderDetail) o;
        //return compositeIdOfOrderDetail.equals(that.getCompositeIdOfOrderDetail());
        return idOfOrderDetail.equals(idOfOrderDetail);
    }


    @Override
    public int hashCode() {
        return idOfOrderDetail.hashCode();
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "idOfOrderDetail=" + idOfOrderDetail + ", idOfOrder=" + idOfOrder +
                ", qty=" + qty + ", discount=" + discount
                + ", rPrice=" + rPrice + ", socDiscount=" + socDiscount + ", menuDetailName='" + menuDetailName
                + '\'' + ", rootMenu='" + rootMenu + '\'' + ", menuType=" + menuType + ", menuOutput='"
                + menuOutput + '\'' + ", menuOrigin=" + menuOrigin + '}';
    }
    public final static int PRODUCT_OWN = 0, PRODUCT_CENTRALIZE = 1, PRODUCT_CENTRALIZE_COOK = 2, PRODUCT_PURCHASE = 10;
    public final static String[] PRODUCTION_NAMES_TYPES = { "Собственное", "Централизованное", "Централизованное с доготовкой", "Закупленное" };

    public static String getMenuOriginAsString(int menuOrigin) {
        if (menuOrigin==PRODUCT_OWN) return PRODUCTION_NAMES_TYPES[0];
        else if (menuOrigin==PRODUCT_CENTRALIZE) return PRODUCTION_NAMES_TYPES[1];
        else if (menuOrigin==PRODUCT_CENTRALIZE_COOK) return PRODUCTION_NAMES_TYPES[2];
        else if (menuOrigin==PRODUCT_PURCHASE) return PRODUCTION_NAMES_TYPES[3];
        return "Неизвестное";
    }

}