/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ClientAuthToken;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.CycleDiagramIn;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWSService;

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
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 26.11.13
 * Time: 12:03
 */

public class SubFeedingServlet extends HttpServlet {

    private ClientRoomController clientRoomController;
    private static final String root = "/subfeeding/pages/";
    private static final Logger logger = LoggerFactory.getLogger(SubFeedingServlet.class);
    private static final Map<Integer, String> daysByNumber = new HashMap<Integer, String>();
    private static final String SUCCESS_MESSAGE = "subFeedingSuccess";
    private static final String ERROR_MESSAGE = "subFeedingError";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        clientRoomController = new ClientRoomControllerWSService().getClientRoomControllerWSPort();
        daysByNumber.put(1, "monday");
        daysByNumber.put(2, "tuesday");
        daysByNumber.put(3, "wednesday");
        daysByNumber.put(4, "thursday");
        daysByNumber.put(5, "friday");
        daysByNumber.put(6, "saturday");
        daysByNumber.put(7, "sunday");
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
                activateSubFeeding(req, resp);
            } else if (path.equals("/suspend")) {
                suspendSubscriptionFeeding(req, resp);
            } else if (path.equals("/reopen")) {
                reopenSubFeeding(req, resp);
            } else {
                sendRedirect(req, resp, "/index");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            SubscriptionFeeding sf = DAOUtils.findClientSubscriptionFeeding(session, contractId);
            Client client = DAOUtils.findClientByContractId(session, contractId);
            req.setAttribute("client", client);
            req.setAttribute("subscriptionFeeding", sf);
            if (sf == null) {
                req.setAttribute("complexes", DAOUtils.findComplexesWithSubFeeding(session, client.getOrg()));
            } else {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                Date startDate = StringUtils.isBlank(req.getParameter("startDate")) ? CalendarUtils
                        .truncateToDayOfMonth(new Date()) : parseDate(req.getParameter("startDate"), df);
                Date endDate = StringUtils.isBlank(req.getParameter("endDate")) ? new Date()
                        : parseDate(req.getParameter("endDate"), df);
                if (startDate == null || endDate == null) {
                    req.setAttribute(ERROR_MESSAGE, "Введенные даты имеют неправильный формат.");
                    req.setAttribute("startDate", req.getParameter("startDate"));
                    req.setAttribute("endDate", req.getParameter("endDate"));
                } else {
                    Long subBalanceNumber = Long.parseLong(contractId + "01");
                    req.setAttribute("payments",
                            clientRoomController.getPaymentList(subBalanceNumber, startDate, endDate));
                    req.setAttribute("purchases",
                            clientRoomController.getPurchaseList(subBalanceNumber, startDate, endDate));
                    req.setAttribute("startDate", df.format(startDate));
                    req.setAttribute("endDate", df.format(endDate));
                }
            }
            transaction.commit();
            outputPage("view", req, resp);
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private void activateSubFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        String complexParamPrefix = "complex_option_";
        if (checkRequest(req, complexParamPrefix)) {
            CycleDiagramIn cycle = new CycleDiagramIn();
            for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
                if (entry.getKey().contains(complexParamPrefix)) {
                    String[] ids = StringUtils.split(entry.getValue()[0], '_');
                    String complexId = ids[0];
                    int dayNumber = Integer.parseInt(ids[1]);
                    addComplexValue(cycle, StringUtils.capitalize(daysByNumber.get(dayNumber)), complexId);
                }
            }
            Result res = clientRoomController.createSubscriptionFeeding(contractId, cycle);
            if (res.resultCode == 0) {
                req.setAttribute(SUCCESS_MESSAGE, "Подписка успешно активирована.");
            } else {
                req.setAttribute(ERROR_MESSAGE, res.description);
            }
        } else {
            req.setAttribute(ERROR_MESSAGE, "Для активации подписки АП необходимо заполнить циклограмму.");
        }
        showSubscriptionFeeding(req, resp);
    }

    private boolean checkRequest(HttpServletRequest request, String complexParamPrefix) {
        boolean flag = false;
        for (String key : request.getParameterMap().keySet()) {
            if (key.contains(complexParamPrefix)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void addComplexValue(CycleDiagramIn cd, String fieldName, String value) throws Exception {
        Class<?> cdClass = cd.getClass();
        String getter = "get" + fieldName;
        String setter = "set" + fieldName;
        Method getterMethod = cdClass.getMethod(getter);
        Method setterMethod = cdClass.getMethod(setter, String.class);
        String fieldValue = (String) getterMethod.invoke(cd);
        if (fieldValue == null) {
            setterMethod.invoke(cd, value);
        } else {
            setterMethod.invoke(cd, StringUtils.join(new Object[]{fieldValue, value}, ','));
        }
    }

    private void suspendSubscriptionFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        Result res = clientRoomController.suspendSubscriptionFeeding(contractId);
        if (res.resultCode == 0) {
            req.setAttribute(SUCCESS_MESSAGE, "Подписка успешно приостановлена.");
        } else {
            req.setAttribute(ERROR_MESSAGE, res.description);
        }
        showSubscriptionFeeding(req, resp);
    }

    private void reopenSubFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        Result res = clientRoomController.reopenSubscriptionFeeding(contractId);
        if (res.resultCode == 0) {
            req.setAttribute(SUCCESS_MESSAGE, "Подписка возобновлена.");
        } else {
            req.setAttribute(ERROR_MESSAGE, res.description);
        }
        showSubscriptionFeeding(req, resp);
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
