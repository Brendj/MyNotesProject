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
        if (startDate == null || endDate == null || idOfOrg == null) {
            return null;
        }
        Date eDate = CalendarUtils.endOfDay(endDate);
        List<TaloonPreorderVerificationItem> items = new ArrayList<>();
        Map<String, TaloonPreorderVerificationDetail> summaryMap = new HashMap<>();
        Map<Long, TaloonPreorderVerificationComplex> complexMap;

        Criteria criteria = session.createCriteria(TaloonPreorder.class);
        criteria.add(Restrictions.gt("taloonDate", startDate));
        criteria.add(Restrictions.lt("taloonDate", eDate));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("taloonDate"));
        criteria.addOrder(Order.asc("complexName"));
        criteria.addOrder(Order.asc("goodsName"));
        List<TaloonPreorder> list = criteria.list();

        Set<Date> dateSet = new TreeSet<>();
        for (TaloonPreorder taloon : list) {
            dateSet.add(taloon.getTaloonDate());
        }

        for (Date date : dateSet) {
            TaloonPreorderVerificationItem item = new TaloonPreorderVerificationItem();
            item.setTaloonDate(date);
            complexMap = new HashMap<>();
            TaloonPreorderVerificationDetail detailSum = new TaloonPreorderVerificationDetail(null, null, null, date,
                    null, null, "Всего", null, null, 0, 0L, 0, 0L, 0, 0L, 0, 0L, 0, 0L, 0, 0L, null, null, null, null,
                    true);

            for (TaloonPreorder taloon : list) {
                if (!date.equals(taloon.getTaloonDate())) {
                    continue;
                }
                TaloonPreorderVerificationDetail detail = new TaloonPreorderVerificationDetail(taloon.getGuid(),
                        taloon.getIdOfOrg(), taloon.getIdOfOrgCreated(), date, taloon.getComplexId(),
                        taloon.getComplexName(), taloon.getGoodsName(), taloon.getGoodsGuid(), taloon.getPrice(),
                        taloon.getRequestedQty(), (taloon.getPrice() == null || taloon.getRequestedQty() == null) ? 0
                        : taloon.getPrice() * taloon.getRequestedQty(), taloon.getSoldQty(),
                        (taloon.getPrice() == null || taloon.getSoldQty() == null) ? 0
                                : taloon.getPrice() * taloon.getSoldQty(), taloon.getRequestedQty(),
                        (taloon.getPrice() == null || taloon.getRequestedQty() == null) ? 0
                                : taloon.getPrice() * taloon.getRequestedQty(), taloon.getReservedQty(),
                        (taloon.getPrice() == null || taloon.getReservedQty() == null) ? 0
                                : taloon.getPrice() * taloon.getReservedQty(), taloon.getBlockedQty(),
                        (taloon.getPrice() == null || taloon.getBlockedQty() == null) ? 0
                                : taloon.getPrice() * taloon.getBlockedQty(),
                        (taloon.getRequestedQty() == null || taloon.getSoldQty() == null) ? 0
                                : (taloon.getRequestedQty() - taloon.getSoldQty()),
                        (taloon.getPrice() == null || taloon.getRequestedQty() == null || taloon.getSoldQty() == null)
                                ? 0 : taloon.getPrice() * (taloon.getRequestedQty() - taloon.getSoldQty()),
                        taloon.getIsppState(), taloon.getPpState(), taloon.getRemarks(), taloon.getComments(), false);

                addComplexToMap(item, complexMap, date, detail, taloon.getComplexId(), taloon.getComplexName());
                detailSum.addQtyAndGet(detail);

                if (!summaryMap.containsKey(detail.getComplexId() + detail.getGoodsGuid())) {
                    // Формирование итоговой строки
                    summaryMap.put(taloon.getComplexId() + taloon.getGoodsGuid(),
                            new TaloonPreorderVerificationDetail(null, null, null, null, detail.getComplexId(),
                                    detail.getComplexName(), detail.getGoodsName(), detail.getGoodsGuid(),
                                    detail.getPrice(), detail.getRequestedQty(), detail.getRequestedSum(),
                                    detail.getSoldQty(), detail.getSoldSum(), detail.getShippedQty(),
                                    detail.getShippedSum(), detail.getReservedQty(), detail.getReservedSum(),
                                    detail.getBlockedQty(), detail.getBlockedSum(), detail.getDifferedQty(),
                                    detail.getDifferedSum(), null, null, null, null, true));
                } else {
                    summaryMap.get(taloon.getComplexId() + taloon.getGoodsGuid()).addQtyAndGet(detail);
                }
            }
            addItemToList(items, complexMap, item);
            // Всего
            TaloonPreorderVerificationComplex complex = new TaloonPreorderVerificationComplex();
            complex.setTaloonDate(date);
            complex.setItem(item);
            complex.setComplexName("Всего");
            detailSum.setComplex(complex);
            complex.getDetails().add(detailSum);
            item.getComplexes().add(complex);
            item.setPpState();
        }
        // Итого
        TaloonPreorderVerificationItem item = new TaloonPreorderVerificationItem();
        item.setTaloonDate(null);

        // Пустая строка Итого
        TaloonPreorderVerificationComplex emptyComplex = new TaloonPreorderVerificationComplex();
        emptyComplex.setTaloonDate(null);
        emptyComplex.setItem(item);
        TaloonPreorderVerificationDetail emptyDetail = new TaloonPreorderVerificationDetail(null, null, null, null,
                null, null, "За весь период", null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, true);
        emptyDetail.setComplex(emptyComplex);
        emptyComplex.getDetails().add(emptyDetail);
        item.getComplexes().add(emptyComplex);

        complexMap = new HashMap<>();
        Iterator<Map.Entry<String, TaloonPreorderVerificationDetail>> iter = summaryMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, TaloonPreorderVerificationDetail> e = iter.next();
            addComplexToMap(item, complexMap, null, e.getValue(), e.getValue().getComplexId(),
                    e.getValue().getComplexName());
        }
        addItemToList(items, complexMap, item);
        return items;
    }

    private void addItemToList(List<TaloonPreorderVerificationItem> items,
            Map<Long, TaloonPreorderVerificationComplex> complexMap, TaloonPreorderVerificationItem item) {
        Iterator<Map.Entry<Long, TaloonPreorderVerificationComplex>> complexIter = complexMap.entrySet().iterator();
        while (complexIter.hasNext()) {
            Map.Entry<Long, TaloonPreorderVerificationComplex> e = complexIter.next();
            item.getComplexes().add(e.getValue());
        }
        items.add(item);
    }

    private void addComplexToMap(TaloonPreorderVerificationItem item,
            Map<Long, TaloonPreorderVerificationComplex> complexMap, Date date, TaloonPreorderVerificationDetail detail,
            Long complexId, String complexName) {
        if (!complexMap.containsKey(complexId)) {
            TaloonPreorderVerificationComplex complex = new TaloonPreorderVerificationComplex();
            complex.setTaloonDate(date);
            complex.setComplexId(complexId);
            complex.setComplexName(complexName);
            complex.setItem(item);
            detail.setComplex(complex);
            complexMap.put(complexId, complex);
        }
        complexMap.get(complexId).getDetails().add(detail);
    }

    @Transactional
    public void applyChanges(Session session, List<TaloonPreorderVerificationItem> items) throws Exception {
        if (items != null) {
            for (TaloonPreorderVerificationItem item : items) {
                for (TaloonPreorderVerificationComplex complex : item.getComplexes()) {
                    for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                        if (detail.isSummaryDay()) {
                            continue;
                        }
                        String guid = detail.getGuid();
                        TaloonPreorder taloon = DAOReadonlyService.getInstance().findTaloonPreorder(guid);
                        if (taloon != null) {
                            if (itemChangedNullSafe(taloon.getShippedQty(), detail.getShippedQty()) ||
                                    !taloon.getPpState().equals(detail.getPpState()) ||
                                    itemChangedNullSafe(taloon.getComments(), detail.getComments())) {
                                detail.setChangedData(true);
                                String rem = (taloon.getRemarks() == null ? "-" : taloon.getRemarks());
                                taloon.setRemarks(rem.concat("\n").concat(String
                                        .format("Изменено в АРМ отчетности, пользователь %s, %2$td.%2$tm.%2$tY %2$tT",
                                                DAOReadonlyService.getInstance().getUserFromSession().getUserName(),
                                                new Date())));
                                taloon.setComments(detail.getComments());
                                taloon.setShippedQty(detail.getShippedQty());
                                taloon.setPpState(detail.getPpState());
                                Long nextVersion = DAOUtils.nextVersionByTaloonPreorder(session);
                                taloon.setVersion(nextVersion);
                                session.update(taloon);
                            }
                        }
                    }
                }
            }
            session.flush();
        }
    }

    private boolean itemChangedNullSafe(Integer fromDB, Integer fromApp) {
        Integer fromDB1 = (fromDB == null ? 0 : fromDB);
        Integer fromApp1 = (fromApp == null ? 0 : fromApp);
        return !fromDB1.equals(fromApp1);
    }

    private boolean itemChangedNullSafe(String fromDB, String fromApp) {
        String fromDB1 = (fromDB == null ? "" : fromDB);
        String fromApp1 = (fromApp == null ? "" : fromApp);
        return !fromDB1.equals(fromApp1);
    }
}
