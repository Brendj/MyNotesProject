/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 03.02.2020.
 */
public class PlanOrdersRestrictionsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PlanOrdersRestrictionsProcessor.class);
    private final PlanOrdersRestrictionsRequest planOrdersRestrictionsRequest;
    protected final Session session;

    public PlanOrdersRestrictionsProcessor(Session persistenceSession, PlanOrdersRestrictionsRequest planOrdersRestrictionsRequest) {
        this.session = persistenceSession;
        this.planOrdersRestrictionsRequest = planOrdersRestrictionsRequest;
    }

    public PlanOrdersRestrictions process() {
        PlanOrdersRestrictions result = new PlanOrdersRestrictions();
        Long nextVersion = DAOUtils.nextVersionByTableWithoutLock(session, "cf_plan_orders_restrictions");
        for (PlanOrdersRestrictionItem item : planOrdersRestrictionsRequest.getItems()) {
            if (!StringUtils.isEmpty(item.getErrorMessage())) {
                logger.error(String.format("Error in PRI item %s : %s", item, item.getErrorMessage()));
                continue;
            }
            try {
                PlanOrdersRestriction planOrdersRestriction = DAOUtils
                        .findPlanOrdersRestriction(session, item.getIdOfClient(), item.getIdOfOrg(), item.getComplexId());
                if (planOrdersRestriction == null) {
                    planOrdersRestriction = new PlanOrdersRestriction();
                }
                planOrdersRestriction.setIdOfClient(item.getIdOfClient());
                planOrdersRestriction.setIdOfOrgOnCreate(item.getIdOfOrg());
                planOrdersRestriction.setArmComplexId(item.getComplexId());
                planOrdersRestriction.setComplexName(item.getComplexName());
                planOrdersRestriction.setIdOfConfigurationProoviderOnCreate(item.getIdOfConfigarationProvider());
                planOrdersRestriction.setPlanOrdersRestrictionType(item.getPlanType());
                planOrdersRestriction.setDeletedState(item.getDeletedState());
                planOrdersRestriction.setVersion(nextVersion);
                planOrdersRestriction.setLastUpdate(new Date());
                session.saveOrUpdate(planOrdersRestriction);
            } catch (Exception e) {
                logger.error("Error saving planOrdersRestriction item = " + item.toString(), e);
            }
        }
        session.flush();

        List<PlanOrdersRestrictionItem> items = new ArrayList<>();
        List<PlanOrdersRestriction> list = DAOUtils.findPlanOrdersRestrictionSinceVersion(session, planOrdersRestrictionsRequest.getMaxVersion(),
                planOrdersRestrictionsRequest.getOrgOwner());
        for (PlanOrdersRestriction planOrdersRestriction : list) {
            PlanOrdersRestrictionItem item = new PlanOrdersRestrictionItem(planOrdersRestriction.getIdOfClient(), planOrdersRestriction.getIdOfOrgOnCreate(),
                    planOrdersRestriction.getIdOfClient(), planOrdersRestriction.getComplexName(), planOrdersRestriction.getArmComplexId(),
                    planOrdersRestriction.getPlanOrdersRestrictionType(), planOrdersRestriction.getVersion(), planOrdersRestriction.getDeletedState(), "");
            items.add(item);
        }
        result.setItems(items);
        return result;
    }

}
