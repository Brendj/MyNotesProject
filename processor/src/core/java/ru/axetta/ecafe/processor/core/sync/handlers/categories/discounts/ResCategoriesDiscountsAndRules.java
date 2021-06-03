/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDiscountRule;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * User: akmukov
 * Date: 30.03.2016
 */
public class ResCategoriesDiscountsAndRules implements AbstractToElement {
    private final List<DiscountCategoryItem> dcis = new LinkedList<DiscountCategoryItem>();
    private final List<DiscountCategoryRuleItem> dcris = new LinkedList<DiscountCategoryRuleItem>();

    //wt
    private final List<DiscountCategoryWtRuleItem> wtDcris = new LinkedList<>();

    private final List<CategoryDiscountDSZNItem> dcriDSZN = new LinkedList<CategoryDiscountDSZNItem>();
    private boolean existOrgWithEmptyCategoryOrgSet;

    public void fillData(Session session, Long idOfOrg, boolean manyOrgs, Long versionDSZN) {
        addDiscountRules(session, idOfOrg, manyOrgs);
        addCategoryDiscounts(session);
        addCategoryDiscountsDSZN(session, versionDSZN, idOfOrg);
    }

    private void addDiscountRules(Session session, Long idOfOrg, boolean manyOrgs) {
        List discountRules = getAllDiscountRules(session);

        List<WtDiscountRule> wtDiscountRules = getAllWtDiscountRules(session);
        Org mainOrg = DAOService.getInstance().getOrg(session, idOfOrg);

        Set<Org> orgs = getProcessedOrgs(session, idOfOrg, manyOrgs);
        existOrgWithEmptyCategoryOrgSet = false;
        for (Org org : orgs) {
            if (mainOrg.getUseWebArm() && org.getUseWebArm()) {
                addWtRulesForOrgWithCategoryOrgSet(wtDiscountRules, org);
            }
            else {
                addRulesForOrgWithCategoryOrgSet(discountRules, org);
            }
        }
        if (existOrgWithEmptyCategoryOrgSet) {
            if (mainOrg.getUseWebArm()) {
                addWtRulesWithEmptyCategoryOrgSet(wtDiscountRules, idOfOrg);
            } else {
                addRulesWithEmptyCategoryOrgSet(discountRules, idOfOrg);
            }
        }
    }

    private List getAllDiscountRules(Session session) {
        Criteria criteriaDiscountRule = session.createCriteria(DiscountRule.class);
        criteriaDiscountRule.setFetchMode("categoryOrgs", FetchMode.JOIN);
        return criteriaDiscountRule.list();
    }

    private List<WtDiscountRule> getAllWtDiscountRules(Session session) {
        Criteria criteriaWtDiscountRule = session.createCriteria(WtDiscountRule.class);
        criteriaWtDiscountRule.setFetchMode("categoryOrgs", FetchMode.JOIN);
        return criteriaWtDiscountRule.list();
    }

    private void addRulesWithEmptyCategoryOrgSet(List discountRules, Long idOfOrg) {
        List<Long> fOrgs = DAOService.getInstance().findFriendlyOrgsIds(idOfOrg);
        for (Object object : discountRules) {
            DiscountRule discountRule = (DiscountRule) object;
            if (containRule(discountRule.getIdOfRule())) {
                continue;
            }
            /* если правила не установлены категории организаций то отправляем*/
            if (discountRule.getCategoryOrgs().isEmpty()) {
                addDCRI(new DiscountCategoryRuleItem(discountRule, idOfOrg, fOrgs));
            }
        }
    }

    //wt
    private void addWtRulesWithEmptyCategoryOrgSet(List<WtDiscountRule> wtDiscountRules, Long idOfOrg) {
        List<Long> fOrgs = DAOService.getInstance().findFriendlyOrgsIds(idOfOrg);
        List<CategoryOrg> allCategoryOrgs = DAOReadonlyService.getInstance().getAllWtCategoryOrgs(wtDiscountRules);
        List<CategoryDiscount> allCategoryDisounts = DAOReadonlyService.getInstance().getAllWtCategoryDiscounts(wtDiscountRules);
        for (WtDiscountRule rule : wtDiscountRules) {
            if (containWtRule(rule.getIdOfRule())) {
                continue;
            }
            if (getCategoryOrgsByWtDiscountRule(allCategoryOrgs, rule).isEmpty()) {
                addWtDCRI(new DiscountCategoryWtRuleItem(allCategoryOrgs, allCategoryDisounts, rule, fOrgs));
            }
        }
    }

    private void addRulesForOrgWithCategoryOrgSet(List discountRules, Org org) {
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        if (categoryOrgSet.isEmpty()) {
            /* Организация не пренадлежит ни к одной категории*/
            existOrgWithEmptyCategoryOrgSet = true;
            return;
        }
        List<Long> fOrgs = DAOService.getInstance().findFriendlyOrgsIds(org.getIdOfOrg());
        for (Object object : discountRules) {
            DiscountRule discountRule = (DiscountRule) object;
            if (containRule(discountRule.getIdOfRule())) {
                continue;
            }
            /*
             * проверяем вхождение одного множества в другое
             * результат categoryOrgSet.containsAll(discountRule.getCategoryOrgs())
             * вернет true если все категории организации взятые из таблицы организации
             * пренадлежат категорий организаций приявязанных к Правилам скидок.
             *
             * Если все категории организации содержатся в правиле то выводим
             * */
            boolean bIncludeRule = false;
            if (discountRule.getCategoryOrgs().isEmpty()) {
                bIncludeRule = true;
            } else if (categoryOrgSet.containsAll(discountRule.getCategoryOrgs())) {
                bIncludeRule = true;
            }
            if (bIncludeRule) {
                addDCRI(new DiscountCategoryRuleItem(discountRule, org.getIdOfOrg(), fOrgs));
            }
        }
    }

    //wt
    private void addWtRulesForOrgWithCategoryOrgSet(List<WtDiscountRule> wtDiscountRules, Org org) {
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        if (categoryOrgSet.isEmpty()) {
            existOrgWithEmptyCategoryOrgSet = true;
            return;
        }
        List<Long> fOrgs = DAOService.getInstance().findFriendlyOrgsIds(org.getIdOfOrg());
        List<CategoryOrg> allCategoryOrgs = DAOReadonlyService.getInstance().getAllWtCategoryOrgs(wtDiscountRules);
        List<CategoryDiscount> allCategoryDiscounts = DAOReadonlyService.getInstance().getAllWtCategoryDiscounts(wtDiscountRules);
        for (WtDiscountRule rule : wtDiscountRules) {
            if (containWtRule(rule.getIdOfRule())) {
                continue;
            }
            boolean bIncludeRule = false;
            List<CategoryOrg> categoryOrgs = getCategoryOrgsByWtDiscountRule(allCategoryOrgs, rule);
            if (categoryOrgs.isEmpty()) {
                bIncludeRule = true;
            } else if (categoryOrgSet.containsAll(categoryOrgs)) {
                bIncludeRule = true;
            }
            if (bIncludeRule) {
                addWtDCRI(new DiscountCategoryWtRuleItem(allCategoryOrgs, allCategoryDiscounts, rule, fOrgs));
            }
        }
    }

    private static List<CategoryOrg> getCategoryOrgsByWtDiscountRule(List<CategoryOrg> allCategoryOrgs, WtDiscountRule rule) {
        List<CategoryOrg> list = new ArrayList<>();
        for (CategoryOrg categoryOrg : allCategoryOrgs) {
            if (rule.getCategoryOrgs().contains(categoryOrg)) list.add(categoryOrg);
        }
        return list;
    }

    private static List<CategoryDiscount> getCategoryDiscountsByWtDiscountRule(List<CategoryDiscount> allCategoryDiscounts, WtDiscountRule rule) {
        List<CategoryDiscount> list = new ArrayList<>();
        for (CategoryDiscount categoryDiscount : allCategoryDiscounts) {
            if (rule.getCategoryDiscounts().contains(categoryDiscount)) list.add(categoryDiscount);
        }
        return list;
    }

    private Set<Org> getProcessedOrgs(Session session, Long idOfOrg, boolean manyOrgs) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        Set<Org> result = new HashSet<Org>();
        result.add(org);
        if (manyOrgs) {
            result.addAll(org.getFriendlyOrg());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void addCategoryDiscounts(Session session) {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        criteria.setFetchMode("categoriesDiscountDSZN", FetchMode.JOIN);
        List<CategoryDiscount> categoryDiscounts = (List<CategoryDiscount>) criteria.list();
        for (CategoryDiscount categoryDiscount : categoryDiscounts) {
            DiscountCategoryItem dci = new DiscountCategoryItem(categoryDiscount.getIdOfCategoryDiscount(),
                    categoryDiscount.getCategoryName(), categoryDiscount.getCategoryType().getValue(),
                    categoryDiscount.getDiscountRules(), categoryDiscount.getOrgType(),
                    categoryDiscount.getBlockedToChange());
            addDCI(dci);
        }
    }

    @SuppressWarnings("unchecked")
    private void addCategoryDiscountsDSZN(Session session, Long versionDSZN, Long idOfOrg) {
        //Org org = (Org) session.load(Org.class, idOfOrg);
        //if(!org.getChangesDSZN()) { // Флаг работы со льготами ДСЗН
        //    return;
        //}
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.gt("version", versionDSZN));
        List<CategoryDiscountDSZN> categoriesDiscountDSZN = (List<CategoryDiscountDSZN>) criteria.list();
        for (CategoryDiscountDSZN discountDSZN : categoriesDiscountDSZN) {
            CategoryDiscountDSZNItem c = new CategoryDiscountDSZNItem(discountDSZN.getCode(),
                    discountDSZN.getCategoryDiscount() != null ? discountDSZN.getCategoryDiscount()
                            .getIdOfCategoryDiscount() : null, discountDSZN.getDescription(), discountDSZN.getVersion(),
                    discountDSZN.getDeleted(), discountDSZN.getGuid());
            addDCIDSZN(c);
        }
    }

    private boolean containRule(long idOfRule) {
        for (DiscountCategoryRuleItem dcri : dcris) {
            if (dcri.getIdOfRule() == idOfRule) {
                return true;
            }
        }
        return false;
    }

    //wt
    private boolean containWtRule(long idOfRule) {
        for (DiscountCategoryWtRuleItem wtDcri : wtDcris) {
            if (wtDcri.getIdOfRule() == idOfRule) {
                return true;
            }
        }
        return false;
    }

    private void addDCI(DiscountCategoryItem dci) {
        this.dcis.add(dci);
    }

    private void addDCIDSZN(CategoryDiscountDSZNItem c) {
        this.dcriDSZN.add(c);
    }

    private void addDCRI(DiscountCategoryRuleItem dcri) {
        this.dcris.add(dcri);
    }

    //wt
    private void addWtDCRI(DiscountCategoryWtRuleItem wtDcri) {
        this.wtDcris.add(wtDcri);
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResCategoriesDiscountsAndRules");
        for (DiscountCategoryItem dci : this.dcis) {
            element.appendChild(dci.toElement(document));
        }
        for (DiscountCategoryRuleItem dcri : this.dcris) {
            element.appendChild(dcri.toElement(document));
        }

        //wt
        for (DiscountCategoryWtRuleItem wtDcri : this.wtDcris) {
            element.appendChild(wtDcri.toElement(document));
        }

        for (CategoryDiscountDSZNItem dcriDSZN : this.dcriDSZN) {
            element.appendChild(dcriDSZN.toElement(document));
        }
        return element;
    }

    @Override
    public String toString() {
        return "ResCategoriesDiscountsAndRules{" + "dcis=" + dcis + ", dcris=" + dcris + ", wtDcris=" + wtDcris + '}';
    }

    private static class DiscountCategoryItem {

        private long idOfCategoryDiscount;
        private String categoryName;
        private Integer categoryType;
        private Integer orgType;
        private String discountRules;
        private Boolean blockedToChange;

        public DiscountCategoryItem(long idOfCategoryDiscount, String categoryName, Integer categoryType,
                String discountRules, Integer organizationType, Boolean blockedToChange) {
            this.idOfCategoryDiscount = idOfCategoryDiscount;
            this.categoryName = categoryName;
            this.discountRules = discountRules;
            this.categoryType = categoryType;
            this.orgType = organizationType;
            this.blockedToChange = blockedToChange;
        }

        public long getIdOfCategoryDiscount() {
            return idOfCategoryDiscount;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public Integer getCategoryType() {
            return categoryType;
        }

        public String getDiscountRules() {
            return discountRules;
        }

        public Integer getOrgType() {
            return orgType;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("DCI");
            element.setAttribute("IdOfCategoryDiscount", Long.toString(this.idOfCategoryDiscount));
            element.setAttribute("CategoryName", this.categoryName);
            element.setAttribute("CategoryType", Integer.toString(this.categoryType));
            element.setAttribute("OrgType", Integer.toString(this.orgType));
            element.setAttribute("BlockedToChange", blockedToChange ? "1" : "0");
            return element;
        }

        @Override
        public String toString() {
            return "DiscountCategoryItem{" + "idOfCategoryDiscount=" + idOfCategoryDiscount + ", categoryName='"
                    + categoryName + ", discountRules='" + discountRules + '\'' + '}';
        }
    }

    private static class DiscountCategoryRuleItem {

        private long idOfRule;
        private String description;
        private int priority;
        private String categoryDiscounts;
        private Boolean operationor;
        private String complexesMap;
        private String subCategory;
        private String orgIds;
        private Boolean deletedState;

        public String getComplexesMap() {
            return complexesMap;
        }

        public void setComplexesMap(String complexesMap) {
            this.complexesMap = complexesMap;
        }

        public Boolean getOperationor() {
            return operationor;
        }

        public void setOperationor(Boolean operationor) {
            this.operationor = operationor;
        }

        public String getCategoryDiscounts() {
            return categoryDiscounts;
        }

        public int getPriority() {
            return priority;
        }

        public Boolean getDeletedState() {
            return deletedState;
        }

        public void setDeletedState(Boolean deletedState) {
            this.deletedState = deletedState;
        }

        //

        public DiscountCategoryRuleItem(DiscountRule discountRule, Long idOfOrg, List<Long> fOrgs) {
            this.idOfRule = discountRule.getIdOfRule();
            this.description = discountRule.getDescription();
            this.categoryDiscounts = discountRule.getCategoryDiscounts();
            this.priority = discountRule.getPriority();
            this.operationor = discountRule.getOperationOr();
            this.complexesMap = discountRule.getComplexesMap();
            this.subCategory = discountRule.getSubCategory();
            this.deletedState = discountRule.getDeletedState();
            Set<Long> orgs = new HashSet<Long>();

            for (CategoryOrg categoryOrg : discountRule.getCategoryOrgs()) {
                for (Org org : categoryOrg.getOrgs()) {
                    if (fOrgs.contains(org.getIdOfOrg())) {
                        orgs.add(org.getIdOfOrg());
                    }
                }
            }
            this.orgIds = StringUtils.join(orgs, ',');
        }

        public long getIdOfRule() {
            return idOfRule;
        }

        public String getDescription() {
            return description;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("DCRI");
            element.setAttribute("IdOfRule", Long.toString(this.idOfRule));
            element.setAttribute("Description", this.description);
            element.setAttribute("CategoriesDiscounts", this.categoryDiscounts);
            // атрибуты Complex0...Complex9 удалены как неактуальные (использовались для АРМ версий 60 и ниже)
            element.setAttribute("Priority", Integer.toString(this.priority));
            element.setAttribute("OperationOr", Boolean.toString(this.operationor));
            if (StringUtils.isNotEmpty(complexesMap)) {
                element.setAttribute("ComplexesMap", this.complexesMap);
            }
            element.setAttribute("SubCategory", this.subCategory);
            element.setAttribute("OrgIds", this.orgIds);
            element.setAttribute("D", Boolean.toString(this.deletedState));
            return element;
        }

        @Override
        public String toString() {
            return "DCRI{" + "idOfRule=" + idOfRule + ", categoriesDiscounts='" + categoryDiscounts + '\''
                    + ", description='" + description + '\'' + ", priority=" + priority + ", operationor=" + operationor
                    + ", complexesMap=\'" + complexesMap + '\'' + ", subCategory='" + subCategory + '\'' + ", orgIds='"
                    + orgIds + '\'' + '}';
        }

        public String getOrgIds() {
            return orgIds;
        }
    }

    //wt
    private static class DiscountCategoryWtRuleItem {

        private long idOfRule;
        private String description;
        private int priority;
        private String categoryDiscounts;
        private Boolean operationOr;
        private String complexesMap;
        private String subCategory;
        private String orgIds;
        private Boolean deletedState;

        public long getIdOfRule() {
            return idOfRule;
        }

        public String getDescription() {
            return description;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public String getOrgIds() {
            return orgIds;
        }

        public void setIdOfRule(long idOfRule) {
            this.idOfRule = idOfRule;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getCategoryDiscounts() {
            return categoryDiscounts;
        }

        public void setCategoryDiscounts(String categoryDiscounts) {
            this.categoryDiscounts = categoryDiscounts;
        }

        public Boolean getOperationOr() {
            return operationOr;
        }

        public void setOperationOr(Boolean operationOr) {
            this.operationOr = operationOr;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public void setOrgIds(String orgIds) {
            this.orgIds = orgIds;
        }

        public String getComplexesMap() {
            return complexesMap;
        }

        public void setComplexesMap(String complexesMap) {
            this.complexesMap = complexesMap;
        }

        public Boolean getDeletedState() {
            return deletedState;
        }

        public void setDeletedState(Boolean deletedState) {
            this.deletedState = deletedState;
        }

        public DiscountCategoryWtRuleItem(List<CategoryOrg> allCategoryOrgs, List<CategoryDiscount> allCategoryDiscounts, WtDiscountRule wtDiscountRule, List<Long> fOrgs) {
            this.idOfRule = wtDiscountRule.getIdOfRule();
            this.description = wtDiscountRule.getDescription();

            this.categoryDiscounts = buildCategoryDiscounts(allCategoryDiscounts, wtDiscountRule);

            this.priority = wtDiscountRule.getPriority();
            this.operationOr = wtDiscountRule.isOperationOr();

            this.complexesMap = buildComplexesMap(wtDiscountRule);

            this.subCategory = wtDiscountRule.getSubCategory();
            Set<Long> orgs = new HashSet<>();

            List<CategoryOrg> categoryOrgs = getCategoryOrgsByWtDiscountRule(allCategoryOrgs, wtDiscountRule);
            for (CategoryOrg categoryOrg : categoryOrgs) {
                for (Org org : categoryOrg.getOrgs()) {
                    if (fOrgs.contains(org.getIdOfOrg())) {
                        orgs.add(org.getIdOfOrg());
                    }
                }
            }
            this.orgIds = StringUtils.join(orgs, ',');
            this.deletedState = wtDiscountRule.getDeletedState();
        }

        private String buildComplexesMap(WtDiscountRule wtDiscountRule) {
            List<WtComplex> complexes = DAOService.getInstance().getComplexesByWtDiscountRule(wtDiscountRule);
            StringBuilder sb = new StringBuilder();
            for (WtComplex complex : complexes) {
                sb.append(complex.getIdOfComplex()).append("=1;");
            }
            return sb.toString();
        }

        private String buildCategoryDiscounts(List<CategoryDiscount> allCategoryDiscounts, WtDiscountRule wtDiscountRule) {
            List<CategoryDiscount> categoryDiscounts = getCategoryDiscountsByWtDiscountRule(allCategoryDiscounts, wtDiscountRule);
            StringBuilder sb = new StringBuilder();
            int counter = categoryDiscounts.size();
            for (CategoryDiscount category : categoryDiscounts) {
                sb.append(category.getIdOfCategoryDiscount());
                if (counter > 1) {
                    sb.append(",");
                    counter--;
                }
            }
            return sb.toString();
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("DCRI");
            element.setAttribute("IdOfRule", Long.toString(this.idOfRule));
            element.setAttribute("Description", this.description);
            element.setAttribute("CategoriesDiscounts", this.categoryDiscounts);
            element.setAttribute("Priority", Integer.toString(this.priority));
            element.setAttribute("OperationOr", Boolean.toString(this.operationOr));
            if (StringUtils.isNotEmpty(complexesMap)) {
                element.setAttribute("ComplexesMap", this.complexesMap);
            }
            element.setAttribute("SubCategory", this.subCategory);
            element.setAttribute("OrgIds", this.orgIds);
            element.setAttribute("D", Boolean.toString(this.deletedState));
            return element;
        }

        @Override
        public String toString() {
            return "DCRI{" + "idOfRule=" + idOfRule + ", categoriesDiscounts='" + categoryDiscounts + '\''
                    + ", description='" + description + '\'' + ", priority=" + priority + ", operationOr=" + operationOr
                    + ", complexesMap=\'" + complexesMap + '\'' + ", subCategory='" + subCategory + '\'' + ", orgIds='"
                    + orgIds + '\'' + '}';
        }
    }

    private static class CategoryDiscountDSZNItem {
        private Integer code;
        private Long categoryId;
        private String description;
        private Long version;
        private Boolean deleted;
        private String guid;

        public CategoryDiscountDSZNItem(Integer code, Long categoryId, String description, Long version,
                Boolean deleted, String guid) {
            this.code = code;
            this.categoryId = categoryId;
            this.description = description;
            this.version = version;
            this.deleted = deleted;
            this.guid = guid;
        }

        public Integer getCode() {
            return code;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public String getDescription() {
            return description;
        }

        public Long getVersion() {
            return version;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("DCRI_DSZN");
            element.setAttribute("Code", Integer.toString(this.code));
            element.setAttribute("CategoryId", this.categoryId != null ? Long.toString(this.categoryId) : "");
            element.setAttribute("Name", this.description);
            element.setAttribute("V", Long.toString(this.version));
            element.setAttribute("D", Boolean.toString(this.deleted));
            element.setAttribute("Guid", this.guid);
            return element;
        }

        @Override
        public String toString() {
            return "CategoryDiscountDSZNItem{" + "code=" + code + ", description='" + description + '\'' + ", version="
                    + version + ", deleted=" + deleted + '}';
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }
    }

}
