/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.kafka.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.iteco.msp.enums.OrderDetailFRationType;
import ru.iteco.msp.models.dto.SupplyMSPOrders;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplyEvent {
    private String person_id;
    private String benefit_code;
    private String benefit_category_code;
    private String benefit_category_name;
    private String data;
    private String date;
    private Long amount;
    private Long organization_id;

    public static SupplyEvent build(SupplyMSPOrders order) {
        SupplyEvent event = new SupplyEvent();

        event.person_id = order.getMeshGUID();
        event.benefit_code = order.getCode() == null ? null : order.getCode().toString();
        if(order.getDtsznCodes() != null){
            if(order.getDtsznCodes().equals("0")){  // Иное
                event.benefit_category_name = "Иное";
            } else {
                event.benefit_category_code = order.getDtsznCodes();
            }
        } else {
            if(order.getCategoryName().equals("Начальные классы")) {
                event.benefit_category_name = order.getCategoryName();
            } else {
                List<String> discounts = new LinkedList<>(Arrays.asList(StringUtils.split(order.getCategoryName(),
                        ";")));
                discounts.remove("Начальные классы");

                event.benefit_category_name = StringUtils.join(discounts, ";");
            }
        }
        event.data = order.getDetails() + OrderDetailFRationType.getByCode(order.getFration());
        event.date = order.getOrderDate().toString();
        event.amount = order.getrSum();
        event.organization_id = order.getOrganizationId();

        return event;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getBenefit_code() {
        return benefit_code;
    }

    public void setBenefit_code(String benefit_code) {
        this.benefit_code = benefit_code;
    }

    public String getBenefit_category_code() {
        return benefit_category_code;
    }

    public void setBenefit_category_code(String benefit_category_code) {
        this.benefit_category_code = benefit_category_code;
    }

    public String getBenefit_category_name() {
        return benefit_category_name;
    }

    public void setBenefit_category_name(String benefit_category_name) {
        this.benefit_category_name = benefit_category_name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Long organization_id) {
        this.organization_id = organization_id;
    }
}
