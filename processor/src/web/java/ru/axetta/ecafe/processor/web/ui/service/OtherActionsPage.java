/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class OtherActionsPage extends BasicWorkspacePage {

    public void runProjectStateGenerator() {
        RuntimeContext.getAppContext().getBean(ProjectStateReportService.class).run();
        printMessage("Генерация данных ключевых показателей выполнена");
    }

    @Override
    public String getPageFilename() {
        return "service/other_actions";
    }
}
