/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.feeding;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.StateDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.10.13
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeedingReportBuilder extends BasicReportForOrgJob.Builder{

    private final String templateFilename;
    public static final String MESSAGE = "Приостановлено";

    public SubscriptionFeedingReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public SubscriptionFeedingJasperReport build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        Date generateTime = new Date();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("idOfOrg", org.getIdOfOrg());
        parameterMap.put("orgName", org.getOfficialName());
        calendar.setTime(startTime);
        int month = calendar.get(Calendar.MONTH);
        parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        parameterMap.put("month", month + 1);
        parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
        parameterMap.put("year", calendar.get(Calendar.YEAR));
        parameterMap.put("startDate", startTime);
        parameterMap.put("endDate", endTime);

        SubscriptionFeedingReport report = build(session, calendar, org.getIdOfOrg());
        JRDataSource dataSource = new JRBeanCollectionDataSource(report.getSubscriptionFeedingReportItems());
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        return new SubscriptionFeedingJasperReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                jasperPrint, startTime, endTime, org.getIdOfOrg());
    }

    public SubscriptionFeedingReport build(Session session, Calendar calendar, Long idOfOrg) throws Exception {
        Date generateTime = new Date();
        List<SubscriptionFeedingReportItem> subscriptionFeedingReportItems = new LinkedList<SubscriptionFeedingReportItem>();
        Criteria cycleDiagramCriteria = session.createCriteria(CycleDiagram.class);
        cycleDiagramCriteria.add(Restrictions.eq("stateDiagram", StateDiagram.ACTIVE));
        cycleDiagramCriteria.add(Restrictions.eq("orgOwner", idOfOrg));
        List cycleDiagrams = cycleDiagramCriteria.list();
        for (Object o: cycleDiagrams){
            CycleDiagram cycleDiagram = (CycleDiagram) o;
            final Client client = cycleDiagram.getClient();
            Long subBalance = client.getSubBalance1();
            Criteria subscriptionFeedingCriteria = session.createCriteria(SubscriptionFeeding.class);
            subscriptionFeedingCriteria.add(Restrictions.eq("wasSuspended",true));
            subscriptionFeedingCriteria.add(Restrictions.eq("client", client));
            subscriptionFeedingCriteria.setMaxResults(1);
            SubscriptionFeeding subscriptionFeeding = (SubscriptionFeeding) subscriptionFeedingCriteria.uniqueResult();
            long[] days = new long[7];
           /* days[0] = cycleDiagram.getSundayPrice();
            days[1] = cycleDiagram.getMondayPrice();
            days[2] = cycleDiagram.getTuesdayPrice();
            days[3] = cycleDiagram.getWednesdayPrice();
            days[4] = cycleDiagram.getThursdayPrice();
            days[5] = cycleDiagram.getFridayPrice();
            days[6] = cycleDiagram.getSaturdayPrice();*/
            Calendar diagramCalendar = Calendar.getInstance();
            Date currentDate = new Date();
            diagramCalendar.setTime(currentDate);
            long currentBalance = subBalance;
            int dayWeek = diagramCalendar.get(Calendar.DAY_OF_WEEK);
            while (currentBalance>0){
                currentBalance = currentBalance - days[dayWeek % 7];
                currentDate = CalendarUtils.addOneDay(currentDate);
                dayWeek++;
            }
            subscriptionFeedingReportItems.add(new SubscriptionFeedingReportItem(client.getPerson(), client, subscriptionFeeding.getDateActivateSubscription(),  cycleDiagram,  currentDate));
        }
        return new SubscriptionFeedingReport(generateTime, new Date().getTime() - generateTime.getTime(), subscriptionFeedingReportItems);
    }

}
