package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.getLongValue;
import static ru.axetta.ecafe.processor.core.utils.XMLUtils.getLongValueNullSafe;
import static ru.axetta.ecafe.processor.core.utils.XMLUtils.getStringValueNullSafe;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class Payment {

    private final Long cardNo;
    private final Date time;
    private final Date orderDate;
    private final Long socDiscount;
    private final Long trdDiscount;
    private final Long confirmerId;
    private final long grant;
    private final Long idOfClient;
    private final long idOfOrder;
    private final long idOfCashier;
    private final long sumByCard;
    private final long sumByCash;
    private final long RSum;
    private final Long idOfPOS;
    private final String comments;
    private final OrderTypeEnumType orderType;
    private final List<Purchase> posPurchases;

    public static Payment build(Node paymentNode, LoadContext loadContext) throws Exception {
        NamedNodeMap namedNodeMap = paymentNode.getAttributes();
        Long cardNo = getLongValueNullSafe(namedNodeMap, "CardNo");
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
        Long idOfClient = getLongValueNullSafe(namedNodeMap, "IdOfClient");
        long idOfOrder = getLongValue(namedNodeMap, "IdOfOrder");
        long idOfCashier = getLongValue(namedNodeMap, "IdOfCashier");
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

        return new Payment(cardNo, date, orderDate, socDiscount, trdDiscount, grant, idOfClient, idOfOrder,
                idOfCashier, sumByCard, sumByCash, rSum, idOfPOS,confirmerId,comments, OrderTypeEnumType.fromInteger(orderType), purchases);
    }

    public Payment(Long cardNo, Date time, Date orderDate, long socDiscount, long trdDiscount, long grant, Long idOfClient,
            long idOfOrder, long idOfCashier, long sumByCard, long sumByCash, long RSum, Long idOfPOS, Long confirmerId,
            String comments, OrderTypeEnumType orderType, List<Purchase> posPurchases) {
        this.cardNo = cardNo;
        this.time = time;
        this.orderDate = orderDate;
        this.socDiscount = socDiscount;
        this.trdDiscount = trdDiscount;
        this.confirmerId = confirmerId;
        this.grant = grant;
        this.idOfClient = idOfClient;
        this.idOfOrder = idOfOrder;
        this.idOfCashier = idOfCashier;
        this.sumByCard = sumByCard;
        this.sumByCash = sumByCash;
        this.RSum = RSum;
        this.idOfPOS = idOfPOS;
        this.comments = comments;
        this.orderType = orderType;
        this.posPurchases = posPurchases;
    }

    public Long getCardNo() {
        return cardNo;
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

    public Long getIdOfClient() {
        return idOfClient;
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
}
