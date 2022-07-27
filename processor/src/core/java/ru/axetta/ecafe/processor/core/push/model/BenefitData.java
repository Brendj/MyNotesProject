package ru.axetta.ecafe.processor.core.push.model;

public class BenefitData extends AbstractPushData{


    private Integer actionType;
    private String benefitCategoryName;
    private String occurredAt;
    private String endAt;
    private String account;
    private String personId;

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getBenefitCategoryName() {
        return benefitCategoryName;
    }

    public void setBenefitCategoryName(String benefitCategoryName) {
        this.benefitCategoryName = benefitCategoryName;
    }

    public String getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
