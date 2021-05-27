/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.ResPaymentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class SchoolApiPaymentsServiceImpl implements SchoolApiPaymentsService {
    @Autowired
    private RegisterPaymentCommand registerPaymentCommand;

    @Override
    public List<ResPaymentDTO> registerPayments(Long idOfOrg, List<PaymentDTO> payments, User user) {
        return registerPaymentCommand.registerPayments(idOfOrg, payments, user);
    }
}
