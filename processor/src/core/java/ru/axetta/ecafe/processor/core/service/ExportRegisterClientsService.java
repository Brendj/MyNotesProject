/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 17.12.12
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ExportRegisterClientsService {

    @Autowired
    MskNSIService nsiService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExportRegisterClientsService.class);
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_REGISTER_CL_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_REGISTER_CL_ON, "" + (on ? "1" : "0"));
    }


    private void setLastUpdateDate(Date date) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_REGISTER_CL_UPD_TIME, dateFormat.format(date));
    }


    private Date getLastUpdateDate() {
        try {
            String d = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_REGISTER_CL_UPD_TIME);
            if (d == null || d.length() < 1) {
                return new Date(0);
            }
            return dateFormat.parse(d);
        } catch (Exception e) {
            logger.error("Failed to parse date from options", e);
        }
        return new Date(0);
    }


    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }
        Date lastUpd = getLastUpdateDate();
        List<Org> orgs = DAOService.getInstance().getOrderedSynchOrgsList();
        for (Org org : orgs) {
            try {
                if (org.getTag() == null || org.getTag().toUpperCase().indexOf("СИНХРОНИЗАЦИЯ_РЕЕСТРЫ") < 0) {
                    continue;
                }
                loadClients(lastUpd, org);
            } catch (Exception e) {
                logger.error("Failed to add clients for " + org.getIdOfOrg() + " org", e);
            }
        }
        setLastUpdateDate(new Date(System.currentTimeMillis()));
    }


    public void loadClients(java.util.Date lastUpd, Org org) throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        List<MskNSIService.ExpandedPupilInfo> pupils = nsiService.getChangedClients(lastUpd, org);


        try {
            persistenceSession = runtimeContext.createPersistenceSession();
        } catch (Exception e) {
            throw e;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        for (MskNSIService.ExpandedPupilInfo pupil : pupils) {
            FieldProcessor.Config fieldConfig;
            boolean exists = ClientManager.existClient(persistenceSession, org, emptyIfNull(pupil.getFirstName()),
                    emptyIfNull(pupil.getFamilyName()), emptyIfNull(pupil.getSecondName()));
            //  Created пока всегда будет пустым, как только r-style сделает их, следует раскомментировать
            if (/*pupil.isCreated() && */!exists && !pupil.isDeleted()) {
                fieldConfig = new ClientManager.ClientFieldConfig();
            } else {
                fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
            }
            fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, pupil.getGuid());
            fieldConfig.setValue(ClientManager.FieldId.SURNAME, emptyIfNull(pupil.getFamilyName()));
            fieldConfig.setValue(ClientManager.FieldId.NAME, emptyIfNull(pupil.getFirstName()));
            fieldConfig.setValue(ClientManager.FieldId.SECONDNAME, emptyIfNull(pupil.getSecondName()));
            if (pupil.getGroup() != null) {
                fieldConfig.setValue(ClientManager.FieldId.GROUP, pupil.getGroup());
            }
            try {
                if (pupil.isDeleted()) {
                    DAOService.getInstance().bindClientToGroup (ClientManager.findClientByFullName(org,
                            (ClientManager.ClientFieldConfigForUpdate) fieldConfig),
                            ClientGroup.Predefined.CLIENT_LEAVING.getValue ());
                }
                //  Created пока всегда будет пустым, как только r-style сделает их, следует раскомментировать
                else if (/*pupil.isCreated() && */!exists) {
                    try {
                        fieldConfig.setValue(ClientManager.FieldId.COMMENTS, "{%Добавлено из Регистров " + date + "%}");
                        ClientManager
                                .registerClient(org.getIdOfOrg(), (ClientManager.ClientFieldConfig) fieldConfig, true);
                    } catch (Exception e) {
                    }
                } else {
                    ClientManager.modifyClient((ClientManager.ClientFieldConfigForUpdate) fieldConfig, org,
                            "{%Изменено из Регистров " + date + "%}");
                }
            } catch (Exception e) {
                // Не раскомментировать, очень много исключений будет из-за дублирования клиентов
                logger.error("Failed to add client for " + org.getIdOfOrg() + " org", e);
            }
        }
    }

    private String emptyIfNull(String str) {
        return str == null ? "" : str;
    }
}