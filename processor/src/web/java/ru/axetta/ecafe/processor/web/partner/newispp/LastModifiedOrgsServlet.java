/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.newispp;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.02.16
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
@Component
@WebServlet(
        name = "LastModifiedOrgsServlet",
        description = "LastModifiedOrgsServlet",
        urlPatterns = {"/modified-orgs"}
)
public class LastModifiedOrgsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LastModifiedOrgsServlet.class);

    protected static final String DATE_AT_PARAMETER_NAME = "at";
    protected static final String RESPONSE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Autowired
    OrgService orgService;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            logger.info(String.format("Starting of callback processing  from %s", request.getRemoteAddr()));

            Long dateAt = getDateAtParameter(request);
            if(dateAt == null) {
                String err = String.format("Обязательный параметр \"Дата от\" (%s) не указан", DATE_AT_PARAMETER_NAME);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, err);
                return;
            }
            long start = System.currentTimeMillis();

            OutputStream os = null;
            OutputStreamWriter osw = null;
            try {
                List<Org> orgs = getLastModifiedOrgs(runtimeContext, dateAt, System.currentTimeMillis());

                JSONArray itemsJSON = wrapItems(orgs);
                JSONObject requestJSON = wrapRequest(request);
                JSONObject responseJSON = wrapResponse(requestJSON, itemsJSON);

                responseJSON.put("buildTime", System.currentTimeMillis() - start);
                String json = responseJSON.toString();

                os = response.getOutputStream();
                osw = new OutputStreamWriter(os);
                osw.write(json);
            } catch (Exception e) {
                String err = String.format("Произошла ошибка при составлении ответа: %s", e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err);
                return;
            } finally {
                if(osw != null) {
                    os.flush();
                    osw.close();
                }
                if(os != null) os.close();
            }

            logger.info(String.format("End of  callback processing from %s", request.getRemoteAddr()));
        } catch (RuntimeContext.NotInitializedException e) {
            logger.error("Failed", e);
            throw new UnavailableException(e.getMessage());
        }
    }



    protected List<Org> getLastModifiedOrgs(RuntimeContext runtimeContext, Long dateAt, Long dateTo) throws Exception {
        if(dateAt == null) {
            dateAt = 0L;
        }
        if(dateTo == null) {
            dateTo = System.currentTimeMillis();
        }

        org.hibernate.Session session = null;
        org.hibernate.Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            logger.debug(String.format("trying to receive args by modification date stating at %s to %s", dateAt, dateTo));
            org.hibernate.Query q = session.createQuery("select o from Org o where o.updateTime!=null and o.updateTime>=:dateAt and o.updateTime<=:dateTo order by o.updateTime desc");
            q.setParameter("dateAt", new Date(dateAt));
            q.setParameter("dateTo", new Date(dateTo));
            List<Org> orgs = q.list();
            if(orgs == null || orgs.size() < 1) {
                logger.debug("there is no just one org with given modification dates range");
                return Collections.EMPTY_LIST;
            }
            logger.debug(String.format("there are %s orgs with given modification dates range", orgs.size()));
            return orgs;
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


    protected JSONArray wrapItems(List<Org> orgs) throws Exception {
        JSONArray res = new JSONArray();
        if(orgs == null || orgs.size() < 1) {
            return res;
        }

        for(Org o : orgs) {
            JSONObject oj = new JSONObject();
            oj.put("idOfOrg", o.getIdOfOrg());
            oj.put("shortName", o.getShortName());
            oj.put("shortNameInfoService", o.getShortNameInfoService());
            oj.put("officialName", o.getOfficialName());
            oj.put("address", o.getAddress());
            oj.put("phone", o.getPhone());
            oj.put("contractId", o.getContractId());
            oj.put("contractTime", o.getContractTime());
            oj.put("state", o.getState());
            oj.put("cardLimit", o.getCardLimit());
            oj.put("publicKey", o.getPublicKey());
            oj.put("lastClientContractId", o.getLastClientContractId());
            oj.put("smsSender", o.getSmsSender());
            oj.put("priceOfSms", o.getPriceOfSms());
            oj.put("subscriptionPrice", o.getSubscriptionPrice());
            oj.put("OGRN", o.getOGRN());
            oj.put("INN", o.getINN());
            oj.put("guid", o.getGuid());
            oj.put("tag", o.getTag());
            oj.put("city", o.getCity());
            oj.put("district", o.getDistrict());
            oj.put("location", o.getLocation());
            oj.put("longitude", o.getLongitude());
            oj.put("latitude", o.getLatitude());
            oj.put("refectoryType", o.getRefectoryType());
            oj.put("fullSyncParam", o.getFullSyncParam());
            oj.put("usePlanOrders", o.getUsePlanOrders());
            oj.put("commodityAccounting", o.getCommodityAccounting());
            oj.put("disableEditingClientsFromAISReestr", o.getDisableEditingClientsFromAISReestr());
            oj.put("usePaydableSubscriptionFeeding", o.getUsePaydableSubscriptionFeeding());
            oj.put("btiUnom", o.getBtiUnom());
            oj.put("btiUnad", o.getBtiUnad());
            oj.put("uniqueAddressId", o.getUniqueAddressId());
            oj.put("introductionQueue", o.getIntroductionQueue());
            oj.put("additionalIdBuilding", o.getAdditionalIdBuilding());
            oj.put("statusDetailing", o.getStatusDetailing());
            oj.put("payByCashier", o.getPayByCashier());
            oj.put("oneActiveCard", o.getOneActiveCard());
            oj.put("updateTime", o.getUpdateTime());
            res.put(oj);
        }
        return res;
    }

    protected JSONObject wrapRequest(HttpServletRequest request) throws Exception {
        JSONObject res = new JSONObject();
        res.put(DATE_AT_PARAMETER_NAME, getDateAtParameter(request));
        return res;
    }

    protected JSONObject wrapResponse(JSONObject requestJSON, JSONArray itemsJSON) throws Exception {
        JSONObject res = new JSONObject();
        res.put("request", requestJSON);
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("items", itemsJSON);
        res.put("response", dataJSON);

        res.put("status", "OK");
        res.put("date", new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(new Date(System.currentTimeMillis())));
        return res;
    }


    protected Long getDateAtParameter(HttpServletRequest request) {
        String at = request.getParameter(DATE_AT_PARAMETER_NAME);
        if(StringUtils.isEmpty(at)) {
            return null;
        }

        try {
            Long val = NumberUtils.toLong(at);
            return val;
        } catch (Exception e) {
            logger.error("Failed to parse input parameter", e);
            return null;
        }
    }
}