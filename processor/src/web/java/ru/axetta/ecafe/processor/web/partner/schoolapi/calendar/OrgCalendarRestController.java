/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.DeleteOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.service.SchoolApiOrgCalendarService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

@RestController
@RequestMapping(value = "/school/api/v1/orgCalendar", produces = "application/json")
public class OrgCalendarRestController extends BaseSchoolApiController {
    private final SchoolApiOrgCalendarService service;

    public OrgCalendarRestController(SchoolApiOrgCalendarService service) {this.service = service;}

    @DeleteMapping("/{idOfRecord}/{idOfOrgRequester}")
    public ResponseEntity<?> deleteOrgCalendarDate(@PathVariable("idOfRecord") long idOfRecord,
                                                   @PathVariable("idOfOrgRequester") long idOfOrgRequester) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        DeleteOrgCalendarDateResponse response = service.deleteOrgCalendarDate(idOfRecord, idOfOrgRequester, getUser());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createOrUpdateOrgCalendarDate(@RequestBody CreateOrUpdateOrgCalendarDateRequest request) {
        if (!isWebArmAnyRole()) {throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);}
        CreateOrUpdateOrgCalendarDateResponse response = service.createOrUpdateOrgCalendarDate(request, getUser());
        return ResponseEntity.ok().body(response);
    }
}
