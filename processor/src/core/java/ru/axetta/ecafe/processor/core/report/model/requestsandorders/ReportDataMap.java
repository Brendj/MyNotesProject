/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 10.11.14
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class ReportDataMap extends HashMap<String, FeedingPlan> {

    private static SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("EE dd.MM", new Locale("ru"));

    private ReportDataMap(List complexList, Date beginDate, Date endDate) {
        List<Date> datesSet = new LinkedList<Date>();
        Date midDate = beginDate;
        do {
            datesSet.add(midDate);
            midDate = CalendarUtils.addDays(midDate, 1);
        } while (endDate.getTime() >= midDate.getTime());
        String orgName = "Итого";
        Complex complex = new Complex();
        for (Object obj1: complexList) {
            ComplexInfo complexInfo = (ComplexInfo) obj1;
            DateElement dateElement = new DateElement();
            for (Object obj2: datesSet) {
                Date date = (Date) obj2;
                Element element = new Element();
                element.put(State.Requested, 0L);
                element.put(State.Ordered, 0L);
                dateElement.put(date, element);
            }
            complex.put(complexInfo.getComplexName(), dateElement);
        }
        FeedingPlan feedingPlan = new FeedingPlan();
        feedingPlan.put(FeedingPlanType.PAY_PLAN, complex);
        feedingPlan.put(FeedingPlanType.REDUCED_PRICE_PLAN, complex);
        feedingPlan.put(FeedingPlanType.SUBSCRIPTION_FEEDING, complex);
        this.put(orgName, feedingPlan);
    }

    public ReportDataMap() {
    }

    @Override
    public FeedingPlan put(String key, FeedingPlan feedingPlan) {
        if (this.containsKey(key)) {
            FeedingPlan oldFeedingPlan = this.get(key);
            for (Object obj: feedingPlan.keySet()) {
                FeedingPlanType type = (FeedingPlanType) obj;
                oldFeedingPlan.put(type, feedingPlan.get(type));
            }
            return super.put(key, oldFeedingPlan);
        } else {
            return super.put(key, feedingPlan);
        }
    }

    public ReportDataMap complement(Date beginDate, Date endDate) {
        Date midDate = beginDate;
        do {
            if (!hasDate(midDate)) {
                String orgName = this.keySet().iterator().next();
                FeedingPlanType feedingPlanType = this.get(orgName).keySet().iterator().next();
                String complex = this.get(orgName).get(feedingPlanType).keySet().iterator().next();
                Date date = midDate;
                Element element = new Element();
                element.put(State.Ordered, 0L);
                this.get(orgName).get(feedingPlanType).get(complex).put(date, element);
            }
            midDate = CalendarUtils.addDays(midDate, 1);
        } while (endDate.getTime() >= midDate.getTime());
        return this;
    }

    private Boolean hasDate(Date date) {
        for (String orgName: this.keySet()) {
            for (FeedingPlanType feedingPlanType: this.get(orgName).keySet()) {
                for (String complex: this.get(orgName).get(feedingPlanType).keySet()) {
                    for (Date dateForCheck: this.get(orgName).get(feedingPlanType).get(complex).keySet()) {
                        if (date.equals(dateForCheck)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
