package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.web.ui.report.online.EnterEventsMonitoringReportPage;

import javax.faces.component.UIComponent;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 12.09.16
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */

public class ElectionsPage implements Serializable {

    private final EnterEventsMonitoringReportPage enterEventsMonitoringReportPage = new EnterEventsMonitoringReportPage();

    private UIComponent pageComponent;

    public UIComponent getPageComponent() {
        return pageComponent;
    }

    public void setPageComponent(UIComponent pageComponent) {
        this.pageComponent = pageComponent;
    }

    public EnterEventsMonitoringReportPage getEnterEventsMonitoringReportPage() {
        return enterEventsMonitoringReportPage;
    }
}
