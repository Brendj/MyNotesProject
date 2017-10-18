/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import com.sun.org.apache.xpath.internal.operations.Bool;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.BasicReport;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;

public class DirectorFinanceReport extends BasicReport {

    private final Map<Long, DirectorFinanceEntry> items;
    private Boolean allOO;

    public static class Builder {
        public DirectorFinanceReport build(Session session, Date startDate, Date endDate, List<Long> idsOfOrg, Boolean allOO) throws Exception {
            Date generateTime = new Date();

            String abonementPlan = "горячее питание по абонементу";
            String payPlan = "горячее питание по платному плану";
            String buffetPlan = "горячее питание, приобретенное в буфете";
            String buffetVendingPlan = "буфетная продукция (в т.ч. вендинг)";

            String sqlQuery =
                    "SELECT o.idoforg, o.shortnameinfoservice, o.shortaddress, "
                  + "   CASE WHEN e.ordertype=:abonementOrder THEN :abonementPlan "
                  + "       WHEN e.ordertype=:paidOrder THEN :payPlan "
                  + "       WHEN e.ordertype IN (:unknownPlan,:defaultPlan) AND (od.menutype BETWEEN :complexMin AND :compexMax) THEN :buffetPlan "
                  + "       WHEN e.ordertype IN (:unknownPlan,:defaultPlan,:vending) AND (od.menutype NOT BETWEEN :complexMin AND :compexMax) THEN :buffetVendingPlan "
                  + "   END AS TIP, "
                  + "   sum (CASE WHEN e.idofclientgroup <:employees AND od.rprice>0 THEN (od.rprice * od.qty) END) / 100 AS s2_FIN_OBUCH, "
                  + "   sum (CASE WHEN e.idofclientgroup IN (:employees,:administration,:tech_employees,:others,:employee,:parents,:visitors) AND od.rprice>0 THEN "
                  + "           (od.rprice * od.qty) END) / 100 AS s2_FIN_SOTR, "
                  + "   count (DISTINCT (CASE WHEN e.idofclientgroup <:employees  THEN e.idofclient END)) AS Kol_OBUCH, "
                  + "   count (DISTINCT (CASE WHEN e.idofclientgroup IN (:employees,:administration,:tech_employees,:others,:employee,:parents,:visitors) THEN "
                  + "           e.idofclient END)) AS Kol_SOTR, "
                  + "   count (DISTINCT (CASE WHEN e.idofclientgroup <:employees  THEN e.idoforder END)) AS Kol_ZAKAZ_OBUCH, "
                  + "   count (DISTINCT (CASE WHEN e.idofclientgroup IN (:employees,:administration,:tech_employees,:others,:employee,:parents,:visitors) THEN "
                  + "           e.idoforder END)) AS Kol_ZAKAZ_SOTR "
                  + "FROM cf_orgs o "
                  + "LEFT JOIN cf_orders e ON o.idoforg=e.idoforg AND e.idofclient IS NOT NULL AND e.socdiscount=0 AND e.state=:state_commited AND e.createddate>=:startDate "
                  + "   AND e.createddate<:endDate "
                  + "LEFT JOIN cf_orderdetails od ON o.idoforg=od.idoforg AND od.idoforder=e.idoforder "
                  + "WHERE o.idoforg IN (:idsOfOrg) "
                  + "GROUP BY o.shortnameinfoservice, TIP, o.shortaddress, o.idoforg, e.ordertype "
                  + "ORDER BY e.ordertype, tip";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("abonementOrder", OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());
            query.setParameter("abonementPlan", abonementPlan);
            query.setParameter("paidOrder", OrderTypeEnumType.PAY_PLAN.ordinal());
            query.setParameter("payPlan", payPlan);
            query.setParameter("unknownPlan", OrderTypeEnumType.UNKNOWN.ordinal());
            query.setParameter("defaultPlan", OrderTypeEnumType.DEFAULT.ordinal());
            query.setParameter("vending", OrderTypeEnumType.VENDING.ordinal());
            query.setParameter("buffetPlan", buffetPlan);
            query.setParameter("buffetVendingPlan", buffetVendingPlan);
            query.setParameter("complexMin", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("compexMax", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("tech_employees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("others", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            query.setParameter("employee", ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());
            query.setParameter("state_commited", Order.STATE_COMMITED);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idsOfOrg", idsOfOrg);

            List resultList = query.list();

            HashMap<Long, DirectorFinanceEntry> entryHashMap = new HashMap<Long, DirectorFinanceEntry>();

            for (Object o : resultList) {
                Object vals[] = (Object[])o;

                DirectorFinanceEntry entry;
                Long idOfOrg = ((BigInteger)vals[0]).longValue();
                String typeOfOrder = (String)vals[3];

                if (entryHashMap.containsKey(idOfOrg)) {
                    entry = entryHashMap.get(idOfOrg);
                } else {
                    entry = new DirectorFinanceEntry();
                    entry.setIdOfOrg(idOfOrg);
                    entry.setShortNameInfoService((String)vals[1]);
                    entry.setShortAddress((String)vals[2]);
                    entryHashMap.put(idOfOrg, entry);
                }

                if (buffetVendingPlan.equals(typeOfOrder)) {
                    if (null != vals[4]) {
                        entry.setStudentsFinanceVending(((BigDecimal) vals[4]).doubleValue());
                    } else {
                        entry.setStudentsFinanceVending(0D);
                    }
                    if (null != vals[5]) {
                        entry.setStaffFinanceVending(((BigDecimal) vals[5]).doubleValue());
                    } else {
                        entry.setStaffFinanceVending(0D);
                    }
                    entry.setStudentsCountVending(((BigInteger)vals[6]).longValue());
                    entry.setStaffCountVending(((BigInteger)vals[7]).longValue());
                    entry.setStudentsOrdersCountVending(((BigInteger)vals[8]).longValue());
                    entry.setStaffOrdersCountVending(((BigInteger)vals[9]).longValue());
                } else if (buffetPlan.equals(typeOfOrder)) {
                    if (null != vals[4]) {
                        entry.setStudentsFinanceBuffet(((BigDecimal) vals[4]).doubleValue());
                    } else {
                        entry.setStudentsFinanceBuffet(0D);
                    }
                    if (null != vals[5]) {
                        entry.setStaffFinanceBuffet(((BigDecimal) vals[5]).doubleValue());
                    } else {
                        entry.setStaffFinanceBuffet(0D);
                    }
                    entry.setStudentsCountBuffet(((BigInteger)vals[6]).longValue());
                    entry.setStaffCountBuffet(((BigInteger)vals[7]).longValue());
                    entry.setStudentsOrdersCountBuffet(((BigInteger)vals[8]).longValue());
                    entry.setStaffOrdersCountBuffet(((BigInteger)vals[9]).longValue());
                } else if (payPlan.equals(typeOfOrder)) {
                    if (null != vals[4]) {
                        entry.setStudentsFinancePayPlan(((BigDecimal) vals[4]).doubleValue());
                    } else {
                        entry.setStudentsFinancePayPlan(0D);
                    }
                    if (null != vals[5]) {
                        entry.setStaffFinancePayPlan(((BigDecimal) vals[5]).doubleValue());
                    } else {
                        entry.setStaffFinancePayPlan(0D);
                    }
                    entry.setStudentsCountPayPlan(((BigInteger)vals[6]).longValue());
                    entry.setStaffCountPayPlan(((BigInteger)vals[7]).longValue());
                    entry.setStudentsOrdersCountPayPlan(((BigInteger)vals[8]).longValue());
                    entry.setStaffOrdersCountPayPlan(((BigInteger)vals[9]).longValue());
                } else if (abonementPlan.equals(typeOfOrder)) {
                    if (null != vals[4]) {
                        entry.setStudentsFinanceAbonement(((BigDecimal) vals[4]).doubleValue());
                    } else {
                        entry.setStudentsFinanceAbonement(0D);
                    }
                    if (null != vals[5]) {
                        entry.setStaffFinanceAbonement(((BigDecimal) vals[5]).doubleValue());
                    } else {
                        entry.setStaffFinanceAbonement(0D);
                    }
                    entry.setStudentsCountAbonement(((BigInteger)vals[6]).longValue());
                    entry.setStaffCountAbonement(((BigInteger)vals[7]).longValue());
                    entry.setStudentsOrdersCountAbonement(((BigInteger)vals[8]).longValue());
                    entry.setStaffOrdersCountAbonement(((BigInteger)vals[9]).longValue());
                }
            }

            return new DirectorFinanceReport(generateTime, new Date().getTime() - generateTime.getTime(), entryHashMap, allOO);
        }
    }

    public DirectorFinanceReport() {
        super();
        this.items = Collections.emptyMap();
        this.allOO = false;
    }

    public DirectorFinanceReport(Date generateTime, long generateDuration,  Map<Long, DirectorFinanceEntry> entries, Boolean allOO) {
        super(generateTime, generateDuration);
        this.items = entries;
        this.allOO = allOO;
    }

    public List<String> chartData() {
        List<String> resultList = new ArrayList<String>();

        double studentsFinanceAbonement = 0D,
                studentsFinancePayPlan = 0D,
                studentsFinanceBuffet = 0D,
                studentsFinanceVending = 0D,
                staffFinanceAbonement = 0D,
                staffFinancePayPlan = 0D,
                staffFinanceBuffet = 0D,
                staffFinanceVending = 0D;
        Long studentsCountAbonement = 0L,
                studentsCountPayPlan = 0L,
                studentsCountBuffet = 0L,
                studentsCountVending = 0L,
                staffCountAbonement = 0L,
                staffCountPayPlan = 0L,
                staffCountBuffet = 0L,
                staffCountVending = 0L;

        String orgName = "";

        for (Long key : items.keySet()) {
            DirectorFinanceEntry entry = items.get(key);

            if (orgName.isEmpty())
                orgName = entry.getShortNameInfoService();

            // если строим отчет не для всего комплекса ОО
            if (!this.allOO) {
                resultList.add(fillStudentFinanceChartData(entry));
                resultList.add(fillStaffFinanceChartData(entry));
                resultList.add(fillStudentCountChartData(entry));
                resultList.add(fillStaffCountChartData(entry));
            } else {
                studentsFinanceAbonement += entry.getStudentsFinanceAbonement();
                studentsFinancePayPlan += entry.getStudentsFinancePayPlan();
                studentsFinanceBuffet += entry.getStudentsFinanceBuffet();
                studentsFinanceVending += entry.getStudentsFinanceVending();

                staffFinanceAbonement += entry.getStaffFinanceAbonement();
                staffFinancePayPlan += entry.getStaffFinancePayPlan();
                staffFinanceBuffet += entry.getStaffFinanceBuffet();
                staffFinanceVending += entry.getStaffFinanceVending();

                studentsCountAbonement += entry.getStudentsCountAbonement();
                studentsCountPayPlan += entry.getStudentsCountPayPlan();
                studentsCountBuffet += entry.getStudentsCountBuffet();
                studentsCountVending += entry.getStudentsCountVending();

                staffCountAbonement += entry.getStaffCountAbonement();
                staffCountPayPlan += entry.getStaffCountPayPlan();
                staffCountBuffet += entry.getStaffCountBuffet();
                staffCountVending += entry.getStaffCountVending();
            }
        }

        // если строим отчет для всего комплекса ОО
        if (this.allOO) {
            // students
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(studentsFinanceAbonement, "горячее питание по абонементу", "");
            dataset.addValue(studentsFinancePayPlan, "горячее питание по платному плану", "");
            dataset.addValue(studentsFinanceBuffet, "горячее питание, приобретенное в буфете", "");
            dataset.addValue(studentsFinanceVending, "буфетная продукция (в т.ч. вендинг)", "");

            dataset.addValue(studentsFinanceAbonement + studentsFinancePayPlan + studentsFinanceBuffet +
                    studentsFinanceVending, "итого (обучающиеся)", "");

            JFreeChart barChart = ChartFactory
                    .createBarChart(String.format("Финансовые показатели по обучающимся %s(весь комплекс)", orgName),
                            "", "", dataset, PlotOrientation.VERTICAL, true,true, false);

            barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
            barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

            BufferedImage image = barChart.createBufferedImage(800, 400);
            resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(image, "png")));

            // staffs
            DefaultCategoryDataset datasetStaff = new DefaultCategoryDataset();
            datasetStaff.addValue(staffFinanceAbonement, "горячее питание по абонементу", "");
            datasetStaff.addValue(staffFinancePayPlan, "горячее питание по платному плану", "");
            datasetStaff.addValue(staffFinanceBuffet, "горячее питание, приобретенное в буфете", "");
            datasetStaff.addValue(staffFinanceVending, "буфетная продукция (в т.ч. вендинг)", "");

            datasetStaff.addValue(staffFinanceAbonement + staffFinancePayPlan + staffFinanceBuffet +
                    staffFinanceVending, "итого (сотрудники)", "");

            JFreeChart barChartStaff = ChartFactory
                    .createBarChart(String.format("Финансовые показатели по сотрудникам %s(весь комплекс)", orgName),
                            "", "", datasetStaff, PlotOrientation.VERTICAL, true,true, false);

            barChartStaff.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
            barChartStaff.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

            BufferedImage imageStaff = barChartStaff.createBufferedImage(800, 400);
            resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(imageStaff, "png")));

            // students
            DefaultCategoryDataset datasetCount = new DefaultCategoryDataset();
            datasetCount.addValue(studentsCountAbonement, "горячее питание по абонементу", "");
            datasetCount.addValue(studentsCountPayPlan, "горячее питание по платному плану", "");
            datasetCount.addValue(studentsCountBuffet, "горячее питание, приобретенное в буфете", "");
            datasetCount.addValue(studentsCountVending, "буфетная продукция (в т.ч. вендинг)", "");

            datasetCount.addValue(studentsCountAbonement + studentsCountPayPlan + studentsCountBuffet +
                    studentsCountVending, "итого (обучающиеся)", "");

            JFreeChart barChartCount = ChartFactory
                    .createBarChart(String.format("Количественные показатели по обучающимся %s(весь комплекс)", orgName),
                            "", "", datasetCount, PlotOrientation.VERTICAL, true,true, false);

            barChartCount.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
            barChartCount.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

            BufferedImage imageCount = barChartCount.createBufferedImage(800, 400);
            resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(imageCount, "png")));

            // staffs
            DefaultCategoryDataset datasetCountStaff = new DefaultCategoryDataset();
            datasetCountStaff.addValue(staffCountAbonement, "горячее питание по абонементу", "");
            datasetCountStaff.addValue(staffCountPayPlan, "горячее питание по платному плану", "");
            datasetCountStaff.addValue(staffCountBuffet, "горячее питание, приобретенное в буфете", "");
            datasetCountStaff.addValue(staffCountVending, "буфетная продукция (в т.ч. вендинг)", "");

            datasetCountStaff.addValue(staffCountAbonement + staffCountPayPlan + staffCountBuffet +
                    staffCountVending, "итого (сотрудники)", "");

            JFreeChart barChartCountStaff = ChartFactory
                    .createBarChart(String.format("Количественные показатели по сотрудникам %s(весь комплекс)", orgName),
                            "", "", datasetCountStaff, PlotOrientation.VERTICAL, true,true, false);

            barChartCountStaff.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
            barChartCountStaff.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

            BufferedImage imageCountStaff = barChartCountStaff.createBufferedImage(800, 400);
            resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(imageCountStaff, "png")));
        }

        return resultList;
    }

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        String base64String = "";
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(img, formatName, output);
            base64String = DatatypeConverter.printBase64Binary(output.toByteArray());
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return base64String;
    }

    private String fillStudentFinanceChartData(DirectorFinanceEntry entry) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(entry.getStudentsFinanceAbonement(), "горячее питание по абонементу", "");
        dataset.addValue(entry.getStudentsFinancePayPlan(), "горячее питание по платному плану", "");
        dataset.addValue(entry.getStudentsFinanceBuffet(), "горячее питание, приобретенное в буфете", "");
        dataset.addValue(entry.getStudentsFinanceVending(), "буфетная продукция (в т.ч. вендинг)", "");

        dataset.addValue(entry.getStudentsFinanceAbonement() + entry.getStudentsFinancePayPlan() +
                entry.getStudentsFinanceBuffet() + entry.getStudentsFinanceVending(), "итого", "");

        JFreeChart barChart = ChartFactory
                .createBarChart(String.format("Финансовые показатели по обучающимся %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                    "", "", dataset, PlotOrientation.VERTICAL, true,true, false);

        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

        BufferedImage image = barChart.createBufferedImage(800, 400);
        return String.format("data:image/png;base64,%s", imgToBase64String(image, "png"));
    }

    private String fillStaffFinanceChartData(DirectorFinanceEntry entry) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(entry.getStaffFinanceAbonement(), "горячее питание по абонементу", "");
        dataset.addValue(entry.getStaffFinancePayPlan(), "горячее питание по платному плану", "");
        dataset.addValue(entry.getStaffFinanceBuffet(), "горячее питание, приобретенное в буфете", "");
        dataset.addValue(entry.getStaffFinanceVending(), "буфетная продукция (в т.ч. вендинг)", "");

        dataset.addValue(entry.getStaffFinanceAbonement() + entry.getStaffFinancePayPlan() +
                entry.getStaffFinanceBuffet() + entry.getStaffFinanceVending(), "итого", "");

        JFreeChart barChart = ChartFactory
                .createBarChart(String.format("Финансовые показатели по сотрудникам %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                        "", "", dataset, PlotOrientation.VERTICAL, true,true, false);

        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

        BufferedImage image = barChart.createBufferedImage(800, 400);
        return String.format("data:image/png;base64,%s", imgToBase64String(image, "png"));
    }

    private String fillStudentCountChartData(DirectorFinanceEntry entry) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(entry.getStudentsCountAbonement(), "горячее питание по абонементу", "");
        dataset.addValue(entry.getStudentsCountPayPlan(), "горячее питание по платному плану", "");
        dataset.addValue(entry.getStudentsCountBuffet(), "горячее питание, приобретенное в буфете", "");
        dataset.addValue(entry.getStudentsCountVending(), "буфетная продукция (в т.ч. вендинг)", "");

        dataset.addValue(entry.getStudentsCountAbonement() + entry.getStudentsCountPayPlan() +
                entry.getStudentsCountBuffet() + entry.getStudentsCountVending(), "итого", "");

        JFreeChart barChart = ChartFactory
                .createBarChart(String.format("Количественные показатели по обучающимся %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                        "", "", dataset, PlotOrientation.VERTICAL, true,true, false);

        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

        BufferedImage image = barChart.createBufferedImage(800, 400);
        return String.format("data:image/png;base64,%s", imgToBase64String(image, "png"));
    }

    private String fillStaffCountChartData(DirectorFinanceEntry entry) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(entry.getStaffCountAbonement(), "горячее питание по абонементу", "");
        dataset.addValue(entry.getStaffCountPayPlan(), "горячее питание по платному плану", "");
        dataset.addValue(entry.getStaffCountBuffet(), "горячее питание, приобретенное в буфете", "");
        dataset.addValue(entry.getStaffCountVending(), "буфетная продукция (в т.ч. вендинг)", "");

        dataset.addValue(entry.getStaffCountAbonement() + entry.getStaffCountPayPlan() +
                entry.getStaffCountBuffet() + entry.getStaffCountVending(), "итого", "");

        JFreeChart barChart = ChartFactory
                .createBarChart(String.format("Количественные показатели по сотрудникам %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                        "", "", dataset, PlotOrientation.VERTICAL, true,true, false);

        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
        barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

        BufferedImage image = barChart.createBufferedImage(800, 400);
        return String.format("data:image/png;base64,%s", imgToBase64String(image, "png"));
    }

    public static class DirectorFinanceEntry {
        private Long idOfOrg;
        private String shortNameInfoService;
        private String shortAddress;
        private Double studentsFinanceVending;
        private Double studentsFinanceBuffet;
        private Double studentsFinancePayPlan;
        private Double studentsFinanceAbonement;
        private Double staffFinanceVending;
        private Double staffFinanceBuffet;
        private Double staffFinancePayPlan;
        private Double staffFinanceAbonement;
        private Long studentsCountVending;
        private Long studentsCountBuffet;
        private Long studentsCountPayPlan;
        private Long studentsCountAbonement;
        private Long staffCountVending;
        private Long staffCountBuffet;
        private Long staffCountPayPlan;
        private Long staffCountAbonement;
        private Long studentsOrdersCountVending;
        private Long studentsOrdersCountBuffet;
        private Long studentsOrdersCountPayPlan;
        private Long studentsOrdersCountAbonement;
        private Long staffOrdersCountVending;
        private Long staffOrdersCountBuffet;
        private Long staffOrdersCountPayPlan;
        private Long staffOrdersCountAbonement;

        public DirectorFinanceEntry() {
            idOfOrg = -1L;
            studentsFinanceVending = 0D;
            studentsFinanceBuffet = 0D;
            studentsFinancePayPlan = 0D;
            studentsFinanceAbonement = 0D;
            staffFinanceVending = 0D;
            staffFinanceBuffet = 0D;
            staffFinancePayPlan = 0D;
            staffFinanceAbonement = 0D;
            studentsCountVending = 0L;
            studentsCountBuffet = 0L;
            studentsCountPayPlan = 0L;
            studentsCountAbonement = 0L;
            staffCountVending = 0L;
            staffCountBuffet = 0L;
            staffCountPayPlan = 0L;
            staffCountAbonement = 0L;
            studentsOrdersCountVending = 0L;
            studentsOrdersCountBuffet = 0L;
            studentsOrdersCountPayPlan = 0L;
            studentsOrdersCountAbonement = 0L;
            staffOrdersCountVending = 0L;
            staffOrdersCountBuffet = 0L;
            staffOrdersCountPayPlan = 0L;
            staffOrdersCountAbonement = 0L;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public String getShortAddress() {
            return shortAddress;
        }

        public void setShortAddress(String shortAddress) {
            this.shortAddress = shortAddress;
        }

        public Double getStudentsFinanceVending() {
            return studentsFinanceVending;
        }

        public void setStudentsFinanceVending(Double studentsFinanceVending) {
            this.studentsFinanceVending = studentsFinanceVending;
        }

        public Double getStudentsFinanceBuffet() {
            return studentsFinanceBuffet;
        }

        public void setStudentsFinanceBuffet(Double studentsFinanceBuffet) {
            this.studentsFinanceBuffet = studentsFinanceBuffet;
        }

        public Double getStudentsFinancePayPlan() {
            return studentsFinancePayPlan;
        }

        public void setStudentsFinancePayPlan(Double studentsFinancePayPlan) {
            this.studentsFinancePayPlan = studentsFinancePayPlan;
        }

        public Double getStudentsFinanceAbonement() {
            return studentsFinanceAbonement;
        }

        public void setStudentsFinanceAbonement(Double studentsFinanceAbonement) {
            this.studentsFinanceAbonement = studentsFinanceAbonement;
        }

        public Double getStaffFinanceVending() {
            return staffFinanceVending;
        }

        public void setStaffFinanceVending(Double staffFinanceVending) {
            this.staffFinanceVending = staffFinanceVending;
        }

        public Double getStaffFinanceBuffet() {
            return staffFinanceBuffet;
        }

        public void setStaffFinanceBuffet(Double staffFinanceBuffet) {
            this.staffFinanceBuffet = staffFinanceBuffet;
        }

        public Double getStaffFinancePayPlan() {
            return staffFinancePayPlan;
        }

        public void setStaffFinancePayPlan(Double staffFinancePayPlan) {
            this.staffFinancePayPlan = staffFinancePayPlan;
        }

        public Double getStaffFinanceAbonement() {
            return staffFinanceAbonement;
        }

        public void setStaffFinanceAbonement(Double staffFinanceAbonement) {
            this.staffFinanceAbonement = staffFinanceAbonement;
        }

        public Long getStudentsCountVending() {
            return studentsCountVending;
        }

        public void setStudentsCountVending(Long studentsCountVending) {
            this.studentsCountVending = studentsCountVending;
        }

        public Long getStudentsCountBuffet() {
            return studentsCountBuffet;
        }

        public void setStudentsCountBuffet(Long studentsCountBuffet) {
            this.studentsCountBuffet = studentsCountBuffet;
        }

        public Long getStudentsCountPayPlan() {
            return studentsCountPayPlan;
        }

        public void setStudentsCountPayPlan(Long studentsCountPayPlan) {
            this.studentsCountPayPlan = studentsCountPayPlan;
        }

        public Long getStudentsCountAbonement() {
            return studentsCountAbonement;
        }

        public void setStudentsCountAbonement(Long studentsCountAbonement) {
            this.studentsCountAbonement = studentsCountAbonement;
        }

        public Long getStaffCountVending() {
            return staffCountVending;
        }

        public void setStaffCountVending(Long staffCountVending) {
            this.staffCountVending = staffCountVending;
        }

        public Long getStaffCountBuffet() {
            return staffCountBuffet;
        }

        public void setStaffCountBuffet(Long staffCountBuffet) {
            this.staffCountBuffet = staffCountBuffet;
        }

        public Long getStaffCountPayPlan() {
            return staffCountPayPlan;
        }

        public void setStaffCountPayPlan(Long staffCountPayPlan) {
            this.staffCountPayPlan = staffCountPayPlan;
        }

        public Long getStaffCountAbonement() {
            return staffCountAbonement;
        }

        public void setStaffCountAbonement(Long staffCountAbonement) {
            this.staffCountAbonement = staffCountAbonement;
        }

        public Long getStudentsOrdersCountVending() {
            return studentsOrdersCountVending;
        }

        public void setStudentsOrdersCountVending(Long studentsOrdersCountVending) {
            this.studentsOrdersCountVending = studentsOrdersCountVending;
        }

        public Long getStudentsOrdersCountBuffet() {
            return studentsOrdersCountBuffet;
        }

        public void setStudentsOrdersCountBuffet(Long studentsOrdersCountBuffet) {
            this.studentsOrdersCountBuffet = studentsOrdersCountBuffet;
        }

        public Long getStudentsOrdersCountPayPlan() {
            return studentsOrdersCountPayPlan;
        }

        public void setStudentsOrdersCountPayPlan(Long studentsOrdersCountPayPlan) {
            this.studentsOrdersCountPayPlan = studentsOrdersCountPayPlan;
        }

        public Long getStudentsOrdersCountAbonement() {
            return studentsOrdersCountAbonement;
        }

        public void setStudentsOrdersCountAbonement(Long studentsOrdersCountAbonement) {
            this.studentsOrdersCountAbonement = studentsOrdersCountAbonement;
        }

        public Long getStaffOrdersCountVending() {
            return staffOrdersCountVending;
        }

        public void setStaffOrdersCountVending(Long staffOrdersCountVending) {
            this.staffOrdersCountVending = staffOrdersCountVending;
        }

        public Long getStaffOrdersCountBuffet() {
            return staffOrdersCountBuffet;
        }

        public void setStaffOrdersCountBuffet(Long staffOrdersCountBuffet) {
            this.staffOrdersCountBuffet = staffOrdersCountBuffet;
        }

        public Long getStaffOrdersCountPayPlan() {
            return staffOrdersCountPayPlan;
        }

        public void setStaffOrdersCountPayPlan(Long staffOrdersCountPayPlan) {
            this.staffOrdersCountPayPlan = staffOrdersCountPayPlan;
        }

        public Long getStaffOrdersCountAbonement() {
            return staffOrdersCountAbonement;
        }

        public void setStaffOrdersCountAbonement(Long staffOrdersCountAbonement) {
            this.staffOrdersCountAbonement = staffOrdersCountAbonement;
        }

    }
}
