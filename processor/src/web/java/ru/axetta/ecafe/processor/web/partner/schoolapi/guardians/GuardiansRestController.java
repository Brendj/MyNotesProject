/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.DeleteGuardianResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service.SchoolApiGuardiansService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

@RestController
@RequestMapping(value = "/school/api/v1/guardians", produces = "application/json")
public class GuardiansRestController extends BaseSchoolApiController {
    private final SchoolApiGuardiansService service;

    public GuardiansRestController(SchoolApiGuardiansService service) {this.service = service;}

    @DeleteMapping("/{idOfRecord}")
    public ResponseEntity<?> deleteGuardian(@PathVariable("idOfRecord") Long idOfRecord) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        DeleteGuardianResponse response = service.deleteGuardian(idOfRecord, getUser());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createOrUpdateGuardian(CreateOrUpdateGuardianRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        CreateOrUpdateGuardianResponse response = service.createOrUpdateGuardian(request, getUser());
        return ResponseEntity.ok().body(response);
    }

}
