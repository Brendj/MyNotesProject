
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>Java class for PurchaseExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PurchaseExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E" type="{}PurchaseElementExt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Sum" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="SocDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="TrdDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Donation" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ByCash" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ByCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferSubBalanceExt", propOrder = {
    "e"
})
public class TransferSubBalanceExt {

    @XmlElement(name = "E")
    protected List<PurchaseElementExt> e;
    //@XmlAttribute(name = "CreateTime")
    //@XmlSchemaType(name = "CreateTime")
    //protected XMLGregorianCalendar createTime;
    @XmlAttribute(name = "CreateTime")
    @XmlSchemaType(name = "dateTime")
    private Date createTime;
    @XmlAttribute(name = "balanceBenefactor")
    protected Long balanceBenefactor;
    @XmlAttribute(name = "balanceBeneficiary")
    protected Long balanceBeneficiary;
    @XmlAttribute(name = "transferSum")
    protected Long transferSum;

    public List<PurchaseElementExt> getE() {
        if (e == null) {
            e = new ArrayList<PurchaseElementExt>();
        }
        return this.e;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getBalanceBenefactor() {
        return balanceBenefactor;
    }

    public void setBalanceBenefactor(Long balanceBenefactor) {
        this.balanceBenefactor = balanceBenefactor;
    }

    public Long getBalanceBeneficiary() {
        return balanceBeneficiary;
    }

    public void setBalanceBeneficiary(Long balanceBeneficiary) {
        this.balanceBeneficiary = balanceBeneficiary;
    }

    public Long getTransferSum() {
        return transferSum;
    }

    public void setTransferSum(Long transferSum) {
        this.transferSum = transferSum;
    }
}
