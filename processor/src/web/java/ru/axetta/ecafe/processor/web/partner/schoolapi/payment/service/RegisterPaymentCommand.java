/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Purchase;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.ResPaymentRegistryItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PurchaseDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.ResPaymentDTO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
class RegisterPaymentCommand {

    private final Logger logger = LoggerFactory.getLogger(RegisterPaymentCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public RegisterPaymentCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public List<ResPaymentDTO> registerPayments(Long idOfOrg, List<PaymentDTO> payments) {
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
                return ResPaymentDTO.error(resPaymentRegistryItem.getIdOfOrder(), resPaymentRegistryItem.getResult(),
                        resPaymentRegistryItem.getError());
            }
        } catch (Exception e) {
            String message = String
                    .format("Error in register payment with Id: '%d', %s", payment.getIdOfOrder(), e.getMessage());
            logger.error(message, e);
            return ResPaymentDTO.error(payment.getIdOfOrder(), ResponseCodes.ORDER_REGISTER_ERROR.getCode(), message);
        }
    }

    private Payment getPayment(PaymentDTO source) {
        return PaymentBuilder.build(source);
    }


    static class PaymentBuilder {

        public static Payment build(PaymentDTO source) {
            List<Purchase> purchases = new ArrayList<>();
            for (PurchaseDTO p : source.getPurchases()) {
                purchases.add(createPurchase(p));
            }

            String comments = getStringValue(source.getComments(),null,90);
            //для случая, когда заказ частью оплачивается из заблокированных средств, а частью с основного баланса - уменьшаем сумму по карте
            //на сумму из заблокированных средств. В обработке заказа в FinancialOpsManager.createOrderCharge в этом случае будет две
            //транзакции - по обычному балансу и по заблокированному. Если sumByCard == summOfCBHR, то заказ целиком по заблокированному балансу
            long sumByCard = source.getSumByCard() - source.getTotalSumClientBalanceHold();

            return new Payment(source.getIdOfCard(), source.getCreatedDateTime(), source.getOrderDate(),
                    source.getSocialDiscount(), source.getTradeDiscount(), source.getGrant(), source.getIdOfOrg(),
                    source.getIdOfClient(), source.getIdOfPayForClient(), source.getIdOfOrder(),
                    source.getIdOfCashier(), sumByCard, source.getSumByCash(), source.getTotalSum(),
                    source.getIdOfPOS(), source.getIdOfStaffConfirm(), source.getOrderState(), comments,
                    OrderTypeEnumType.fromInteger(source.getOrderType()), purchases, source.getGuidClientBalanceHold(),
                    source.getTotalSumClientBalanceHold(), source.getIdOfCardLongFormat());
        }

        private static Purchase createPurchase(PurchaseDTO p) {
            String rootMenu = getStringValue(p.getRootMenu(),"", 32);
            String menuOutput = getStringValue(p.getMenuOutput(),"",32);
            String name = getStringValue(p.getName(),"", 256);
            String itemCode = getStringValue(p.getItemCode(),null, 32);
            String menuGroup = getStringValue(p.getMenuGroup(), MenuDetail.DEFAULT_GROUP_NAME, 60);
            String manufacturer = getStringValue(p.getManufacturer(),null, 128);

            return new Purchase(p.getTotalDiscount(), p.getSocialDiscount(), p.getIdOfOrderDetail(), name,
                    p.getQty(), p.getTotalPrice(), rootMenu, menuOutput, p.getType(), menuGroup, p.getMenuOrigin(),
                    itemCode, null, p.getIdOfRule(), p.getIdOfMenu(), manufacturer, p.getGuidPreOrderComplex(),
                    p.getRation(), p.getIdOfComplex(), p.getIdOfDish());
        }
    }


    private static String getStringValue(String value, String defaultValue, int maxLength){
        return value !=null ? StringUtils.substring(value.trim(), 0, maxLength) : defaultValue;
    }

}
