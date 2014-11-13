package ru.axetta.ecafe.processor.core.persistence.dao.model.order;

/**
 * User: shamil
 * Date: 23.10.14
 * Time: 16:45
 */
public class OrderItem {

    public long idOfOrg;
    public long idOfClient;
    public long orderDate;
    public int ordertype;
    public int idOfComplex;
    public String complexName; //complex name
    public String groupName;
    private int qty;

    public OrderItem(long idOfOrg, long idOfClient, long orderDate, int ordertype, int idOfComplex, String complexName,
            String groupName, int qty) {
        this.idOfOrg = idOfOrg;
        this.idOfClient = idOfClient;
        this.orderDate = orderDate;
        this.ordertype = ordertype;
        this.idOfComplex = idOfComplex;
        this.complexName = complexName;
        this.groupName = groupName;
        this.qty = qty;
    }

    public OrderItem(long idOfClient, long orderDate, int ordertype, int idOfComplex, String complexName,
            String groupName, int qty) {
        this.idOfClient = idOfClient;
        this.orderDate = orderDate;
        this.ordertype = ordertype;
        this.idOfComplex = idOfComplex;
        this.complexName = complexName;
        this.groupName = groupName;
        this.qty = qty;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(int ordertype) {
        this.ordertype = ordertype;
    }

    public int getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(int idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
