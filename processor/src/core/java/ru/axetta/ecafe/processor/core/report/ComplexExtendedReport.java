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
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            JRDataSource dataSource = createDataSource(session, Long.valueOf(idOfComplex));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        private JRDataSource createDataSource(Session session, Long idOfComplex) throws Exception {
            List<ComplexExtendedItem> result = new LinkedList<ComplexExtendedItem>();
            List<Long> idOfcomplexItem = new ArrayList<>();
            Criteria criteria = session.createCriteria(WtComplex.class);
            criteria.add(Restrictions.eq("idOfComplex", idOfComplex));
            criteria.add(Restrictions.eq("deleteState", 0));
            List<WtComplex> complexList = criteria.list();
            WtComplex complex = complexList.get(0);
            String getComplexItems = "select c.cycle_day, c.idofcomplexitem from cf_wt_complexes_items c where c.idofcomplex = :idOfComplex ";
            Query queryDish = session.createSQLQuery(getComplexItems);
            queryDish.setParameter("idOfComplex", idOfComplex);
            List<Object[]> dishList = queryDish.list();
            for(Object[] id: dishList)
                idOfcomplexItem.add(Long.valueOf(id[1].toString()));
            String getDish;
            if(idOfcomplexItem.size() > 0)
                getDish = "select cid.idofcomplexitem, cid.idOfDish from cf_wt_complex_items_dish cid where cid.idofcomplexitem in (:idOfcomplexItem) ";

            List<String> ll = new ArrayList<>();
            for(Object[] day: dishList) {
                result.add(new ComplexExtendedItem("jyy", complex.getName(), complex.getWtDietType().getDescription(),
                        complex.getWtAgeGroupItem().getDescription(), complex.getWtComplexGroupItem().getDescription(),
                        complex.getIsPortal().toString(), complex.getBarcode(),
                        complex.getBeginDate().toString() + " - " + complex.getEndDate(), complex.getDayInCycle().toString(), complex.getCycleMotion().toString(), "trt", "erge",
                        "erhe", day[0].toString(), ll));
            }

            return new JRBeanCollectionDataSource(result);
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
