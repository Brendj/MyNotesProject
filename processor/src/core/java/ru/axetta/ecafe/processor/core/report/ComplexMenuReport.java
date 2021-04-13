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
import java.sql.Timestamp;
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
            startTime = startTime == null ? null : CalendarUtils.startOfDay(startTime);
            parameterMap.put("startDate", startTime);

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
            Boolean showCycle = Boolean.valueOf(getReportProperties().getProperty("showCycle"));
            Long dishIds = null;
            try {
                dishIds = Long.valueOf(getReportProperties().getProperty("dishIds"));
            }catch (NumberFormatException ignore){}
            JRDataSource dataSource = new JRBeanCollectionDataSource(createDataSource(session, contragent, idOfOrgList, typeFood, diet, ageGroup, archived, startTime, dishIds, showCycle));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent);
        }

        public List<ComplexMenuReportItem> createDataSource(Session session, Contragent contragent, List<Long> idOfOrgList, Long typeFood, Long diet,
                Long ageGroup, Long archived, Date date, Long dishIds, Boolean showCycle) throws Exception {
            List<ComplexMenuReportItem> result = new LinkedList<>();
            List<WtComplexGroupItem> wtGroupComplex = DAOService.getInstance().getTypeComplexFood();
            List<WtDietType> dietGroupItems = DAOService.getInstance().getMapDiet();
            List<WtAgeGroupItem> ageGroups = DAOService.getInstance().getAgeGroups();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp ts = date == null ? null : Timestamp.valueOf(formatter.format(date));
            String idOfOrgsCondition = CollectionUtils.isEmpty(idOfOrgList) ? "" : " and o.idoforg in (:idOfOrgList) " ;
            String idOfContragentCondition = contragent == null ? "" : " and wc.idofcontragent = :idOfContragent ";
            String typeFoodCondition = typeFood == -1 ? "" : " and wc.idofcomplexgroupitem = :typeFood ";
            String dietCondition = diet == -1 ? "" : " and wc.idofdiettype = :diet ";
            String ageGroupCondition = ageGroup == -1 ? "" : " and wc.idofagegroupitem = :ageGroup ";
            String archivedCondition = archived == -1 ? "" : " and wc.deletestate = :archived ";
            String dateCondition = ts == null ? "" : " and wc.begindate <= :ts and wc.enddate >= :ts ";
            String dishCondition = dishIds == null ? "" : " and cid.idofdish = :dishIds ";

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
                    + dateCondition
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
                    + dateCondition
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
                    + dishCondition
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
                    + dishCondition
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
            if(ts != null)
                query.setParameter("ts", ts);
            if(dishIds != null)
                queryDish.setParameter("dishIds", dishIds);
            List<Object[]> complexList = query.list();
            List<Object[]> dishList = null;
            if(showCycle)
                dishList = queryDish.list();
            if (CollectionUtils.isEmpty(complexList)) {
                throw new Exception("Нет данных для построения отчета");
            }

            Set<Long> orgId = new TreeSet<>();
            for (Object[] idOfOrg: complexList)
                orgId.add(Long.valueOf(idOfOrg[0].toString()));
            String idOfOrgs = CollectionUtils.isEmpty(orgId) ? "" : " and co.idoforg in (:orgId) " ;
            String getOrg = " select co.idoforg, co.shortnameinfoservice, co.address, co.shortname, co.district "
                    + " from cf_orgs co "
                    + "where co.idoforg >= 0 "
                    + idOfOrgs ;
            Query queryOrg = session.createSQLQuery(getOrg);
            if(!CollectionUtils.isEmpty(orgId))
                queryOrg.setParameterList("orgId", orgId);
            List<Object[]> orgData = queryOrg.list();
            if (CollectionUtils.isEmpty(orgData))
                throw new Exception("Нет данных для построения отчета");

            List<ComplexMenuReportItem> orgList = sortOrg(complexList);
            orgList = sortComplex(orgList, complexList);
            List<ComplexItem> complexItems = getComplexItem(complexList, wtGroupComplex, dietGroupItems, ageGroups, dishList, getAllComplex(complexList));
            for (ComplexMenuReportItem complex: orgList) {
                List<ComplexItem> finalComplex = new ArrayList<>();
                for (String co : complex.getComplexList())
                    finalComplex.add(getCurrentComplexItem(co, complexItems));
                StringBuilder org = new StringBuilder();
                for (String orgs: complex.getOrg())
                    org.append(orgs).append(", ");
                if (org.length() > 1)
                    org.setLength(org.length() - 2);
                finalComplex = checkDish(finalComplex, dishCondition);
                if (finalComplex.size() > 0)
                    result.add(new ComplexMenuReportItem(org.toString(), complex.getOrgCount(), finalComplex, getOrgList(complex, orgData), showCycle));
                else{
                    throw new Exception("Нет данных для построения отчета");
                }
            }
            return result;
        }

        private List<String> getAllComplex(List<Object[]> allComplex){
            Set<String> complexList = new TreeSet<>();
            for(Object[] complex: allComplex)
                complexList.add(complex[2].toString());
            return new ArrayList<>(complexList);
        }

        private List<ComplexItem> checkDish(List<ComplexItem> finalComplex, String dishCondition){
            if (dishCondition.equals(""))
                return finalComplex;
            List<ComplexItem> result = new ArrayList<>();
            for (ComplexItem complexItem: finalComplex)
                if (!complexItem.getCycle().equals("") && complexItem.getCycle() != null)
                    result.add(complexItem);
            return result;
        }

        private List<ComplexOrgItem> getOrgList(ComplexMenuReportItem complex, List<Object[]> orgData) {
            List<ComplexOrgItem> orgItem = new ArrayList<>();
            for (String org: complex.getOrg()){
                for (Object[] data: orgData){
                    if(org.equals(data[0].toString()))
                        orgItem.add(new ComplexOrgItem(data[0].toString(), data[1].toString(), data[2].toString(),
                                data[3].toString(), data[4].toString()));
                }
            }
            return orgItem;
        }


        private ComplexItem getCurrentComplexItem(String complex, List<ComplexItem> complexList){
            for (ComplexItem list: complexList)
                if (list.getIdOfComplex().equals(complex))
                    return list;
                return null;
        }

        private List<ComplexItem> getComplexItem(List<Object[]> complexList, List<WtComplexGroupItem> wtGroupComplex,
                List<WtDietType> dietGroupItems, List<WtAgeGroupItem> ageGroups, List<Object[]> dishList, List<String> allComplex){
            List<ComplexItem> complexes = new ArrayList<>();
            for (Object[] complex : complexList) {
                if (allComplex.contains(complex[2].toString()))
                    allComplex.remove(complex[2].toString());
                else continue;
                String groupComplex = "";
                String dietType = "";
                String agrGroup = "";
                String startDate = "";
                String endDate = "";
                String complexName = complex[10].toString();
                StringBuilder cycle = new StringBuilder();
                BigDecimal price = BigDecimal.valueOf(0);
                if (complex[6] != null)
                    price = new BigDecimal(complex[6].toString());
                String isPortal = Boolean.parseBoolean(complex[7].toString()) ? "Да" : "Нет";
                try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
                startDate = myFormat.format(format.parse(complex[8].toString()));
                endDate = myFormat.format(format.parse(complex[9].toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for (WtAgeGroupItem aGroup: ageGroups)
                    if (complex[5].toString().equals(aGroup.getIdOfAgeGroupItem().toString()))
                        agrGroup = aGroup.getDescription();

                for (WtDietType dType: dietGroupItems)
                    if (complex[4].toString().equals(dType.getIdOfDietType().toString()))
                        dietType = dType.getDescription();

                for (WtComplexGroupItem gComplex: wtGroupComplex)
                    if (complex[3].toString().equals(gComplex.getIdOfComplexGroupItem().toString()))
                        groupComplex = gComplex.getDescription();
                    if(dishList != null)
                        for (Object[] dl : dishList) {
                            if(dl[1].toString().equals(complex[2].toString()) && dl[0].toString().equals(complex[0].toString()))
                        cycle.append(dl[2].toString()).append("(").append(dl[3].toString()).append(")").append(", ");
                }
                if (cycle.length() > 1)
                    cycle.setLength(cycle.length() - 2);

                ComplexItem complexItem = new ComplexItem(complex[2].toString(), groupComplex,
                        dietType, agrGroup, price, isPortal, startDate, endDate, complexName, cycle.toString());
                complexes.add(complexItem);
            }
            return complexes;
        }

        private List<ComplexMenuReportItem> sortOrg(List<Object[]> complexList){
            List<ComplexMenuReportItem> complexMenuReportList = new ArrayList<>();
            String orgType = null;
            Set<String> sortOrg = new TreeSet<>();
            for (Object[] complex: complexList)
                sortOrg.add(complex[0].toString());
            for (String sort: sortOrg){
                ArrayList<String> idOfComplex = new ArrayList<>();
                for (Object[] complex: complexList)
                    if (sort.equals(complex[0].toString())) {
                        idOfComplex.add(complex[2].toString());
                        orgType = complex[1].toString();
                    }
                Arrays.sort(new ArrayList[]{idOfComplex});
                complexMenuReportList.add(new ComplexMenuReportItem(Arrays.asList(sort), orgType, idOfComplex));
            }
            return complexMenuReportList;
        }

        private String getOrgCount(List<String> sort, List<Object[]> idOfOrg){
            int dou = 0, soh = 0, spo = 0;
            StringBuilder result = new StringBuilder();
            for (String ss: sort){
                for (Object[] id: idOfOrg){
                    if (ss.equals(id[0].toString())){
                        switch (id[1].toString()) {
                            case "0":
                                soh++;
                                break;
                            case "1":
                                dou++;
                                break;
                            case "3":
                                spo++;
                                break;
                        }
                        break;
                    }
                }
            }
            if (soh > 0)
                result.append(soh).append(" СОШ, ");
            if (dou > 0)
                result.append(dou).append(" ДОУ, ");
            if (spo > 0)
                result.append(spo).append(" СПО, ");
            if (result.length() > 1)
                result.setLength(result.length() - 2);
            return result.toString();
        }

        private List<ComplexMenuReportItem> sortComplex(List<ComplexMenuReportItem> list, List<Object[]> complexList){
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
                        ComplexMenuReportItem complexMenuReportItem = new ComplexMenuReportItem(newOrg, getOrgCount(newOrg, complexList), list.get(s).getComplexList());
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
