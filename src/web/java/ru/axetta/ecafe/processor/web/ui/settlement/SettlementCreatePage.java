/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.settlement;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Settlement;
import ru.axetta.ecafe.processor.core.persistence.utils.CurrentPositionsManager;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class SettlementCreatePage extends BasicWorkspacePage
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
        return "contragent/settlement/create";
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

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            if (multiContrFlag == 0)
                this.contragentPayer = new ContragentItem(contragent);
            else
                this.contragentReceiver = new ContragentItem(contragent);
        }
    }

    public void fill(Session session) throws Exception {

    }

    public void createSettlement(Session session) throws Exception {
        Contragent contragentPayer = (Contragent) session.load(Contragent.class,
                this.contragentPayer.getIdOfContragent());
        Contragent contragentReceiver = (Contragent) session.load(Contragent.class,
                this.contragentReceiver.getIdOfContragent());

        // Проверка пар контрагентов
        if (contragentPayer.getClassId() == Contragent.TSP &&
            contragentReceiver.getClassId() != Contragent.OPERATOR)
            throw new WrongContragentsException();

        if (contragentPayer.getClassId() == Contragent.PAY_AGENT &&
            (contragentReceiver.getClassId() != Contragent.TSP &&
                contragentReceiver.getClassId() != Contragent.OPERATOR))
                throw new WrongContragentsException();

        if (contragentPayer.getClassId() == Contragent.OPERATOR &&
            contragentReceiver.getClassId() != Contragent.TSP)
                throw new WrongContragentsException();

        if (contragentPayer.getClassId() == Contragent.BUDGET &&
            contragentReceiver.getClassId() != Contragent.TSP)
                throw new WrongContragentsException();

        //TODO: проверить схему
        /*Criteria criteria = session.createCriteria(Option.class);
        criteria.add(Restrictions.eq("idOfOption", 2L));
        Option option = (Option)criteria.uniqueResult();
        boolean withOperator = option.getOptionText().equals("1");
        if (withOperator) {

        }*/

        Long summaLong = (long) (new Double(summa) * 100);
        Settlement settlement = new Settlement();
        settlement.setIdOfContragentPayer(contragentPayer);
        settlement.setIdOfContragentReceiver(contragentReceiver);
        settlement.setCreatedDate(new Date());
        settlement.setPaymentDate(paymentDate);
        settlement.setPaymentDoc(paymentDoc);
        settlement.setSumma(summaLong);
        CurrentPositionsManager.createSettlement(session, settlement);
    }

    public class WrongContragentsException extends Exception {}
}
