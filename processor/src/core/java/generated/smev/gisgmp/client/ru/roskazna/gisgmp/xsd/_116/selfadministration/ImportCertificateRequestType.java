
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.selfadministration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * ��������/���������� �����������
 * 
 * <p>Java class for ImportCertificateRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImportCertificateRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestEntry" type="{http://roskazna.ru/gisgmp/xsd/116/SelfAdministration}RequestEntryType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImportCertificateRequestType", propOrder = {
    "requestEntry"
})
public class ImportCertificateRequestType {

    @XmlElement(name = "RequestEntry", required = true)
    protected List<RequestEntryType> requestEntry;

    /**
     * Gets the value of the requestEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestEntryType }
     * 
     * 
     */
    public List<RequestEntryType> getRequestEntry() {
        if (requestEntry == null) {
            requestEntry = new ArrayList<RequestEntryType>();
        }
        return this.requestEntry;
    }

}
