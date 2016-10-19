/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.10.16
 * Time: 12:26
 */

public class ElectronicReconciliationStatisticsBuilder extends BasicReportForAllOrgJob.Builder {

    Long idOfContragent = -1L;

    private final String templateFilename;

    public ElectronicReconciliationStatisticsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public ElectronicReconciliationStatisticsBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + ElectronicReconciliationStatisticsReport.class.getSimpleName() + ".jasper";
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));

        if (contragent != null) {
            parameterMap.put("contragentName", contragent.getContragentName());
            idOfContragent = contragent.getIdOfContragent();
        } else {
            throw new Exception("Поставщик не указан.");
        }

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
        // Список организаций
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }

       /* Object region = reportProperties.getProperty("region");
        if (region == null) {
            throw new IllegalArgumentException("Не указан Округ");
        }*/

        String isppState = getReportProperties().getProperty("isppStateFilter");
        String ppState = getReportProperties().getProperty("ppStateFilter");

        TaloonISPPStatesEnum ISPPStates = null;
        for (TaloonISPPStatesEnum taloonISPPStatesEnum : TaloonISPPStatesEnum.values()) {
            if (isppState.equalsIgnoreCase(taloonISPPStatesEnum.toString())) {
                ISPPStates = taloonISPPStatesEnum;
            }
        }

        TaloonPPStatesEnum PPStateEnum = null;
        for (TaloonPPStatesEnum taloonPPStatesEnum : TaloonPPStatesEnum.values()) {
            if (ppState.equalsIgnoreCase(taloonPPStatesEnum.toString())) {
                PPStateEnum = taloonPPStatesEnum;
            }
        }

        JRDataSource dataSource = buildDataSource(session, startTime, endTime, idOfContragent, idOfOrgList, ISPPStates, PPStateEnum);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generationDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new ElectronicReconciliationStatisticsReport(generateBeginTime, generationDuration, jasperPrint,
                startTime, endTime, idOfContragent);
    }

    private JRDataSource buildDataSource(Session session, Date startTime, Date endTime, Long idOfContragent,
            List<Long> idOfOrgList, TaloonISPPStatesEnum taloonISPPStatesEnum, TaloonPPStatesEnum taloonPPStatesEnum) {


        // Главный запрос
        Criteria criteria = session.createCriteria(TaloonApproval.class);

        if (!idOfOrgList.isEmpty()) {
            criteria.add(Restrictions.in("compositeIdOfTaloonApproval.idOfOrg", idOfOrgList));
        } else {
            Contragent contragentById = (Contragent) session.load(Contragent.class, idOfContragent);

            List<Long> idOfOrgs = new ArrayList<Long>();
            for (Org contrOrg : contragentById.getOrgs()) {
                idOfOrgs.add(contrOrg.getIdOfOrg());
            }

            criteria.add(Restrictions.in("compositeIdOfTaloonApproval.idOfOrg", idOfOrgs));
        }

        if (taloonISPPStatesEnum != null) {
            criteria.add(Restrictions.eq("isppState", taloonISPPStatesEnum));
        }

        if (taloonPPStatesEnum != null) {
            criteria.add(Restrictions.eq("ppState", taloonPPStatesEnum));
        }

        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.ge("compositeIdOfTaloonApproval.taloonDate", startTime));
        criteria.add(Restrictions.lt("compositeIdOfTaloonApproval.taloonDate", endTime));

        List<TaloonApproval> taloonApprovalList = criteria.list();

        //Результирующий мапа по которому строиться отчет
        Map<String, ElectronicReconciliationStatisticsItem> map = new HashMap<String, ElectronicReconciliationStatisticsItem>();

        Long rowNum = 1L;

        for (TaloonApproval taloonApproval : taloonApprovalList) {
            if (map.containsKey(taloonApproval.getOrg().getShortName())) {
                ElectronicReconciliationStatisticsSubItem electronicReconciliationStatisticsSubItem = new ElectronicReconciliationStatisticsSubItem(
                        CalendarUtils.dateShortToStringFullYear(
                                taloonApproval.getCompositeIdOfTaloonApproval().getTaloonDate()),
                        taloonApproval.getCompositeIdOfTaloonApproval().getTaloonDate(),
                        taloonApproval.getIsppState().toString(), taloonApproval.getPpState().toString());

                map.get(taloonApproval.getOrg().getShortName()).getElectronicReconciliationStatisticsSubItems()
                        .add(electronicReconciliationStatisticsSubItem);
            } else {
                ElectronicReconciliationStatisticsItem electronicReconciliationStatisticsItem = new ElectronicReconciliationStatisticsItem(
                        rowNum, taloonApproval.getOrg().getShortName(), taloonApproval.getOrg().getType().toString(),
                        taloonApproval.getOrg().getDistrict(), taloonApproval.getOrg().getAddress());

                ElectronicReconciliationStatisticsSubItem electronicReconciliationStatisticsSubItem = new ElectronicReconciliationStatisticsSubItem(
                        CalendarUtils.dateShortToStringFullYear(
                                taloonApproval.getCompositeIdOfTaloonApproval().getTaloonDate()),
                        taloonApproval.getCompositeIdOfTaloonApproval().getTaloonDate(),
                        taloonApproval.getIsppState().toString(), taloonApproval.getPpState().toString());

                electronicReconciliationStatisticsItem.getElectronicReconciliationStatisticsSubItems()
                        .add(electronicReconciliationStatisticsSubItem);

                map.put(taloonApproval.getOrg().getShortName(), electronicReconciliationStatisticsItem);
                rowNum++;
            }
        }

        // Результирующий лист
        List<ElectronicReconciliationStatisticsItem> electronicReconciliationStatisticsItemList = new ArrayList<ElectronicReconciliationStatisticsItem>();
        electronicReconciliationStatisticsItemList.addAll(map.values());

        for (ElectronicReconciliationStatisticsItem electronicReconciliationStatisticsItem : electronicReconciliationStatisticsItemList) {
            Collections.sort(electronicReconciliationStatisticsItem.getElectronicReconciliationStatisticsSubItems());
        }

        Collections.sort(electronicReconciliationStatisticsItemList);

        return new JRBeanCollectionDataSource(electronicReconciliationStatisticsItemList);
    }
}
