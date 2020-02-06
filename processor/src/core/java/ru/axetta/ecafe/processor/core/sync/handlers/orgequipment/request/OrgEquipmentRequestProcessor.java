/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OrgEquipmentRequestProcessor extends AbstractProcessor<ResOrgEquipmentRequest> {

    private static final Logger logger = LoggerFactory.getLogger(OrgEquipmentRequestProcessor.class);
    private final OrgEquipmentRequest orgEquipmentRequest;

    public OrgEquipmentRequestProcessor(Session persistenceSession, OrgEquipmentRequest orgEquipmentRequest){
            //OrgEquipment orgEquipment) {
        super(persistenceSession);
        this.orgEquipmentRequest = orgEquipmentRequest;
    }

    public ResOrgEquipmentRequest process() {
        ResOrgEquipmentRequest result = new ResOrgEquipmentRequest();

        List<ResOrgEquipmentRequestItem> items = new ArrayList<ResOrgEquipmentRequestItem>();
        try {
            ResOrgEquipmentRequestItem resItem = null;
            boolean errorFound = false;
            Long orgOwner = orgEquipmentRequest.getOrgOwner();
            Long nextVersion = DAOUtils.nextVersionByOrgEquipmentRequest(session);
            for (OrgEquipmentRequestItem item : orgEquipmentRequest.getItems()) {
                String moduleType = item.getType();
                switch (moduleType) {
                    case "MT":
                        errorFound = !item.getResCode().equals(OrgEquipmentRequestItem.ERROR_CODE_ALL_OK);
                        if (!errorFound) {
                            OrgEquipmentRequestMTItem mtItem = (OrgEquipmentRequestMTItem)item;
                            ru.axetta.ecafe.processor.core.persistence.OrgEquipment orgEquipment = DAOUtils
                                    .getOrgEquipmentRequestByOrg(session, orgOwner);
                            if (null == orgEquipment) {
                                orgEquipment = new ru.axetta.ecafe.processor.core.persistence.OrgEquipment(
                                        mtItem.getValue(), mtItem.getInstallStatus(), mtItem.getLastUpdate());
                                orgEquipment.setVersion(nextVersion);
                                session.save(orgEquipment);
                            } else {
                                orgEquipment.setModuleType(mtItem.getValue());
                                orgEquipment.setInstallStatus(mtItem.getInstallStatus());
                                orgEquipment.setLastUpdateForModuleType(mtItem.getLastUpdate());
                                orgEquipment.setVersion(nextVersion);
                                session.update(orgEquipment);
                            }

                            resItem = new ResOrgEquipmentRequestMTItem(orgEquipment, mtItem.getResCode());
                        } else {
                            resItem = new ResOrgEquipmentRequestMTItem(item.getResCode(), item.getErrorMessage());
                        }
                        items.add(resItem);
                        session.flush();
                }
            }

        } catch (Exception e) {
            logger.error("Error saving OrgEquipmentRequest", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    /*public OrgEquipmentRequestData processData() throws Exception {
        OrgEquipmentRequestData result = new OrgEquipmentRequestData();
        List<ResOrgEquipmentRequestItem> items = new ArrayList<ResOrgEquipmentRequestItem>();
        ResOrgEquipmentRequestItem resItem;
        List<ru.axetta.ecafe.processor.core.persistence.OrgEquipment> list = DAOUtils
                .getOrgEquipmentsForOrgSinceVersion(session, orgEquipmentRequest.getOrgOwner(),
                        orgEquipmentRequest.getMaxVersion());
        for (ru.axetta.ecafe.processor.core.persistence.OrgEquipment orgEquipment : list) {
            if (orgEquipment != null) {
                resItem = new ResOrgEquipmentRequestMTItem(orgEquipment);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }*/
}
