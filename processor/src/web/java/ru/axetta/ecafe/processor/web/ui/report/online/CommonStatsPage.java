/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    private List<StatItem> statItems;

    public List<StatItem> getStatItems() {
        return statItems;
    }



    public void updateData() {
        loadData(true);
    }
    
    private void loadData(boolean loadFromDb) {
        statItems = new ArrayList<StatItem>(12);
        statItems.add(new StatItem("Клиентов", !loadFromDb?"-":(""+daoService.getStatClientsCount())));
        statItems.add(new StatItem("Клиентов с мобильным телефоном", !loadFromDb?"-":(""+daoService.getStatClientsWithMobile())));
        statItems.add(new StatItem("Клиентов с e-mail", !loadFromDb?"-":(""+daoService.getStatClientsWithEmail())));
        statItems.add(new StatItem("Клиентов с пополнениями", !loadFromDb?"-":(""+daoService.getStatUniqueClientsWithPaymentTransaction())));
        statItems.add(new StatItem("Клиентов с проходами", !loadFromDb?"-":(""+daoService.getStatUniqueClientsWithEnterEvent())));
        statItems.add(new StatItem("Пополнений лицевых счетов", !loadFromDb?"-":(""+daoService.getStatClientPaymentsCount())));
        statItems.add(new StatItem("Оплаченных заказов", !loadFromDb?"-":(""+daoService.getStatOrdersCount())));
        statItems.add(new StatItem("Проходов", !loadFromDb?"-":(""+daoService.getEnterEventsCount())));
        statItems.add(new StatItem("Отправлено SMS", !loadFromDb?"-":(""+daoService.getClientSmsCount())));
        statItems.add(new StatItem("Клиентов с покупкой платного питания", !loadFromDb?"-":(""+daoService.callClientsPayPowerPurchase())));
        statItems.add(new StatItem("Клиентов с покупкой льготного питания", !loadFromDb?"-":(""+daoService.callClientsWithPurchaseOfMealBenefits())));
        statItems.add(new StatItem("Клиентов с покупкой платного питания / покупкой льготного питания", !loadFromDb?"-":(""+daoService.callClientsWithPurchaseOfFoodPayPurchaseReducedPriceMeals())));
    }

}
