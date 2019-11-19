/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.persistence.EMIAS;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSection;


import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EmiasProcessor extends AbstractProcessor<OrgSettingSection> {

    private final EmiasRequest emiasRequest;
    private static final Logger logger = LoggerFactory.getLogger(EmiasProcessor.class);

    public EmiasProcessor(Session session, EmiasRequest emiasRequest) {
        super(session);
        this.emiasRequest = emiasRequest;
    }

    @Override
    public FullEmiasAnswerForARM process() throws Exception {
        Long maxVersionFromARM = emiasRequest.getMaxVersion();
        //Apply change from ARM
        FullEmiasAnswerForARM fullEmiasAnswerForARM = new FullEmiasAnswerForARM();
        fullEmiasAnswerForARM.setMaxVersionArm(emiasRequest.getMaxVersion());
        for (EMIASSyncFromARMPOJO pojo : emiasRequest.getItems()) {
            EMIASSyncFromAnswerARMPOJO emiasSyncFromAnswerARMPOJO = new EMIASSyncFromAnswerARMPOJO();
            try {
                emiasSyncFromAnswerARMPOJO.setIdEventEMIAS(pojo.getIdEventEMIAS());
                List<EMIAS> emias = DAOUtils.getEmiasbyidEventEMIAS(pojo.getIdEventEMIAS(), session);
                Long version = 0L;
                for (EMIAS oneEmias : emias) {
                    version = 0L;
                    boolean isChanged = true;
                    if (oneEmias.getAccepted() != null && pojo.getAccepted() != null) {
                        if (oneEmias.getAccepted().equals(pojo.getAccepted())) {
                            isChanged = false;
                        }
                    }
                    if (isChanged) {
                        version = DAOUtils.getMaxVersionOfEmias(session) + 1;
                        oneEmias.setAccepted(pojo.getAccepted());
                        oneEmias.setUpdateDate(new Date());
                        oneEmias.setVersion(version);
                        session.persist(oneEmias);
                    }
                }
                if (!version.equals(0L))
                    emiasSyncFromAnswerARMPOJO.setVersion(version);
                emiasSyncFromAnswerARMPOJO.setErrormessage("");
            } catch (Exception e)
            {
                emiasSyncFromAnswerARMPOJO.setErrormessage(e.getMessage());
            }
            fullEmiasAnswerForARM.getItemsArm().add(emiasSyncFromAnswerARMPOJO);
        }


        //Build section for response
        List<EMIAS> EMIASFromDB = DAOUtils.getEmiasForMaxVersion(maxVersionFromARM, session);

        //fullEmiasAnswerForARM.setMaxVersion(maxVersionFromDB);
        for (EMIAS emias : EMIASFromDB) {

            EMIASSyncPOJO emiasSyncPOJO = new EMIASSyncPOJO();
            emiasSyncPOJO.setGuid(emias.getGuid());
            emiasSyncPOJO.setIdEventEMIAS(emias.getIdEventEMIAS());
            emiasSyncPOJO.setTypeEventEMIAS(emias.getTypeEventEMIAS());
            emiasSyncPOJO.setDateLiberate(emias.getDateLiberate());
            emiasSyncPOJO.setStartDateLiberate(emias.getStartDateLiberate());
            emiasSyncPOJO.setEndDateLiberate(emias.getEndDateLiberate());
            emiasSyncPOJO.setCreateDate(emias.getCreateDate());
            emiasSyncPOJO.setUpdateDate(emias.getUpdateDate());
            emiasSyncPOJO.setAccepted(emias.getAccepted());
            emiasSyncPOJO.setDeletedemiasid(emias.getDeletedemiasid());
            emiasSyncPOJO.setVersion(emias.getVersion());
            fullEmiasAnswerForARM.getItems().add(emiasSyncPOJO);
        }
        return fullEmiasAnswerForARM;
    }
}
