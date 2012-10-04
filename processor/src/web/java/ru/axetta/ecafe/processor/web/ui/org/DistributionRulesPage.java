/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.08.12
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class DistributionRulesPage extends BasicWorkspacePage {

    @PersistenceContext
    private EntityManager entityManager;
    private List<RuleItem> ruleItemList;

    @Override
    public void onShow() throws Exception {
        ruleItemList = new LinkedList<RuleItem>();
        reload();
    }

    @Transactional
    private void reload() throws Exception{
        TypedQuery<Org> orgList = entityManager.createQuery("select org from Org org LEFT JOIN FETCH org.sourceMenuOrgs LEFT JOIN FETCH org.defaultSupplier",Org.class);
        for (Org org : orgList.getResultList()) {
            Org distributionOrg = null;
            if (!org.getSourceMenuOrgs().isEmpty()) {
                // может быть только одно правило в сете
                for (Org sourceMenuOrg : org.getSourceMenuOrgs()) {
                    distributionOrg = sourceMenuOrg;
                }
            }
            Contragent contragent = org.getDefaultSupplier();
            RuleItem ruleItem = new RuleItem();
            ruleItem.setContragent(contragent);
            ruleItem.setDistributionOrg(distributionOrg);
            ruleItem.setSourceOrg(org);
            ruleItemList.add(ruleItem);
        }

        /*
        TypedQuery<MenuExchangeRule> menuExchangeRuleTypedQuery = entityManager.createQuery("from MenuExchangeRule order by idOfDestOrg",MenuExchangeRule.class);
        List<MenuExchangeRule> menuExchangeRules = menuExchangeRuleTypedQuery.getResultList();
        for (MenuExchangeRule menuExchangeRule: menuExchangeRules){
            Org distributionOrg = entityManager.find(Org.class,menuExchangeRule.getIdOfSourceOrg());
            Org sourceOrg = entityManager.find(Org.class,menuExchangeRule.getIdOfDestOrg());
            Contragent contragent = entityManager.find(Contragent.class, sourceOrg.getDefaultSupplier().getIdOfContragent());
            RuleItem ruleItem = new RuleItem();
            ruleItem.setContragent(contragent);
            ruleItem.setDistributionOrg(distributionOrg);
            ruleItem.setSourceOrg(sourceOrg);
            ruleItemList.add(ruleItem);
        }
        */
    }

    public List<RuleItem> getRuleItemList() {
        return ruleItemList;
    }

    @Override
    public String getPageFilename() {
        return "org/distribution_rules";
    }

    public static class RuleItem{
        private Contragent contragent;
        private Org sourceOrg;
        private Org distributionOrg;

        public Contragent getContragent() {
            return contragent;
        }

        public void setContragent(Contragent contragent) {
            this.contragent = contragent;
            getContragentLabel();
        }

        public Org getSourceOrg() {
            return sourceOrg;
        }

        public void setSourceOrg(Org sourceOrg) {
            this.sourceOrg = sourceOrg;
            getSourceOrgLabel();
        }

        public Org getDistributionOrg() {
            return distributionOrg;
        }

        public void setDistributionOrg(Org distributionOrg) {
            this.distributionOrg = distributionOrg;
            getDistributionOrgLabel();
        }
        
        public String getDistributionOrgLabel() {
            if (distributionOrg==null) return "";
            return distributionOrg.getShortName()+" - "+distributionOrg.getIdOfOrg();
        }
        public String getSourceOrgLabel() {
            if (sourceOrg==null) return "";
            return sourceOrg.getShortName()+" - "+sourceOrg.getIdOfOrg();
        }
        public String getContragentLabel() {
            if (contragent==null) return "";
            return contragent.getContragentName()+" - "+contragent.getIdOfContragent();
        }
        
        
    }

}
