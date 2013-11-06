
package generated.registry.manual_synch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for proceedRegitryChangeItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="proceedRegitryChangeItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="changesList" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="operation" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="fullNameValidation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proceedRegitryChangeItem", propOrder = {
    "changesList",
    "operation",
    "fullNameValidation"
})
public class ProceedRegitryChangeItem {

    @XmlElement(type = Long.class)
    protected List<Long> changesList;
    protected int operation;
    protected boolean fullNameValidation;

    /**
     * Gets the value of the changesList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the changesList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChangesList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getChangesList() {
        if (changesList == null) {
            changesList = new ArrayList<Long>();
        }
        return this.changesList;
    }

    /**
     * Gets the value of the operation property.
     * 
     */
    public int getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     */
    public void setOperation(int value) {
        this.operation = value;
    }

    /**
     * Gets the value of the fullNameValidation property.
     * 
     */
    public boolean isFullNameValidation() {
        return fullNameValidation;
    }

    /**
     * Sets the value of the fullNameValidation property.
     * 
     */
    public void setFullNameValidation(boolean value) {
        this.fullNameValidation = value;
    }

}
