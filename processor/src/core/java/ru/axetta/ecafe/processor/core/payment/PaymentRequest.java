/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.persistence.ClientPayment;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:52:14
 * To change this template use File | Settings | File Templates.
 */
public class PaymentRequest {

    private static int getIntValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        return Integer.parseInt(namedNodeMap.getNamedItem(name).getTextContent());
    }

    private static long getLongValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        return Long.parseLong(namedNodeMap.getNamedItem(name).getTextContent());
    }

    private static String getStringValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            return null;
        }
        return node.getTextContent();
    }

    private static Long getLongValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            return null;
        }
        return Long.parseLong(node.getTextContent());
    }

    public static class PaymentRegistry {

        public static class Payment {

            public List<Long> getAllowedTSPIds() {
                return allowedTSPIds;
            }

            public static class Builder {

                private final DateFormat timeFormat;

                public Builder(DateFormat timeFormat) {
                    this.timeFormat = timeFormat;
                }

                public Payment build(Node paymentNode) throws Exception {
                    NamedNodeMap namedNodeMap = paymentNode.getAttributes();
                    String idOfPayment = namedNodeMap.getNamedItem("IdOfPayment").getTextContent();
                    Long contractId = getLongValueNullSafe(namedNodeMap, "ContractId");
                    Long clientId = getLongValueNullSafe(namedNodeMap, "ClientId");
                    if (contractId==null && clientId==null) throw new IllegalArgumentException("Missing ContractId and ClientId");
                    Date payTime = timeFormat.parse(namedNodeMap.getNamedItem("PayTime").getTextContent());
                    long sum = getLongValue(namedNodeMap, "Sum");
                    int paymentMethod = ClientPayment.REGISTRY_PAYMENT_METHOD;
                    if (paymentMethod < 0 || paymentMethod >= ClientPayment.PAYMENT_METHOD_NAMES.length) {
                        throw new IllegalArgumentException("Unknown payment method");
                    }

                    String resetBalance = getStringValueNullSafe(namedNodeMap, "ResetBalance");
                    boolean bResetBalance = false;
                    if (resetBalance != null && resetBalance.toLowerCase().equals("true")) {
                        bResetBalance = true;
                    }

                    String addPaymentMethod = getStringValueNullSafe(namedNodeMap, "AddPaymentMethod");
                    String addIdOfPayment = getStringValueNullSafe(namedNodeMap, "AddIdOfPayment");
                    return new Payment(idOfPayment, contractId, clientId, null, payTime, sum, paymentMethod, addPaymentMethod,
                            addIdOfPayment, bResetBalance);
                }

            }

            /// при установленом флаге должен быть сброс баланса
            private final boolean bResetBalance;
            private final boolean checkOnly;
            private final String idOfPayment;
            private final Long contractId;
            private final Long clientId;
            private final Long tspContragentId; 
            private final Date payTime;
            private final long sum;
            private final int paymentMethod;
            private final String addPaymentMethod;
            private  String addIdOfPayment;
            private final List<Long> allowedTSPIds;

            public Payment(String idOfPayment, Long contractId, Long clientId, Long tspContragentId, Date payTime, long sum, int paymentMethod,
                    String addPaymentMethod, String addIdOfPayment, boolean bResetBalance) {
                this.idOfPayment = idOfPayment;
                this.contractId = contractId;
                this.clientId = clientId;
                this.tspContragentId = tspContragentId;
                this.payTime = payTime;
                this.sum = sum;
                this.paymentMethod = paymentMethod;
                this.addPaymentMethod = addPaymentMethod;
                this.addIdOfPayment = addIdOfPayment;
                this.checkOnly = false;
                this.bResetBalance = bResetBalance;
                allowedTSPIds = null;
            }

            public Payment(boolean checkOnly, String idOfPayment, Long contractId, Long clientId, Long tspContragentId, Date payTime, long sum,
                    int paymentMethod, String addPaymentMethod, String addIdOfPayment, boolean bResetBalance, List<Long> allowedTSPIds) {
                this.checkOnly = checkOnly;
                this.idOfPayment = idOfPayment;
                this.contractId = contractId;
                this.clientId = clientId;
                this.tspContragentId = tspContragentId;
                this.payTime = payTime;
                this.sum = sum;
                this.paymentMethod = paymentMethod;
                this.addPaymentMethod = addPaymentMethod;
                this.addIdOfPayment = addIdOfPayment;
                this.bResetBalance = bResetBalance;
                this.allowedTSPIds = allowedTSPIds;
            }

            public boolean isResetBalance() {
                return bResetBalance;
            }

            public boolean isCheckOnly() {
                return checkOnly;
            }

            public String getIdOfPayment() {
                return idOfPayment;
            }

            public Long getContractId() {
                return contractId;
            }

            public Long getClientId() {
                return clientId;
            }

            public Date getPayTime() {
                return payTime;
            }

            public long getSum() {
                return sum;
            }

            public int getPaymentMethod() {
                return paymentMethod;
            }

            public String getAddPaymentMethod() {
                return addPaymentMethod;
            }

            public String getAddIdOfPayment() {
                return addIdOfPayment;
            }

            public void setAddIdOfPayment(String addIdOfPayment) {
                this.addIdOfPayment = addIdOfPayment;
            }

            public Long getTspContragentId() {
                return tspContragentId;
            }

            @Override
            public String toString() {
                return "Payment{" +
                        "bResetBalance=" + bResetBalance +
                        ", checkOnly=" + checkOnly +
                        ", idOfPayment='" + idOfPayment + '\'' +
                        ", contractId=" + contractId +
                        ", clientId=" + clientId +
                        ", tspContragentId=" + tspContragentId +
                        ", payTime=" + payTime +
                        ", sum=" + sum +
                        ", paymentMethod=" + paymentMethod +
                        ", addPaymentMethod='" + addPaymentMethod + '\'' +
                        ", addIdOfPayment='" + addIdOfPayment + '\'' +
                        '}';
            }
        }

        public static class Builder {

            private final Payment.Builder paymentBuilder;

            public Builder(DateFormat timeFormat) {
                this.paymentBuilder = new Payment.Builder(timeFormat);
            }

            public PaymentRegistry build(Node paymentRegistryNode) throws Exception {
                List<Payment> payments = new LinkedList<Payment>();
                Node itemNode = paymentRegistryNode.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("PT")) {
                        payments.add(paymentBuilder.build(itemNode));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new PaymentRegistry(payments);
            }
        }

        private final List<Payment> payments;

        public PaymentRegistry(List<Payment> payments) {
            this.payments = payments;
        }

        public Enumeration<Payment> getPayments() {
            return Collections.enumeration(payments);
        }

        public List<Payment> getPaymentsList(){
            return payments;
        }

        @Override
        public String toString() {
            return "PaymentRegistry{" + "payments=" + payments + '}';
        }
    }

    public static class Builder {

        private final DateFormat timeFormat;
        private final PaymentRegistry.Builder paymentRegistryBuilder;

        public Builder() {
            this.timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
            this.timeFormat.setTimeZone(localTimeZone);

            this.paymentRegistryBuilder = new PaymentRegistry.Builder(timeFormat);
        }

        public static Node findEnvelopeNode(Document document) throws Exception {
            Node dataNode = findFirstChildElement(document, "Data");
            Node bodyNode = findFirstChildElement(dataNode, "Body");
            return findFirstChildElement(bodyNode, "PaymentExchange");
        }

        public static long getIdOfContragent(NamedNodeMap namedNodeMap) throws Exception {
            return getLongValue(namedNodeMap, "IdOfContragent");
        }

        public static String getIdOfSync(NamedNodeMap namedNodeMap) throws Exception {
            return namedNodeMap.getNamedItem("SyncTime").getTextContent();
        }

        public PaymentRequest build(Node envelopeNode, NamedNodeMap namedNodeMap, Long idOfUser, long idOfOrg,
                String idOfSync) throws Exception {
            long version = getLongValue(namedNodeMap, "Version");
            if (3L != version) {
                throw new Exception(String.format("Unsupported version: %d", version));
            }
            Date syncTime = timeFormat.parse(idOfSync);
            long idOfPacket = getLongValue(namedNodeMap, "IdOfPacket");
            Node paymentRegistryNode = findFirstChildElement(envelopeNode, "PaymentRegistry");
            PaymentRegistry paymentRegistry = paymentRegistryBuilder.build(paymentRegistryNode);
            return new PaymentRequest(idOfUser, version, idOfOrg, syncTime, idOfPacket, paymentRegistry);
        }

        private static Node findFirstChildElement(Node node, String name) throws Exception {
            Node currNode = node.getFirstChild();
            while (null != currNode) {
                if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                    return currNode;
                }
                currNode = currNode.getNextSibling();
            }
            return null;
        }

        private static Node findFirstChildTextNode(Node node) throws Exception {
            Node currNode = node.getFirstChild();
            while (null != currNode) {
                if (Node.TEXT_NODE == currNode.getNodeType()) {
                    return currNode;
                }
                currNode = currNode.getNextSibling();
            }
            return null;
        }
    }

    private final Long idOfUser;
    private final long version;
    private final long idOfContragent;
    private final Date syncTime;
    private final long idOfPacket;
    private final PaymentRegistry paymentRegistry;

    public PaymentRequest(Long idOfUser, long version, long idOfContragent, Date syncTime, long idOfPacket,
            PaymentRegistry paymentRegistry) {
        this.idOfUser = idOfUser;
        this.version = version;
        this.idOfContragent = idOfContragent;
        this.syncTime = syncTime;
        this.idOfPacket = idOfPacket;
        this.paymentRegistry = paymentRegistry;
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    public long getVersion() {
        return version;
    }

    public long getIdOfContragent() {
        return idOfContragent;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public long getIdOfPacket() {
        return idOfPacket;
    }

    public PaymentRegistry getPaymentRegistry() {
        return paymentRegistry;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" + "version=" + version + ", idOfContragent=" + idOfContragent + ", syncTime="
                + syncTime + ", idOfPacket=" + idOfPacket + ", paymentRegistry=" + paymentRegistry + '}';
    }
}