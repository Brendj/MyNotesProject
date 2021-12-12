/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonApproval;

import org.hibernate.Transaction;
import ru.axetta.ecafe.processor.core.persistence.TaloonApproval;
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
 * Created by i.semenov on 19.07.2016.
 */
public class TaloonApprovalVerification {

    public List<TaloonApprovalVerificationItem> getItems(Session session, Date startDate, Date endDate, Long idOfOrg) {
        if (startDate == null || endDate == null || idOfOrg == null) return null;

        Date eDate = CalendarUtils.endOfDay(endDate);

        List<TaloonApprovalVerificationItem> items = new ArrayList<TaloonApprovalVerificationItem>();
        Criteria criteria = session.createCriteria(TaloonApproval.class);
        criteria.add(Restrictions.gt("taloonDate", startDate));
        criteria.add(Restrictions.lt("taloonDate", eDate));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("taloonDate"));
        criteria.addOrder(Order.asc("taloonName"));
        List<TaloonApproval> list = criteria.list();
        Set<Date> map = new HashSet<Date>();
        for(TaloonApproval taloon : list) {
            map.add(taloon.getTaloonDate());
        }
        List<Date> sortedMap = new ArrayList(map);
        Collections.sort(sortedMap);
        Integer tdRequestedQty = 0;
        Integer tdSoldedQty = 0;
        Integer tdShippedQty = 0;
        Long tdSumma = 0L;
        for (Date d : sortedMap) {
            TaloonApprovalVerificationItem item = new TaloonApprovalVerificationItem();
            item.setTaloonDate(d);
            Integer sdRequestedQty = 0;
            Integer sdSoldedQty = 0;
            Integer sdShippedQty = 0;
            Long sdSumma = 0L;
            for (TaloonApproval taloon : list) {
                if (!d.equals(taloon.getTaloonDate())) {
                    continue;
                }

                TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail =
                        new TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail(taloon.getTaloonName(), taloon.getIdOfOrgCreated(),
                                taloon.getSoldedQty(), taloon.getRequestedQty(), taloon.getShippedQty(), taloon.getPrice(),
                                (taloon.getPrice() == null || taloon.getSoldedQty() == null) ? 0 : taloon.getPrice() * taloon.getSoldedQty(),
                                taloon.getIsppState(),
                                taloon.getPpState(), taloon.getIdOfOrg(), d, false, taloon.getGoodsGuid(), taloon.getRemarks());

                sdRequestedQty += taloon.getRequestedQty() == null ? 0 : taloon.getRequestedQty();
                sdSoldedQty += taloon.getSoldedQty() == null ? 0 : taloon.getSoldedQty();
                sdShippedQty += taloon.getShippedQty() == null ? 0 : taloon.getShippedQty();
                sdSumma += (taloon.getPrice() == null || taloon.getSoldedQty() == null) ? 0 : taloon.getPrice() * taloon.getSoldedQty();

                item.getDetails().add(detail);
            }
            TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail =
                    new TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail("Всего", null,
                            sdSoldedQty, sdRequestedQty, sdShippedQty, null,
                            sdSumma, null, null, null, d, true, null, null);
            item.getDetails().add(detail);
            tdRequestedQty += sdRequestedQty;
            tdShippedQty += sdShippedQty;
            tdSoldedQty += sdSoldedQty;
            tdSumma += sdSumma;
            items.add(item);
        }

        TaloonApprovalVerificationItem item = new TaloonApprovalVerificationItem();
        item.setTaloonDate(null);
        TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail =
                new TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail("Итого", null,
                        tdSoldedQty, tdRequestedQty, tdShippedQty, null,
                        tdSumma, null, null, null, null, true, null, null);
        item.getDetails().add(detail);
        items.add(item);

        return items;
    }

    @Transactional
    public void applyChanges(Session session, List<TaloonApprovalVerificationItem> items) throws Exception {
        Transaction transaction = session.beginTransaction();
            if (items != null) {
                for (TaloonApprovalVerificationItem item : items) {
                    Date taloonDate = item.getTaloonDate();
                    for (TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail : item.getDetails()) {
                        if (detail.isSummaryDay()) {
                            continue;
                        }
                        String taloonName = detail.getTaloonName();
                        String goodsGuid = detail.getGoodsGuid();
                        Long idOfOrg = detail.getIdOfOrg();
                        Long price = detail.getPrice();
                        TaloonApproval taloon = DAOReadonlyService.getInstance().findTaloonApproval(idOfOrg, taloonDate, taloonName, goodsGuid, price);
                        if (taloon != null) {
                            if (itemChangedNullSafe(taloon.getShippedQty(), detail.getShippedQty()) || !taloon.getPpState().equals(detail.getPpState())) {
                                String rem = (taloon.getRemarks() == null ? "-" : taloon.getRemarks());
                                taloon.setRemarks(rem.concat("\n").concat(String.format("Изменено в АРМ отчетности, пользователь=%s, %2$td.%2$tm.%2$tY %2$tT",
                                        DAOReadonlyService.getInstance().getUserFromSession().getUserName(), new Date())));
                                taloon.setShippedQty(detail.getShippedQty());
                                taloon.setPpState(detail.getPpState());
                                Long nextVersion = DAOUtils.nextVersionByTaloonApproval(session);
                                taloon.setVersion(nextVersion);
                                session.update(taloon);
                            }
                        }
                    }
                }
                session.flush();
            }
        transaction.commit();
    }

    private boolean itemChangedNullSafe(Integer fromDB, Integer fromApp) {
        Integer fromDB1 = (fromDB == null ? 0 : fromDB);
        Integer fromApp1 = (fromApp == null ? 0 : fromApp);
        return !fromDB1.equals(fromApp1);
    }

}
