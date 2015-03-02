package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import ru.axetta.ecafe.processor.core.persistence.AccountOperations;
import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Date;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.*;

/**
 * User: Shamil
 * Date: 19.02.15
 * Time: 22:13
 */
public class AccountOperationItem {
    public static final String SYNC_NAME = "AT";

    private long idOfOperation;
    private long date;
    private long idOfContract;
    private long value;
    private int type;
    private Long idOfOrder;
    private String staffGuid;
    private Long idOfPos;
    private Long idOfContragent;

    public AccountOperationItem(long idOfOperation, long date, long idOfContract, long value, int type, Long idOfOrder,
            String staffGuid, Long idOfPos, Long idOfContragent) {
        this.idOfOperation = idOfOperation;
        this.date = date;
        this.idOfContract = idOfContract;
        this.value = value;
        this.type = type;
        this.idOfOrder = idOfOrder;
        this.staffGuid = staffGuid;
        this.idOfPos = idOfPos;
        this.idOfContragent = idOfContragent;
    }

    public static AccountOperationItem build(Node paymentNode, LoadContext loadContext) throws Exception {
        NamedNodeMap namedNodeMap = paymentNode.getAttributes();

        Long idOfOperation = getLongValueNullSafe(namedNodeMap, "IdOfOperation");
        Date date = loadContext.getTimeFormat().parse(namedNodeMap.getNamedItem("Date").getTextContent());
        Long idOfContract = getLongValueNullSafe(namedNodeMap, "ContractId");
        long value = getLongValue(namedNodeMap, "Value");
        int type = getIntValue(namedNodeMap, "Type");
        Long idOfOrder = getLongValueNullSafe(namedNodeMap, "OrderId");
        String staffGuid = getStringValueNullSafe(namedNodeMap, "StaffGuid");
        Long idOfPOS = getLongValueNullSafe(namedNodeMap, "PosId");
        Long idOfContragent = getLongValueNullSafe(namedNodeMap, "ContragentId");

        return new AccountOperationItem(idOfOperation,date.getTime(),idOfContract,value,type,idOfOrder,staffGuid,idOfPOS,idOfContragent);
    }

    public Long getIdOfPos() {
        return idOfPos;
    }

    public String getStaffGuid() {
        return staffGuid;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public int getType() {
        return type;
    }

    public long getValue() {
        return value;
    }

    public long getIdOfContract() {
        return idOfContract;
    }

    public long getDate() {
        return date;
    }

    public long getIdOfOperation() {
        return idOfOperation;
    }

    public boolean isPayment(){
        return type == AccountOperations.TYPE_PAYMENT;
    }
}
