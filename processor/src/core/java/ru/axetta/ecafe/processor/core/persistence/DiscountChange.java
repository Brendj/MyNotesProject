/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 21.03.16
 * Time: 10:09
 */
public class DiscountChange {

    private Long idOfDiscountChange;
    private Date registrationDate;
    private Client client;
    private Integer discountMode;
    private Integer oldDiscountMode;
    private String categoriesDiscounts;
    private String oldCategoriesDiscounts;

    public DiscountChange() {
    }

    public DiscountChange(Client client) {
        this.registrationDate = new Date();
        this.client = client;
    }

    public DiscountChange(Client client, Integer discountMode, Integer oldDiscountMode, String categoriesDiscounts,
            String oldCategoriesDiscounts) {
        this.registrationDate = new Date();
        this.client = client;
        this.discountMode = discountMode;
        this.oldDiscountMode = oldDiscountMode;
        this.categoriesDiscounts = categoriesDiscounts;
        this.oldCategoriesDiscounts = oldCategoriesDiscounts;
    }

    public String getCategoriesDiscountsString(String categoriesDiscounts) {
        if (categoriesDiscounts.isEmpty()) {
            return "";
        } else {
            Session session = null;
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
            } catch (Exception e) {
            }
            String[] discountCategories = categoriesDiscounts.split(",");
            List<Long> categoriesId = new ArrayList<Long>();
            for (String s : discountCategories) {
                if (!s.isEmpty()) {
                    categoriesId.add(Long.parseLong(s));
                }
            }
            Criteria categoryCriteria = session.createCriteria(CategoryDiscount.class);
            categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", categoriesId));
            StringBuilder clientCategories = new StringBuilder();
            for (Object object : categoryCriteria.list()) {
                CategoryDiscount categoryDiscount = (CategoryDiscount) object;
                clientCategories.append(categoryDiscount.getCategoryName());
                clientCategories.append(",");
            }
            session.close();
            return clientCategories.substring(0, clientCategories.length() - 1);

        }
    }

    public String getDiscountModeString(Integer discountMode) {
        if (discountMode == 0) {
            return "Отсутствует";
        }
        if (discountMode == 1) {
            return "Дотация";
        }
        if (discountMode == 2) {
            return "Бесплатно";
        }
        if (discountMode == 3) {
            return "Льгота по категориям";
        }
        return "";
    }

    public Long getIdOfDiscountChange() {
        return idOfDiscountChange;
    }

    public void setIdOfDiscountChange(Long idOfDiscountChange) {
        this.idOfDiscountChange = idOfDiscountChange;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getDiscountMode() {
        return discountMode;
    }

    public void setDiscountMode(Integer discountMode) {
        this.discountMode = discountMode;
    }

    public Integer getOldDiscountMode() {
        return oldDiscountMode;
    }

    public void setOldDiscountMode(Integer oldDiscountMode) {
        this.oldDiscountMode = oldDiscountMode;
    }

    public String getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(String categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public String getOldCategoriesDiscounts() {
        return oldCategoriesDiscounts;
    }

    public void setOldCategoriesDiscounts(String oldCategoriesDiscounts) {
        this.oldCategoriesDiscounts = oldCategoriesDiscounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscountChange)) {
            return false;
        }

        DiscountChange that = (DiscountChange) o;

        if (!idOfDiscountChange.equals(that.idOfDiscountChange)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfDiscountChange.hashCode();
    }
}