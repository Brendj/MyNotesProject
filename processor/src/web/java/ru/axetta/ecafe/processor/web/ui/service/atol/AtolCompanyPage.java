/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.atol;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AtolCompany;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by nuc on 14.08.2019.
 */
@Component
@Scope("session")
public class AtolCompanyPage extends OnlineReportPage implements ContragentListSelectPage.CompleteHandler {
    Logger logger = LoggerFactory.getLogger(AtolCompanyPage.class);
    private AtolCompany atolCompany;
    private String contragentIds;
    private String contragentFilter = "Не выбрано";
    private List<CCAccountFilter.ContragentItem > contragentItems = new ArrayList<CCAccountFilter.ContragentItem >();

    public AtolCompanyPage() {

    }

    @Override
    public void onShow() throws Exception {
        fill();
    }

    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag, String classTypes) throws Exception {
        contragentItems.clear();
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            CCAccountFilter.ContragentItem contragentItem = new CCAccountFilter.ContragentItem(currentContragent);
            contragentItems.add(contragentItem);
        }
        setContragentFilterReceiverInfo();
    }

    private void setContragentFilterReceiverInfo() {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (contragentItems.isEmpty()) {
            contragentFilter = "Не выбрано";
        } else {
            for (CCAccountFilter.ContragentItem it : contragentItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(it.getContragentName());
                ids.append(it.getIdOfContragent());
            }
            contragentFilter = str.toString();
        }
        contragentIds = ids.toString();
    }

    private void fill() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(AtolCompany.class);
            atolCompany = (AtolCompany)criteria.uniqueResult();
            if (atolCompany == null) {
                atolCompany = new AtolCompany();
                atolCompany.setContragents(new HashSet<Contragent>());
            }
            List<Long> list = new ArrayList<>();
            for (Contragent contragent : atolCompany.getContragents()) {
                list.add(contragent.getIdOfContragent());
            }
            completeContragentListSelection(session, list, 0, "");
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in show ATOL company page: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void doSave() {
        if (StringUtils.isEmpty(atolCompany.getEmailOrg()) || StringUtils.isEmpty(atolCompany.getInn()) || StringUtils.isEmpty(atolCompany.getPlace())) {
            printError("Не заполнены обязательные поля: E-mail организаци, место расчетов, ИНН");
            return;
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            atolCompany.getContragents().clear();
            for (CCAccountFilter.ContragentItem it : contragentItems) {
                Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
                atolCompany.getContragents().add(contragent);
            }
            Criteria criteria = session.createCriteria(AtolCompany.class);
            AtolCompany company = (AtolCompany)criteria.uniqueResult();
            if (company == null) {
                session.save(atolCompany);
            } else {
                session.merge(atolCompany);
            }
            transaction.commit();
            transaction = null;
            printMessage("Данные сохранены в БД");
        } catch (Exception e) {
            logger.error("Error in show ATOL company page: ", e);
            printError("Произошла ошибка во время сохранения данных: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void doRefresh() {
        fill();
    }

    @Override
    public String getPageFilename() {
        return "service/atol/atol_company";
    }

    public AtolCompany getAtolCompany() {
        return atolCompany;
    }

    public void setAtolCompany(AtolCompany atolCompany) {
        this.atolCompany = atolCompany;
    }

    public String getContragentIds() {
        return contragentIds;
    }

    public void setContragentIds(String contragentIds) {
        this.contragentIds = contragentIds;
    }

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void setContragentFilter(String contragentFilter) {
        this.contragentFilter = contragentFilter;
    }

    public List<CCAccountFilter.ContragentItem> getContragentItems() {
        return contragentItems;
    }

    public void setContragentItems(List<CCAccountFilter.ContragentItem> contragentItems) {
        this.contragentItems = contragentItems;
    }
}
