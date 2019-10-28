/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.org.SettingService;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzd;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.SettingType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingItemSyncPOJO;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSection;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSyncPOJO;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingsRequest;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
        Set<Long> friendlyOrgsid = OrgUtils.getFriendlyOrgIds(sourceOrg);
        friendlyOrgsid.add(sourceOrg.getIdOfOrg());

        //Проставляем флаг, что последние заявки по ЛП отправлены
        DAOUtils.updateOrgHaveNewLP(sourceOrg, false);

        List<RequestsEzd> requestsEzds = DAOUtils.getAllGoodRequestEZD(session, friendlyOrgsid, maxVersionFromARM);

        for (RequestsEzd requestsEzd : requestsEzds) {
            GoodRequestEZDSyncPOJO goodRequestEZDSyncPOJO = new GoodRequestEZDSyncPOJO();

            goodRequestEZDSyncPOJO.setIdOfOrg(requestsEzd.getIdOfOrg().intValue());

            Org org = (Org) session.load(Org.class, requestsEzd.getIdOfOrg());

            goodRequestEZDSyncPOJO.setGuid(org.getGuid());
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
