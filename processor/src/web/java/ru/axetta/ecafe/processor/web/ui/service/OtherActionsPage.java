/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.partner.nsi.NSIRepairService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

@Component
@Scope("session")
public class OtherActionsPage extends BasicWorkspacePage {

    public void rubBIExport () throws Exception {
        RuntimeContext.getAppContext().getBean(BIDataExportService.class).run(); // DEF
        printMessage("Генерация данных ключевых показателей выполнена");
    }

    public void runProjectStateGenerator() throws Exception {
        RuntimeContext.getAppContext().getBean(ProjectStateReportService.class).run(); //DEF
        //RuntimeContext.getAppContext().getBean(SignHandlerService.class).testCall();
        //RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).run();
        //RuntimeContext.getAppContext().getBean(BIDataExportService.class).run();
        printMessage("Генерация данных ключевых показателей выполнена");
    }


    public void runImportRegisterClients() throws Exception {
        RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).run(); //DEF
        printMessage("Импорт клиентов из Реестров выполнен");
    }

    public void runBenefitsRecalculation() throws Exception {
        RuntimeContext.getAppContext().getBean(BenefitsRecalculationService.class).runForcibly(); //DEF
        printMessage("Пересчет льготных правил выполнен");
    }

    public void runClientGuardSANRebuild () throws Exception {
        RuntimeContext.getAppContext().getBean(ClientGuardSanRebuildService.class).rebuild(); //DEF
        printMessage("Переформирование Guard SAN для клиентов выполнено успешно");
        /*RuntimeContext.getAppContext().getBean(ClientRoomControllerWS.class).attachGuardSan("14414414452", "14414414453"); //DEF
        printMessage("Переформирование Guard SAN для клиентов выполнено успешно");*/
    }

    public void runImportRNIPPayment () throws Exception {
        RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsService.class).run(); //DEF
        printMessage("Импорт платежей RNIP был импортирован успешно");
    }

    public void runRepositoryReportsCleanup() throws Exception {
        RuntimeContext.getAppContext().getBean(CleanupReportsService.class).run(); //DEF
        printMessage("Очистка Репозитория успешно завершена");
    }

    /*public void runClientGuardSANRemove () throws Exception {
        RuntimeContext.getAppContext().getBean(ClientGuardSanRebuildService.class).delete(); //DEF
        printMessage("Переформирование Guard SAN для клиентов выполнено успешно");
    }*/

    public void runSynchCleanup() throws Exception {
        RuntimeContext.getAppContext().getBean(SynchCleanupService.class).run(); //DEF
        printMessage("Очистка журналов синхронизации успешно завершена");
    }

    public void runBindEMPClients() throws Exception {
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runBindClients(); //DEF
        printMessage("Привязка клиентов с ЕМП завершена");
    }

    public void runReceiveEMPUpdates() throws Exception {
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates(); //DEF
        printMessage("Загрузка обновление из ЕМП завершена");
    }

    public void runSendEMPEvent() throws Exception {
        //Client client = DAOService.getInstance().getClientByGuid("e5000805-29a9-1388-e043-a2997e0ab714");
        Client client = DAOService.getInstance().findClientById(1069L);

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(new Date(System.currentTimeMillis()));
        String[] values = new String[]{
                "paySum", CurrencyStringUtils.copecksToRubles(100000),
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()),
                "contractId",String.valueOf(client.getContractId()),
                "surname","Тестеров"/*client.getPerson().getSurname()*/,
                "firstName","Тест"/*client.getPerson().getFirstName()*/,
                "empTime", empTime,
                "targetId", "" + 1
        };
        values = EventNotificationService.attachTargetIdToValues(1L, values);

        RuntimeContext.getAppContext().getBean(EventNotificationService.class).
                sendSMS(client, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values); //DEF
        printMessage("Пробное  событие успешно отправлено на ЕМП");
    }

    public void runRecalculateEMPStatistics() throws Exception {
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).recalculateEMPClientsCount(); //DEF
        printMessage("Статистика ЕМП обновлена");
    }

    public void repairNSI() throws Exception {
        RuntimeContext.getAppContext().getBean(NSIRepairService.class).run(); //DEF
        printMessage("Записи из Реестров исправлены");
    }

    public void runRegularPayments() throws Exception {
        RegularPaymentSubscriptionService regularPaymentSubscriptionService =  RuntimeContext.getInstance().getRegularPaymentSubscriptionService();
        regularPaymentSubscriptionService.checkClientBalances();
    }

    public void runTest() throws Exception {
        RuntimeContext.getAppContext().getBean(SMSResendingService.class).executeResending(); //DEF
        printMessage("Повторная отправка не доставленных СМС завершена");
    }



    @Override
    public String getPageFilename() {
        return "service/other_actions";
    }
}
