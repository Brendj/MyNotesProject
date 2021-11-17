/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CertificateExpiryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CertificateExpiryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="expireDays" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateExpiryType", namespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.2")
public class CertificateExpiryType {

    @XmlAttribute(name = "expireDays")
    protected Integer expireDays;

    /**
     * Gets the value of the expireDays property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getExpireDays() {
        return expireDays;
    }

    /**
     * Sets the value of the expireDays property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setExpireDays(Integer value) {
        this.expireDays = value;
    }

}