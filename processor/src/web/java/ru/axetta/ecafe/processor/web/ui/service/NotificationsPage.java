/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianNotificationSetting;
import ru.axetta.ecafe.processor.core.service.BenefitService;
import ru.axetta.ecafe.processor.core.service.PreorderCancelNotificationService;
import ru.axetta.ecafe.processor.core.service.SummaryCalculationService;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Scope("session")
public class NotificationsPage extends BasicWorkspacePage {
    private String serviceNumber;

   // public String getPageTitle() {
     //   return "Уведомления";
    //}

    public String getPageFilename() {
        return "service/notifications";
    }

    public void runEventNotificationServiceForDaily() {
        SummaryCalculationService service = RuntimeContext.getAppContext().getBean(SummaryCalculationService.class);
        Date today = new Date(System.currentTimeMillis());
        Date endDate = CalendarUtils.endOfDay(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(today);
        service.run(startDate, endDate, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue(),
                true);
    }

    public void runEventNotificationServiceForWeekly() {
        SummaryCalculationService service = RuntimeContext.getAppContext().getBean(SummaryCalculationService.class);
        Date today = new Date(System.currentTimeMillis());
        Date[] dates = CalendarUtils.getCurrentWeekBeginAndEnd(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(dates[0]);
        Date endDate = CalendarUtils.endOfDay(dates[1]);
        service.run(startDate, endDate, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue(),
                true);
    }

    public void endBenefitNotification() {
        BenefitService service = RuntimeContext.getAppContext().getBean(BenefitService.class);
        service.runEndBenefit(true);
        printMessage("Оповещения об окончании срока действия льготы отправлены");
    }

    public void applicationDenide() {
        try {
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodTaskServiceNotification(serviceNumber);
        } catch (Exception e) {
            printError("Во время выполнения операции произошла ошибка с текстом " + e.getMessage());
        }

        printMessage("Оповещение об отказе в заявлении отправлено");
    }

    public void cancelPreorder() throws Exception {
        PreorderCancelNotificationService.sendNotification.manualStart();
        printMessage("Отправка уведомлений об отмене предзаказа выполнена");
    }

    public void runRegularPayments() throws Exception {
        RegularPaymentSubscriptionService regularPaymentSubscriptionService = RuntimeContext.getInstance()
                .getRegularPaymentSubscriptionService();
        regularPaymentSubscriptionService.checkClientBalances();
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getServiceNumber() {
        return this.serviceNumber;
    }
}
