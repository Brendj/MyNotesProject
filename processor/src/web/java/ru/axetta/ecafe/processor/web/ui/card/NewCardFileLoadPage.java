/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Liya on 01.04.2016.
 */
public class NewCardFileLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(NewCardFileLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfNewCard;

        public LineResult(long lineNo, int resultCode, String message, Long idOfNewCard) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = message;
            this.idOfNewCard = idOfNewCard;
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

        public Long getIdOfNewCard() {
            return idOfNewCard;
        }
    }

    private boolean checkCardPrintedNoUnique = true;
    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;
    private String cardTypeNames = formCardTypeNames();

    public static String formCardTypeNames(){
        StringBuilder sb = new StringBuilder();
        sb.append("Возможные типы карт: ");
        for(int i = 0; i < Card.TYPE_NAMES.length; i++){
            if(Card.TYPE_NAMES[i].equals(Card.UNKNOWN_TYPE_NAME)){
                continue;
            }
            sb.append(String.format("%d - %s", i, Card.TYPE_NAMES[i]));
            if(i != (Card.TYPE_NAMES.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append(".");
        return sb.toString();
    }

    public String getCardTypeNames(){
        return cardTypeNames;
    }

    public void setCardTypeNames(String cardTypeNames) {
        this.cardTypeNames = cardTypeNames;
    }

    public String getPageFilename() {
        return "card/load_new_cards";
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
                result = createCard(runtimeContext, cardManager, currLine, lineNo);
            } catch (Exception e) {
                logger.warn("Failed to create new card", e);
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

    private LineResult createCard(RuntimeContext runtimeContext, CardManager cardManager,
                    String line, int lineNo) throws Exception {
        String[] tokens = line.split(";");
        if (tokens.length < 2) {
            return new LineResult(lineNo, 1, "Недостаточно данных.", null);
        }
        long cardNo = Long.parseLong(tokens[0]);
        Long cardPrintedNo = StringUtils.isEmpty(tokens[1]) ? null : Long.parseLong(tokens[1]);

        if (cardPrintedNo == null) {
            return new LineResult(lineNo, 11, "Номер, нанесенный на карту не найден.", null);
        }

        if (checkCardPrintedNoUnique) {
            if (existCard(runtimeContext, cardPrintedNo)) {
                return new LineResult(lineNo, 10, "Карта с данным нанесенным номером уже существует.", null);
            }
        }
        Integer cardType = null;
        if(tokens.length > 2){
            cardType = StringUtils.isEmpty(tokens[2]) ? null : Integer.parseInt(tokens[2]);
        }
        if(cardType != null && !(cardType > 0 && cardType <= Card.TYPE_NAMES.length)){
            return new LineResult(lineNo, 12, "Неверный тип карты.", null);
        }

        try {
            Long idOfCard = cardManager
                    .createNewCard(cardNo, cardPrintedNo, null, cardType);
            return new LineResult(lineNo, 0, "Карта внесена в базу.", idOfCard);
        } catch (Exception e) {
            logger.debug("Failed to create card", e);
            return new LineResult(lineNo, 1, e.getMessage(), null);
        }
    }


    private static boolean existCard(RuntimeContext runtimeContext, long cardPrintedNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            boolean result = DAOUtils.existCard(persistenceSession, cardPrintedNo) ||
                    DAOUtils.existNewCard(persistenceSession, cardPrintedNo);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}
