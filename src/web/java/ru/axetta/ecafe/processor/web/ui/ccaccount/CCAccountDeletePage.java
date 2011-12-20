/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.ccaccount;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfContragentClientAccount;
import ru.axetta.ecafe.processor.core.persistence.ContragentClientAccount;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CCAccountDeletePage extends BasicPage {

    public void removeCCAccount(Session session, CompositeIdOfContragentClientAccount id) throws Exception {
        ContragentClientAccount contragentClientAccount = (ContragentClientAccount) session
                .get(ContragentClientAccount.class, id);
        session.delete(contragentClientAccount);
    }

}