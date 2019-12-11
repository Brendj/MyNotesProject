/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonPreorder;

import ru.axetta.ecafe.processor.core.persistence.TaloonPreorder;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
public class TaloonPreorderVerification {

    public List<TaloonPreorderVerificationItem> getItems(Session session, Date startDate, Date endDate, Long idOfOrg) {
        if (startDate == null || endDate == null || idOfOrg == null) return null;

        Date eDate = CalendarUtils.endOfDay(endDate);

        List<TaloonPreorderVerificationItem> items = new ArrayList<>();
        Criteria criteria = session.createCriteria(TaloonPreorder.class);
        criteria.add(Restrictions.gt("taloonDate", startDate));
        criteria.add(Restrictions.lt("taloonDate", eDate));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("taloonDate"));
        criteria.addOrder(Order.asc("complexName"));
        criteria.addOrder(Order.asc("goodsName"));
        List<TaloonPreorder> list = criteria.list();
        Set<Date> map = new HashSet<>();
        for(TaloonPreorder taloon : list) {
            map.add(taloon.getTaloonDate());
        }
        List<Date> sortedMap = new ArrayList(map);
        Collections.sort(sortedMap);
        Map<String, TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail> summaryMap = new HashMap<>();

        for (Date d : sortedMap) {
            TaloonPreorderVerificationItem item = new TaloonPreorderVerificationItem();
            item.setTaloonDate(d);

            TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail detailSum =
                    new TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail(
                            null, null, d, null, "Всего", null,
                            null, null, 0, 0L, 0, 0L,
                            0, 0L, 0, 0L, 0, 0L,
                            0, 0L, null, null, null, true);

            for (TaloonPreorder taloon : list) {
                if (!d.equals(taloon.getTaloonDate())) {
                    continue;
                }
                TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail detail =
                        new TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail(
                                taloon.getIdOfOrg(),
                                taloon.getIdOfOrgCreated(),
                                d,
                                taloon.getComplexId(),
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

                detailSum.addAndGet(detail);
                item.getDetails().add(detail);

                if (!summaryMap.containsKey(detail.getComplexId() + detail.getGoodsGuid())) {
                    summaryMap.put(taloon.getComplexId() + taloon.getGoodsGuid(),
                                    new TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail(
                                            null, null, null,
                                            detail.getComplexId(), detail.getComplexName(),
                                            detail.getGoodsName(), detail.getGoodsGuid(), detail.getPrice(),
                                            detail.getRequestedQty(), detail.getRequestedSum(), detail.getSoldQty(),
                                            detail.getSoldSum(), detail.getShippedQty(), detail.getShippedSum(),
                                            detail.getReservedQty(), detail.getReservedSum(), detail.getBlockedQty(),
                                            detail.getBlockedSum(), detail.getDifferedQty(), detail.getDifferedSum(),
                                            null, null, null,
                                            true));
                } else {
                    summaryMap.get(taloon.getComplexId() + taloon.getGoodsGuid()).addAndGet(detail);
                }
            }
            item.getDetails().add(detailSum);
            items.add(item);
        }

        TaloonPreorderVerificationItem item = new TaloonPreorderVerificationItem();
        item.setTaloonDate(null);
        Iterator<Map.Entry<String, TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail>> iter =
                summaryMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail> e =
                    iter.next();
            item.getDetails().add(e.getValue());
        }
        items.add(item);
        return items;
    }

    @Transactional
    public void applyChanges(Session session, List<TaloonPreorderVerificationItem> items) throws Exception {
        for (TaloonPreorderVerificationItem item : items) {
            Date taloonDate = item.getTaloonDate();
            for (TaloonPreorderVerificationItem.TaloonPreorderVerificationItemDetail detail : item.getDetails()) {
                if (detail.isSummaryDay()) {
                    continue;
                }
                Long complexId = detail.getComplexId();
                String goodsGuid = detail.getGoodsGuid();
                Long idOfOrg = detail.getIdOfOrg();
                Long price = detail.getPrice();
                TaloonPreorder taloon = DAOReadonlyService.getInstance().findTaloonPreorder(idOfOrg, taloonDate, complexId, goodsGuid, price);
                if (taloon != null) {
                    if (itemChangedNullSafe(taloon.getShippedQty(), detail.getShippedQty()) || !taloon.getPpState().equals(detail.getPpState())) {
                        String rem = (taloon.getRemarks() == null ? "-" : taloon.getRemarks());
                        taloon.setRemarks(rem.concat("\n").concat(String.format("Изменено в АРМ отчетности, пользователь=%s, %2$td.%2$tm.%2$tY %2$tT",
                                DAOReadonlyService.getInstance().getUserFromSession().getUserName(), new Date())));
                        taloon.setShippedQty(detail.getShippedQty());
                        taloon.setPpState(detail.getPpState());
                        Long nextVersion = DAOUtils.nextVersionByTaloonPreorder(session);
                        taloon.setVersion(nextVersion);
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
