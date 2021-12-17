/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import ru.axetta.ecafe.processor.core.persistence.*;
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
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class DirectorDiscountFoodReport extends BasicReport {

    private final List<DirectorDiscountFoodEntry> items;

    private static final Long youngSOSH     = -90L; // начальные СОШ
    private static final Long middleSOSH    = -91L; // средние СОШ
    private static final Long elderSOSH     = -92L; // старшие СОШ

    private static final Long youngDOU1     = 105L; // 1.5-3 ДОУ
    private static final Long youngDOU2     = 123L; // 1.5-3 ДОУ
    private static final Long elderDOU1     = 106L; // 3-7 ДОУ
    private static final Long elderDOU2     = 124L; // 3-7 ДОУ

    private Boolean allOO;

    public static class Builder {
        public DirectorDiscountFoodReport build(Session session, Date startDate, Date endDate, List<Long> idsOfOrg, Boolean allOO) throws Exception {
            Date generateTime = new Date();
            List<DirectorDiscountFoodEntry> entries = new ArrayList<DirectorDiscountFoodEntry>();

            for (Long idOfOrg : idsOfOrg) {
                String sqlQuery =
                        "SELECT o.idoforg, o.organizationtype, o.shortnameinfoservice, o.shortname, o.shortaddress "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reduced,:correction,:recycling) AND (dr.categoriesdiscounts LIKE :youngSOSH OR "
                      + "                                                         dr.categoriesdiscounts LIKE :youngSOSH || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :youngSOSH OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :youngSOSH || ',%') THEN e.idofclient END)) AS privilege_young "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reduced,:correction,:recycling) AND (dr.categoriesdiscounts LIKE :middleSOSH OR "
                      + "                                                         dr.categoriesdiscounts LIKE :middleSOSH || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :middleSOSH OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :middleSOSH || ',%') THEN e.idofclient END)) AS privilege_middle "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reduced,:correction,:recycling) AND (dr.categoriesdiscounts LIKE :elderSOSH OR "
                      + "                                                         dr.categoriesdiscounts LIKE :elderSOSH || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :elderSOSH OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :elderSOSH || ',%') THEN e.idofclient END)) AS privilege_elder "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reduced,:correction,:recycling) AND (dr.categoriesdiscounts LIKE :youngDOU1 OR "
                      + "                                                         dr.categoriesdiscounts LIKE :youngDOU1 || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :youngDOU1 OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :youngDOU1 || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE :youngDOU2 OR "
                      + "                                                         dr.categoriesdiscounts LIKE :youngDOU2 || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :youngDOU2 OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :youngDOU2 || ',%') THEN e.idofclient END)) AS privilege_dou_young "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reduced,:correction,:recycling) AND (dr.categoriesdiscounts LIKE :elderDOU1 OR "
                      + "                                                         dr.categoriesdiscounts LIKE :elderDOU1 || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :elderDOU1 OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :elderDOU1 || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE :elderDOU2 OR "
                      + "                                                         dr.categoriesdiscounts LIKE :elderDOU2 || ',%' OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :elderDOU2 OR "
                      + "                                                         dr.categoriesdiscounts LIKE '%,' || :elderDOU2 || ',%') THEN e.idofclient END)) AS privilege_dou_elder "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reserve,:change) AND (dr.categoriesdiscounts LIKE :youngSOSH OR "
                      + "                                                       dr.categoriesdiscounts LIKE :youngSOSH || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :youngSOSH OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :youngSOSH || ',%') THEN e.idofclient END)) AS reserve_young "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reserve,:change) AND (dr.categoriesdiscounts LIKE :middleSOSH OR "
                      + "                                                       dr.categoriesdiscounts LIKE :middleSOSH || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :middleSOSH OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :middleSOSH || ',%') THEN e.idofclient END)) AS reserve_middle "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reserve,:change) AND (dr.categoriesdiscounts LIKE :elderSOSH OR "
                      + "                                                       dr.categoriesdiscounts LIKE :elderSOSH || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :elderSOSH OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :elderSOSH || ',%') THEN e.idofclient END)) AS reserve_elder "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reserve,:change) AND (dr.categoriesdiscounts LIKE :youngDOU1 OR "
                      + "                                                       dr.categoriesdiscounts LIKE :youngDOU1 || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :youngDOU1 OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :youngDOU1 || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE :youngDOU2 OR "
                      + "                                                       dr.categoriesdiscounts LIKE :youngDOU2 || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :youngDOU2 OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :youngDOU2 || ',%') THEN e.idofclient END)) AS reserve_dou_young "
                      + "   ,count (DISTINCT (CASE WHEN e.ordertype IN (:reserve,:change) AND (dr.categoriesdiscounts LIKE :elderDOU1 OR "
                      + "                                                       dr.categoriesdiscounts LIKE :elderDOU1 || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :elderDOU1 OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :elderDOU1 || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE :elderDOU2 OR "
                      + "                                                       dr.categoriesdiscounts LIKE :elderDOU2 || ',%' OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :elderDOU2 OR "
                      + "                                                       dr.categoriesdiscounts LIKE '%,' || :elderDOU2 || ',%') THEN e.idofclient END)) AS reserve_dou_elder "
                      + "FROM cf_orgs o "
                      + "LEFT JOIN cf_orders e ON o.idoforg=e.idoforg AND e.idofclient IS NOT NULL AND (e.idofclientgroup<:employees OR e.idofclientgroup>:deleted) "
                      + "                       AND e.ordertype IN (:reduced,:reserve,:correction,:change,:recycling) "
                      + "AND e.createddate>=:startDate AND e.createddate<:endDate "
                      + "LEFT JOIN cf_orderdetails od ON e.idoforder=od.idoforder AND e.idoforg=od.idoforg AND e.state=:state_commited AND od.state=:state_commited_details "
                      + "                       AND od.idofrule IS NOT NULL "
                      + "LEFT JOIN cf_discountrules dr ON dr.idofrule=od.idofrule "
                      + "LEFT JOIN cf_clients c ON e.idofclient=c.idofclient "
                      + "WHERE o.idoforg=:idOfOrg "
                      + "GROUP BY o.idoforg, o.shortnameinfoservice, o.shortaddress, o.shortname, o.organizationtype";

                Query query = session.createSQLQuery(sqlQuery);
                query.setParameter("reduced", OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
                query.setParameter("reserve", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
                query.setParameter("correction", OrderTypeEnumType.CORRECTION_TYPE.ordinal());
                query.setParameter("recycling", OrderTypeEnumType.RECYCLING_RETIONS.ordinal());
                query.setParameter("change", OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal());
                query.setParameter("state_commited", Order.STATE_COMMITED);
                query.setParameter("state_commited_details", OrderDetail.STATE_COMMITED);
                query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                query.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameter("idOfOrg", idOfOrg);
                query.setParameter("youngSOSH", youngSOSH.toString());
                query.setParameter("middleSOSH", middleSOSH.toString());
                query.setParameter("elderSOSH", elderSOSH.toString());
                query.setParameter("youngDOU1", youngDOU1.toString());
                query.setParameter("elderDOU1", elderDOU1.toString());
                query.setParameter("youngDOU2", youngDOU2.toString());
                query.setParameter("elderDOU2", elderDOU2.toString());

                List resultList = query.list();

                for (Object o : resultList) {
                    Object vals[] = (Object[]) o;

                    DirectorDiscountFoodEntry entry = new DirectorDiscountFoodEntry();
                    entry.setIdOfOrg(((BigInteger) vals[0]).longValue());
                    entry.setOrganizationType(((Integer) vals[1]).longValue());
                    entry.setShortNameInfoService((String) vals[2]);
                    entry.setShortName((String) vals[3]);
                    entry.setAddress((String) vals[4]);
                    entry.setPrivilegeYoungValue(((BigInteger) vals[5]).longValue());
                    entry.setPrivilegeMiddleValue(((BigInteger) vals[6]).longValue());
                    entry.setPrivilegeElderValue(((BigInteger) vals[7]).longValue());
                    entry.setPrivilegeDOUYoungValue(((BigInteger) vals[8]).longValue());
                    entry.setPrivilegeDOUElderValue(((BigInteger) vals[9]).longValue());
                    entry.setReserveYoungValue(((BigInteger) vals[10]).longValue());
                    entry.setReserveMiddleValue(((BigInteger) vals[11]).longValue());
                    entry.setReserveElderValue(((BigInteger) vals[12]).longValue());
                    entry.setReserveDOUYoungValue(((BigInteger) vals[13]).longValue());
                    entry.setReserveDOUElderValue(((BigInteger) vals[14]).longValue());

                    entries.add(entry);
                }
            }

            return new DirectorDiscountFoodReport(generateTime, new Date().getTime() - generateTime.getTime(), entries, allOO);
        }
    }

    public DirectorDiscountFoodReport() {
        super();
        this.items = Collections.emptyList();
        this.allOO = false;
    }

    public DirectorDiscountFoodReport(Date generateTime, long generateDuration, List<DirectorDiscountFoodEntry> entries, Boolean allOO) {
        super(generateTime, generateDuration);
        this.items = entries;
        this.allOO = allOO;
    }

    public List<String> chartData() {
        List<String> resultList = new ArrayList<String>();

        if (items.isEmpty())
            return resultList;

        Long privilegeYoung = 0L,
                privilegeMiddle = 0L,
                privilegeElder = 0L,
                reserveYoung = 0L,
                reserveMiddle = 0L,
                reserveElder = 0L,
                privilegeDOUYoung = 0L,
                privilegeDOUElder = 0L,
                reserveDOUYoung = 0L,
                reserveDOUElder = 0L;

        for (DirectorDiscountFoodEntry entry : items) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // СОШ
            if (OrganizationType.SCHOOL.getCode().longValue() == entry.getOrganizationType()) {
                // если строим отчет не для всего комплекса ОО
                if (!this.allOO) {
                    dataset.addValue(entry.getPrivilegeYoungValue().doubleValue(), "льготная категория",
                            "начальные (СОШ)");
                    dataset.addValue(entry.getPrivilegeMiddleValue().doubleValue(), "льготная категория",
                            "средние (СОШ)");
                    dataset.addValue(entry.getPrivilegeElderValue().doubleValue(), "льготная категория", "старшие (СОШ)");

                    dataset.addValue(entry.getReserveYoungValue().doubleValue(), "резервная категория",
                            "начальные (СОШ)");
                    dataset.addValue(entry.getReserveMiddleValue().doubleValue(), "резервная категория", "средние (СОШ)");
                    dataset.addValue(entry.getReserveElderValue().doubleValue(), "резервная категория", "старшие (СОШ)");
                }

                privilegeYoung += entry.getPrivilegeYoungValue();
                privilegeMiddle += entry.getPrivilegeMiddleValue();
                privilegeElder += entry.getPrivilegeElderValue();

                reserveYoung += entry.getReserveYoungValue();
                reserveMiddle += entry.getReserveMiddleValue();
                reserveElder += entry.getReserveElderValue();
            }

            // ДОУ
            if (OrganizationType.KINDERGARTEN.getCode().longValue() == entry.getOrganizationType()) {
                // если строим отчет не для всего комплекса ОО
                if (!this.allOO) {
                    dataset.addValue(entry.getPrivilegeDOUYoungValue().doubleValue(), "льготная категория",
                            "1.5-3 (ДОУ)");
                    dataset.addValue(entry.getPrivilegeDOUElderValue().doubleValue(), "льготная категория", "3-7 (ДОУ)");

                    dataset.addValue(entry.getReserveDOUYoungValue().doubleValue(), "резервная категория", "1.5-3 (ДОУ)");
                    dataset.addValue(entry.getReserveDOUElderValue().doubleValue(), "резервная категория", "3-7 (ДОУ)");
                }

                privilegeDOUYoung += entry.getPrivilegeDOUYoungValue();
                privilegeDOUElder += entry.getPrivilegeDOUElderValue();

                reserveDOUYoung += entry.getReserveDOUYoungValue();
                reserveDOUElder += entry.getReserveDOUElderValue();
            }

            // если строим отчет не для всего комплекса ОО
            if (!this.allOO) {
                JFreeChart barChart = ChartFactory.createBarChart(
                        String.format("Предоставление льготного питания %s\n(%s)", entry.getShortNameInfoService(), entry.getAddress()),
                        "", "", dataset, PlotOrientation.VERTICAL, true, true, false);
                configureBarChart(barChart, dataset);
                BufferedImage image = barChart.createBufferedImage(800, 400);
                resultList.add(String.format("data:image/png;base64,%s", imgToBase64String(image, "png")));
            }
        }
        // если строим отчет для всего комплекса ОО
            if (this.allOO) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            dataset.addValue(privilegeYoung.doubleValue(), "льготная категория", "начальные (СОШ)");
            dataset.addValue(privilegeMiddle.doubleValue(), "льготная категория", "средние (СОШ)");
            dataset.addValue(privilegeElder.doubleValue(), "льготная категория", "старшие (СОШ)");

            dataset.addValue(reserveYoung.doubleValue(), "резервная категория", "начальные (СОШ)");
            dataset.addValue(reserveMiddle.doubleValue(), "резервная категория", "средние (СОШ)");
            dataset.addValue(reserveElder.doubleValue(), "резервная категория", "старшие (СОШ)");

            dataset.addValue(privilegeDOUYoung.doubleValue(), "льготная категория", "1.5-3 (ДОУ)");
            dataset.addValue(privilegeDOUElder.doubleValue(), "льготная категория", "3-7 (ДОУ)");

            dataset.addValue(reserveDOUYoung.doubleValue(), "резервная категория", "1.5-3 (ДОУ)");
            dataset.addValue(reserveDOUElder.doubleValue(), "резервная категория", "3-7 (ДОУ)");

            JFreeChart barChart = ChartFactory.createBarChart(
                    String.format("Предоставление льготного питания %s\n(весь комплекс)", items.get(0).getShortNameInfoService()),
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

    public List<DirectorDiscountFoodEntry> getItems() {
        return items;
    }

    public Boolean getAllOO() {
        return allOO;
    }

    public List<DirectorDiscountFoodEntry> getAllOOItem() {
        DirectorDiscountFoodEntry entry = new DirectorDiscountFoodEntry();
        for (DirectorDiscountFoodEntry e : items) {
            if (null == entry.getShortNameInfoService()) {
                entry.setShortNameInfoService(e.getShortNameInfoService());
            }

            entry.setPrivilegeYoungValue(entry.getPrivilegeYoungValue() + e.getPrivilegeYoungValue());
            entry.setPrivilegeMiddleValue(entry.getPrivilegeMiddleValue() + e.getPrivilegeMiddleValue());
            entry.setPrivilegeElderValue(entry.getPrivilegeElderValue() + e.getPrivilegeElderValue());
            entry.setPrivilegeDOUYoungValue(entry.getPrivilegeDOUYoungValue() + e.getPrivilegeDOUYoungValue());
            entry.setPrivilegeDOUElderValue(entry.getPrivilegeDOUElderValue() + e.getPrivilegeDOUElderValue());

            entry.setReserveYoungValue(entry.getReserveYoungValue() + e.getReserveYoungValue());
            entry.setReserveMiddleValue(entry.getReserveMiddleValue() + e.getReserveMiddleValue());
            entry.setReserveElderValue(entry.getReserveElderValue() + e.getReserveElderValue());
            entry.setReserveDOUYoungValue(entry.getReserveDOUYoungValue() + e.getReserveDOUYoungValue());
            entry.setReserveDOUElderValue(entry.getReserveDOUElderValue() + e.getReserveDOUElderValue());
        }

        List<DirectorDiscountFoodEntry> resultList = new ArrayList<DirectorDiscountFoodEntry>();
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

    public static class DirectorDiscountFoodEntry {
        private Long idOfOrg;
        private Long organizationType;
        private String shortNameInfoService;
        private String shortName;
        private String address;
        private Long privilegeElderValue;       // СОШ старшие льготники
        private Long privilegeMiddleValue;      // СОШ средние льготники
        private Long privilegeYoungValue;       // СОШ младшие льготники
        private Long reserveElderValue;         // СОШ старшие резервники
        private Long reserveMiddleValue;        // СОШ средние резервники
        private Long reserveYoungValue;         // СОШ средние резервники
        private Long privilegeDOUYoungValue;    // ДОУ 1.5-3 льготники
        private Long privilegeDOUElderValue;    // ДОУ 3-7 льготники
        private Long reserveDOUYoungValue;      // ДОУ 1.5-3 резервники
        private Long reserveDOUElderValue;      // ДОУ 3-7 резервники

        public DirectorDiscountFoodEntry() {
            idOfOrg                 = -1L;
            organizationType        = -1L;
            privilegeElderValue     = 0L;
            privilegeMiddleValue    = 0L;
            privilegeYoungValue     = 0L;
            reserveElderValue       = 0L;
            reserveMiddleValue      = 0L;
            reserveYoungValue       = 0L;
            privilegeDOUYoungValue  = 0L;
            privilegeDOUElderValue  = 0L;
            reserveDOUElderValue    = 0L;
            reserveDOUYoungValue    = 0L;
        }

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

        public String getAddress() {
            return address;
        }

        public Long getPrivilegeElderValue() {
            return privilegeElderValue;
        }

        public Long getPrivilegeMiddleValue() {
            return privilegeMiddleValue;
        }

        public Long getPrivilegeYoungValue() {
            return privilegeYoungValue;
        }

        public Long getReserveElderValue() {
            return reserveElderValue;
        }

        public Long getReserveMiddleValue() {
            return reserveMiddleValue;
        }

        public Long getReserveYoungValue() {
            return reserveYoungValue;
        }

        public Long getPrivilegeDOUYoungValue() {
            return privilegeDOUYoungValue;
        }

        public Long getPrivilegeDOUElderValue() {
            return privilegeDOUElderValue;
        }

        public Long getReserveDOUElderValue() {
            return reserveDOUElderValue;
        }

        public Long getReserveDOUYoungValue() {
            return reserveDOUYoungValue;
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

        public void setAddress(String address) {
            this.address = address;
        }

        public void setPrivilegeElderValue(Long privilegeElderValue) {
            this.privilegeElderValue = privilegeElderValue;
        }

        public void setPrivilegeMiddleValue(Long privilegeMiddleValue) {
            this.privilegeMiddleValue = privilegeMiddleValue;
        }

        public void setPrivilegeYoungValue(Long privilegeYoungValue) {
            this.privilegeYoungValue = privilegeYoungValue;
        }

        public void setReserveYoungValue(Long reserveYoungValue) {
            this.reserveYoungValue = reserveYoungValue;
        }

        public void setReserveMiddleValue(Long reserveMiddleValue) {
            this.reserveMiddleValue = reserveMiddleValue;
        }

        public void setReserveElderValue(Long reserveElderValue) {
            this.reserveElderValue = reserveElderValue;
        }

        public void setPrivilegeDOUYoungValue(Long privilegeDOUYoungValue) {
            this.privilegeDOUYoungValue = privilegeDOUYoungValue;
        }

        public void setPrivilegeDOUElderValue(Long privilegeDOUElderValue) {
            this.privilegeDOUElderValue = privilegeDOUElderValue;
        }

        public void setReserveDOUElderValue(Long reserveDOUElderValue) {
            this.reserveDOUElderValue = reserveDOUElderValue;
        }

        public void setReserveDOUYoungValue(Long reserveDOUYoungValue) {
            this.reserveDOUYoungValue = reserveDOUYoungValue;
        }

        public Boolean isSOSH() {
            return ((Long)OrganizationType.SCHOOL.getCode().longValue()).equals(this.organizationType);
        }

        public Boolean isDOU() {
            return ((Long)OrganizationType.KINDERGARTEN.getCode().longValue()).equals(this.organizationType);
        }
    }
}
