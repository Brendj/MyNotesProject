/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 10:20:05
 * To change this template use File | Settings | File Templates.
 */
public class FunctionViewer {

    public static class Item implements Comparable<Item> {

        private final String functionName;
        private final String functionDesc;
        private final Long functionId;

        public String getFunctionName() {
            return functionName;
        }

        public String getFunctionDesc() {
            return functionDesc;
        }

        public Item(Function function) {
            this.functionId = function.getIdOfFunction();
            this.functionName = function.getFunctionName();
            this.functionDesc = Function.getFunctionDesc(functionName);
        }

        @Override
        public int compareTo(Item o) {
            int res = this.functionName.compareTo(o.functionName);
            if (res == 0) {
                res = this.functionId.compareTo(o.functionId);
            }
            return res;
        }
    }

    private List<Item> onlineReportItems = Collections.emptyList();
    private List<Item> organizationItems = Collections.emptyList();
    private List<Item> contragentItems = Collections.emptyList();
    private List<Item> clientItems = Collections.emptyList();
    private List<Item> visitorItems = Collections.emptyList();
    private List<Item> cardItems = Collections.emptyList();
    private List<Item> wayBillItems = Collections.emptyList();
    private List<Item> serviceItems = Collections.emptyList();
    private List<Item> monitorItems = Collections.emptyList();
    private List<Item> repositoryItems = Collections.emptyList();
    private List<Item> helpdeskItems = Collections.emptyList();
    private List<Item> optionsItems = Collections.emptyList();
    private List<Item> cardOperatorItems = Collections.emptyList();

    public List<Item> getOnlineReportItems() {
        return onlineReportItems;
    }

    public List<Item> getOrganizationItems() {
        return organizationItems;
    }

    public List<Item> getContragentItems() {
        return contragentItems;
    }

    public List<Item> getClientItems() {
        return clientItems;
    }

    public List<Item> getVisitorItems() {
        return visitorItems;
    }

    public List<Item> getCardItems() {
        return cardItems;
    }

    public List<Item> getWayBillItems() {
        return wayBillItems;
    }

    public List<Item> getServiceItems() {
        return serviceItems;
    }

    public List<Item> getMonitorItems() {
        return monitorItems;
    }

    public List<Item> getRepositoryItems() {
        return repositoryItems;
    }

    public List<Item> getHelpdeskItems() {
        return helpdeskItems;
    }

    public List<Item> getOptionsItems() {
        return optionsItems;
    }

    public List<Item> getCardOperatorItems() {
        return cardOperatorItems;
    }

    public void fill(User user) throws Exception {
        List<Item> onlineReportItems = new ArrayList<Item>();
        List<Item> organizationItems = new ArrayList<Item>();
        List<Item> contragentItems = new ArrayList<Item>();
        List<Item> clientItems = new ArrayList<Item>();
        List<Item> visitorItems = new ArrayList<Item>();
        List<Item> cardItems = new ArrayList<Item>();
        List<Item> wayBillItems = new ArrayList<Item>();
        List<Item> serviceItems = new ArrayList<Item>();
        List<Item> monitorItems = new ArrayList<Item>();
        List<Item> repositoryItems = new ArrayList<Item>();
        List<Item> helpdeskItems = new ArrayList<Item>();
        List<Item> optionsItems = new ArrayList<Item>();
        Set<Function> userFunctions = user.getFunctions();
        List<Item> cardOperatorItems = new ArrayList<Item>();
        for (Function function : userFunctions) {
            Item item = new Item(function);
            if (item.getFunctionName().equals("orgEdit") || item.getFunctionName().equals("orgView")) {
                organizationItems.add(item);
            } else if (item.getFunctionName().equals("contraEdit") || item.getFunctionName().equals("contraView")
                    || item.getFunctionName().equals("payProcess") || item.getFunctionName().equals("pmntEdit") || item
                    .getFunctionName().equals("pmntView") || item.getFunctionName().equals("posEdit") || item
                    .getFunctionName().equals("posView")) {
                contragentItems.add(item);
            } else if (item.getFunctionName().equals("clientDel") || item.getFunctionName().equals("clientEdit") || item
                    .getFunctionName().equals("onlineRprtClients") || item.getFunctionName().equals("clientView")) {
                clientItems.add(item);
            } else if (item.getFunctionName().equals("visitorDogmEdit")) {
                visitorItems.add(item);
            } else if (item.getFunctionName().equals("cardEdit") || item.getFunctionName().equals("cardView")) {
                cardItems.add(item);
            } else if (item.getFunctionName().equals("commAcc")) {
                wayBillItems.add(item);
            } else if (item.getFunctionName().equals("servAdm") || item.getFunctionName().equals("servClnt") || item
                    .getFunctionName().equals("servSupp")) {
                serviceItems.add(item);
            } else if (item.getFunctionName().equals("monitor")) {
                monitorItems.add(item);
            } else if (item.getFunctionName().equals("showReportRepository")) {
                repositoryItems.add(item);
            } else if (item.getFunctionName().equals("helpdesk")) {
                helpdeskItems.add(item);
            } else if (item.getFunctionName().equals("workOption") || item.getFunctionName().equals("catEdit") ||
                    item.getFunctionName().equals("catView") || item.getFunctionName().equals("ruleEdit") || item
                    .getFunctionName().equals("ruleView") || item.getFunctionName().equals("reportEdit") || item
                    .getFunctionName().equals("reportView") || item.getFunctionName().equals("supplier") || item.getFunctionName().equals("manualRprt")) {
                optionsItems.add(item);
            } else if (item.getFunctionName().equals("onlineRprt") || item.getFunctionName().equals("onlineRprtComplex")
                    || item.getFunctionName().equals("onlineRprtBenefit") || item.getFunctionName()
                    .equals("onlineRprtRequest") || item.getFunctionName().equals("electronicReconciliationRprt")
                    || item.getFunctionName().equals("onlineRprtMeals") || item.getFunctionName().equals("paidFood")
                    || item.getFunctionName().equals("subscriptionFeeding") || item.getFunctionName()
                    .equals("onlineRprtRefill") || item.getFunctionName().equals("onlineRprtActivity") || item.getFunctionName().equals("onlineRprtCalendar") || item
                    .getFunctionName().equals("clientRprts") || item.getFunctionName().equals("statisticDifferences")
                    || item.getFunctionName().equals("financialControl") || item.getFunctionName().equals("informRprts")
                    || item.getFunctionName().equals("salesRprt") || item.getFunctionName().equals("enterEventRprt")
                    || item.getFunctionName().equals("totalServicesRprt") || item.getFunctionName()
                    .equals("clientsBenefitsRprt") || item.getFunctionName().equals("transactionsRprt") || item
                    .getFunctionName().equals("cardRprts") || item.getFunctionName().equals("countCP") || item.getFunctionName().equals("messageARMinOO")
                    ||item.getFunctionName().equals("coverageNutritionRprt")) {
                onlineReportItems.add(item);
            } else if (item.getFunctionName().equals("cardOperator")) {
                cardOperatorItems.add(item);
            }
        }
        this.onlineReportItems = onlineReportItems;
        Collections.sort(onlineReportItems);
        this.organizationItems = organizationItems;
        Collections.sort(organizationItems);
        this.contragentItems = contragentItems;
        Collections.sort(contragentItems);
        this.clientItems = clientItems;
        Collections.sort(clientItems);
        this.visitorItems = visitorItems;
        this.cardItems = cardItems;
        Collections.sort(cardItems);
        this.wayBillItems = wayBillItems;
        this.serviceItems = serviceItems;
        Collections.sort(serviceItems);
        this.monitorItems = monitorItems;
        this.repositoryItems = repositoryItems;
        this.helpdeskItems = helpdeskItems;
        this.optionsItems = optionsItems;
        this.cardOperatorItems = cardOperatorItems;
        Collections.sort(optionsItems);
    }

    public boolean isEmpty(List<Item> itemList) {
        if (itemList.isEmpty()) {
            return false;
        }
        return true;
    }
}