package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.msc.StatisticsPaymentPreferentialSupplyJasperReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.01.14
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesDataOnOrdersAndPaymentBuilder extends BasicReportForContragentJob.Builder{

    private final String templateFilename;

    public DiscrepanciesDataOnOrdersAndPaymentBuilder() {
        this(null);
    }

    public DiscrepanciesDataOnOrdersAndPaymentBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if(StringUtils.isEmpty(this.templateFilename)) throw new Exception("templateFilename not found");
        String idOfContragent1 = getReportProperties().getProperty(BasicReportForContragentJob.PARAM_CONTRAGENT_RECEIVER_ID); // ищем контргентов ТСП
        String idOfContragent = getReportProperties().getProperty("idOfContragent"); // ищем контргентов ТСП
        String idOfOrgs = getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG); // ищем организации
        Long validId = null;
        Contragent contragent = null;
        if (StringUtils.trimToNull(idOfContragent) != null) {
            try {
                validId = Long.parseLong(idOfContragent);
            } catch (NumberFormatException e) {
                throw new Exception("Ошибка парсинга идентификатора контрагента: " + idOfContragent, e);
            }
            contragent = (Contragent) session.get(Contragent.class, validId);
            if (contragent == null) {
                throw new Exception("Контрагент не найден: " + idOfContragent);
            }
        } else {
            throw new Exception("Контрагент не найден ");
        }

        List<Long> idOfOrg = new ArrayList<Long>();

        if (StringUtils.trimToNull(idOfOrgs)!=null) {
            String[] arr = StringUtils.split(idOfOrgs, ",");
            for (String v: arr){
                String vv = StringUtils.trimToNull(v);
                if(vv!=null){
                    Long validIdOrg = null;
                    try {
                        validIdOrg = Long.parseLong(v);
                    } catch (NumberFormatException e) {
                        throw new Exception("Ошибка парсинга идентификатора организации: " + idOfContragent, e);
                    }
                    Long id = DAOUtils.getIdOfOrg(session, validIdOrg);
                    if (id == null) {
                        throw new Exception("Организация не найдена: " + validId);
                    }
                    idOfOrg.add(id);
                }
            }
        }

        Date generateBeginTime = new Date();
        Map<String, Object> parameterMap = new HashMap<String, Object>();

        DiscrepanciesDataOnOrdersAndPaymentReport report = build(session, contragent, idOfOrg, calendar, startTime, endTime);
        JRDataSource dataSource = new JRBeanCollectionDataSource(report.getDiscrepanciesDataOnOrdersAndPaymentItems());

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new StatisticsPaymentPreferentialSupplyJasperReport(generateBeginTime, generateDuration,
                jasperPrint, startTime, endTime, contragent.getIdOfContragent());
    }

    public DiscrepanciesDataOnOrdersAndPaymentReport build(Session session, Contragent contragent, List<Long> idOfOrgs,
            Calendar calendar, Date startTime, Date endTime) throws  Exception{
        Date generateTime = new Date();
        List<DiscrepanciesDataOnOrdersAndPaymentItem> items = new LinkedList<DiscrepanciesDataOnOrdersAndPaymentItem>();

        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("defaultSupplier", contragent));
        /* если введены список организаций фильтруеми только по ним */
        if(!(idOfOrgs==null || idOfOrgs.isEmpty() || idOfOrgs.get(0)==null)) {
            criteria.add(Restrictions.in("idOfOrg", idOfOrgs));
        }
        criteria.createCriteria("categoriesInternal","cat");
        //criteria.setProjection(Projections.projectionList()
        //        .add(Property.forName("idOfOrg"))
        //        .add(Property.forName("district"))
        //        .add(Property.forName("shortName"))
        //        .add(Property.forName("address"))
        //        .add(Property.forName("type"))
        //        .add(Property.forName("cat.categoryName"))
        //);
        criteria.setProjection(Property.forName("idOfOrg"));

        List orgList = criteria.list();
        for (Object obj: orgList){
            Long idOfOrg = (Long) obj;
            Org org = (Org) session.load(Org.class, idOfOrg);
            //Object[] row = (Object[]) obj;
            //Long idOfOrg  = Long.valueOf(row[0].toString());
            //String district  = row[1].toString();
            //String shortName  = row[2].toString();
            //String address  = row[3].toString();
            //String type = row[4].toString();
            //String categoryName = row[4].toString();
            String shortName  = org.getShortName();
            String address  = org.getAddress();
            OrganizationType type = org.getType();
            Set<CategoryOrg> categories = org.getCategories();
            calendar.setTime(startTime);
            while (endTime.getTime()>calendar.getTimeInMillis()){
                final Date time = calendar.getTime();
                /* Подсчет количества товаров без учета корректировки */
                Criteria allOrdersCountCriteria = session.createCriteria(OrderDetail.class);
                allOrdersCountCriteria.createCriteria("order", "ord");
                allOrdersCountCriteria.add(Restrictions.ne("ord.orderType", OrderTypeEnumType.CORRECTION_TYPE));
                allOrdersCountCriteria.add(Restrictions.between("ord.createTime", startTime, time));
                allOrdersCountCriteria.add(Restrictions.eq("compositeIdOfOrderDetail.idOfOrg", idOfOrg));
                allOrdersCountCriteria.setProjection(Projections.projectionList().add(Projections.sum("qty")));
                allOrdersCountCriteria.setMaxResults(0);
                Long allOrdersCount = (Long) allOrdersCountCriteria.uniqueResult();

                /* Подсчет количества товаров с учетом корректировки */
                Criteria actualPresenceCriteria = session.createCriteria(OrderDetail.class);
                actualPresenceCriteria.createCriteria("order", "ord");
                actualPresenceCriteria.add(Restrictions.between("ord.createTime", startTime, time));
                actualPresenceCriteria.add(Restrictions.eq("compositeIdOfOrderDetail.idOfOrg", idOfOrg));
                actualPresenceCriteria.setProjection(Projections.projectionList().add(Projections.sum("qty")));
                actualPresenceCriteria.setMaxResults(0);
                Long actualPresenceCount = (Long) actualPresenceCriteria.uniqueResult();

                //StatisticsPaymentPreferentialSupplyItem item = new StatisticsPaymentPreferentialSupplyItem();
                //item.setPaymentDate(time);
                //item.setActualPresenceCount(actualPresenceCount);
                //item.setOrderedCount(allOrdersCount);
                //item.setDistrict(district);
                //item.setAddress(address);
                //item.setShortName(shortName);
                //item.setType(type);
                //item.setNumber(Org.extractOrgNumberFromName(shortName));
                //
                //statistics.add(item);

                calendar.add(Calendar.DATE,1);
            }
        }

        final long generateDuration = new Date().getTime() - generateTime.getTime();
        return new DiscrepanciesDataOnOrdersAndPaymentReport(generateTime, generateDuration,items);
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(DiscrepanciesDataOnOrdersAndPaymentBuilder.class);


}
