/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic.MeshClientCardRefService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.CardBlockService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class CardServicesPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {
    private static final Logger log = LoggerFactory.getLogger(CardServicesPage.class);
    private List<Long> idOfOrgList;
    private String filter = "";
    private Boolean allFriendlyOrgs = false;
    private MeshClientCardRefService meshClientCardRefService;

    public CardServicesPage(){
        this.meshClientCardRefService = RuntimeContext.getAppContext().getBean(MeshClientCardRefService.class);
    }

    @Override
    public String getPageFilename() {
        return "service/card_services_page";
    }

    public void autoBlockCards() {
        RuntimeContext.getAppContext().getBean(CardBlockService.class).run();
        printMessage("Операция блокировки ЭИ завершена");
    }

    // Не использую реализацию в OnlineReport чтоб можно было вызывать этот метод для разных фильтров ОО в других блоках
    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new LinkedList<>();
            if (orgMap.isEmpty()) {
                filter = "Не выбрано";
            } else {
                idOfOrgList.addAll(orgMap.keySet());
                filter = StringUtils.join(idOfOrgList, "; ");
            }
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    public void sendCardsToMESH() {
        if(CollectionUtils.isEmpty(idOfOrgList)){
            printError("Не выбраны организации для отправки");
            return;
        }

        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            Set<Org> targetOrgs = new HashSet<>();
            for (Long idOfOrg : idOfOrgList) {
                if (allFriendlyOrgs || CollectionUtils.isNotEmpty(DAOUtils.findFriendlyOrgIds(session, idOfOrg))) {
                    targetOrgs.addAll(DAOUtils.findAllFriendlyOrgs(session, idOfOrg));
                } else {
                    targetOrgs.add(DAOUtils.findOrg(session, idOfOrg));
                }
            }
            session.close();

            List<Org> targetOrgsList = new LinkedList<>(targetOrgs);
            meshClientCardRefService.registryCardInMESHByOrgs(targetOrgsList);
        } catch (Exception e){
          log.error("Error when try send Card to MESH: ", e);
          printError("Ошибка при отправке: " + e.getMessage());
        } finally {
            HibernateUtils.close(session, log);
        }
    }
}
