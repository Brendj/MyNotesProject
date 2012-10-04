/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
@Scope("session")
public class LicInfoPage extends BasicWorkspacePage {

    public String getPageFilename() {
        return "option/licinfo";
    }

    public LinkedList<RuntimeContext.DataInfo> getLicInfos() {
        return RuntimeContext.getInstance().getDataInfos();
    }
}
