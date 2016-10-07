/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonApproval;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfTaloonApproval;
import ru.axetta.ecafe.processor.core.persistence.TaloonApproval;
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
        criteria.add(Restrictions.gt("compositeIdOfTaloonApproval.taloonDate", startDate));
        criteria.add(Restrictions.lt("compositeIdOfTaloonApproval.taloonDate", eDate));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("compositeIdOfTaloonApproval.taloonDate"));
        criteria.addOrder(Order.asc("compositeIdOfTaloonApproval.taloonName"));
        List<TaloonApproval> list = criteria.list();
        Set<Date> map = new HashSet<Date>();
        for(TaloonApproval taloon : list) {
            map.add(taloon.getCompositeIdOfTaloonApproval().getTaloonDate());
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
                if (!d.equals(taloon.getCompositeIdOfTaloonApproval().getTaloonDate())) {
                    continue;
                }

                TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail =
                        new TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail(taloon.getCompositeIdOfTaloonApproval().getTaloonName(),
                                taloon.getSoldedQty(), taloon.getRequestedQty(), taloon.getShippedQty(), taloon.getPrice(),
                                (taloon.getPrice() == null || taloon.getSoldedQty() == null) ? 0 : taloon.getPrice() * taloon.getSoldedQty(),
                                taloon.getIsppState(),
                                taloon.getPpState(), taloon.getCompositeIdOfTaloonApproval().getIdOfOrg(), d, false);

                sdRequestedQty += taloon.getRequestedQty() == null ? 0 : taloon.getRequestedQty();
                sdSoldedQty += taloon.getSoldedQty() == null ? 0 : taloon.getSoldedQty();
                sdShippedQty += taloon.getShippedQty() == null ? 0 : taloon.getShippedQty();
                sdSumma += (taloon.getPrice() == null || taloon.getSoldedQty() == null) ? 0 : taloon.getPrice() * taloon.getSoldedQty();

                item.getDetails().add(detail);
            }
            TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail =
                    new TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail("Всего",
                            sdSoldedQty, sdRequestedQty, sdShippedQty, null,
                            sdSumma, null, null, null, d, true);
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
                new TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail("Итого",
                        tdSoldedQty, tdRequestedQty, tdShippedQty, null,
                        tdSumma, null, null, null, null, true);
        item.getDetails().add(detail);
        items.add(item);

        return items;
    }

    @Transactional
    public void applyChanges(Session session, List<TaloonApprovalVerificationItem> items) throws Exception {
        for (TaloonApprovalVerificationItem item : items) {
            Date taloonDate = item.getTaloonDate();
            for (TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail : item.getDetails()) {
                if (detail.isSummaryDay()) {
                    continue;
                }
                String taloonName = detail.getTaloonName();
                Long idOfOrg = detail.getIdOfOrg();
                CompositeIdOfTaloonApproval id = new CompositeIdOfTaloonApproval(idOfOrg, taloonDate, taloonName);
                TaloonApproval taloon = (TaloonApproval) session.load(TaloonApproval.class, id);
                if (taloon != null) {
                    taloon.setShippedQty(detail.getShippedQty());
                    taloon.setPpState(detail.getPpState());
                    Long nextVersion = DAOUtils.nextVersionByTaloonApproval(session);
                    taloon.setVersion(nextVersion);
                    session.update(taloon);
                }
            }
        }
        session.flush();
    }

}
