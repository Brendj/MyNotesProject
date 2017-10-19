/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.report.BasicReport;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DirectorStaffAttendanceReport extends BasicReport {

    private final List<DirectorStaffAttendanceEntry> items;
    private Boolean allOO;

    public static class Builder {
        public DirectorStaffAttendanceReport build(Session session, Date startDate, Date endDate, List<Long> idsOfOrg, Boolean allOO) throws Exception {
            Date generateTime = new Date();
            List<DirectorStaffAttendanceEntry> entries = new ArrayList<DirectorStaffAttendanceEntry>();

            String sqlQuery =
                    "SELECT o.idoforg, o.organizationtype, o.shortnameinfoservice, o.shortname, o.shortaddress "
                  + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND e.idofclientgroup IN (:employees,:administration) THEN e.idofclient END)) AS s2_VOSHLI_SOTR "
                  + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND e.idofclientgroup IN (:tech_employees,:others,:employee) THEN e.idofclient END)) AS s3_VOSHLI_TEHPERS "
                  + " ,count (DISTINCT (CASE WHEN c.idofclientgroup IN (:employees,:administration) AND e.idofenterevent IS NULL THEN c.idofclient END)) AS s4_NE_VOSHLI_SOTR "
                  + " ,count (DISTINCT (CASE WHEN c.idofclientgroup IN (:tech_employees,:others,:employee) AND e.idofenterevent IS NULL THEN c.idofclient END)) AS s5_NE_VOSHLI_TEHPERS "
                  + " ,count (DISTINCT (CASE WHEN e.idofclientgroup IN (:visitors)  THEN e.idofclient END)) AS s6_VOSHLI_POSETITELI "
                  + " ,count (DISTINCT (CASE WHEN e.idofclientgroup IN (:parents) THEN e.idofclient END)) AS s7_VOSHLI_RODITELI "
                  + "FROM cf_clients c "
                  + "LEFT JOIN cf_orgs o ON c.idoforg=o.idoforg "
                  + "LEFT JOIN cf_enterevents e ON e.idofclient=c.idofclient AND e.evtdatetime>=:startDate AND e.evtdatetime<:endDate "
                  + "WHERE (c.idofclientgroup >=:employees OR e.idofclientgroup <= :deleted) AND o.idoforg IN (:idsOfOrg) "
                  + "GROUP BY o.shortnameinfoservice, o.idoforg, o.organizationtype, o.shortname, o.shortaddress";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("tech_employees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("others", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            query.setParameter("employee", ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idsOfOrg", idsOfOrg);

            List resultList = query.list();

            for (Object o : resultList) {
                DirectorStaffAttendanceEntry entry = new DirectorStaffAttendanceEntry();
                Object vals[]=(Object[])o;

                entry.setIdOfOrg(((BigInteger)vals[0]).longValue());
                entry.setOrganizationType(((Integer)vals[1]).longValue());
                entry.setShortNameInfoService((String)vals[2]);
                entry.setShortName((String)vals[3]);
                entry.setShortAddress((String)vals[4]);
                entry.setAdministrationValue(((BigInteger)vals[5]).longValue());
                entry.setTechpersonalValue(((BigInteger)vals[6]).longValue());
                entry.setExAdministrationValue(((BigInteger)vals[7]).longValue());
                entry.setExTechpersonalValue(((BigInteger)vals[8]).longValue());
                entry.setVisitorValue(((BigInteger)vals[9]).longValue());
                entry.setParentValue(((BigInteger)vals[10]).longValue());

                entries.add(entry);
            }

            return new DirectorStaffAttendanceReport(generateTime, new Date().getTime() - generateTime.getTime(), entries, allOO);
        }
    }

    public DirectorStaffAttendanceReport() {
        super();
        this.items = Collections.emptyList();
        this.allOO = false;
    }

    public DirectorStaffAttendanceReport(Date generateTime, long generateDuration, List<DirectorStaffAttendanceEntry> entries, Boolean allOO) {
        super(generateTime, generateDuration);
        this.items = entries;
        this.allOO = allOO;
    }

    public List<String> chartData() {
        List<String> resultList = new ArrayList<String>();

        if (items.isEmpty())
            return resultList;

        Long admin = 0L,
                tech = 0L,
                exAdmins = 0L,
                exTech = 0L,
                visitor = 0L,
                parent = 0L;

        for (DirectorStaffAttendanceEntry entry : items) {
            admin += entry.getAdministrationValue();
            tech += entry.getTechpersonalValue();
            exAdmins += entry.getExAdministrationValue();
            exTech += entry.getExTechpersonalValue();
            visitor += entry.getVisitorValue();
            parent += entry.getParentValue();

            // если строим отчет не для всего комплекса ОО
            if (!this.allOO) {
                DefaultPieDataset dataset = new DefaultPieDataset();
                dataset.setValue("педогогический состав и администрация (вошли)", entry.getAdministrationValue());
                dataset.setValue("тех. персонал, сотрудники и др. (вошли)", entry.getTechpersonalValue());
                dataset.setValue("педогогический состав и администрация (не вошли)", entry.getExAdministrationValue());
                dataset.setValue("тех. персонал, сотрудники и др. (не вошли)", entry.getExTechpersonalValue());
                dataset.setValue("посетители", entry.getVisitorValue());
                dataset.setValue("родители", entry.getParentValue());


                JFreeChart pieChart = ChartFactory.createPieChart(
                        String.format("Посещаемость сотрудников %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                        dataset, true, true, false);
                ((PiePlot) pieChart.getPlot()).setLabelGenerator(
                        new StandardPieSectionLabelGenerator("{1}({2})", new DecimalFormat("0"), new DecimalFormat("0.00%")));

                BufferedImage image = pieChart.createBufferedImage(800, 400);
                resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(image, "png")));
            }
        }

        // если строим отчет для всего комплекса ОО
        if (this.allOO) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("педогогический состав и администрация (вошли)", admin);
            dataset.setValue("тех. персонал, сотрудники и др. (вошли)", tech);
            dataset.setValue("педогогический состав и администрация (не вошли)", exAdmins);
            dataset.setValue("тех. персонал, сотрудники и др. (не вошли)", exTech);
            dataset.setValue("посетители", visitor);
            dataset.setValue("родители", parent);

            JFreeChart pieChart = ChartFactory.createPieChart(
                    String.format("Посещаемость сотрудников %s(весь комплекс)", items.get(0).getShortNameInfoService()),
                    dataset, true, true, false);
            ((PiePlot) pieChart.getPlot()).setLabelGenerator(
                    new StandardPieSectionLabelGenerator("{1}({2})", new DecimalFormat("0"), new DecimalFormat("0.00%")));

            BufferedImage image = pieChart.createBufferedImage(800, 400);
            resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(image, "png")));
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

    public static class DirectorStaffAttendanceEntry {
        private Long idOfOrg;
        private Long organizationType;
        private String shortNameInfoService;
        private String shortName;
        private String shortAddress;
        private Long administrationValue;
        private Long techpersonalValue;
        private Long exAdministrationValue;
        private Long exTechpersonalValue;
        private Long visitorValue;
        private Long parentValue;

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getOrganizationType() {
            return organizationType;
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public String getShortName() {
            return shortName;
        }

        public String getShortAddress() {
            return shortAddress;
        }

        public Long getAdministrationValue() {
            return administrationValue;
        }

        public Long getTechpersonalValue() {
            return techpersonalValue;
        }

        public Long getExAdministrationValue() {
            return exAdministrationValue;
        }

        public Long getExTechpersonalValue() {
            return exTechpersonalValue;
        }

        public Long getVisitorValue() {
            return visitorValue;
        }

        public Long getParentValue() {
            return parentValue;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public void setOrganizationType(Long organizationType) {
            this.organizationType = organizationType;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public void setShortAddress(String shortAddress) {
            this.shortAddress = shortAddress;
        }

        public void setAdministrationValue(Long administrationValue) {
            this.administrationValue = administrationValue;
        }

        public void setTechpersonalValue(Long techpersonalValue) {
            this.techpersonalValue = techpersonalValue;
        }

        public void setExAdministrationValue(Long exAdministrationValue) {
            this.exAdministrationValue = exAdministrationValue;
        }

        public void setExTechpersonalValue(Long exTechpersonalValue) {
            this.exTechpersonalValue = exTechpersonalValue;
        }

        public void setVisitorValue(Long visitorValue) {
            this.visitorValue = visitorValue;
        }

        public void setParentValue(Long parentValue) {
            this.parentValue = parentValue;
        }
    }
}
