/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDeletePage extends BasicPage {

    public void removeCategory(Session session, Long id) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount) session.load(CategoryDiscount.class, id);
        Criteria clientCriteria = session.createCriteria(Client.class);
        Criterion exp1 = Restrictions.or(
            Restrictions.like("categoriesDiscounts", categoryDiscount.getIdOfCategoryDiscount() + "", MatchMode.EXACT),
            Restrictions.like("categoriesDiscounts", categoryDiscount.getIdOfCategoryDiscount() + ",",
                MatchMode.START));
        Criterion exp2 = Restrictions.or(
            Restrictions.like("categoriesDiscounts", "," + categoryDiscount.getIdOfCategoryDiscount(),
                MatchMode.END),
            Restrictions.like("categoriesDiscounts", "," + categoryDiscount.getIdOfCategoryDiscount() + ",",
                MatchMode.ANYWHERE));
        Criterion expression = Restrictions.or(exp1, exp2);
        clientCriteria.add(expression);
        List<Client> clients = clientCriteria.list();
        for (Client client : clients) {
            String categoriesDiscounts = client.getCategoriesDiscounts();
            if (categoriesDiscounts.contains("," + id + ","))
                categoriesDiscounts = categoriesDiscounts.replace("," + id + ",", ",");
            else if (categoriesDiscounts.startsWith(id + ","))
                categoriesDiscounts = categoriesDiscounts.substring((id + ",").length());
            else if (categoriesDiscounts.endsWith("," + id))
                categoriesDiscounts = categoriesDiscounts.substring(0, categoriesDiscounts.length() - ("," + id).length());
            else
                categoriesDiscounts = categoriesDiscounts.replace("" + id, "");
            client.setCategoriesDiscounts(categoriesDiscounts);
            session.save(client);
        }

        session.delete(categoryDiscount);
    }
}
