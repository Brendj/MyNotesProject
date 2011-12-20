/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.pos;

import ru.axetta.ecafe.processor.core.persistence.POS;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.classic.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class PosDeletePage extends BasicPage {
    public void removePos(Session session, Long id) throws Exception {
        POS pos = (POS) session.get(POS.class, id);
        session.delete(pos);
    }
}
