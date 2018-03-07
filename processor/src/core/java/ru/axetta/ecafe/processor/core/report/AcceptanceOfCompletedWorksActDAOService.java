/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem1;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.MoneyInWords;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

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

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForAct(BasicReportJob.OrgShortItem org,
            Boolean showAllOrgs, Date startTime, Date endDate) {
        List<AcceptanceOfCompletedWorksActItem> result = new ArrayList<AcceptanceOfCompletedWorksActItem>();

        if (showAllOrgs) {

            List<Long> idOfOrgList = new ArrayList<Long>();
            idOfOrgList.add(org.getIdOfOrg());

            Set<FriendlyOrganizationsInfoModel> andFriendlyOrgsList = OrgUtils
                    .getMainBuildingAndFriendlyOrgsList(getSession(), idOfOrgList);

            for (FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel : andFriendlyOrgsList) {
                result = findAllItemsForActByOrgs(friendlyOrganizationsInfoModel, startTime, endDate);
            }

        } else {
            result = findAllItemsForActByOrg(org.getIdOfOrg(), startTime, endDate);
        }

        return result;
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForActByOrgs(
            FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel, Date startTime, Date endDate) {

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

        SumPriceAndCrossTabItems sumPriceAndCrossTabItems = findAllForCrossTabByOrgs(
                friendlyOrganizationsInfoModel.getFriendlyOrganizationsSet(), startTime, endDate);
        acceptanceOfCompletedWorksActItem.setActCrossTabDataList(sumPriceAndCrossTabItems.actCrossTabDatas);

        Long priceWhole = sumPriceAndCrossTabItems.getSumPrice() / 100;
        Long priceCoin = Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100);

        String sumStr = priceWhole + " руб. " + priceCoin + " копеек";

        String sum = (String.format("%d.%02d", sumPriceAndCrossTabItems.getSumPrice() / 100,
                Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100)));

        double sumInDouble = Double.parseDouble(sum);

        sumStr = sumStr + " (" + MoneyInWords.inwords(sumInDouble) + ")";

        acceptanceOfCompletedWorksActItem.setSum(sumStr);

        if (!res.isEmpty()) {
            result.add(acceptanceOfCompletedWorksActItem);
        }

        return result;
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForActByOrg(Long idOfOrg, Date startTime, Date endDate) {

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

        SumPriceAndCrossTabItems sumPriceAndCrossTabItems = findAllForCrossTabByOrg(idOfOrg, startTime, endDate);
        acceptanceOfCompletedWorksActItem.setActCrossTabDataList(sumPriceAndCrossTabItems.actCrossTabDatas);

        Long priceWhole = sumPriceAndCrossTabItems.getSumPrice() / 100;
        Long priceCoin = Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100);

        String sumStr = priceWhole + " руб. " + priceCoin + " копеек";

        String sum = (String.format("%d.%02d", sumPriceAndCrossTabItems.getSumPrice() / 100,
                Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100)));

        double sumInDouble = Double.parseDouble(sum);

        sumStr = sumStr + " (" + MoneyInWords.inwords(sumInDouble) + ")";

        acceptanceOfCompletedWorksActItem.setSum(sumStr);

        if (!res.isEmpty()) {
            result.add(acceptanceOfCompletedWorksActItem);
        }

        return result;
    }

    private SumPriceAndCrossTabItems findAllForCrossTabByOrg(Long idOfOrg, Date startTime, Date endTime) {
        List<AcceptanceOfCompletedWorksActCrossTabData> actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();
        Long sumPrice = 0L;

        OrderDetailsDAOService service = new OrderDetailsDAOService();
        service.setSession(getSession());

        List<GoodItem1> allGoods = service.findAllGoodsByOrderTypesByOrg(idOfOrg, startTime, endTime,
                service.getPayPlanAndSubscriptionFeedingOrderTypes());
        //allGoods.addAll(service.findAllGoods(idOfOrg, startTime, endTime, service.getWaterAccountingOrderTypesWithDailySample()));

/*        HashMap<String, GoodItem1> goodItem1HashMap = new HashMap<String, GoodItem1>();

        for (GoodItem1 goodItem1: allGoods) {
            if (!goodItem1HashMap.containsKey(goodItem1.getFullName())) {
                goodItem1HashMap.put(goodItem1.getFullName(), goodItem1);
            } else {
                GoodItem1 gg = goodItem1HashMap.get(goodItem1.getFullName());
                gg.setPriceLong(gg.getPrice() + goodItem1.getPrice());
            }
        }*/

        if (allGoods.isEmpty()) {
            /*AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", "0");
            AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", "0");
            actItems.add(actCrossTabDataSc);
            actItems.add(actCrossTabDataCh);*/
        } else {

            //boolean flag  = true;

            for (GoodItem1 goodItem : allGoods) {

                Long qty = service.buildRegisterStampBodyValueByOrderTypesByOrg(idOfOrg, startTime, endTime,
                        goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                        goodItem.getPathPart4(), goodItem.getPathPart1(),
                        (String.format("%d.%02d", goodItem.getPrice() * qty / 100,
                                Math.abs(goodItem.getPrice() * qty % 100))));
                //AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(), "д/сад", String.valueOf(sumQtyAndPriceItem.getSumQty()));
                actItems.add(actCrossTabData);
                //actItems.add(actCrossTabDataCh);

                sumPrice += goodItem.getPrice() * qty;

               /* if (goodItem.getOrderType().equals(1)) {

                    //flag = false;

                    SumQtyAndPriceItem sumQtyAndPriceItem1 = service.buildRegisterStampBodyValue(idOfOrg, startTime,  endTime, goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                    sumPrice += sumQtyAndPriceItem.getSumPrice();

                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataScW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", String.valueOf(sumQtyAndPriceItem1.getSumQty()));
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataChW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", String.valueOf(sumQtyAndPriceItem1.getSumQty()));
                    actItems.add(actCrossTabDataScW);
                    actItems.add(actCrossTabDataChW);
                }*/
            }

           /* if (flag) {
                setWaterValueByFlag(actItems);
            }*/
        }

        return new SumPriceAndCrossTabItems(actItems, sumPrice);
    }

    private SumPriceAndCrossTabItems findAllForCrossTabByOrgs(Set<Org> friendlyOrganizationsSet, Date startTime,
            Date endTime) {

        List<Long> idOfOrgList = new ArrayList<Long>();

        for (Org org : friendlyOrganizationsSet) {
            idOfOrgList.add(org.getIdOfOrg());
        }

        List<AcceptanceOfCompletedWorksActCrossTabData> actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();

        Long sumPrice = 0L;

        OrderDetailsDAOService service = new OrderDetailsDAOService();
        service.setSession(getSession());

        List<GoodItem1> allGoods = service.findAllGoodsByOrderTypesByOrgs(idOfOrgList, startTime, endTime,
                service.getPayPlanAndSubscriptionFeedingOrderTypes());
        //allGoods.addAll(service.findAllGoods(idOfOrgList, startTime, endTime, service.getWaterAccountingOrderTypesWithDailySample()));

        if (allGoods.isEmpty()) {
            /*AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", "0");
            AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", "0");
            actItems.add(actCrossTabDataSc);
            actItems.add(actCrossTabDataCh);*/
        } else {

            // boolean flag  = true;

            for (GoodItem1 goodItem : allGoods) {

                Long qty = service.buildRegisterStampBodyValueByOrderTypesByOrgs(idOfOrgList, startTime, endTime,
                        goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                        goodItem.getPathPart4(), goodItem.getPathPart1(),
                        (String.format("%d.%02d", goodItem.getPrice() * qty / 100,
                                Math.abs(goodItem.getPrice() * qty % 100))));
                actItems.add(actCrossTabData);

                sumPrice += goodItem.getPrice() * qty;

                /*if (goodItem.getOrderType().equals(1)) {

                    //flag = false;

                    SumQtyAndPriceItem sumQtyAndPriceItem1 = service.buildRegisterStampBodyValueByOrgList(idOfOrgList, startTime,  endTime, goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataScW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "школа", String.valueOf(sumQtyAndPriceItem1.getSumQty()));
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataChW = new AcceptanceOfCompletedWorksActCrossTabData("Вода питьевая", "д/сад", String.valueOf(sumQtyAndPriceItem1.getSumQty()));
                    actItems.add(actCrossTabDataScW);
                    actItems.add(actCrossTabDataChW);
                }*/
            }

            /*if (flag) {
                setWaterValueByFlag(actItems);
            }*/

        }

        return new SumPriceAndCrossTabItems(actItems, sumPrice);
    }

/*    public void setWaterValueByFlag(List<AcceptanceOfCompletedWorksActCrossTabData> actItems) {
        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                "Вода питьевая", "школа", "0");
        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataCh = new AcceptanceOfCompletedWorksActCrossTabData(
                "Вода питьевая", "д/сад", "0");
        actItems.add(actCrossTabDataSc);
        actItems.add(actCrossTabDataCh);
    }*/

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

                String fullName = objList[7] + " " + ((String) objList[8]).substring(0, 1) + "." + ((String) objList[9])
                        .substring(0, 1) + ".";
                acceptanceOfCompletedWorksActItem.setFullName(fullName);
            }
        }

        return acceptanceOfCompletedWorksActItem;
    }

    public class SumPriceAndCrossTabItems {

        private List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDatas;

        private Long sumPrice;

        public SumPriceAndCrossTabItems() {
        }

        public SumPriceAndCrossTabItems(List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDatas,
                Long sumPrice) {
            this.actCrossTabDatas = actCrossTabDatas;
            this.sumPrice = sumPrice;
        }

        public List<AcceptanceOfCompletedWorksActCrossTabData> getActCrossTabDatas() {
            return actCrossTabDatas;
        }

        public void setActCrossTabDatas(List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDatas) {
            this.actCrossTabDatas = actCrossTabDatas;
        }

        public Long getSumPrice() {
            return sumPrice;
        }

        public void setSumPrice(Long sumPrice) {
            this.sumPrice = sumPrice;
        }
    }
}
