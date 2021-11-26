/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.ResPaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.service.SchoolApiPaymentsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;

import java.util.List;

@RestController
@RequestMapping(value = "/school/api/v1/payments", produces = "application/json")
public class PaymentRestConroller extends BaseSchoolApiController {
    private final SchoolApiPaymentsService service;

    public PaymentRestConroller(SchoolApiPaymentsService service) {this.service = service;}

    @PostMapping(value = "/orgs/{id}", consumes = "application/json")
    public ResponseEntity<?> registerPayments(@PathVariable("id") Long idOfOrg, List<PaymentDTO> payments) {
        List<ResPaymentDTO> response = service.registerPayments(idOfOrg, payments, getUser());
        return ResponseEntity.ok().body(response);
    }

}
