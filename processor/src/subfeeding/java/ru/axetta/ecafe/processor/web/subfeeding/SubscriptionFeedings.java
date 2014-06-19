/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubscriptionFeedingExt;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubscriptionFeedingListResult;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.subfeeding.SubscriptionFeeding;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.06.14
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeedings implements Serializable {

    private ClientRoomController clientRoomController;
    private SubscriptionFeedingListResult subscriptionFeedingResult;
    private List<SubscriptionFeeding> subscriptionFeedings;

    private SubscriptionFeedings(ClientRoomController clientRoomController) {
        this.clientRoomController = clientRoomController;
    }

    public static SubscriptionFeedings buildHistoryList(
            ClientRoomController clientRoomController,
            Long contractId,
            Date startDate,
            Date endDate){
        SubscriptionFeedings feedings = new SubscriptionFeedings(clientRoomController);
        feedings.buildList(contractId, startDate, endDate);
        return feedings;
    }

    private void buildList(Long contractId, Date startDate, Date endDate){
        subscriptionFeedingResult = getSubFeeding(contractId, startDate, endDate);
        boolean subfeedingExist = isSubscriptionFeedingExist();
        subscriptionFeedings = new ArrayList<SubscriptionFeeding>();
        if(subfeedingExist){
            for (SubscriptionFeedingExt subscriptionFeedingExt : subscriptionFeedingResult.subscriptionFeedingListExt.getS()){
                subscriptionFeedings.add(new SubscriptionFeeding(subscriptionFeedingExt));
            }
            Collections.sort(subscriptionFeedings, buildUpdateDateComparator());
        }
    }

    public Boolean isSubscriptionFeedingExist() {
        return subscriptionFeedingResult != null && subscriptionFeedingResult.subscriptionFeedingListExt != null &&
                !subscriptionFeedingResult.subscriptionFeedingListExt.getS().isEmpty();
    }

    public List<SubscriptionFeeding> getSubscriptionFeedings() {
        return subscriptionFeedings;
    }

    static SubscriptionFeedingCompareByUpdateDate buildUpdateDateComparator(){
        return new SubscriptionFeedingCompareByUpdateDate();
    }

    static class SubscriptionFeedingCompareByUpdateDate implements Comparator<SubscriptionFeeding> {
        @Override
        public int compare(SubscriptionFeeding o1, SubscriptionFeeding o2) {
            return o1.getUpdateDate().compareTo(o2.getUpdateDate());
        }

    }

    private SubscriptionFeedingListResult getSubFeeding(Long contractId, Date startDate, Date endDate) {
        return clientRoomController.getSubscriptionFeedingHistoryList(contractId, startDate, endDate);
    }


}
