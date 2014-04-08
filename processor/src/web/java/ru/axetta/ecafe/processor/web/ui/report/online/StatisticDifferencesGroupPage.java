package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.03.14
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class StatisticDifferencesGroupPage extends BasicWorkspacePage {

    public boolean getEligibleToWorkCommodityAccounting() throws Exception {
        return true;
    }

}
