/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Purchase;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.ResPaymentRegistryItem;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */

@WebService()
public class POSPaymentControllerWS extends HttpServlet implements POSPaymentController{

    private final static Logger LOGGER = LoggerFactory.getLogger(POSPaymentControllerWS.class);

    @Override
    public String test(Long orgId){
        return "OK";
    }

    @Override
    public PosResPaymentRegistry createOrder(Long idOfOrg, List<PosPayment> posPaymentList) {
        RuntimeContext runtimeContext =null;
        final PosResPaymentRegistry resPaymentRegistry = new PosResPaymentRegistry();
        try {
            runtimeContext = RuntimeContext.getInstance();
            Processor coreProcessor = runtimeContext.getProcessor();
            List<Long> allocatedClients = ClientManager.getAllocatedClientsIds(idOfOrg);

            final PosResPaymentRegistryItemList posResPaymentRegistryItemList = new PosResPaymentRegistryItemList();
            Iterator<PosPayment> payments = posPaymentList.iterator();
            while (payments.hasNext()) {
                PosPayment posPayment = payments.next();
                List<Purchase> purchases = new ArrayList<Purchase>(posPayment.getPurchases().size());
                for (PosPurchase p: posPayment.getPurchases()){
                        Purchase purchase = new Purchase(p.getDiscount(), p.getSocDiscount(), p.getIdOfOrderDetail(), p.getName(), p.getQty(), p.getrPrice(),
                            p.getRootMenu(), p.getMenuOutput(), p.getType(), p.getMenuGroup(), p.getMenuOrigin(), p.getItemCode(), p.getGuidOfGoods(),
                                p.getIdOfRule(), p.getIdOfMenu(), p.getManufacturer(), p.getGuidPreOrderDetail(), null);
                    purchases.add(purchase);
                }
                // for friendly org access pass here idOfFriendlyOrg instead of null
                Payment payment = new Payment(posPayment.getCardNo(), posPayment.getTime(), posPayment.getOrderDate(), posPayment.getSocDiscount(),
                  posPayment.getTrdDiscount(), posPayment.getGrant(), posPayment.getIdOfClient(), null, posPayment.getIdOfPayForClient(), posPayment.getIdOfOrder(), posPayment.getIdOfCashier(),
                        posPayment.getSumByCard(), posPayment.getSumByCash(), posPayment.getrSum(), posPayment.getIdOfPOS(), posPayment.getConfirmerId(),
                        0, posPayment.getComments(), OrderTypeEnumType.fromInteger(posPayment.getOrderType()), purchases, null, null, null);

                ResPaymentRegistryItem resAcc;
                final PosResPaymentRegistryItem e = new PosResPaymentRegistryItem();
                try {
                    resAcc = coreProcessor.processSyncPaymentRegistryPayment(null, idOfOrg, payment, null, allocatedClients);
                    if (resAcc.getResult() != 0) {
                        LOGGER.error("Failure in response payment registry: " + resAcc);
                    }
                    e.setIdOfOrder(resAcc.getIdOfOrder());
                    e.setResult(resAcc.getResult());
                    e.setError(resAcc.getError());
                    posResPaymentRegistryItemList.getI().add(e);
                } catch (Exception exc) {
                    LOGGER.error(String.format("Failed to process payment == %s", payment), exc);
                    resAcc = new ResPaymentRegistryItem(payment.getIdOfOrder(), 100, "Internal error");
                    e.setIdOfOrder(resAcc.getIdOfOrder());
                    e.setResult(resAcc.getResult());
                    e.setError(resAcc.getError());
                    posResPaymentRegistryItemList.getI().add(e);
                }
            }
            LOGGER.info("createOrder payment.size=" + posPaymentList.size());
            resPaymentRegistry.resultCode = 0L;
            resPaymentRegistry.description = "Ok";
        } catch (Exception e) {
            LOGGER.error("Failed to process POSPayment controller request", e);
            resPaymentRegistry.resultCode = 100L;
            resPaymentRegistry.description = "Internal error";
        }
        return resPaymentRegistry;
    }
}
