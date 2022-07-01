package ru.axetta.ecafe.processor.web.partner.meals.models;

import ru.axetta.ecafe.processor.core.persistence.Client;

import javax.ws.rs.core.Response;
import java.util.Date;

public class MealsPOJO {
    private Response response;
    private Long contractId;
    private Client client;
    private Long availableMoney;
    private Date from;
    private Date to;
    private Boolean sortDesc;
    private Long isppIdFoodbox;
    private Date onDate;
    private Boolean foodBoxAvailable;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(Long availableMoney) {
        this.availableMoney = availableMoney;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Boolean getSortDesc() {
        return sortDesc;
    }

    public void setSortDesc(Boolean sortDesc) {
        this.sortDesc = sortDesc;
    }

    public Long getIsppIdFoodbox() {
        return isppIdFoodbox;
    }

    public void setIsppIdFoodbox(Long isppIdFoodbox) {
        this.isppIdFoodbox = isppIdFoodbox;
    }

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }

    public Boolean getFoodBoxAvailable() {
        return foodBoxAvailable;
    }

    public void setFoodBoxAvailable(Boolean foodBoxAvailable) {
        this.foodBoxAvailable = foodBoxAvailable;
    }
}
