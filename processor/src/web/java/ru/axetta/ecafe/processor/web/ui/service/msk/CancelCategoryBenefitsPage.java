/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 04.03.15
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class CancelCategoryBenefitsPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(CancelCategoryBenefitsPage.class);

    private List<GroupControlBenefitsItems> groupControlBenefitsItemsList;

    @Override
    public String getPageFilename() {
        return "service/msk/cancel_category_benefits";
    }

    public void onShow() throws Exception {

    }

    public void cancelCategoryBenefitsGenerate(RuntimeContext runtimeContext) throws Exception {
        CancelCategoryBenefitsService cancelCategoryBenefitsService = new CancelCategoryBenefitsService();

        groupControlBenefitsItemsList = new ArrayList<GroupControlBenefitsItems>();

        Session persistenceSession = runtimeContext.createPersistenceSession();
        Transaction persistenceTransaction = null;
        try {
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Client> clientList = cancelCategoryBenefitsService.getAllBenefitClients(persistenceSession);

            if (!clientList.isEmpty()) {
                Long rowNum = 0L;
                for (Client client : clientList) {
                    String clientGroup;
                    if (client.getClientGroup() != null) {
                        clientGroup = client.getClientGroup().getGroupName();
                    } else {
                        clientGroup = "";
                    }
                    GroupControlBenefitsItems groupControlBenefitsItems = new GroupControlBenefitsItems(++rowNum,
                            client.getOrg().getShortName(), clientGroup, client.getPerson().getSurname(),
                            client.getPerson().getFirstName(), client.getPerson().getSecondName(),
                            client.getContractId().toString(), "", "");

                    groupControlBenefitsItemsList.add(groupControlBenefitsItems);
                }
            }
            persistenceTransaction.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(persistenceTransaction, logger);
            ex.printStackTrace();
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public List<GroupControlBenefitsItems> getGroupControlBenefitsItemsList() {
        return groupControlBenefitsItemsList;
    }

    public void setGroupControlBenefitsItemsList(List<GroupControlBenefitsItems> groupControlBenefitsItemsList) {
        this.groupControlBenefitsItemsList = groupControlBenefitsItemsList;
    }
}
