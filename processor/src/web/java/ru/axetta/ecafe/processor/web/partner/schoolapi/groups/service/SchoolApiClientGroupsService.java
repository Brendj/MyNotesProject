/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;


import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupResponse;

public interface SchoolApiClientGroupsService {
    MiddleGroupResponse createMiddleGroup(Long id, Long orgId, MiddleGroupRequest request);
    MiddleGroupResponse updateMiddleGroup(Long id, Long orgId, MiddleGroupRequest request);
    GroupClientsUpdateResponse updateGroup(Long id, Long orgId, GroupClientsUpdateRequest request);
}
