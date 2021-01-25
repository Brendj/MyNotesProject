/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;

import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchoolApiClientGroupsServiceImpl implements SchoolApiClientGroupsService {

    @Autowired
    CreateMiddleGroupCommand middleGroupCommand;
    @Autowired
    UpdateGroupCommand updateGroupCommand;

    @Override
    public MiddleGroupResponse createMiddleGroup(Long id, Long orgId, MiddleGroupRequest request) {
        return middleGroupCommand.createGroup(id, orgId, request);
    }

    @Override
    public MiddleGroupResponse updateMiddleGroup(Long id, Long orgId, MiddleGroupRequest request) {
        return middleGroupCommand.updateGroup(id, orgId, request);
    }

    @Override
    public GroupClientsUpdateResponse updateGroup(Long id, Long orgId, GroupClientsUpdateRequest request) {
        return updateGroupCommand.updateGroup(id, orgId, request);
    }


}
