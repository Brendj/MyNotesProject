/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.12.14
 * Time: 13:38
 */

@Component
@Scope("session")
public class GroupControlSubscriptionsPage extends BasicWorkspacePage {

    private List<GroupControlSubscriptionsItem> groupControlSubscriptionsItems = new ArrayList<GroupControlSubscriptionsItem>();

    private Long lineResultSize;
    private Long successLineNumber;

    @Override
    public void onShow() throws Exception {
    }

    @Override
    public String getPageFilename() {
        return "service/msk/group_control_subscription";
    }

    public void subscriptionLoadFileListener(UploadEvent event) {
        UploadItem item = event.getUploadItem();

        getGroupControlSubscriptionsItems().add(new GroupControlSubscriptionsItem(133390L, "ok"));
        getGroupControlSubscriptionsItems().add(new GroupControlSubscriptionsItem(133390L, "204"));
        getGroupControlSubscriptionsItems().add(new GroupControlSubscriptionsItem(112345L, "200"));
        getGroupControlSubscriptionsItems().add(new GroupControlSubscriptionsItem(111111L, "95"));
        getGroupControlSubscriptionsItems().add(new GroupControlSubscriptionsItem(234590L, "104"));
        getGroupControlSubscriptionsItems().add(new GroupControlSubscriptionsItem(987650L, "some"));

        lineResultSize = Long.valueOf(groupControlSubscriptionsItems.size());

        /*try {
            File file = item.getFile();
            questionariesRootElement = questionaryService.parseQuestionaryByXML(file);
        } catch (Exception e){
            registrationItems.add(new RegistrationItem("Ошибка при загрузке данных: " + e));
            logger.error("Failed to load from file", e);
            return;
        }
        try {
            registrationItems = questionaryService.registrationQuestionariesFromXML(questionariesRootElement, idOfOrgList);
            printMessage("Данные загружены и зарегистрированы успешно");
        } catch (Exception e) {
            registrationItems.add(new RegistrationItem("Ошибка при регистрации данных: " + e));
            logger.error("Failed to registration questionaries from file", e);
        }*/
    }

    public List<GroupControlSubscriptionsItem> getGroupControlSubscriptionsItems() {
        return groupControlSubscriptionsItems;
    }

    public Long getLineResultSize() {
        return lineResultSize;
    }

    public void setLineResultSize(Long lineResultSize) {
        this.lineResultSize = lineResultSize;
    }

    public Long getSuccessLineNumber() {
        return successLineNumber;
    }

    public void setSuccessLineNumber(Long successLineNumber) {
        this.successLineNumber = successLineNumber;
    }
}

