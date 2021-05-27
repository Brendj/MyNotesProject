/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Purchase;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.ResPaymentRegistryItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PurchaseDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.ResPaymentDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
class RegisterPaymentCommand {

    private final Logger logger = LoggerFactory.getLogger(RegisterPaymentCommand.class);
    private final RuntimeContext runtimeContext;

    public RegisterPaymentCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public List<ResPaymentDTO> registerPayments(Long idOfOrg, List<PaymentDTO> payments, User user) {
        List<ResPaymentDTO> response = new ArrayList<>();
        Processor processor = runtimeContext.getProcessor();
        List<Long> allocatedClients = ClientManager.getAllocatedClientsIds(idOfOrg);
        for (PaymentDTO payment : payments) {
            response.add(registerPayment(processor, allocatedClients, idOfOrg, payment));
        }
        return response;
    }

    private ResPaymentDTO registerPayment(Processor processor, List<Long> allocatedClients, Long idOfOrg,
            PaymentDTO payment) {
        try {
            ResPaymentRegistryItem resPaymentRegistryItem = processor
                    .processSyncPaymentRegistryPayment(null, idOfOrg, getPayment(payment),
                            Collections.<Long>emptyList(), allocatedClients);
            if (resPaymentRegistryItem.getResult() == 0) {
                return ResPaymentDTO.success(resPaymentRegistryItem.getIdOfOrder());
            } else {
                return ResPaymentDTO.error(resPaymentRegistryItem.getResult(), resPaymentRegistryItem.getError());
            }
        } catch (Exception e) {
            String message = String
                    .format("Error in register payment with Id: '%d', %s", payment.getIdOfOrder(), e.getMessage());
            logger.error(message, e);
            return ResPaymentDTO.error(ResponseCodes.ORDER_REGISTER_ERROR.getCode(), message);
        }
    }

    private Payment getPayment(PaymentDTO source) {
        List<Purchase> purchases = new ArrayList<>();

        for (PurchaseDTO p : source.getPurchases()) {
            purchases.add(new Purchase(p.getTotalDiscount(), p.getSocialDiscount(), p.getIdOfOrderDetail(), p.getName(),
                    p.getQty(), p.getTotalPrice(), p.getRootMenu(), p.getMenuOutput(), p.getType(), p.getMenuGroup(),
                    p.getMenuOrigin(), p.getItemCode(), null, p.getIdOfRule(), p.getIdOfMenu(), p.getManufacturer(),
                    p.getGuidPreOrderComplex(), p.getRation(), p.getIdOfComplex(), p.getIdOfDish()));
        }

        return new Payment(source.getIdOfCard(), source.getCreatedDateTime(), source.getOrderDate(),
                source.getSocialDiscount(), source.getTradeDiscount(), source.getGrant(), source.getIdOfOrg(),
                source.getIdOfClient(), source.getIdOfPayForClient(), source.getIdOfOrder(), source.getIdOfCashier(),
                source.getSumByCard(), source.getSumByCash(), source.getTotalSum(), source.getIdOfPOS(),
                source.getIdOfStaffConfirm(), source.getOrderState(), source.getComments(),
                OrderTypeEnumType.fromInteger(source.getOrderType()), purchases, source.getGuidClientBalanceHold(),
                source.getTotalSumClientBalanceHold(), source.getIdOfCardLongFormat());

    }


}
