/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;

@Component
@Scope(value = "session")
public class CommonStatsPage extends BasicWorkspacePage {
    @Autowired
    DAOService daoService;

    @Override
    public String getPageFilename() {
        return "report/online/commonstats";
    }

    @Override
    public void onShow() throws Exception {
        loadData(false);
    }
    
    public static class StatItem {
        String name; String value;

        public StatItem(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    LinkedList<StatItem> statItems;

    public LinkedList<StatItem> getStatItems() {
        return statItems;
    }



    public void updateData() {
        loadData(true);
    }
    
    private void loadData(boolean loadFromDb) {
        statItems = new LinkedList<StatItem>();
        statItems.add(new StatItem("Клиентов", !loadFromDb?"-":(""+daoService.getStatClientsCount())));
        statItems.add(new StatItem("Клиентов с мобильным телефоном", !loadFromDb?"-":(""+daoService.getStatClientsWithMobile())));
        statItems.add(new StatItem("Клиентов с e-mail", !loadFromDb?"-":(""+daoService.getStatClientsWithEmail())));
        statItems.add(new StatItem("Клиентов с платежными операциями", !loadFromDb?"-":(""+daoService.getStatUniqueClientsWithPaymentTransaction())));
        statItems.add(new StatItem("Клиентов с проходами", !loadFromDb?"-":(""+daoService.getStatUniqueClientsWithEnterEvent())));
        statItems.add(new StatItem("Пополнений лицевых счетов", !loadFromDb?"-":(""+daoService.getStatClientPaymentsCount())));
        statItems.add(new StatItem("Оплаченных заказов", !loadFromDb?"-":(""+daoService.getStatOrdersCount())));
        statItems.add(new StatItem("Проходов", !loadFromDb?"-":(""+daoService.getEnterEventsCount())));
        statItems.add(new StatItem("Отправлено SMS", !loadFromDb?"-":(""+daoService.getClientSmsCount())));
    }

}
