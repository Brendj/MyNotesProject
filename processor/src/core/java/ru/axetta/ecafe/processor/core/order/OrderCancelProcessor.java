/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.order;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfOrder;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 16.04.2010
 * Time: 15:44:13
 * To change this template use File | Settings | File Templates.
 */
public interface OrderCancelProcessor {

    void cancelOrder(CompositeIdOfOrder compositeIdOfOrder) throws Exception;

}
