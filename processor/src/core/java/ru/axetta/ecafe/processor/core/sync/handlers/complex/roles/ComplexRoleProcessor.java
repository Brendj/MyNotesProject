/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.roles;

import ru.axetta.ecafe.processor.core.persistence.ComplexRole;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.07.13
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRoleProcessor extends AbstractProcessor<ComplexRoles> {

    public ComplexRoleProcessor(Session session) {
        super(session);
    }

    @Override
    public ComplexRoles process() throws Exception {
        List<ComplexRoleItem> complexRoleItemList = new ArrayList<ComplexRoleItem>();
        List<ComplexRole> complexRoleList = DAOReadonlyService.getInstance().findComplexRoles();
        for (ComplexRole complexRole: complexRoleList){
            complexRoleItemList.add(new ComplexRoleItem(complexRole));
        }
        return new ComplexRoles(complexRoleItemList);
    }
}
