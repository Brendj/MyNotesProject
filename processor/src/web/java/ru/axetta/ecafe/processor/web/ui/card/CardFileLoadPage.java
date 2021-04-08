/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.util.ParseUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardFileLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(CardFileLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfCard;

        public LineResult(long lineNo, int resultCode, String message, Long idOfCard) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = message;
            this.idOfCard = idOfCard;
        }

        public long getLineNo() {
            return lineNo;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public Long getIdOfCard() {
            return idOfCard;
        }
    }

    private boolean checkCardPrintedNoUnique = true;
    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;

    public String getPageFilename() {
        return "card/load_file";
    }

    public boolean isCheckCardPrintedNoUnique() {
        return checkCardPrintedNoUnique;
    }

    public void setCheckCardPrintedNoUnique(boolean checkCardPrintedNoUnique) {
        this.checkCardPrintedNoUnique = checkCardPrintedNoUnique;
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public int getLineResultSize() {
        return lineResults.size();
    }

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

    public void fill(Session persistenceSession) throws Exception {
        // Nothing to do here
    }

    public void loadCards(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        TimeZone localTimeZone = runtimeContext
                .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);
        CardManager cardManager = runtimeContext.getCardManager();

        long lineCount = dataSize / 200;
        if (lineCount > MAX_LINE_NUMBER) {
            lineCount = MAX_LINE_NUMBER;
        }
        List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
        int lineNo = 0;
        int successLineNumber = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String currLine = reader.readLine();
        while (null != currLine) {
            LineResult result = null;
            try {
                result = createCard(runtimeContext, cardManager, dateFormat, timeFormat, currLine, lineNo);
            } catch (Exception e) {
                logger.warn("Failed", e);
                result = new LineResult(lineNo, 500, e.getMessage(), null);
            }
            if (result.getResultCode() == 0) {
                ++successLineNumber;
            }
            lineResults.add(result);
            currLine = reader.readLine();
            if (lineNo == MAX_LINE_NUMBER) {
                break;
            }
            ++lineNo;
        }

        this.lineResults = lineResults;
        this.successLineNumber = successLineNumber;
    }

    private LineResult createCard(RuntimeContext runtimeContext, CardManager cardManager, DateFormat dateFormat,
            DateFormat timeFormat, String line, int lineNo) throws Exception {
        String[] tokens = line.split(";");
        if (tokens.length < 9) {
            return new LineResult(lineNo, 1, "Not enough data", null);
        }
        Long contractId = Long.parseLong(tokens[0]);
        Long idOfClient = getIdOfClient(runtimeContext, contractId);
        if (null == idOfClient) {
            return new LineResult(lineNo, 2,
                    String.format("Client not found: contractId == %s", ContractIdFormat.format(contractId)), null);
        }
        long cardNo = Long.parseLong(tokens[1]);
        Long cardPrintedNo = StringUtils.isEmpty(tokens[2]) ? null : Long.parseLong(tokens[2]);

        if (checkCardPrintedNoUnique) {
            if (cardPrintedNo == null) {
                return new LineResult(lineNo, 11, "Card printed no. is absent", null);
            }
            if (existCard(runtimeContext, cardPrintedNo)) {
                return new LineResult(lineNo, 10, "Duplicate card (card printed no.)", null);
            }
        }

        int cardType = Integer.parseInt(tokens[3]);
        int state = Integer.parseInt(tokens[4]);
        int lifeState = Integer.parseInt(tokens[5]);

        Date validTime = parseTimeOrDate(tokens[8], dateFormat, timeFormat);
        Date issueTime = null;
        String lockReason = null;
        if (tokens.length >= 10) {
            issueTime = StringUtils.isEmpty(tokens[9]) ? null : parseTimeOrDate(tokens[9], dateFormat, timeFormat);
            if (tokens.length >= 11) {
                lockReason = tokens[10];
            }
        }
        try {
            Long idOfCard = cardManager
                    .createCard(idOfClient, cardNo, cardType, state, validTime, lifeState, lockReason, issueTime,
                            cardPrintedNo, null);
            return new LineResult(lineNo, 0, "Ok", idOfCard);
        } catch (Exception e) {
            logger.debug("Failed to create card", e);
            return new LineResult(lineNo, 1, e.getMessage(), null);
        }
    }

    private static Long getIdOfClient(RuntimeContext runtimeContext, Long contractId) throws Exception {
        Long idOfClient = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (null != client) {
                idOfClient = client.getIdOfClient();
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idOfClient;
    }

    private static Date parseTimeOrDate(String text, DateFormat dateFormat, DateFormat timeFormat) throws Exception {
        try {
            return ParseUtils.parseDateTime(timeFormat, text);
        } catch (Exception e) {
            return ParseUtils.parseDateTime(dateFormat, text);
        }
    }

    private static boolean existCard(RuntimeContext runtimeContext, long cardPrintedNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            boolean result = DAOUtils.existCard(persistenceSession, cardPrintedNo);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}