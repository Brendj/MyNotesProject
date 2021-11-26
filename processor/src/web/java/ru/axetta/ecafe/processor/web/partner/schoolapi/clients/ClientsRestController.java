/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service.SchoolApiClientsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

@RestController
@RequestMapping(value = "/school/api/v1/clients", produces = "application/json")
public class ClientsRestController extends BaseSchoolApiController {
    private final SchoolApiClientsService service;

    public ClientsRestController(SchoolApiClientsService service) {this.service = service;}

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateClient(@PathVariable("id") Long idOfClient, ClientUpdateItem request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientUpdateResult response = service.updateClient(idOfClient, request, getUser());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/move", consumes = "application/json")
    public ResponseEntity<?> moveClients(ClientsUpdateRequest moveClientsRequest) throws WebApplicationException {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientsUpdateResponse response = service.moveClients(moveClientsRequest.getUpdateClients(), getUser());
        return ResponseEntity.ok().body(response);
    }


    @PutMapping(value = "/plan/exclude", consumes = "application/json")
    public ResponseEntity<?> planExclude(ClientsUpdateRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientsUpdateResponse response = service.excludeClientsFromPlan(request.getUpdateClients());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/discounts", consumes = "application/json")
    public ResponseEntity<?> updateDiscounts(ClientsUpdateRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientsUpdateResponse response = service.updateClientsDiscounts(request.getUpdateClients(), getUser());
        return ResponseEntity.ok().body(response);
    }

}
