/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DiscountChangeHistory;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    private boolean disabled = true;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    Random random = new Random();

    private Integer number1;

    private Integer number2;

    private Integer plusResult = null;

    public Integer getNumber1() {
        return number1;
    }

    public void setNumber1(Integer number1) {
        this.number1 = number1;
    }

    public Integer getNumber2() {
        return number2;
    }

    public void setNumber2(Integer number2) {
        this.number2 = number2;
    }

    public Integer getPlusResult() {
        return plusResult;
    }

    public void setPlusResult(Integer plusResult) {
        this.plusResult = plusResult;
    }

    @Override
    public String getPageFilename() {
        return "service/msk/cancel_category_benefits";
    }

    public void onShow() throws Exception {
        number1 = random.nextInt(98) + 1;
        number2 = random.nextInt(8) + 1;
        disabled = true;
        plusResult = null;
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


                    Set<CategoryDiscount> emptyCategoryDiscountSet = new HashSet<CategoryDiscount>();// Сохраняем историю изменения льгот
                    DiscountManager.saveDiscountHistory(persistenceSession, client, client.getOrg(), client.getCategories(),
                            emptyCategoryDiscountSet, client.getDiscountMode(), Client.DISCOUNT_MODE_NONE, DiscountChangeHistory.MODIFY_IN_SERVICE);
                    client.setLastDiscountsUpdate(new Date());

                    Set<CategoryDiscount> categoryDiscountSet = client.getCategories();

                    if (!categoryDiscountSet.isEmpty()) {
                        String categoriesDiscounts = "";
                        int countSize = 0;
                        for (CategoryDiscount categoryDiscount : categoryDiscountSet) {
                            ++countSize;
                            categoriesDiscounts = categoriesDiscounts + categoryDiscount.getCategoryName();
                            if (categoryDiscountSet.size() > 1 && countSize < categoryDiscountSet.size()) {
                                categoriesDiscounts = categoriesDiscounts + ", ";
                            }
                        }

                        GroupControlBenefitsItems groupControlBenefitsItems = new GroupControlBenefitsItems(++rowNum,
                                client.getOrg().getShortName(), clientGroup, client.getPerson().getSurname(),
                                client.getPerson().getFirstName(), client.getPerson().getSecondName(),
                                client.getContractId().toString(), categoriesDiscounts,
                                "Клиент с л/c № " + client.getContractId().toString() + " отменены льготы ("
                                        + categoriesDiscounts + ")");

                        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
                        client.setDiscountMode(Client.DISCOUNT_MODE_NONE);
                        client.setClientRegistryVersion(clientRegistryVersion);
                        client.setCategories(emptyCategoryDiscountSet);
                        persistenceSession.update(client);

                        groupControlBenefitsItemsList.add(groupControlBenefitsItems);
                    } else {

                        GroupControlBenefitsItems groupControlBenefitsItems = new GroupControlBenefitsItems(++rowNum,
                                client.getOrg().getShortName(), clientGroup, client.getPerson().getSurname(),
                                client.getPerson().getFirstName(), client.getPerson().getSecondName(),
                                client.getContractId().toString(), "",
                                "Клиент с л/c № " + client.getContractId().toString() + " не обнаружены льготы");

                        if (client.getDiscountMode() == 3) {
                            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);

                            client.setDiscountMode(Client.DISCOUNT_MODE_NONE);
                            client.setClientRegistryVersion(clientRegistryVersion);
                            persistenceSession.update(client);
                        }
                        groupControlBenefitsItemsList.add(groupControlBenefitsItems);
                    }
                }
            }
            persistenceTransaction.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(persistenceTransaction, logger);
            ex.printStackTrace();
            printError("Произошла ошибка обработки клиента. Отмена льгот не произведена для всех клиентов.");
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

    public void resultChecker() {
        if (plusResult != null && plusResult != 0) {
            if ((number1 + number2) == plusResult) {
                disabled = false;
            } else {
                printError("Ответ подсчитан неверно");
            }
        } else {
            printError("Введите подсчитанный ответ");
        }
    }
}
