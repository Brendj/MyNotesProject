/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;

import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class OnlineReportPageOnePerUser extends OnlineReportPage {

    protected static final Map<Long, HashSet<String>> activeUserReports = new HashMap<Long, HashSet<String>>();

    public abstract Object exportToHtml();

    public abstract void exportToXLS(ActionEvent actionEvent);

    public Object exportToHtmlOnePerUser() {
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        HttpSession httpSession = request.getSession(true);
        Long idOfUser = (Long)httpSession.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
        String className = this.getClass().getSimpleName();
        if (!isAllowed(idOfUser, className)) {
            return null;
        }
        try {
            HashSet<String> reports = activeUserReports.containsKey(idOfUser) ? activeUserReports.get(idOfUser) : new HashSet<String>();
            reports.add(className);
            activeUserReports.put(idOfUser, reports);
            return exportToHtml();
        } finally {
            activeUserReports.get(idOfUser).remove(className);
        }
    }

    public void exportToXLSOnePerUser(ActionEvent actionEvent) {
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        HttpSession httpSession = request.getSession(true);
        Long idOfUser = (Long)httpSession.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
        String className = this.getClass().getSimpleName();
        if (!isAllowed(idOfUser, className)) {
            return;
        }
        try {
            HashSet<String> reports = activeUserReports.containsKey(idOfUser) ? activeUserReports.get(idOfUser) : new HashSet<String>();
            reports.add(className);
            activeUserReports.put(idOfUser, reports);
            exportToXLS(actionEvent);
        } finally {
            activeUserReports.get(idOfUser).remove(className);
        }
    }

    private boolean isAllowed(Long idOfUser, String className) {
        if (activeUserReports.containsKey(idOfUser)) {
            Set<String> reports = activeUserReports.get(idOfUser);
            if (reports.contains(className)) {
                printError("Этот отчет уже выполняется в данный момент под вашей учетной записью");
                return false;
            }
        }
        return true;
    }
}
