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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

            List<RegistryChangeItemParam> registryChangeItemParams;

            for (RegistryChange c : changes) {
                registryChangeItemV2 = new RegistryChangeItemV2();
                registryChangeItemParams = new ArrayList<RegistryChangeItemParam>();

                RegistryChangeItemParam r0 = new RegistryChangeItemParam("idOfOrg", c.getIdOfOrg().toString());
                registryChangeItemParams.add(r0);

                RegistryChangeItemParam r1 = new RegistryChangeItemParam("idOfMigrateOrgTo",  c.getIdOfMigrateOrgTo() == null ? "-1" : c.getIdOfMigrateOrgTo().toString());
                registryChangeItemParams.add(r1);

                RegistryChangeItemParam r2 = new RegistryChangeItemParam("idOfMigrateOrgFrom", c.getIdOfMigrateOrgFrom() == null ? "-1" : c.getIdOfMigrateOrgFrom().toString());
                registryChangeItemParams.add(r2);

                RegistryChangeItemParam r3 = new RegistryChangeItemParam("createDate", c.getCreateDate().toString());
                registryChangeItemParams.add(r3);

                RegistryChangeItemParam r4 = new RegistryChangeItemParam("idOfRegistryChange", c.getIdOfRegistryChange().toString());
                registryChangeItemParams.add(r4);

                RegistryChangeItemParam r5 = new RegistryChangeItemParam("clientGUID", c.getClientGUID());
                registryChangeItemParams.add(r5);

                RegistryChangeItemParam r6 = new RegistryChangeItemParam("firstName", c.getFirstName());
                registryChangeItemParams.add(r6);

                RegistryChangeItemParam r7 = new RegistryChangeItemParam("secondName", c.getSecondName());
                registryChangeItemParams.add(r7);

                RegistryChangeItemParam r8 = new RegistryChangeItemParam("surname", c.getSurname());
                registryChangeItemParams.add(r8);

                RegistryChangeItemParam r9 = new RegistryChangeItemParam("groupName", c.getGroupName());
                registryChangeItemParams.add(r9);

                RegistryChangeItemParam r10 = new RegistryChangeItemParam("firstNameFrom", c.getFirstNameFrom());
                registryChangeItemParams.add(r10);

                RegistryChangeItemParam r11 = new RegistryChangeItemParam("secondNameFrom", c.getSecondNameFrom());
                registryChangeItemParams.add(r11);

                RegistryChangeItemParam r12 = new RegistryChangeItemParam("surnameFrom", c.getSurnameFrom());
                registryChangeItemParams.add(r12);

                RegistryChangeItemParam r13 = new RegistryChangeItemParam("groupNameFrom", c.getGroupNameFrom());
                registryChangeItemParams.add(r13);

                RegistryChangeItemParam r14 = new RegistryChangeItemParam("idOfClient", c.getIdOfClient() == null ? "-1" : c.getIdOfClient().toString());
                registryChangeItemParams.add(r14);

                RegistryChangeItemParam r15 = new RegistryChangeItemParam("operation", c.getOperation().toString());
                registryChangeItemParams.add(r15);

                RegistryChangeItemParam r16 = new RegistryChangeItemParam("applied", c.getApplied().toString());
                registryChangeItemParams.add(r16);

                RegistryChangeItemParam r17 = new RegistryChangeItemParam("error", c.getError());
                registryChangeItemParams.add(r17);

                RegistryChangeItemParam r18 = new RegistryChangeItemParam("gender", c.getGender() == null ? "" : c.getGender().toString());
                registryChangeItemParams.add(r18);

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

                RegistryChangeItemParam r19 = new RegistryChangeItemParam("birthDate", c.getBirthDate() == null ? "" : dateTimeFormat.format(new Date(c.getBirthDate())));
                registryChangeItemParams.add(r19);

                RegistryChangeItemParam r20 = new RegistryChangeItemParam("newDiscounts", c.getNewDiscounts() == null ? "" : c.getNewDiscounts());
                registryChangeItemParams.add(r20);

                RegistryChangeItemParam r21 = new RegistryChangeItemParam("genderFrom", c.getGenderFrom() == null ? "" : c.getGenderFrom().toString());
                registryChangeItemParams.add(r21);

                RegistryChangeItemParam r22 = new RegistryChangeItemParam("birthDateFrom", c.getBirthDateFrom() == null ? "" : c.getBirthDateFrom().toString());
                registryChangeItemParams.add(r22);

                RegistryChangeItemParam r23 = new RegistryChangeItemParam("oldDiscounts", c.getOldDiscounts()== null ? "" : c.getOldDiscounts());
                registryChangeItemParams.add(r23);

                RegistryChangeItemParam r24 = new RegistryChangeItemParam("guardiansCount", c.getGuardiansCount() == null ? "" : c.getGuardiansCount().toString());
                registryChangeItemParams.add(r24);

                RegistryChangeItemParam r25 = new RegistryChangeItemParam("ageTypeGroup", c.getAgeTypeGroup() == null ? "" : c.getAgeTypeGroup());
                registryChangeItemParams.add(r25);

                RegistryChangeItemParam r26 = new RegistryChangeItemParam("ageTypeGroupFrom", c.getAgeTypeGroupFrom() == null ? "" : c.getAgeTypeGroupFrom());
                registryChangeItemParams.add(r26);

                RegistryChangeItemParam r27 = new RegistryChangeItemParam("benefitDSZN", c.getBenefitDSZN() == null ? "" : c.getBenefitDSZN());
                registryChangeItemParams.add(r27);

                RegistryChangeItemParam r28 = new RegistryChangeItemParam("benefitDSZNFrom", c.getBenefitDSZNFrom() == null ? "" : c.getBenefitDSZNFrom());
                registryChangeItemParams.add(r28);

                registryChangeItemV2.setList(registryChangeItemParams);

                itemParams.add(registryChangeItemV2);
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

    public List<RegistryChangeItemV2> refreshRegistryChangeItemsV2(long idOfOrg) throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).syncClientsWithRegistry(idOfOrg,false, new StringBuffer(), true);
            return loadRegistryChangeItemsV2(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
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

            //boolean authPassed = false;
            for (Long idOfRegistryChange : changesList) {
                /*if(!authPassed) {
                    RegistryChange change = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).getRegistryChange(idOfRegistryChange);
                    authPassed = true;
                } */
                try {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).applyRegistryChange(idOfRegistryChange, fullNameValidation);
                    result.add(new RegistryChangeCallback(idOfRegistryChange, ""));
                } catch (Exception e1) {
                    //if(e1 instanceof ClientAlreadyExistException) {
                    logger.error("Error sverka: ", e1);
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