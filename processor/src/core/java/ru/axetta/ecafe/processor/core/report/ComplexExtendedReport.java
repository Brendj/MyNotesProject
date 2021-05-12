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
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplexExcludeDays;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class ComplexExtendedReport extends BasicReportForContragentJob {

    private Logger logger = LoggerFactory.getLogger(ComplexExtendedReport.class);


    public static class Builder extends BasicReportForContragentJob.Builder {
        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance()
                    .getAutoReportGenerator().getReportsTemplateFilePath()
                    + ComplexExtendedReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.startOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            String idOfComplex = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfComplex"));
            JRDataSource dataSource = new JRBeanCollectionDataSource(createDataSource(session, Long.valueOf(idOfComplex)));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<ComplexExtendedItem> createDataSource(Session session, Long idOfComplex) throws Exception {
            List<ComplexExtendedItem> result = new LinkedList<ComplexExtendedItem>();
            Set<Integer> days = new TreeSet<>();
            Criteria criteria = session.createCriteria(WtComplex.class);
            criteria.add(Restrictions.eq("idOfComplex", idOfComplex));
            criteria.add(Restrictions.eq("deleteState", 0));
            List<WtComplex> complexList = criteria.list();
            WtComplex complex = complexList.get(0);

            Criteria wtComplexExcludeDays = session.createCriteria(WtComplexExcludeDays.class);
            wtComplexExcludeDays.add(Restrictions.eq("deleteState", 0));
            wtComplexExcludeDays.add(Restrictions.eq("complex", complex));
            List<WtComplexExcludeDays> complexExcludeDays = wtComplexExcludeDays.list();

            String getDish = " select wci.cycle_day, wd.dishname, wd.price, wd.componentsofdish, c.description, ci.description as subcategory, "
                    + "wd.calories, wd.protein, wd.fat, wd.carbohydrates, wd.code, wd.qty, wd.dateofbeginmenuincluding, wd.dateofendmenuincluding, wd.idofdish, ci.deletestate "
                    + "from cf_wt_complexes_items wci "
                    + "join cf_wt_complex_items_dish cid on wci.idofcomplexitem = cid.idofcomplexitem "
                    + "join cf_wt_dishes wd on cid.idofdish = wd.idofdish "
                    + "join cf_wt_categories c on wd.idofcategory = c.idofcategory "
                    + "left join cf_wt_dish_categoryitem_relationships r on cid.idofdish = r.idofdish "
                    + "left join cf_wt_category_items ci on r.idofcategoryitem = ci.idofcategoryitem "
                    + "where wd.deletestate = 0 and c.deletestate = 0 "
                    + "and wci.idofcomplex = :idofcomplex "
                    + "order by 1";

            Query query = session.createSQLQuery(getDish);
            if(complex.getIdOfComplex() != null)
                query.setParameter("idofcomplex", complex.getIdOfComplex());
            List<Object[]> dishData = query.list();
            if (CollectionUtils.isEmpty(dishData))
                throw new Exception("Нет данных для построения отчета");
            for(Object[] day: dishData)
                days.add(Integer.parseInt(day[0].toString()));

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            String complexDate = formatter.format(complex.getBeginDate()) + " - " + formatter.format(complex.getEndDate());
            StringBuilder passDay = new StringBuilder("Нет");
            if (complexExcludeDays.size() > 0) {
                passDay = new StringBuilder("");
                for (WtComplexExcludeDays excludeDays : complexExcludeDays) {
                    passDay.append(formatter.format(excludeDays.getDate())).append(", ");
                }
                if (passDay.length() > 2)
                    passDay = new StringBuilder(passDay.substring(0, passDay.length() - 2));
            }

            for(Integer day: days) {
                result.add(new ComplexExtendedItem(complex.getContragent().getContragentName(), complex.getName(), complex.getWtDietType().getDescription(),
                        complex.getWtAgeGroupItem().getDescription(), complex.getWtComplexGroupItem().getDescription(),
                        complex.getIsPortal() ? "Да" : "Нет", complex.getBarcode(), complexDate, complex.getDayInCycle().toString(),
                        complex.getCycleMotion().toString(), complex.getStartCycleDay().toString(), passDay.toString(),
                        complex.getComment() == null ? "" : complex.getComment(), day.toString(), getDishList(dishData, day.toString())));
            }

            return result;
        }

        private List<ComplexExtendedDishItem> getDishList(List<Object[]> dishData, String day){
            List<ComplexExtendedDishItem> list = new ArrayList<>();
            for (Object[] dish: dishData){
                    if (day.equals(dish[0].toString())) {
                        String dishName = dish[1].toString();
                        String price = dish[2].toString();
                        String structure = dish[3].toString();
                        String category = dish[4] == null ? "" : dish[4].toString();
                        String subCategory = "";
                        if (dish[15] != null && dish[15].toString().equals("0"))
                            subCategory = dish[5] == null ? "" : dish[5].toString();
                        String kbju = dish[6].toString() + "/" + dish[7].toString()+ "/" +dish[8].toString() + "/" +dish[9].toString();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                        String code = dish[10].toString();
                        String weight = dish[11].toString();
                        String beginDate = dish[12] == null ? "" : formatter.format(dish[12]);
                        String endDate = dish[13] == null ? "" : formatter.format(dish[13]);
                        String idOfDish = dish[14].toString();

                        list.add(new ComplexExtendedDishItem(dishName, price, structure,
                                category, subCategory, kbju, code, weight,
                                beginDate, endDate, idOfDish));
                    }
            }
            return list;
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
        return new ContragentPreordersReport();
    }

    @Override
    public ContragentPreordersReport.Builder createBuilder(String templateFilename) {
        return new ContragentPreordersReport.Builder(templateFilename);
    }

}
