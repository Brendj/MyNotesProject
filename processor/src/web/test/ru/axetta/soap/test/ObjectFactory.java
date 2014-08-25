package ru.axetta.soap.test;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.EnterEventWithRepItem;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.EnterEventWithRepList;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.axetta.soap.test package. 
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

    private final static QName _ProcessResponse_QNAME = new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/", "processResponse");
    private final static QName _Process_QNAME = new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/", "process");
    private final static QName _Exception_QNAME = new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/", "Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.axetta.soap.test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PurchaseElementExt }
     * 
     */
    public PurchaseElementExt createPurchaseElementExt() {
        return new PurchaseElementExt();
    }

    /**
     * Create an instance of {@link PaymentList }
     * 
     */
    public PaymentList createPaymentList() {
        return new PaymentList();
    }

    /**
     * Create an instance of {@link ClientSummary }
     * 
     */
    public ClientSummary createClientSummary() {
        return new ClientSummary();
    }

    /**
     * Create an instance of {@link Payment }
     * 
     */
    public Payment createPayment() {
        return new Payment();
    }

    /**
     * Create an instance of {@link PurchaseList }
     * 
     */
    public PurchaseList createPurchaseList() {
        return new PurchaseList();
    }

    /**
     * Create an instance of {@link CardItem }
     * 
     */
    public CardItem createCardItem() {
        return new CardItem();
    }

    /**
     * Create an instance of {@link MenuDateItem }
     * 
     */
    public MenuDateItem createMenuDateItem() {
        return new MenuDateItem();
    }

    /**
     * Create an instance of {@link EnterEventList }
     * 
     */
    public EnterEventList createEnterEventList() {
        return new EnterEventList();
    }

    /**
     * Create an instance of {@link MenuDateItemExt }
     * 
     */
    public MenuDateItemExt createMenuDateItemExt() {
        return new MenuDateItemExt();
    }

    /**
     * Create an instance of {@link PurchaseListExt }
     * 
     */
    public PurchaseListExt createPurchaseListExt() {
        return new PurchaseListExt();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link MenuItem }
     * 
     */
    public MenuItem createMenuItem() {
        return new MenuItem();
    }

    /**
     * Create an instance of {@link PaymentResult }
     * 
     */
    public PaymentResult createPaymentResult() {
        return new PaymentResult();
    }

    /**
     * Create an instance of {@link ClientSummaryExt }
     * 
     */
    public ClientSummaryExt createClientSummaryExt() {
        return new ClientSummaryExt();
    }

    /**
     * Create an instance of {@link EnterEventWithRepList }
     *
     */
    public EnterEventWithRepList createEnterEventWithRepList() {
        return new EnterEventWithRepList();
    }

    /**
     * Create an instance of {@link MenuList }
     * 
     */
    public MenuList createMenuList() {
        return new MenuList();
    }

    /**
     * Create an instance of {@link ProcessResponse }
     * 
     */
    public ProcessResponse createProcessResponse() {
        return new ProcessResponse();
    }

    /**
     * Create an instance of {@link Purchase }
     * 
     */
    public Purchase createPurchase() {
        return new Purchase();
    }

    /**
     * Create an instance of {@link CardList }
     * 
     */
    public CardList createCardList() {
        return new CardList();
    }

    /**
     * Create an instance of {@link Data }
     * 
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link PurchaseExt }
     * 
     */
    public PurchaseExt createPurchaseExt() {
        return new PurchaseExt();
    }

    /**
     * Create an instance of {@link PurchaseElement }
     * 
     */
    public PurchaseElement createPurchaseElement() {
        return new PurchaseElement();
    }

    /**
     * Create an instance of {@link EnterEventItem }
     * 
     */
    public EnterEventItem createEnterEventItem() {
        return new EnterEventItem();
    }

    /**
     * Create an instance of {@link EnterEventItem }
     *
     */
    public EnterEventWithRepItem createEnterEventWithRepItem() {
        return new EnterEventWithRepItem();
    }

    /**
     * Create an instance of {@link MenuItemExt }
     * 
     */
    public MenuItemExt createMenuItemExt() {
        return new MenuItemExt();
    }

    /**
     * Create an instance of {@link MenuListExt }
     * 
     */
    public MenuListExt createMenuListExt() {
        return new MenuListExt();
    }

    /**
     * Create an instance of {@link Process }
     * 
     */
    public Process createProcess() {
        return new Process();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", name = "processResponse")
    public JAXBElement<ProcessResponse> createProcessResponse(ProcessResponse value) {
        return new JAXBElement<ProcessResponse>(_ProcessResponse_QNAME, ProcessResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Process }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", name = "process")
    public JAXBElement<Process> createProcess(Process value) {
        return new JAXBElement<Process>(_Process_QNAME, Process.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

}
