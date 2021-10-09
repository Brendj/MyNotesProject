/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.kafka.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.iteco.msp.enums.AssignOperationType;
import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;
import ru.iteco.msp.models.CodeMSP;
import ru.iteco.msp.service.DiscountsService;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignEvent {
    private String person_id;
    private String benefit_code;
    private String benefit_category_code;
    private String benefit_category_name;
    private String action_code;
    private String begin_at;
    private String end_at;
    private Long organization_id;

    public static AssignEvent build(CategoryDiscount categoryDiscount, Client client,
            AssignOperationType type, ClientDTSZNDiscountInfo info, String beginDate){
        AssignEvent event = new AssignEvent();
        event.setPerson_id(client.getMeshGuid());

        CodeMSP msp = categoryDiscount.getMSPByClient(client);
        event.setBenefit_code(msp == null ? null : msp.getCode().toString());
        if(categoryDiscount.getCategoryDiscountDTSZN() != null){
            if(categoryDiscount.getCategoryDiscountDTSZN().getCode().equals(0)){  // Иное
                event.setBenefit_category_name("Иное");
            } else {
                event.setBenefit_category_code(categoryDiscount.getCategoryDiscountDTSZN().getCode().toString());
            }
        } else {
            event.setBenefit_category_name(categoryDiscount.getCategoryName());
        }
        event.setBegin_at(beginDate);

        if(info != null && DiscountsService.DISCOUNTS_CODES_WITH_END_DATE.contains(info.getDTISZNCode())){
            event.setEnd_at(info.getDateEnd().toString());
        }
        event.setAction_code(type.getCode());
        event.setOrganization_id(client.getOrg().getOrganizationIdFromNSI());

        return event;
    }

    public Long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Long organization_id) {
        this.organization_id = organization_id;
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

    public String getAction_code() {
        return action_code;
    }

    public void setAction_code(String action_code) {
        this.action_code = action_code;
    }

    public String getBegin_at() {
        return begin_at;
    }

    public void setBegin_at(String begin_at) {
        this.begin_at = begin_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }
}