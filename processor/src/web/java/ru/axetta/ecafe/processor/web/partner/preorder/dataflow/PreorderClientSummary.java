
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderDAOService;

import java.util.Date;
import java.util.Map;


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
    private Integer forbiddenDays;
    private Long usedSum;
    private Integer subscriptionFeeding;
    private Map<String, Integer[]> calendar;

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
        Client client = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getClientByContractId(contractId);
        this.usedSum = getPreordersSum(client);
        this.forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(client);
        this.calendar = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getSpecialDates(new Date(), PreorderComplex.getDaysOfRegularPreorders(), summary.getOrgId(), client);
        SubscriptionFeeding sf = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getClientSubscriptionFeeding(client);
        this.subscriptionFeeding = (sf == null) ? 0 : 1;
    }

    private Long getPreordersSum(Client client) {
        Date today = CalendarUtils.startOfDay(new Date());
        Date endDate = CalendarUtils.addDays(today, 14);
        endDate = CalendarUtils.endOfDay(endDate);
        return RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getPreordersSum(client, today, endDate);
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

    public Integer getForbiddenDays() {
        return forbiddenDays;
    }

    public void setForbiddenDays(Integer forbiddenDays) {
        this.forbiddenDays = forbiddenDays;
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

    public Map<String, Integer[]> getCalendar() {
        return calendar;
    }

    public void setCalendar(Map<String, Integer[]> calendar) {
        this.calendar = calendar;
    }

    public Long getUsedSum() {
        return usedSum;
    }

    public void setUsedSum(Long usedSum) {
        this.usedSum = usedSum;
    }

    public Integer getSubscriptionFeeding() {
        return subscriptionFeeding;
    }

    public void setSubscriptionFeeding(Integer subscriptionFeeding) {
        this.subscriptionFeeding = subscriptionFeeding;
    }
}