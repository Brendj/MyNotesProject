/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class OptionPage extends BasicWorkspacePage {

    private Option withOperatorOption;
    private Option notifyBySMSAboutEnterEventOption; 

    private Boolean notifyBySMSAboutEnterEvent;

    private Boolean withOperator;

    public Boolean getWithOperator() {
        return withOperator;
    }

    public void setWithOperator(Boolean withOperator) {
        this.withOperator = withOperator;
    }

    public Boolean getNotifyBySMSAboutEnterEvent() {
        return notifyBySMSAboutEnterEvent;
    }

    public void setNotifyBySMSAboutEnterEvent(Boolean notifyBySMSAboutEnterEvent) {
        this.notifyBySMSAboutEnterEvent = notifyBySMSAboutEnterEvent;
    }

    public String getPageFilename() {
        return "option/option";
    }

    public void fill(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Option.class);
        criteria.add(Restrictions.eq("idOfOption", 2L));
        withOperatorOption = (Option)criteria.uniqueResult();
        withOperator = withOperatorOption.getOptionText().equals("1");
        criteria = session.createCriteria(Option.class);
        criteria.add(Restrictions.eq("idOfOption", 3L));
        notifyBySMSAboutEnterEventOption = (Option)criteria.uniqueResult();
        notifyBySMSAboutEnterEvent = notifyBySMSAboutEnterEventOption.getOptionText().equals("1");
    }

    public void save(Session session) {
        withOperatorOption.setOptionText(withOperator.equals(true) ? "1" : "0");
        notifyBySMSAboutEnterEventOption.setOptionText(notifyBySMSAboutEnterEvent.equals(true) ? "1" : "0");
        session.merge(withOperatorOption);
        session.merge(notifyBySMSAboutEnterEventOption);
    }

    public void cancelOption() {
        withOperator = withOperatorOption.getOptionText().equals("1");
        notifyBySMSAboutEnterEvent = notifyBySMSAboutEnterEventOption.getOptionText().equals("1");
    }
}
