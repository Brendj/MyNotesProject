/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.settlement;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Settlement;
import ru.axetta.ecafe.processor.core.report.BasicReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class SettlementEditPage extends BasicWorkspacePage
        implements ContragentSelectPage.CompleteHandler {
    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public String getPageFilename() {
        return "contragent/settlement/edit";
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            if (multiContrFlag == 0)
                this.contragentPayer = new ContragentItem(contragent);
            else
                this.contragentReceiver = new ContragentItem(contragent);
        }
    }

    private ContragentItem contragentPayer = new ContragentItem();
    private ContragentItem contragentReceiver = new ContragentItem();
    private Date paymentDate;
    private String paymentDoc;
    private String summa;

    public ContragentItem getContragentPayer() {
        return contragentPayer;
    }

    public ContragentItem getContragentReceiver() {
        return contragentReceiver;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentDoc() {
        return paymentDoc;
    }

    public void setPaymentDoc(String paymentDoc) {
        this.paymentDoc = paymentDoc;
    }

    public String getSumma() {
        return summa;
    }

    public void setSumma(String summa) {
        this.summa = summa;
    }

    public void fill(Session session, Long idOfSettlement) throws Exception {
        Settlement settlement = (Settlement) session.load(Settlement.class, idOfSettlement);
        fill(settlement);
    }

    public void updateSettlement(Session persistenceSession, Long idOfSettlement) throws Exception {
        Contragent contragentPayer = (Contragent) persistenceSession.load(Contragent.class, this.contragentPayer.getIdOfContragent());
        Contragent contragentReceiver = (Contragent) persistenceSession.load(Contragent.class, this.contragentReceiver.getIdOfContragent());
        Long summaLong = (long) (new Double(summa) * 100);
        Settlement settlement = (Settlement) persistenceSession.load(Settlement.class, idOfSettlement);
        settlement.setIdOfContragentPayer(contragentPayer);
        settlement.setIdOfContragentReceiver(contragentReceiver);
        settlement.setPaymentDate(paymentDate);
        settlement.setPaymentDoc(paymentDoc);
        Long preSumma = settlement.getSumma();
        settlement.setSumma(summaLong);
        RuntimeContext.getFinancialOpsManager().editSettlement(persistenceSession, settlement, summaLong - preSumma);
        fill(settlement);
    }

    private void fill(Settlement settlement) throws Exception {
        this.contragentPayer = new ContragentItem(settlement.getIdOfContragentPayer());
        this.contragentReceiver = new ContragentItem(settlement.getIdOfContragentReceiver());
        this.paymentDate = settlement.getPaymentDate();
        this.paymentDoc = settlement.getPaymentDoc();
        this.summa = BasicReport.longToMoney(settlement.getSumma());
    }
}