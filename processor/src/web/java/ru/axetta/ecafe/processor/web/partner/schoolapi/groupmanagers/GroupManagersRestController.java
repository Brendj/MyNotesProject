/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto.ClientGroupManagerDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service.ClientGroupManagersService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import java.util.List;

@RestController
@RequestMapping(value = "/school/api/v1/groupmanagers", produces = "application/json")
public class GroupManagersRestController extends BaseSchoolApiController {
    private final ClientGroupManagersService service;

    public GroupManagersRestController(ClientGroupManagersService service) {this.service = service;}


    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> attachedGroups(@RequestBody List<ClientGroupManagerDTO> groupClientManagers) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        List<ClientGroupManager> clientGroupManagers = service.attachedGroups(groupClientManagers);
        List<ClientGroupManagerDTO> response = ClientGroupManagerDTO.fromCollection(clientGroupManagers);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> dettachedGroup(@PathVariable("id") Long idOfClientGroupManager) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        service.dettachedGroup(idOfClientGroupManager);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<?> dettachedGroups(@RequestParam("id") final List<Long> ids) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        service.dettachedGroups(ids);
        return ResponseEntity.ok().build();
    }

}
