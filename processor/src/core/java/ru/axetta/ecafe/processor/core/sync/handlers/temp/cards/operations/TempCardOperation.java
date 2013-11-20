package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class TempCardOperation {

    private final Long idOfOperation;
    private Long idOfOrg;
    private Long idOfClient;
    private Long idOfVisitor;
    private Long idOfTempCard;
    private Date issueExpiryDate;
    private Date operationDate;
    private Integer operationType;
    private String errorMessage;

    public static TempCardOperation build(Node itemNode, Long idOfOrg){
        Long idOfTempCard=null;
        Long idOfClient=null;
        Long idOfVisitor=null;
        Long idOfOperation=null;
        Date issueExpiryDate=null;
        Date operationDate=null;
        Integer operationType=null;
        String strIdOfOperation = XMLUtils.getAttributeValue(itemNode,"IdOfOperation");
        if(StringUtils.isNotEmpty(strIdOfOperation)){
            try {
                idOfOperation =  Long.parseLong(strIdOfOperation);
            } catch (NumberFormatException e){
                return new TempCardOperation(-1L,"NumberFormatException IdOfOperation not found");
            }
        } else {
            return new TempCardOperation(idOfOperation, "Attribute IdOfOperation not found");
        }
        String strIdOfTempCard = XMLUtils.getAttributeValue(itemNode,"IdOfTempCard");
        if(StringUtils.isNotEmpty(strIdOfTempCard)){
            try {
                idOfTempCard =  Long.parseLong(strIdOfTempCard);
            } catch (NumberFormatException e){
                return new TempCardOperation(idOfOperation, "NumberFormatException IdOfTempCard");
            }
        } else {
            return new TempCardOperation(idOfOperation, "Attribute IdOfTempCard not found");
        }
        if(idOfOrg==null){
            return new TempCardOperation(idOfOperation, "IdOfOrg is null");
        }
        String strIdOfClient = XMLUtils.getAttributeValue(itemNode,"IdOfClient");
        if(StringUtils.isNotEmpty(strIdOfClient)){
            try {
                idOfClient =  Long.parseLong(strIdOfClient);
            } catch (NumberFormatException e){
                return new TempCardOperation(idOfOperation, "NumberFormatException IdOfClient not found");
            }
        }
        String strIdOfVisitor = XMLUtils.getAttributeValue(itemNode,"IdOfVisitor");
        if(StringUtils.isNotEmpty(strIdOfVisitor)){
            try {
                idOfVisitor =  Long.parseLong(strIdOfVisitor);
            } catch (NumberFormatException e){
                return new TempCardOperation(idOfOperation, "NumberFormatException IdOfVisitor not found");
            }
        }
        String strOperationType = XMLUtils.getAttributeValue(itemNode,"OperationType");
        if(StringUtils.isNotEmpty(strOperationType)){
            try {
                operationType = Integer.parseInt(strOperationType);
            } catch (NumberFormatException e){
                return new TempCardOperation(idOfOperation, "NumberFormatException OperationType");
            }
        } else {
            return new TempCardOperation(idOfOperation, "Attribute OperationType not found");
        }
        String strIssueExpiryDate = XMLUtils.getAttributeValue(itemNode,"IssueExpiryDate");
        if(StringUtils.isNotEmpty(strIssueExpiryDate)){
            try {
                issueExpiryDate = CalendarUtils.parseFullDateTimeWithLocalTimeZone(strIssueExpiryDate);
            } catch (Exception e){
                return new TempCardOperation(idOfOperation, "Exception IssueExpiryDate");
            }
        }
        String strOperationDate = XMLUtils.getAttributeValue(itemNode,"OperationDate");
        if(StringUtils.isNotEmpty(strOperationDate)){
            try {
                operationDate = CalendarUtils.parseFullDateTimeWithLocalTimeZone(strOperationDate);
            } catch (Exception e){
                return new TempCardOperation(idOfOperation, "Exception OperationDate");
            }
        } else {
            return new TempCardOperation(idOfOperation, "Attribute OperationDate not found");
        }
        if(idOfClient==null && idOfVisitor==null && operationType!=0){
            return new TempCardOperation(idOfOperation, "IdOfClient and IdOfVisitor is null");
        }
        if(idOfClient!=null && idOfVisitor!=null){
            return new TempCardOperation(idOfOperation, "IdOfClient and IdOfVisitor is not null");
        }


        return new TempCardOperation(idOfOrg, idOfClient, idOfVisitor, idOfOperation, idOfTempCard,issueExpiryDate, operationDate, operationType);
    }

    private TempCardOperation(Long idOfOrg, Long idOfClient, Long idOfVisitor, Long idOfOperation, Long idOfTempCard,
            Date issueExpiryDate, Date operationDate, Integer operationType) {
        this.idOfOrg = idOfOrg;
        this.idOfClient = idOfClient;
        this.idOfVisitor = idOfVisitor;
        this.idOfOperation = idOfOperation;
        this.idOfTempCard = idOfTempCard;
        this.issueExpiryDate = issueExpiryDate;
        this.operationDate = operationDate;
        this.operationType = operationType;
    }

    private TempCardOperation(Long idOfOperation, String errorMessage) {
        this.idOfOperation = idOfOperation;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getIdOfOperation() {
        return idOfOperation;
    }

    public Long getIdOfTempCard() {
        return idOfTempCard;
    }

    public Date getIssueExpiryDate() {
        return issueExpiryDate;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

}
