/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.cards;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Date;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.*;

/**
 * User: shamil
 * Date: 30.04.15
 * Time: 10:57
 */
public class CardsOperationsRegistryItem {

    public static final String SYNC_NAME = "CO";


    private long idOfOperation;
    private long cardNo;
    private int type;
    private Date operationDate;
    private Long idOfClient;
    private Long globalId;
    private String staffGuid;
    private Date validDate;
    private String comment;
    private Boolean isTemp;
    private String requestGuid;

    public CardsOperationsRegistryItem(long idOfOperation, long cardNo, int type, Date operationDate, Long idOfClient,
            Long globalId, String staffGuid, Date validDate, String comment, Boolean isTemp, String requestGuid) {
        this.idOfOperation = idOfOperation;
        this.cardNo = cardNo;
        this.type = type;
        this.operationDate = operationDate;
        this.idOfClient = idOfClient;
        this.globalId = globalId;
        this.staffGuid = staffGuid;
        this.validDate = validDate;
        this.comment = comment;
        this.isTemp = isTemp;
        this.requestGuid = requestGuid;
    }

    public static CardsOperationsRegistryItem build(Node CardsOperationRegistry, LoadContext loadContext) throws Exception {
        NamedNodeMap namedNodeMap = CardsOperationRegistry.getAttributes();

        Long idOfOperation = getLongValueNullSafe(namedNodeMap, "IdOfOperation");
        Long cardNo = getLongValueNullSafe(namedNodeMap, "CardNo");
        int type = getIntValue(namedNodeMap, "Type");
        Date operationDate = loadContext.getTimeFormat().parse(
                namedNodeMap.getNamedItem("OperationDate").getTextContent());
        Long idOfClient = getLongValueNullSafe(namedNodeMap, "IdOfClient");
        Long globalId = getLongValueNullSafe(namedNodeMap, "GlobalId");
        String staffGuid = getStringValueNullSafe(namedNodeMap, "StaffGuid");
        Date validDate = null;
        if(namedNodeMap.getNamedItem("ValidDate") != null){
             validDate = loadContext.getTimeFormat().parse(namedNodeMap.getNamedItem("ValidDate").getTextContent());
        }
        String comment = getStringValueNullSafe(namedNodeMap, "Comment");
        Boolean isTemp = false;
        if("1".equals(getStringValueNullSafe(namedNodeMap, "IsTemp")) ){
            isTemp = true;
        }
        String requestGuid = getStringValueNullSafe(namedNodeMap, "GuidRequest");
        return new CardsOperationsRegistryItem(idOfOperation,cardNo,type,operationDate,idOfClient,globalId,staffGuid,
                validDate, comment, isTemp, requestGuid);
    }

    public long getIdOfOperation() {
        return idOfOperation;
    }

    public long getCardNo() {
        return cardNo;
    }

    public int getType() {
        return type;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public String getStaffGuid() {
        return staffGuid;
    }

    public Date getValidDate() {
        return validDate;
    }

    public String getComment() {
        return comment;
    }

    public Boolean getTemp() {
        return isTemp;
    }

    public String getRequestGuid() {
        return requestGuid;
    }
}
