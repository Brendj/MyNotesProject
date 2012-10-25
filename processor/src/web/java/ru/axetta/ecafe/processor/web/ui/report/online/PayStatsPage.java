/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.partner.rbkmoney.CurrencyConverter;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope(value = "session")
public class PayStatsPage extends BasicWorkspacePage {

    Date fromDate, toDate;

    @Autowired
    DAOService daoService;

    @Override
    public String getPageFilename() {
        return "report/online/paystats";
    }

    @Override
    public void onShow() throws Exception {
    }

    public String getFromDateAsString() {
        return CalendarUtils.dateToString(fromDate);
    }

    public String getToDateAsString() {
        return CalendarUtils.dateToString(toDate);
    }

    public static class StatItem {

        String contragentName;
        String payMethod;
        String avg;
        String count;
        String total;

        public String getContragentName() {
            return contragentName;
        }

        public String getPayMethod() {
            return payMethod;
        }

        public String getAvg() {
            return avg;
        }

        public String getCount() {
            return count;
        }

        public StatItem(String contragentName, String payMethod, String avg, String total, String count) {

            this.contragentName = contragentName;
            this.payMethod = payMethod;
            this.avg = avg;
            this.total = total;
            this.count = count;
        }

        public String getTotal() {
            return total;
        }
    }

    LinkedList<StatItem> statItems;

    public LinkedList<StatItem> getStatItems() {
        return statItems;
    }


    public void updateData() {
        if (fromDate==null) { printError("Не указана начальная дата"); return; }
        if (toDate==null) { printError("Не указана конечная дата"); return; }
        statItems = new LinkedList<StatItem>();
        List<Object[]> vals = daoService.getStatPaymentsByContragents(fromDate, toDate);
        for (Object[] d : vals) {
            String caName = "" + d[0];
            Integer payMethod = Integer.parseInt(d[1].toString());
            Long avgPay = (long)Float.parseFloat(d[2].toString());
            Long totalPay = Long.parseLong(d[3].toString());
            Long payCount = Long.parseLong(d[4].toString());
            statItems.add(new StatItem(caName, ClientPayment.PAYMENT_METHOD_NAMES[payMethod], CurrencyStringUtils.copecksToRubles(avgPay),
                    CurrencyStringUtils.copecksToRubles(totalPay), "" + payCount));
        }
    }


    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
