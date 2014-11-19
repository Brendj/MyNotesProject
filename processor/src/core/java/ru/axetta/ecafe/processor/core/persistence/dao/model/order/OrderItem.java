package ru.axetta.ecafe.processor.core.persistence.dao.model.order;

/**
 * User: shamil
 * Date: 23.10.14
 * Time: 16:45
 */
public class OrderItem {

    public long idOfOrg;
    public String orgName;
    public long idOfClient;
    public String fullname;
    public long idOfClientOrg;
    public String clientOrgName;
    public long orderDate;
    public int ordertype;
    public int idOfComplex;
    public String complexName; //complex name
    public String groupName;
    private int qty;

    public OrderItem(long idOfOrg, String orgName, long idOfClient, long orderDate, int ordertype, int idOfComplex, String complexName,
            String groupName, int qty, long idOfClientOrg, String clientOrgName, String fullname) {
        this.idOfOrg = idOfOrg;
        this.orgName = orgName;
        this.idOfClient = idOfClient;
        this.orderDate = orderDate;
        this.ordertype = ordertype;
        this.idOfComplex = idOfComplex;
        this.complexName = complexName;
        this.groupName = groupName;
        this.qty = qty;
        this.idOfClientOrg = idOfClientOrg;
        this.clientOrgName = clientOrgName;
        this.fullname = fullname;
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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public long getIdOfClientOrg() {
        return idOfClientOrg;
    }

    public void setIdOfClientOrg(long idOfClientOrg) {
        this.idOfClientOrg = idOfClientOrg;
    }

    public String getClientOrgName() {
        return clientOrgName;
    }

    public void setClientOrgName(String clientOrgName) {
        this.clientOrgName = clientOrgName;
    }
}
