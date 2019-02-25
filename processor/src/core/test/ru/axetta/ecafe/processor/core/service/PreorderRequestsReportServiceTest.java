/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.persistence.ProductionCalendar;
import ru.axetta.ecafe.processor.core.persistence.SpecialDate;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.junit.Test;

import java.util.*;

public class PreorderRequestsReportServiceTest {

    @Test
    public void getClientBalancesOnDates() throws Exception {
        PreorderRequestsReportService service = new PreorderRequestsReportService();
        List<PreorderRequestsReportService.PreorderItem> list = new ArrayList<PreorderRequestsReportService.PreorderItem>();
        Long idOfOrg = 11L;
        Long balance = 55000L;
        Long idOfClientGroup = 90L;

        list.add(getItem(idOfOrg, "22.09.2019", 11000L, balance, idOfClientGroup, idOfOrg));
        list.add(getItem(idOfOrg, "23.09.2019", 11000L, balance, idOfClientGroup, idOfOrg));
        list.add(getItem(idOfOrg, "23.09.2019", 1000L, balance, idOfClientGroup, idOfOrg));
        list.add(getItem(idOfOrg, "24.09.2019", 11000L, balance, idOfClientGroup, idOfOrg));

        Map<Long, List<SpecialDate>> mapSpecialDates = new HashMap<Long, List<SpecialDate>>();
        List<SpecialDate> specialDates = new ArrayList<SpecialDate>();
        specialDates.add(createSpecialDate("23.09.2019", idOfOrg, false, null));
        mapSpecialDates.put(11L, specialDates);

        List<ProductionCalendar> productionCalendar = new ArrayList<ProductionCalendar>();
        Map<Long, Map<Date, Long>> map = service.getClientBalancesOnDates(list, mapSpecialDates, productionCalendar);
        System.out.println(map);
    }

    private SpecialDate createSpecialDate(String date, Long idOfOrg, boolean isWeekend, Long idOfClientGroup) throws Exception {
        SpecialDate sd = new SpecialDate();
        sd.setDate(CalendarUtils.parseDate(date));
        sd.setIdOfOrg(idOfOrg);
        sd.setIsWeekend(isWeekend);
        sd.setDeleted(false);
        sd.setIdOfClientGroup(idOfClientGroup);

        return sd;
    }

    private PreorderRequestsReportService.PreorderItem getItem(Long idOfClient, String date, Long sum, Long balance, Long idOfClientGroup, Long idOfOrg) throws Exception {
        PreorderRequestsReportService.PreorderItem item = new PreorderRequestsReportService.PreorderItem();
        item.setPreorderDate(CalendarUtils.parseDate(date));
        item.setIdOfClient(idOfClient);
        item.setComplexPrice(sum);
        item.setClientBalance(balance);
        item.setUsedSum(0L);
        item.setIdOfClientGroup(idOfClientGroup);
        item.setIdOfOrg(idOfOrg);
        return item;
    }
}