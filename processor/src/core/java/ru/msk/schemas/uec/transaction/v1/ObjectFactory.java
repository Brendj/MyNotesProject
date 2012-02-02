
package ru.msk.schemas.uec.transaction.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.msk.schemas.uec.transaction.v1 package. 
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

    private final static QName _TransactionDescription_QNAME = new QName("http://schemas.msk.ru/uec/transaction/v1", "transactionDescription");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.msk.schemas.uec.transaction.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TagType }
     * 
     */
    public TagType createTagType() {
        return new TagType();
    }

    /**
     * Create an instance of {@link OtherStatusesType.OtherStatus }
     * 
     */
    public OtherStatusesType.OtherStatus createOtherStatusesTypeOtherStatus() {
        return new OtherStatusesType.OtherStatus();
    }

    /**
     * Create an instance of {@link PaymentAdditionalInfoType }
     * 
     */
    public PaymentAdditionalInfoType createPaymentAdditionalInfoType() {
        return new PaymentAdditionalInfoType();
    }

    /**
     * Create an instance of {@link TransactionDescriptionType.AccountingDescription }
     * 
     */
    public TransactionDescriptionType.AccountingDescription createTransactionDescriptionTypeAccountingDescription() {
        return new TransactionDescriptionType.AccountingDescription();
    }

    /**
     * Create an instance of {@link TransactionDescriptionType }
     * 
     */
    public TransactionDescriptionType createTransactionDescriptionType() {
        return new TransactionDescriptionType();
    }

    /**
     * Create an instance of {@link TransactionListType }
     * 
     */
    public TransactionListType createTransactionListType() {
        return new TransactionListType();
    }

    /**
     * Create an instance of {@link OtherStatusesType }
     * 
     */
    public OtherStatusesType createOtherStatusesType() {
        return new OtherStatusesType();
    }

    /**
     * Create an instance of {@link TransactionIdDescriptionType }
     * 
     */
    public TransactionIdDescriptionType createTransactionIdDescriptionType() {
        return new TransactionIdDescriptionType();
    }

    /**
     * Create an instance of {@link RelationDescriptionType.PreviousTransaction }
     * 
     */
    public RelationDescriptionType.PreviousTransaction createRelationDescriptionTypePreviousTransaction() {
        return new RelationDescriptionType.PreviousTransaction();
    }

    /**
     * Create an instance of {@link AccountingDescriptionItemType.FinancialDescription }
     * 
     */
    public AccountingDescriptionItemType.FinancialDescription createAccountingDescriptionItemTypeFinancialDescription() {
        return new AccountingDescriptionItemType.FinancialDescription();
    }

    /**
     * Create an instance of {@link ErrorListType }
     * 
     */
    public ErrorListType createErrorListType() {
        return new ErrorListType();
    }

    /**
     * Create an instance of {@link TransactionDescriptionType.MacDescription }
     * 
     */
    public TransactionDescriptionType.MacDescription createTransactionDescriptionTypeMacDescription() {
        return new TransactionDescriptionType.MacDescription();
    }

    /**
     * Create an instance of {@link PaymentAdditionalInfoType.PaymentInstruction }
     * 
     */
    public PaymentAdditionalInfoType.PaymentInstruction createPaymentAdditionalInfoTypePaymentInstruction() {
        return new PaymentAdditionalInfoType.PaymentInstruction();
    }

    /**
     * Create an instance of {@link TransactionTypeDescriptionType }
     * 
     */
    public TransactionTypeDescriptionType createTransactionTypeDescriptionType() {
        return new TransactionTypeDescriptionType();
    }

    /**
     * Create an instance of {@link FinancialDescriptionItemType }
     * 
     */
    public FinancialDescriptionItemType createFinancialDescriptionItemType() {
        return new FinancialDescriptionItemType();
    }

    /**
     * Create an instance of {@link FinancialDescriptionItemType.PaymentInfo }
     * 
     */
    public FinancialDescriptionItemType.PaymentInfo createFinancialDescriptionItemTypePaymentInfo() {
        return new FinancialDescriptionItemType.PaymentInfo();
    }

    /**
     * Create an instance of {@link TransactionSourceDescriptionType }
     * 
     */
    public TransactionSourceDescriptionType createTransactionSourceDescriptionType() {
        return new TransactionSourceDescriptionType();
    }

    /**
     * Create an instance of {@link TransactionStatusDescriptionType }
     * 
     */
    public TransactionStatusDescriptionType createTransactionStatusDescriptionType() {
        return new TransactionStatusDescriptionType();
    }

    /**
     * Create an instance of {@link SAMMACDescriptionType }
     * 
     */
    public SAMMACDescriptionType createSAMMACDescriptionType() {
        return new SAMMACDescriptionType();
    }

    /**
     * Create an instance of {@link RelationDescriptionType }
     * 
     */
    public RelationDescriptionType createRelationDescriptionType() {
        return new RelationDescriptionType();
    }

    /**
     * Create an instance of {@link DeviceDescriptionType }
     * 
     */
    public DeviceDescriptionType createDeviceDescriptionType() {
        return new DeviceDescriptionType();
    }

    /**
     * Create an instance of {@link AccountingDescriptionItemType }
     * 
     */
    public AccountingDescriptionItemType createAccountingDescriptionItemType() {
        return new AccountingDescriptionItemType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionDescriptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/transaction/v1", name = "transactionDescription")
    public JAXBElement<TransactionDescriptionType> createTransactionDescription(TransactionDescriptionType value) {
        return new JAXBElement<TransactionDescriptionType>(_TransactionDescription_QNAME, TransactionDescriptionType.class, null, value);
    }

}
