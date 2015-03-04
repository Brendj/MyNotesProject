/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AccountOperations;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.dao.operations.account.AccountOperationsRepository;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.DuplicatePaymentException;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.InternalException;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Shamil
 * Date: 19.02.15
 */
public class AccountOperationsRegistryHandler {

    public ResAccountOperationsRegistry process(SyncRequest request) {
        AccountOperationsRegistry accountOperationsRegistry = request.getAccountOperationsRegistry();
        if (accountOperationsRegistry.getOperationItemList() == null
                || accountOperationsRegistry.getOperationItemList().size() == 0) {
            return new ResAccountOperationsRegistry();
        }

        ResAccountOperationsRegistry resAccountOperationsRegistry = new ResAccountOperationsRegistry();
        AccountOperationsRepository accountOperationsRepository = AccountOperationsRepository.getInstance();

        for (AccountOperationItem accountOperationItem : accountOperationsRegistry.getOperationItemList()) {
            accountOperationItem.setModifiedIdOfOperation(preapreIdOfOperation(request, accountOperationItem.getIdOfOperation()));
            List<AccountOperations> searchResult = accountOperationsRepository
                    .findByIdOfOperation(accountOperationItem.getIdOfOperation(), request.getIdOfOrg());
            ResAccountOperationItem resAccountOperationItem = handleItem(accountOperationItem, searchResult,request);
            resAccountOperationsRegistry.getItemsList().add(resAccountOperationItem);
        }

        return resAccountOperationsRegistry;
    }

    private ResAccountOperationItem handleItem(AccountOperationItem accountOperationItem,
            List<AccountOperations> searchResult, SyncRequest request) {
        switch (accountOperationItem.getType()) {
            case AccountOperations.TYPE_PAYMENT:
                if (searchResult.size() > 0) {
                    return handleDuplicate(accountOperationItem);
                } else {
                    return handleCreate(accountOperationItem,request);
                }
            case AccountOperations.TYPE_CANCEL:
                if (searchResult.size() > 1) {
                    return handleDuplicate(accountOperationItem);
                } else {
                    return handleCancel(accountOperationItem, request);
                }

            default:
                return handleError(accountOperationItem);
        }
    }


    private ResAccountOperationItem handleCancel(AccountOperationItem accountOperationItem,SyncRequest request) {
        AccountOperationsRepository accountOperationsRepository = AccountOperationsRepository.getInstance();
        OnlinePaymentProcessor.PayResponse payResponse = null;
        ResAccountOperationItem resAccountOperationItem = null;
        try {
            payResponse = sendCancelRequestToPayment(accountOperationItem);
            accountOperationsRepository.create(new AccountOperations(accountOperationItem,request));
        } catch (InternalException e) {
            e.printStackTrace();
            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.ERROR, "Внутренняя ошибка процесинга");
        } catch (DuplicatePaymentException e){
            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.DUPLICATE, "Операция с данным идентификатором зарегистрирована");
        }

        if (payResponse != null){
            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(), ResAccountOperationItem.OK, "");
        }else if(resAccountOperationItem == null) {

            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.ERROR, "Внутренняя ошибка процесинга");
        }

        return resAccountOperationItem;
    }

    @Transactional
    private ResAccountOperationItem handleCreate(AccountOperationItem accountOperationItem, SyncRequest request) {
        AccountOperationsRepository accountOperationsRepository = AccountOperationsRepository.getInstance();
        OnlinePaymentProcessor.PayResponse payResponse = null;
        ResAccountOperationItem resAccountOperationItem = null;
        try {
            payResponse = sendRequestToPayment(accountOperationItem);
            accountOperationsRepository.create(new AccountOperations(accountOperationItem,request));
        } catch (InternalException e) {
            e.printStackTrace();
            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.ERROR, "Внутренняя ошибка процесинга");
        } catch (DuplicatePaymentException e){
            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.DUPLICATE, "Операция с данным идентификатором зарегистрирована");
        }

        if (payResponse != null){
            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(), ResAccountOperationItem.OK, "");
        }else if(resAccountOperationItem == null) {

            resAccountOperationItem = new ResAccountOperationItem(accountOperationItem.getIdOfOperation(),ResAccountOperationItem.ERROR, "Внутренняя ошибка процесинга");
        }

        return resAccountOperationItem;
    }

    private ResAccountOperationItem handleDuplicate(AccountOperationItem accountOperationItem) {
        return new ResAccountOperationItem(
                accountOperationItem.getIdOfOperation(), ResAccountOperationItem.DUPLICATE, "Операция с данным идентификатором зарегистрирована");
    }

    private ResAccountOperationItem handleError(AccountOperationItem accountOperationItem) {
        return new ResAccountOperationItem(accountOperationItem.getIdOfOperation(), ResAccountOperationItem.ERROR,
                "Внутренняя ошибка процесинга");
    }



    /*
    * Отправка запроса на оплату внутри ИСПП
    * */
    private OnlinePaymentProcessor.PayResponse sendRequestToPayment(AccountOperationItem accountOperationItem) throws DuplicatePaymentException, InternalException{
        if (accountOperationItem.getIdOfContragent() == null){
            throw new InternalException("Контрагент не установлен. Contragent not found.");
        }
        OnlinePaymentProcessor.PayRequest payRequest = null;
        try {
            payRequest = new OnlinePaymentProcessor.PayRequest(1, false, accountOperationItem.getIdOfContragent(), null,
                    ClientPayment.CASHIER_PAYMENT_METHOD, accountOperationItem.getIdOfContract(), accountOperationItem.getModifiedIdOfOperation(), null, accountOperationItem.getValue(), false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }

        OnlinePaymentProcessor.PayResponse payResponse = RuntimeContext.getInstance().getOnlinePaymentProcessor()
                .processPayRequest(payRequest);
        if (payResponse.getResultCode() == 140) {
            throw new DuplicatePaymentException();
        }
        return payResponse;
    }


    /*
    * Отправка запроса на отмену оплаты внутри ИСПП
    * */
    private OnlinePaymentProcessor.PayResponse sendCancelRequestToPayment(AccountOperationItem accountOperationItem) throws DuplicatePaymentException, InternalException{

        if (accountOperationItem.getIdOfContragent() == null){
            throw new InternalException("Контрагент не установлен. Contragent not found.");
        }
        OnlinePaymentProcessor.PayRequest payRequest = null;
        try {
            payRequest = new OnlinePaymentProcessor.PayRequest(1, false, accountOperationItem.getIdOfContragent(), null,
                    ClientPayment.CASHIER_PAYMENT_METHOD, accountOperationItem.getIdOfContract(), accountOperationItem.getModifiedIdOfOperation(), null, (accountOperationItem.getValue() * (-1)), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }

        OnlinePaymentProcessor.PayResponse payResponse = RuntimeContext.getInstance().getOnlinePaymentProcessor()
                .processPayRequest(payRequest);
        if (payResponse.getResultCode() == 140) {
            throw new DuplicatePaymentException();
        }
        return payResponse;
    }

    /*
    * AO+_+idoforg+_+idofoperation
    * */
    private String preapreIdOfOperation(SyncRequest request, long idOfOperation){
        return "AO_"+request.getIdOfOrg()+"_"+idOfOperation;

    }
}
