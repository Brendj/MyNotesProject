/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
@Scope("singleton")
public class PaymentReconciliationManager {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    
    public static class RegistryItem {
        String dt;
        String dtNormal;
        Long sum;
        Long contractId;
        String idOfPayment;
        
        public String getDifferencesAsString(PaymentItem pi) {
            String info="";
            if (pi == null) {
                return info;
            }
            if (sum != null && pi.sum!=sum) {
                info= String.format("%sОтличаются суммы: %d/%d;", info, pi.sum, sum);
            }
            if (pi.contractId!=contractId) {
                info= String.format("%sОтличаются номера л/с: %d/%d;", info, pi.contractId, contractId);
            }
            return info;
        }

        public RegistryItem(String dt, String dtNormal, Long sum, Long contractId, String idOfPayment) {
            this.dt = dt;
            this.dtNormal = dtNormal;
            this.sum = sum;
            this.contractId = contractId;
            this.idOfPayment = idOfPayment;
        }

        @Override
        public String toString() {
            return String.format("Запись_реестра{время=%s, сумма=%d, лицевой счет=%d, ид. платежа='%s'}", dt, sum,
                    contractId, idOfPayment);
        }

        public String toCsvString() {
            return String.format("Запись реестра;%s;%s;%s;%s", dt, sum, contractId, idOfPayment);
        }

        public String toXmlString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<PT ContractId=\"").append(ContractIdFormat.format(contractId)).append("\" IdOfPayment=\"")
                    .append(idOfPayment).append("\" PayTime=\"").append(dtNormal).append("\" Sum=\"").append(sum)
                    .append("\"/>");
            return sb.toString();
        }
    }

    public static class PaymentItem {
        Date dt;
        long sum;
        long contractId;
        String idOfPayment;
        @Override
        public String toString() {
            return String.format("Запись_базы{время=%s, сумма=%d, лицевой счет=%d, ид. платежа='%s'}",
                    CalendarUtils.dateTimeToString(dt), sum, contractId, idOfPayment);
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
                return String.format("Не найдена запись реестра: %s", registryItem);
            }
            else if (type==TYPE_ATTRIBUTES_DIFFER) {
                return String.format("Различаются атрибуты: %s/%s: %s", registryItem, paymentItem,
                        registryItem.getDifferencesAsString(paymentItem));
            }
            else if (type==TYPE_CLIENT_PAYMENT_MISSING) {
                return String.format("Запись в базе не найдена в реестре: %s", paymentItem);
            }
            return "Неизвестно";
        }
    }

    @Transactional
    public LinkedList<Difference> processRegistry(long idOfContragentAgent, Long idOfContragentTsp, Date dtFrom, Date dtTo,
            List<RegistryItem> registryItems) throws Exception {
        Contragent ca = em.find(Contragent.class, idOfContragentAgent);
        if (ca==null) throw new Exception(String.format("Контрагент не найден: %d", idOfContragentAgent));
        Contragent caReceiver = null;
        if (idOfContragentTsp!=null) {
            caReceiver = em.find(Contragent.class, idOfContragentTsp);
            if (caReceiver==null) throw new Exception(String.format("Контрагент не найден: %d", idOfContragentTsp));
        }

        List<Object[]> clientPayments = DAOUtils.getClientPaymentsDataForPeriod(em, dtFrom, dtTo, caReceiver, ca);
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
