/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.BenefitService;

import java.util.Map;

public class EMPRefusalPreferentialFoodType extends EMPAbstractEventType {

    public EMPRefusalPreferentialFoodType(String [] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.REFUSAL_PREFERENTIAL_FOOD;
        this.getParameters().put(BenefitService.SERVICE_NUMBER,
                findValueInParams(new String[]{BenefitService.SERVICE_NUMBER}, values));
        this.getParameters().put(BenefitService.DTISZN_CODE,
                findValueInParams(new String[]{BenefitService.DTISZN_CODE}, values));
        this.getParameters().put(BenefitService.DTISZN_DESCRIPTION,
                findValueInParams(new String[]{BenefitService.DTISZN_DESCRIPTION}, values));
        this.getParameters().put(BenefitService.DATE,
                findValueInParams(new String[]{BenefitService.DATE}, values));
    }

    @Override
    public void parse(Client client) {
        parseClientSimpleInfo(client);
    }

    @Override
    public void parse(Client child, Client guardian) {
        parseChildAndGuardianInfo(child, guardian);
    }
}