/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ClientAuthToken;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.CycleDiagramIn;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWSService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 26.11.13
 * Time: 12:03
 */

public class SubFeedingServlet extends HttpServlet {

    private ClientRoomController clientRoomController;
    private RuntimeContext runtimeContext;
    private SubscriptionFeedingService sfService;
    private static final String root = "/subfeeding/pages/";
    private static final Logger logger = LoggerFactory.getLogger(SubFeedingServlet.class);
    private static final String SUCCESS_MESSAGE = "subFeedingSuccess";
    private static final String ERROR_MESSAGE = "subFeedingError";
    private static final String COMPLEX_PARAM_PREFIX = "complex_option_";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        clientRoomController = new ClientRoomControllerWSService().getClientRoomControllerWSPort();
        runtimeContext = RuntimeContext.getInstance();
        sfService = RuntimeContext.getAppContext().getBean(SubscriptionFeedingService.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String path = req.getPathInfo();
        try {
            if (path.equals("/index") || path.equals("/")) {
                req.getRequestDispatcher(root + "index.jsp").forward(req, resp);
            } else if (path.equals("/login")) {
                authorizeClient(req, resp);
            } else if (path.equals("/view")) {
                showSubscriptionFeeding(req, resp);
            } else if (path.equals("/activate")) {
                activateSubscriptionFeeding(req, resp);
            } else if (path.equals("/suspend")) {
                suspendSubscriptionFeeding(req, resp);
            } else if (path.equals("/reopen")) {
                reopenSubscriptionFeeding(req, resp);
            } else if (path.equals("/plan")) {
                showSubscriptionFeedingPlan(req, resp);
            } else if (path.equals("/edit")) {
                editSubscriptionFeedingPlan(req, resp);
            } else if (path.equals("/logout")) {
                logout(req, resp);
            } else {
                sendRedirect(req, resp, "/index");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            req.getSession().invalidate();
            resp.getWriter().print(ex.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void authorizeClient(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId;
        try {
            contractId = ContractIdFormat.parse(req.getParameter("contractId"));
        } catch (Exception ex) {
            logger.error("Problem parsing contract id = {}", req.getParameter("contractId"));
            req.setAttribute(ERROR_MESSAGE, "Ошибка авторизации клиента");
            outputPage("index", req, resp);
            return;
        }
        String mobPhone = PhoneNumberCanonicalizator.canonicalize(req.getParameter("password"));
        Result res = clientRoomController.authorizeClient(contractId, CryptoUtils.MD5(mobPhone));
        if (res.resultCode == 0) {
            ClientAuthToken token = new ClientAuthToken(contractId, false);
            token.storeTo(req.getSession());
            sendRedirect(req, resp, "/view");
        } else {
            req.setAttribute(ERROR_MESSAGE, res.description);
            outputPage("index", req, resp);
        }
    }

    private void showSubscriptionFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            SubscriptionFeeding sf = sfService.findClientSubscriptionFeeding(contractId);
            Client client = DAOUtils.findClientByContractId(session, contractId);
            client.getPerson(); // нужно для ФИО.
            transaction.commit();
            req.setAttribute("client", client);
            req.setAttribute("subscriptionFeeding", sf);
            if (sf == null) {
                sendRedirect(req, resp, "/plan");
            } else {
                DateFormat df = CalendarUtils.getDateFormatLocal();
                Date startDate = StringUtils.isBlank(req.getParameter("startDate")) ? null
                        : parseDate(req.getParameter("startDate"), df);
                Date endDate = StringUtils.isBlank(req.getParameter("endDate")) ? null
                        : parseDate(req.getParameter("endDate"), df);
                if (startDate == null || endDate == null) {
                    Date[] week = CalendarUtils.getCurrentWeekBeginAndEnd(new Date());
                    startDate = week[0];
                    endDate = week[1];
                }
                Long subBalanceNumber = Long.parseLong(contractId + "01");
                req.setAttribute("payments", clientRoomController.getPaymentList(subBalanceNumber, startDate, endDate));
                req.setAttribute("purchases",
                        clientRoomController.getPurchaseList(subBalanceNumber, startDate, endDate));
                req.setAttribute("startDate", df.format(startDate));
                req.setAttribute("endDate", df.format(endDate));
                outputPage("view", req, resp);
            }
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private void activateSubscriptionFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        if (checkComplexesChecked(req)) {
            CycleDiagramIn cycle = getSubFeedingPlan(req);
            Result res = clientRoomController.createSubscriptionFeeding(contractId, cycle);
            if (res.resultCode == 0) {
                req.setAttribute(SUCCESS_MESSAGE, "Подписка успешно активирована.");
                showSubscriptionFeeding(req, resp);
            } else {
                req.setAttribute(ERROR_MESSAGE, res.description);
                showSubscriptionFeedingPlan(req, resp);
            }
        } else {
            req.setAttribute(ERROR_MESSAGE, "Для активации подписки АП необходимо заполнить циклограмму.");
            showSubscriptionFeedingPlan(req, resp);
        }
    }

    private void editSubscriptionFeedingPlan(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        if (checkComplexesChecked(req)) {
            CycleDiagramIn cycle = getSubFeedingPlan(req);
            if (isPlanChanged(req, cycle)) {
                Result res = clientRoomController.editSubscriptionFeedingPlan(contractId, cycle);
                if (res.resultCode == 0) {
                    CycleDiagram cd = sfService.findLastCycleDiagram(contractId);
                    DateFormat df = CalendarUtils.getDateFormatLocal();
                    req.setAttribute(SUCCESS_MESSAGE,
                            "Изменения плана питания успешно сохранены. Изменения вступят в силу " + df
                                    .format(cd.getDateActivationDiagram()));
                } else {
                    req.setAttribute(ERROR_MESSAGE, res.description);
                }
            } else {
                sendRedirect(req, resp, "/plan");
                return;
            }
        } else {
            req.setAttribute(ERROR_MESSAGE, "Для сохранения плана АП необходимо заполнить циклограмму.");
        }
        showSubscriptionFeedingPlan(req, resp);
    }

    private boolean isPlanChanged(HttpServletRequest req, CycleDiagramIn cycle) {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        CycleDiagram cd = sfService.findLastCycleDiagram(contractId);
        Map<Integer, List<String>> activeComplexes = splitPlanComplexes(cd);
        for (Map.Entry<Integer, List<String>> entry : activeComplexes.entrySet()) {
            int dayNumber = entry.getKey();
            String newValue = cycle.getDayValue(dayNumber);
            boolean isEqual = CollectionUtils.isEqualCollection(entry.getValue(),
                    Arrays.asList(StringUtils.split(StringUtils.defaultString(newValue), ';')));
            if (!isEqual) {
                return true;
            }
        }
        return false;
    }

    private void showSubscriptionFeedingPlan(HttpServletRequest req, HttpServletResponse resp) {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = DAOUtils.findClientByContractId(session, contractId);
            client.getPerson(); client.getOrg();
            transaction.commit();
            SubscriptionFeeding sf = sfService.findClientSubscriptionFeeding(contractId);
            req.setAttribute("client", client);
            req.setAttribute("subscriptionFeeding", sf);
            req.setAttribute("complexes", sfService.findComplexesWithSubFeeding(client.getOrg()));
            CycleDiagram cd = sfService.findClientCycleDiagram(contractId);
            if (sf != null && cd != null) {
                req.setAttribute("activeComplexes", splitPlanComplexes(cd));
            }
            outputPage("plan", req, resp);
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private Map<Integer, List<String>> splitPlanComplexes(CycleDiagram cd) {
        Map<Integer, List<String>> activeComplexes = new HashMap<Integer, List<String>>();
        activeComplexes.put(1, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getMonday()), ';')));
        activeComplexes.put(2, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getTuesday()), ';')));
        activeComplexes.put(3, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getWednesday()), ';')));
        activeComplexes.put(4, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getThursday()), ';')));
        activeComplexes.put(5, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getFriday()), ';')));
        activeComplexes.put(6, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getSaturday()), ';')));
        activeComplexes.put(7, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getSunday()), ';')));
        return activeComplexes;
    }

    private CycleDiagramIn getSubFeedingPlan(HttpServletRequest req) throws Exception {
        CycleDiagramIn cycle = new CycleDiagramIn();
        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            if (entry.getKey().contains(COMPLEX_PARAM_PREFIX)) {
                String[] ids = StringUtils.split(entry.getValue()[0], '_');
                String complexId = ids[0];
                int dayNumber = Integer.parseInt(ids[1]);
                String prevValue = cycle.getDayValue(dayNumber);
                cycle.setDayValue(dayNumber, prevValue == null ? complexId : (prevValue + ";" + complexId));
            }
        }
        return cycle;
    }

    private boolean checkComplexesChecked(HttpServletRequest request) {
        boolean flag = false;
        for (String key : request.getParameterMap().keySet()) {
            if (key.contains(COMPLEX_PARAM_PREFIX)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void suspendSubscriptionFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        Result res = clientRoomController.suspendSubscriptionFeeding(contractId);
        if (res.resultCode == 0) {
            sendRedirect(req, resp, "/view");
        } else {
            req.setAttribute(ERROR_MESSAGE, res.description);
            showSubscriptionFeeding(req, resp);
        }
    }

    private void reopenSubscriptionFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        Result res = clientRoomController.reopenSubscriptionFeeding(contractId);
        if (res.resultCode == 0) {
            DateFormat df = CalendarUtils.getDateFormatLocal();
            String reopenDate = df.format(CalendarUtils.addDays(new Date(), 2));
            req.setAttribute(SUCCESS_MESSAGE, "Подписка возобновлена. Формирование заказов начнется с " + reopenDate);
        } else {
            req.setAttribute(ERROR_MESSAGE, res.description);
        }
        showSubscriptionFeeding(req, resp);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getSession().invalidate();
        sendRedirect(req, resp, "/index");
    }

    private void outputPage(String name, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(root + name + ".jsp");
        rd.forward(req, resp);
    }

    private void sendRedirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(String.format("%s%s%s", req.getContextPath(), req.getServletPath(), path));
    }

    private Date parseDate(String source, DateFormat df) {
        Date res = null;
        try {
            res = df.parse(source);
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        }
        return res;
    }
}
