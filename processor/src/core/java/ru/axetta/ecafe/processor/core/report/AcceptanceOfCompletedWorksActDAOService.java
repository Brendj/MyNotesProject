/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by anvarov on 20.02.2018.
 */
@Service
@Transactional(readOnly = true)
public class AcceptanceOfCompletedWorksActDAOService extends AbstractDAOService {

    private final static Logger logger = LoggerFactory.getLogger(AcceptanceOfCompletedWorksActDAOService.class);

    public static AcceptanceOfCompletedWorksActDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(AcceptanceOfCompletedWorksActDAOService.class);
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForAct(BasicReportJob.OrgShortItem org, Boolean showAllOrgs, Date startTime, Date endDate) {
        List<AcceptanceOfCompletedWorksActItem> result = new ArrayList<AcceptanceOfCompletedWorksActItem>();

        if (showAllOrgs) {

            List<Long> idOfOrgList = new ArrayList<Long>();
            idOfOrgList.add(org.getIdOfOrg());

            Set<FriendlyOrganizationsInfoModel> andFriendlyOrgsList = OrgUtils.getMainBuildingAndFriendlyOrgsList(getSession(), idOfOrgList);

            for (FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel: andFriendlyOrgsList) {
                result = findByOrgAllItemsForActByOrgs(friendlyOrganizationsInfoModel, startTime, endDate);
            }

        } else {
            result = findByOrgAllItemsForAct(org.getIdOfOrg(), startTime, endDate);
        }

        return result;
    }

    public List<AcceptanceOfCompletedWorksActItem> findByOrgAllItemsForActByOrgs(FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel, Date startTime, Date endDate) {

        List<AcceptanceOfCompletedWorksActItem> result = new ArrayList<AcceptanceOfCompletedWorksActItem>();

        Query query = getSession().createSQLQuery("SELECT contractnumber, dateofconclusion, "
                + "shortnameinfoservice, contragentname, dateOfClosing, officialposition, "
                + " (cfp.surname || ' ' || cfp.firstname || ' ' || cfp.secondname) AS fullname, "
                + " cfp.surname,  cfp.firstname,  cfp.secondname "
                + " FROM cf_contracts cfc LEFT JOIN cf_orgs cfo ON cfc.idofcontract = cfo.idofcontract "
                + " LEFT JOIN CF_Contragents cfco ON cfco.IdOfContragent = cfc.IdOfContragent "
                + " LEFT JOIN cf_persons cfp ON cfp.idofperson = cfo.IdOfOfficialPerson "
                + " WHERE cfo.idoforg = :idOfOrg");
        query.setParameter("idOfOrg", friendlyOrganizationsInfoModel.getIdOfOrg());
        List res = query.list();

        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem = fooBar(res);

        List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDataList = findAllForCrossTabByOrgs(friendlyOrganizationsInfoModel.getFriendlyOrganizationsSet(), startTime, endDate);
        acceptanceOfCompletedWorksActItem.setActCrossTabDataList(actCrossTabDataList);

        if (!res.isEmpty()) {
            result.add(acceptanceOfCompletedWorksActItem);
        }

        return  result;
    }

    public List<AcceptanceOfCompletedWorksActItem> findByOrgAllItemsForAct(Long idOfOrg, Date startTime, Date endDate) {

        List<AcceptanceOfCompletedWorksActItem> result = new ArrayList<AcceptanceOfCompletedWorksActItem>();

        Query query = getSession().createSQLQuery("SELECT contractnumber, dateofconclusion, "
                + "shortnameinfoservice, contragentname, dateOfClosing, officialposition, "
                + " (cfp.surname || ' ' || cfp.firstname || ' ' || cfp.secondname) AS fullname, "
                + " cfp.surname,  cfp.firstname,  cfp.secondname "
                + " FROM cf_contracts cfc LEFT JOIN cf_orgs cfo ON cfc.idofcontract = cfo.idofcontract "
                + " LEFT JOIN CF_Contragents cfco ON cfco.IdOfContragent = cfc.IdOfContragent "
                + " LEFT JOIN cf_persons cfp ON cfp.idofperson = cfo.IdOfOfficialPerson "
                + " WHERE cfo.idoforg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        List res = query.list();

        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem = fooBar(res);

        List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDataList = findAllForCrossTab(idOfOrg, startTime, endDate);
        acceptanceOfCompletedWorksActItem.setActCrossTabDataList(actCrossTabDataList);

        if (!res.isEmpty()) {
            result.add(acceptanceOfCompletedWorksActItem);
        }

        return  result;
    }

    private List<AcceptanceOfCompletedWorksActCrossTabData> findAllForCrossTab(Long idOfOrg, Date startTime, Date endTime) {
        List<AcceptanceOfCompletedWorksActCrossTabData> actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();

        OrderDetailsDAOService service = new OrderDetailsDAOService();
        service.setSession(getSession());

        List<GoodItem> allGoods = service.findAllGoods(idOfOrg, startTime, endTime, service.getReducedPaymentOrderTypesWithDailySample());
        allGoods.addAll(service.findAllGoods(idOfOrg, startTime, endTime, service.getWaterAccountingOrderTypesWithDailySample()));

        if (allGoods.isEmpty()) {
            AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", "0");
            AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", "0");
            actItems.add(actCrossTabDataSc);
            actItems.add(actCrossTabDataCh);
        } else {

            boolean flag  = true;

            for (GoodItem goodItem: allGoods) {
                Long val = service.buildRegisterStampBodyValue(idOfOrg, startTime,  endTime, goodItem.getFullName());
                Long valDaily = service.buildRegisterStampDailySampleValue(idOfOrg, startTime, endTime, goodItem.getFullName());

                AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(), "школа", String.valueOf(val + valDaily));
                AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(), "д/сад", String.valueOf(val + valDaily));
                actItems.add(actCrossTabDataSc);
                actItems.add(actCrossTabDataCh);

                if (goodItem.getOrderType().equals(1)) {

                    flag = false;

                    Long val1 = service.buildRegisterStampBodyValue(idOfOrg, startTime,  endTime, goodItem.getFullName());
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataScW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", String.valueOf(val1));
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataChW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", String.valueOf(val1));
                    actItems.add(actCrossTabDataScW);
                    actItems.add(actCrossTabDataChW);
                }
            }

            if (flag) {
                setWaterValueByFlag(actItems);
            }
        }

        return actItems;
    }

    private List<AcceptanceOfCompletedWorksActCrossTabData> findAllForCrossTabByOrgs(Set<Org> friendlyOrganizationsSet, Date startTime, Date endTime) {

        List<Long> idOfOrgList = new ArrayList<Long>();

        for (Org org: friendlyOrganizationsSet) {
            idOfOrgList.add(org.getIdOfOrg());
        }

        List<AcceptanceOfCompletedWorksActCrossTabData> actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();

        OrderDetailsDAOService service = new OrderDetailsDAOService();
        service.setSession(getSession());

        List<GoodItem> allGoods = service.findAllGoods(idOfOrgList, startTime, endTime, service.getReducedPaymentOrderTypesWithDailySample());
        allGoods.addAll(service.findAllGoods(idOfOrgList, startTime, endTime, service.getWaterAccountingOrderTypesWithDailySample()));

        if (allGoods.isEmpty()) {
            AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", "0");
            AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", "0");
            actItems.add(actCrossTabDataSc);
            actItems.add(actCrossTabDataCh);
        } else {

            boolean flag  = true;

            for (GoodItem goodItem: allGoods) {
                Long val = service.buildRegisterStampBodyValueByOrgList(idOfOrgList, startTime,  endTime, goodItem.getFullName());
                Long valDaily = service.buildRegisterStampDailySampleValueByOrgs(idOfOrgList, startTime, endTime, goodItem.getFullName());

                AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(), "школа", String.valueOf(val + valDaily));
                AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(), "д/сад", String.valueOf(val + valDaily));
                actItems.add(actCrossTabDataSc);
                actItems.add(actCrossTabDataCh);

                if (goodItem.getOrderType().equals(1)) {

                    flag = false;

                    Long val1 = service.buildRegisterStampBodyValueByOrgList(idOfOrgList, startTime,  endTime, goodItem.getFullName());
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataScW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", String.valueOf(val1));
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataChW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", String.valueOf(val1));
                    actItems.add(actCrossTabDataScW);
                    actItems.add(actCrossTabDataChW);
                }
            }

            if (flag) {
                setWaterValueByFlag(actItems);
            }

        }

        return actItems;
    }

    public void setWaterValueByFlag(List<AcceptanceOfCompletedWorksActCrossTabData> actItems) {
        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                "Вода питьевая", "школа", "0");
        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData(
                "Вода питьевая", "д/сад", "0");
        actItems.add(actCrossTabDataSc);
        actItems.add(actCrossTabDataCh);
    }

    public AcceptanceOfCompletedWorksActItem fooBar(List<Object> res) {
        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem = new AcceptanceOfCompletedWorksActItem();

        for (Object o : res) {
            Object[] objList = (Object[]) o;

            acceptanceOfCompletedWorksActItem.setNumberOfContract((String) objList[0]);
            acceptanceOfCompletedWorksActItem
                    .setDateOfConclusion(CalendarUtils.dateShortToStringFullYear((Date) objList[1]) + "г.");
            acceptanceOfCompletedWorksActItem.setShortNameInfoService((String) objList[2]);
            acceptanceOfCompletedWorksActItem.setExecutor((String) objList[3]);
            acceptanceOfCompletedWorksActItem
                    .setDateOfClosing(CalendarUtils.dateShortToStringFullYear((Date) objList[4]) + "г.");
            if (objList[5].equals("")) {
                acceptanceOfCompletedWorksActItem
                        .setOfficialPosition("__________________________________________________________________");
                acceptanceOfCompletedWorksActItem.setFullName("____________");
            } else {
                String offPosPlusFullName = objList[5] + ", " + objList[6];
                acceptanceOfCompletedWorksActItem.setOfficialPosition(offPosPlusFullName);

                String fullName =
                        objList[7] + " " + ((String) objList[8]).substring(0, 1) + "." + ((String) objList[9])
                                .substring(0, 1) + ".";
                acceptanceOfCompletedWorksActItem.setFullName(fullName);
            }
        }

        return acceptanceOfCompletedWorksActItem;
    }
}
