/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.stoplist;


import generated.msr.stoplist.LongRunningStopListService;
import generated.msr.stoplist.LongRunningStopListService_Service;
import generated.msr.stoplist.schema.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.BindingProvider;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 01.10.12
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class MSRStopListLoader {

    private String WS_LOGIN = null;
    private String WS_PASSWORD = null;
    private String WS_END_POINT = null;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MSRStopListLoader.class);
    private boolean loggingEnabled = false;
    private DatatypeFactory df;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private DAOService daoService = DAOService.getInstance();

    private Date updateDate;
    private LongRunningStopListService port = null;
    private String wsUID = ""; // ID сессис web-службы


    public boolean getLogging() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSR_STOPLIST_LOGGING);
    }


    public static void setLogging(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_MSR_STOPLIST_LOGGING, "" + (on ? "1" : "0"));
    }


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSR_STOPLIST_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_MSR_STOPLIST_ON, "" + (on ? "1" : "0"));
    }


    private void setLastUpdateDate(Date date) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_MSR_STOPLIST_UPD_TIME, dateFormat.format(date));
    }


    private Date getLastUpdateDate() {
        try {
            String d = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSR_STOPLIST_UPD_TIME);
            if (d == null || d.length() < 1) {
                return new Date(0);
            }
            return dateFormat.parse(d);
        } catch (Exception e) {
            log("Failed to parse date from options");
        }
        return new Date(0);
    }


    public static String getLogin() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSR_STOPLIST_USER);
    }


    public static void setLogin(String login) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_MSR_STOPLIST_USER, login);
    }


    public static String getPassword() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSR_STOPLIST_PSWD);
    }


    public static void setPassword(String password) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_MSR_STOPLIST_PSWD, password);
    }


    public static String getURL() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSR_STOPLIST_URL);
    }


    public static void setURL(String url) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_MSR_STOPLIST_URL, url);
    }


    private Date getNowUpdateDate() {
        return new Date();
    }


    private void init() {
        WS_LOGIN = getLogin();
        WS_PASSWORD = getPassword();
        WS_END_POINT = getURL();
        loggingEnabled = getLogging();

        log("Start stoplist updating. Logging in using login: " + WS_LOGIN + " and pwsd: " + WS_PASSWORD + "...");
        LongRunningStopListService_Service service = new LongRunningStopListService_Service();
        service.setHandlerResolver(new HandlerResolver() {
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlerList = new ArrayList<Handler>();
                handlerList.add(new AuthenticationHandler(WS_LOGIN, WS_PASSWORD));
                return handlerList;
            }
        });
        port = service.getLongRunningStopListServicePort();
        Map context = ((BindingProvider) port).getRequestContext();
        context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_END_POINT);
    }


    public void initCards() throws Exception {
        if (!RuntimeContext.getInstance().isMainNode() && !isOn()) {
            return;
        }

        try {
            if (port == null) {
                init();
            }
            Date last = getLastUpdateDate();
            Date now = getNowUpdateDate();
            if (now.compareTo(last) <= 0) {
                return;
            }
            log("Get from [" + last + "] to [" + now + "]");
            wsUID = port.submitCardsTask(createCardsTaskRequest(last, now)).getUid();
            updateDate = now;
            log("Authorisation success, received UID: " + wsUID);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }


    public void parseCards() {
        setOn(false);
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("MSR Stop List importer is turned off. You have to activate this tool using common Settings");
            return;
        }

        if (wsUID == null || wsUID.length() < 1) {
            return;
        }


        String uid = new String(wsUID);
        wsUID = null;
        try {
            log("Receiving out previous [" + uid + "] task completion state...");
            String state = port.getTaskState(createTaskStateRequest(uid)).getTaskState().value();
            while (!state.equalsIgnoreCase("done")) {
                log("Our task is not finished yet [" + state + "]. Wait for 10 seconds and retry...");
                wsUID = new String(uid);
                return;
            }
            log("Our task is finished");


            log("Receiving count of cards...");
            int count = port.getTaskCount(createTaskCountRequest(uid)).getCount();
            log("There are " + count + " since last revise");


            log("Receiving a list of cards...");
            List<StopListCardReply> cards = port.getCards(createCardsRequest(uid, 0, count)).getCards();
            log("There are blocked cards bellow: ");
            for (StopListCardReply card : cards) {
                if (card.getCurrentState().trim().equals("11") ||
                        card.getCurrentState().trim().equals("12") ||
                        card.getCurrentState().trim().equals("13") ||
                        card.getCurrentState().trim().equals("14") ||
                        card.getCurrentState().trim().equals("21") ||
                        card.getCurrentState().trim().equals("22")) {
                    log(card.getIdentifier() + " is blocked. Update it's status...");
                    daoService.setCardStatus(Long.parseLong(card.getIdentifier()), Card.LOCKED_STATE,
                            "Заблокировано МСР");
                }
                if (card.getCurrentState().trim().equals("5")) {
                    log(card.getIdentifier() + " is unlocked. Update it's status...");
                    daoService.setCardStatus(Long.parseLong(card.getIdentifier()), Card.ACTIVE_STATE, "");
                }
            }
            log("Operations are finished. Update uploading history in database...");
            setLastUpdateDate(updateDate);
            log("DB updating complete.");
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }


    private CardsTaskRequest createCardsTaskRequest(Date dateFrom, Date dateTo) throws Exception {
        CardsTaskRequest req = new CardsTaskRequest();
        req.setFromDate(toGregorean(dateFrom));
        req.setToDate(toGregorean(dateTo));
        req.setIdentityType(IdentityType.MUID);
        return req;
    }


    private TaskStateRequest createTaskStateRequest(String uid) {
        TaskStateRequest req = new TaskStateRequest();
        req.setUid(uid);
        return req;
    }


    private TaskCountRequest createTaskCountRequest(String uid) {
        TaskCountRequest req = new TaskCountRequest();
        req.setUid(uid);
        return req;
    }


    private CardsRequest createCardsRequest(String uid, int first, int count) {
        CardsRequest req = new CardsRequest();
        req.setUid(uid);
        req.setFirst(first);
        req.setCount(count);
        return req;
    }


    private XMLGregorianCalendar toGregorean(Date date) throws Exception {
        if (df == null) {
            try {
                df = DatatypeFactory.newInstance();
            } catch (Exception e) {
                throw new Exception("Failed to intiate datetype factory");
            }
        }

        if (date == null) {
            throw new Exception("Failed to conver date, cause: input parameter is NULL");
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date.getTime());
        return df.newXMLGregorianCalendar(cal);
    }


    private void log(String str) {
        if (loggingEnabled) {
            logger.info(str);
            if (logger.isDebugEnabled()) {
                logger.debug(str);
            }
        }
    }


    class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

        public static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        public static final String PASSWORD_TEXT_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
        public static final String WSSE_SECURITY_LNAME = "Security";
        public static final String WSSE_NS_PREFIX = "wsse";
        private boolean mustUnderstand = false;

        private String username;
        private String password;


        public AuthenticationHandler(String username, String password) {
            this.username = username;
            this.password = password;
        }


        public Set<QName> getHeaders() {
            return new TreeSet();
        }

        public boolean handleFault(SOAPMessageContext context) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void close(MessageContext context) {
        }

        public boolean handleMessage(SOAPMessageContext context) {
            Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (outboundProperty.booleanValue()) {
                try {
                    addSecurityHeader(context);
                } catch (Exception e) {
                    System.out.println("Exception in handler: " + e);
                    return false;
                }
            }
            return true;
        }


        private void addSecurityHeader(SOAPMessageContext messageContext) throws SOAPException {
            SOAPFactory sf = SOAPFactory.newInstance();
            SOAPHeader header = messageContext.getMessage().getSOAPPart().getEnvelope().getHeader();
            if (header == null) {
                header = messageContext.getMessage().getSOAPPart().getEnvelope().addHeader();
            }

            Name securityName = sf.createName(WSSE_SECURITY_LNAME, WSSE_NS_PREFIX, WSSE_NS);
            SOAPHeaderElement securityElem = header.addHeaderElement(securityName);
            securityElem.setMustUnderstand(mustUnderstand);

            Name usernameTokenName = sf.createName("UsernameToken", WSSE_NS_PREFIX, WSSE_NS);
            SOAPElement usernameTokenMsgElem = sf.createElement(usernameTokenName);

            Name usernameName = sf.createName("Username", WSSE_NS_PREFIX, WSSE_NS);
            SOAPElement usernameMsgElem = sf.createElement(usernameName);
            usernameMsgElem.addTextNode("i-teco");
            usernameTokenMsgElem.addChildElement(usernameMsgElem);

            Name passwordName = sf.createName("Type", WSSE_NS_PREFIX, WSSE_NS);
            SOAPElement passwordMsgElem = sf.createElement("Password", WSSE_NS_PREFIX, WSSE_NS);

            passwordMsgElem.addAttribute(passwordName, PASSWORD_TEXT_TYPE);
            passwordMsgElem.addTextNode("s4529qp2");
            usernameTokenMsgElem.addChildElement(passwordMsgElem);

            securityElem.addChildElement(usernameTokenMsgElem);
        }
    }
}