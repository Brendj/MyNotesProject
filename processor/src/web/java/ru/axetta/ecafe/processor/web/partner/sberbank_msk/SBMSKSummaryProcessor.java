/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientWithAddInfo;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientsWithResultCode;
import ru.axetta.ecafe.processor.web.partner.utils.CommonMethodUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class SBMSKSummaryProcessor {
    private Logger logger = LoggerFactory.getLogger(SBMSKSummaryProcessor.class);

    public SBMSKClientSummaryBaseListResult processByGuardMobile(String guardMobile) throws InvalidINNException {
        Session session = null;
        Transaction transaction = null;
        boolean exeptionINN = false;
        try {
            List<SBMSKClientSummaryBase> clientSummaries = new LinkedList<>();
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();
            ClientsWithResultCode cd = RuntimeContext.getAppContext().getBean(CommonMethodUtil.class)
                    .getClientsByGuardMobile(guardMobile, session, SBMSKPaymentsCodes.OK.getCode(),
                            PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode(), SBMSKPaymentsCodes.INTERNAL_ERROR.getCode());

            SBMSKClientSummaryBaseListResult clientSummaryBaseListResult = new SBMSKClientSummaryBaseListResult();
            if (cd != null && cd.getClients() != null) {
                for (Map.Entry<Client, ClientWithAddInfo> entry : cd.getClients().entrySet()) {
                    if (entry.getValue().isDisabled()) {
                        continue;
                    }
                    SBMSKClientSummaryBase base = processSummaryBase(entry.getKey());
                    if (base.getInn() != null && !base.getInn().trim().isEmpty() && base.getContractId() != null)
                        clientSummaries.add(base);
                }
                if (clientSummaries.isEmpty())
                {
                    clientSummaryBaseListResult.resultCode = SBMSKPaymentsCodes.NOT_FOUND_INN.getCode().longValue();
                    clientSummaryBaseListResult.description = SBMSKPaymentsCodes.NOT_FOUND_INN.toString();

                    exeptionINN = true;
                }
            }
            clientSummaryBaseListResult.setClientSummary(clientSummaries);
            if (!exeptionINN) {
                clientSummaryBaseListResult.resultCode = cd.resultCode;
                clientSummaryBaseListResult.description = cd.description;
            }

            transaction.commit();
            transaction = null;

            return clientSummaryBaseListResult;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
            if (exeptionINN)
                throw new InvalidINNException("Invalid INN");
        }
    }

    private SBMSKClientSummaryBase processSummaryBase(Client client) {
        SBMSKClientSummaryBase result = new SBMSKClientSummaryBase(client.getContractId(), client.getBalance(),
                client.getPerson().getFirstName(), client.getPerson().getSurname(), client.getPerson().getSecondName());
        result.setNazn(client.getOrg().getDefaultSupplier().getContragentName());
        result.setInn(client.getOrg().getDefaultSupplier().getInn());
        return result;
    }

    public SBMSKSummaryResponse processRequest(SBMSKSummaryRequest request) throws InvalidINNException {
        SBMSKClientSummaryBaseListResult clientSummaryBaseListResult = processByGuardMobile(request.getMobile());
        return new SBMSKSummaryResponse(0, true, clientSummaryBaseListResult.resultCode.intValue(),
                clientSummaryBaseListResult.description, null, null, null,
                null, null, null, null, null, null, null,
                clientSummaryBaseListResult.getClientSummary());
    }
}
