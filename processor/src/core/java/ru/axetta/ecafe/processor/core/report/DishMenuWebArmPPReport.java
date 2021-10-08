/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
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
    //public static final String REPORT_NAME = "Детализированный отчет по посещению";
    //public static final String[] TEMPLATE_FILE_NAMES = {"DetailedEnterEventReport.jasper"};
    //public static final boolean IS_TEMPLATE_REPORT = true;
    //public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};
    final public static String P_ID_OF_CLIENTS = "idOfClients";

    private final static Logger logger = LoggerFactory.getLogger(DishMenuWebArmPPReport.class);

    final public static String P_ID_OF_CONTRAGENT = "idOfContragent";
    final public static String P_ID_OF_COMPLEXES = "idOfComplexes";
    final public static String P_ID_OF_TYPES_FOOD = "idOfTypesFood";
    final public static String P_ID_OF_AGE_GROUP = "idOfAgeGroup";
    final public static String P_SHOW_CONTAGENT = "showcontagent";
    final public static String P_ARCHIVED = "archived";
    final public static String P_BUFET = "bufet";
    final public static String P_COMPLEX = "complex";
    final public static String P_DISHNAME = "dishName";

    public DishMenuWebArmPPReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
    }

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
            Integer bufet;
            try {
                bufet = Integer.parseInt(reportProperties.getProperty(DishMenuWebArmPPReport.P_BUFET));
            } catch (Exception e) {
                bufet = 0;
            }
            Integer incomplex;
            try {
                incomplex = Integer.parseInt(reportProperties.getProperty(DishMenuWebArmPPReport.P_COMPLEX));
            } catch (Exception e) {
                incomplex = 0;
            }
            Boolean showcontagent = Boolean.parseBoolean(reportProperties.getProperty(DishMenuWebArmPPReport.P_SHOW_CONTAGENT));
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("showcontragent", showcontagent);
            startTime = CalendarUtils.roundToBeginOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    new JRBeanCollectionDataSource(createDataSource(session, bufet, incomplex)));
            Date generateEndTime = new Date();
            return new DishMenuWebArmPPReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<DishMenuWebArmPPItem> createDataSource(Session session, Integer bufet, Integer incomplex) {
            List<DishMenuWebArmPPItem> dishMenuWebArmPPItems = new ArrayList<>();

            List<Long> idOfOrgList = parseStringAsLongList(ReportPropertiesUtils.P_ID_OF_ORG);
            List<Long> idOfContragentList = parseStringAsLongList(DishMenuWebArmPPReport.P_ID_OF_CONTRAGENT);
            List<Long> idOfComplexesList = parseStringAsLongList(DishMenuWebArmPPReport.P_ID_OF_COMPLEXES);
            String idTypeFoodIdString = StringUtils
                    .trimToEmpty(reportProperties.getProperty(DishMenuWebArmPPReport.P_ID_OF_TYPES_FOOD));
            Long idTypeFood;
            try {
                idTypeFood = Long.parseLong(idTypeFoodIdString);
            } catch (Exception e) {
                idTypeFood = null;
            }
            String idAgeGroupString = StringUtils
                    .trimToEmpty(reportProperties.getProperty(DishMenuWebArmPPReport.P_ID_OF_AGE_GROUP));
            Long idAgeGroup;
            try {
                idAgeGroup = Long.parseLong(idAgeGroupString);
            } catch (Exception e) {
                idAgeGroup = null;
            }
            Integer archived = 1;
            try {
                archived = Integer.parseInt(reportProperties.getProperty(DishMenuWebArmPPReport.P_ARCHIVED));
            } catch (Exception e) {
                archived = 1;
            }
            String nameSeach = StringUtils
                    .trimToEmpty(reportProperties.getProperty(DishMenuWebArmPPReport.P_DISHNAME));

            String filterOrgs = "";
            String filterContragent = "";
            String filterComplexes = "";
            String filterTypeFoodId = "";
            String filterAgeGroup = "";
            String filterArchived = "";
            String filterNameDish = "";
            if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
                for (Long idofOrg : idOfOrgList) {
                    filterOrgs += "'" + idofOrg + "',";
                }
                filterOrgs = filterOrgs.substring(0, filterOrgs.length() - 1);
                filterOrgs = " and (cwco.idoforg in (" + filterOrgs + ") or cwmo.idoforg in (" + filterOrgs + ")  or cwgro.idoforg in (" + filterOrgs + ") or cwgro1.idoforg in (" + filterOrgs + ")) and (cwm.deletestate = 0 or cwmc.deletestate = 0) ";
            }
            if (idOfContragentList != null && !idOfContragentList.isEmpty()) {
                for (Long idofContragent : idOfContragentList) {
                    filterContragent += "'" + idofContragent + "',";
                }
                filterContragent = filterContragent.substring(0, filterContragent.length() - 1);
                filterContragent = " and cwd.idofcontragent in (" + filterContragent + ") ";
            }
            if (idOfComplexesList != null && !idOfComplexesList.isEmpty()) {
                for (Long idofComplex : idOfComplexesList) {
                    filterComplexes += "'" + idofComplex + "',";
                }
                filterComplexes = filterComplexes.substring(0, filterComplexes.length() - 1);
                filterComplexes = " and cwco.idofcomplex in (" + filterComplexes + ") ";
            }
            if (idTypeFood != null) {
                filterTypeFoodId = " where cwgi.idofgroupitem = " + idTypeFood + " ";
            }
            if (idAgeGroup != null) {
                filterAgeGroup = " and cwag.idofagegroupitem = " + idAgeGroup + " ";
            }
            if (!nameSeach.isEmpty())
            {
                filterNameDish = " and cwd.dishname ilike '%" + nameSeach + "%'" + " ";
            }

            if (archived == 1)
            {
                filterArchived = " where cwd.deletestate = 0";
            }
            if (archived == 2)
            {
                filterArchived = " where (cwd.deletestate = 0 or cwd.deletestate = 1) ";
            }
            if (archived == 3)
            {
                filterArchived = " where cwd.deletestate = 1 ";
            }

            String fullWhere = filterArchived  + filterOrgs
                    + filterContragent  + filterAgeGroup + filterComplexes + filterNameDish;

            String sqlQueryBase =
                    "select base2.idofdish, base2.dishname, base2.componentsofdish, base2.code, base2.price, base2.dateofbeginmenuincluding, \n"
                            + "base2.dateofendmenuincluding, base2.agegroup, base2.typeOfProduction,\n"
                            + "base2.typefood, base2.category, string_agg(distinct cwci.description, ';' order by cwci.description) as subcategory, \n"
                            + "base2.calories, base2.qty, base2.protein, base2.fat, base2.carbohydrates, base2.barcode, base2.dishdel, base2.contragentname from\n"
                            + "(select base1.idofdish, base1.dishname, base1.componentsofdish, base1.code, base1.price, \n"
                            + "base1.dateofbeginmenuincluding, base1.dateofendmenuincluding, base1.agegroup, base1.typeOfProduction,\n"
                            + "string_agg(distinct cwgi.description, ';' order by cwgi.description) as typefood, base1.category, \n"
                            + "base1.calories, base1.qty, base1.protein, base1.fat, base1.carbohydrates, base1.barcode, base1.dishdel, base1.contragentname\n"
                            + "from \n"
                            + "(select distinct cwd.idofdish, cwd.dishname, cwd.componentsofdish, cwd.code, cwd.price, cwd.dateofbeginmenuincluding, \n"
                            + "cwd.dateofendmenuincluding, cwag.description as agegroup, cwpi.description as typeOfProduction,\n"
                            + "cwc.description as category,\n"
                            + "cwd.calories, cwd.qty, cwd.protein, cwd.fat, cwd.carbohydrates, cwd.barcode, cwgr.idofgroupitem, cwd.deletestate as dishdel,\n"
                            + "cwm.deletestate as menudel, cwmc.deletestate as compldel, cc.contragentname \n"
                            + "from cf_wt_dishes cwd \n"
                            + "left join cf_contragents cc on cc.idofcontragent = cwd.idofcontragent \n"
                            + "left join cf_wt_agegroup_items cwag on cwag.idofagegroupitem = cwd.idofagegroupitem\n"
                            + "left join cf_wt_typeofproduction_items cwpi on cwpi.idoftypeproductionitem = cwd.idoftypeofproductionitem\n"
                            + "left join cf_wt_dish_groupitem_relationships cwgr on cwgr.idofdish = cwd.idofdish\n"
                            + "left join cf_wt_categories cwc on cwc.idofcategory = cwd.idofcategory\n"
                            + "left join cf_wt_complex_items_dish cwcid on cwcid.idofdish = cwd.idofdish\n"
                            + "left join cf_wt_complexes_items cwcid1 on cwcid1.idofcomplexitem = cwcid.idofcomplexitem\n"
                            + "left join cf_wt_complexes cwmc on cwmc.idofcomplex = cwcid1.idofcomplex\n"
                            + "left join cf_wt_org_group_relations cwgro on cwgro.idoforggroup = cwmc.idoforggroup\n"
                            + "left join cf_wt_complexes_org cwco on cwco.idofcomplex = cwcid1.idofcomplex\n"
                            + "left join cf_wt_menu_group_dish_relationships cwmgr on cwmgr.idofdish = cwd.idofdish\n"
                            + "left join cf_wt_menu_group_relationships cwmg on cwmg.id = cwmgr.idofmenumenugrouprelation\n"
                            + "left join cf_wt_menu cwm on cwm.idofmenu = cwmg.idofmenu\n"
                            + "left join cf_wt_org_group_relations cwgro1 on cwgro1.idoforggroup = cwm.idoforggroup\n"
                            + "left join cf_wt_menu_org cwmo on cwmo.idofmenu = cwm.idofmenu\n"
                            + fullWhere + ") as base1\n"
                            + "left join cf_wt_group_items cwgi on cwgi.idofgroupitem = base1.idofgroupitem \n"
                            + filterTypeFoodId
                            + "group by base1.idofdish, base1.dishname, base1.componentsofdish, base1.code, \n"
                            + "base1.price, base1.dateofbeginmenuincluding,base1.dateofendmenuincluding, base1.agegroup, base1.typeOfProduction, \n"
                            + "base1.category,\n"
                            + "base1.calories, base1.qty, base1.protein, base1.fat, base1.carbohydrates, base1.barcode, base1.dishdel, base1.contragentname) as base2\n"
                            + "left join cf_wt_dish_categoryitem_relationships cwdc on cwdc.idofdish = base2.idofdish\n"
                            + "left join cf_wt_category_items cwci on cwci.idofcategoryitem = cwdc.idofcategoryitem\n"
                            + "group by base2.idofdish, base2.dishname, base2.componentsofdish, base2.code, base2.price, base2.dateofbeginmenuincluding, \n"
                            + "base2.dateofendmenuincluding, base2.agegroup, base2.typeOfProduction,\n"
                            + "base2.typefood, base2.category, \n"
                            + "base2.calories, base2.qty, base2.protein, base2.fat, base2.carbohydrates, base2.barcode, base2.dishdel, base2.contragentname";

            ///////////////////
            if (bufet>1 && incomplex>1) {
                String filterCount = "";
                Boolean usedWhere = false;
                if (bufet == 3) {
                    filterCount = " where base4.countInMenu>0 ";
                    usedWhere = true;
                }
                if (bufet == 4) {
                    filterCount = " where base4.countInMenu=0 ";
                    usedWhere = true;
                }
                if (incomplex == 3) {
                    if (!usedWhere)
                        filterCount = " where base5.countInComplex>0 ";
                    else
                        filterCount = filterCount + " and base5.countInComplex>0 ";
                }
                if (incomplex == 4) {
                    if (!usedWhere)
                        filterCount = "where base5.countInComplex=0 ";
                    else
                        filterCount = " and base5.countInComplex=0 ";
                }


                sqlQueryBase = "select base4.*, base5.countInComplex from (\n"
                        + "select base3.*, count (distinct cwm.idofmenu) as countInMenu from (" + sqlQueryBase + ") as base3\n"
                        + "left join cf_wt_menu_group_dish_relationships cwmgr on cwmgr.idofdish = base3.idofdish\n"
                        + "left join cf_wt_menu_group_relationships cwmg on cwmg.id = cwmgr.idofmenumenugrouprelation\n"
                        + "left join (select * from cf_wt_menu where deletestate = 0) cwm on cwm.idofmenu = cwmg.idofmenu\n"
                        + "group by base3.idofdish, base3.dishname, base3.componentsofdish, base3.code, base3.price, base3.dateofbeginmenuincluding, \n"
                        + "base3.dateofendmenuincluding, base3.agegroup, base3.typeOfProduction,\n"
                        + "base3.typefood, base3.category, base3.subcategory, \n"
                        + "base3.calories, base3.qty, base3.protein, base3.fat, base3.carbohydrates, base3.barcode, base3.dishdel, base3.contragentname) as base4\n"
                        + "inner join \n"
                        + "(select base3.*, count (distinct cwtc.idofcomplex) as countInComplex from \n"
                        + "(" + sqlQueryBase + ") as base3\n"
                        + "left join cf_wt_complex_items_dish cwcid on cwcid.idofdish = base3.idofdish\n"
                        + "left join cf_wt_complexes_items cwcid1 on cwcid1.idofcomplexitem = cwcid.idofcomplexitem\n"
                        + "left join (select * from cf_wt_complexes where deletestate = 0) cwtc on cwtc.idofcomplex = cwcid1.idofcomplex\n"
                        + "group by base3.idofdish, base3.dishname, base3.componentsofdish, base3.code, base3.price, base3.dateofbeginmenuincluding, \n"
                        + "base3.dateofendmenuincluding, base3.agegroup, base3.typeOfProduction,\n"
                        + "base3.typefood, base3.category, base3.subcategory, \n"
                        + "base3.calories, base3.qty, base3.protein, base3.fat, base3.carbohydrates, base3.barcode, base3.dishdel, base3.contragentname) as base5\n"
                        + "on base4.idofdish = base5.idofdish " + filterCount;
            } else
            {
                if (bufet>1) {
                    String filterCount = "";
                    if (bufet == 3)
                        filterCount = " where base4.countInMenu>0";
                    if (bufet == 4)
                        filterCount = " where base4.countInMenu=0";
                    sqlQueryBase =  "select * from (select base3.*, count (distinct cwm.idofmenu) as countInMenu from \n"
                            + "(" + sqlQueryBase + ") as base3\n"
                            + "left join cf_wt_menu_group_dish_relationships cwmgr on cwmgr.idofdish = base3.idofdish\n"
                            + "left join cf_wt_menu_group_relationships cwmg on cwmg.id = cwmgr.idofmenumenugrouprelation\n"
                            + "left join (select * from cf_wt_menu where deletestate = 0) cwm on cwm.idofmenu = cwmg.idofmenu\n"
                            + "group by base3.idofdish, base3.dishname, base3.componentsofdish, base3.code, base3.price, base3.dateofbeginmenuincluding, \n"
                            + "base3.dateofendmenuincluding, base3.agegroup, base3.typeOfProduction,\n"
                            + "base3.typefood, base3.category, base3.subcategory, \n"
                            + "base3.calories, base3.qty, base3.protein, base3.fat, base3.carbohydrates, base3.barcode, base3.dishdel, base3.contragentname) as base4 "
                            + filterCount;

                } else {
                    if (incomplex>1) {
                        String filterCount = "";
                        if (incomplex == 3)
                            filterCount = " where base4.countInComplex>0";
                        if (incomplex == 4)
                            filterCount = " where base4.countInComplex=0";
                        sqlQueryBase = "select * from (select base3.*, count (distinct cwtc.idofcomplex) as countInComplex from \n"
                                + "(" + sqlQueryBase + ") as base3\n"
                                + "left join cf_wt_complex_items_dish cwcid on cwcid.idofdish = base3.idofdish\n"
                                + "left join cf_wt_complexes_items cwcid1 on cwcid1.idofcomplexitem = cwcid.idofcomplexitem\n"
                                + "left join (select * from cf_wt_complexes where deletestate = 0) cwtc on cwtc.idofcomplex = cwcid1.idofcomplex\n"
                                + "group by base3.idofdish, base3.dishname, base3.componentsofdish, base3.code, base3.price, base3.dateofbeginmenuincluding, \n"
                                + "base3.dateofendmenuincluding, base3.agegroup, base3.typeOfProduction,\n"
                                + "base3.typefood, base3.category, base3.subcategory, \n"
                                + "base3.calories, base3.qty, base3.protein, base3.fat, base3.carbohydrates, base3.barcode, base3.dishdel, base3.contragentname) as base4 "
                                + filterCount;
                    }
                }
            }
            Query dishitemsSQLQuery = session.createSQLQuery(sqlQueryBase);
            ///////////////////
            List dishitems = dishitemsSQLQuery.list();
            for (Object o : dishitems) {
                Object[] row = (Object[]) o;
                DishMenuWebArmPPItem dishMenuWebArmPPItem = new DishMenuWebArmPPItem();
                if (row[0] != null) {
                    dishMenuWebArmPPItem.setCodeISPP(row[0].toString());
                }
                if (row[1] != null) {
                    dishMenuWebArmPPItem.setDishname(row[1].toString());
                }
                if (row[2] != null) {
                    dishMenuWebArmPPItem.setComponentsofdish(row[2].toString());
                }
                if (row[3] != null) {
                    dishMenuWebArmPPItem.setIdsupplier(row[3].toString());
                }
                if (row[4] != null) {
                    dishMenuWebArmPPItem.setPrice(row[4].toString());
                }
                if (row[5] != null) {
                    dishMenuWebArmPPItem.setDateFrom(new Date(((Timestamp) row[5]).getTime()));
                }
                if (row[6] != null) {
                    dishMenuWebArmPPItem.setDateTo(new Date(((Timestamp) row[6]).getTime()));
                }
                if (row[7] != null) {
                    dishMenuWebArmPPItem.setAgegroup(row[7].toString());
                }
                if (row[8] != null) {
                    dishMenuWebArmPPItem.setTypeOfProduction(row[8].toString());
                }
                if (row[9] != null) {
                    dishMenuWebArmPPItem.setTypefood(row[9].toString());
                }
                if (row[10] != null) {
                    dishMenuWebArmPPItem.setCategory(row[10].toString());
                }
                if (row[11] != null) {
                    dishMenuWebArmPPItem.setSubcategory(row[11].toString());
                }
                if (row[12] != null) {
                    dishMenuWebArmPPItem.setCalories(row[12].toString());
                }
                if (row[13] != null) {
                    dishMenuWebArmPPItem.setQty(row[13].toString());
                }
                if (row[14] != null) {
                    dishMenuWebArmPPItem.setProtein(row[14].toString());
                }
                if (row[15] != null) {
                    dishMenuWebArmPPItem.setFat(row[15].toString());
                }
                if (row[16] != null) {
                    dishMenuWebArmPPItem.setCarbohydrates(row[16].toString());
                }
                if (row[17] != null) {
                    dishMenuWebArmPPItem.setBarcode(row[17].toString());
                }
                if (row[18] != null) {
                    try {
                        dishMenuWebArmPPItem.setArchived((Integer) row[18]);
                    } catch (Exception e) {
                        dishMenuWebArmPPItem.setArchived(0);
                    }
                }
                if (row[19] != null) {
                    dishMenuWebArmPPItem.setContragentName(row[19].toString());
                }
                if (bufet>1 && incomplex>1) {
                    if (row[20] != null) {
                        dishMenuWebArmPPItem.setCountInMenu(row[20].toString());
                    }
                    if (row[21] != null) {
                        dishMenuWebArmPPItem.setCountInComplex(row[21].toString());
                    }
                } else
                {
                    if (bufet>1) {
                        if (row[20] != null) {
                            dishMenuWebArmPPItem.setCountInMenu(row[20].toString());
                        }
                    } else {
                        if (incomplex>1)
                            if (row[20] != null) {
                                dishMenuWebArmPPItem.setCountInComplex(row[20].toString());
                            }
                    }
                }
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
                if (propertyValueString == null) {
                    return new ArrayList<>();
                }
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
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
    }
}
