package ru.axetta.ecafe.processor.core.push.model;

import ru.axetta.ecafe.processor.core.persistence.Client;

public class BalanceData extends AbstractPushData {

    private Integer actionType;
    private String occurredAt;
    private String account;
    private Integer balance;
    private Integer balanceChange;
    private Integer reasonId;
    private Integer benefitType;
    private Long organizationId;
    private String personId;

    @Override
    public String toString() {
        return "BalanceData{" +
                "actionType=" + actionType +
                ", occurredAt='" + occurredAt + '\'' +
                ", account='" + account + '\'' +
                ", balance=" + balance +
                ", balanceChange=" + balanceChange +
                ", reasonId=" + reasonId +
                ", benefitType=" + benefitType +
                ", organizationId=" + organizationId +
                ", personId='" + personId + '\'' +
                '}';
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getBalanceChange() {
        return balanceChange;
    }

    public void setBalanceChange(Integer balanceChange) {
        this.balanceChange = balanceChange;
    }

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public Integer getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(Integer benefitType) {
        this.benefitType = benefitType;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

}
