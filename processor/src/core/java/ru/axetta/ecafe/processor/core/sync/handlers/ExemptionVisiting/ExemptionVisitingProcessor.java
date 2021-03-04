/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.persistence.EMIAS;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSection;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExemptionVisitingProcessor extends AbstractProcessor<OrgSettingSection> {

    private final ExemptionVisitingRequest exemptionVisitingRequest;
    private Long idOfOrg;
    private static final Logger logger = LoggerFactory.getLogger(
            ExemptionVisitingProcessor.class);

    public ExemptionVisitingProcessor(Session session, ExemptionVisitingRequest exemptionVisitingRequest, Long idOfOrg) {
        super(session);
        this.exemptionVisitingRequest = exemptionVisitingRequest;
        this.idOfOrg = idOfOrg;
    }

    @Override
    public FullExemptionVisitingAnswerForARM process() throws Exception {
        Long maxVersionFromARM = exemptionVisitingRequest.getMaxVersion();
        //Apply change from ARM
        FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = new FullExemptionVisitingAnswerForARM();
        fullExemptionVisitingAnswerForARM.setMaxVersionArm(exemptionVisitingRequest.getMaxVersion());
        for (ExemptionVisitingSyncFromARMPOJO pojo : exemptionVisitingRequest.getItems()) {
            ExemptionVisitingSyncFromAnswerARMPOJO exemptionVisitingSyncFromAnswerARMPOJO = new ExemptionVisitingSyncFromAnswerARMPOJO();
            try {
                exemptionVisitingSyncFromAnswerARMPOJO.setIdExemption(pojo.getIdExemption());
                EMIAS emias = (EMIAS) session.get(EMIAS.class, pojo.getIdExemption());
                Long version = 0L;
                if (emias.getAccepted() != null && pojo.getAccepted() != null) {
                    if (!emias.getAccepted().equals(pojo.getAccepted())) {
                        version = DAOUtils.getMaxVersionOfExemptionVisiting(session) + 1;
                        emias.setAccepted(pojo.getAccepted());
                        emias.setUpdateDate(new Date());
                        emias.setVersion(version);
                        session.persist(emias);
                    }
                }

                if (!version.equals(0L))
                    exemptionVisitingSyncFromAnswerARMPOJO.setVersion(version);
                exemptionVisitingSyncFromAnswerARMPOJO.setErrormessage("");
            } catch (Exception e)
            {
                exemptionVisitingSyncFromAnswerARMPOJO.setErrormessage(e.getMessage());
            }
            fullExemptionVisitingAnswerForARM.getItemsArm().add(exemptionVisitingSyncFromAnswerARMPOJO);
        }


        //Build section for response

        //Собираем данные по всем дружественным корпусам
        List<Long> friendlyOrg = new ArrayList<>();
        friendlyOrg.add(idOfOrg);
        Org org = (Org) session.load(Org.class, idOfOrg);
        for (Org friendlyOrgs : org.getFriendlyOrg()) {
            friendlyOrg.add(friendlyOrgs.getIdOfOrg());
        }

        List<EMIAS> EMIASFromDB = DAOReadonlyService.getInstance().getExemptionVisitingForMaxVersionAndIdOrg(maxVersionFromARM, friendlyOrg);

        for (EMIAS emias : EMIASFromDB) {

            ExemptionVisitingSyncPOJO exemptionVisitingSyncPOJO = new ExemptionVisitingSyncPOJO();
            exemptionVisitingSyncPOJO.setIdExemption(emias.getIdOfEMIAS());
            exemptionVisitingSyncPOJO.setArchive(emias.getArchive());
            exemptionVisitingSyncPOJO.setHazard_level_id(emias.getHazard_level_id());
            exemptionVisitingSyncPOJO.setMeshguid(emias.getGuid());
            exemptionVisitingSyncPOJO.setStartDateLiberate(emias.getStartDateLiberate());
            exemptionVisitingSyncPOJO.setEndDateLiberate(emias.getEndDateLiberate());
            exemptionVisitingSyncPOJO.setDateLiberate(emias.getDateLiberate());
            exemptionVisitingSyncPOJO.setAccepted(emias.getAccepted());
            exemptionVisitingSyncPOJO.setVersion(emias.getVersion());
            exemptionVisitingSyncPOJO.setCreateDate(emias.getCreateDate());
            exemptionVisitingSyncPOJO.setUpdateDate(emias.getUpdateDate());
            fullExemptionVisitingAnswerForARM.getItems().add(exemptionVisitingSyncPOJO);
        }
        return fullExemptionVisitingAnswerForARM;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
