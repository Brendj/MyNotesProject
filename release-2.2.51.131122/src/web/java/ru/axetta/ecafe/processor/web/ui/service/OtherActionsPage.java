/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    @Override
    public String getPageFilename() {
        return "service/other_actions";
    }
}
