/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.RegistryChange;
import ru.axetta.ecafe.processor.core.persistence.RegistryChangeError;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.BadOrgGuidsException;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.internal.front.items.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        return loadRegistryChangeItems(idOfOrg, revisionDate, null, null);
    }


    public List<RegistryChangeItem> loadRegistryChangeItems(long idOfOrg, long revisionDate,
                                                            Integer actionFilter, String nameFilter) {
        try {
            List<RegistryChangeItem> items = new ArrayList<RegistryChangeItem>();
            List<RegistryChange> changes = DAOService.getInstance().getLastRegistryChanges(idOfOrg, revisionDate, actionFilter, nameFilter);
            for (RegistryChange c : changes) {
                RegistryChangeItem i = new RegistryChangeItem(c.getIdOfOrg(),
                        c.getIdOfMigrateOrgTo() == null ? -1L : c.getIdOfMigrateOrgTo(),
                        c.getIdOfMigrateOrgFrom() == null ? -1L : c.getIdOfMigrateOrgFrom(),
                        c.getCreateDate(), c.getIdOfRegistryChange(),
                        c.getClientGUID(), c.getFirstName(), c.getSecondName(),
                        c.getSurname(), c.getGroupName(), c.getFirstNameFrom(),
                        c.getSecondNameFrom(), c.getSurnameFrom(), c.getGroupNameFrom(),
                        c.getIdOfClient() == null ? -1L : c.getIdOfClient(),
                        c.getOperation(), c.getApplied(), c.getError());
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

    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2(long idOfOrg, long revisionDate) {
        return loadRegistryChangeItemsV2(idOfOrg, revisionDate, null, null);
    }


    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2(long idOfOrg, long revisionDate, Integer actionFilter,
            String nameFilter) {

        try {
            List<RegistryChangeItemV2> itemParams = new ArrayList<RegistryChangeItemV2>();

            List<RegistryChange> changes = DAOService.getInstance()
                    .getLastRegistryChanges(idOfOrg, revisionDate, actionFilter, nameFilter);

            RegistryChangeItemV2 registryChangeItemV2;

            for (RegistryChange c : changes) {
            }

            return itemParams;
        } catch (FrontController.FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            logger.error("Failed to load registry change items form database", e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<RegistryChangeRevisionItem> loadRegistryChangeRevisions(long idOfOrg) {
        try {
            List queryResult = DAOService.getInstance().getRegistryChangeRevisions(idOfOrg);
            if(queryResult == null || queryResult.size() < 1) {
                return Collections.EMPTY_LIST;
            }
            List<RegistryChangeRevisionItem> result = new ArrayList<RegistryChangeRevisionItem>();
            for(Object o : queryResult) {
                Object entry [] = (Object[]) o;
                long date = (Long) entry[0];
                int type = (Integer) entry[1];
                result.add(new RegistryChangeRevisionItem(date, type));
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to load registry change revisions list", e);
        }
        return Collections.EMPTY_LIST;
    }

    public List<RegistryChangeItem> refreshRegistryChangeItems(long idOfOrg) throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).syncClientsWithRegistry(idOfOrg,false, new StringBuffer(), true);
            return loadRegistryChangeItems(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
        } catch (BadOrgGuidsException eGuid) {
            throw eGuid;
        }
        catch (Exception e) {
            logger.error("Failed to refresh registry change items", e);
        }
        return Collections.EMPTY_LIST;
    }

    public List<RegistryChangeCallback> proceedRegitryChangeItem(List<Long> changesList,
                                            int operation,
                                            boolean fullNameValidation) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }

        List<RegistryChangeCallback> result = new ArrayList<RegistryChangeCallback>();
        try {
            if(changesList == null || changesList.size() < 1) {
                return Collections.EMPTY_LIST;
            }

            boolean authPassed = false;
            for (Long idOfRegistryChange : changesList) {
                if(!authPassed) {
                    RegistryChange change = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).getRegistryChange(idOfRegistryChange);
                    authPassed = true;
                }
                try {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).applyRegistryChange(idOfRegistryChange, fullNameValidation);
                    result.add(new RegistryChangeCallback(idOfRegistryChange, ""));
                } catch (Exception e1) {
                    //if(e1 instanceof ClientAlreadyExistException) {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).setChangeError(
                            idOfRegistryChange, e1);
                    //}
                    result.add(new RegistryChangeCallback(idOfRegistryChange, e1.getMessage()));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to commit registry change item", e);
            //return "При подтверждении изменения из Реестров, произошла ошибка: " + e.getMessage();
        }
        return result;
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