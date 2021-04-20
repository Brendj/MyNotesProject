package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class Payment {

    private final Long cardNo;
    private final Long longCardNo;
    private final Date time;
    private final Date orderDate;
    private final Long socDiscount;
    private final Long trdDiscount;
    private final Long confirmerId;
    private final long grant;
    private final Long idOfOrg;
    private final Long idOfClient;
    private final Long idOfPayForClient;
    private final long idOfOrder;
    private final Long idOfCashier;
    private final long sumByCard;
    private final long sumByCash;
    private final long RSum;
    private final Long idOfPOS;
    private final int state;
    private final String comments;
    private final OrderTypeEnumType orderType;
    private final List<Purchase> posPurchases;
    private final String guidOfCBHR;
    private final Long summFromCBHR;

    public static Payment build(Node paymentNode, LoadContext loadContext) throws Exception {
        NamedNodeMap namedNodeMap = paymentNode.getAttributes();
        Long cardNo = getLongValueNullSafe(namedNodeMap, "CardNo");
        Long longCardNo = getLongValueNullSafe(namedNodeMap, "LongCardId");
        Date date = loadContext.getTimeFormat().parse(namedNodeMap.getNamedItem("Date").getTextContent());
        /* поддержка версий которые не высылают OrderDate в эту колонку записываются дата из колоки Date */
        String orderDateStr = getStringValueNullSafe(namedNodeMap,"OrderDate");
        Date orderDate = null;
        if (orderDateStr == null) {
            orderDate = date;
        } else {
            orderDate = loadContext.getTimeFormat().parse(orderDateStr);
        }
        Long socDiscount = 0L;
        Long trdDiscount = 0L;
        long rSum = getLongValue(namedNodeMap, "rSum");
        if (namedNodeMap.getNamedItem("Discount") != null) {
            long discount = getLongValue(namedNodeMap, "Discount");
            if (discount == rSum) {
                socDiscount = discount;
            } else {
                trdDiscount = discount;
            }
        } else {
            socDiscount = getLongValueNullSafe(namedNodeMap, "SocDiscount");
            trdDiscount = getLongValueNullSafe(namedNodeMap, "TrdDiscount");
            if (socDiscount == null) {
                socDiscount = 0L;
            }
            if (trdDiscount == null) {
                trdDiscount = 0L;
            }
        }
        Long confirmerId = getLongValueNullSafe(namedNodeMap, "ConfirmerId");
        long grant = getLongValue(namedNodeMap, "Grant");
        Long idOfOrg = getLongValueNullSafe(namedNodeMap, "IdOfOrg");
        Long idOfClient = getLongValueNullSafe(namedNodeMap, "IdOfClient");
        Long idOfPayForClient = getLongValueNullSafe(namedNodeMap, "IdOfPayForClient");
        long idOfOrder = getLongValue(namedNodeMap, "IdOfOrder");
        Long idOfCashier;
        try {
            idOfCashier = getLongValue(namedNodeMap, "IdOfCashier");
        } catch (Exception e) {
            idOfCashier = null;
        }
        long sumByCard = getLongValue(namedNodeMap, "SumByCard");
        long sumByCash = getLongValue(namedNodeMap, "SumByCash");
        Long idOfPOS = null;
        if (namedNodeMap.getNamedItem("IdOfPOS") != null) {
            idOfPOS = getLongValue(namedNodeMap, "IdOfPOS");
        }
        List<Purchase> purchases = new ArrayList<Purchase>();
        Node purchaseNode = paymentNode.getFirstChild();
        while (null != purchaseNode) {
            if (Node.ELEMENT_NODE == purchaseNode.getNodeType() && purchaseNode.getNodeName()
                    .equals("PC")) {
                purchases.add(Purchase.build(purchaseNode, loadContext.getMenuGroups()));
            }
            purchaseNode = purchaseNode.getNextSibling();
        }

        String comments = getStringValueNullSafe(namedNodeMap,"Comments",90);

        // тип по умолчанию
        int orderType = 1;
        String orderTypeStr = getStringValueNullSafe(namedNodeMap, "OrderType");
        if (orderTypeStr != null) {
            orderType = Integer.parseInt(orderTypeStr);
        }

        // статус заказа 0 - проведен (значение по умолчанию), 1 - отменен
        int state=0;
        String stateStr = getStringValueNullSafe(namedNodeMap, "State");
        if (stateStr != null) {
            state = Integer.parseInt(stateStr);
        }

        String guidOfCBHR = getStringValueNullSafe(namedNodeMap, "GuidOfCBHR");
        Long summOfCBHR = getLongValueNullSafe(namedNodeMap, "SummFromCBHR");
        if (summOfCBHR == null) summOfCBHR = 0L;
        //для случая, когда заказ частью оплачивается из заблокированных средств, а частью с основного баланса - уменьшаем сумму по карте
        //на сумму из заблокированных средств. В обработке заказа в FinancialOpsManager.createOrderCharge в этом случае будет две
        //транзакции - по обычному балансу и по заблокированному. Если sumByCard == summOfCBHR, то заказ целиком по заблокированному балансу
        sumByCard = sumByCard - summOfCBHR;

        return new Payment(cardNo, date, orderDate, socDiscount, trdDiscount, grant, idOfOrg, idOfClient, idOfPayForClient, idOfOrder,
                idOfCashier, sumByCard, sumByCash, rSum, idOfPOS,confirmerId, state, comments, OrderTypeEnumType.fromInteger(orderType),
                purchases, guidOfCBHR, summOfCBHR, longCardNo);
    }

    public Payment(Long cardNo, Date time, Date orderDate, long socDiscount, long trdDiscount, long grant, Long IdOfOrg, Long idOfClient,
            Long idOfPayForClient, long idOfOrder, Long idOfCashier, long sumByCard, long sumByCash, long RSum, Long idOfPOS,
            Long confirmerId, int state, String comments, OrderTypeEnumType orderType, List<Purchase> posPurchases,
            String guidOfCBHR, Long summOfCBHR, Long longCardNo) {
        this.cardNo = cardNo;
        this.longCardNo = longCardNo;
        this.time = time;
        this.orderDate = orderDate;
        this.socDiscount = socDiscount;
        this.trdDiscount = trdDiscount;
        this.confirmerId = confirmerId;
        this.grant = grant;
        this.idOfOrg = IdOfOrg;
        this.idOfClient = idOfClient;
        this.idOfPayForClient = idOfPayForClient;
        this.idOfOrder = idOfOrder;
        this.idOfCashier = idOfCashier;
        this.sumByCard = sumByCard;
        this.sumByCash = sumByCash;
        this.RSum = RSum;
        this.idOfPOS = idOfPOS;
        this.state = state;
        this.comments = comments;
        this.orderType = orderType;
        this.posPurchases = posPurchases;
        this.guidOfCBHR = guidOfCBHR;
        this.summFromCBHR = summOfCBHR;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public Long getLongCardNo() {
        return longCardNo;
    }

    public Date getTime() {
        return time;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public Long getTrdDiscount() {
        return trdDiscount;
    }

    public Long getConfirmerId() {
        return confirmerId;
    }

    public Long getGrant() {
        return grant;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getIdOfPayForClient() {
        return idOfPayForClient;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public Long getIdOfCashier() {
        return idOfCashier;
    }

    public Long getSumByCard() {
        return sumByCard;
    }

    public Long getSumByCash() {
        return sumByCash;
    }

    public Long getRSum() {
        return RSum;
    }

    public Long getIdOfPOS() {
        return idOfPOS;
    }

    public String getComments() {
        return comments;
    }

    public OrderTypeEnumType getOrderType() {
        return orderType;
    }

    public List<Purchase> getPurchases() {
        return posPurchases;
    }

    /**
     * Проверка на статус заказа
     * @return true - заказ пробит, false - заказ отменен
     */
    public boolean isCommit() {
        return state== Order.STATE_COMMITED;
    }

    public String getGuidOfCBHR() {
        return guidOfCBHR;
    }

    public Long getSummFromCBHR() {
        return summFromCBHR;
    }
}
