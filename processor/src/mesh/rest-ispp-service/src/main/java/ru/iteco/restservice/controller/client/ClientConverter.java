/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import ru.iteco.restservice.controller.base.BaseConverter;
import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseErrorDTO;
import ru.iteco.restservice.model.CategoryDiscount;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.PreorderFlag;
import ru.iteco.restservice.servise.EnterEventsService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Component
public class ClientConverter extends BaseConverter<ClientResponseDTO, Client> {

    public final EnterEventsService enterEventsService;

    public ClientConverter(EnterEventsService enterEventsService) {
        this.enterEventsService = enterEventsService;
    }

    @Override
    public ClientResponseDTO toDTO(@NotNull Client c) {
        List<String> discountsName = new LinkedList<>();

        c.getDiscounts()
                .stream()
                .map(CategoryDiscount::getCategoryName)
                .forEach(discountsName::add);

        Long contractId = c.getContractId();
        String firstName = c.getPerson().getFirstName();
        String middleName = c.getPerson().getMiddleName();
        String lastname = c.getPerson().getLastName();
        String grade = c.getClientGroup().getGroupName();
        String orgType = c.getOrg().getType().toString();
        String orgName = c.getOrg().getShortNameInfoService();
        Long balance = c.getBalance();
        String address = c.getOrg().getShortAddress();
        Boolean isInside = enterEventsService.clientIsInside(c.getIdOfClient());
        String meshGUID = c.getMeshGuid();
        Boolean specialMenu = c.getSpecialMenu() != null && c.getSpecialMenu().equals(1);
        String gender = c.getGender().toString();
        String categoryDiscount = StringUtils.join(discountsName, ",");

        Boolean preorderAllowed = false;
        for (PreorderFlag pf : c.getPreorderFlag()) {
            if (pf.getAllowedPreorder() != null && pf.getAllowedPreorder().equals(1)) {
                preorderAllowed = true;
            }
        }

        Long limit = c.getExpenditureLimit();

        return new ClientResponseDTO(contractId, balance, firstName, lastname, middleName, grade, orgName, orgType,
                address, isInside, meshGUID, specialMenu, gender, categoryDiscount, preorderAllowed, limit);

    }

    public List<NotificationResponseErrorDTO> toDTOs(@NotNull List<Long> codes) {
        List<NotificationResponseErrorDTO> result = new LinkedList<>();
        for (Long code: codes)
        {
            NotificationResponseErrorDTO notificationResponseErrorDTO = new NotificationResponseErrorDTO();
            notificationResponseErrorDTO.setSettingsCode(code);
            result.add(notificationResponseErrorDTO);
        }
        return result;
    }
}
