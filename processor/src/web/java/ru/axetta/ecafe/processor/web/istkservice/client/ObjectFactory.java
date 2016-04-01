
package ru.axetta.ecafe.processor.web.istkservice.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.axetta.ecafe.processor.web.istkservice.client package. 
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

    private final static QName _SaveSchoolsGamePermissionsResponse_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "saveSchoolsGamePermissionsResponse");
    private final static QName _PlainSchool_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "plainSchool");
    private final static QName _SchoolListResult_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "schoolListResult");
    private final static QName _GetSchoolsResponse_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "getSchoolsResponse");
    private final static QName _BaseResult_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "baseResult");
    private final static QName _SaveSchoolsGamePermissions_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "saveSchoolsGamePermissions");
    private final static QName _GetSchools_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "getSchools");
    private final static QName _Pair_QNAME = new QName("http://soap.services.school.istk.axetta.ru/", "pair");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.axetta.ecafe.processor.web.istkservice.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PlainSchool }
     * 
     */
    public PlainSchool createPlainSchool() {
        return new PlainSchool();
    }

    /**
     * Create an instance of {@link GetSchools }
     * 
     */
    public GetSchools createGetSchools() {
        return new GetSchools();
    }

    /**
     * Create an instance of {@link GetSchoolsResponse }
     * 
     */
    public GetSchoolsResponse createGetSchoolsResponse() {
        return new GetSchoolsResponse();
    }

    /**
     * Create an instance of {@link BaseResult }
     * 
     */
    public BaseResult createBaseResult() {
        return new BaseResult();
    }

    /**
     * Create an instance of {@link SchoolListResult.Districts }
     * 
     */
    public SchoolListResult.Districts createSchoolListResultDistricts() {
        return new SchoolListResult.Districts();
    }

    /**
     * Create an instance of {@link Pair }
     * 
     */
    public Pair createPair() {
        return new Pair();
    }

    /**
     * Create an instance of {@link SchoolListResult.Areas }
     * 
     */
    public SchoolListResult.Areas createSchoolListResultAreas() {
        return new SchoolListResult.Areas();
    }

    /**
     * Create an instance of {@link SchoolListResult }
     * 
     */
    public SchoolListResult createSchoolListResult() {
        return new SchoolListResult();
    }

    /**
     * Create an instance of {@link SaveSchoolsGamePermissions }
     * 
     */
    public SaveSchoolsGamePermissions createSaveSchoolsGamePermissions() {
        return new SaveSchoolsGamePermissions();
    }

    /**
     * Create an instance of {@link SchoolListResult.Areas.Entry }
     * 
     */
    public SchoolListResult.Areas.Entry createSchoolListResultAreasEntry() {
        return new SchoolListResult.Areas.Entry();
    }

    /**
     * Create an instance of {@link SchoolListResult.Districts.Entry }
     * 
     */
    public SchoolListResult.Districts.Entry createSchoolListResultDistrictsEntry() {
        return new SchoolListResult.Districts.Entry();
    }

    /**
     * Create an instance of {@link SaveSchoolsGamePermissionsResponse }
     * 
     */
    public SaveSchoolsGamePermissionsResponse createSaveSchoolsGamePermissionsResponse() {
        return new SaveSchoolsGamePermissionsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveSchoolsGamePermissionsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "saveSchoolsGamePermissionsResponse")
    public JAXBElement<SaveSchoolsGamePermissionsResponse> createSaveSchoolsGamePermissionsResponse(SaveSchoolsGamePermissionsResponse value) {
        return new JAXBElement<SaveSchoolsGamePermissionsResponse>(_SaveSchoolsGamePermissionsResponse_QNAME, SaveSchoolsGamePermissionsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PlainSchool }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "plainSchool")
    public JAXBElement<PlainSchool> createPlainSchool(PlainSchool value) {
        return new JAXBElement<PlainSchool>(_PlainSchool_QNAME, PlainSchool.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SchoolListResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "schoolListResult")
    public JAXBElement<SchoolListResult> createSchoolListResult(SchoolListResult value) {
        return new JAXBElement<SchoolListResult>(_SchoolListResult_QNAME, SchoolListResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSchoolsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "getSchoolsResponse")
    public JAXBElement<GetSchoolsResponse> createGetSchoolsResponse(GetSchoolsResponse value) {
        return new JAXBElement<GetSchoolsResponse>(_GetSchoolsResponse_QNAME, GetSchoolsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "baseResult")
    public JAXBElement<BaseResult> createBaseResult(BaseResult value) {
        return new JAXBElement<BaseResult>(_BaseResult_QNAME, BaseResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveSchoolsGamePermissions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "saveSchoolsGamePermissions")
    public JAXBElement<SaveSchoolsGamePermissions> createSaveSchoolsGamePermissions(SaveSchoolsGamePermissions value) {
        return new JAXBElement<SaveSchoolsGamePermissions>(_SaveSchoolsGamePermissions_QNAME, SaveSchoolsGamePermissions.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSchools }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "getSchools")
    public JAXBElement<GetSchools> createGetSchools(GetSchools value) {
        return new JAXBElement<GetSchools>(_GetSchools_QNAME, GetSchools.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Pair }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.services.school.istk.axetta.ru/", name = "pair")
    public JAXBElement<Pair> createPair(Pair value) {
        return new JAXBElement<Pair>(_Pair_QNAME, Pair.class, null, value);
    }

}
