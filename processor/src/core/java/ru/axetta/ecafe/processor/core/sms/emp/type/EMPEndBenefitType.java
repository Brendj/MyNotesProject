/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.BenefitService;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

import java.util.Map;

public class EMPEndBenefitType extends EMPAbstractEventType {

    public EMPEndBenefitType(String [] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.END_BENEFIT;
        this.getParameters().put(BenefitService.DATE_END_DISCOUNT,
                findValueInParams(new String[]{BenefitService.DATE_END_DISCOUNT}, values));
        this.getParameters().put(BenefitService.DTISZN_CODE,
                findValueInParams(new String[]{BenefitService.DTISZN_CODE}, values));
        this.getParameters().put(BenefitService.DTISZN_DESCRIPTION,
                findValueInParams(new String[]{BenefitService.DTISZN_DESCRIPTION}, values));
        this.getParameters().put(BenefitService.ID_DISCOUNT_INFO,
                findValueInParams(new String[]{BenefitService.ID_DISCOUNT_INFO}, values));
    }

    @Override
    public void parse(Client client) {
        parseClientSimpleInfo(client, type);
    }

    @Override
    public void parse(Client child, Client guardian) {
        parseChildAndGuardianInfo(child, guardian, type);
    }
}