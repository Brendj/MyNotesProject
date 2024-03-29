
package generated.opc.ru.msk.schemas.uec.transactionsynchronization.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import generated.opc.ru.msk.schemas.uec.transaction.v1.ErrorListType;
import generated.opc.ru.msk.schemas.uec.transaction.v1.TransactionListType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.msk.schemas.uec.transactionsynchronization.v1 package. 
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

    private final static QName _StoreTransactionsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "storeTransactionsResponse");
    private final static QName _StoreTagsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "storeTagsResponse");
    private final static QName _GetTransactionsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "getTransactionsResponse");
    private final static QName _StoreTransactionsRequest_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "storeTransactionsRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.msk.schemas.uec.transactionsynchronization.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.Tags }
     * 
     */
    public GetTransactionsRequest.Tags createGetTransactionsRequestTags() {
        return new GetTransactionsRequest.Tags();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes }
     * 
     */
    public GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes createGetTransactionsRequestTransactionTypesExcludeTransactionTypes() {
        return new GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes();
    }

    /**
     * Create an instance of {@link StoreTagsRequest.RemoveTag }
     * 
     */
    public StoreTagsRequest.RemoveTag createStoreTagsRequestRemoveTag() {
        return new StoreTagsRequest.RemoveTag();
    }

    /**
     * Create an instance of {@link StoreTagsRequest.AddTag }
     * 
     */
    public StoreTagsRequest.AddTag createStoreTagsRequestAddTag() {
        return new StoreTagsRequest.AddTag();
    }

    /**
     * Create an instance of {@link StoreTagsRequest }
     * 
     */
    public StoreTagsRequest createStoreTagsRequest() {
        return new StoreTagsRequest();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes }
     * 
     */
    public GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes createGetTransactionsRequestTransactionTypesIncludeTransactionTypes() {
        return new GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.TransactionTypes }
     * 
     */
    public GetTransactionsRequest.TransactionTypes createGetTransactionsRequestTransactionTypes() {
        return new GetTransactionsRequest.TransactionTypes();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest }
     * 
     */
    public GetTransactionsRequest createGetTransactionsRequest() {
        return new GetTransactionsRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "storeTransactionsResponse")
    public JAXBElement<ErrorListType> createStoreTransactionsResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreTransactionsResponse_QNAME, ErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "storeTagsResponse")
    public JAXBElement<ErrorListType> createStoreTagsResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreTagsResponse_QNAME, ErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "getTransactionsResponse")
    public JAXBElement<TransactionListType> createGetTransactionsResponse(TransactionListType value) {
        return new JAXBElement<TransactionListType>(_GetTransactionsResponse_QNAME, TransactionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "storeTransactionsRequest")
    public JAXBElement<TransactionListType> createStoreTransactionsRequest(TransactionListType value) {
        return new JAXBElement<TransactionListType>(_StoreTransactionsRequest_QNAME, TransactionListType.class, null, value);
    }

}
