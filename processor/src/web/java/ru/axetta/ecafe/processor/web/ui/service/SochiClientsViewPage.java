/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.SochiClient;
import ru.axetta.ecafe.processor.core.persistence.SochiClientPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class SochiClientsViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(SochiClientsViewPage.class);
    private static final int CONTRACT_ID_MAX_LENGTH = ContractIdFormat.MAX_LENGTH;

    public static class ClientInfo {

        private final Long contractId;
        private final String fullName;
        private final String address;
        private final Date createTime;
        private final Date updateTime;

        public ClientInfo(SochiClient sochiClient) {
            this.contractId = sochiClient.getContractId();
            this.fullName = sochiClient.getFullName();
            this.address = sochiClient.getAddress();
            this.createTime = sochiClient.getCreateTime();
            this.updateTime = sochiClient.getUpdateTime();
        }

        public Date getCreateTime() {
            return createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public long getContractId() {
            return contractId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getAddress() {
            return address;
        }

    }

    public static class ClientPaymentInfo {

        private final long paymentId;
        private final long paymentSum;
        private final long paymentSumF;
        private final Date paymentTime;
        private final Date createTime;
        private final long terminalId;

        public ClientPaymentInfo(SochiClientPayment payment) {
            this.paymentId = payment.getPaymentId();
            this.paymentSum = payment.getPaymentSum();
            this.paymentSumF = payment.getPaymentSumF();
            this.paymentTime = payment.getPaymentTime();
            this.createTime = payment.getCreateTime();
            this.terminalId = payment.getTerminalId();
        }

        public long getPaymentId() {
            return paymentId;
        }

        public long getPaymentSum() {
            return paymentSum;
        }

        public long getPaymentSumF() {
            return paymentSumF;
        }

        public Date getPaymentTime() {
            return paymentTime;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public long getTerminalId() {
            return terminalId;
        }

    }

    private long sochiClientNumber;
    private ClientInfo client;
    private List<ClientPaymentInfo> payments = Collections.emptyList();

    public String getPageFilename() {
        return "service/view_sochi_clients";
    }

    public long getSochiClientNumber() {
        return sochiClientNumber;
    }

    public ClientInfo getClient() {
        return client;
    }

    public List<ClientPaymentInfo> getPayments() {
        return payments;
    }

    public int getContractIdMaxLength() {
        return CONTRACT_ID_MAX_LENGTH;
    }

    public void fill(RuntimeContext runtimeContext, Session persistenceSession, Long contractId) throws Exception {
        if (contractId == null) {
            this.client = null;
            this.payments = Collections.emptyList();
        } else {
            SochiClient sochiClient = DAOUtils.findSochiClient(persistenceSession, contractId);
            ClientInfo client = new ClientInfo(sochiClient);
            List<ClientPaymentInfo> payments = createClientPaymentList(sochiClient);
            this.client = client;
            this.payments = payments;
        }
        this.sochiClientNumber = DAOUtils.getSochiClientNumber(persistenceSession);
    }

    private static List<ClientPaymentInfo> createClientPaymentList(SochiClient client) throws Exception {
        Set<SochiClientPayment> payments = client.getPayments();
        List<ClientPaymentInfo> result = new ArrayList<ClientPaymentInfo>(payments.size());
        for (SochiClientPayment payment : payments) {
            result.add(new ClientPaymentInfo(payment));
        }
        return result;
    }

}