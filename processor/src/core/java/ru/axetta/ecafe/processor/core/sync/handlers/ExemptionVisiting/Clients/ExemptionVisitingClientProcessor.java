/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting.Clients;

import ru.axetta.ecafe.processor.core.persistence.EMIAS;
import ru.axetta.ecafe.processor.core.persistence.EMIASbyDay;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.emias.*;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSection;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExemptionVisitingClientProcessor extends AbstractProcessor<OrgSettingSection> {

    private final ExemptionVisitingClientRequest exemptionVisitingClientRequest;
    private Long idOfOrg;
    private static final Logger logger = LoggerFactory.getLogger(ExemptionVisitingClientProcessor.class);

    public ExemptionVisitingClientProcessor(Session session, ExemptionVisitingClientRequest exemptionVisitingClientRequest, Long idOfOrg) {
        super(session);
        this.exemptionVisitingClientRequest = exemptionVisitingClientRequest;
        this.idOfOrg = idOfOrg;
    }

    @Override
    public ResExemptionVisitingClient process() throws Exception {
        Long maxVersionFromARM = exemptionVisitingClientRequest.getMaxVersion();
        ResExemptionVisitingClient resExemptionVisitingClient = new ResExemptionVisitingClient();
        //Собираем данные по всем дружественным корпусам
        List<Long> friendlyOrg = new ArrayList<>();
        friendlyOrg.add(idOfOrg);
        Org org = (Org) session.load(Org.class, idOfOrg);
        for (Org friendlyOrgs : org.getFriendlyOrg()) {
            friendlyOrg.add(friendlyOrgs.getIdOfOrg());
        }

        List<EMIASbyDay> emiasByDays = DAOReadonlyService.getInstance().getEmiasbyDayForOrgs(maxVersionFromARM, friendlyOrg);

        resExemptionVisitingClient.setVersion(0L);
        for (EMIASbyDay emiaSbyDay : emiasByDays) {
            //Устанавливаем версию
            if (emiaSbyDay.getVersion() > resExemptionVisitingClient.getVersion())
                resExemptionVisitingClient.setVersion(emiaSbyDay.getVersion());
            //Новые данные
            ExemptionVisitingClientDates exemptionVisitingClientDates1 = new ExemptionVisitingClientDates();
            exemptionVisitingClientDates1.setDate(emiaSbyDay.getDate());
            exemptionVisitingClientDates1.setEat(emiaSbyDay.getEat());
            boolean findclient = false;
            for (ExemptionVisitingClientPOjO emiasSyncPOJO: resExemptionVisitingClient.getItems())
            {
                //Если такой клиент уже найден
                if (emiasSyncPOJO.getIdOfClient().equals(emiaSbyDay.getIdOfClient()))
                {

                    emiasSyncPOJO.getItemList().add(exemptionVisitingClientDates1);
                    findclient = true;
                    break;
                }
            }
            //Если такой клиент не найден
            if (!findclient)
            {
                ExemptionVisitingClientPOjO exemptionVisitingClientPOjO = new ExemptionVisitingClientPOjO();
                exemptionVisitingClientPOjO.setIdOfClient(emiaSbyDay.getIdOfClient());
                List<ExemptionVisitingClientDates> itemList = new ArrayList<>();
                itemList.add(exemptionVisitingClientDates1);
                exemptionVisitingClientPOjO.setItemList(itemList);
                resExemptionVisitingClient.getItems().add(exemptionVisitingClientPOjO);
            }
        }
        return resExemptionVisitingClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
