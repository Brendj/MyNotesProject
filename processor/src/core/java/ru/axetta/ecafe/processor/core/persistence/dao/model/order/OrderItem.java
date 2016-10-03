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
    private long sum;

    private long createdDateEqualsOrderDate;


    private long socDiscount;
    private int menutype;
    private int menuOrigin;
    private String manufacturer;

    public OrderItem(long idOfOrg, String orgName, long idOfClient, long orderDate, int ordertype, int idOfComplex, String complexName,
            String groupName, int qty, long idOfClientOrg, String clientOrgName, String fullname, long createdDateEqualsOrderDate) {
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
        this.createdDateEqualsOrderDate = createdDateEqualsOrderDate;
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

    public OrderItem(String orgName, long orderDate, long sum) {
        this.orgName = orgName;
        this.orderDate = orderDate;
        this.sum = sum;
    }
    public OrderItem(long idOfOrg, long orderDate, long sum, long socDiscount, int menutype, int menuOrigin) {
        this.idOfOrg = idOfOrg;
        this.orderDate = orderDate;
        this.sum = sum;
        this.socDiscount = socDiscount;
        this.menutype = menutype;
        this.menuOrigin = menuOrigin;
    }

    public OrderItem(long idOfOrg, long orderDate, long sum, String manufacturer) {
        this.idOfOrg = idOfOrg;
        this.orderDate = orderDate;
        this.sum = sum;
        this.manufacturer = manufacturer;
    }

    public OrderItem(long idOfOrg, int ordertype, long sum) {
        this.idOfOrg = idOfOrg;
        this.ordertype = ordertype;
        this.sum = sum;
    }

    public OrderItem(long idOfOrg, int ordertype, long sum, long idOfClient) {
        this.idOfOrg = idOfOrg;
        this.ordertype = ordertype;
        this.sum = sum;
        this.idOfClient = idOfClient;
    }

    public OrderItem(long idOfOrg, int ordertype, int qty, long sum) {
        this.idOfOrg = idOfOrg;
        this.ordertype = ordertype;
        this.qty = qty;
        this.sum = sum;
    }

    public OrderItem(long idOfOrg, long sum) {
        this.idOfOrg = idOfOrg;
        this.sum = sum;
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

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public long getCreatedDateEqualsOrderDate() {
        return createdDateEqualsOrderDate;
    }

    public void setCreatedDateEqualsOrderDate(long createdDateEqualsOrderDate) {
        this.createdDateEqualsOrderDate = createdDateEqualsOrderDate;
    }

    public long getSocDiscount() {
        return socDiscount;
    }

    public int getMenutype() {
        return menutype;
    }

    public int getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(int menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
