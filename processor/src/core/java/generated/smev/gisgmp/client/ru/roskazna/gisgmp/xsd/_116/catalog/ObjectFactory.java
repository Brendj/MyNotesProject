
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp.xsd._116.catalog package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CatalogMsgError_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Catalog", "CatalogMsgError");
    private final static QName _ServiceCatalog_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Catalog", "ServiceCatalog");
    private final static QName _AllowedValues_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Catalog", "AllowedValues");
    private final static QName _ServiceCode_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Catalog", "ServiceCode");
    private final static QName _CatalogDeltaInqMsgRs_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Catalog", "CatalogDeltaInqMsgRs");
    private final static QName _Service_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Catalog", "Service");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp.xsd._116.catalog
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DescriptionSimpleParameter }
     * 
     */
    public DescriptionSimpleParameter createDescriptionSimpleParameter() {
        return new DescriptionSimpleParameter();
    }

    /**
     * Create an instance of {@link CatalogFullInqMsgRs }
     * 
     */
    public CatalogFullInqMsgRs createCatalogFullInqMsgRs() {
        return new CatalogFullInqMsgRs();
    }

    /**
     * Create an instance of {@link ServiceCategoryType }
     * 
     */
    public ServiceCategoryType createServiceCategoryType() {
        return new ServiceCategoryType();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link DescriptionComplexParameter }
     * 
     */
    public DescriptionComplexParameter createDescriptionComplexParameter() {
        return new DescriptionComplexParameter();
    }

    /**
     * Create an instance of {@link DescriptionParameterType }
     * 
     */
    public DescriptionParameterType createDescriptionParameterType() {
        return new DescriptionParameterType();
    }

    /**
     * Create an instance of {@link DescriptionFieldType }
     * 
     */
    public DescriptionFieldType createDescriptionFieldType() {
        return new DescriptionFieldType();
    }

    /**
     * Create an instance of {@link DescriptionParametersType }
     * 
     */
    public DescriptionParametersType createDescriptionParametersType() {
        return new DescriptionParametersType();
    }

    /**
     * Create an instance of {@link ServiceType.CurrenciesServices }
     * 
     */
    public ServiceType.CurrenciesServices createServiceTypeCurrenciesServices() {
        return new ServiceType.CurrenciesServices();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link ServiceType.Subservices }
     * 
     */
    public ServiceType.Subservices createServiceTypeSubservices() {
        return new ServiceType.Subservices();
    }

    /**
     * Create an instance of {@link ServiceCatalogType }
     * 
     */
    public ServiceCatalogType createServiceCatalogType() {
        return new ServiceCatalogType();
    }

    /**
     * Create an instance of {@link ServiceType.Regions }
     * 
     */
    public ServiceType.Regions createServiceTypeRegions() {
        return new ServiceType.Regions();
    }

    /**
     * Create an instance of {@link CatalogDeltaInqMsgRsType }
     * 
     */
    public CatalogDeltaInqMsgRsType createCatalogDeltaInqMsgRsType() {
        return new CatalogDeltaInqMsgRsType();
    }

    /**
     * Create an instance of {@link RegionType }
     * 
     */
    public RegionType createRegionType() {
        return new RegionType();
    }

    /**
     * Create an instance of {@link AllowedValuesType }
     * 
     */
    public AllowedValuesType createAllowedValuesType() {
        return new AllowedValuesType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ErrorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Catalog", name = "CatalogMsgError")
    public JAXBElement<ErrorType> createCatalogMsgError(ErrorType value) {
        return new JAXBElement<ErrorType>(_CatalogMsgError_QNAME, ErrorType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ServiceCatalogType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Catalog", name = "ServiceCatalog")
    public JAXBElement<ServiceCatalogType> createServiceCatalog(ServiceCatalogType value) {
        return new JAXBElement<ServiceCatalogType>(_ServiceCatalog_QNAME, ServiceCatalogType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link AllowedValuesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Catalog", name = "AllowedValues")
    public JAXBElement<AllowedValuesType> createAllowedValues(AllowedValuesType value) {
        return new JAXBElement<AllowedValuesType>(_AllowedValues_QNAME, AllowedValuesType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Catalog", name = "ServiceCode")
    public JAXBElement<String> createServiceCode(String value) {
        return new JAXBElement<String>(_ServiceCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link CatalogDeltaInqMsgRsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Catalog", name = "CatalogDeltaInqMsgRs")
    public JAXBElement<CatalogDeltaInqMsgRsType> createCatalogDeltaInqMsgRs(CatalogDeltaInqMsgRsType value) {
        return new JAXBElement<CatalogDeltaInqMsgRsType>(_CatalogDeltaInqMsgRs_QNAME, CatalogDeltaInqMsgRsType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ServiceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Catalog", name = "Service")
    public JAXBElement<ServiceType> createService(ServiceType value) {
        return new JAXBElement<ServiceType>(_Service_QNAME, ServiceType.class, null, value);
    }

}
