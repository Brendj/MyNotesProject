/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.subscription;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfSubscriptionFee;

/**
 * Created by IntelliJ IDEA.
 * User: Marat
 * Date: 15.07.2010
 * Time: 15:25:11
 * To change this template use File | Settings | File Templates.
 */
public interface SubscriptionFeeManager {

    void addSubcriptionFee(Long idOfClient, CompositeIdOfSubscriptionFee idOfSubscriptionFee) throws Exception;

}
