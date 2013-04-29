/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;
import ru.axetta.ecafe.processor.core.service.BIDataExportService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class OtherActionsPage extends BasicWorkspacePage {

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

    @Override
    public String getPageFilename() {
        return "service/other_actions";
    }
}
