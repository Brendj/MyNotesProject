/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.RegistryChange;
import ru.axetta.ecafe.processor.core.persistence.RegistryChangeError;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeErrorItem;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.02.14
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class FrontControllerProcessor {
    private final Logger logger = LoggerFactory.getLogger(FrontControllerProcessor.class);


    public List<RegistryChangeItem> loadRegistryChangeItems(long idOfOrg,
                                                            long revisionDate) {
        try {
            List<RegistryChangeItem> items = new ArrayList<RegistryChangeItem>();
            List<RegistryChange> changes = DAOService.getInstance().getLastRegistryChanges(idOfOrg, revisionDate);
            for (RegistryChange c : changes) {
                RegistryChangeItem i = new RegistryChangeItem(c.getIdOfOrg(),
                        c.getIdOfMigrateOrgTo() == null ? -1L : c.getIdOfMigrateOrgTo(),
                        c.getIdOfMigrateOrgFrom() == null ? -1L : c.getIdOfMigrateOrgFrom(),
                        c.getCreateDate(), c.getIdOfRegistryChange(),
                        c.getClientGUID(), c.getFirstName(), c.getSecondName(),
                        c.getSurname(), c.getGroupName(), c.getFirstNameFrom(),
                        c.getSecondNameFrom(), c.getSurnameFrom(), c.getGroupNameFrom(),
                        c.getIdOfClient() == null ? -1L : c.getIdOfClient(),
                        c.getOperation(), c.getApplied());
                items.add(i);
            }
            return items;
        } catch(FrontController.FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            logger.error("Failed to load registry change items form database", e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<Long> loadRegistryChangeRevisions(long idOfOrg) {
        try {
            return DAOService.getInstance().getRegistryChangeRevisions(idOfOrg);
        } catch (Exception e) {
            logger.error("Failed to load registry change revisions list", e);
        }
        return Collections.EMPTY_LIST;
    }

    public List<RegistryChangeItem> refreshRegistryChangeItems(long idOfOrg) {
        try {
            RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).syncClientsWithRegistry(idOfOrg,false, new StringBuffer(), true);
            return loadRegistryChangeItems(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
        } catch (Exception e) {
            logger.error("Failed to refresh registry change items", e);
        }
        return Collections.EMPTY_LIST;
    }

    public String proceedRegitryChangeItem(List<Long> changesList,
                                            int operation,
                                            boolean fullNameValidation) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return null;
        }

        try {
            if(changesList == null || changesList.size() < 1) {
                return null;
            }

            boolean authPassed = false;
            for (Long idOfRegistryChange : changesList) {
                if(!authPassed) {
                    RegistryChange change = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).getRegistryChange(idOfRegistryChange);
                    authPassed = true;
                }
                RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).applyRegistryChange(idOfRegistryChange, fullNameValidation);
            }
        } catch (Exception e) {
            logger.error("Failed to commit registry change item", e);
            return "При подтверждении изменения из Реестров, произошла ошибка: " + e.getMessage();
        }

        return null;
    }

    public List<RegistryChangeErrorItem> loadRegistryChangeErrorItems(long idOfOrg) {
        try {
            List<RegistryChangeErrorItem> items = new ArrayList<RegistryChangeErrorItem>();
            List<RegistryChangeError> errors = DAOService.getInstance().getRegistryChangeErrors(idOfOrg);
            for (RegistryChangeError e : errors) {
                String orgName = DAOService.getInstance().findOrById(e.getIdOfOrg()).getOfficialName();
                RegistryChangeErrorItem i = new RegistryChangeErrorItem(e.getIdOfRegistryChangeError(), e.getIdOfOrg(),
                        e.getRevisionCreateDate(), e.getCreateDate(),
                        e.getCommentCreateDate(), e.getError(),
                        e.getErrorDetail(), e.getComment(),
                        orgName, e.getCommentAuthor());
                items.add(i);
            }
            return items;
        } catch (Exception e) {
            logger.error("Failed to load registry change error items from database", e);
            return Collections.EMPTY_LIST;
        }
    }

    public String addRegistryChangeError(long idOfOrg,
            long revisionDate,
            String error,
            String errorDetails) {
        try {
            DAOService.getInstance().addRegistryChangeError(idOfOrg, revisionDate, error, errorDetails);
            return null;
        } catch (Exception e) {
            logger.error("Failed to add comment for registry change error", e);
            return e.getMessage();
        }
    }

    public String commentRegistryChangeError(long idOfRegistryChangeError,
            String comment,
            String author) {
        try {
            RegistryChangeError e = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).getRegistryChangeError(idOfRegistryChangeError);
            DAOService.getInstance().addRegistryChangeErrorComment(idOfRegistryChangeError, comment, author);
            return null;
        } catch (Exception e) {
            logger.error("Failed to add comment for registry change error", e);
            return e.getMessage();
        }
    }
}