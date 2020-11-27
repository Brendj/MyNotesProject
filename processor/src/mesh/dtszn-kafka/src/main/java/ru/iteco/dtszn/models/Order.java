/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import ru.iteco.dtszn.models.compositeId.OrderCompositeId;

import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_orders")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "order_with_detail_only",
                attributeNodes = {
                        @NamedAttributeNode("orderDetailSet")
                }
        ),
        @NamedEntityGraph(
                name = "full_info",
                attributeNodes = {
                    @NamedAttributeNode(
                            value = "orderDetailSet",
                            subgraph = "orderDetailSet.rule"
                    ),
                    @NamedAttributeNode(
                            value = "client",
                            subgraph = "client.discounts"
                    ),
                    @NamedAttributeNode("org")
                },
                subgraphs = {
                    @NamedSubgraph(
                            name = "orderDetailSet.rule",
                            attributeNodes = {
                                    @NamedAttributeNode("rule")
                            }
                    ),
                    @NamedSubgraph(
                            name = "client.discounts",
                            attributeNodes = {
                                    @NamedAttributeNode("discounts")
                            }
                    )
                }
        )
})
@SqlResultSetMapping(
        name = "fullInfo",
        classes = {
                @ConstructorResult(
                        targetClass = ru.iteco.dtszn.models.dto.SupplyMSPOrders.class,
                        columns = {
                                @ColumnResult(name = "idOfOrg", type = Long.class),
                                @ColumnResult(name = "idOfOrder", type = Long.class),
                                @ColumnResult(name = "meshGUID", type = String.class),
                                @ColumnResult(name = "code", type = Integer.class),
                                @ColumnResult(name = "dtsznCodes", type = String.class),
                                @ColumnResult(name = "categoryName", type = String.class),
                                @ColumnResult(name = "orderDate", type = Long.class),
                                @ColumnResult(name = "rSum", type = Long.class),
                                @ColumnResult(name = "organizationId", type = Long.class),
                                @ColumnResult(name = "details", type = String.class),
                        }
                )
        }
)
@NamedNativeQuery(
        name = "Order.fullInfo",
        resultSetMapping = "fullInfo",
        query = "select o.idoforg as \"idOfOrg\",\n" +
        "       o.idoforder as \"idOfOrder\",\n" +
        "       c.meshguid as \"meshGUID\",\n" +
        "       ccm.code as \"code\",\n" +
        "       string_agg(cast(cd_dszn.code as text), ';')  as \"dtsznCodes\",\n" +
        "       string_agg(distinct cd.categoryname, ';') as \"categoryName\",\n" +
        "       o.orderdate as \"orderDate\",\n" +
        "       o.rsum  as \"rSum\",\n" +
        "       org.organizationidfromnsi  as \"organizationId\",\n" +
        "       string_agg(od.menudetailname, ';')  as \"details\"\n" +
        " from cf_orders o\n" +
        "         join cf_orderdetails od on o.idoforder = od.idoforder and o.idoforg = od.idoforg\n" +
        "         join cf_clients c on o.idofclient = c.idofclient and c.meshguid is not null\n" +
        "         join cf_clientdiscounts ccd on c.idofclient = ccd.idofclient\n" +
        "         left join cf_discountrules dr on dr.idofrule = od.idofrule\n" +
        "         left join cf_wt_discountrules wdr on wdr.idofrule = od.idofrule\n" +
        "         left join cf_discountrules_categorydiscounts drcc on drcc.idofrule = dr.idofrule\n" +
        "         left join cf_wt_discountrules_categorydiscount wdrcc on wdr.idofrule = wdrcc.idofrule\n" +
        "         join cf_categorydiscounts cd on ((drcc.idofcategorydiscount = cd.idofcategorydiscount) or\n" +
        "                                         (wdrcc.idofcategorydiscount = cd.idofcategorydiscount))\n" +
        "                                         and cd.idofcategorydiscount = ccd.idofcategorydiscount\n" +
        "         left join cf_categorydiscounts_dszn cd_dszn on cd.idofcategorydiscount = cd_dszn.idofcategorydiscount\n" +
        "         left join cf_code_msp ccm on dr.idofcode = ccm.idofcode\n" +
        "         join cf_orgs org on o.idoforg = org.idoforg\n" +
        "where o.state = 0\n" +
        "  and o.createddate between :begin and :end \n" +
        "  and o.ordertype in :types \n" +
        "  and od.menutype between 50 and 99\n" +
        "  and od.idofrule is not null\n" +
        "  and (cd.idofcategorydiscount >= 0 or cd.idofcategorydiscount = -90)\n" +
        "  group by 1,2,3,4,7,8,9"
)
public class Order {
    public static final int DISCOUNT_TYPE = 4;
    public static final int DISCOUNT_TYPE_RESERVE = 6;

    @EmbeddedId
    private OrderCompositeId compositeId;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    @JoinFormula("(meshguid is not null or meshguid not like '')")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetailSet;

    @Column(name = "orderdate")
    private Long orderDate;

    @Column(name = "createddate")
    private Long createdDate;

    @Column(name = "ordertype")
    private Integer orderType;

    @Column(name = "rsum")
    private Long rsum;

    @Column(name = "state")
    private Integer state;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Set<OrderDetail> getOrderDetailSet() {
        return orderDetailSet;
    }

    public void setOrderDetailSet(Set<OrderDetail> orderDetailSet) {
        this.orderDetailSet = orderDetailSet;
    }

    public OrderCompositeId getCompositeId() {
        return compositeId;
    }

    public void setCompositeId(OrderCompositeId compositeKey) {
        this.compositeId = compositeKey;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getRsum() {
        return rsum;
    }

    public void setRsum(Long rsum) {
        this.rsum = rsum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(compositeId, order.compositeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeId);
    }
}
