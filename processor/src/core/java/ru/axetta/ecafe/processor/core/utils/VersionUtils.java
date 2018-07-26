/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

public class VersionUtils {

    private static final String CARD_REGISTRATION_CLIENT_VERSION_OPTION = "ecafe.processor.card.registration.client.version";
    private static final String CARD_REGISTRATION_CLIENT_VERSION_OPTION_DEFAULT_VALUE = "2.7.86.1";

    public static int compareClientVersionForRegisterCard(Session session, Long idOfOrg) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String clientVersionProperty =
                runtimeContext.getPropertiesValue(CARD_REGISTRATION_CLIENT_VERSION_OPTION, CARD_REGISTRATION_CLIENT_VERSION_OPTION_DEFAULT_VALUE);
        OrgSync orgSync = DAOUtils.getOrgSyncForOrg(session, idOfOrg);
        if (null == orgSync) {
            return -1;
        }
        String clientVersion = orgSync.getClientVersion();
        VersionV2 propVersion = new VersionV2(clientVersionProperty);
        VersionV2 clVersion = new VersionV2(clientVersion);
        return clVersion.compareTo(propVersion);
    }
}
