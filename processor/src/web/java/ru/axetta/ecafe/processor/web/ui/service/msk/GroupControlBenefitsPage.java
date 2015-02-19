/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.model.UploadItem;

import javax.faces.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 18.02.15
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class GroupControlBenefitsPage extends BasicWorkspacePage {

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
        groupControlBenefitsItems = new ArrayList<GroupControlBenefitsItems>();
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

    public void groupBenefitsGenerate(UploadItem item, RuntimeContext runtimeContext, BufferedReader bufferedReader)
            throws Exception {

        GroupControlBenefitService groupControlBenefitService = new GroupControlBenefitService();

        String line;
        String csvSplitBy = ";";

        String benefitsSplitBy = ",";

        if (item != null) {
            File file = item.getFile();
            bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));

            groupControlBenefitsItems = new ArrayList<GroupControlBenefitsItems>();
            files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

            while ((line = bufferedReader.readLine()) != null) {
                Session persistenceSession = runtimeContext.createPersistenceSession();
                Transaction persistenceTransaction = persistenceSession.beginTransaction();

                String[] separatedData = line.split(csvSplitBy);

                if (separatedData.length < 7) {
                    groupControlBenefitsItems
                            .add(new GroupControlBenefitsItems(separatedData[0], separatedData[1], separatedData[2],
                                    separatedData[3], separatedData[4], separatedData[5], "",
                                    "Не верный формат .csv файла"));
                } else {

                    Client client = groupControlBenefitService
                            .findClientByContractId(Long.parseLong(separatedData[5].trim()), persistenceSession);

                    if (client != null) {
                        String[] separatedBenefits = separatedData[7].split(benefitsSplitBy);
                        // выгрузка csv файл
                        // Клиент
                        if (!clientCancelBenefits) {
                            // добавляем с проверкой.

                        } else {
                            // сбрасываем и устанавливаем новые

                        }
                    } else {
                        groupControlBenefitsItems
                                .add(new GroupControlBenefitsItems(separatedData[0], separatedData[1], separatedData[2],
                                        separatedData[3], separatedData[4], separatedData[5], separatedData[7],
                                        "Клиент с Л/с № " + separatedData[5] + " не найден"));
                    }
                }
            }
        } else {
            printError("Сделайте загрузку файла");
        }
    }
}
