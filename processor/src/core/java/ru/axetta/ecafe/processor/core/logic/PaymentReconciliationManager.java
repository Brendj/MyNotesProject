/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.collections.ListUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
@Scope("singleton")
public class PaymentReconciliationManager {
    @PersistenceContext
    EntityManager em;
    
    public static class RegistryItem {
        String dt;
        Long sum;
        Long contractId;
        String idOfPayment;
        
        public String getDifferencesAsString(PaymentItem pi) {
            String info="";
            if (pi.sum!=sum) info+="Отличаются суммы: "+pi.sum+"/"+sum+";";
            if (pi.contractId!=contractId) info+="Отличаются номера л/с: "+pi.contractId+"/"+contractId+";";
            return info;
        }

        public RegistryItem(String dt, Long sum, Long contractId, String idOfPayment) {
            this.dt = dt;
            this.sum = sum;
            this.contractId = contractId;
            this.idOfPayment = idOfPayment;
        }

        @Override
        public String toString() {
            return "Запись_реестра{" +
                    "время=" + dt +
                    ", сумма=" + sum +
                    ", лицевой счет=" + contractId +
                    ", ид. платежа='" + idOfPayment + '\'' +
                    '}';
        }
    }
    public static class PaymentItem {
        Date dt;
        long sum;
        long contractId;
        String idOfPayment;
        @Override
        public String toString() {
            return "Запись_базы{" +
                    "время=" + CalendarUtils.dateTimeToString(dt) +
                    ", сумма=" + sum +
                    ", лицевой счет=" + contractId +
                    ", ид. платежа='" + idOfPayment + '\'' +
                    '}';
        }
    }
    
    public static class Difference {
        public static int TYPE_REGISTRY_ITEM_NOT_FOUND=1;
        public static final int TYPE_ATTRIBUTES_DIFFER = 2;
        public static final int TYPE_CLIENT_PAYMENT_MISSING = 3;
        int type;
        RegistryItem registryItem;
        PaymentItem paymentItem;

        public Difference(int type, RegistryItem registryItem, PaymentItem paymentItem) {
            this.type = type;
            this.registryItem = registryItem;
            this.paymentItem = paymentItem;
        }

        public int getType() {
            return type;
        }

        public RegistryItem getRegistryItem() {
            return registryItem;
        }

        public PaymentItem getPaymentItem() {
            return paymentItem;
        }

        @Override
        public String toString() {
            if (type==TYPE_REGISTRY_ITEM_NOT_FOUND) {
                return "Не найдена запись реестра: "+registryItem;
            }
            else if (type==TYPE_ATTRIBUTES_DIFFER) {
                return "Различаются атрибуты: "+registryItem+"/"+paymentItem+": "+registryItem.getDifferencesAsString(paymentItem);
            }
            else if (type==TYPE_CLIENT_PAYMENT_MISSING) {
                return "Запись в базе не найдена в реестре: "+paymentItem;
            }
            return "Неизвестно";
        }
    }

    @Transactional
    public LinkedList<Difference> processRegistry(long idOfContragentAgent, Long idOfContragentTsp, Date dtFrom, Date dtTo,
            List<RegistryItem> registryItems) throws Exception {
        Contragent ca = em.find(Contragent.class, idOfContragentAgent);
        if (ca==null) throw new Exception("Контрагент не найден: "+idOfContragentAgent);
        Contragent caReceiver = null;
        if (idOfContragentTsp!=null) {
            caReceiver = em.find(Contragent.class, idOfContragentTsp);
            if (caReceiver==null) throw new Exception("Контрагент не найден: "+idOfContragentTsp);
        }

        List<Object[]> clientPayments = DAOUtils.getClientPaymentsDataForPeriod(em, dtFrom, dtTo, caReceiver);
        SortedMap<String, PaymentItem> clientPaymentsMap = new TreeMap<String, PaymentItem>();
        for (Object[] ci : clientPayments) {
            PaymentItem pi = new PaymentItem();
            pi.contractId = (Long)ci[0];
            pi.dt = (Date)ci[1];
            pi.sum = (Long)ci[2];
            pi.idOfPayment = (String)ci[3];
            clientPaymentsMap.put(pi.idOfPayment, pi);
        }
        LinkedList<Difference> differences = new LinkedList<Difference>();
        
        for (RegistryItem ri : registryItems) {
            PaymentItem pi = clientPaymentsMap.get(ri.idOfPayment);
            if (pi==null) {
                differences.add(new Difference(Difference.TYPE_REGISTRY_ITEM_NOT_FOUND, ri, null));
            }
            else {
                if (ri.getDifferencesAsString(pi).length()>0) {
                    differences.add(new Difference(Difference.TYPE_ATTRIBUTES_DIFFER, ri, pi));
                }
                clientPaymentsMap.remove(ri.idOfPayment);
            }
        }
        for (Map.Entry<String, PaymentItem> e : clientPaymentsMap.entrySet()) {
            differences.add(new Difference(Difference.TYPE_CLIENT_PAYMENT_MISSING, null, e.getValue()));
        }
        return differences;
    }
}
