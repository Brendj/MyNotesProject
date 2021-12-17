/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.org.owners;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.13
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgOwnerProcessor extends AbstractProcessor<OrgOwnerData> {

    private final Long idOfOrg;

    public OrgOwnerProcessor(Session session, Long idOfOrg) {
        super(session);
        this.idOfOrg = idOfOrg;
    }

    @Override
    public OrgOwnerData process() throws Exception {
        List<OrgOwner> orgOwners = new ArrayList<OrgOwner>(
                DAOUtils.getOrgSourceByMenuExchangeRule(session, idOfOrg, false)
        );
        Org org = DAOUtils.findOrg(session, idOfOrg);
         /*
         * TODO: ранее высылался как поставщик сама организация
         * TODO: при возникновении проблем написать пояснения
         * TODO: Зачем синхронизируемой организации являться поставщиком
         * */
        if(CollectionUtils.isNotEmpty(orgOwners)){
            orgOwners.add(new OrgOwner(org.getIdOfOrg(),org.getShortName(),org.getOfficialName(), true));
        } else {
            orgOwners.add(new OrgOwner(org.getIdOfOrg(),org.getShortName(),org.getOfficialName(), false));
        }
        orgOwners.addAll(DAOUtils.getOrgSourceByMenuExchangeRule(session, idOfOrg, true));
        return new OrgOwnerData(orgOwners);
    }
}
