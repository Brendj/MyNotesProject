/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

import ru.axetta.ecafe.processor.core.RuntimeContext;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 16:11:45
 * To change this template use File | Settings | File Templates.
 */
public interface PayPointProcessor {

    PayPointResponse processPartnerPayPointRequest(RuntimeContext runtimeContext, PayPointRequest request) throws InvalidRequestException;

}
