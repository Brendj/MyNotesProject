/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.employee;

import ru.iteco.restservice.controller.base.BaseConverter;
import ru.iteco.restservice.controller.employee.responsedto.EmployeeResponseDTO;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.servise.EnterEventsService;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class EmployeeConverter extends BaseConverter<EmployeeResponseDTO, Client> {
    public final EnterEventsService enterEventsService;

    public EmployeeConverter(EnterEventsService enterEventsService) {
        this.enterEventsService = enterEventsService;
    }

    @Override
    public EmployeeResponseDTO toDTO(@NotNull Client c) {
        Long contractId = c.getContractId();
        String firstName = c.getPerson().getFirstName();
        String middleName = c.getPerson().getMiddleName();
        String lastname = c.getPerson().getLastName();
        String grade = c.getClientGroup().getGroupName();
        String orgType = c.getOrg().getType().toString();
        String orgName = c.getOrg().getShortName();
        Long balance = c.getBalance();
        String address = c.getOrg().getShortAddress();
        Boolean isInside = enterEventsService.clientIsInside(c.getIdOfClient());
        Boolean specialMenu = c.getSpecialMenu() != null && c.getSpecialMenu().equals(1);
        String gender = c.getGender().toString();

        Boolean preorderAllowed =
                c.getPreorderFlag() != null && c.getPreorderFlag().getAllowedPreorder() != null && c.getPreorderFlag().getAllowedPreorder().equals(1);

        return new EmployeeResponseDTO(contractId, balance, firstName, middleName, lastname, grade, orgType, orgName,
                address, isInside, specialMenu, gender, preorderAllowed);
    }
}
