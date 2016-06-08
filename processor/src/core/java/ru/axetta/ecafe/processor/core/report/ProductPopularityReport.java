/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

public class ProductPopularityReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет по частоте покупки товаров, содержащих определенные продукты";
    public static final String[] TEMPLATE_FILE_NAMES = {"ProductPopularityReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class ProductPopularityReportItem implements Comparable {

            // Имя продукта
            private String nameofproduct;
            // Количество покупок товаров, содержащих данный продукт
            private Long productcount;

            public ProductPopularityReportItem(String nameofproduct, Long productcount) {
                this.nameofproduct = nameofproduct;
                this.productcount = productcount;
            }

            public int compareTo(Object o) {
                if (o instanceof ProductPopularityReportItem) {
                    Long productCount =  ((ProductPopularityReportItem)o).getProductcount();
                    if (productCount == null) {
                        return 0;
                    }
                    final long diff = this.productcount - productCount;
                    return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
                } else {
                    return 0;
                }
            }

            public String getNameofproduct() {
                return nameofproduct;
            }

            public void setNameofproduct(String nameOfGood) {
                this.nameofproduct = nameofproduct;
            }

            public Long getProductcount() {
                return productcount;
            }

            public void setProductcount(Long productcount) {
                this.productcount = productcount;
            }

        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new ProductPopularityReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<ProductPopularityReportItem> resultRows = new LinkedList<ProductPopularityReportItem>();
            Query query = session.createSQLQuery("SELECT g.idoftechnologicalmaps, g.idofproducts, count(od.idofgood) as good_count"
                    + " FROM cf_orderdetails od, cf_orders o, cf_goods g"
                    + " WHERE od.idofgood IS NOT NULL AND od.idoforder = o.idoforder"
                    + " AND od.idoforg = :idoforg AND o.createddate >= :startTime AND o.createddate <= :endTime"
                    + " AND od.idofgood = g.idofgood"
                    + " GROUP BY g.idoftechnologicalmaps, g.idofproducts"
                    + " ORDER BY good_count DESC");
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("idoforg", org.getIdOfOrg());
            List list = query.list();
            HashMap<Long, Long> productCountMap = new HashMap<Long, Long>();
            for (Object result : list) {
                Object[] good = (Object[]) result;
                Long idOfTechnologicalMapsFromQuery = null;
                Long idOfProductsFromQuery = null;
                if (good[0] != null) {
                    idOfTechnologicalMapsFromQuery = Long.parseLong(good[0].toString());
                }
                if (good[1] != null) {
                    idOfProductsFromQuery = Long.parseLong(good[1].toString());
                }
                HashSet<Long> productSet = new HashSet<Long>();
                if (idOfProductsFromQuery != null) {
                    productSet.add(idOfProductsFromQuery);
                }
                Long goodCount = Long.parseLong(good[2].toString());

                if (idOfTechnologicalMapsFromQuery != null) {
                    Query techMapQuery = session.createSQLQuery("SELECT idofproducts FROM cf_technological_map_products"
                            + " WHERE idoftechnologicalmaps = :techMap");
                    techMapQuery.setParameter("techMap", idOfTechnologicalMapsFromQuery);
                    List techMapProductsList = techMapQuery.list();
                    for (Object techMapResult : techMapProductsList) {
                        if (techMapResult != null) {
                            Long idOfProductsFromTechMap = Long.parseLong(techMapResult.toString());
                            productSet.add(idOfProductsFromTechMap);
                        }
                    }
                }

                for (Long productId : productSet) {
                    if (!productCountMap.containsKey(productId)) {
                        productCountMap.put(productId, goodCount);
                    } else {
                        productCountMap.put(productId, productCountMap.get(productId) + goodCount);
                    }
                }
            }

            for (Map.Entry<Long, Long> entry : productCountMap.entrySet()) {
                Query productQuery = session.createSQLQuery("SELECT productname FROM cf_products"
                        + " WHERE idofproducts = :productId");
                productQuery.setParameter("productId", entry.getKey());
                Object productResult = productQuery.uniqueResult();
                String nameOfProduct = productResult.toString();
                Long productCount = entry.getValue();
                if ((nameOfProduct != null) && (productCount != null)) {
                    resultRows.add(new ProductPopularityReportItem(nameOfProduct, productCount));
                }
            }

            Collections.sort(resultRows);

            return new JRBeanCollectionDataSource(resultRows);
        }

    }

    public ProductPopularityReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(ProductPopularityReport.class);

    public ProductPopularityReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ProductPopularityReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }
}
