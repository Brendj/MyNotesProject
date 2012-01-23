/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.settlement;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Settlement;
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
public class SettlementListPage extends BasicWorkspacePage
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

        private long idOfSettlement;
        private ContragentItem contragentPayer;
        private ContragentItem contragentReceiver;
        private Date createdDate;
        private Date paymentDate;
        private String paymentDoc;
        private String summa;


        public Item(Settlement settlement) {
            this.idOfSettlement = settlement.getIdOfSettlement();
            this.contragentPayer = new ContragentItem(settlement.getIdOfContragentPayer());
            this.contragentReceiver = new ContragentItem(settlement.getIdOfContragentReceiver());
            this.createdDate = settlement.getCreatedDate();
            this.paymentDate = settlement.getPaymentDate();
            this.paymentDoc = settlement.getPaymentDoc();
            this.summa = BasicReport.longToMoney(settlement.getSumma());
        }

        public long getIdOfSettlement() {
            return idOfSettlement;
        }

        public ContragentItem getContragentPayer() {
            return contragentPayer;
        }

        public ContragentItem getContragentReceiver() {
            return contragentReceiver;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public Date getPaymentDate() {
            return paymentDate;
        }

        public String getPaymentDoc() {
            return paymentDoc;
        }

        public String getSumma() {
            return summa;
        }
    }

    private List<Item> items = Collections.emptyList();
    private final SettlementFilter filter = new SettlementFilter();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "contragent/settlement/list";
    }

    public SettlementFilter getFilter() {
        return filter;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag) throws Exception {
        this.filter.completeContragentSelection(session, idOfContragent, multiContrFlag);
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!filter.isEmpty()) {
            List settlements = filter.retrieveSettlement(session);
            for (Object object : settlements) {
                Settlement settlement = (Settlement) object;
                items.add(new Item(settlement));
            }
        }
        this.items = items;
    }
}
