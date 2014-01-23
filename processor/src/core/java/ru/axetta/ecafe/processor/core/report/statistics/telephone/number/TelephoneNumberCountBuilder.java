/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.telephone.number;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.msc.TelephoneNumberCountJasperReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.criterion.Order;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LocaleType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class TelephoneNumberCountBuilder extends BasicReportForContragentJob.Builder {

    private final String templateFilename;

    public TelephoneNumberCountBuilder() {
        this(null);
    }

    public TelephoneNumberCountBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if(StringUtils.isEmpty(this.templateFilename)) throw new Exception("templateFilename not found");
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

        TelephoneNumberCountReport report = build(session, contragent, idOfOrg);
        JRDataSource dataSource = new JRBeanCollectionDataSource(report.getTelephoneNumberCountItems());

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new TelephoneNumberCountJasperReport(generateBeginTime, generateDuration,
                jasperPrint, startTime, endTime, contragent.getIdOfContragent());
    }

    public TelephoneNumberCountReport build(Session session, Contragent contragent, List<Long> idOfOrgs) throws  Exception{
        Date generateTime = new Date();
        List<TelephoneNumberCountItem> statistics = new LinkedList<TelephoneNumberCountItem>();

        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("defaultSupplier", contragent));
        /* если введены список организаций фильтруеми только по ним */
        if(!(idOfOrgs==null || idOfOrgs.isEmpty() || idOfOrgs.get(0)==null)) {
            criteria.add(Restrictions.in("idOfOrg", idOfOrgs));
        }
        criteria.add(Restrictions.eq("type", OrganizationType.SCHOOL));
        criteria.setProjection(
                Projections.projectionList().add(Property.forName("idOfOrg")).add(Property.forName("district"))
                        .add(Property.forName("shortName")));
        List orgList = criteria.list();
        for (Object obj: orgList){
            Object[] orgRow = (Object[]) obj;
            Long idOfOrg  = Long.valueOf(orgRow[0].toString());
            String district  = orgRow[1].toString();
            String shortName  = orgRow[2].toString();
            Criteria groupCriteria = session.createCriteria(ClientGroup.class);
            groupCriteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrg));
            groupCriteria.add(Restrictions.not(Restrictions.in("groupName",ClientGroup.predefinedGroupNames())));
            groupCriteria.add(Restrictions.lt("compositeIdOfClientGroup.idOfClientGroup", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES));
            //groupCriteria.setProjection(Projections.property("compositeIdOfClientGroup.idOfClientGroup"));
            List<ClientGroup> clientGroups = groupCriteria.list();
            //HashMap<Long, String> groupMap = new HashMap<Long, String>(clientGroups.size());
            //for (ClientGroup clientGroup: clientGroups){
            //    groupMap.put(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName());
            //}

            //Criteria clientGrTelCriteria = session.createCriteria(Client.class);
            //clientGrTelCriteria.add(Restrictions.in("idOfClientGroup",groupMap.keySet()));
            //clientGrTelCriteria.createAlias("org","o").add(Restrictions.eq("o.idOfOrg", idOfOrg));
            //clientGrTelCriteria.createAlias("clientGroup","cg");
            //clientGrTelCriteria.add(Restrictions.eq("clientGroup",clientGroups));
            //clientGrTelCriteria.add(Restrictions.isNotNull("mobile"));
            //clientGrTelCriteria.add(Restrictions.ne("mobile",""));
            //clientGrTelCriteria.addOrder(Order.asc("idOfClientGroup"));
            //clientGrTelCriteria.setProjection(Projections.projectionList().add(
            //        Projections.sqlGroupProjection(
            //                "sum(case when {alias}.mobile is not null and {alias}.mobile<>'' then 1 else 0 end) as hasMobile",
            //                "{alias}.mobile", new String[]{"hasMobile"}, new Type[]{new LongType()}))
            //        .add(Projections.sum("notifyViaSMS"))
            //        .add(Projections.property("idOfClientGroup"))
            //        .add(Projections.groupProperty("idOfClientGroup"))
            //        .add(Projections.rowCount()));
            //
            //List list = clientGrTelCriteria.list();
            //for (Object o: list){
            //    Object[] row = (Object[]) o;
            //    Long countActiveTelephone  = Long.valueOf(row[0].toString());
            //    Long countTelephone  = Long.valueOf(row[1].toString());
            //    Long groupId =  Long.valueOf(row[2].toString());
            //    TelephoneNumberCountItem item = new TelephoneNumberCountItem();
            //    item.setDistrict(district);
            //    item.setShortName(shortName);
            //    item.setCountActiveTelephone(countActiveTelephone);
            //    item.setCountTelephone(countTelephone);
            //    item.setGroup(groupMap.get(groupId));
            //    statistics.add(item);
            //}
            //LOGGER.info("Size: "+list.size());


            for (ClientGroup clientGroup: clientGroups){
                Criteria clientTelephoneCriteria = session.createCriteria(Client.class);
                clientTelephoneCriteria.add(Restrictions.eq("clientGroup",clientGroup));
                clientTelephoneCriteria.add(Restrictions.isNotNull("mobile"));
                clientTelephoneCriteria.add(Restrictions.ne("mobile",""));
                clientTelephoneCriteria.setProjection(Projections.rowCount());
                //clientTelephoneCriteria.setProjection(Projections.projectionList()
                //        .add(Projections.sum("notifyViaSMS"))
                //        .add(Projections.rowCount())
                //);

                //Object result = clientTelephoneCriteria.uniqueResult();
                //Object[] cols = (Object[]) result;
                Long countTelephone = (Long) clientTelephoneCriteria.uniqueResult();

                Criteria clientActiveTelephoneCriteria = session.createCriteria(Client.class);
                clientTelephoneCriteria.add(Restrictions.eq("clientGroup",clientGroup));
                clientTelephoneCriteria.add(Restrictions.isNotNull("mobile"));
                clientTelephoneCriteria.add(Restrictions.ne("mobile",""));
                clientTelephoneCriteria.add(Restrictions.eq("notifyViaSMS", true));
                clientTelephoneCriteria.setProjection(Projections.rowCount());
                Long countActiveTelephone = (Long) clientTelephoneCriteria.uniqueResult();

                TelephoneNumberCountItem item = new TelephoneNumberCountItem();
                item.setDistrict(district);
                item.setShortName(shortName);
                item.setCountActiveTelephone(countActiveTelephone);
                item.setCountTelephone(countTelephone);
                //item.setCountActiveTelephone((Long) cols[0]);
                //item.setCountTelephone((Long) cols[1]);
                item.setGroup(clientGroup.getGroupName());

                statistics.add(item);

            }

        }

        final long generateDuration = new Date().getTime() - generateTime.getTime();
        return new TelephoneNumberCountReport(generateTime, generateDuration, statistics);
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(TelephoneNumberCountBuilder.class);

}
