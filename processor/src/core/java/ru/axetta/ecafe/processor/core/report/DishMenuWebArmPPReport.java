/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.Data;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.MapKeyModel;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.ShortBuilding;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.StClass;
import ru.axetta.ecafe.processor.core.service.CardBlockUnblockService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DishMenuWebArmPPReport extends BasicReportForMainBuildingOrgJob {

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
    public static final String REPORT_NAME = "Детализированный отчет по посещению";
    public static final String[] TEMPLATE_FILE_NAMES = {"DetailedEnterEventReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};
    final public static String P_ID_OF_CLIENTS = "idOfClients";

    private final static Logger logger = LoggerFactory.getLogger(DishMenuWebArmPPReport.class);

    final public static String P_ID_OF_CONTRAGENT = "idOfContragent";
    final public static String P_ID_OF_TYPES_FOOD = "idOfTypesFood";
    final public static String P_ID_OF_AGE_GROUP = "idOfAgeGroup";
    final public static String P_ARCHIVED = "archived";

    public DishMenuWebArmPPReport() {

    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new DishMenuWebArmPPReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();

            startTime = CalendarUtils.roundToBeginOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    new JRBeanCollectionDataSource(createDataSource(session)));
            Date generateEndTime = new Date();
            return new BlockUnblockCardReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<DishMenuWebArmPPItem> createDataSource(Session session) {
            List<DishMenuWebArmPPItem> dishMenuWebArmPPItems = new ArrayList<>();

            List<Long> idOfOrgList = parseStringAsLongList(ReportPropertiesUtils.P_ID_OF_ORG);
            List<Long> idOfContragentList = parseStringAsLongList(DishMenuWebArmPPReport.P_ID_OF_CONTRAGENT);
            String idTypeFoodIdString = StringUtils.trimToEmpty(reportProperties.getProperty(DishMenuWebArmPPReport.P_ID_OF_TYPES_FOOD));
            Long idTypeFood;
            try {
                idTypeFood = Long.parseLong(idTypeFoodIdString);
            } catch (Exception e) {
                idTypeFood = null;
            }
            String idAgeGroupString = StringUtils.trimToEmpty(reportProperties.getProperty(DishMenuWebArmPPReport.P_ID_OF_AGE_GROUP));
            Long idAgeGroup;
            try {
                idAgeGroup = Long.parseLong(idAgeGroupString);
            } catch (Exception e)
            {
                idAgeGroup = null;
            }
            Boolean archived;
            Integer archivedInt = 0;
            try {
                archived = Boolean.parseBoolean(reportProperties.getProperty(DishMenuWebArmPPReport.P_ARCHIVED));
                if (archived)
                    archivedInt = 1;
                else
                    archivedInt = 0;
            } catch (Exception e)
            {
                archived = null;
            }

            String filterOrgs = "";
            String filterContragent = "";
            String filterTypeFoodId = "";
            String filterAgeGroup = "";
            if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
                for (Long idofOrg : idOfOrgList) {
                    filterOrgs += "'" + idofOrg + "',";
                }
                filterOrgs = filterOrgs.substring(0, filterOrgs.length() - 1);
                filterOrgs = " and cwco.idoforg in (" + filterOrgs + ") ";
            }
            if (idOfContragentList != null && !idOfContragentList.isEmpty()) {
                for (Long idofContragent : idOfContragentList) {
                    filterContragent += "'" + idofContragent + "',";
                }
                filterContragent = filterContragent.substring(0, filterContragent.length() - 1);
                filterContragent = " and cwd.idofcontragent in (" + filterContragent + ") ";
            }
            if (idTypeFood != null)
            {
                filterTypeFoodId = " and cwgi.idofgroupitem = " + idTypeFood + " ";
            }
            if (idAgeGroup != null)
            {
                filterAgeGroup = " and cwag.idofagegroupitem = " + idAgeGroup + " ";
            }

            String fullWhere = " where (cwd.deletestate = 0 or cwd.deletestate = " + archivedInt + " ) " + filterOrgs + filterContragent + filterTypeFoodId + filterAgeGroup;

            Query dishitemsSQLQuery = session.createSQLQuery(
                    "select distinct cwd.idofdish, cwd.dishname, cwd.componentsofdish, cwd.idofcontragent, cwd.price, cwd.dateofbeginmenuincluding, \n"
                            + "cwd.dateofendmenuincluding, cwag.description as agegroup, cwpi.description as typeOfProduction,\n"
                            + "cwgi.description as typefood, cwc.description as category, cwci.description as subcategory, \n"
                            + "cwd.calories, cwd.qty, cwd.protein, cwd.fat, cwd.carbohydrates, cwd.barcode\n"
                            + "from cf_wt_dishes cwd\n"
                            + "left join cf_wt_agegroup_items cwag on cwag.idofagegroupitem = cwd.idofagegroupitem\n"
                            + "left join cf_wt_typeofproduction_items cwpi on cwpi.idoftypeproductionitem = cwd.idoftypeofproductionitem\n"
                            + "left join cf_wt_dish_groupitem_relationships cwgr on cwgr.idofdish = cwd.idofdish\n"
                            + "left join cf_wt_group_items cwgi on cwgi.idofgroupitem = cwgr.idofgroupitem\n"
                            + "left join cf_wt_categories cwc on cwc.idofcategory = cwd.idofcategory\n"
                            + "left join cf_wt_dish_categoryitem_relationships cwdc on cwdc.idofdish = cwd.idofdish\n"
                            + "left join cf_wt_category_items cwci on cwci.idofcategoryitem = cwdc.idofcategoryitem\n"
                            + "left join cf_wt_complex_items_dish cwcid on cwcid.idofdish = cwd.idofdish\n"
                            + "left join cf_wt_complexes_items cwcid1 on cwcid1.idofcomplexitem = cwcid.idofcomplexitem\n"
                            + "left join cf_wt_complexes_org cwco on cwco.idofcomplex = cwcid1.idofcomplex\n"
                            + fullWhere);

            List dishitems = dishitemsSQLQuery.list();
            for (Object o : dishitems) {
                Object[] row = (Object[]) o;
                DishMenuWebArmPPItem dishMenuWebArmPPItem = new DishMenuWebArmPPItem();
                if (row[0] != null)
                dishMenuWebArmPPItem.setCodeISPP(row[0].toString());
                if (row[1] != null)
                dishMenuWebArmPPItem.setDishname(row[1].toString());
                if (row[2] != null)
                dishMenuWebArmPPItem.setComponentsofdish(row[2].toString());
                if (row[3] != null)
                dishMenuWebArmPPItem.setIdcontragent(row[3].toString());
                if (row[4] != null)
                dishMenuWebArmPPItem.setPrice(row[4].toString());
                if (row[5] != null)
                dishMenuWebArmPPItem.setDateFrom(row[5].toString());
                if (row[6] != null)
                dishMenuWebArmPPItem.setDateTo(row[6].toString());
                if (row[7] != null)
                dishMenuWebArmPPItem.setAgegroup(row[7].toString());
                if (row[8] != null)
                dishMenuWebArmPPItem.setTypeOfProduction(row[8].toString());
                if (row[9] != null)
                dishMenuWebArmPPItem.setTypefood(row[9].toString());
                if (row[10] != null)
                dishMenuWebArmPPItem.setCategory(row[10].toString());
                if (row[11] != null)
                    dishMenuWebArmPPItem.setSubcategory(row[11].toString());
                if (row[12] != null)
                dishMenuWebArmPPItem.setCalories(row[12].toString());
                if (row[13] != null)
                    dishMenuWebArmPPItem.setQty(row[13].toString());
                if (row[14] != null)
                dishMenuWebArmPPItem.setProtein(row[14].toString());
                if (row[15] != null)
                dishMenuWebArmPPItem.setFat(row[15].toString());
                if (row[16] != null)
                dishMenuWebArmPPItem.setCarbohydrates(row[16].toString());
                if (row[17] != null)
                dishMenuWebArmPPItem.setBarcode(row[17].toString());

                dishMenuWebArmPPItems.add(dishMenuWebArmPPItem);

            }

            Collections.sort(dishMenuWebArmPPItems, new Comparator<DishMenuWebArmPPItem>() {
                public int compare(DishMenuWebArmPPItem o1, DishMenuWebArmPPItem o2) {
                    if (o1.getDishname().equals(o2.getDishname())) {
                        return 0;
                    } else if (o2.getDishname().compareTo(o1.getDishname()) < 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            return dishMenuWebArmPPItems;
        }

        private List<Long> parseStringAsLongList(String propertyName) {
            try {
                String propertyValueString = reportProperties.getProperty(propertyName);
                if (propertyValueString == null)
                    return new ArrayList<>();
                String[] propertyValueArray = StringUtils.split(propertyValueString, ',');
                List<Long> propertyValueList = new ArrayList<Long>();
                for (String propertyValue : propertyValueArray) {
                    try {
                        propertyValueList.add(Long.parseLong(propertyValue));
                    } catch (NumberFormatException e) {
                        logger.error(
                                String.format("Unable to parse propertyValue: property = %s, value = %s", propertyName,
                                        propertyValue), e);
                    }
                }
                return propertyValueList;
            }catch (Exception e) {
                return new ArrayList<>();
            }
        }
    }
}
