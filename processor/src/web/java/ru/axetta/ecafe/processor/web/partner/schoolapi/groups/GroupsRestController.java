/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service.SchoolApiClientGroupsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

@RestController
@RequestMapping(value = "/school/api/v1/groups", produces = "application/json")
public class GroupsRestController extends BaseSchoolApiController {
    private final SchoolApiClientGroupsService service;

    public GroupsRestController(SchoolApiClientGroupsService service) {this.service = service;}

    @PostMapping(value = "/{id}/org/{orgId}/subgroups", consumes = "application/json")
    public ResponseEntity<?> createMiddleGroup(@PathVariable("id") Long id, @PathVariable("orgId") Long orgId,
                                               MiddleGroupRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        MiddleGroupResponse response = service.createMiddleGroup(id, orgId, request, getUser());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}/org/{orgId}/subgroups", consumes = "application/json")
    public ResponseEntity<?> updateMiddleGroup(@PathVariable("id") Long id, @PathVariable("orgId") Long orgId,
                                               MiddleGroupRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        MiddleGroupResponse response = service.updateMiddleGroup(id, orgId, request, getUser());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/subgroups/{id}")
    public ResponseEntity<?> deleteMiddleGroup(@PathVariable("id") Long id) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        MiddleGroupResponse response = service.deleteMiddleGroup(id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}/org/{orgId}", consumes = "application/json")
    public ResponseEntity<?> updateGroup(@PathVariable("id") Long id, @PathVariable("orgId") Long orgId,
                                         GroupClientsUpdateRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        GroupClientsUpdateResponse response = service.updateGroup(id, orgId, request, getUser());
        return ResponseEntity.ok().body(response);
    }


}
