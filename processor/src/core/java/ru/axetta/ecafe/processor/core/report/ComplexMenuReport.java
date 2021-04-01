/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplexGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDietType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("singleton")
public class ComplexMenuReport extends BasicReportForContragentJob {

    private Logger logger = LoggerFactory.getLogger(ComplexMenuReport.class);

    public static class Builder extends BasicReportForContragentJob.Builder {
        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance()
                    .getAutoReportGenerator().getReportsTemplateFilePath()
                    + ComplexMenuReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.startOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            Long idOfContragent = (contragent == null ? null : contragent.getIdOfContragent());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }
            Long typeFood = Long.valueOf(getReportProperties().getProperty("selectIdTypeFoodId"));
            Long diet = Long.valueOf(getReportProperties().getProperty("selectDiet"));
            Long ageGroup = Long.valueOf(getReportProperties().getProperty("selectIdAgeGroup"));
            Long archived = Long.valueOf(getReportProperties().getProperty("selectArchived"));
            JRDataSource dataSource = new JRBeanCollectionDataSource(createDataSource(session, contragent, idOfOrgList, typeFood, diet, ageGroup, archived));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent);
        }

        public List<ComplexMenuReportItem> createDataSource(Session session, Contragent contragent, List<Long> idOfOrgList, Long typeFood, Long diet, Long ageGroup, Long archived) throws Exception {
            List<ComplexMenuReportItem> result = new LinkedList<>();
            String idOfOrgsCondition = CollectionUtils.isEmpty(idOfOrgList) ? "" : " and o.idoforg in (:idOfOrgList) " ;
            String idOfContragentCondition = contragent == null ? "" : " and wc.idofcontragent = :idOfContragent ";
            String typeFoodCondition = typeFood == -1 ? "" : " and wc.idofcomplexgroupitem = :typeFood ";
            String dietCondition = diet == -1 ? "" : " and wc.idofdiettype = :diet ";
            String ageGroupCondition = ageGroup == -1 ? "" : " and wc.idofagegroupitem = :ageGroup ";
            String archivedCondition = archived == -1 ? "" : " and wc.deletestate = :archived ";

            String getIdOfOrg = "with complex_menu AS ( "
                    + "select o.idoforg, o.organizationtype, wc.idofcomplex, wc.idofcomplexgroupitem, wc.idofdiettype, wc.idofagegroupitem, wc.price, wc.is_portal, wc.begindate, wc.enddate, wc.name "
                    + "from cf_wt_complexes_org co "
                    + "join cf_orgs o on co.idoforg = o.idoforg "
                    + "join cf_wt_complexes wc on co.idofcomplex = wc.idofcomplex "
                    + " where co.idoforg >= 0 "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + typeFoodCondition
                    + dietCondition
                    + ageGroupCondition
                    + archivedCondition
                    + " union "
                    + "select o.idoforg, o.organizationtype, wc.idofcomplex, wc.idofcomplexgroupitem, wc.idofdiettype, wc.idofagegroupitem, wc.price, wc.is_portal, wc.begindate, wc.enddate, wc.name "
                    + "from cf_wt_org_group_relations ogr "
                    + "join cf_orgs o on ogr.idoforg = o.idoforg "
                    + "join cf_wt_complexes wc on wc.idoforggroup = ogr.idoforggroup"
                    + " where wc.idoforggroup >= 0 "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + typeFoodCondition
                    + dietCondition
                    + ageGroupCondition
                    + archivedCondition
                    + " )"
                    + " select * from complex_menu "
                    + " order by 1, 4, 5, 6 ";
            Query query = session.createSQLQuery(getIdOfOrg);

            String getDish = "with dish_menu AS ( "
                    + "select o.idoforg, wc.idofcomplex, ci.cycle_day, COUNT(cid.idofdish) , array_to_string(array_agg(cid.idofdish), '&&') as dishes "
                    + "from cf_wt_complexes_org co "
                    + "join cf_orgs o on co.idoforg = o.idoforg "
                    + "join cf_wt_complexes wc on co.idofcomplex = wc.idofcomplex "
                    + "join cf_wt_complexes_items ci on wc.idofcomplex = ci.idofcomplex "
                    + "left join cf_wt_complex_items_dish cid on ci.idofcomplexitem = cid.idofcomplexitem "
                    + " where co.idoforg >= 0 "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + typeFoodCondition
                    + dietCondition
                    + ageGroupCondition
                    + archivedCondition
                    + " group by 1, 2, 3 "
                    + " union "
                    + "select o.idoforg, wc.idofcomplex, ci.cycle_day, COUNT(cid.idofdish) , array_to_string(array_agg(cid.idofdish), '&&') as dishes "
                    + "from cf_wt_org_group_relations ogr "
                    + "join cf_orgs o on ogr.idoforg = o.idoforg "
                    + "join cf_wt_complexes wc on wc.idoforggroup = ogr.idoforggroup "
                    + "join cf_wt_complexes_items ci on wc.idofcomplex = ci.idofcomplex "
                    + "left join cf_wt_complex_items_dish cid on ci.idofcomplexitem = cid.idofcomplexitem "
                    + " where wc.idoforggroup >= 0 "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + typeFoodCondition
                    + dietCondition
                    + ageGroupCondition
                    + archivedCondition
                    + " group by 1, 2, 3 "
                    + " )"
                    + " select * from dish_menu "
                    + " order by 1, 2, 3 ";
            Query queryDish = session.createSQLQuery(getDish);

            if(contragent != null) {
                query.setParameter("idOfContragent", contragent.getIdOfContragent());
                queryDish.setParameter("idOfContragent", contragent.getIdOfContragent());
            }
            if(!CollectionUtils.isEmpty(idOfOrgList)) {
                query.setParameterList("idOfOrgList", idOfOrgList);
                queryDish.setParameterList("idOfOrgList", idOfOrgList);
            }
            if(typeFood != -1) {
                query.setParameter("typeFood", typeFood);
                queryDish.setParameter("typeFood", typeFood);
            }
            if(diet != -1) {
                query.setParameter("diet", diet);
                queryDish.setParameter("diet", diet);
            }
            if(ageGroup != -1) {
                query.setParameter("ageGroup", ageGroup);
                queryDish.setParameter("ageGroup", ageGroup);
            }
            if(archived != -1) {
                query.setParameter("archived", archived);
                queryDish.setParameter("archived", archived);
            }
            List<Object[]> idOfOrg = query.list();
            List<Object[]> dishList = queryDish.list();

            if (CollectionUtils.isEmpty(idOfOrg)) {
                throw new Exception("Нет данных для построения отчета");
            }

            List<ComplexMenuReportItem> orgList = sortOrg(idOfOrg);
            orgList = sortComplex(orgList, idOfOrg);

            List<WtComplexGroupItem> wtGroupComplex = DAOService.getInstance().getTypeComplexFood();
            List<WtDietType> dietGroupItems = DAOService.getInstance().getMapDiet();
            List<WtAgeGroupItem> ageGroups = DAOService.getInstance().getAgeGroups();
            List<ComplexItem> complexItems = getComplexItem(idOfOrg, wtGroupComplex, dietGroupItems, ageGroups, dishList);

            for (ComplexMenuReportItem complex: orgList) {
                List<ComplexItem> complexItem = new ArrayList<>();
                for (String co : complex.getComplexList())
                    complexItem.add(getCurrentComplexItem(co, complexItems));
                StringBuilder org = new StringBuilder();
                for (String orgs: complex.getOrg())
                    org.append(orgs).append(", ");
                if (org.length() > 1)
                    org.setLength(org.length() - 2);

                result.add(new ComplexMenuReportItem(org.toString(), complex.getOrgCount(), complexItem));
            }
            return result;
        }

        private ComplexItem getCurrentComplexItem(String complex, List<ComplexItem> complexList){
            for (ComplexItem compexList: complexList)
                if (compexList.getIdOfComplex().equals(complex))
                    return compexList;
                return null;
        }

        private List<ComplexItem> getComplexItem(List<Object[]> idOfOrg, List<WtComplexGroupItem> wtGroupComplex,
                List<WtDietType> dietGroupItems, List<WtAgeGroupItem> ageGroups, List<Object[]> dishList ){
            List<ComplexItem> complexList = new ArrayList<>();
            for (Object[] id : idOfOrg) {
                String groupComplex = "";
                String dietType = "";
                String agrGroup = "";
                StringBuilder cycle = new StringBuilder();
                BigDecimal price = BigDecimal.valueOf(0);
                if (id[6] != null)
                    price = new BigDecimal(id[6].toString());
                String isPortal = Boolean.parseBoolean(id[7].toString()) ? "Да" : "Нет";

                String dates = "";

                try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
                String beginDate = myFormat.format(format.parse(id[8].toString()));
                String endDate = myFormat.format(format.parse(id[9].toString()));
                    dates = beginDate + " - " + endDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (WtAgeGroupItem aGroup: ageGroups)
                    if (id[5].toString().equals(aGroup.getIdOfAgeGroupItem().toString()))
                        agrGroup = aGroup.getDescription();

                for (WtDietType dType: dietGroupItems)
                    if (id[4].toString().equals(dType.getIdOfDietType().toString()))
                        dietType = dType.getDescription();

                for (WtComplexGroupItem gComplex: wtGroupComplex)
                    if (id[3].toString().equals(gComplex.getIdOfComplexGroupItem().toString()))
                        groupComplex = gComplex.getDescription();

                for (Object[] dl : dishList) {
                    if(dl[1].toString().equals(id[2].toString()) && dl[0].toString().equals(id[0].toString()))
                        cycle.append(dl[2].toString()).append("(").append(dl[3].toString()).append(")").append(", ");
                }
                if (cycle.length() > 1)
                    cycle.setLength(cycle.length() - 2);

                ComplexItem complexItem = new ComplexItem(id[2].toString(), groupComplex,
                        dietType, agrGroup, price, isPortal, dates, id[10].toString(), cycle.toString());
                complexList.add(complexItem);
            }
            return complexList;
        }

        private List<ComplexMenuReportItem> sortOrg(List<Object[]> idOfOrg){
            List<ComplexMenuReportItem> complexMenuReportList = new ArrayList<>();
            String orgType = null;
            Set<String> sortOrg = new HashSet<>();
            for (Object[] orgs: idOfOrg)
                sortOrg.add(orgs[0].toString());
            for (String sort: sortOrg){
                ArrayList<String> idOfComplex = new ArrayList<>();
                for (Object[] orgs: idOfOrg)
                    if (sort.equals(orgs[0].toString())) {
                        idOfComplex.add(orgs[2].toString());
                        orgType = orgs[1].toString();
                    }
                Arrays.sort(new ArrayList[]{idOfComplex});
                complexMenuReportList.add(new ComplexMenuReportItem(Arrays.asList(sort), orgType, idOfComplex));
            }
            return complexMenuReportList;
        }

        private String getOrgCount(List<String> sort, List<Object[]> idOfOrg){
            int dou = 0, soh = 0;
            for (String ss: sort){
                for (Object[] id: idOfOrg){
                    if (ss.equals(id[0].toString())){
                        if (id[1].toString().equals("1"))
                            dou++;
                        else
                            soh++;
                        break;
                    }
                }
            }
            return dou + " ДОУ, " + soh + " СОШ";
        }

        private List<ComplexMenuReportItem> sortComplex(List<ComplexMenuReportItem> list, List<Object[]> idOfOrg){
            List<ComplexMenuReportItem> item = new ArrayList<>();
            for(int s = 0; s < list.size(); s++){
                if (list.get(s).getOrg() != null) {
                    List<String> newOrg = new ArrayList<>();
                    newOrg.add(list.get(s).getOrg().get(0));
                    for (ComplexMenuReportItem complexMenuReportItem : list) {
                        if (equalsComplex(list.get(s).getComplexList(), complexMenuReportItem.getComplexList()) &&
                                !list.get(s).getOrg().get(0).equals(complexMenuReportItem.getOrg().get(0))) {
                            newOrg.add(complexMenuReportItem.getOrg().get(0));
                            complexMenuReportItem.setOrg(null);
                        }
                    }
                        ComplexMenuReportItem complexMenuReportItem = new ComplexMenuReportItem(newOrg, getOrgCount(newOrg, idOfOrg), list.get(s).getComplexList());
                        item.add(complexMenuReportItem);
                }
            }
            return item;
        }

        private boolean equalsComplex(List<String> complexOne, List<String> complexTwo){
            if (complexOne.size() != complexTwo.size())
                return false;
            for (String one: complexOne){
                boolean equals = false;
                for (String two: complexTwo)
                    if (one.equals(two)) {
                        equals = true;
                        break;
                    }
                if (!equals)
                    return false;
            }
            return true;
        }
    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ComplexMenuReport();
    }

    @Override
    public ComplexMenuReport.Builder createBuilder(String templateFilename) {
        return new ComplexMenuReport.Builder(templateFilename);
    }
}
