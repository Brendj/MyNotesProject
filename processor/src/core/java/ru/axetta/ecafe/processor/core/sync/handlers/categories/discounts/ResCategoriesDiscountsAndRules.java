/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * User: akmukov
 * Date: 30.03.2016
 */
public class ResCategoriesDiscountsAndRules implements AbstractToElement{
    private final List<DiscountCategoryItem> dcis = new LinkedList<DiscountCategoryItem>();
    private final List<DiscountCategoryRuleItem> dcris = new LinkedList<DiscountCategoryRuleItem>();
    private final List<CategoryDiscountDSZNItem> dcriDSZN = new LinkedList<CategoryDiscountDSZNItem>();
    private boolean existOrgWithEmptyCategoryOrgSet;

    public void fillData(Session session, Long idOfOrg, boolean manyOrgs, Long versionDSZN) {
        addDiscountRules(session, idOfOrg, manyOrgs);
        addCategoryDiscounts(session);
        addCategoryDiscountsDSZN(session, versionDSZN, idOfOrg);
    }

    private void addDiscountRules(Session session, Long idOfOrg, boolean manyOrgs) {
        List discountRules = getAllDiscountRules(session);
        Set<Org> orgs = getProcessedOrgs(session, idOfOrg, manyOrgs);
        existOrgWithEmptyCategoryOrgSet = false;
        for (Org org : orgs) {
            addRulesForOrgWithCategoryOrgSet(discountRules, org);
        }
        if (existOrgWithEmptyCategoryOrgSet) {
            addRulesWithEmptyCategoryOrgSet(discountRules, idOfOrg);
        }
    }

    private List getAllDiscountRules(Session session) {
        Criteria criteriaDiscountRule = session.createCriteria(DiscountRule.class);
        return criteriaDiscountRule.list();
    }

    private void addRulesWithEmptyCategoryOrgSet(List discountRules, Long idOfOrg) {
        for (Object object : discountRules) {
            DiscountRule discountRule = (DiscountRule) object;
            if (containRule(discountRule.getIdOfRule())) {
                continue;
            }
                /* если правила не установлены категории организаций то отправляем*/
            if (discountRule.getCategoryOrgs().isEmpty()) {
                addDCRI(new DiscountCategoryRuleItem(discountRule, idOfOrg));
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
                addDCRI(new DiscountCategoryRuleItem(discountRule, org.getIdOfOrg()));
            }
        }
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
        List<CategoryDiscount> categoryDiscounts = (List<CategoryDiscount>) criteria.list();
        for (CategoryDiscount categoryDiscount : categoryDiscounts) {
            DiscountCategoryItem dci = new DiscountCategoryItem(categoryDiscount.getIdOfCategoryDiscount(),
                    categoryDiscount.getCategoryName(), categoryDiscount.getCategoryType().getValue(),
                    categoryDiscount.getDiscountRules(), categoryDiscount.getOrgType(), categoryDiscount.getBlockedChange());
            addDCI(dci);
        }
    }

    @SuppressWarnings("unchecked")
    private void addCategoryDiscountsDSZN(Session session, Long versionDSZN, Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        if(!org.getChangesDSZN()) { // Флаг работы со льготами ДСЗН
            return;
        }
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.gt("version", versionDSZN));
        List<CategoryDiscountDSZN> categoriesDiscountDSZN = (List<CategoryDiscountDSZN>) criteria.list();
        for (CategoryDiscountDSZN discountDSZN : categoriesDiscountDSZN) {
            CategoryDiscountDSZNItem c = new CategoryDiscountDSZNItem(discountDSZN.getCode(), discountDSZN.getDescription(),
                    discountDSZN.getVersion(), discountDSZN.getDeleted());
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

    private void addDCI(DiscountCategoryItem dci) {
        this.dcis.add(dci);
    }

    private void addDCIDSZN(CategoryDiscountDSZNItem c) {
        this.dcriDSZN.add(c);
    }

    private void addDCRI(DiscountCategoryRuleItem dcri) {
        this.dcris.add(dcri);
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResCategoriesDiscountsAndRules");
        for (DiscountCategoryItem dci : this.dcis) {
            element.appendChild(dci.toElement(document));
        }
        for (DiscountCategoryRuleItem dcri : this.dcris) {
            element.appendChild(dcri.toElement(document));
        }
        for (CategoryDiscountDSZNItem dcriDSZN : this.dcriDSZN) {
            element.appendChild(dcriDSZN.toElement(document));
        }
        return element;
    }

    @Override
    public String toString() {
        return "ResCategoriesDiscountsAndRules{" +
                "dcis=" + dcis +
                ", dcris=" + dcris +
                '}';
    }

    private static class DiscountCategoryItem {

        private long idOfCategoryDiscount;
        private String categoryName;
        private Integer categoryType;
        private Integer orgType;
        private String discountRules;
        private Boolean blockedChange;

        public DiscountCategoryItem(long idOfCategoryDiscount, String categoryName, Integer categoryType,
                String discountRules, Integer organizationType, Boolean blockedChange) {
            this.idOfCategoryDiscount = idOfCategoryDiscount;
            this.categoryName = categoryName;
            this.discountRules = discountRules;
            this.categoryType = categoryType;
            this.orgType = organizationType;
            this.blockedChange = blockedChange;
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
            element.setAttribute("BlockedChange", blockedChange ? "1" : "0");
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
        private int complex0;
        private int complex1;
        private int complex2;
        private int complex3;
        private int complex4;
        private int complex5;
        private int complex6;
        private int complex7;
        private int complex8;
        private int complex9;
        private int priority;
        private String categoryDiscounts;
        private Boolean operationor;
        private String complexesMap;
        private String subCategory;
        private String orgIds;

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
        //

        public DiscountCategoryRuleItem(DiscountRule discountRule, Long idOfOrg) {
            this.idOfRule = discountRule.getIdOfRule();
            this.description = discountRule.getDescription();
            this.categoryDiscounts = discountRule.getCategoryDiscounts();
            this.complex0 = discountRule.getComplex0();
            this.complex1 = discountRule.getComplex1();
            this.complex2 = discountRule.getComplex2();
            this.complex3 = discountRule.getComplex3();
            this.complex4 = discountRule.getComplex4();
            this.complex5 = discountRule.getComplex5();
            this.complex6 = discountRule.getComplex6();
            this.complex7 = discountRule.getComplex7();
            this.complex8 = discountRule.getComplex8();
            this.complex9 = discountRule.getComplex9();
            this.priority = discountRule.getPriority();
            this.operationor = discountRule.getOperationOr();
            this.complexesMap = discountRule.getComplexesMap();
            this.subCategory = discountRule.getSubCategory();
            Set<Long> orgs = new HashSet<Long>();
            List<Long> fOrgs = DAOService.getInstance().findFriendlyOrgsIds(idOfOrg);
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

        public int getComplex0() {
            return complex0;
        }

        public int getComplex1() {
            return complex1;
        }

        public int getComplex2() {
            return complex2;
        }

        public int getComplex3() {
            return complex3;
        }

        public int getComplex4() {
            return complex4;
        }

        public int getComplex5() {
            return complex5;
        }

        public int getComplex6() {
            return complex6;
        }

        public int getComplex7() {
            return complex7;
        }

        public int getComplex8() {
            return complex8;
        }

        public int getComplex9() {
            return complex9;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("DCRI");
            element.setAttribute("IdOfRule", Long.toString(this.idOfRule));
            element.setAttribute("Description", this.description);
            element.setAttribute("CategoriesDiscounts", this.categoryDiscounts);
            element.setAttribute("Complex0", Integer.toString(this.complex0));
            element.setAttribute("Complex1", Integer.toString(this.complex1));
            element.setAttribute("Complex2", Integer.toString(this.complex2));
            element.setAttribute("Complex3", Integer.toString(this.complex3));
            element.setAttribute("Complex4", Integer.toString(this.complex4));
            element.setAttribute("Complex5", Integer.toString(this.complex5));
            element.setAttribute("Complex6", Integer.toString(this.complex6));
            element.setAttribute("Complex7", Integer.toString(this.complex7));
            element.setAttribute("Complex8", Integer.toString(this.complex8));
            element.setAttribute("Complex9", Integer.toString(this.complex9));
            element.setAttribute("Priority", Integer.toString(this.priority));
            element.setAttribute("OperationOr", Boolean.toString(this.operationor));
            if (StringUtils.isNotEmpty(complexesMap)) {
                element.setAttribute("ComplexesMap", this.complexesMap);
            }
            element.setAttribute("SubCategory", this.subCategory);
            element.setAttribute("OrgIds", this.orgIds);
            return element;
        }

        @Override
        public String toString() {
            return "DCRI{" + "idOfRule=" + idOfRule + ", categoriesDiscounts='" + categoryDiscounts + '\''
                    + ", description='" + description + '\'' + ", complex0=" + complex0 + ", complex1=" + complex1
                    + ", complex2=" + complex2 + ", complex3=" + complex3 + ", complex4=" + complex4 + ", complex5="
                    + complex5 + ", complex6=" + complex6 + ", complex7=" + complex7 + ", complex8=" + complex8
                    + ", complex9=" + complex9 + ", priority=" + priority + ", operationor=" + operationor
                    + ", complexesMap=\'" + complexesMap + '\'' + ", subCategory='" + subCategory + '\''
                    + ", orgIds='" + orgIds + '\'' + '}';
        }

        public String getOrgIds() {
            return orgIds;
        }
    }

    private static class CategoryDiscountDSZNItem {
        private Integer code;
        private String description;
        private Long version;
        private Boolean deleted;

        public CategoryDiscountDSZNItem(Integer code, String description, Long version, Boolean deleted) {
            this.code = code;
            this.description = description;
            this.version = version;
            this.deleted = deleted;
        }

        public Integer getCode() {
            return code;
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
            element.setAttribute("Name", this.description);
            element.setAttribute("V", Long.toString(this.version));
            element.setAttribute("D", Boolean.toString(this.deleted));
            return element;
        }

        @Override
        public String toString() {
            return "CategoryDiscountDSZNItem{" +
                    "code=" + code +
                    ", description='" + description + '\'' +
                    ", version=" + version +
                    ", deleted=" + deleted +
                    '}';
        }
    }

}
