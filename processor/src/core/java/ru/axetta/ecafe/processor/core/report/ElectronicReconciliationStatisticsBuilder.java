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
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

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
    private final String subReportDir;

    public ElectronicReconciliationStatisticsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
    }

    public ElectronicReconciliationStatisticsBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + ElectronicReconciliationStatisticsReport.class.getSimpleName() + ".jasper";
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
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
        parameterMap.put("SUBREPORT_DIR", subReportDir);

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

        String isppState = getReportProperties().getProperty("isppStateFilter");
        String ppState = getReportProperties().getProperty("ppStateFilter");
        String region = getReportProperties().getProperty("region");

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

        JRDataSource dataSource = buildDataSource(session, startTime, endTime, idOfContragent, idOfOrgList, ISPPStates,
                PPStateEnum, region);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generationDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new ElectronicReconciliationStatisticsReport(generateBeginTime, generationDuration, jasperPrint,
                startTime, endTime, idOfContragent);
    }

    private JRDataSource buildDataSource(Session session, Date startTime, Date endTime, Long idOfContragent,
            List<Long> idOfOrgList, TaloonISPPStatesEnum taloonISPPStatesEnum, TaloonPPStatesEnum taloonPPStatesEnum,
            String region) {


        // Главный запрос
        Criteria criteria = session.createCriteria(TaloonApproval.class);
        criteria.createAlias("org", "org");

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

        if (region != null && region != "") {
            criteria.add(Restrictions.eq("org.district", region));
        }

        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.ge("compositeIdOfTaloonApproval.taloonDate", startTime));
        criteria.add(Restrictions.lt("compositeIdOfTaloonApproval.taloonDate", endTime));
        criteria.addOrder(Order.asc("compositeIdOfTaloonApproval.taloonDate"));

        List<TaloonApproval> taloonApprovalList = criteria.list();

        //Результирующий мапа по которому строиться отчет
        Map<String, ElectronicReconciliationStatisticsItem> map = new HashMap<String, ElectronicReconciliationStatisticsItem>();

        Long rowNum = 1L;


        Long complexCount = 0L;

        Long verificationStatusAgreed = 0L;
        Long verificationStatusNotIndicated = 0L;
        Long powerSupplierStatusAgreed = 0L;
        Long powerSupplierStatusNotIndicated = 0L;
        Long powerSupplierStatusRenouncement = 0L;

        List<ElectronicReconciliationStatisticsSubItem> electronicReconciliationStatisticsSubList;


        for (TaloonApproval taloonApproval : taloonApprovalList) {
            if (!map.containsKey(taloonApproval.getOrg().getShortNameInfoService())) {

                electronicReconciliationStatisticsSubList = new ArrayList<ElectronicReconciliationStatisticsSubItem>();

                ElectronicReconciliationStatisticsItem electronicReconciliationStatisticsItem = new ElectronicReconciliationStatisticsItem(
                        rowNum, taloonApproval.getOrg().getShortNameInfoService(), taloonApproval.getOrg().getType().toString(),
                        taloonApproval.getOrg().getDistrict(), taloonApproval.getOrg().getShortAddress());

                Date date = startTime;
                while (endTime.getTime() > date.getTime()) {
                    ElectronicReconciliationStatisticsSubItem electronicSubItem = new ElectronicReconciliationStatisticsSubItem(
                            CalendarUtils.dateShortToStringFullYear(date), date, complexCount, verificationStatusAgreed,
                            verificationStatusNotIndicated, powerSupplierStatusAgreed, powerSupplierStatusNotIndicated,
                            powerSupplierStatusRenouncement);
                    electronicReconciliationStatisticsSubList.add(electronicSubItem);
                    date = CalendarUtils.addOneDay(date);
                }

                electronicReconciliationStatisticsItem.getElectronicReconciliationStatisticsSubItems()
                        .addAll(electronicReconciliationStatisticsSubList);

                map.put(taloonApproval.getOrg().getShortNameInfoService(), electronicReconciliationStatisticsItem);
                rowNum++;
            }
        }

        for (TaloonApproval taloonApproval : taloonApprovalList) {

            List<ElectronicReconciliationStatisticsSubItem> electronicReconciliationStatisticsSubItems = map.get(taloonApproval.getOrg().getShortNameInfoService()).getElectronicReconciliationStatisticsSubItems();

            for (int i = 0; i < electronicReconciliationStatisticsSubItems.size(); i++) {

                ElectronicReconciliationStatisticsSubItem subItem = electronicReconciliationStatisticsSubItems.get(i);

                if (CalendarUtils.dateShortToStringFullYear(taloonApproval.getCompositeIdOfTaloonApproval().getTaloonDate()).equals(subItem.getDate())) {

                    String ispp = taloonApproval.getIsppState().toString();
                    if (ispp.equals("Согласовано")) {
                        ++verificationStatusAgreed;
                    }
                    if (ispp.equals("Не указано")) {
                        ++verificationStatusNotIndicated;
                    }

                    String pp = taloonApproval.getPpState().toString();
                    if (pp.equals("Согласовано")) {
                        ++powerSupplierStatusAgreed;
                    }
                    if (pp.equals("Не указано")) {
                        ++powerSupplierStatusNotIndicated;
                    }
                    if (pp.equals("Отказ")) {
                        ++powerSupplierStatusRenouncement;
                    }

                    electronicReconciliationStatisticsSubItems.get(i).setComplexCount(subItem.getComplexCount() + 1L);
                    electronicReconciliationStatisticsSubItems.get(i).setVerificationStatusAgreed(
                            subItem.getVerificationStatusAgreed() + verificationStatusAgreed);
                    electronicReconciliationStatisticsSubItems.get(i).setVerificationStatusNotIndicated(
                            subItem.getVerificationStatusNotIndicated() + verificationStatusNotIndicated);
                    electronicReconciliationStatisticsSubItems.get(i).setPowerSupplierStatusAgreed(
                            subItem.getPowerSupplierStatusAgreed() + powerSupplierStatusAgreed);
                    electronicReconciliationStatisticsSubItems.get(i).setPowerSupplierStatusNotIndicated(
                            subItem.getPowerSupplierStatusNotIndicated() + powerSupplierStatusNotIndicated);
                    electronicReconciliationStatisticsSubItems.get(i).setPowerSupplierStatusRenouncement(
                            subItem.getPowerSupplierStatusRenouncement() + powerSupplierStatusRenouncement);

                    verificationStatusAgreed = 0L;
                    verificationStatusNotIndicated = 0L;
                    powerSupplierStatusAgreed = 0L;
                    powerSupplierStatusNotIndicated = 0L;
                    powerSupplierStatusRenouncement = 0L;
                }
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
