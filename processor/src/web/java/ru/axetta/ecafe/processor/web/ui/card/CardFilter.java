/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.CompareFilterMenu;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class CardFilter {

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

    private Long cardNo = null;
    private Long cardPrintedNo = null;
    private OrgItem org = new OrgItem();
    private CardStateFilterMenu cardStateFilterMenu = new CardStateFilterMenu();
    private CardLifeStateFilterMenu cardLifeStateFilterMenu = new CardLifeStateFilterMenu();
    private CompareFilterMenu balanceCompareFilterMenu = new CompareFilterMenu();
    private int cardState = CardStateFilterMenu.NO_CONDITION;
    private int cardLifeState = CardLifeStateFilterMenu.NO_CONDITION;
    private int balanceCompareCondition = CompareFilterMenu.NO_CONDITION;

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

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public OrgItem getOrg() {
        return org;
    }

    public boolean isEmpty() {
        return null == cardNo && null == cardPrintedNo && CardStateFilterMenu.NO_CONDITION == cardState
                && CardLifeStateFilterMenu.NO_CONDITION == cardLifeState
                && CompareFilterMenu.NO_CONDITION == balanceCompareCondition && org.isEmpty();
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
            this.org = new OrgItem(org);
        }
    }

    public void clear() {
        cardNo = null;
        cardPrintedNo = null;
        org = new OrgItem();
        cardState = CardStateFilterMenu.NO_CONDITION;
        cardLifeState = CardLifeStateFilterMenu.NO_CONDITION;
        balanceCompareCondition = CompareFilterMenu.NO_CONDITION;
    }

    public List retrieveCards(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Card.class, "card");
        if (!this.isEmpty()) {
            if (null != this.cardNo) {
                criteria.add(Restrictions.eq("card.cardNo", this.cardNo));
            }
            if (null != this.cardPrintedNo) {
                criteria.add(Restrictions.eq("card.cardPrintedNo", this.cardPrintedNo));
            }
            if (CardStateFilterMenu.NO_CONDITION != this.cardState) {
                criteria.add(Restrictions.eq("card.state", this.cardState));
            }
            if (CardLifeStateFilterMenu.NO_CONDITION != this.cardLifeState) {
                criteria.add(Restrictions.eq("card.lifeState", this.cardLifeState));
            }
            if (!this.org.isEmpty()) {
                Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
                criteria.createAlias("client", "client", JoinType.LEFT_OUTER_JOIN);
                criteria.add(Restrictions.disjunction().add(Restrictions.eq("card.org", org))
                        .add(Restrictions.eq("client.org", org)));
            }
        }
        return criteria.list();
    }
}