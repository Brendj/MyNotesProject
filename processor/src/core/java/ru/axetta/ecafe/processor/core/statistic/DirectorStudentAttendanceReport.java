/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.report.BasicReport;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class DirectorStudentAttendanceReport extends BasicReport {

    private final List<DirectorStudentAttendanceEntry> items;

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
            for (Long idOfOrg : idsOfOrg) {
                 String sqlQuery =
                        "SELECT s.idoforg, s.organizationtype, s.shortnameinfoservice, s.shortname, s.shortaddress, "
                      + "       sum(come_in_young) AS come_in_young, sum(come_in_middle) AS come_in_middle, sum(come_in_elder) AS come_in_elder, "
                      + "       sum(come_in_dou_young) AS come_in_dou_young, sum(come_in_dou_elder) AS come_in_dou_elder, "
                      + "       sum(ex_come_in_young) AS ex_come_in_young, sum(ex_come_in_middle) AS ex_come_in_middle, "
                      + "       sum(ex_come_in_elder) AS ex_come_in_elder, sum(ex_come_in_dou_young) AS ex_come_in_dou_young, "
                      + "       sum(ex_come_in_dou_elder) AS ex_come_in_dou_elder "
                      + "FROM ( "
                      + "   SELECT DISTINCT o.idoforg, o.organizationtype, o.shortnameinfoservice, o.shortname, o.shortaddress, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:sosh AND "
                      + "                       (CAST ((SELECT SUBSTRING(g.groupname, '(\\d{1,2})-{0,1}[А-Яа-я{1}]')) AS INTEGER) BETWEEN 1 AND 4) "
                      + "                       THEN e.idofclient END)) AS come_in_young, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:sosh AND "
                      + "                       (CAST ((SELECT SUBSTRING(g.groupname, '(\\d{1,2})-{0,1}[А-Яа-я{1}]')) AS INTEGER) BETWEEN 5 AND 9) "
                      + "                       THEN e.idofclient END)) AS come_in_middle, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:sosh AND "
                      + "                       (CAST ((SELECT SUBSTRING(g.groupname, '(\\d{1,2})-{0,1}[А-Яа-я{1}]')) AS INTEGER) BETWEEN 10 AND 11) "
                      + "                       THEN e.idofclient END)) AS come_in_elder, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:dou AND (c.categoriesdiscounts LIKE :youngDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :youngDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE :youngDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :youngDOU2 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU2 || ',%') "
                      + "                       THEN e.idofclient END)) AS come_in_dou_young, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:dou AND (c.categoriesdiscounts LIKE :elderDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :elderDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE :elderDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :elderDOU2 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU2 || ',%') "
                      + "                       THEN e.idofclient END)) AS come_in_dou_elder, "
                      + "       0 AS ex_come_in_young, 0 AS ex_come_in_middle, 0 AS ex_come_in_elder, 0 AS ex_come_in_dou_young, 0 AS ex_come_in_dou_elder "
                      + "   FROM cf_orgs o "
                      + "   LEFT JOIN cf_enterevents e ON e.idoforg=o.idoforg AND e.evtdatetime>=:startDate AND e.evtdatetime<:endDate "
                      + "       AND e.passdirection=:direction AND e.idofenterevent IS NOT NULL "
                      + "   LEFT JOIN cf_clients c ON c.idofclient=e.idofclient AND c.idofclientgroup<:employees "
                      + "   LEFT JOIN cf_clientgroups g ON g.idofclientgroup=c.idofclientgroup AND c.idoforg=g.idoforg "
                      + "       AND g.groupname SIMILAR TO '\\d{1,2}-{0,1}[А-Яа-я{1}]' "
                      + "   INNER JOIN cf_orgs co ON co.idoforg=c.idoforg "
                      + "   WHERE o.idoforg=:idOfOrg "
                      + "   GROUP BY o.idoforg, o.organizationtype, o.shortnameinfoservice, o.idoforg, o.shortname, o.shortaddress "
                      + "   UNION ALL "
                      + "   SELECT o.idoforg, o.organizationtype, o.shortnameinfoservice, o.shortname, o.shortaddress, 0 AS come_in_young, "
                      + "       0 AS come_in_middle, 0 AS come_in_elder, 0 AS come_in_dou_young, 0 AS come_in_dou_elder, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:sosh AND "
                      + "                       (CAST ((SELECT SUBSTRING(g.groupname, '(\\d{1,2})-{0,1}[А-Яа-я{1}]')) AS INTEGER) BETWEEN 1 AND 4) "
                      + "                       THEN c.idofclient END)) AS ex_come_in_young, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:sosh AND "
                      + "                       (CAST ((SELECT SUBSTRING(g.groupname, '(\\d{1,2})-{0,1}[А-Яа-я{1}]')) AS INTEGER) BETWEEN 5 AND 9) "
                      + "                       THEN c.idofclient END)) AS ex_come_in_middle, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:sosh AND "
                      + "                       (CAST ((SELECT SUBSTRING(g.groupname, '(\\d{1,2})-{0,1}[А-Яа-я{1}]')) AS INTEGER) BETWEEN 10 AND 11) "
                      + "                       THEN c.idofclient END)) AS ex_come_in_elder,"
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:dou AND (c.categoriesdiscounts LIKE :youngDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :youngDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE :youngDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :youngDOU2 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :youngDOU2 || ',%') "
                      + "                       THEN c.idofclient END)) AS ex_come_in_dou_young, "
                      + "       count (DISTINCT (CASE WHEN co.organizationtype=:dou AND (c.categoriesdiscounts LIKE :elderDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :elderDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU1 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU1 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE :elderDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE :elderDOU2 || ',%' OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU2 OR "
                      + "                                                                    c.categoriesdiscounts LIKE '%,' || :elderDOU2 || ',%') "
                      + "                       THEN c.idofclient END)) AS ex_come_in_dou_elder "
                      + "   FROM cf_orgs o "
                      + "   LEFT JOIN cf_clients c ON c.idoforg=o.idoforg AND c.idofclientgroup<:employees "
                      + "   LEFT JOIN cf_enterevents ne ON ne.idofclient=c.idofclient AND ne.idoforg=c.idoforg "
                      + "       AND ne.evtdatetime>=:startDate AND ne.evtdatetime<:endDate AND ne.passdirection=:direction "
                      + "       AND ne.idofenterevent IS NULL "
                      + "   LEFT JOIN cf_clientgroups g ON g.idofclientgroup=c.idofclientgroup AND c.idoforg=g.idoforg "
                      + "       AND g.groupname SIMILAR TO '\\d{1,2}-{0,1}[А-Яа-я{1}]' "
                      + "   INNER JOIN cf_orgs co ON co.idoforg=c.idoforg "
                      + "   WHERE o.idoforg=:idOfOrg "
                      + "   GROUP BY o.idoforg, o.organizationtype, o.shortnameinfoservice, o.idoforg, o.shortname, o.shortaddress "
                      + ") s "
                      + "GROUP BY s.idoforg, s.organizationtype, s.shortnameinfoservice, s.shortname, s.shortaddress;";

                Query query = session.createSQLQuery(sqlQuery);
                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                query.setParameter("idOfOrg", idOfOrg);
                query.setParameter("direction", EnterEvent.ENTRY);
                query.setParameter("youngDOU1", youngDOU1.toString());
                query.setParameter("elderDOU1", elderDOU1.toString());
                query.setParameter("youngDOU2", youngDOU2.toString());
                query.setParameter("elderDOU2", elderDOU2.toString());
                query.setParameter("sosh", OrganizationType.SCHOOL.getCode());
                query.setParameter("dou", OrganizationType.KINDERGARTEN.getCode());

                List resultList = query.list();

                for (Object o : resultList) {
                    Object vals[] = (Object[]) o;

                    DirectorStudentAttendanceEntry entry = new DirectorStudentAttendanceEntry();
                    entry.setIdOfOrg(((BigInteger) vals[0]).longValue());
                    entry.setOrganizationType((Integer) vals[1]);
                    entry.setShortNameInfoService((String) vals[2]);
                    entry.setShortName((String) vals[3]);
                    entry.setShortAddress((String) vals[4]);
                    entry.setComeInYoungSOSHValue(((BigDecimal) vals[5]).longValue());
                    entry.setComeInMiddleSOSHValue(((BigDecimal) vals[6]).longValue());
                    entry.setComeInElderSOSHValue(((BigDecimal) vals[7]).longValue());
                    entry.setComeInYoungDOUValue(((BigDecimal) vals[8]).longValue());
                    entry.setComeInElderDOUValue(((BigDecimal) vals[9]).longValue());
                    entry.setExComeInYoungSOSHValue(((BigDecimal) vals[10]).longValue());
                    entry.setExComeInMiddleSOSHValue(((BigDecimal) vals[11]).longValue());
                    entry.setExComeInElderSOSHValue(((BigDecimal) vals[12]).longValue());
                    entry.setExComeInYoungDOUValue(((BigDecimal) vals[13]).longValue());
                    entry.setExComeInElderDOUValue(((BigDecimal) vals[14]).longValue());

                    entries.add(entry);
                }
            }

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
                        String.format("Посещаемость обучающихся %s\n(%s)", entry.getShortNameInfoService(), entry.getShortAddress()),
                        "", "", dataset, PlotOrientation.VERTICAL, true, true, false);
                configureBarChart(barChart, dataset);
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
                    String.format("Посещаемость обучающихся %s\n(весь комплекс)", items.get(0).getShortNameInfoService()),
                    "", "", dataset, PlotOrientation.VERTICAL, true, true, false);
            configureBarChart(barChart, dataset);
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

    public List<DirectorStudentAttendanceEntry> getItems() {
        return items;
    }

    public Boolean getAllOO() {
        return allOO;
    }

    public List<DirectorStudentAttendanceEntry> getAllOOItem() {
        DirectorStudentAttendanceEntry entry = new DirectorStudentAttendanceEntry();
        for (DirectorStudentAttendanceEntry e : items) {
            if (null == entry.getShortNameInfoService()) {
                entry.setShortNameInfoService(e.getShortNameInfoService());
            }

            if (e.getOrganizationType().equals(OrganizationType.SCHOOL.getCode())) {
                entry.setComeInYoungSOSHValue(entry.getComeInYoungSOSHValue() + e.getComeInYoungSOSHValue());
                entry.setComeInMiddleSOSHValue(entry.getComeInMiddleSOSHValue() + e.getComeInMiddleSOSHValue());
                entry.setComeInElderSOSHValue(entry.getComeInElderSOSHValue() + e.getComeInElderSOSHValue());

                entry.setExComeInYoungSOSHValue(entry.getExComeInYoungSOSHValue() + e.getExComeInYoungSOSHValue());
                entry.setExComeInMiddleSOSHValue(entry.getExComeInMiddleSOSHValue() + e.getExComeInMiddleSOSHValue());
                entry.setExComeInElderSOSHValue(entry.getExComeInElderSOSHValue() + e.getExComeInElderSOSHValue());
            }

            if (e.getOrganizationType().equals(OrganizationType.KINDERGARTEN.getCode())) {
                entry.setComeInYoungDOUValue(entry.getComeInYoungDOUValue() + e.getComeInYoungDOUValue());
                entry.setComeInElderDOUValue(entry.getComeInElderDOUValue() + e.getComeInElderDOUValue());

                entry.setExComeInYoungDOUValue(entry.getExComeInYoungDOUValue() + e.getExComeInYoungDOUValue());
                entry.setExComeInElderDOUValue(entry.getExComeInElderDOUValue() + e.getExComeInElderDOUValue());
            }
        }

        List<DirectorStudentAttendanceEntry> resultList = new ArrayList<DirectorStudentAttendanceEntry>();
        resultList.add(entry);
        return resultList;
    }

    private Range calcRange(CategoryDataset dataset) {
        List rowKeys = dataset.getRowKeys();
        List columnKeys = dataset.getColumnKeys();

        Double minValue = 0.D;
        Double maxValue = 8.D;

        for (Object rowKey : rowKeys) {
            for (Object colKey : columnKeys) {
                Double value = dataset.getValue((Comparable)rowKey, (Comparable)colKey).doubleValue();

                if (minValue > value)
                    minValue = value;

                if (maxValue < value)
                    maxValue = value;
            }
        }
        return new Range(minValue, maxValue + maxValue / 8.D);
    }

    private void configureBarChart(JFreeChart barChart, DefaultCategoryDataset dataset) {
        barChart.getCategoryPlot().getRangeAxis().setRange(calcRange(dataset));
        Color color = new Color(243,243,242);
        barChart.getCategoryPlot().setBackgroundPaint(color);
        barChart.setBackgroundPaint(color);
        barChart.getLegend().setBackgroundPaint(color);
        barChart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().getRenderer().setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
        barChart.getCategoryPlot().getRenderer().setBaseItemLabelsVisible(true);
        barChart.getCategoryPlot().setNoDataMessage("Нет данных");
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
            if (organizationType < 0 || OrganizationType.SCHOOL.getCode().equals(organizationType))
                return comeInYoungSOSHValue;
            return 0L;
        }

        public void setComeInYoungSOSHValue(Long comeInYoungSOSHValue) {
            this.comeInYoungSOSHValue = comeInYoungSOSHValue;
        }

        public Long getComeInMiddleSOSHValue() {
            if (organizationType < 0 || OrganizationType.SCHOOL.getCode().equals(organizationType))
                return comeInMiddleSOSHValue;
            return 0L;
        }

        public void setComeInMiddleSOSHValue(Long comeInMiddleSOSHValue) {
            this.comeInMiddleSOSHValue = comeInMiddleSOSHValue;
        }

        public Long getComeInElderSOSHValue() {
            if (organizationType < 0 || OrganizationType.SCHOOL.getCode().equals(organizationType))
                return comeInElderSOSHValue;
            return 0L;
        }

        public void setComeInElderSOSHValue(Long comeIntElderSOSHValue) {
            this.comeInElderSOSHValue = comeIntElderSOSHValue;
        }

        public Long getComeInYoungDOUValue() {
            if (organizationType < 0 || OrganizationType.KINDERGARTEN.getCode().equals(organizationType))
                return comeInYoungDOUValue;
            return 0L;
        }

        public void setComeInYoungDOUValue(Long comeInYoungDOUValue) {
            this.comeInYoungDOUValue = comeInYoungDOUValue;
        }

        public Long getComeInElderDOUValue() {
            if (organizationType < 0 || OrganizationType.KINDERGARTEN.getCode().equals(organizationType))
                return comeInElderDOUValue;
            return 0L;
        }

        public void setComeInElderDOUValue(Long comeInElderDOUValue) {
            this.comeInElderDOUValue = comeInElderDOUValue;
        }

        public Long getExComeInYoungSOSHValue() {
            if (organizationType < 0 || OrganizationType.SCHOOL.getCode().equals(organizationType))
                return exComeInYoungSOSHValue;
            return 0L;
        }

        public void setExComeInYoungSOSHValue(Long exComeInYoungSOSHValue) {
            this.exComeInYoungSOSHValue = exComeInYoungSOSHValue;
        }

        public Long getExComeInMiddleSOSHValue() {
            if (organizationType < 0 || OrganizationType.SCHOOL.getCode().equals(organizationType))
                return exComeInMiddleSOSHValue;
            return 0L;
        }

        public void setExComeInMiddleSOSHValue(Long exComeInMiddleSOSHValue) {
            this.exComeInMiddleSOSHValue = exComeInMiddleSOSHValue;
        }

        public Long getExComeInElderSOSHValue() {
            if (organizationType < 0 || OrganizationType.SCHOOL.getCode().equals(organizationType))
                return exComeInElderSOSHValue;
            return 0L;
        }

        public void setExComeInElderSOSHValue(Long exComeInElderSOSHValue) {
            this.exComeInElderSOSHValue = exComeInElderSOSHValue;
        }

        public Long getExComeInYoungDOUValue() {
            if (organizationType < 0 || OrganizationType.KINDERGARTEN.getCode().equals(organizationType))
                return exComeInYoungDOUValue;
            return 0L;
        }

        public void setExComeInYoungDOUValue(Long exComeInYoungDOUValue) {
            this.exComeInYoungDOUValue = exComeInYoungDOUValue;
        }

        public Long getExComeInElderDOUValue() {
            if (organizationType < 0 || OrganizationType.KINDERGARTEN.getCode().equals(organizationType))
                return exComeInElderDOUValue;
            return 0L;
        }

        public void setExComeInElderDOUValue(Long exComeInElderDOUValue) {
            this.exComeInElderDOUValue = exComeInElderDOUValue;
        }

        public Boolean isSOSH() {
            return OrganizationType.SCHOOL.getCode().equals(this.organizationType);
        }

        public Boolean isDOU() {
            return OrganizationType.KINDERGARTEN.getCode().equals(this.organizationType);
        }
    }
}
