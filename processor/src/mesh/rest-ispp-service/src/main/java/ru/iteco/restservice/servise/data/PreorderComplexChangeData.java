package ru.iteco.restservice.servise.data;

import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;

import java.util.Date;

public class PreorderComplexChangeData {
    private Client client;
    private Date startDate;
    private Date endDate;
    private PreorderMobileGroupOnCreateType mobileGroupOnCreate;

    public PreorderComplexChangeData(Client client, Date startDate, Date endDate, PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mobileGroupOnCreate = mobileGroupOnCreate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public PreorderMobileGroupOnCreateType getMobileGroupOnCreate() {
        return mobileGroupOnCreate;
    }

    public void setMobileGroupOnCreate(PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.mobileGroupOnCreate = mobileGroupOnCreate;
    }
}
