/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.Gender;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_clients")
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "clientResponse",
                classes = {
                        @ConstructorResult(
                                targetClass = ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO.class,
                                columns = {
                                    @ColumnResult(name = "contractId", type = Long.class),
                                    @ColumnResult(name = "balance", type = Long.class),
                                    @ColumnResult(name = "firstName", type = String.class),
                                    @ColumnResult(name = "lastName", type = String.class),
                                    @ColumnResult(name = "middleName", type = String.class),
                                    @ColumnResult(name = "grade", type = String.class),
                                    @ColumnResult(name = "orgName", type = String.class),
                                    @ColumnResult(name = "orgType", type = String.class),
                                    @ColumnResult(name = "address", type = String.class),
                                    @ColumnResult(name = "isInside", type = Boolean.class),
                                    @ColumnResult(name = "meshGuid", type = String.class),
                                    @ColumnResult(name = "specialMenu", type = Boolean.class),
                                    @ColumnResult(name = "gender", type = String.class),
                                    @ColumnResult(name = "categoryDiscount", type = String.class),
                                    @ColumnResult(name = "preorderAllowed", type = Boolean.class),
                                    @ColumnResult(name = "limit", type = Long.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = "guardResponse",
                classes = {
                        @ConstructorResult(
                                targetClass = ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO.class,
                                columns = {
                                        @ColumnResult(name = "contractId", type = Long.class),
                                        @ColumnResult(name = "firstName", type = String.class),
                                        @ColumnResult(name = "lastName", type = String.class),
                                        @ColumnResult(name = "middleName", type = String.class),
                                        @ColumnResult(name = "grade", type = String.class),
                                        @ColumnResult(name = "orgName", type = String.class),
                                        @ColumnResult(name = "orgType", type = String.class),
                                        @ColumnResult(name = "address", type = String.class),
                                        @ColumnResult(name = "relation", type = Integer.class),
                                        @ColumnResult(name = "isLegalRepresent", type = Integer.class)
                                }
                        )
                }
        )
    }
)
@NamedNativeQueries({
        @NamedNativeQuery(
            name="getClientByGuardPhone",
            resultSetMapping = "clientResponse",
            query = "SELECT child.contractId AS \"contractId\",\n"
                    + "       child.balance AS \"balance\",\n"
                    + "       cp.firstname AS \"firstName\",\n"
                    + "       cp.secondname AS \"lastName\",\n"
                    + "       cp.surname AS \"middleName\",\n"
                    + "       cc.groupname AS \"grade\",\n"
                    + "       co.shortname AS \"orgName\",\n"
                    + "       CASE\n"
                    + "           WHEN co.organizationtype = 0 THEN 'Общеобразовательное ОУ'\n"
                    + "           WHEN co.organizationtype = 1 THEN 'Дошкольное ОУ'\n"
                    + "           WHEN co.organizationtype = 2 THEN 'Поставщик питания'\n"
                    + "           WHEN co.organizationtype = 3 THEN 'Профессиональное ОУ'\n"
                    + "           WHEN co.organizationtype = 4 THEN 'Доп.образование'\n"
                    + "           ELSE 'Неизвестно' END AS \"orgType\",\n"
                    + "       co.shortaddress AS \"address\",\n"
                    + "       COALESCE((SELECT CASE WHEN ee.eventcode IN (0, 6, 100, 101, 102, 112) THEN TRUE ELSE FALSE END\n"
                    + "        FROM cf_enterevents ee\n"
                    + "        WHERE ee.idofclient = child.idofclient\n"
                    + "        ORDER BY evtdatetime DESC\n"
                    + "        LIMIT 1), FALSE) AS \"isInside\",\n"
                    + "       child.meshguid AS \"meshGuid\",\n"
                    + "       COALESCE(CASE WHEN child.specialmenu = 1 THEN TRUE ELSE FALSE END, FALSE) AS \"specialMenu\",\n"
                    + "       CASE WHEN child.gender = 0 THEN 'Ж' ELSE 'М' END AS \"gender\",\n"
                    + "       string_agg(c.categoryname, ',') AS \"categoryDiscount\",\n"
                    + "       COALESCE(CASE WHEN cpf.allowedpreorder = 1 THEN TRUE ELSE FALSE END, FALSE) AS \"preorderAllowed\",\n"
                    + "       child.limits AS \"limit\" \n"
                    + "FROM cf_clients AS child\n"
                    + "         JOIN cf_orgs AS co ON child.idoforg = co.idoforg\n"
                    + "         JOIN cf_persons AS cp ON child.idofperson = cp.idofperson\n"
                    + "         JOIN cf_clientgroups cc ON child.idoforg = cc.idoforg AND child.idofclientgroup = cc.idofclientgroup\n"
                    + "         LEFT JOIN cf_clients_categorydiscounts ccc ON child.idofclient = ccc.idofclient\n"
                    + "         LEFT JOIN cf_categorydiscounts c ON ccc.idofcategorydiscount = c.idofcategorydiscount\n"
                    + "         JOIN cf_client_guardian AS guardians ON child.idofclient = guardians.idofchildren\n"
                    + "         JOIN cf_clients AS guardian ON guardians.idofguardian = guardian.idofclient\n"
                    + "         LEFT JOIN cf_preorder_flags cpf ON child.idofclient = cpf.idofclient\n"
                    + "WHERE guardian.mobile LIKE :guardPhone \n"
                    + "GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,15,16;"
        ),
        @NamedNativeQuery(
                name="getGuardiansByClient",
                resultSetMapping = "guardResponse",
                query = "SELECT guardian.contractId AS \"contractId\",\n"
                        + "       cp.firstname AS \"firstName\",\n"
                        + "       cp.secondname AS \"lastName\",\n"
                        + "       cp.surname AS \"middleName\",\n"
                        + "       cc.groupname AS \"grade\",\n"
                        + "       co.shortname AS \"orgName\",\n"
                        + "       CASE\n"
                        + "           WHEN co.organizationtype = 0 THEN 'Общеобразовательное ОУ'\n"
                        + "           WHEN co.organizationtype = 1 THEN 'Дошкольное ОУ'\n"
                        + "           WHEN co.organizationtype = 2 THEN 'Поставщик питания'\n"
                        + "           WHEN co.organizationtype = 3 THEN 'Профессиональное ОУ'\n"
                        + "           WHEN co.organizationtype = 4 THEN 'Доп.образование'\n"
                        + "           ELSE 'Неизвестно' END AS \"orgType\",\n"
                        + "       co.shortaddress AS \"address\", \n"
                        + "       childs.relation AS \"relation\", \n"
                        + "       childs.islegalrepresent AS \"isLegalRepresent\" \n"
                        + "FROM cf_clients AS guardian\n"
                        + "         JOIN cf_orgs AS co ON guardian.idoforg = co.idoforg\n"
                        + "         JOIN cf_persons AS cp ON guardian.idofperson = cp.idofperson\n"
                        + "         JOIN cf_clientgroups cc ON guardian.idoforg = cc.idoforg AND guardian.idofclientgroup = cc.idofclientgroup\n"
                        + "         JOIN cf_client_guardian AS childs ON guardian.idofclient = childs.idofguardian\n"
                        + "         JOIN cf_clients AS child ON child.idofclient = childs.idofchildren\n"
                        + "WHERE child.contractid = :contractId"
        )
})
@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "forClientResponseDTO",
            attributeNodes = {
                    @NamedAttributeNode("discounts"),
                    @NamedAttributeNode("org"),
                    @NamedAttributeNode("clientGroup"),
                    @NamedAttributeNode("person"),
                    @NamedAttributeNode("preorderFlag")
            }
    )
})
public class Client {
    @Id
    @Column(name = "idofclient")
    private Long idOfClient;

    @Column(name = "contractid")
    private Long contractId;

    @Column(name = "meshguid")
    private String meshGuid;

    @Column(name = "agetypegroup")
    private String ageGroup;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "gender")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    @Column(name = "specialmenu")
    private Integer specialMenu;

    @Column(name = "limits")
    private Long limits;

    @ManyToOne
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @ManyToMany
    @JoinTable(
            name = "cf_clients_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofclient"),
            inverseJoinColumns = @JoinColumn(name = "idofcategorydiscount")
    )
    private List<CategoryDiscount> discounts;

    @OneToMany
    @JoinColumn(name = "idofchildren")
    private Set<ClientGuardian> guardians;

    @OneToMany
    @JoinColumn(name = "idofguardian")
    private Set<ClientGuardian> childrens;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "idofclientgroup"),
            @JoinColumn(name = "idoforg")
    })
    private ClientGroup clientGroup;

    @OneToOne
    @JoinColumn(name = "idofperson")
    private Person person;

    @OneToOne(mappedBy = "client")
    private PreorderFlag preorderFlag;

    public Client() {
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public List<CategoryDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<CategoryDiscount> discounts) {
        this.discounts = discounts;
    }

    public Set<ClientGuardian> getGuardians() {
        return guardians;
    }

    public void setGuardians(Set<ClientGuardian> guardians) {
        this.guardians = guardians;
    }

    public Set<ClientGuardian> getChildrens() {
        return childrens;
    }

    public void setChildrens(Set<ClientGuardian> childrens) {
        this.childrens = childrens;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(ClientGroup clientGroup) {
        this.clientGroup = clientGroup;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Integer specialMenu) {
        this.specialMenu = specialMenu;
    }

    public Long getLimits() {
        return limits;
    }

    public void setLimits(Long limits) {
        this.limits = limits;
    }

    public PreorderFlag getPreorderFlag() {
        return preorderFlag;
    }

    public void setPreorderFlag(PreorderFlag preorderFlag) {
        this.preorderFlag = preorderFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equals(idOfClient, client.idOfClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClient);
    }
}
