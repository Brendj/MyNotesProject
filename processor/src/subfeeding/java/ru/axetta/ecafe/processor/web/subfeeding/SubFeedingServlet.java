/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.ClientAuthToken;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWSService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.addDays;
import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.truncateToDayOfMonth;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 26.11.13
 * Time: 12:03
 */

public class SubFeedingServlet extends HttpServlet {

    private ClientRoomController clientRoomController;
    private static final String root = "/WEB-INF/pages/";
    private static final Logger logger = LoggerFactory.getLogger(SubFeedingServlet.class);
    private static final String SUCCESS_MESSAGE = "subFeedingSuccess";
    private static final String ERROR_MESSAGE = "subFeedingError";
    private static final String COMPLEX_PARAM_PREFIX = "complex_option_";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String appPropPath = getServletContext().getInitParameter("appProperties");
        InputStream is = getServletContext().getResourceAsStream(appPropPath);
        Properties properties = new Properties();
        try {
            properties.load(is);
            URL wsdl = new URL(properties.getProperty("clientServiceWsdlLocation"));
            clientRoomController = new ClientRoomControllerWSService(wsdl).getClientRoomControllerWSPort();
        } catch (Exception ex) {
            throw new ServletException(ex);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            if (path == null || path.equals("/index") || path.equals("/")) {
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
            } else if (path.equals("/transfer")) {
                processBalanceTransfer(req, resp);
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
        ClientSummaryResult client = clientRoomController.getSummary(contractId);
        SubFeedingResult sf = clientRoomController.findSubscriptionFeeding(contractId);
        req.setAttribute("client", client.clientSummary);
        req.setAttribute("subscriptionFeeding", sf);
        if (sf.getIdOfSubscriptionFeeding() == null) {
            sendRedirect(req, resp, "/plan");
        } else {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
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
            req.setAttribute("purchases", clientRoomController.getPurchaseList(subBalanceNumber, startDate, endDate));
            req.setAttribute("transfers", clientRoomController.getTransferSubBalanceList(contractId, startDate, endDate));
            req.setAttribute("startDate", df.format(startDate));
            req.setAttribute("endDate", df.format(endDate));
            outputPage("view", req, resp);
        }
    }

    private void activateSubscriptionFeeding(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        if (checkComplexesChecked(req)) {
            CycleDiagramIn cycle = getSubFeedingPlan(req);

            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            Date dateActivate = StringUtils.isBlank(req.getParameter("dateActivate")) ? null
                    : parseDate(req.getParameter("dateActivate"), df);

            Result res = clientRoomController.createSubscriptionFeeding(contractId, cycle, dateActivate);
            if (res.resultCode == 0) {
                req.setAttribute(SUCCESS_MESSAGE, "Подписка успешно подключена.");
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
                CycleDiagramOut res = clientRoomController.editSubscriptionFeedingPlan(contractId, cycle);
                if (res.resultCode == 0) {
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                    String stringDate = df.format(res.getDateActivationDiagram());
                    String message = "Изменения циклограммы успешно сохранены. Изменения вступят в силу " + stringDate;
                    req.setAttribute(SUCCESS_MESSAGE, message);
                } else {
                    req.setAttribute(ERROR_MESSAGE, res.description);
                }
            } else {
                sendRedirect(req, resp, "/plan");
                return;
            }
        } else {
            req.setAttribute(ERROR_MESSAGE, "Для сохранения циклограммы необходимо ее заполнить.");
        }
        showSubscriptionFeedingPlan(req, resp);
    }

    private boolean isPlanChanged(HttpServletRequest req, CycleDiagramIn cycle) {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        CycleDiagramOut cd = clientRoomController.findClientCycleDiagram(contractId);
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

    private void showSubscriptionFeedingPlan(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        ClientSummaryResult client = clientRoomController.getSummary(contractId);
        SubFeedingResult sf = clientRoomController.findSubscriptionFeeding(contractId);
        req.setAttribute("client", client.clientSummary);
        req.setAttribute("subscriptionFeeding", sf);
        req.setAttribute("complexes",
                clientRoomController.findComplexesWithSubFeeding(contractId).getComplexInfoList().getList());
        CycleDiagramOut cd = clientRoomController.findClientCycleDiagram(contractId);
        if (cd.getGlobalId() != null) {
            req.setAttribute("activeComplexes", splitPlanComplexes(cd));
        }
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        if(sf.getIdOfSubscriptionFeeding()==null){
            SubscriptionFeedingSettingResult settingResult = clientRoomController.getSubscriptionFeedingSetting(
                    contractId);
            Date activationDate = addDays(truncateToDayOfMonth(new Date()), 100);
            if (settingResult.resultCode == 0) {
                int dayForbidChange = settingResult.subscriptionFeedingSettingExt.getDayForbidChange();
                activationDate = addDays(truncateToDayOfMonth(new Date()), 1 + dayForbidChange);
            } else {
                req.setAttribute(ERROR_MESSAGE, settingResult.description);
            }
            req.setAttribute("dateActivate", df.format(activationDate));
            req.setAttribute("subscriptionFeeding", sf);
        } else {
            req.setAttribute("dateActivate", df.format(sf.getDateActivate()));
        }
        outputPage("plan", req, resp);
    }

    private Map<Integer, List<String>> splitPlanComplexes(CycleDiagramOut cd) {
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
        for (String key: req.getParameterMap().keySet()){
            if (key.contains(COMPLEX_PARAM_PREFIX)) {
                String[] ids = StringUtils.split(key, '_');
                String complexId = ids[3];
                int dayNumber = Integer.parseInt(ids[2]);
                String prevValue = cycle.getDayValue(dayNumber);
                cycle.setDayValue(dayNumber, prevValue == null ? complexId : (prevValue + ";" + complexId));
            }
        }
        //for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
        //    if (entry.getKey().contains(COMPLEX_PARAM_PREFIX)) {
        //        String[] ids = StringUtils.split(entry.getValue()[0], '_');
        //        String complexId = ids[0];
        //        int dayNumber = Integer.parseInt(ids[1]);
        //        String prevValue = cycle.getDayValue(dayNumber);
        //        cycle.setDayValue(dayNumber, prevValue == null ? complexId : (prevValue + ";" + complexId));
        //    }
        //}
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
            req.setAttribute(SUCCESS_MESSAGE, "Подписка возобновлена.");
        } else {
            req.setAttribute(ERROR_MESSAGE, res.description);
        }
        showSubscriptionFeeding(req, resp);
    }

    private void processBalanceTransfer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long contractId = ClientAuthToken.loadFrom(req.getSession()).getContractId();
        String action = req.getParameter("stage");
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        Date startDate = StringUtils.isBlank(req.getParameter("startDate")) ? null
                : parseDate(req.getParameter("startDate"), df);
        Date endDate = StringUtils.isBlank(req.getParameter("endDate")) ? null
                : parseDate(req.getParameter("endDate"), df);
        if (startDate == null || endDate == null) {
            Date[] week = CalendarUtils.getCurrentWeekBeginAndEnd(new Date());
            startDate = week[0];
            endDate = week[1];
        }
        req.setAttribute("transfers", clientRoomController.getTransferSubBalanceList(contractId, startDate, endDate));
        req.setAttribute("startDate", df.format(startDate));
        req.setAttribute("endDate", df.format(endDate));
        if ("createTransfer".equals(action)) {
            String transferAmount = req.getParameter("transferAmount");
            String transferDirection = req.getParameter("transferDirection");
            Result result;
            if ("toSub".equals(transferDirection)) {
                result = clientRoomController
                        .transferBalance(contractId, 0, 1, CurrencyStringUtils.rublesToCopecks(transferAmount));
            } else if ("fromSub".equals(transferDirection)) {
                result = clientRoomController
                        .transferBalance(contractId, 1, 0, CurrencyStringUtils.rublesToCopecks(transferAmount));
            } else {
                outputPage("transfer", req, resp);
                return;
            }
            if (result.resultCode == 0) {
                req.setAttribute(SUCCESS_MESSAGE, "Перевод средств прошел успешно.");
            } else {
                req.setAttribute(ERROR_MESSAGE, result.description);
            }
        }

        ClientSummaryResult client = clientRoomController.getSummary(contractId);
        req.setAttribute("client", client.clientSummary);
        outputPage("transfer", req, resp);
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
