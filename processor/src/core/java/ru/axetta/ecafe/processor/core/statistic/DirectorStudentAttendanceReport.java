/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
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
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;

public class DirectorStudentAttendanceReport extends BasicReport {

    private final List<DirectorStudentAttendanceEntry> items;

    private static final Long youngSOSH = -90L; // начальные СОШ
    private static final Long middleSOSH = -91L; // средние СОШ
    private static final Long elderSOSH = -92L; // старшие СОШ

    private static final Long youngDOU1 = 105L; // 1.5-3 ДОУ
    private static final Long youngDOU2 = 123L; // 1.5-3 ДОУ
    private static final Long elderDOU1 = 106L; // 3-7 ДОУ
    private static final Long elderDOU2 = 124L; // 3-7 ДОУ

    private Boolean allOO;

    public static class Builder {

        public DirectorStudentAttendanceReport build(Session session, Date startDate, Date endDate, List<Long> idsOfOrg, Boolean allOO)
                throws Exception {
            Date generateTime = new Date();
            List<DirectorStudentAttendanceEntry> entries = new ArrayList<DirectorStudentAttendanceEntry>();

            String sqlQuery =
                    "SELECT o.idoforg, o.organizationtype, o.shortnameinfoservice, o.shortname, o.shortaddress, c.categoriesdiscounts, "
                            + "count (DISTINCT (CASE WHEN e.idofenterevent IS NOT NULL  THEN e.idofclient END)) AS PRISHLO, "
                            + "count (DISTINCT (CASE WHEN e.idofenterevent IS  NULL  THEN c.idofclient END)) AS NE_PRISHLO "
                            + "FROM cf_orgs o "
                            + "LEFT JOIN cf_clients c ON c.idoforg=o.idoforg "
                            + "LEFT JOIN cf_enterevents e ON e.idofclient=c.idofclient AND e.evtdatetime>=:startDate AND e.evtdatetime<:endDate "
                            + "WHERE (c.idofclientgroup<:employees OR c.idofclientgroup > :deleted) "
                            + "AND o.idoforg IN (:idsOfOrg) AND c.categoriesdiscounts <> '' "
                            + "GROUP BY o.organizationtype, o.shortnameinfoservice, o.idoforg, o.shortname, o.shortaddress, c.categoriesdiscounts";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameterList("idsOfOrg", idsOfOrg);

            List resultList = query.list();

            HashMap<Long, DirectorStudentAttendanceEntry> entryHashMap = new HashMap<Long, DirectorStudentAttendanceEntry>();

            for (Object o : resultList) {
                Object vals[] = (Object[]) o;

                DirectorStudentAttendanceEntry entry;
                String categoryDiscounts[] = ((String) vals[5]).split(",");
                Integer orgType = (Integer) vals[1];
                Long idOfOrg = ((BigInteger) vals[0]).longValue();

                for (String discount : categoryDiscounts) {
                    Long discountL = Long.parseLong(discount);

                    if (entryHashMap.containsKey(idOfOrg)) {
                        entry = entryHashMap.get(idOfOrg);

                        if (entry.getOrganizationType().equals(orgType)) {
                            if (youngSOSH.equals(discountL)) {                                          // начальные СОШ
                                entry.setComeInYoungSOSHValue(entry.getComeInYoungSOSHValue() + ((BigInteger) vals[6]).longValue());
                                entry.setExComeInYoungSOSHValue(entry.getExComeInYoungSOSHValue() + ((BigInteger) vals[7]).longValue());
                            } else if (middleSOSH.equals(discountL)) {                                  // средние СОШ
                                entry.setComeInMiddleSOSHValue(entry.getComeInMiddleSOSHValue() + ((BigInteger) vals[6]).longValue());
                                entry.setExComeInMiddleSOSHValue(entry.getExComeInMiddleSOSHValue() + ((BigInteger) vals[7]).longValue());
                            } else if (elderSOSH.equals(discountL)) {                                   // старшие СОШ
                                entry.setComeInElderSOSHValue(entry.getComeInElderSOSHValue() + ((BigInteger) vals[6]).longValue());
                                entry.setExComeInElderSOSHValue(entry.getExComeInElderSOSHValue() + ((BigInteger) vals[7]).longValue());
                            } else if (youngDOU1.equals(discountL) || youngDOU2.equals(discountL)) {    // 1.5-3 ДОУ
                                entry.setComeInYoungDOUValue(entry.getExComeInYoungDOUValue() + ((BigInteger) vals[6]).longValue());
                                entry.setExComeInYoungDOUValue(entry.getExComeInYoungDOUValue() + ((BigInteger) vals[7]).longValue());
                            } else if (elderDOU1.equals(discountL) || elderDOU2.equals(discountL)) {    // 3-7 ДОУ
                                entry.setComeInElderDOUValue(entry.getComeInElderDOUValue() + ((BigInteger) vals[6]).longValue());
                                entry.setExComeInElderDOUValue(entry.getExComeInElderDOUValue() + ((BigInteger) vals[7]).longValue());
                            }
                        }
                    } else {
                        entry = new DirectorStudentAttendanceEntry();
                        entry.setIdOfOrg(((BigInteger) vals[0]).longValue());
                        entry.setOrganizationType(orgType);
                        entry.setShortNameInfoService((String) vals[2]);
                        entry.setShortName((String) vals[3]);
                        entry.setShortAddress((String) vals[4]);

                        if (youngSOSH.equals(discountL)) {                                      // начальные СОШ
                            entry.setComeInYoungSOSHValue(((BigInteger) vals[6]).longValue());
                            entry.setExComeInYoungSOSHValue(((BigInteger) vals[7]).longValue());
                            entryHashMap.put(entry.getIdOfOrg(), entry);
                        } else if (middleSOSH.equals(discountL)) {                               // средние СОШ
                            entry.setComeInMiddleSOSHValue(((BigInteger) vals[6]).longValue());
                            entry.setExComeInMiddleSOSHValue(((BigInteger) vals[7]).longValue());
                            entryHashMap.put(entry.getIdOfOrg(), entry);
                        } else if (elderSOSH.equals(discountL)) {                               // старшие СОШ
                            entry.setComeInElderSOSHValue(((BigInteger) vals[6]).longValue());
                            entry.setExComeInElderSOSHValue(((BigInteger) vals[7]).longValue());
                            entryHashMap.put(entry.getIdOfOrg(), entry);
                        } else if (youngDOU1.equals(discountL) || youngDOU2.equals(discountL)) {    // 1.5-3 ДОУ
                            entry.setComeInYoungDOUValue(((BigInteger) vals[6]).longValue());
                            entry.setExComeInElderDOUValue(((BigInteger) vals[7]).longValue());
                            entryHashMap.put(entry.getIdOfOrg(), entry);
                        } else if (elderDOU1.equals(discountL) || elderDOU2.equals(discountL)) {    // 3-7 ДОУ
                            entry.setComeInElderDOUValue(((BigInteger) vals[6]).longValue());
                            entry.setExComeInElderDOUValue(((BigInteger) vals[7]).longValue());
                            entryHashMap.put(entry.getIdOfOrg(), entry);
                        }
                    }
                }
            }

            for (Long key : entryHashMap.keySet())
                entries.add(entryHashMap.get(key));

            return new DirectorStudentAttendanceReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    entries, allOO);
        }
    }

    public DirectorStudentAttendanceReport() {
        super();
        this.items = Collections.emptyList();
        this.allOO = false;
    }

    public DirectorStudentAttendanceReport(Date generateTime, long generateDuration, List<DirectorStudentAttendanceEntry> entries,
            Boolean allOO) {
        super(generateTime, generateDuration);
        this.items = entries;
        this.allOO = allOO;
    }

    public List<String> chartData() {
        List<String> resultList = new ArrayList<String>();

        if (items.isEmpty())
            return resultList;

        Long comeInYoungSOSH = 0L, exComeInYoungSOSH = 0L, comeInMiddleSOSH = 0L, exComeInMiddleSOSH = 0L, comeInElderSOSH = 0L, exComeInElderSOSH = 0L, comeInYoungDOU = 0L, exComeInYoungDOU = 0L, comeInElderDOU = 0L, exComeInElderDOU = 0L;

        for (DirectorStudentAttendanceEntry entry : items) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // СОШ
            if (OrganizationType.SCHOOL.getCode().longValue() == entry.getOrganizationType()) {
                // если строим отчет не для всего комплекса ОО
                if (!this.allOO) {
                    dataset.addValue(entry.getComeInYoungSOSHValue().doubleValue(), "присутствуют", "начальные (СОШ)");
                    dataset.addValue(entry.getComeInMiddleSOSHValue().doubleValue(), "присутствуют", "средние (СОШ)");
                    dataset.addValue(entry.getComeInElderSOSHValue().doubleValue(), "присутствуют", "старшие (СОШ)");

                    dataset.addValue(entry.getExComeInYoungSOSHValue().doubleValue(), "отсутствуют", "начальные (СОШ)");
                    dataset.addValue(entry.getExComeInMiddleSOSHValue().doubleValue(), "отсутствуют", "средние (СОШ)");
                    dataset.addValue(entry.getExComeInElderSOSHValue().doubleValue(), "отсутствуют", "старшие (СОШ)");
                }

                comeInYoungSOSH += entry.getComeInYoungSOSHValue();
                comeInMiddleSOSH += entry.getComeInMiddleSOSHValue();
                comeInElderSOSH += entry.getComeInElderSOSHValue();

                exComeInYoungSOSH += entry.getExComeInYoungSOSHValue();
                exComeInMiddleSOSH += entry.getExComeInMiddleSOSHValue();
                exComeInElderSOSH += entry.getExComeInElderSOSHValue();
            }

            // ДОУ
            if (OrganizationType.KINDERGARTEN.getCode().longValue() == entry.getOrganizationType()) {
                // если строим отчет не для всего комплекса ОО
                if (!this.allOO) {
                    dataset.addValue(entry.getComeInYoungDOUValue().doubleValue(), "присутствуют", "1.5-3 (ДОУ)");
                    dataset.addValue(entry.getComeInElderDOUValue().doubleValue(), "присутствуют", "3-7 (ДОУ)");

                    dataset.addValue(entry.getExComeInYoungDOUValue().doubleValue(), "отсутствуют", "1.5-3 (ДОУ)");
                    dataset.addValue(entry.getExComeInElderDOUValue().doubleValue(), "отсутствуют", "3-7 (ДОУ)");
                }

                comeInYoungDOU += entry.getComeInYoungDOUValue();
                comeInElderDOU += entry.getComeInElderDOUValue();

                exComeInYoungDOU += entry.getExComeInYoungDOUValue();
                exComeInElderDOU += entry.getExComeInElderDOUValue();
            }

            // если строим отчет не для всего комплекса ОО
            if (!this.allOO) {
                JFreeChart barChart = ChartFactory.createBarChart(
                        String.format("Посещаемость обучающихся %s(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                        "", "", dataset, PlotOrientation.VERTICAL, true, true, false);

                barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                        new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
                barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

                BufferedImage image = barChart.createBufferedImage(800, 400);
                resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(image, "png")));
            }
        }
        // если строим отчет для всего комплекса ОО
        if (this.allOO) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            dataset.addValue(comeInYoungSOSH.doubleValue(), "присутствуют", "начальные (СОШ)");
            dataset.addValue(comeInMiddleSOSH.doubleValue(), "присутствуют", "средние (СОШ)");
            dataset.addValue(comeInElderSOSH.doubleValue(), "присутствуют", "старшие (СОШ)");

            dataset.addValue(exComeInYoungSOSH.doubleValue(), "отсутствуют", "начальные (СОШ)");
            dataset.addValue(exComeInMiddleSOSH.doubleValue(), "отсутствуют", "средние (СОШ)");
            dataset.addValue(exComeInElderSOSH.doubleValue(), "отсутствуют", "старшие (СОШ)");

            dataset.addValue(comeInYoungDOU.doubleValue(), "присутствуют", "1.5-3 (ДОУ)");
            dataset.addValue(comeInElderDOU.doubleValue(), "присутствуют", "3-7 (ДОУ)");

            dataset.addValue(exComeInYoungDOU.doubleValue(), "отсутствуют", "1.5-3 (ДОУ)");
            dataset.addValue(exComeInElderDOU.doubleValue(), "отсутствуют", "3-7 (ДОУ)");

            JFreeChart barChart = ChartFactory.createBarChart(
                    String.format("Посещаемость обучающихся %s(весь комплекс)", items.get(0).getShortNameInfoService()),
                    "", "", dataset, PlotOrientation.VERTICAL, true, true, false);

            barChart.getCategoryPlot().getRenderer().setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
            barChart.getCategoryPlot().getRenderer().setDefaultItemLabelsVisible(true);

            BufferedImage image = barChart.createBufferedImage(800, 400);
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

    public static class DirectorStudentAttendanceEntry {
        private Long idOfOrg;
        private Integer organizationType;
        private String shortNameInfoService;
        private String shortName;
        private String shortAddress;
        private Long comeInYoungSOSHValue;
        private Long comeInMiddleSOSHValue;
        private Long comeInElderSOSHValue;
        private Long comeInYoungDOUValue;
        private Long comeInElderDOUValue;
        private Long exComeInYoungSOSHValue;
        private Long exComeInMiddleSOSHValue;
        private Long exComeInElderSOSHValue;
        private Long exComeInYoungDOUValue;
        private Long exComeInElderDOUValue;

        public DirectorStudentAttendanceEntry() {
            idOfOrg = 0L;
            organizationType = -1;
            comeInYoungSOSHValue = 0L;
            comeInMiddleSOSHValue = 0L;
            comeInElderSOSHValue = 0L;
            comeInYoungDOUValue = 0L;
            comeInElderDOUValue = 0L;
            exComeInYoungSOSHValue = 0L;
            exComeInMiddleSOSHValue = 0L;
            exComeInElderSOSHValue = 0L;
            exComeInYoungDOUValue = 0L;
            exComeInElderDOUValue = 0L;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Integer getOrganizationType() {
            return organizationType;
        }

        public void setOrganizationType(Integer organizationType) {
            this.organizationType = organizationType;
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getShortAddress() {
            return shortAddress;
        }

        public void setShortAddress(String shortAddress) {
            this.shortAddress = shortAddress;
        }

        public Long getComeInYoungSOSHValue() {
            return comeInYoungSOSHValue;
        }

        public void setComeInYoungSOSHValue(Long comeInYoungSOSHValue) {
            this.comeInYoungSOSHValue = comeInYoungSOSHValue;
        }

        public Long getComeInMiddleSOSHValue() {
            return comeInMiddleSOSHValue;
        }

        public void setComeInMiddleSOSHValue(Long comeInMiddleSOSHValue) {
            this.comeInMiddleSOSHValue = comeInMiddleSOSHValue;
        }

        public Long getComeInElderSOSHValue() {
            return comeInElderSOSHValue;
        }

        public void setComeInElderSOSHValue(Long comeIntElderSOSHValue) {
            this.comeInElderSOSHValue = comeIntElderSOSHValue;
        }

        public Long getComeInYoungDOUValue() {
            return comeInYoungDOUValue;
        }

        public void setComeInYoungDOUValue(Long comeInYoungDOUValue) {
            this.comeInYoungDOUValue = comeInYoungDOUValue;
        }

        public Long getComeInElderDOUValue() {
            return comeInElderDOUValue;
        }

        public void setComeInElderDOUValue(Long comeInElderDOUValue) {
            this.comeInElderDOUValue = comeInElderDOUValue;
        }

        public Long getExComeInYoungSOSHValue() {
            return exComeInYoungSOSHValue;
        }

        public void setExComeInYoungSOSHValue(Long exComeInYoungSOSHValue) {
            this.exComeInYoungSOSHValue = exComeInYoungSOSHValue;
        }

        public Long getExComeInMiddleSOSHValue() {
            return exComeInMiddleSOSHValue;
        }

        public void setExComeInMiddleSOSHValue(Long exComeInMiddleSOSHValue) {
            this.exComeInMiddleSOSHValue = exComeInMiddleSOSHValue;
        }

        public Long getExComeInElderSOSHValue() {
            return exComeInElderSOSHValue;
        }

        public void setExComeInElderSOSHValue(Long exComeInElderSOSHValue) {
            this.exComeInElderSOSHValue = exComeInElderSOSHValue;
        }

        public Long getExComeInYoungDOUValue() {
            return exComeInYoungDOUValue;
        }

        public void setExComeInYoungDOUValue(Long exComeInYoungDOUValue) {
            this.exComeInYoungDOUValue = exComeInYoungDOUValue;
        }

        public Long getExComeInElderDOUValue() {
            return exComeInElderDOUValue;
        }

        public void setExComeInElderDOUValue(Long exComeInElderDOUValue) {
            this.exComeInElderDOUValue = exComeInElderDOUValue;
        }
    }
}
