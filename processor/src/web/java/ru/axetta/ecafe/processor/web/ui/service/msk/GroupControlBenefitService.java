/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 19.02.15
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class GroupControlBenefitService {

    private static final Logger logger = LoggerFactory.getLogger(GroupControlBenefitService.class);

    // Находит клиента по Л/с
    public Client findClientByContractId(Long contractId, Session session) {
        Client client = DAOUtils.findClientByContractId(session, contractId);
        return client;
    }



}
