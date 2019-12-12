
/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.OrganizationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientSummaryBase")
public class ClientSummaryBase {

    @XmlAttribute(name = "ContractId")
    protected Long contractId;
    @XmlAttribute(name = "Balance")
    protected Long balance;
    @XmlAttribute(name = "FirstName")
    protected String firstName;
    @XmlAttribute(name = "LastName")
    protected String lastName;
    @XmlAttribute(name = "MiddleName")
    protected String middleName;
    @XmlAttribute(name = "Grade")
    protected String grade;
    @XmlAttribute(name = "OfficialName")
    protected String officialName;
    @XmlAttribute(name = "MobilePhone")
    protected String mobilePhone;
    @XmlAttribute(name = "OrgType")
    protected String orgType;
    @XmlAttribute(name = "OrgId")
    protected Long orgId;
    @XmlAttribute(name = "isInside")
    private Integer isInside;
    @XmlAttribute(name = "GUID")
    protected String guid;
    @XmlAttribute(name = "SpecialMenu")
    private Integer specialMenu;
    @XmlAttribute(name = "InformedSpecialMenu")
    private Integer informedSpecialMenu;

    @XmlAttribute(name = "GuardianCreatedWhere")
    protected Integer guardianCreatedWhere;
    @XmlAttribute(name = "GroupPredefined")
    private Integer groupPredefined;
    @XmlAttribute(name = "Gender")
    private Integer gender;
    @XmlAttribute(name = "PreorderAllowed")
    private Integer preorderAllowed;
    /**
     *
     * Gets the value of the contractId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getContractId() {
        return contractId;
    }

    /**
     * Sets the value of the contractId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setContractId(Long value) {
        this.contractId = value;
    }

    /**
     * Gets the value of the balance property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBalance(Long value) {
        this.balance = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the grade property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the value of the grade property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrade(String value) {
        this.grade = value;
    }

    /**
     * Gets the value of the officialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfficialName() {
        return officialName;
    }

    /**
     * Sets the value of the officialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfficialName(String value) {
        this.officialName = value;
    }

    /**
     * Gets the value of the mobilePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Sets the value of the mobilePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhone(String value) {
        this.mobilePhone = value;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (lastName != null && lastName.length() > 0) {
            sb.append(lastName);
        }
        if (firstName != null && firstName.length() > 0) {
            sb.append(" ").append(firstName);
        }
        if (middleName != null && middleName.length() > 0) {
            sb.append(" ").append(middleName);
        }
        return sb.toString().trim();
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(OrganizationType orgType) {
        if (OrganizationType.KINDERGARTEN.equals(orgType) ){
            this.orgType = "ch";
        }else if(OrganizationType.SCHOOL.equals(orgType)){
            this.orgType = "sc";
        }else if (OrganizationType.PROFESSIONAL.equals(orgType)){
            this.orgType = "st";
        }else if (OrganizationType.SUPPLIER.equals(orgType)){
            this.orgType = "su";
        }
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getGuardianCreatedWhere() {
        return guardianCreatedWhere;
    }

    public void setGuardianCreatedWhere(Integer guardianCreatedWhere) {
        this.guardianCreatedWhere = guardianCreatedWhere;
    }

    public Integer getIsInside() {
        return isInside;
    }

    public void setIsInside(Integer isInside) {
        this.isInside = isInside;
    }

    public Integer getGroupPredefined() {
        return groupPredefined;
    }

    public void setGroupPredefined(Integer groupPredefined) {
        this.groupPredefined = groupPredefined;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Integer specialMenu) {
        this.specialMenu = specialMenu;
    }

    public Integer getInformedSpecialMenu() {
        return informedSpecialMenu;
    }

    public void setInformedSpecialMenu(Integer informedSpecialMenu) {
        this.informedSpecialMenu = informedSpecialMenu;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getPreorderAllowed() {
        return preorderAllowed;
    }

    public void setPreorderAllowed(Integer preorderAllowed) {
        this.preorderAllowed = preorderAllowed;
    }
}
