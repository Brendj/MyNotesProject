/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonApprovalPreorder;

import ru.axetta.ecafe.processor.core.persistence.TaloonApprovalPreorder;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by o.petrova on 09.12.2019.
 */
public class TaloonApprovalPreorderVerification {

    public List<TaloonApprovalPreorderVerificationItem> getItems(Session session, Date startDate, Date endDate, Long idOfOrg) {
        if (startDate == null || endDate == null || idOfOrg == null) return null;

        Date eDate = CalendarUtils.endOfDay(endDate);

        List<TaloonApprovalPreorderVerificationItem> items = new ArrayList<>();
        Criteria criteria = session.createCriteria(TaloonApprovalPreorder.class);
        criteria.add(Restrictions.gt("taloonDate", startDate));
        criteria.add(Restrictions.lt("taloonDate", eDate));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("taloonDate"));
        criteria.addOrder(Order.asc("taloonName"));
        List<TaloonApprovalPreorder> list = criteria.list();
        Set<Date> map = new HashSet<>();
        for(TaloonApprovalPreorder taloon : list) {
            map.add(taloon.getTaloonDate());
        }
        List<Date> sortedMap = new ArrayList(map); // ?
        Collections.sort(sortedMap);

        Integer tdRequestedQty = 0;
        Long tdRequestedSum = 0L;
        Integer tdSoldQty = 0;
        Long tdSoldSum = 0L;
        Integer tdShippedQty = 0;
        Long tdShippedSum = 0L;
        Integer tdReservedQty = 0;
        Long tdReservedSum = 0L;
        Integer tdBlockedQty = 0;
        Long tdBlockedSum = 0L;
        Integer tdDifferedQty = 0;
        Long tdDifferedSum = 0L;

        for (Date d : sortedMap) {
            TaloonApprovalPreorderVerificationItem item = new TaloonApprovalPreorderVerificationItem();
            item.setTaloonDate(d);
            Integer sdRequestedQty = 0;
            Long sdRequestedSum = 0L;
            Integer sdSoldQty = 0;
            Long sdSoldSum = 0L;
            Integer sdShippedQty = 0;
            Long sdShippedSum = 0L;
            Integer sdReservedQty = 0;
            Long sdReservedSum = 0L;
            Integer sdBlockedQty = 0;
            Long sdBlockedSum = 0L;
            Integer sdDifferedQty = 0;
            Long sdDifferedSum = 0L;

            for (TaloonApprovalPreorder taloon : list) {
                if (!d.equals(taloon.getTaloonDate())) {
                    continue;
                }
                TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail =
                        new TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail(
                                taloon.getIdOfOrg(),
                                taloon.getIdOfOrgCreated(),
                                d,
                                taloon.getComplexName(),
                                taloon.getGoodsName(),
                                taloon.getGoodsGuid(),
                                taloon.getPrice(),
                                taloon.getRequestedQty(),
                                (taloon.getPrice() == null || taloon.getRequestedQty() == null) ?
                                        0 : taloon.getPrice() * taloon.getRequestedQty(),
                                taloon.getSoldQty(),
                                (taloon.getPrice() == null || taloon.getSoldQty() == null) ?
                                        0 : taloon.getPrice() * taloon.getSoldQty(),
                                taloon.getShippedQty(),
                                (taloon.getPrice() == null || taloon.getShippedQty() == null) ?
                                        0 : taloon.getPrice() * taloon.getShippedQty(),
                                taloon.getReservedQty(),
                                (taloon.getPrice() == null || taloon.getReservedQty() == null) ?
                                        0 : taloon.getPrice() * taloon.getReservedQty(),
                                taloon.getBlockedQty(),
                                (taloon.getPrice() == null || taloon.getBlockedQty() == null) ?
                                        0 : taloon.getPrice() * taloon.getBlockedQty(),
                                (taloon.getShippedQty() == null || taloon.getSoldQty() == null) ?
                                        0 : (taloon.getShippedQty() - taloon.getSoldQty()),
                                (taloon.getPrice() == null || taloon.getShippedQty() == null || taloon.getSoldQty() == null) ?
                                        0 : taloon.getPrice() * (taloon.getShippedQty() - taloon.getSoldQty()),
                                taloon.getIsppState(),
                                taloon.getPpState(),
                                taloon.getRemarks(),
                                false);

                sdRequestedQty += taloon.getRequestedQty() == null ? 0 : taloon.getRequestedQty();
                sdRequestedSum = (taloon.getPrice() == null) ? 0 : taloon.getPrice() * sdRequestedQty;
                sdSoldQty += taloon.getSoldQty() == null ? 0 : taloon.getSoldQty();
                sdSoldSum = (taloon.getPrice() == null) ? 0 : taloon.getPrice() * sdSoldQty;
                sdShippedQty += taloon.getShippedQty() == null ? 0 : taloon.getShippedQty();
                sdShippedSum = (taloon.getPrice() == null) ? 0 : taloon.getPrice() * sdShippedQty;
                sdReservedQty += taloon.getReservedQty() == null ? 0 : taloon.getReservedQty();
                sdReservedSum = (taloon.getPrice() == null) ? 0 : taloon.getPrice() * sdReservedQty;
                sdBlockedQty += taloon.getBlockedQty() == null ? 0 : taloon.getBlockedQty();
                sdBlockedSum = (taloon.getPrice() == null) ? 0 : taloon.getPrice() * sdBlockedQty;
                sdDifferedQty = sdShippedQty - sdSoldQty;
                sdDifferedSum = (taloon.getPrice() == null) ? 0 : taloon.getPrice() * sdDifferedQty;
                item.getDetails().add(detail);
            }

            TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail =
                    new TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail(
                            null, null, d, "Всего", null, null,
                            null, sdRequestedQty, sdRequestedSum, sdSoldQty, sdSoldSum, sdShippedQty, sdShippedSum,
                            sdReservedQty, sdReservedSum, sdBlockedQty, sdBlockedSum, sdDifferedQty, sdDifferedSum,
                            null, null, null, true);

            item.getDetails().add(detail);
            tdRequestedQty += sdRequestedQty;
            tdRequestedSum += sdRequestedSum;
            tdSoldQty += sdSoldQty;
            tdSoldSum += sdSoldSum;
            tdShippedQty += sdShippedQty;
            tdShippedSum += sdShippedSum;
            tdReservedQty += sdReservedQty;
            tdReservedSum += sdReservedSum;
            tdBlockedQty += sdBlockedQty;
            tdBlockedSum += sdBlockedSum;
            tdDifferedQty += sdDifferedQty;
            tdDifferedSum += sdDifferedSum;
            items.add(item);
        }

        TaloonApprovalPreorderVerificationItem item = new TaloonApprovalPreorderVerificationItem();
        item.setTaloonDate(null);
        TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail =
                new TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail(
                        null, null, null, "Итого", null,
                        null, null, tdRequestedQty, tdRequestedSum, tdSoldQty, tdSoldSum, tdShippedQty,
                        tdShippedSum, tdReservedQty, tdReservedSum, tdBlockedQty, tdBlockedSum, tdDifferedQty,
                        tdDifferedSum, null, null, null, true);
        item.getDetails().add(detail);
        items.add(item);

        return items;
    }

    @Transactional
    public void applyChanges(Session session, List<TaloonApprovalPreorderVerificationItem> items) throws Exception {
        for (TaloonApprovalPreorderVerificationItem item : items) {
            Date taloonDate = item.getTaloonDate();
            for (TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail : item.getDetails()) {
                if (detail.isSummaryDay()) {
                    continue;
                }
                String complexName = detail.getComplexName();
                String goodsName = detail.getGoodsName();
                String goodsGuid = detail.getGoodsGuid();
                Long idOfOrg = detail.getIdOfOrg();
                Long price = detail.getPrice();
                TaloonApprovalPreorder taloon = DAOReadonlyService.getInstance().findTaloonApprovalPreorder(idOfOrg, taloonDate, complexName, goodsName, goodsGuid, price);
                if (taloon != null) {
                    if (itemChangedNullSafe(taloon.getShippedQty(), detail.getShippedQty()) || !taloon.getPpState().equals(detail.getPpState())) {
                        String rem = (taloon.getRemarks() == null ? "-" : taloon.getRemarks());
                        taloon.setRemarks(rem.concat("\n").concat(String.format("Изменено в АРМ отчетности, пользователь=%s, %2$td.%2$tm.%2$tY %2$tT",
                                DAOReadonlyService.getInstance().getUserFromSession().getUserName(), new Date())));
                        taloon.setShippedQty(detail.getShippedQty());
                        taloon.setPpState(detail.getPpState());
                        //Long nextVersion = DAOUtils.nextVersionByTaloonApproval(session); // !!!
                        //taloon.setVersion(nextVersion);
                        session.update(taloon);
                    }
                }
            }
        }
        session.flush();
    }

    private boolean itemChangedNullSafe(Integer fromDB, Integer fromApp) {
        Integer fromDB1 = (fromDB == null ? 0 : fromDB);
        Integer fromApp1 = (fromApp == null ? 0 : fromApp);
        return !fromDB1.equals(fromApp1);
    }

}
