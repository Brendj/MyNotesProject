/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.ReportDAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope(value = "session")
public class PayStatsPage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler {

    private Date fromDate, toDate;

    private Contragent contragent;

    @Autowired
    private ReportDAOService daoService;
    private Calendar localCalendar;

    @Override
    public String getPageFilename() {
        return "report/online/paystats";
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
        this.fromDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        localCalendar.setTime(this.fromDate);

        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.toDate = localCalendar.getTime();
    }

    public String getFromDateAsString() {
        if (fromDate != null) {
            return CalendarUtils.dateToString(fromDate);
        } else {
            return "";
        }

    }

    public String getToDateAsString() {
        if (toDate != null) {
            return CalendarUtils.dateToString(toDate);
        } else {
            return "";
        }

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
        if (fromDate == null) {
            printError("Не указана \"Начальная дата\"");
            return;
        }
        if (toDate == null) {
            printError("Не указана \"Конечная дата\"");
            return;
        }
        if (contragent == null) {
            printError("Не заполнено поле \"Поставщик\"");
            return;
        }
        statItems = new LinkedList<StatItem>();
        List<Object[]> vals = daoService.getStatPaymentsByContragents(fromDate, toDate, contragent);
        for (Object[] d : vals) {
            String caName = "" + d[0];
            Integer payMethod = Integer.parseInt(d[1].toString());
            Long avgPay = (long) Float.parseFloat(d[2].toString());
            Long totalPay = Long.parseLong(d[3].toString());
            Long payCount = Long.parseLong(d[4].toString());
            statItems.add(new StatItem(caName, ClientPayment.PAYMENT_METHOD_NAMES[payMethod],
                    CurrencyStringUtils.copecksToRubles(avgPay), CurrencyStringUtils.copecksToRubles(totalPay),
                    "" + payCount));
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
        localCalendar.setTime(toDate);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        localCalendar.add(Calendar.SECOND, -1);
        this.toDate = localCalendar.getTime();
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        if (null != idOfContragent) {
            this.contragent = (Contragent) session.get(Contragent.class, idOfContragent);
        } else {
            clear();
        }
    }

    /*public void setToDate(Date toDate) {
        localCalendar.setTime(toDate);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        localCalendar.add(Calendar.SECOND, -1);
        this.toDate = localCalendar.getTime();
        //this.toDate = toDate;
    }*/

    private void clear() {
        contragent = null;
    }
}
