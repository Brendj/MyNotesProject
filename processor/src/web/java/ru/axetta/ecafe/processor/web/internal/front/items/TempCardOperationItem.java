package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.core.persistence.CardTempOperation;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.07.13
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class TempCardOperationItem {

    private Long idOfOperation;
    private Long idOfOrg;
    private Long idOfClient;
    private Long idOfVisitor;
    private Long idOfTempCard;
    private Date issueExpiryDate;
    private Date operationDate;
    private Integer operationType;

    public TempCardOperationItem(CardTempOperation cardTempOperation) {
        this.idOfOperation = cardTempOperation.getIdOfCardTempOperation();
        this.idOfOrg = cardTempOperation.getOrg().getIdOfOrg();
        if(cardTempOperation.getClient() !=null){
            this.idOfClient = cardTempOperation.getClient().getIdOfClient();
        }
        if(cardTempOperation.getVisitor() !=null){
            this.idOfVisitor = cardTempOperation.getVisitor().getIdOfVisitor();
        }
        if(cardTempOperation.getCardTemp() !=null){
            this.idOfTempCard = cardTempOperation.getCardTemp().getCardNo();
        }
        //this.issueExpiryDate = cardTempOperation.;
        this.operationDate = cardTempOperation.getOperationDate();
        this.operationType = cardTempOperation.getOperationType().ordinal();
   }

    public Long getIdOfOperation() {
        return idOfOperation;
    }

    public void setIdOfOperation(Long idOfOperation) {
        this.idOfOperation = idOfOperation;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
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

    public TempCardOperationItem() {}
}
