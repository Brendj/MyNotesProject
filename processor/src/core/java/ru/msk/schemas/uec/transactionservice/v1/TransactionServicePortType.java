
package ru.msk.schemas.uec.transactionservice.v1;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import ru.msk.schemas.uec.transaction.v1.ErrorListType;
import ru.msk.schemas.uec.transaction.v1.TransactionListType;
import ru.msk.schemas.uec.transactionsynchronization.v1.GetTransactionsRequest;
import ru.msk.schemas.uec.transactionsynchronization.v1.StoreTagsRequest;

import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "TransactionServicePortType", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ru.msk.schemas.uec.transactionsynchronization.v1.ObjectFactory.class,
    ru.msk.schemas.uec.transaction.v1.ObjectFactory.class,
    iso.std.iso._20022.tech.xsd.pain_001_001.ObjectFactory.class,
    iso.std.iso._20022.tech.xsd.pain_008_001.ObjectFactory.class,
    ru.msk.schemas.uec.common.v1.ObjectFactory.class,
    ru.msk.schemas.uec.transactionservice.v1.ObjectFactory.class,
    ru.msk.schemas.uec.identification.v1.ObjectFactory.class
})
@InInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingInInterceptor")
@OutInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
public interface TransactionServicePortType {


    /**
     * Получение транзакций
     * 
     * @param parameters
     * @return
     *     returns ru.msk.schemas.uec.transaction.v1.TransactionListType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:getTransactions")
    @WebResult(name = "getTransactionsResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", partName = "transactionList")
    public TransactionListType getTransactions(
        @WebParam(name = "getTransactionsRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", partName = "parameters")
        GetTransactionsRequest parameters)
        throws FaultResponse
    ;

    /**
     * Сброс транзакций
     * 
     * @param parameters
     * @return
     *     returns ru.msk.schemas.uec.transaction.v1.ErrorListType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:storeTransactions")
    @WebResult(name = "storeTransactionsResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", partName = "errors")
    public ErrorListType storeTransactions(
        @WebParam(name = "storeTransactionsRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", partName = "parameters")
        TransactionListType parameters)
        throws FaultResponse
    ;

    /**
     * Получение транзакций по персоне
     * 
     * @param parameters
     * @return
     *     returns ru.msk.schemas.uec.transaction.v1.TransactionListType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:getPersonTransactions")
    @WebResult(name = "getPersonTransactionsResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "transactionList")
    public TransactionListType getPersonTransactions(
        @WebParam(name = "getPersonTransactionsRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "parameters")
        GetPersonTransactionsRequest parameters)
        throws FaultResponse
    ;

    /**
     * Изменение тэгов транзакции
     * 
     * @param parameter
     * @return
     *     returns ru.msk.schemas.uec.transaction.v1.ErrorListType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:storeTags")
    @WebResult(name = "storeTagsResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", partName = "parameter")
    public ErrorListType storeTags(
        @WebParam(name = "storeTagsRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", partName = "parameter")
        StoreTagsRequest parameter)
        throws FaultResponse
    ;

    /**
     * Запрос на сохранение списков рассылки
     * 
     * @param parameters
     * @return
     *     returns ru.msk.schemas.uec.transaction.v1.ErrorListType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:storeNotificationSubscribers")
    @WebResult(name = "storeNotificationSubscribersResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "errors")
    public ErrorListType storeNotificationSubscribers(
        @WebParam(name = "storeNotificationSubscribersRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "parameters")
        SubscribtionListType parameters)
        throws FaultResponse
    ;

    /**
     * Запрос на сохранение тарифов
     * 
     * @param storeTariffsRequest
     * @return
     *     returns ru.msk.schemas.uec.transaction.v1.ErrorListType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:storeTariffs")
    @WebResult(name = "storeTariffsResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "errors")
    public ErrorListType storeTariffs(
        @WebParam(name = "storeTariffsRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "storeTariffsRequest")
        StoreTariffsRequest storeTariffsRequest)
        throws FaultResponse
    ;

    /**
     * Запрос на получение тарификационных расчетов
     * 
     * @param parameters
     * @return
     *     returns ru.msk.schemas.uec.transactionservice.v1.GetBillsResponseType
     * @throws FaultResponse
     */
    @WebMethod(action = "urn:getBills")
    @WebResult(name = "getBillsResponse", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "billsList")
    public GetBillsResponseType getBills(
        @WebParam(name = "getBillsRequest", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", partName = "parameters")
        GetBillsRequestType parameters)
        throws FaultResponse
    ;

}
