
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Catalog}DescriptionParameter_Type">
 *       &lt;sequence>
 *         &lt;element name="Field" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}DescriptionField_Type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "field"
})
@XmlRootElement(name = "DescriptionComplexParameter")
public class DescriptionComplexParameter
    extends DescriptionParameterType
{

    @XmlElement(name = "Field", required = true)
    protected List<DescriptionFieldType> field;

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionFieldType }
     * 
     * 
     */
    public List<DescriptionFieldType> getField() {
        if (field == null) {
            field = new ArrayList<DescriptionFieldType>();
        }
        return this.field;
    }

}