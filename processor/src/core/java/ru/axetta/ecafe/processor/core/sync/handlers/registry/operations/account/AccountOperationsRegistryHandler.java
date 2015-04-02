/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;
import ru.axetta.ecafe.processor.core.persistence.AccountOperations;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.dao.operations.account.AccountOperationsRepository;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.InternalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Shamil
 * Date: 19.02.15
 */
public class AccountOperationsRegistryHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccountOperationsRegistryHandler.class);


    public ResAccountOperationsRegistry process(SyncRequest request) {
        AccountOperationsRegistry accountOperationsRegistry = request.getAccountOperationsRegistry();
        if (accountOperationsRegistry.getOperationItemList() == null
                || accountOperationsRegistry.getOperationItemList().size() == 0) {
            return new ResAccountOperationsRegistry();
        }

        ResAccountOperationsRegistry resAccountOperationsRegistry = new ResAccountOperationsRegistry();

        for (AccountOperationItem accountOperationItem : accountOperationsRegistry.getOperationItemList()) {
            try {
                accountOperationItem.setModifiedIdOfOperation(preapreIdOfOperation(request, accountOperationItem.getIdOfOperation()));
                ResAccountOperationItem resAccountOperationItem = handle(accountOperationItem, request);
                resAccountOperationsRegistry.getItemsList().add(resAccountOperationItem);
            }catch (Exception e){
                logger.error("AccountOperationsRegistryHandler #3: " + accountOperationItem.getModifiedIdOfOperation(), e);
            }
        }

        return resAccountOperationsRegistry;
    }

    @Transactional
    private ResAccountOperationItem handle(AccountOperationItem accountOperationItem,SyncRequest request) {
        AccountOperationsRepository accountOperationsRepository = AccountOperationsRepository.getInstance();
        OnlinePaymentProcessor.PayResponse payResponse = null;
        try {
            payResponse = sendRequestToPayment(accountOperationItem);
            AccountOperations accountOperations = new AccountOperations(accountOperationItem, request, payResponse);
            accountOperationsRepository.create(accountOperations);
        } catch (InternalException e) {
            logger.error("Внутренняя ошибка процесинга: " + accountOperationItem.getModifiedIdOfOperation(),e);
            return new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.ERROR, "Внутренняя ошибка процесинга");
        }

        if (payResponse.getResultCode() == PaymentProcessResult.OK.getCode() ){
            return new ResAccountOperationItem(accountOperationItem.getIdOfOperation(), ResAccountOperationItem.OK, "");
        }

        if (payResponse.getResultCode() == PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode()){
            logger.error("Операция с данным идентификатором зарегистрирована: " + accountOperationItem.getModifiedIdOfOperation());

            AccountOperations byIdOfOperation = accountOperationsRepository.findByIdOfOperation(accountOperationItem.getIdOfOperation(), request.getIdOfOrg());
            if(byIdOfOperation != null){
                AccountOperations tempAOperations = new AccountOperations(accountOperationItem, request);
                if (byIdOfOperation.equals(tempAOperations)){
                    return new ResAccountOperationItem(accountOperationItem.getIdOfOperation(), ResAccountOperationItem.OK, "");
                }
            }else {
                AccountOperations accountOperations = new AccountOperations(accountOperationItem, request, payResponse);
                accountOperationsRepository.create(accountOperations);
            }
            return new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.DUPLICATE, "Операция с данным идентификатором зарегистрирована");
        }

        logger.error("Внутренняя ошибка процесинга#2: " + accountOperationItem.getModifiedIdOfOperation() + ", ErrorCode: " + payResponse.getResultCode());
        return new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.ERROR, "Внутренняя ошибка процесинга");

    }

    /*
    * Отправка запроса на оплату внутри ИСПП
    * */
    private OnlinePaymentProcessor.PayResponse sendRequestToPayment(AccountOperationItem accountOperationItem) throws InternalException{
        if (accountOperationItem.getIdOfContragent() == null){
            throw new InternalException("Контрагент не установлен. Contragent not found.");
        }
        OnlinePaymentProcessor.PayRequest payRequest = null;
        try {switch (accountOperationItem.getType()){
            case AccountOperations.TYPE_PAYMENT:
                payRequest = new OnlinePaymentProcessor.PayRequest(1, false, accountOperationItem.getIdOfContragent(), null,
                        ClientPayment.CASHIER_PAYMENT_METHOD, accountOperationItem.getIdOfContract(), accountOperationItem.getModifiedIdOfOperation(), null, accountOperationItem.getValue(), false);
                break;
            case AccountOperations.TYPE_CANCEL:
                payRequest = new OnlinePaymentProcessor.PayRequest(1, false, accountOperationItem.getIdOfContragent(), null,
                        ClientPayment.CASHIER_PAYMENT_METHOD, accountOperationItem.getIdOfContract(), accountOperationItem.getModifiedIdOfOperation(), null, (accountOperationItem.getValue() * (-1)), true);
                break;
        }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
        OnlinePaymentProcessor.PayResponse payResponse = RuntimeContext.getInstance().getOnlinePaymentProcessor()
                .processPayRequest(payRequest);
        return payResponse;
    }

    /*
    * AO+_+idoforg+_+idofoperation
    * */
    private String preapreIdOfOperation(SyncRequest request, long idOfOperation){
        return "AO_"+request.getIdOfOrg()+"_"+idOfOperation;

    }
}
