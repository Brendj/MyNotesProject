/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzd;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSection;

import java.util.List;
import java.util.Set;

public class GoodRequestEZDProcessor extends AbstractProcessor<OrgSettingSection> {
    private final GoodRequestEZDRequest goodRequestEZDRequest;
    private final Long idOfOrg;
    private static final Logger logger = LoggerFactory.getLogger(GoodRequestEZDProcessor.class);

    public GoodRequestEZDProcessor(Session session, GoodRequestEZDRequest goodRequestEZDRequest, Long idOfOrg) {
        super(session);
        this.goodRequestEZDRequest = goodRequestEZDRequest;
        this.idOfOrg = idOfOrg;
    }

    @Override
    public GoodRequestEZDSection process() throws Exception {
        Long maxVersionFromARM = goodRequestEZDRequest.getMaxVersion();

        //Собираем данные для ответа
        GoodRequestEZDSection section = new GoodRequestEZDSection();

        Org sourceOrg = (Org) session.load(Org.class, idOfOrg);
        Set<Long> friendlyOrgsid = DAOReadonlyService.getInstance().findFriendlyOrgsIdsAsSet(idOfOrg);
        friendlyOrgsid.add(sourceOrg.getIdOfOrg());

        //Проставляем флаг, что последние заявки по ЛП отправлены
        DAOService.getInstance().applyHaveNewLPForOrg(sourceOrg.getIdOfOrg(), false);

        List<RequestsEzd> requestsEzds = DAOUtils.getAllGoodRequestEZD(session, friendlyOrgsid, maxVersionFromARM);

        for (RequestsEzd requestsEzd : requestsEzds) {
            GoodRequestEZDSyncPOJO goodRequestEZDSyncPOJO = new GoodRequestEZDSyncPOJO();

            goodRequestEZDSyncPOJO.setIdOfOrg(requestsEzd.getIdOfOrg().intValue());

            goodRequestEZDSyncPOJO.setGuid(requestsEzd.getGuid());
            goodRequestEZDSyncPOJO.setGroupName(requestsEzd.getGroupname());
            goodRequestEZDSyncPOJO.setDate(requestsEzd.getDateappointment());
            goodRequestEZDSyncPOJO.setComplexId(requestsEzd.getIdofcomplex().intValue());
            goodRequestEZDSyncPOJO.setComplexName(requestsEzd.getComplexname());
            goodRequestEZDSyncPOJO.setCount(requestsEzd.getComplexcount());
            goodRequestEZDSyncPOJO.setUser(requestsEzd.getUsername());
            goodRequestEZDSyncPOJO.setVersion((long)requestsEzd.getVersionrecord());
            section.getItems().add(goodRequestEZDSyncPOJO);
        }
        return section;
    }
}
