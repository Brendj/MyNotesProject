/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianHistory;
import ru.axetta.ecafe.processor.core.persistence.RegistryChange;
import ru.axetta.ecafe.processor.core.persistence.RegistryChangeError;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.web.internal.front.items.*;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.UnknownServiceException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.02.14
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class FrontControllerProcessor {
    private final Logger logger = LoggerFactory.getLogger(FrontControllerProcessor.class);
    private final ImportClientRegisterService importClientRegisterService = getImportClientRegisterService();
    private final int REGISTRY_CHANGE_ITEM_PARAM_ROWS_COUNT = 31; // Для инициализации массива в loadRegistryChangeItemsV2_ForClassName

    private ImportClientRegisterService getImportClientRegisterService() {
        try {
            if (RuntimeContext.RegistryType.isMsk()) {
                return RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class);
            } else if (RuntimeContext.RegistryType.isSpb()) {
                return RuntimeContext.getAppContext().getBean(ImportRegisterSpbClientsService.class);
            } else {
                throw new UnknownServiceException("Unknown RegistryType for ImportClientRegisterService");
            }
        } catch (Exception e){
            logger.error("Can't init ImportClientRegisterService, set as Unsupported. Exception:", e);
            return RuntimeContext.getAppContext().getBean(UnsupportedImportClientRegisterService.class);
        }
    }

    public List<RegistryChangeItem> loadRegistryChangeItems(long idOfOrg,
            long revisionDate) {
        return loadRegistryChangeItems_ForClassName(idOfOrg, revisionDate, null, null, "RegistryChange");
    }

    public List<RegistryChangeItem> loadRegistryChangeEmployeeItems(long idOfOrg,
            long revisionDate) {
        return loadRegistryChangeItems_ForClassName(idOfOrg, revisionDate, null, null, "RegistryChangeEmployee");
    }

    public List<RegistryChangeItem> loadRegistryChangeItems(long idOfOrg, long revisionDate,
            Integer actionFilter, String nameFilter) {
        return loadRegistryChangeItems_ForClassName(idOfOrg, revisionDate, actionFilter, nameFilter, "RegistryChange");
    }

    public List<RegistryChangeItem> loadRegistryChangeItems_ForClassName(long idOfOrg, long revisionDate,
                                                            Integer actionFilter, String nameFilter, String className) {
        try {
            List<RegistryChangeItem> items = new ArrayList<RegistryChangeItem>();
            List<RegistryChange> changes = DAOService.getInstance().getLastRegistryChanges(idOfOrg, revisionDate, actionFilter, nameFilter, className);
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

    public List<RegistryChangeItemV2> loadRegistryChangeItemsEmployeeV2(long idOfOrg, long revisionDate) {
        return loadRegistryChangeItemsEmployeeV2(idOfOrg, revisionDate, null, null);
    }

    public List<RegistryChangeItemV2> loadRegistryChangeItemsEmployeeV2(long idOfOrg, long revisionDate, Integer actionFilter,
            String nameFilter) {
        return loadRegistryChangeItemsV2_ForClassName(idOfOrg, revisionDate, actionFilter, nameFilter, "RegistryChangeEmployee");
    }

    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2_WithFullFIO(long idOfOrg, long revisionDate, Integer actionFilter,
            String lastName, String firstName, String patronymic) {
        return loadRegistryChangeItemsV2_ForClassName_WithFullFIO(idOfOrg, revisionDate, actionFilter, lastName, firstName, patronymic, "RegistryChange");
    }

    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2(long idOfOrg, long revisionDate, Integer actionFilter,
            String nameFilter) {
        return loadRegistryChangeItemsV2_ForClassName(idOfOrg, revisionDate, actionFilter, nameFilter, "RegistryChange");
    }

    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2_ForClassName_WithFullFIO(long idOfOrg, long revisionDate, Integer actionFilter,
            String lastName, String firstName, String patronymic, String className) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            List<RegistryChangeItemV2> itemParams = new LinkedList<>();

            List<RegistryChange> changes = DAOService.getInstance()
                    .getLastRegistryChanges_WithFullFIO(idOfOrg, revisionDate, actionFilter, lastName, firstName, patronymic, className);

            RegistryChangeItemV2 registryChangeItemV2;
            List<RegistryChangeItemParam> registryChangeItemParams;

            for (RegistryChange c : changes) {
                registryChangeItemV2 = new RegistryChangeItemV2();
                registryChangeItemParams = new ArrayList<>(REGISTRY_CHANGE_ITEM_PARAM_ROWS_COUNT);

                RegistryChangeItemParam r0 = new RegistryChangeItemParam("idOfOrg", c.getIdOfOrg().toString());
                registryChangeItemParams.add(r0);

                RegistryChangeItemParam r1 = new RegistryChangeItemParam("idOfMigrateOrgTo", c.getIdOfMigrateOrgTo() == null ? "-1" : c.getIdOfMigrateOrgTo().toString());
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

                RegistryChangeItemParam r19 = new RegistryChangeItemParam("birthDate", c.getBirthDate() == null ? "" : dateTimeFormat.format(new Date(c.getBirthDate())));
                registryChangeItemParams.add(r19);

                RegistryChangeItemParam r20 = new RegistryChangeItemParam("newDiscounts", c.getCheckBenefitsSafe() ? c.getNewDiscounts() == null ? "" : c.getNewDiscounts() : null);
                registryChangeItemParams.add(r20);

                RegistryChangeItemParam r21 = new RegistryChangeItemParam("genderFrom", c.getGenderFrom() == null ? "" : c.getGenderFrom().toString());
                registryChangeItemParams.add(r21);

                RegistryChangeItemParam r22 = new RegistryChangeItemParam("birthDateFrom", c.getBirthDateFrom() == null ? "" : dateTimeFormat.format(c.getBirthDateFrom()));
                registryChangeItemParams.add(r22);

                RegistryChangeItemParam r23 = new RegistryChangeItemParam("oldDiscounts", c.getCheckBenefitsSafe() ? c.getOldDiscounts()== null ? "" : c.getOldDiscounts() : null);
                registryChangeItemParams.add(r23);

                RegistryChangeItemParam r24 = new RegistryChangeItemParam("guardiansCount", c.getGuardiansCount() == null ? "" : c.getGuardiansCount().toString());
                registryChangeItemParams.add(r24);

                RegistryChangeItemParam r25 = new RegistryChangeItemParam("ageTypeGroup", c.getAgeTypeGroup() == null ? "" : c.getAgeTypeGroup());
                registryChangeItemParams.add(r25);

                RegistryChangeItemParam r26 = new RegistryChangeItemParam("ageTypeGroupFrom", c.getAgeTypeGroupFrom() == null ? "" : c.getAgeTypeGroupFrom());
                registryChangeItemParams.add(r26);

                RegistryChangeItemParam r27 = new RegistryChangeItemParam("benefitDSZN", c.getCheckBenefitsSafe() ? c.getBenefitDSZN() == null ? "" : c.getBenefitDSZN() : null);
                registryChangeItemParams.add(r27);

                RegistryChangeItemParam r28 = new RegistryChangeItemParam("benefitDSZNFrom", c.getCheckBenefitsSafe() ? c.getBenefitDSZNFrom() == null ? "" : c.getBenefitDSZNFrom() : null);
                registryChangeItemParams.add(r28);

                RegistryChangeItemParam r29 = new RegistryChangeItemParam("parallel", c.getParallel());
                registryChangeItemParams.add(r29);

                RegistryChangeItemParam r30 = new RegistryChangeItemParam("parallelFrom", c.getParallelFrom());
                registryChangeItemParams.add(r30);

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

    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2_ForClassName(long idOfOrg, long revisionDate, Integer actionFilter,
            String nameFilter, String className) {
        return loadRegistryChangeItemsV2_ForClassName_WithFullFIO(idOfOrg, revisionDate, actionFilter,
                nameFilter, null, null, className);
    }

    private List<RegistryChangeRevisionItem> loadRegistryChangeRevisionsByClassName(long idOfOrg, String className) {
        try {
            List<Object[]> queryResult = DAOService.getInstance().getRegistryChangeRevisions(idOfOrg, className);
            if(CollectionUtils.isEmpty(queryResult)) {
                return Collections.EMPTY_LIST;
            }
            List<RegistryChangeRevisionItem> result = new ArrayList<>();
            for(Object[] entry : queryResult) {
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

    public List<RegistryChangeRevisionItem> loadRegistryEmployeeChangeRevisions(long idOfOrg) {
        return loadRegistryChangeRevisionsByClassName(idOfOrg, "RegistryChangeEmployee");
    }

    public List<RegistryChangeRevisionItem> loadRegistryChangeRevisions(long idOfOrg) {
        return loadRegistryChangeRevisionsByClassName(idOfOrg, "RegistryChange");
    }

    public List<RegistryChangeItem> refreshRegistryChangeEmployeeItems(long idOfOrg) throws Exception {
        try {
                RuntimeContext.getAppContext().getBean("importRegisterEmployeeService", ImportRegisterMSKEmployeeService.class)
                        .syncEmployeesWithRegistry(idOfOrg, new StringBuffer());
            return loadRegistryChangeEmployeeItems(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
        } catch (BadOrgGuidsException | ServiceTemporaryUnavailableException | RegistryTimeDeltaException e) {
            logger.error("Failed to refresh registry change items", e);
            throw new FrontController.FrontControllerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to refresh registry change items", e);
        }
        return Collections.EMPTY_LIST;
    }

    public List<RegistryChangeItem> refreshRegistryChangeItems(long idOfOrg) throws Exception {
        try {
            importClientRegisterService.syncClientsWithRegistry(idOfOrg, false, new StringBuffer(), true);
            return loadRegistryChangeItems(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
        } catch (BadOrgGuidsException | ServiceTemporaryUnavailableException | RegistryTimeDeltaException e) {
            logger.error("Failed to refresh registry change items", e);
            throw new FrontController.FrontControllerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to refresh registry change items", e);
        }
        return Collections.EMPTY_LIST;
    }

    public List<RegistryChangeItemV2> refreshRegistryChangeItemsV2(long idOfOrg) throws Exception {
        importClientRegisterService.syncClientsWithRegistry(idOfOrg, false, new StringBuffer(), true);
        return loadRegistryChangeItemsV2(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
    }

    public List<RegistryChangeItemV2> refreshRegistryChangeEmployeeItemsV2(long idOfOrg) throws Exception {
        RuntimeContext.getAppContext().getBean("importRegisterEmployeeService", ImportRegisterMSKEmployeeService.class)
              .syncEmployeesWithRegistry(idOfOrg, new StringBuffer());
        return loadRegistryChangeItemsEmployeeV2(idOfOrg, -1L);   //  -1 значит последняя загрузка из Реестров
    }

    public List<RegistryChangeCallback> proceedRegistryChangeItem(List<Long> changesList, int operation,
            boolean fullNameValidation, ClientsMobileHistory clientsMobileHistory, ClientGuardianHistory clientGuardianHistory) {
        if (operation != RegistryChangeItem.APPLY_REGISTRY_CHANGE || CollectionUtils.isEmpty(changesList)) {
            return Collections.EMPTY_LIST;
        }

        List<RegistryChangeCallback> result = Collections.EMPTY_LIST;
        try {
            result = importClientRegisterService.applyRegistryChangeBatch(changesList, fullNameValidation,
                    null, clientsMobileHistory, clientGuardianHistory);
        } catch (Exception e) {
            logger.error("Failed to commit registry change item", e);
        }
        return result;
    }

    public List<RegistryChangeCallback> proceedRegistryEmployeeChangeItem(List<Long> changesList, int operation,
            boolean fullNameValidation, String groupName, ClientsMobileHistory clientsMobileHistory, ClientGuardianHistory clientGuardianHistory) {
        if (operation != RegistryChangeItem.APPLY_REGISTRY_CHANGE || CollectionUtils.isEmpty(changesList)) {
            return Collections.EMPTY_LIST;
        }

        List<RegistryChangeCallback> result = Collections.EMPTY_LIST;
        try {
            result = importClientRegisterService.applyRegistryChangeBatch(changesList, fullNameValidation, groupName,
                    clientsMobileHistory, clientGuardianHistory);
        } catch (Exception e) {
            logger.error("Failed to commit registry change item", e);
        }
        return result;
    }

    public List<RegistryChangeErrorItem> loadRegistryChangeEmployeeErrorItems(long idOfOrg) {
        return new ArrayList<>();
    }

    public List<RegistryChangeErrorItem> loadRegistryChangeErrorItems(long idOfOrg) {
        try {
            List<RegistryChangeErrorItem> items = new LinkedList<>();
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

    public String addRegistryChangeError(long idOfOrg, long revisionDate, String error, String errorDetails) {
        try {
            DAOService.getInstance().addRegistryChangeError(idOfOrg, revisionDate, error, errorDetails);
            return null;
        } catch (Exception e) {
            logger.error("Failed to add comment for registry change error", e);
            return e.getMessage();
        }
    }

    public String commentRegistryChangeError(long idOfRegistryChangeError, String comment, String author) {
        try {
            RegistryChangeError e = importClientRegisterService.getRegistryChangeError(idOfRegistryChangeError);
            DAOService.getInstance().addRegistryChangeErrorComment(idOfRegistryChangeError, comment, author);
            return null;
        } catch (Exception e) {
            logger.error("Failed to add comment for registry change error", e);
            return e.getMessage();
        }
    }

    public static <T extends ClientField> String getFindClientFieldValueByName(String paramName, T clientFieldList) {
        for(ClientField.ClientFieldItemParam field : clientFieldList.getParam()) {
            if (field.paramName.equalsIgnoreCase(paramName)) {
                return field.paramValue;
            }
        }
        return null;
    }
}