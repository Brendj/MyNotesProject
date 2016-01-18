package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.*;

/*
 * Copyright (c) 2016. Axetta LLC. All Rights/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 18.01.16
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class BalanceLeavingReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public BalanceLeavingReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateTime = new Date();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        calendar.setTime(endTime);
        int month = calendar.get(Calendar.MONTH);
        parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
        parameterMap.put("year", calendar.get(Calendar.YEAR));
        parameterMap.put("endDate", endTime);
        Long idOfContragent1 = -1L;
        if (contragent != null) {
            parameterMap.put("contragentName", contragent.getContragentName());
            idOfContragent1 = contragent.getIdOfContragent();
        }
        String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        String clientGroupIdString = reportProperties.getProperty("clientGroupId");
        Long clientGroupId = null;
        try {
            clientGroupId = Long.valueOf(clientGroupIdString);
        } catch (NumberFormatException e) {
            clientGroupId = ClientGroupMenu.CLIENT_ALL;
        }
        return null;
    }

    private JRDataSource createDataSource(Session session, Date endTime, List<Long> idOfOrgList, Long clientGroupId,
            Integer clientBalanceCondition) throws Exception {
        Long idOfContragent = null;
        if (contragent != null) {
            idOfContragent = contragent.getIdOfContragent();
        }

        List<BalanceLeavingItem> result = buildReportItems(session, idOfContragent, idOfOrgList, endTime,
                clientGroupId, clientBalanceCondition);
        return new JRBeanCollectionDataSource(result);
    }

    private List<BalanceLeavingItem> buildReportItems(Session session, Long idOfContragent, List<Long> idOfOrgList, Date endTime,
            Long clientGroupId, Integer clientBalanceCondition) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public class BalanceLeavingItem {
        private String idOfClient;

    }
}
