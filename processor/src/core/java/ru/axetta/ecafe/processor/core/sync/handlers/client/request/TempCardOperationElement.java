package ru.axetta.ecafe.processor.core.sync.handlers.client.request;

import ru.axetta.ecafe.processor.core.persistence.CardTempOperation;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 01.08.13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class TempCardOperationElement {

    private Long idOfOperation;
    private Long idOfClient;
    private Long idOfVisitor;
    private Long idOfTempCard;
    private String cardPrintedNo;
    private Date issueExpiryDate;
    private Date operationDate;
    private Integer operationType;

    private final static DateFormat dateOnlyFormat;
    static {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateOnlyFormat.setTimeZone(utcTimeZone);
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("TCO");
        element.setAttribute("IdOfOperation", Long.toString(this.idOfOperation));
        if(this.idOfClient !=null){
            element.setAttribute("IdOfClient", Long.toString(this.idOfClient));
        }
        if(this.idOfVisitor !=null){
            element.setAttribute("IdOfClient", Long.toString(this.idOfVisitor));
        }
        if(idOfTempCard !=null){
            element.setAttribute("IdOfTempCard", Long.toString(this.idOfTempCard));
        }
        if(issueExpiryDate !=null){
            String dateTime = CalendarUtils.toStringFullDateTimeWithUTCTimeZone(this.issueExpiryDate);
            element.setAttribute("IssueExpiryDate", dateTime);
            //element.setAttribute("IssueExpiryDate", dateOnlyFormat.format(this.issueExpiryDate));
        }
        if(operationDate !=null){
            String dateTime = CalendarUtils.toStringFullDateTimeWithUTCTimeZone(this.operationDate);
            element.setAttribute("OperationDate", dateTime);
            //element.setAttribute("OperationDate", dateOnlyFormat.format(this.operationDate));
        }
        element.setAttribute("OperationType", Integer.toString(this.operationType));
        return element;
    }

    public TempCardOperationElement(CardTempOperation cardTempOperation) {
        this.idOfOperation = cardTempOperation.getIdOfCardTempOperation();
        if(cardTempOperation.getClient() !=null){
            this.idOfClient = cardTempOperation.getClient().getIdOfClient();
        }
        if(cardTempOperation.getVisitor() !=null){
            this.idOfVisitor = cardTempOperation.getVisitor().getIdOfVisitor();
        }
        if(cardTempOperation.getCardTemp() !=null){
            this.idOfTempCard = cardTempOperation.getCardTemp().getCardNo();
            this.issueExpiryDate = cardTempOperation.getCardTemp().getValidDate();
            this.cardPrintedNo = cardTempOperation.getCardTemp().getCardPrintedNo();
        }
        this.operationDate = cardTempOperation.getOperationDate();
        this.operationType = cardTempOperation.getOperationType().ordinal();
    }

    public Long getIdOfOperation() {
        return idOfOperation;
    }

    public void setIdOfOperation(Long idOfOperation) {
        this.idOfOperation = idOfOperation;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public Long getIdOfTempCard() {
        return idOfTempCard;
    }

    public String getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(String cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public void setIdOfTempCard(Long idOfTempCard) {
        this.idOfTempCard = idOfTempCard;
    }

    public Date getIssueExpiryDate() {
        return issueExpiryDate;
    }

    public void setIssueExpiryDate(Date issueExpiryDate) {
        this.issueExpiryDate = issueExpiryDate;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

}
