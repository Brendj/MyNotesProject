/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.pos;

import ru.axetta.ecafe.processor.core.persistence.POS;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class SelectedPosGroupPage extends BasicWorkspacePage {
    private String name;

    public String getName() {
        return name;
    }

    public void fill(Session session, Long idOfPos) throws Exception {
        POS pos = (POS) session.load(POS.class, idOfPos);
        this.name = pos.getName();
    }

}
