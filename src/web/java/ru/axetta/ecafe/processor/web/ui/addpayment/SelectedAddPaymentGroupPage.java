/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.addpayment;

import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 25.11.11
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class SelectedAddPaymentGroupPage extends BasicWorkspacePage {
    private String name;

    public String getName() {
        return name;
    }

    public void fill(Session session, Long idOfAddPayment) throws Exception {
        AddPayment addPayment = (AddPayment) session.load(AddPayment.class, idOfAddPayment);
        this.name = addPayment.getContragentPayer().getContragentName() + " - "
                    + addPayment.getContragentReceiver().getContragentName();
    }

}
