/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.addpayment;

import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.CurrentPositionsManager;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class AddPaymentDeletePage extends BasicPage {
    public void removeAddPayment(Session session, Long id) throws Exception {
        AddPayment addPayment = (AddPayment) session.get(AddPayment.class, id);
        CurrentPositionsManager.deleteAddPayment(session, addPayment);
    }
}
