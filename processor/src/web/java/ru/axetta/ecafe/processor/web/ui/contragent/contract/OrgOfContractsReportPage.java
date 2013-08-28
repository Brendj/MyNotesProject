package ru.axetta.ecafe.processor.web.ui.contragent.contract;

import ru.axetta.ecafe.processor.core.daoservices.contract.ContractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.contract.OrgContractReportItem;
import ru.axetta.ecafe.processor.core.report.OrgOfContractsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.08.13
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class OrgOfContractsReportPage extends OnlineReportPage {

    @Autowired
    private ContractDAOService service;
    private OrgOfContractsReport contractsReport = null;
    private Boolean selectBindContract;

    @Override
    public void onShow() throws Exception {
        localCalendar.setTime(new Date());
        CalendarUtils.truncateToDayOfMonth(localCalendar);
        this.startDate = localCalendar.getTime();
    }

    public OrgOfContractsReport getContractsReport() {
        return contractsReport;
    }

    public Object build(){
        Date generateTime = new Date();
        List<OrgContractReportItem> items = service.generateOrgContractReport(selectBindContract, startDate);
        contractsReport = new OrgOfContractsReport(generateTime, new Date().getTime() - generateTime.getTime(), items);
        return null;
    }

    public String getPageFilename() {
        return "contragent/contract/report_org_of_contracts";
    }

    public void setSelectBindContract(Boolean selectBindContract) {
        this.selectBindContract = selectBindContract;
    }

    public Boolean getSelectBindContract() {
        return selectBindContract;
    }
}
