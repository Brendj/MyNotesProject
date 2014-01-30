package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders;

import ru.axetta.ecafe.processor.core.report.BasicReport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.01.14
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesDataOnOrdersAndPaymentReport extends BasicReport {
    private final List<DiscrepanciesDataOnOrdersAndPaymentItem> discrepanciesDataOnOrdersAndPaymentItems;

    public DiscrepanciesDataOnOrdersAndPaymentReport() {
        super();
        this.discrepanciesDataOnOrdersAndPaymentItems = Collections.emptyList();
    }

    public DiscrepanciesDataOnOrdersAndPaymentReport(Date generateTime, long generateDuration,
            List<DiscrepanciesDataOnOrdersAndPaymentItem> discrepanciesDataOnOrdersAndPaymentItems) {
        super(generateTime, generateDuration);
        this.discrepanciesDataOnOrdersAndPaymentItems = discrepanciesDataOnOrdersAndPaymentItems;
    }


    public List<DiscrepanciesDataOnOrdersAndPaymentItem> getDiscrepanciesDataOnOrdersAndPaymentItems() {
        return discrepanciesDataOnOrdersAndPaymentItems;
    }
}
