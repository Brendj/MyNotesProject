/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;

public class PreordersReport extends BasicReportForOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по предварительным заказам";
    public static final String[] TEMPLATE_FILE_NAMES = {"PreordersReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};
    final public static String P_ID_OF_CLIENTS="idOfClients";

    final private static Logger logger = LoggerFactory.getLogger(PreordersReport.class);

    public static class Builder extends BasicReportForOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));
            parameterMap.put("reportName", REPORT_NAME);
            parameterMap.put("SUBREPORT_DIR", RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());

            String idOfOrgString = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            Long idOfOrg = Long.parseLong(idOfOrgString);

            Org org = (Org) session.load(Org.class, idOfOrg);
            if (null != org) {
                parameterMap.put("shortName", org.getShortNameInfoService());
                parameterMap.put("address", org.getAddress());
            } else {
                parameterMap.put("shortName", "не указано");
                parameterMap.put("address", "не указано");
            }

            String idOfClients = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_OF_CLIENTS));
            List<String> stringClientsList = Arrays.asList(StringUtils.split(idOfClients, ','));
            List<Long> idOfClientList = new ArrayList<Long>();
            for (String idOfClient : stringClientsList) {
                idOfClientList.add(Long.parseLong(idOfClient));
            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, idOfOrg, idOfClientList, parameterMap);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new PreordersReport(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Long idOfOrg,
                List<Long> idOfClientList, Map<String, Object> parameterMap) throws Exception {
            HashMap<Long, PreorderReportClientItem> result = new HashMap<Long, PreorderReportClientItem>();
            PreorderReportTotalItem preorderReportTotalItem = new PreorderReportTotalItem();
            HashMap<String, PreorderReportComplexItem> preorderReportTotalItems = new HashMap<String, PreorderReportComplexItem>();
            PreorderReportComplexItem complexItem = null;
            String conditions = "";
            if (idOfClientList.size() > 0) {
                conditions += " and c.idofclient in (:clients) ";
            }
            Query query = session.createSQLQuery(
                   "SELECT distinct o.shortnameinfoservice, o.address, "
                    + "     c.contractid, p.surname || ' ' || p.firstname || ' ' || p.secondname AS clientname, cg.groupname, "
                    + "     pc.preorderdate, pc.amount AS complexAmount, pmd.amount AS menudetailAmount, pc.complexname, "
                    + "     pmd.menudetailname, pc.complexPrice, pmd.menudetailPrice, "
                    + "     pc.idofregularpreorder IS NOT NULL OR pmd.idofregularpreorder IS NOT NULL AS isRegularPreorder,"
                    + "     pc.idofpreordercomplex, pl.idofpreorderlinkod is not null as isPayed "
                    + "FROM cf_preorder_complex pc "
                    + "INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                    + "INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                    + "INNER JOIN cf_clientgroups cg ON cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                    + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex AND pc.amount = 0 "
                    + "LEFT JOIN (SELECT idofpreorderlinkod, preorderguid "
                    + "           FROM cf_preorder_linkod pl "
                    + "           INNER JOIN cf_orders o ON o.idoforg = pl.idoforg AND o.idoforder = pl.idoforder AND o.state = :orderStateCommited) pl "
                    + "     ON pl.preorderguid = pc.guid "
                    + "WHERE (pc.amount > 0 OR pmd.amount > 0) "
                    + "     and (pc.preorderdate between :startDate and :endDate "
                    + "     or pmd.preorderdate between :startDate and :endDate) "
                    + "     and o.idoforg = :idOfOrg and coalesce(pc.deletedstate, 0) = 0 and coalesce(pmd.deletedstate, 0) = 0 "
                    + conditions
                    + "ORDER BY cg.groupname, clientname, pc.preorderdate, pc.idofpreordercomplex, pc.complexname, pmd.menudetailname");
            query.setParameter("startDate", CalendarUtils.startOfDay(startTime).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(endTime).getTime());
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("orderStateCommited", Order.STATE_COMMITED);
            if (idOfClientList.size() > 0) {
                query.setParameterList("clients", idOfClientList);
            }
            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                String shortNameInfoService = (String) row[0];
                String address = (String) row[1];
                Long contractId = ((BigInteger) row[2]).longValue();
                String clientName = (String) row[3];
                String clientGroup = (String) row[4];
                Date preorderDate = new Date(((BigInteger) row[5]).longValue());
                Integer complexAmount = (Integer) row[6];
                Integer menudetailAmount = (Integer) row[7];
                String complexName = (String) row[8];
                String menudetailName = (String) row[9];
                Long complexPrice = ((BigInteger) row[10]).longValue();
                Long menudetailPrice = (null != row[11]) ? ((BigInteger) row[11]).longValue() : null;
                Boolean isRegularPreorder = (Boolean) row[12];

                Boolean isPayed = (Boolean) row[14];
                if (!result.containsKey(contractId)) {
                    result.put(contractId, new PreorderReportClientItem(idOfOrg, shortNameInfoService, address, contractId,
                            clientName, clientGroup));
                }

                if (!preorderReportTotalItems.containsKey(complexName)) {
                    PreorderReportComplexItem preorderReportComplexItem = new PreorderReportComplexItem(complexName);
                    if (0 != complexAmount) {
                        preorderReportComplexItem.setPreorderPrice(complexPrice);
                        preorderReportComplexItem.calculateTotalPrice();
                    }
                    preorderReportTotalItems.put(complexName, preorderReportComplexItem);
                }

                PreorderReportComplexItem item = preorderReportTotalItems.get(complexName);
                if (!result.get(contractId).isComplexExists(preorderDate, complexName)) {
                    complexItem = new PreorderReportComplexItem(preorderDate, complexAmount, complexName,
                            complexAmount == 0 ? null : complexPrice, isRegularPreorder, isPayed);
                    result.get(contractId).getPreorderComplexItems().add(complexItem);

                    if (0 == complexAmount) {
                        item.setAmount(item.getAmount() + 1);
                    }
                }

                if (null != complexItem && complexItem.getAmount() == 0) {
                    complexItem.getDishes()
                            .add(new PreorderReportItem(preorderDate, menudetailAmount, menudetailName, menudetailPrice,
                                    isRegularPreorder, isPayed));
                    item.appendToTotalDishes(new PreorderReportItem(null, menudetailAmount, menudetailName,
                            menudetailPrice, false, false));
                } else if (null != complexItem && complexItem.getAmount() != 0) {
                    item.setAmount(item.getAmount() + complexAmount);
                }
            }

            List<PreorderReportClientItem> resultClientList = new ArrayList<PreorderReportClientItem>(result.values());
            Collections.sort(resultClientList);
            List<PreorderReportComplexItem> totalItemList = new ArrayList<PreorderReportComplexItem>(preorderReportTotalItems.values());
            Collections.sort(totalItemList);
            for (PreorderReportComplexItem item : totalItemList) {
                Collections.sort(item.getDishes());
            }

            preorderReportTotalItem.setPreorderReportClientItems(resultClientList);
            preorderReportTotalItem.setPreorderReportComplexItems(totalItemList);
            preorderReportTotalItem.calculateTotalItem();

            ArrayList<PreorderReportTotalItem> reportTotalItems = new ArrayList<PreorderReportTotalItem>();
            reportTotalItems.add(preorderReportTotalItem);

            return new JRBeanCollectionDataSource(reportTotalItems);
        }
    }

    public PreordersReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }


    @Override
    public BasicReportForOrgJob createInstance() {
        return new PreordersReport();
    }

    public PreordersReport() {
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
