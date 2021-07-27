package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
                    false,
                    request.getAllowedTSPIds());
            //// обработать платеж
            PaymentResponse.ResPaymentRegistry.Item processResult = RuntimeContext.getInstance().getPaymentProcessor().processPayPaymentRegistryPayment(
                    request.getContragentId(), payment);
            ////
            PaymentResponse.ResPaymentRegistry.Item.ClientInfo client = processResult.getClient();
            PayResponse payResponse = new PayResponse(request.protoVersion, request.isCheckOnly(),
                    processResult.getResult(), processResult.getError(), processResult.getTspContragentId(), clientId,
                    request.getPaymentId(), processResult.getBalance(), processResult.getSubBalance1(),
                    (client == null || client.getPerson() == null) ? null : client.getPerson().getFirstName(),
                    (client == null || client.getPerson() == null) ? null : client.getPerson().getSurname(),
                    (client == null || client.getPerson() == null) ? null : client.getPerson().getSecondName(),
                    getCardPrintedNo(processResult.getCard()), processResult.getAddInfo());
            payResponse.setIdOfClientPayment(processResult.getIdOfClientPayment());
            payResponse.setInn(processResult.getInn());
            payResponse.setNazn(processResult.getNazn());
            payResponse.setBic(processResult.getBic());
            payResponse.setRasch(processResult.getRasch());
            payResponse.setBank(processResult.getBank());
            payResponse.setCorrAccount(processResult.getCorrAccount());
            payResponse.setKpp(processResult.getKpp());
            return payResponse;
        } catch (Exception e) {
            logger.error(String.format("Failed to process request: %s", request), e);
            return new PayResponse(request.protoVersion, request.isCheckOnly(), PaymentProcessResult.UNKNOWN_ERROR.getCode(),
                    PaymentProcessResult.UNKNOWN_ERROR.getDescription(), request.getTspContragentId(), clientId, request.getPaymentId(), null,
                    null, null, null, null, null, null);
        }
    }

    private Long getCardPrintedNo(PaymentResponse.ResPaymentRegistry.Item.CardInfo card) {
        if (card==null) return null;
        return card.getCardPrintedNo();
    }

    public static PayResponse generateErrorResponse(PaymentProcessResult result) {
        return new OnlinePaymentProcessor.PayResponse(0, true, result.getCode(),
            result.getDescription(), null, null, null, null, null,
                null, null, null, null, null);
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
        private String bmId;
        private List<Long> allowedTSPIds;

        public PayRequest(int protoVersion, boolean bCheckOnly, long contragentId, Long tspContragentId,
                int paymentMethod, long clientId, String paymentId, String paymentAdditionalId, long sum,
                boolean bNegativeSum, String bmId) throws Exception {
            this(protoVersion, bCheckOnly, contragentId, tspContragentId, paymentMethod, clientId, paymentId,
                    paymentAdditionalId, sum, bNegativeSum);
            this.bmId = bmId;
        }

        public PayRequest(int protoVersion, boolean bCheckOnly, long contragentId, Long tspContragentId, int paymentMethod, long clientId,
                String paymentId, String paymentAdditionalId, long sum, boolean bNegativeSum, List<Long> allowedTSPIds) throws Exception {
            this(protoVersion, bCheckOnly, contragentId, tspContragentId, paymentMethod, clientId,
                    paymentId, paymentAdditionalId, sum, bNegativeSum);
            this.allowedTSPIds = allowedTSPIds;
        }

        public PayRequest(int protoVersion, boolean bCheckOnly, long contragentId, Long tspContragentId, int paymentMethod, long clientId,
                String paymentId, String paymentAdditionalId, long sum, boolean bNegativeSum)
                throws Exception {
            if (!bNegativeSum && sum < 0) throw new Exception("Payment sum is negative: " + sum);
            this.protoVersion = protoVersion;
            this.bCheckOnly = bCheckOnly;
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
                    + '\'' + ", sum=" + sum + ", bCheckOnly=" + bCheckOnly + ", BMID=" + bmId + '}';
        }

        public List<Long> getAllowedTSPIds() {
            return allowedTSPIds;
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
        private Long idOfClientPayment;
        private String inn;
        private String nazn;
        private String bic;
        private String rasch;
        private String bank;
        private String corrAccount;
        private String kpp;

        public PayResponse(int protoVersion, boolean bCheckOnly, int resultCode, String resultDescription, Long tspContragentId, Long clientId, String paymentId,
                Long balance, Long subBalance1, String clientFirstName, String clientSurname, String clientSecondName, Long cardPrintedNo,
                HashMap<String, String> addInfo) {
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

        public String getClientBlurName() {
            if (clientSecondName == null) {
                return null;
            }
            return blurString(clientSurname) + " " + blurString(clientFirstName) + " " + blurString(clientSecondName);
        }

        public String getClientBlurWithoutClientFirstName() {
            if (clientSecondName == null) {
                return null;
            }
            return  blurStringWithoutClientFirstName(clientSurname) + " " + clientFirstName;
        }

        public String blurStringWithoutClientFirstName(String toBlurString) {
            String result;

            char[] split = toBlurString.toCharArray();

            result = String.valueOf(split[0]).concat(String.valueOf(split[1])).concat("***");

            return result;
        }

        public String blurString(String toBlurString) {
            String result = "";

            String[] splitBar;

            if (toBlurString.contains("-")) {
                splitBar = toBlurString.split("-");
            } else {
                splitBar = toBlurString.split(" ");
            }

            if (splitBar.length >= 2) {
                String preResult = "";

                for (String str : splitBar) {
                    int length = str.length();

                    if (length > 3) {
                        char[] split = str.toCharArray();
                        preResult = String.valueOf(split[0]).concat(String.valueOf(split[1])).concat("***")
                                .concat(String.valueOf(split[length - 2])).concat(String.valueOf(split[length - 1]));
                    } else {
                        if (length <= 2) {
                            preResult = str.concat("***").concat(str.toLowerCase());
                        }

                        if (length == 3) {
                            char[] split = str.toCharArray();
                            preResult = String.valueOf(split[0]).concat(String.valueOf(split[1])).concat("***")
                                    .concat(String.valueOf(split[1])).concat(String.valueOf(split[2]));
                        }
                    }
                    if (toBlurString.contains("-")) {
                        result = result.concat(preResult).concat("-");
                    } else {
                        result = result.concat(preResult).concat(" ");
                    }
                }
                result = result.substring(0, result.length() - 1);
            } else {
                int length = toBlurString.length();

                if (length > 3) {
                    char[] split = toBlurString.toCharArray();
                    result = String.valueOf(split[0]).concat(String.valueOf(split[1])).concat("***")
                            .concat(String.valueOf(split[length - 2])).concat(String.valueOf(split[length - 1]));
                } else {
                    if (length <= 2) {
                        result = toBlurString.concat("***").concat(toBlurString.toLowerCase());
                    }

                    if (length == 3) {
                        char[] split = toBlurString.toCharArray();
                        result = String.valueOf(split[0]).concat(String.valueOf(split[1])).concat("***")
                                .concat(String.valueOf(split[1])).concat(String.valueOf(split[2]));
                    }
                }
            }

            return result;
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

        public Long getIdOfClientPayment() {
            return idOfClientPayment;
        }

        public void setIdOfClientPayment(Long idOfClientPayment) {
            this.idOfClientPayment = idOfClientPayment;
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

        public String getInn() {
            return inn;
        }

        public void setInn(String inn) {
            this.inn = inn;
        }

        public String getNazn() {
            return nazn;
        }

        public void setNazn(String nazn) {
            this.nazn = nazn;
        }

        public String getBic() {
            return bic;
        }

        public void setBic(String bic) {
            this.bic = bic;
        }

        public String getRasch() {
            return rasch;
        }

        public void setRasch(String rasch) {
            this.rasch = rasch;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getCorrAccount() {
            return corrAccount;
        }

        public void setCorrAccount(String corrAccount) {
            this.corrAccount = corrAccount;
        }

        public String getKpp() {
            return kpp;
        }

        public void setKpp(String kpp) {
            this.kpp = kpp;
        }

        public String processFio() {
            return String.format("%s %s %.1s.", clientFirstName, clientSecondName, clientSurname);
        }
    }

}
