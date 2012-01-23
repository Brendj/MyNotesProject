/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfOrder;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrderRemovePage extends BasicWorkspacePage {

    private Long idOfOrg;
    private Long idOfOrder;

    public String getPageFilename() {
        return "service/remove_order";
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public void fill(Session session) throws Exception {

    }

    public void removeOrder() throws Exception {
        CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(this.idOfOrg, this.idOfOrder);
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            runtimeContext.getOrderCancelProcessor().cancelOrder(compositeIdOfOrder);
        } finally {
            RuntimeContext.release(runtimeContext);
        }
    }
}