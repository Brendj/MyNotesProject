/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DiscountChangeHistory;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import javax.persistence.PersistenceException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 18.02.15
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class GroupControlBenefitsPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(GroupControlBenefitsPage.class);

    private Boolean clientCancelBenefits = false;
    public UploadItem uploadItem;
    private ArrayList<ru.axetta.ecafe.processor.core.mail.File> files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

    private List<GroupControlBenefitsItems> groupControlBenefitsItems;

    @Override
    public String getPageFilename() {
        return "service/msk/group_control_benefit";
    }

    @Override
    public void onShow() throws Exception {
        //    groupControlBenefitsItems = new ArrayList<GroupControlBenefitsItems>();
        files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();
    }


    public ArrayList<ru.axetta.ecafe.processor.core.mail.File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<ru.axetta.ecafe.processor.core.mail.File> files) {
        this.files = files;
    }

    public List<GroupControlBenefitsItems> getGroupControlBenefitsItems() {
        return groupControlBenefitsItems;
    }

    public void setGroupControlBenefitsItems(List<GroupControlBenefitsItems> groupControlBenefitsItems) {
        this.groupControlBenefitsItems = groupControlBenefitsItems;
    }

    public Boolean getClientCancelBenefits() {
        return clientCancelBenefits;
    }

    public void setClientCancelBenefits(Boolean clientCancelBenefits) {
        this.clientCancelBenefits = clientCancelBenefits;
    }

    public UploadItem getUploadItem() {
        return uploadItem;
    }

    public void setUploadItem(UploadItem uploadItem) {
        this.uploadItem = uploadItem;
    }

    public void onHideClientCancelBenefitsChange(ActionEvent event) {
        clientCancelBenefits = true;
    }

    public void groupBenefitsGenerate(UploadItem item, RuntimeContext runtimeContext) throws Exception {

        GroupControlBenefitService groupControlBenefitService = new GroupControlBenefitService();

        String line;
        String csvSplitBy = ";";

        String benefitsSplitBy = ",";

        if (item != null) {
            File file = item.getFile();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file.getAbsolutePath()), Charset.forName("UTF-8")));

            groupControlBenefitsItems = new ArrayList<GroupControlBenefitsItems>();
            files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

            Long rowNum = 0L;

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    ++rowNum;
                    Session persistenceSession = runtimeContext.createPersistenceSession();
                    Transaction persistenceTransaction = null;
                    try {
                        persistenceTransaction = persistenceSession.beginTransaction();
                        String[] separatedData = line.split(csvSplitBy);

                        if (separatedData.length < 7) {
                            groupControlBenefitsItems
                                    .add(new GroupControlBenefitsItems(rowNum, separatedData[0], separatedData[1],
                                            separatedData[2], separatedData[3], separatedData[4], separatedData[5], "",
                                            "Неверный формат, вид .csv файла"));
                        } else {
                            Client client = groupControlBenefitService
                                    .findClientByContractId(Long.parseLong(separatedData[5].trim()),
                                            persistenceSession);

                            if (client != null) {
                                String[] separatedBenefits = separatedData[7].split(benefitsSplitBy);

                                Set<CategoryDiscount> categoryDiscountSet = new HashSet<CategoryDiscount>();

                                String errorCategoryName = "";

                                String categoriesDiscounts = "";

                                int count = 0;

                                for (String categoryName : separatedBenefits) {
                                    ++count;
                                    CategoryDiscount categoryDiscount = groupControlBenefitService
                                            .findCategoryDiscountByCategoryName(categoryName.trim(),
                                                    persistenceSession);
                                    if (categoryDiscount != null) {
                                        categoryDiscountSet.add(categoryDiscount);
                                        categoriesDiscounts =
                                                categoriesDiscounts + categoryDiscount.getIdOfCategoryDiscount();
                                        if (separatedBenefits.length > 1 && count < separatedBenefits.length) {
                                            categoriesDiscounts = categoriesDiscounts + ',';
                                        }
                                    } else {
                                        categoryDiscountSet.clear();
                                        categoriesDiscounts = "";
                                        errorCategoryName = errorCategoryName + categoryName;
                                        if (separatedBenefits.length > 1 && count < separatedBenefits.length) {
                                            errorCategoryName = errorCategoryName + ", ";
                                        }
                                    }

                                }

                                if (categoryDiscountSet.size() != separatedBenefits.length) {
                                    groupControlBenefitsItems
                                            .add(new GroupControlBenefitsItems(rowNum, separatedData[0],
                                                    separatedData[1], separatedData[2], separatedData[3],
                                                    separatedData[4], separatedData[5], separatedData[7],
                                                    "Льготы названия : '" + errorCategoryName
                                                            + "' не найдены в системе"));
                                } else {

                                    if (!clientCancelBenefits) {
                                        // добавляем с проверкой.
                                        Set<CategoryDiscount> clientCategoryDiscounts = client.getCategories();

                                        String categoryName = "";

                                        for (CategoryDiscount categoryDiscount : categoryDiscountSet) {
                                            if (!clientCategoryDiscounts.contains(categoryDiscount)) {
                                                clientCategoryDiscounts.add(categoryDiscount);
                                                categoryName = categoryName + categoryDiscount.getCategoryName() + ", ";
                                            }
                                        }

                                        String categoryDiscounts = "";

                                        int countSize = 0;

                                        for (CategoryDiscount clientCategory : clientCategoryDiscounts) {
                                            ++countSize;
                                            categoryDiscounts =
                                                    categoryDiscounts + clientCategory.getIdOfCategoryDiscount();
                                            if (clientCategoryDiscounts.size() > 1
                                                    && countSize < clientCategoryDiscounts.size()) {
                                                categoryDiscounts = categoryDiscounts + ',';
                                            }
                                        }

                                        long clientRegistryVersion = DAOUtils
                                                .updateClientRegistryVersion(persistenceSession);

                                        saveClientDiscountChange(persistenceSession, client, 3, categoriesDiscounts);
                                        client.setDiscountMode(3);
                                        client.setCategories(clientCategoryDiscounts);
                                        client.setCategoriesDiscounts(categoryDiscounts);
                                        client.setClientRegistryVersion(clientRegistryVersion);
                                        persistenceSession.update(client);
                                        groupControlBenefitsItems
                                                .add(new GroupControlBenefitsItems(rowNum, separatedData[0],
                                                        separatedData[1], separatedData[2], separatedData[3],
                                                        separatedData[4], separatedData[5], separatedData[7],
                                                        "Клиент с Л/с № " + separatedData[5]
                                                                + " обновлены льготы, добавились льготы ("
                                                                + categoryName.trim() + ")"));

                                    } else {
                                        //добавляем с удаление прежних льгот

                                        long clientRegistryVersion = DAOUtils
                                                .updateClientRegistryVersion(persistenceSession);

                                        saveClientDiscountChange(persistenceSession, client, 3, categoriesDiscounts);
                                        client.setDiscountMode(3);
                                        client.setCategoriesDiscounts(categoriesDiscounts);
                                        client.setCategories(categoryDiscountSet);
                                        client.setClientRegistryVersion(clientRegistryVersion);
                                        persistenceSession.update(client);
                                        groupControlBenefitsItems
                                                .add(new GroupControlBenefitsItems(rowNum, separatedData[0],
                                                        separatedData[1], separatedData[2], separatedData[3],
                                                        separatedData[4], separatedData[5], separatedData[7],
                                                        "Клиент с Л/с № " + separatedData[5]
                                                                + " обновлены льготы, с предварительной отменой льготных категорий"));
                                    }
                                }
                            } else {
                                groupControlBenefitsItems
                                        .add(new GroupControlBenefitsItems(rowNum, separatedData[0], separatedData[1],
                                                separatedData[2], separatedData[3], separatedData[4], separatedData[5],
                                                separatedData[7], "Клиент с Л/с № " + separatedData[5] + " не найден"));
                            }
                        }
                        persistenceTransaction.commit();
                    } catch (Exception ex) {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                    } finally {
                        HibernateUtils.close(persistenceSession, logger);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                printError("Файл не был найден");
            } catch (PersistenceException ex) {
                ex.printStackTrace();
                printError("Файл неверного формата");
            } catch (Exception e) {
                e.printStackTrace();
                printError(e.getMessage());
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        printError(e.getMessage());
                    }
                }
            }
        } else {
            printError("Сделайте загрузку файла");
        }
    }

    public void saveClientDiscountChange(Session session, Client client, Integer discountMode,
            String categoriesDiscount) {
        DiscountChangeHistory discountChangeHistory = new DiscountChangeHistory(client, null, discountMode, client.getDiscountMode(),
                categoriesDiscount, client.getCategoriesDiscounts());
        discountChangeHistory.setComment(DiscountChangeHistory.MODIFY_IN_SERVICE);
        session.save(discountChangeHistory);
    }
}
