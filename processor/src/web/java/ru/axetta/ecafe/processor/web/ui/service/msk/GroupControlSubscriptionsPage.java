/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import org.richfaces.model.UploadedFile;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.12.14
 * Time: 13:38
 */
public class GroupControlSubscriptionsPage extends BasicWorkspacePage {

    public Long successLineNumber;
    public Long paymentAmount;
    public Long lowerLimitAmount;
    public UploadedFile uploadItem;
    private ArrayList<ru.axetta.ecafe.processor.core.mail.File> files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

    private List<GroupControlSubscriptionsItem> groupControlSubscriptionsItems;

    @Override
    public String getPageFilename() {
        return "service/msk/group_control_subscription";
    }

    @Override
    public void onShow() throws Exception {
        files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();
        paymentAmount = null;
        lowerLimitAmount = null;
    }

    public Long getSuccessLineNumber() {
        return successLineNumber;
    }

    public void setSuccessLineNumber(Long successLineNumber) {
        this.successLineNumber = successLineNumber;
    }

    public Long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Long getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(Long lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public void groupControlGenerate(UploadedFile item, RuntimeContext runtimeContext, BufferedReader bufferedReader)
            throws Exception {

        if (this.lowerLimitAmount != null && this.paymentAmount != null) {

            RegularPaymentEasyCheck regularPaymentEasyCheck = new RegularPaymentEasyCheck();

            String line;
            String cvsSplitBy = ";";

            if (item != null) {
                bufferedReader = new BufferedReader(
                        new InputStreamReader(item.getInputStream(), Charset.forName("UTF-8")));

                groupControlSubscriptionsItems = new ArrayList<GroupControlSubscriptionsItem>();
                files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

                Long rowNum = 0L;

                while ((line = bufferedReader.readLine()) != null) {
                    ++rowNum;
                    Session persistenceSession = runtimeContext.createPersistenceSession();
                    Transaction persistenceTransaction = persistenceSession.beginTransaction();

                    // разделитель
                    String[] separatedData = line.split(cvsSplitBy);
                    if (separatedData.length < 6) {
                        groupControlSubscriptionsItems
                                .add(new GroupControlSubscriptionsItem(rowNum, separatedData[0], separatedData[2],
                                        separatedData[3], separatedData[4], null, "Неверный формат, вид .csv файла"));
                    } else {
                        RequestResultEasyCheck requestResultEasyCheck = regularPaymentEasyCheck
                                .regularPaymentEasyCheckReadSubscriptionList(Long.parseLong(separatedData[5].trim()),
                                        persistenceSession);

                        if (requestResultEasyCheck.getSubscriptionListEasyCheck() == null
                                || requestResultEasyCheck.getSubscriptionListEasyCheck().getIdList().size() <= 0) {
                            RequestResultEasyCheck requestResultEasyCheck1 = regularPaymentEasyCheck.
                                    regularPaymentEasyCheckCreateSubscription(Long.parseLong(separatedData[5].trim()),
                                            this.lowerLimitAmount, this.paymentAmount, persistenceSession,
                                            persistenceTransaction, runtimeContext);

                            groupControlSubscriptionsItems
                                    .add(new GroupControlSubscriptionsItem(rowNum, separatedData[0], separatedData[2],
                                            separatedData[3], separatedData[4], Long.parseLong(separatedData[5].trim()),
                                            requestResultEasyCheck1.getErrorDesc() != null ? requestResultEasyCheck1
                                                    .getErrorDesc() : "добавлен"));
                        } else {
                            RequestResultEasyCheck requestResultEasyCheck2 = regularPaymentEasyCheck
                                    .regularPaymentEasyCheckEdit(
                                            requestResultEasyCheck.getSubscriptionListEasyCheck().getIdList(),
                                            Long.parseLong(separatedData[5].trim()), this.lowerLimitAmount,
                                            this.paymentAmount, persistenceSession, persistenceTransaction);

                            groupControlSubscriptionsItems
                                    .add(new GroupControlSubscriptionsItem(rowNum, separatedData[0], separatedData[2],
                                            separatedData[3], separatedData[4], Long.parseLong(separatedData[5].trim()),
                                            requestResultEasyCheck2.getErrorDesc() != null ? requestResultEasyCheck2
                                                    .getErrorDesc() : "редактирован"));
                        }
                    }
                }
            } else {
                printError("Сделайте загрузку файла");
            }
        } else {
            printError("Сумма пополнения (руб.), Порог баланса для пополнения (руб.) - не указаны");
        }
    }

    public List<GroupControlSubscriptionsItem> getGroupControlSubscriptionsItems() {
        return groupControlSubscriptionsItems;
    }

    public void setGroupControlSubscriptionsItems(List<GroupControlSubscriptionsItem> groupControlSubscriptionsItems) {
        this.groupControlSubscriptionsItems = groupControlSubscriptionsItems;
    }

    public UploadedFile getUploadItem() {
        return uploadItem;
    }

    public void setUploadItem(UploadedFile uploadItem) {
        this.uploadItem = uploadItem;
    }

    public ArrayList<ru.axetta.ecafe.processor.core.mail.File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<ru.axetta.ecafe.processor.core.mail.File> files) {
        this.files = files;
    }
}

