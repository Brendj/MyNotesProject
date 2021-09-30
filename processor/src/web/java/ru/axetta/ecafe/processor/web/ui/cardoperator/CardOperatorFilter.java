/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.HistoryCard;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.CompareFilterMenu;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.card.CardFilter;
import ru.axetta.ecafe.processor.web.ui.card.CardLifeStateFilterMenu;
import ru.axetta.ecafe.processor.web.ui.card.CardStateFilterMenu;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 31.07.2017.
 */
public class CardOperatorFilter extends OnlineReportPage {

    private Calendar localCalendar;

    public Calendar getLocalCalendar() {
        return localCalendar;
    }

    public void setLocalCalendar(Calendar localCalendar) {
        this.localCalendar = localCalendar;
    }

    public CardOperatorFilter() {
        super();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        localCalendar.setTime(this.startDate);

        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
    }

    protected Date startDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ClientItem getClient() {
        return client;
    }

    public void setClient(ClientItem client) {
        this.client = client;
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        public boolean isEmpty() {
            return null == idOfOrg;
        }

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    private CardFilter.OrgItem org = new CardFilter.OrgItem();
    private ClientItem client;
    private CardStateFilterMenu cardStateFilterMenu = new CardStateFilterMenu();
    private CardLifeStateFilterMenu cardLifeStateFilterMenu = new CardLifeStateFilterMenu();
    private CompareFilterMenu balanceCompareFilterMenu = new CompareFilterMenu();
    private int cardState = CardStateFilterMenu.NO_CONDITION;
    private int cardLifeState = CardLifeStateFilterMenu.NO_CONDITION;
    private int balanceCompareCondition = CompareFilterMenu.NO_CONDITION;

    private Boolean showOperationsAllPeriod = false;

    public Boolean getShowOperationsAllPeriod() {
        return showOperationsAllPeriod;
    }

    public void setShowOperationsAllPeriod(Boolean showOperationsAllPeriod) {
        this.showOperationsAllPeriod = showOperationsAllPeriod;
    }

    public int getCardState() {
        return cardState;
    }

    public void setCardState(int cardState) {
        this.cardState = cardState;
    }

    public int getCardLifeState() {
        return cardLifeState;
    }

    public void setCardLifeState(int cardLifeState) {
        this.cardLifeState = cardLifeState;
    }

    public int getBalanceCompareCondition() {
        return balanceCompareCondition;
    }

    public void setBalanceCompareCondition(int balanceCompareCondition) {
        this.balanceCompareCondition = balanceCompareCondition;
    }

    public CardStateFilterMenu getCardStateFilterMenu() {
        return cardStateFilterMenu;
    }

    public CardLifeStateFilterMenu getCardLifeStateFilterMenu() {
        return cardLifeStateFilterMenu;
    }

    public CompareFilterMenu getBalanceCompareFilterMenu() {
        return balanceCompareFilterMenu;
    }


    public CardFilter.OrgItem getOrg() {
        return org;
    }

    public boolean isEmpty() {
        return CardStateFilterMenu.NO_CONDITION == cardState && CardLifeStateFilterMenu.NO_CONDITION == cardLifeState
                && CompareFilterMenu.NO_CONDITION == balanceCompareCondition && org.isEmpty() && (client == null || client.getIdOfClient() == null);
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new CardFilter.OrgItem(org);
        }
    }

    public void clear() {
        org = new CardFilter.OrgItem();
        cardState = CardStateFilterMenu.NO_CONDITION;
        cardLifeState = CardLifeStateFilterMenu.NO_CONDITION;
        balanceCompareCondition = CompareFilterMenu.NO_CONDITION;
    }

    public List<HistoryCard> retrieveCards(Session session) throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        Criteria cr = session.createCriteria(HistoryCard.class);
        cr.createAlias("card", "card", JoinType.LEFT_OUTER_JOIN);
        cr.add(Restrictions.eq("user", user));
        if (CardStateFilterMenu.NO_CONDITION != this.cardState) {
            cr.add(Restrictions.eq("card.state", this.cardState));
        }
        if (!showOperationsAllPeriod) {
            if (startDate != null) {
                cr.add(Restrictions.between("upDatetime", startDate, CalendarUtils.addOneDay(startDate)));
            }
        }
        if (client != null && client.getIdOfClient() != null) {
            Client cl = (Client) session.load(Client.class, client.getIdOfClient());
            cr.add(Restrictions.eq("card.client", cl));
        }
        if (!this.org.isEmpty()) {
            Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
            cr.add(Restrictions.eq("card.org", org));
            /*cr.createAlias("client", "card.client", JoinType.LEFT_OUTER_JOIN);
            cr.add(Restrictions.disjunction().add(Restrictions.eq("card.org", org))
                    .add(Restrictions.eq("client.org", org)));*/
        }
        cr.addOrder(Order.asc("upDatetime"));
        return cr.list();


        /*Criteria criteria = session.createCriteria(Card.class, "card");
        if (CardStateFilterMenu.NO_CONDITION != this.cardState) {
            criteria.add(Restrictions.eq("card.state", this.cardState));
        }
        if (!showOperationsAllPeriod) {
            if (startDate != null) {
                criteria.add(
                        Restrictions.between("card.updateTime", startDate, CalendarUtils.addOneDay(startDate)));
            }
        }
        if (client != null && client.getIdOfClient() != null) {
            Client cl = (Client)session.load(Client.class, client.getIdOfClient());
            criteria.add(Restrictions.eq("card.client", cl));
        }
        if (!this.org.isEmpty()) {
            Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
            criteria.createAlias("client", "client", JoinType.LEFT_OUTER_JOIN);
            criteria.add(Restrictions.disjunction().add(Restrictions.eq("card.org", org))
                    .add(Restrictions.eq("client.org", org)));
        }
        return criteria.list();*/
    }
}
