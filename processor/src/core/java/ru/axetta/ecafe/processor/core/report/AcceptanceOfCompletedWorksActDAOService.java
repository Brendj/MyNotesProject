/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem1;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItemAct;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.MoneyInWords;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by anvarov on 20.02.2018.
 */
@Service
@Transactional(readOnly = true)
public class AcceptanceOfCompletedWorksActDAOService extends AbstractDAOService {

    private final static Logger logger = LoggerFactory.getLogger(AcceptanceOfCompletedWorksActDAOService.class);

    private String[] goodNamesReduce = {"Второй завтрак", "Завтрак 1", "Завтрак 2", "Обед", "Полдник", "Вода питьевая"};

    public static AcceptanceOfCompletedWorksActDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(AcceptanceOfCompletedWorksActDAOService.class);
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForAct(BasicReportJob.OrgShortItem org,
            Boolean showAllOrgs, Date startTime, Date endDate, String type) {
        List<AcceptanceOfCompletedWorksActItem> result = new ArrayList<AcceptanceOfCompletedWorksActItem>();

        if (showAllOrgs) {

            List<Long> idOfOrgList = new ArrayList<Long>();
            idOfOrgList.add(org.getIdOfOrg());

            Set<FriendlyOrganizationsInfoModel> andFriendlyOrgsList = OrgUtils
                    .getMainBuildingAndFriendlyOrgsList(getSession(), idOfOrgList);

            for (FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel : andFriendlyOrgsList) {
                result = findAllItemsForActByOrgs(friendlyOrganizationsInfoModel, startTime, endDate, type);
            }
        } else {
            result = findAllItemsForActByOrg(org.getIdOfOrg(), startTime, endDate, type);
        }

        return result;
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForActByOrg(Long idOfOrg, Date startTime, Date endDate,
            String type) {

        Org org = (Org) getSession().load(Org.class, idOfOrg);

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

        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem;

        if (!res.isEmpty()) {
            acceptanceOfCompletedWorksActItem = fooBar(res);
        } else {
            acceptanceOfCompletedWorksActItem = emptyBar(org);
        }

        SumPriceAndCrossTabItems sumPriceAndCrossTabItems = findAllForCrossTabByOrg(idOfOrg, startTime, endDate, type);
        acceptanceOfCompletedWorksActItem.setActCrossTabDataList(sumPriceAndCrossTabItems.actCrossTabDatas);

        Long priceWhole = sumPriceAndCrossTabItems.getSumPrice() / 100;
        Long priceCoin = Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100);

        String sumStr = priceWhole + " руб. " + priceCoin + " копеек";

        String sum = (String.format("%d.%02d", sumPriceAndCrossTabItems.getSumPrice() / 100,
                Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100)));

        double sumInDouble = Double.parseDouble(sum);

        sumStr = sumStr + " (" + MoneyInWords.inwords(sumInDouble) + ")";

        acceptanceOfCompletedWorksActItem.setSum(sumStr);

        result.add(acceptanceOfCompletedWorksActItem);

        return result;
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForActByOrgs(
            FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel, Date startTime, Date endDate, String type) {

        Org org = null;
        for (Org org1 : friendlyOrganizationsInfoModel.getFriendlyOrganizationsSet()) {
            if (org1.isMainBuilding()) {
                org = org1;
            } else {
                org = (Org) getSession().load(Org.class, friendlyOrganizationsInfoModel.getIdOfOrg());
            }
        }

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

        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem;

        if (!res.isEmpty()) {
            acceptanceOfCompletedWorksActItem = fooBar(res);
        } else {
            acceptanceOfCompletedWorksActItem = emptyBar(org);
        }

        SumPriceAndCrossTabItems sumPriceAndCrossTabItems = findAllForCrossTabByOrgs(
                friendlyOrganizationsInfoModel.getFriendlyOrganizationsSet(), startTime, endDate, type);
        acceptanceOfCompletedWorksActItem.setActCrossTabDataList(sumPriceAndCrossTabItems.actCrossTabDatas);

        Long priceWhole = sumPriceAndCrossTabItems.getSumPrice() / 100;
        Long priceCoin = Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100);

        String sumStr = priceWhole + " руб. " + priceCoin + " копеек";

        String sum = (String.format("%d.%02d", sumPriceAndCrossTabItems.getSumPrice() / 100,
                Math.abs(sumPriceAndCrossTabItems.getSumPrice() % 100)));

        double sumInDouble = Double.parseDouble(sum);

        sumStr = sumStr + " (" + MoneyInWords.inwords(sumInDouble) + ")";

        acceptanceOfCompletedWorksActItem.setSum(sumStr);

        result.add(acceptanceOfCompletedWorksActItem);

        return result;
    }

    private SumPriceAndCrossTabItems findAllForCrossTabByOrg(Long idOfOrg, Date startTime, Date endTime, String type) {
        List<AcceptanceOfCompletedWorksActCrossTabData> actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();
        Long sumPrice = 0L;

        if (type.equals("Льготное питание")) {
            AcceptanceOfCompletedWorksActReducePricePlanService service = new AcceptanceOfCompletedWorksActReducePricePlanService();
            service.setSession(getSession());

            List<GoodItemAct> allGoods = service
                    .findAllGoodsByTypesByOrg(idOfOrg, startTime, endTime, service.getReducedPricePlanOrderTypes());

            Set<GoodItemAct> allGoodsNon = new TreeSet<GoodItemAct>();
            for (GoodItemAct goodItemAct : allGoods) {
                allGoodsNon.add(goodItemAct);
            }

            Org org = (Org) getSession().load(Org.class, idOfOrg);

            if (allGoodsNon.isEmpty()) {
                for (String name : goodNamesReduce) {
                    if (org.getType().equals(OrganizationType.SCHOOL)) {
                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                                name, "Школа", "0");
                        actItems.add(actCrossTabDataSc);
                    } else if (org.getType().equals(OrganizationType.KINDERGARTEN)) {
                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataDs = new AcceptanceOfCompletedWorksActCrossTabData(
                                name, "Детский сад", "0");
                        actItems.add(actCrossTabDataDs);
                    }
                }
            } else {
                for (GoodItemAct goodItem : allGoodsNon) {
                    SumQtyAndPriceItem sumQtyAndPriceItem = service
                            .buildRegisterStampBodyValueByOrg(idOfOrg, startTime, endTime, goodItem.getFullName(),
                                    service.getReducedPricePlanOrderTypes());

                    sumPrice += sumQtyAndPriceItem.getSumPrice();

                    if (goodItem.getOrderType().equals(OrderTypeEnumType.WATER_ACCOUNTING)) {
                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                goodItem.getPathPart3(), goodItem.getPathPart1(),
                                sumQtyAndPriceItem.getSumQty().toString());
                        actItems.add(actCrossTabData);
                    } else {
                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                goodItem.getPathPart4(), goodItem.getPathPart1(),
                                sumQtyAndPriceItem.getSumQty().toString());
                        actItems.add(actCrossTabData);
                    }
                }

                Map<String, AcceptanceOfCompletedWorksActCrossTabData> actItemsSumMap = new HashMap<String, AcceptanceOfCompletedWorksActCrossTabData>();

                for (AcceptanceOfCompletedWorksActCrossTabData actCrossTabData : actItems) {
                    if (actItemsSumMap.containsKey(actCrossTabData.getGoodName())) {
                        Long sum = Long.valueOf(actItemsSumMap.get(actCrossTabData.getGoodName()).getSum());
                        sum = sum + Long.valueOf(actCrossTabData.getSum());
                        actItemsSumMap.get(actCrossTabData.getGoodName()).setSum(String.valueOf(sum));
                    } else {
                        actItemsSumMap.put(actCrossTabData.getGoodName(), actCrossTabData);
                    }
                }

                actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();

                actItems.addAll(actItemsSumMap.values());

                List<String> goodNamesNot = new ArrayList<String>();
                for (String name : goodNamesReduce) {
                    boolean flag = true;
                    for (AcceptanceOfCompletedWorksActCrossTabData actCrossTabData : actItems) {
                        if (actCrossTabData.getGoodName().equals(name)) {
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (!flag) {
                        goodNamesNot.add(name);
                    }
                }

                for (String name : goodNamesNot) {
                    if (org.getType().equals(OrganizationType.SCHOOL)) {
                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                                name, "Школа", "0");
                        actItems.add(actCrossTabDataSc);
                    } else if (org.getType().equals(OrganizationType.KINDERGARTEN)) {
                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataDs = new AcceptanceOfCompletedWorksActCrossTabData(
                                name, "Детский сад", "0");
                        actItems.add(actCrossTabDataDs);
                    }
                }
            }
        }

        if (type.equals("Платное горячее питание")) {
            AcceptanceOfCompletedWorksActPayPlanService service = new AcceptanceOfCompletedWorksActPayPlanService();
            service.setSession(getSession());

            List<GoodItem1> allGoods = service.findAllGoodsByOrderTypesByOrg(idOfOrg, startTime, endTime,
                    service.getPayPlanAndSubscriptionFeedingOrderTypes());

            if (allGoods.isEmpty()) {
                String str = "";
                for (int i = 0; i < 6; i++) {
                    str = str + " ";
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataDs = new AcceptanceOfCompletedWorksActCrossTabData(
                            str, " ", "");
                    actItems.add(actCrossTabDataDs);
                }
            } else {
                Org org = (Org) getSession().load(Org.class, idOfOrg);
                for (GoodItem1 goodItem : allGoods) {

                    if (org.getType().equals(OrganizationType.SCHOOL)) {
                        Long qty = service.buildRegisterStampBodyValueByOrderTypesByOrg(idOfOrg, startTime, endTime,
                                goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                goodItem.getPathPart1(), "Школа", qty.toString());
                        actItems.add(actCrossTabData);

                        sumPrice += goodItem.getPrice() * qty;
                    } else if (org.getType().equals(OrganizationType.KINDERGARTEN)) {
                        Long qty = service.buildRegisterStampBodyValueByOrderTypesByOrg(idOfOrg, startTime, endTime,
                                goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                        AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                goodItem.getPathPart1(), "Детский сад", qty.toString());
                        actItems.add(actCrossTabData);

                        sumPrice += goodItem.getPrice() * qty;
                    }
                }
            }
        }

        return new SumPriceAndCrossTabItems(actItems, sumPrice);
    }

    private SumPriceAndCrossTabItems findAllForCrossTabByOrgs(Set<Org> friendlyOrganizationsSet, Date startTime,
            Date endTime, String type) {

        List<Long> idOfOrgList = new ArrayList<Long>();

        for (Org org : friendlyOrganizationsSet) {
            idOfOrgList.add(org.getIdOfOrg());
        }

        List<AcceptanceOfCompletedWorksActCrossTabData> actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();

        Long sumPrice = 0L;

        if (type.equals("Льготное питание")) {
            AcceptanceOfCompletedWorksActReducePricePlanService service = new AcceptanceOfCompletedWorksActReducePricePlanService();
            service.setSession(getSession());

            List<GoodItemAct> allGoods = service.findAllGoodsByTypesByOrgs(idOfOrgList, startTime, endTime,
                    service.getReducedPricePlanOrderTypes());

            Set<GoodItemAct> allGoodsNon = new TreeSet<GoodItemAct>();
            for (GoodItemAct goodItemAct : allGoods) {
                allGoodsNon.add(goodItemAct);
            }

            Map<MapItem, AcceptanceOfCompletedWorksActCrossTabData> actItemsSumMap = new HashMap<MapItem, AcceptanceOfCompletedWorksActCrossTabData>();

            if (allGoodsNon.isEmpty()) {
                for (String name : goodNamesReduce) {
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataDs = new AcceptanceOfCompletedWorksActCrossTabData(
                            name, "Детский сад", "0");
                    actItems.add(actCrossTabDataDs);
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                            name, "Школа", "0");
                    actItems.add(actCrossTabDataSc);
                }
            } else {
                for (GoodItemAct goodItem : allGoodsNon) {
                    SumQtyAndPriceItem sumQtyAndPriceItem = service
                            .buildRegisterStampBodyValueByOrgs(idOfOrgList, startTime, endTime, goodItem.getFullName(),
                                    service.getReducedPricePlanOrderTypes());

                    sumPrice += sumQtyAndPriceItem.getSumPrice();

                    if (goodItem.getOrderType().equals(OrderTypeEnumType.WATER_ACCOUNTING)) {
                        if (goodItem.getPathPart1().equals("Детский сад")) {
                            AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                    goodItem.getPathPart3(), goodItem.getPathPart1(),
                                    sumQtyAndPriceItem.getSumQty().toString());
                            actItems.add(actCrossTabData);

                            MapItem mapItem = new MapItem(goodItem.getPathPart3(), goodItem.getPathPart1());
                            MapItem mapItemSc = new MapItem(goodItem.getPathPart3(), "Школа");

                            if (!actItemsSumMap.containsKey(mapItem)) {
                                actItemsSumMap.put(mapItem,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart3(),
                                                goodItem.getPathPart1(), "0"));
                                actItemsSumMap.put(mapItemSc,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart3(), "Школа",
                                                "0"));
                            }
                        } else {
                            AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                    goodItem.getPathPart3(), goodItem.getPathPart1(),
                                    sumQtyAndPriceItem.getSumQty().toString());
                            actItems.add(actCrossTabData);

                            MapItem mapItem = new MapItem(goodItem.getPathPart3(), goodItem.getPathPart1());
                            MapItem mapItemDs = new MapItem(goodItem.getPathPart3(), "Детский сад");

                            if (!actItemsSumMap.containsKey(mapItem)) {
                                actItemsSumMap.put(mapItem,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart3(),
                                                goodItem.getPathPart1(), "0"));
                                actItemsSumMap.put(mapItemDs,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart3(),
                                                "Детский сад", "0"));
                            }
                        }
                    } else {
                        if (goodItem.getPathPart1().equals("Детский сад")) {
                            AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                    goodItem.getPathPart4(), goodItem.getPathPart1(),
                                    sumQtyAndPriceItem.getSumQty().toString());
                            actItems.add(actCrossTabData);

                            MapItem mapItem = new MapItem(goodItem.getPathPart4(), goodItem.getPathPart1());
                            MapItem mapItemSc = new MapItem(goodItem.getPathPart4(), "Школа");

                            if (!actItemsSumMap.containsKey(mapItem)) {
                                actItemsSumMap.put(mapItem,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(),
                                                goodItem.getPathPart1(), "0"));
                                actItemsSumMap.put(mapItemSc,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(), "Школа",
                                                "0"));
                            }
                        } else {
                            AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                                    goodItem.getPathPart4(), goodItem.getPathPart1(),
                                    sumQtyAndPriceItem.getSumQty().toString());
                            actItems.add(actCrossTabData);

                            MapItem mapItem = new MapItem(goodItem.getPathPart4(), goodItem.getPathPart1());
                            MapItem mapItemDs = new MapItem(goodItem.getPathPart4(), "Детский сад");

                            if (!actItemsSumMap.containsKey(mapItem)) {
                                actItemsSumMap.put(mapItem,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(),
                                                goodItem.getPathPart1(), "0"));
                                actItemsSumMap.put(mapItemDs,
                                        new AcceptanceOfCompletedWorksActCrossTabData(goodItem.getPathPart4(),
                                                "Детский сад", "0"));
                            }
                        }
                    }
                }

                for (AcceptanceOfCompletedWorksActCrossTabData actCrossTabData : actItems) {
                    for (MapItem mapItem : actItemsSumMap.keySet()) {
                        if (mapItem.getGoodName().equals(actCrossTabData.getGoodName()) && mapItem.getEducation()
                                .equals(actCrossTabData.getEducation())) {
                            long sum = Long.parseLong(actItemsSumMap.get(mapItem).getSum());
                            sum = sum + Long.parseLong(actCrossTabData.getSum());
                            actItemsSumMap.get(mapItem).setSum(String.valueOf(sum));
                            break;
                        }
                    }
                }

                actItems = new ArrayList<AcceptanceOfCompletedWorksActCrossTabData>();

                actItems.addAll(actItemsSumMap.values());

                List<String> goodNamesNot = new ArrayList<String>();
                for (String name : goodNamesReduce) {
                    boolean flag = true;
                    for (AcceptanceOfCompletedWorksActCrossTabData actCrossTabData : actItems) {
                        if (actCrossTabData.getGoodName().equals(name)) {
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (!flag) {
                        goodNamesNot.add(name);
                    }
                }

                for (String name : goodNamesNot) {
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                            name, "Школа", "0");
                    actItems.add(actCrossTabDataSc);

                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataDs = new AcceptanceOfCompletedWorksActCrossTabData(
                            name, "Детский сад", "0");
                    actItems.add(actCrossTabDataDs);
                }
            }
        }
        if (type.equals("Платное горячее питание")) {
            AcceptanceOfCompletedWorksActPayPlanService service = new AcceptanceOfCompletedWorksActPayPlanService();
            service.setSession(getSession());

            List<GoodItem1> allGoods = service.findAllGoodsByOrderTypesByOrgs(idOfOrgList, startTime, endTime,
                    service.getPayPlanAndSubscriptionFeedingOrderTypes());

            if (allGoods.isEmpty()) {
                String str = "";
                for (int i = 0; i < 6; i++) {
                    str = str + " ";
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataDs = new AcceptanceOfCompletedWorksActCrossTabData(
                            str, " ", "");
                    actItems.add(actCrossTabDataDs);
                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabDataSc = new AcceptanceOfCompletedWorksActCrossTabData(
                            str, "  ", "");
                    actItems.add(actCrossTabDataSc);
                }
            } else {

                for (GoodItem1 goodItem : allGoods) {

                    Long qty = service.buildRegisterStampBodyValueByOrderTypesByOrgs(idOfOrgList, startTime, endTime,
                            goodItem.getFullName(), service.getPayPlanAndSubscriptionFeedingOrderTypes());

                    AcceptanceOfCompletedWorksActCrossTabData actCrossTabData = new AcceptanceOfCompletedWorksActCrossTabData(
                            goodItem.getPathPart4(), goodItem.getPathPart1(), qty.toString());
                    actItems.add(actCrossTabData);

                    sumPrice += goodItem.getPrice() * qty;
                }
            }
        }

        return new SumPriceAndCrossTabItems(actItems, sumPrice);
    }

    public AcceptanceOfCompletedWorksActItem fooBar(List<Object> res) {
        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem = new AcceptanceOfCompletedWorksActItem();

        for (Object o : res) {
            Object[] objList = (Object[]) o;

            acceptanceOfCompletedWorksActItem.setNumberOfContract((String) objList[0]);
            acceptanceOfCompletedWorksActItem
                    .setDateOfConclusion(CalendarUtils.dateShortToStringFullYear((Date) objList[1]) + "г.");
            acceptanceOfCompletedWorksActItem.setShortNameInfoService(((String) objList[2]).replaceAll("\"", ""));
            String executor = ((String) objList[3]).replaceAll("\"", "");
            acceptanceOfCompletedWorksActItem.setExecutor(executor);
            acceptanceOfCompletedWorksActItem
                    .setDateOfClosing(CalendarUtils.dateShortToStringFullYear((Date) objList[4]) + "г.");
            if (objList[5].equals("")) {
                acceptanceOfCompletedWorksActItem
                        .setOfficialPosition("_____________________________________________________");
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

    private AcceptanceOfCompletedWorksActItem emptyBar(Org org) {
        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem = new AcceptanceOfCompletedWorksActItem();

        acceptanceOfCompletedWorksActItem.setNumberOfContract("________");
        acceptanceOfCompletedWorksActItem.setDateOfConclusion("____________");
        acceptanceOfCompletedWorksActItem.setShortNameInfoService(org.getShortNameInfoService().replaceAll("\"", ""));
        acceptanceOfCompletedWorksActItem.setExecutor("______________________________");
        acceptanceOfCompletedWorksActItem.setDateOfClosing("____________");
        if (org.getOfficialPosition() != null) {
            acceptanceOfCompletedWorksActItem.setOfficialPosition(
                    org.getOfficialPosition() + ", " + org.getOfficialPerson().getSurnameAndFirstLetters());
            acceptanceOfCompletedWorksActItem.setFullName(org.getOfficialPerson().getSurnameAndFirstLetters());
        } else {
            acceptanceOfCompletedWorksActItem
                    .setOfficialPosition("_____________________________________________________");
            acceptanceOfCompletedWorksActItem.setFullName("____________");
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

    public class MapItem {

        /**
         * Наименование товара (Завтрак, Завтрак второй, Обед и т.д.)
         */
        public String goodName;

        /**
         * Наименование образовательного учреждения
         */
        private String education;

        public MapItem(String goodName, String education) {
            this.goodName = goodName;
            this.education = education;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public String getEducation() {
            return education;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MapItem mapItem = (MapItem) o;

            if (!education.equals(mapItem.education)) {
                return false;
            }
            if (!goodName.equals(mapItem.goodName)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = goodName.hashCode();
            result = 31 * result + education.hashCode();
            return result;
        }
    }
}
