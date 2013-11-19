package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class OnlinePaymentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OnlinePaymentProcessor.class);
    private Processor processor=null;

    public OnlinePaymentProcessor(Processor p) {
        this.processor=p;
    }

    public boolean checkPaymentEligibility(long idOfClient, LinkedList<Long> idOfAllowedClientOrgsList)
            throws Exception {
        Client client = processor.getClientInfo(idOfClient);
        if (client==null) return false;
        long idOfClientOrg=client.getOrg().getIdOfOrg();
        for (Long l : idOfAllowedClientOrgsList) {
            if (l==idOfClientOrg) return true;
        }
        return false;
    }

    public PayResponse processPayRequest(PayRequest request) {
        final long clientId = request.getClientId();
        try {
            PaymentRequest.PaymentRegistry.Payment payment = new PaymentRequest.PaymentRegistry.Payment(
                    request.getCheckOnly(),
                    request.getPaymentId(),
                    clientId,
                    null,
                    request.getTspContragentId(),
                    request.getPayDate()==null?new Date():request.getPayDate(),
                    request.getSum(),
                    request.getPaymentMethod(),
                    null,
                    request.getPaymentAdditionalId(),
                    false);
            //// обработать платеж
            PaymentResponse.ResPaymentRegistry.Item processResult = processor.processPayPaymentRegistryPayment(
                    request.getContragentId(), payment);
            ////
            PaymentResponse.ResPaymentRegistry.Item.ClientInfo client=processResult.getClient();
            return new PayResponse(request.protoVersion, request.isCheckOnly(), processResult.getResult(),
                    processResult.getError(), processResult.getTspContragentId(), clientId, request.getPaymentId(), processResult.getBalance(),
                    processResult.getSubBalance1(), (client==null || client.getPerson()==null)?null:client.getPerson().getFirstName(),
                    (client==null || client.getPerson()==null)?null:client.getPerson().getSurname(),
                    (client==null || client.getPerson()==null)?null:client.getPerson().getSecondName(),
                    getCardPrintedNo(processResult.getCard()), processResult.getAddInfo());
        } catch (Exception e) {
            logger.error(String.format("Failed to process request: %s", request), e);
            return new PayResponse(request.protoVersion, request.isCheckOnly(), Processor.PaymentProcessResult.UNKNOWN_ERROR.getCode(),
                    Processor.PaymentProcessResult.UNKNOWN_ERROR.getDescription(), request.getTspContragentId(), clientId, request.getPaymentId(), null,
                    null, null, null, null, null, null);
        }
    }

    private Long getCardPrintedNo(PaymentResponse.ResPaymentRegistry.Item.CardInfo card) {
        if (card==null) return null;
        return card.getCardPrintedNo();
    }

    public static PayResponse generateErrorResponse(Processor.PaymentProcessResult result) {
        return new OnlinePaymentProcessor.PayResponse(0, true, result.getCode(),
            result.getDescription(), null, null, null, null, null, null, null, null, null, null);
    }

    public static class PayRequest {
        public final static int V_0=0, V_1=1;
        
        private final int protoVersion;
        private final long contragentId;
        private final int paymentMethod;

        private final long clientId;
        private final String paymentId;
        private final String paymentAdditionalId;
        private final long sum;
        private final Long tspContragentId;

        private final boolean bCheckOnly;
        private Date payDate = null;

        public PayRequest(int protoVersion, boolean bCheckOnly, long contragentId, Long tspContragentId, int paymentMethod, long clientId, String paymentId, String paymentAdditionalId, long sum, boolean bNegativeSum)
                throws Exception {
            if (!bNegativeSum && sum<0) throw new Exception("Payment sum is negative: "+sum);
            this.protoVersion = protoVersion;
            this.bCheckOnly=bCheckOnly;
            this.contragentId = contragentId;
            this.paymentMethod = paymentMethod;
            this.clientId = clientId;
            this.paymentId = paymentId;
            this.paymentAdditionalId = paymentAdditionalId;
            this.sum = sum;
            this.tspContragentId = tspContragentId;
        }

        public boolean isCheckOnly() {
            return bCheckOnly;
        }

        public long getContragentId() {
            return contragentId;
        }

        public int getPaymentMethod() {
            return paymentMethod;
        }

        public long getSum() {
            return sum;
        }

        public long getClientId() {
            return clientId;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public String getPaymentAdditionalId() {
            return paymentAdditionalId;
        }

        public boolean getCheckOnly() {
            return bCheckOnly;
        }

        public Long getTspContragentId() {
            return tspContragentId;
        }

        public int getProtoVersion() {
            return protoVersion;
        }

        public Date getPayDate() {
            return payDate;
        }

        public void setPayDate(Date payDate) {
            this.payDate = payDate;
        }

        @Override
        public String toString() {
            return "PayRequest{" + "contragentId=" + contragentId + ", paymentMethod=" + paymentMethod + ", clientId="
                    + clientId + ", paymentId='" + paymentId + '\'' + ", paymentAdditionalId='" + paymentAdditionalId
                    + '\'' + ", sum=" + sum + ", bCheckOnly=" + bCheckOnly + '}';
        }
    }

    public static class PayResponse {
        private final int protoVersion;
        private final boolean bCheckOnly;
        private final Long clientId;
        private final String paymentId, clientFirstName, clientSurname, clientSecondName;
        private final Long balance;
        private final Long subBalance1;
        private final Long cardPrintedNo;
        private final int resultCode;
        private final String resultDescription;
        private final Long tspContragentId;
        private final HashMap<String, String> addInfo;

        public PayResponse(int protoVersion, boolean bCheckOnly, int resultCode, String resultDescription, Long tspContragentId, Long clientId, String paymentId,
                Long balance, Long subBalance1, String clientFirstName, String clientSurname, String clientSecondName, Long cardPrintedNo, HashMap<String, String> addInfo) {
            this.protoVersion = protoVersion;
            this.bCheckOnly = bCheckOnly;
            this.resultCode=resultCode;
            this.resultDescription=resultDescription;
            this.tspContragentId = tspContragentId;
            this.clientId = clientId;
            this.paymentId = paymentId;
            this.balance = balance;
            this.subBalance1 = subBalance1;
            this.clientSecondName = clientSecondName;
            this.clientFirstName = clientFirstName;
            this.clientSurname = clientSurname;
            this.cardPrintedNo = cardPrintedNo;
            this.addInfo = addInfo;
        }

        public int getResultCode() {
            return resultCode;
        }
        public String getResultDescription() {
            return resultDescription;
        }

        public Long getClientId() {
            return clientId;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public Long getBalance() {
            return balance;
        }

        public String getClientFullName() {
            if (clientSurname==null) return null;
            return clientSurname +" "+clientFirstName+" "+ clientSecondName;
        }

        public String getClientFirstName() {
            return clientFirstName;
        }

        public String getClientSurname() {
            return clientSurname;
        }

        public String getClientSecondName() {
            return clientSecondName;
        }

        public Long getCardPrintedNo() {
            return cardPrintedNo;
        }

        public boolean isCheckOnly() {
            return bCheckOnly;
        }

        public Long getTspContragentId() {
            return tspContragentId;
        }

        public int getProtoVersion() {
            return protoVersion;
        }

        public HashMap<String, String> getAddInfo() {
            return addInfo;
        }

        public Long getSubBalance1() {
            return subBalance1;
        }

        @Override
        public String toString() {
            return "PayResponse{" +
                    "protoVersion=" + protoVersion +
                    ", bCheckOnly=" + bCheckOnly +
                    //", clientId=" + clientId +
                    ", paymentId='" + paymentId + '\'' +
                    ", clientFirstName='" + clientFirstName + '\'' +
                    ", clientSurname='" + clientSurname + '\'' +
                    ", clientSecondName='" + clientSecondName + '\'' +
                    ", balance=" + balance +
                    ", subBalance1=" + subBalance1 +
                    ", cardPrintedNo=" + cardPrintedNo +
                    ", resultCode=" + resultCode +
                    ", resultDescription='" + resultDescription + '\'' +
                    ", tspContragentId=" + tspContragentId +
                    '}';
        }
    }

}
