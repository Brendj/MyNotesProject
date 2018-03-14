
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SpecialDate;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubscriptionFeedingSettingResult;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderDAOService;

import java.util.*;


public class PreorderClientSummary {

    private Long contractId;
    private Long balance;
    private String firstName;
    private String lastName;
    private String middleName;
    private String grade;
    private String officialName;
    private String mobilePhone;
    private String orgType;
    private Long orgId;
    private Integer specialMenu;
    protected Integer guardianCreatedWhere;
    private Integer groupPredefined;
    private Integer hoursForbidPP;
    private Map<String, Integer> calendar;

    public PreorderClientSummary() {

    }

    public PreorderClientSummary(ClientSummaryBase summary) throws Exception {
        this.contractId = summary.getContractId();
        this.balance = summary.getBalance();
        this.firstName = summary.getFirstName();
        this.lastName = summary.getLastName();
        this.middleName = summary.getMiddleName();
        this.grade = summary.getGrade();
        this.officialName = summary.getOfficialName();
        this.mobilePhone = summary.getMobilePhone();
        this.orgType = summary.getOrgType();
        this.orgId = summary.getOrgId();
        this.specialMenu = summary.getSpecialMenu();
        this.guardianCreatedWhere = summary.getGuardianCreatedWhere();
        this.groupPredefined = summary.getGroupPredefined();
        ClientRoomControllerWS controller = new ClientRoomControllerWS();
        SubscriptionFeedingSettingResult result = controller.getSubscriptionFeedingSetting(summary.getContractId());
        if (result.resultCode.equals(0L)) {
            this.hoursForbidPP = result.subscriptionFeedingSettingExt.getHoursForbidPP();
        }
        this.calendar = getSpecialDates(new Date(), summary.getOrgId(), summary.getGrade());
    }

    private Map<String, Integer> getSpecialDates(Date today, Long orgId, String groupName) throws Exception {
        Map map = new TreeMap();
        TimeZone timeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        Date endDate = CalendarUtils.addDays(today, 14);
        boolean isSixWorkWeek = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).isSixWorkWeek(orgId);
        int two_days = 0;
        while (c.getTimeInMillis() < endDate.getTime() ){
            Date currentDate = CalendarUtils.parseDate(CalendarUtils.dateShortToStringFullYear(c.getTime()));
            if (two_days <= 2) {
                c.add(Calendar.DATE, 1);
                map.put(CalendarUtils.dateToString(currentDate), 1);
                two_days++;
                continue;
            }

            SpecialDate specialDate = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getSpecialDate(currentDate, orgId);
            Boolean isWeekend = !CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate);
            if(specialDate != null){
                if(!specialDate.getDeleted()){
                    isWeekend = specialDate.getIsWeekend();
                }
            }
            int day = CalendarUtils.getDayOfWeek(currentDate);
            if (day == Calendar.SATURDAY && !isSixWorkWeek  && isWeekend) {
                //проверяем нет ли привязки отдельных групп к 6-ти дневной неделе
                isWeekend = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).isWeekendByGroup(orgId, groupName);
            }

            c.add(Calendar.DATE, 1);
            map.put(CalendarUtils.dateToString(currentDate), isWeekend ? 1 : 0);
        }
        return map;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getHoursForbidPP() {
        return hoursForbidPP;
    }

    public void setHoursForbidPP(Integer hoursForbidPP) {
        this.hoursForbidPP = hoursForbidPP;
    }

    public Integer getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Integer specialMenu) {
        this.specialMenu = specialMenu;
    }

    public Integer getGroupPredefined() {
        return groupPredefined;
    }

    public void setGroupPredefined(Integer groupPredefined) {
        this.groupPredefined = groupPredefined;
    }

    public Map<String, Integer> getCalendar() {
        return calendar;
    }

    public void setCalendar(Map<String, Integer> calendar) {
        this.calendar = calendar;
    }
}