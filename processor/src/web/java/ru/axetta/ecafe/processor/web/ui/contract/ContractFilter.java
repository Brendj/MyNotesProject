/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.ContragentClientAccount;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class ContractFilter {

    public static class ContractItem {

        private final Long idOfContract;
        private final String contractName;

        public ContractItem() {
            this.idOfContract = null;
            this.contractName = null;
        }

        public boolean isEmpty() {
            return null == idOfContract;
        }

        public ContractItem(Contract contract) {
            this.idOfContract = contract.getIdOfContract();
            this.contractName = contract.getContractNumber();
        }

        public ContractItem(Long idOfContract, String contractName) {
            this.idOfContract = idOfContract;
            this.contractName = contractName;
        }

        public Long getIdOfContract() {
            return idOfContract;
        }

        public String getContractName() {
            return contractName;
        }
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

    private Long idOfAccount = null;
    private ContractItem contract = new ContractItem();
    private OrgItem org = new OrgItem();

    public Long getIdOfAccount() {
        return idOfAccount;
    }

    public void setIdOfAccount(Long idOfAccount) {
        if (0L == idOfAccount) {
            this.idOfAccount = null;
        } else {
            this.idOfAccount = idOfAccount;
        }
    }

    public ContractItem getContract() {
        return contract;
    }

    public OrgItem getOrg() {
        return org;
    }

    public boolean isEmpty() {
        return null == idOfAccount && contract.isEmpty() && org.isEmpty();
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void completeContractSelection (Contract contract) throws Exception {
        completeContractSelection(contract, 0, "");
    }

    public void completeContractSelection (Long idofContract, String contractName) throws Exception {
        if (null != idofContract && null != contractName && contractName.length() > 0) {
            this.contract = new ContractItem(idofContract, contractName);
        }
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContract) {
            Contract contract = (Contract) session.load(Contract.class, idOfContract);
            //this.contract = new ContractItem(contract);
            completeContractSelection(contract, multiContrFlag, classTypes);
        } else {
            clear ();
        }
    }

    public void completeContractSelection(Contract contract, int multiContrFlag, String classTypes) throws Exception {
        if (null != contract) {
            this.contract = new ContractItem(contract);
        }
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
    }

    public void clear() {
        idOfAccount = null;
        contract = new ContractItem();
        org = new OrgItem();
    }

    public List retrieveCCAccounts(Session session) throws Exception {
        /*Criteria criteria = session.createCriteria(ContragentClientAccount.class);
        criteria.setFetchMode("client", FetchMode.JOIN);
        if (!this.isEmpty()) {
            if (null != this.idOfAccount) {
                criteria.add(Restrictions.eq("idOfAccount", this.idOfAccount));
            }
            if (!this.contract.isEmpty()) {
                Contract contract = (Contract) session
                        .load(Contract.class, this.contract.getIdOfContract());
                criteria.add(Restrictions.eq("contract", contract));
            }
            if (!this.org.isEmpty()) {
                Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
                criteria.createCriteria("client").add(Restrictions.eq("org", org));
            }
        }
        return criteria.list();*/
        return Collections.EMPTY_LIST;
    }
}