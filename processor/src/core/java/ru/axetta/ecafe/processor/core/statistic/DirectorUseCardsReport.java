/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
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
import java.util.*;

public class DirectorUseCardsReport extends BasicReport {

    private final List<DirectorUseCardsEntry> items;
    private Boolean allOO;

    public static class Builder {

        public DirectorUseCardsReport build(Session session, Date startDate, Date endDate, List<Long> idsOfOrg, Boolean allOO) throws Exception {
            Date generateTime = new Date();
            List<DirectorUseCardsEntry> entries = new ArrayList<DirectorUseCardsEntry>();

            String sqlQuery =
                        "SELECT o.idoforg, o.shortnameinfoservice, o.shortname, o.shortaddress, o.organizationtype "
                      + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND e.idofcard IS NOT NULL THEN e.idofclient END)) AS s2_VSE "
                      + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND ca.cardtype IN (0,1,2,9,10) AND e.guardianid IS NULL THEN e.idofclient END)) AS s2_VOSHLI_SS_CARD "
                      + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND ca.cardtype IN (3,7,8) AND e.guardianid IS NULL THEN e.idofclient END)) AS s3_VOSHLI_SOC_CARD "
                      + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND ca.cardtype IN (5,6,4) AND e.guardianid IS NULL THEN e.idofclient END)) AS s4_VOSHLI_ATHER_CARD "
                      + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND e.idofcard IS NULL AND e.guardianid IS NULL THEN e.idofclient END)) AS s5_VOSHLI_BES_CARD_SOSH "
                      + " ,count (DISTINCT (CASE WHEN e.idofclient IS NOT NULL AND e.guardianid IS NOT NULL THEN e.idofclient END)) AS s6_VOSHLI_BES_CARD_DOU "
                      + " FROM cf_orgs o"
                      + " LEFT JOIN cf_enterevents e ON e.idoforg=o.idoforg AND (e.idofclientgroup < :employees OR e.idofclientgroup > :deleted) "
                      + "               AND e.evtdatetime BETWEEN :startDate AND :endDate AND e.passdirection NOT IN (:checked_ext, :checked_int) "
                      + " LEFT JOIN cf_cards ca ON ca.cardno=e.idofcard "
                      + " WHERE o.idoforg IN (:idsOfOrg) "
                      + " GROUP BY o.shortnameinfoservice, o.shortname, o.idoforg, o.shortaddress, o.organizationtype";

            Query query = session.createSQLQuery(sqlQuery);

            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idsOfOrg", idsOfOrg);
            query.setParameter("checked_ext", EnterEvent.CHECKED_BY_TEACHER_EXT);
            query.setParameter("checked_int", EnterEvent.CHECKED_BY_TEACHER_INT);

            List resultList = query.list();

            for (Object o : resultList) {
                DirectorUseCardsEntry entry = new DirectorUseCardsEntry();
                Object vals[]=(Object[])o;

                entry.setIdOfOrg(((BigInteger)vals[0]).longValue());
                entry.setShortNameInfoService((String)vals[1]);
                entry.setShortName((String)vals[2]);
                entry.setShortAddress((String)vals[3]);
                entry.setOrganizationType(((Integer)vals[4]).longValue());
                entry.setServiceValue(((BigInteger)vals[6]).longValue());
                entry.setSocialValue(((BigInteger)vals[7]).longValue());
                entry.setTransportValue(((BigInteger)vals[8]).longValue());
                entry.setWithoutSOSHValue(((BigInteger)vals[9]).longValue());
                entry.setWithoutDOUValue(((BigInteger)vals[10]).longValue());

                entries.add(entry);
            }

            return new DirectorUseCardsReport(generateTime, new Date().getTime() - generateTime.getTime(), entries, allOO);
        }
    }

    public DirectorUseCardsReport() {
        super();
        this.items = Collections.emptyList();
        this.allOO = false;
    }

    public DirectorUseCardsReport(Date generateTime, long generateDuration, List<DirectorUseCardsEntry> entries, Boolean allOO) {
        super(generateTime, generateDuration);
        this.items = entries;
        this.allOO = allOO;
    }

    public List<String> chartData () {
        List<String> resultList = new ArrayList<String>();

        if (items.isEmpty())
            return resultList;

        Long serviceCards = 0L,
                socialCards = 0L,
                transportCards = 0L,
                withoutSOSHCards = 0L,
                withoutDOUCards = 0L;

        for (DirectorUseCardsEntry entry : items) {

            serviceCards += entry.getServiceValue();
            socialCards += entry.getSocialValue();
            transportCards += entry.getTransportValue();
            withoutSOSHCards += entry.getWithoutSOSHValue();
            withoutDOUCards += entry.getWithoutDOUValue();

            // если строим отчет не для всего комплекса ОО
            if (!this.allOO) {
                DefaultPieDataset dataset = new DefaultPieDataset();
                dataset.setValue("сервисная карта, браслет, брелок", entry.getServiceValue());
                dataset.setValue("социальная карта", entry.getSocialValue());
                dataset.setValue("прочие карты", entry.getTransportValue());
                dataset.setValue("без электронных носителей (СОШ)", entry.getWithoutSOSHValue());
                dataset.setValue("без электронных носителей (ДОУ)", entry.getWithoutDOUValue());

                JFreeChart pieChart = ChartFactory.createPieChart(
                        String.format("Использование электронных носителей при посещении здания ОО %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
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
            dataset.setValue("сервисная карта, браслет, брелок", serviceCards);
            dataset.setValue("социальная карта", socialCards);
            dataset.setValue("прочие карты", transportCards);
            dataset.setValue("без электронных носителей (СОШ)", withoutSOSHCards);
            dataset.setValue("без электронных носителей (ДОУ)", withoutDOUCards);

            JFreeChart pieChart = ChartFactory.createPieChart(
                    String.format("Использование электронных носителей при посещении здания ОО %s(весь комплекс)",
                            items.get(0).getShortNameInfoService()), dataset, true, true, false);

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

    public static class DirectorUseCardsEntry {
        private String shortNameInfoService;
        private String shortName;
        private String shortAddress;
        private Long idOfOrg;
        private Long organizationType;
        private Long serviceValue;    // Сервисные карты, часы, браслеты
        private Long socialValue;     // Социальные карты
        private Long transportValue;  // Транспортная, Банковская, УЭК
        private Long withoutSOSHValue; // БЕЗ карты СОШ
        private Long withoutDOUValue; // БЕЗ карты ДОУ

        public String getShortName() {
            return shortName;
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public String getShortAddress() {
            return shortAddress;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getOrganizationType() {
            return organizationType;
        }

        public Long getServiceValue() {
            return serviceValue;
        }

        public Long getSocialValue() {
            return socialValue;
        }

        public Long getTransportValue() {
            return transportValue;
        }

        public Long getWithoutDOUValue() {
            return withoutDOUValue;
        }

        public Long getWithoutSOSHValue() {
            return withoutSOSHValue;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public void setOrganizationType(Long organizationType) {
            this.organizationType = organizationType;
        }

        public void setServiceValue(Long serviceValue) {
            this.serviceValue = serviceValue;
        }

        public void setSocialValue(Long socialValue) {
            this.socialValue = socialValue;
        }

        public void setTransportValue(Long transportValue) {
            this.transportValue = transportValue;
        }

        public void setWithoutDOUValue(Long withoutDOUValue) {
            this.withoutDOUValue = withoutDOUValue;
        }

        public void setWithoutSOSHValue(Long withoutSOSHValue) {
            this.withoutSOSHValue = withoutSOSHValue;
        }

        public void setShortAddress(String shortAddress) {
            this.shortAddress = shortAddress;
        }
    }
}
