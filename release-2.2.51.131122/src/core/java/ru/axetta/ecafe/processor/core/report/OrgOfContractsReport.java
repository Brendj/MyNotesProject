package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.daoservices.contract.ContractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.contract.OrgContractReportItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.08.13
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public class OrgOfContractsReport extends BasicReport {

    private List<OrgContractReportItem> items;

    public OrgOfContractsReport() {
        this.items = Collections.emptyList();
    }

    public OrgOfContractsReport(Date generateTime, long generateDuration, List<OrgContractReportItem> items) {
        super(generateTime, generateDuration);
        this.items = items;
    }

    public List<OrgContractReportItem> getItems() {
        return items;
    }
}
