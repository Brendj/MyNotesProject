/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodConfirmResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service.SchoolApiApplicationForFoodService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import java.util.Date;

@RestController
@RequestMapping(value = "/school/api/v1/applicationForFood", produces = "application/json")
public class ApplicationForFoodRestController extends BaseSchoolApiController {
    private final SchoolApiApplicationForFoodService service;
    public ApplicationForFoodRestController(SchoolApiApplicationForFoodService service) {this.service = service;}

    @PutMapping(value = "/{id}/confirmDocuments", consumes = "application/json")
    public ResponseEntity<?> confirmDocuments(@PathVariable("id") Long id) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        AplicationForFoodConfirmDocumentsResponse response = service.confirmDocuments(id, getUser());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}/decline", consumes = "application/json")
    public ResponseEntity<?> decline(@PathVariable("id") Long id, @RequestParam("docOrderDate") Long docOrderDate,
                                     @RequestParam("docOrderId") String docOrderId) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ApplicationForFoodDeclineResponse response =
                service.decline(id, new Date(docOrderDate), docOrderId, getUser());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}/confirm", consumes = "application/json")
    public ResponseEntity<?> confirm(@PathVariable("id") Long id, @RequestParam("docOrderDate") Long docOrderDate,
                                     @RequestParam("docOrderId") String docOrderId,
                                     @RequestParam("discountStartDate") Long discountStartDate,
                                     @RequestParam("discountEndDate") Long discountEndDate) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ApplicationForFoodConfirmResponse response =
                service.confirm(id, new Date(docOrderDate), docOrderId, new Date(discountStartDate), new Date(discountEndDate), getUser());
        return ResponseEntity.ok().body(response);
    }


}
