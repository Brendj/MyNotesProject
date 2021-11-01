
package generated.emp_events;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventFilter_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventFilter_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="persons" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="person" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice>
 *                             &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="MSISDN" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/choice>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="groups" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="options" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="option" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="rule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="option_group" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="option" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="rule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="AND" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventFilter_Type", propOrder = {
    "persons",
    "groups",
    "options",
    "optionGroup"
})
public class EventFilterType {

    protected EventFilterType.Persons persons;
    protected EventFilterType.Groups groups;
    protected EventFilterType.Options options;
    @XmlElement(name = "option_group")
    protected EventFilterType.OptionGroup optionGroup;
    @XmlAttribute(name = "operator")
    protected OperatorType operator;

    /**
     * Gets the value of the persons property.
     * 
     * @return
     *     possible object is
     *     {@link EventFilterType.Persons }
     *     
     */
    public EventFilterType.Persons getPersons() {
        return persons;
    }

    /**
     * Sets the value of the persons property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventFilterType.Persons }
     *     
     */
    public void setPersons(EventFilterType.Persons value) {
        this.persons = value;
    }

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link EventFilterType.Groups }
     *     
     */
    public EventFilterType.Groups getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventFilterType.Groups }
     *     
     */
    public void setGroups(EventFilterType.Groups value) {
        this.groups = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link EventFilterType.Options }
     *     
     */
    public EventFilterType.Options getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventFilterType.Options }
     *     
     */
    public void setOptions(EventFilterType.Options value) {
        this.options = value;
    }

    /**
     * Gets the value of the optionGroup property.
     * 
     * @return
     *     possible object is
     *     {@link EventFilterType.OptionGroup }
     *     
     */
    public EventFilterType.OptionGroup getOptionGroup() {
        return optionGroup;
    }

    /**
     * Sets the value of the optionGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventFilterType.OptionGroup }
     *     
     */
    public void setOptionGroup(EventFilterType.OptionGroup value) {
        this.optionGroup = value;
    }

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorType }
     *     
     */
    public OperatorType getOperator() {
        if (operator == null) {
            return OperatorType.AND;
        } else {
            return operator;
        }
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorType }
     *     
     */
    public void setOperator(OperatorType value) {
        this.operator = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "groupId"
    })
    public static class Groups {

        @XmlElement(type = Integer.class)
        protected List<Integer> groupId;
        @XmlAttribute(name = "operator")
        protected OperatorType operator;

        /**
         * Gets the value of the groupId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the groupId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGroupId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Integer }
         * 
         * 
         */
        public List<Integer> getGroupId() {
            if (groupId == null) {
                groupId = new ArrayList<Integer>();
            }
            return this.groupId;
        }

        /**
         * Gets the value of the operator property.
         * 
         * @return
         *     possible object is
         *     {@link OperatorType }
         *     
         */
        public OperatorType getOperator() {
            if (operator == null) {
                return OperatorType.OR;
            } else {
                return operator;
            }
        }

        /**
         * Sets the value of the operator property.
         * 
         * @param value
         *     allowed object is
         *     {@link OperatorType }
         *     
         */
        public void setOperator(OperatorType value) {
            this.operator = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="option" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="rule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "option"
    })
    public static class OptionGroup {

        @XmlElement(required = true)
        protected List<EventFilterType.OptionGroup.Option> option;
        @XmlAttribute(name = "operator")
        protected OperatorType operator;

        /**
         * Gets the value of the option property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the option property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOption().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EventFilterType.OptionGroup.Option }
         * 
         * 
         */
        public List<EventFilterType.OptionGroup.Option> getOption() {
            if (option == null) {
                option = new ArrayList<EventFilterType.OptionGroup.Option>();
            }
            return this.option;
        }

        /**
         * Gets the value of the operator property.
         * 
         * @return
         *     possible object is
         *     {@link OperatorType }
         *     
         */
        public OperatorType getOperator() {
            if (operator == null) {
                return OperatorType.OR;
            } else {
                return operator;
            }
        }

        /**
         * Sets the value of the operator property.
         * 
         * @param value
         *     allowed object is
         *     {@link OperatorType }
         *     
         */
        public void setOperator(OperatorType value) {
            this.operator = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="rule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "type",
            "value",
            "rule"
        })
        public static class Option {

            @XmlElement(required = true)
            protected String type;
            @XmlElement(required = true)
            protected String value;
            protected String rule;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the rule property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRule() {
                return rule;
            }

            /**
             * Sets the value of the rule property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRule(String value) {
                this.rule = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="option" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="rule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "option"
    })
    public static class Options {

        @XmlElement(required = true)
        protected List<EventFilterType.Options.Option> option;
        @XmlAttribute(name = "operator")
        protected OperatorType operator;

        /**
         * Gets the value of the option property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the option property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOption().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EventFilterType.Options.Option }
         * 
         * 
         */
        public List<EventFilterType.Options.Option> getOption() {
            if (option == null) {
                option = new ArrayList<EventFilterType.Options.Option>();
            }
            return this.option;
        }

        /**
         * Gets the value of the operator property.
         * 
         * @return
         *     possible object is
         *     {@link OperatorType }
         *     
         */
        public OperatorType getOperator() {
            if (operator == null) {
                return OperatorType.OR;
            } else {
                return operator;
            }
        }

        /**
         * Sets the value of the operator property.
         * 
         * @param value
         *     allowed object is
         *     {@link OperatorType }
         *     
         */
        public void setOperator(OperatorType value) {
            this.operator = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="rule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "type",
            "value",
            "rule"
        })
        public static class Option {

            @XmlElement(required = true)
            protected String type;
            @XmlElement(required = true)
            protected String value;
            protected String rule;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the rule property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRule() {
                return rule;
            }

            /**
             * Sets the value of the rule property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRule(String value) {
                this.rule = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="person" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice>
     *                   &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="MSISDN" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/choice>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="operator" type="{urn://subscription.api.emp.altarix.ru}OperatorType" default="OR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "person"
    })
    public static class Persons {

        @XmlElement(required = true)
        protected List<EventFilterType.Persons.Person> person;
        @XmlAttribute(name = "operator")
        protected OperatorType operator;

        /**
         * Gets the value of the person property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the person property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPerson().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EventFilterType.Persons.Person }
         * 
         * 
         */
        public List<EventFilterType.Persons.Person> getPerson() {
            if (person == null) {
                person = new ArrayList<EventFilterType.Persons.Person>();
            }
            return this.person;
        }

        /**
         * Gets the value of the operator property.
         * 
         * @return
         *     possible object is
         *     {@link OperatorType }
         *     
         */
        public OperatorType getOperator() {
            if (operator == null) {
                return OperatorType.OR;
            } else {
                return operator;
            }
        }

        /**
         * Sets the value of the operator property.
         * 
         * @param value
         *     allowed object is
         *     {@link OperatorType }
         *     
         */
        public void setOperator(OperatorType value) {
            this.operator = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;choice>
         *         &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="MSISDN" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/choice>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "ssoid",
            "citizenId",
            "msisdn",
            "email"
        })
        public static class Person {

            @XmlElement(name = "SSOID")
            protected String ssoid;
            protected Integer citizenId;
            @XmlElement(name = "MSISDN")
            protected Long msisdn;
            protected String email;

            /**
             * Gets the value of the ssoid property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSSOID() {
                return ssoid;
            }

            /**
             * Sets the value of the ssoid property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSSOID(String value) {
                this.ssoid = value;
            }

            /**
             * Gets the value of the citizenId property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getCitizenId() {
                return citizenId;
            }

            /**
             * Sets the value of the citizenId property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setCitizenId(Integer value) {
                this.citizenId = value;
            }

            /**
             * Gets the value of the msisdn property.
             * 
             * @return
             *     possible object is
             *     {@link Long }
             *     
             */
            public Long getMSISDN() {
                return msisdn;
            }

            /**
             * Sets the value of the msisdn property.
             * 
             * @param value
             *     allowed object is
             *     {@link Long }
             *     
             */
            public void setMSISDN(Long value) {
                this.msisdn = value;
            }

            /**
             * Gets the value of the email property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEmail() {
                return email;
            }

            /**
             * Sets the value of the email property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEmail(String value) {
                this.email = value;
            }

        }

    }

}
