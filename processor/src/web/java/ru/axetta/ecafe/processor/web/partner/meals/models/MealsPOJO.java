package ru.axetta.ecafe.processor.web.partner.meals.models;

import org.springframework.http.ResponseEntity;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;

import javax.ws.rs.core.Response;
import java.util.Date;

public class MealsPOJO {
    private ResponseEntity<?> responseEntity;
    private Long contractId;
    private Client client;
    private Long availableMoney;
    private Date from;
    private Date to;
    private Boolean sortDesc;
    private Long isppIdFoodbox;
    private Date onDate;
    private Boolean foodBoxAvailable;
    private FoodBoxPreorder foodBoxPreorder;
    private Boolean created;

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

    public ResponseEntity<?> getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<?> responseEntity) {
        this.responseEntity = responseEntity;
    }

    public FoodBoxPreorder getFoodBoxPreorder() {
        return foodBoxPreorder;
    }

    public void setFoodBoxPreorder(FoodBoxPreorder foodBoxPreorder) {
        this.foodBoxPreorder = foodBoxPreorder;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }
}
