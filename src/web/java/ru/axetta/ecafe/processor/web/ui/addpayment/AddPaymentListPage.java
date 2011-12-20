/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.addpayment;

import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.BasicReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 9:56
 * To change this template use File | Settings | File Templates.
 */
public class AddPaymentListPage extends BasicWorkspacePage
    implements ContragentSelectPage.CompleteHandler {

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public static class Item {
        private long idOfAddPayment;
        private ContragentItem contragentPayer;
        private ContragentItem contragentReceiver;
        private String summa;
        private String comment;
        private Date fromDate;
        private Date toDate;

        public Item(AddPayment addPayment) {
            this.idOfAddPayment = addPayment.getIdOfAddPayment();
            this.contragentPayer = new ContragentItem(addPayment.getContragentPayer());
            this.contragentReceiver = new ContragentItem(addPayment.getContragentReceiver());
            this.summa = BasicReport.longToMoney(addPayment.getSumma());
            this.comment = addPayment.getComment();
            this.fromDate = addPayment.getFromDate();
            this.toDate = addPayment.getToDate();
        }

        public long getIdOfAddPayment() {
            return idOfAddPayment;
        }

        public ContragentItem getContragentPayer() {
            return contragentPayer;
        }

        public ContragentItem getContragentReceiver() {
            return contragentReceiver;
        }

        public String getSumma() {
            return summa;
        }

        public String getComment() {
            return comment;
        }

        public Date getFromDate() {
            return fromDate;
        }

        public Date getToDate() {
            return toDate;
        }
    }

    private List<Item> items = Collections.emptyList();
    private final AddPaymentFilter filter = new AddPaymentFilter();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "contragent/addpayment/list";
    }

    public AddPaymentFilter getFilter() {
        return filter;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag) throws Exception {
        this.filter.completeContragentSelection(session, idOfContragent, multiContrFlag);
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!filter.isEmpty()) {
            List addPayments = filter.retrieveAddPayment(session);
            for (Object object : addPayments) {
                AddPayment addPayment = (AddPayment) object;
                items.add(new Item(addPayment));
            }
        }
        this.items = items;
    }
}
