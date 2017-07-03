/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.Visitor;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.persistence.Visitor.VISITORDOGM_TYPE;
import static ru.axetta.ecafe.processor.core.persistence.Visitor.isEmptyDocumentParams;
import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.isDateEqLtCurrentDate;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 27.06.17
 * Time: 10:37
 */

public class VisitorDogmLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(VisitorDogmLoadPage.class);
    private static final long MAX_LINE_NUMBER = 50000;

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfVisitor;

        public LineResult(long lineNo, int resultCode, String message, Long idOfVisitor) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = message;
            this.idOfVisitor = idOfVisitor;
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

        public Long getIdOfVisitor() {
            return idOfVisitor;
        }
    }

    private boolean checkVisitorNoUnique = true;
    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;

    public String getPageFilename() {
        return "visitorsdogm/visitordogm/load_visitors";
    }

    public boolean isCheckVisitorNoUnique() {
        return checkVisitorNoUnique;
    }

    public void setCheckVisitorNoUnique(boolean checkVisitorNoUnique) {
        this.checkVisitorNoUnique = checkVisitorNoUnique;
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

    public void loadVisitors(InputStream inputStream, long dataSize) throws Exception {
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
                result = createVisitor(currLine, lineNo);
            } catch (Exception e) {
                logger.warn("Failed to create new visitor", e);
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

    private LineResult createVisitor(String line, int lineNo) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        format.setLenient(false);

        String[] tokens = line.split(";");
        if(tokens.length < 6) {
            return new LineResult(lineNo, 8, "Недостаточно данных.", null);
        }

        String surname = tokens[0].trim();
        String firstName = tokens[1].trim();
        String secondName = tokens[2].trim();

        String position = tokens[3];

        Date passportDate;
        String passportNumber = tokens[5].replace(" ", "");
        Date driverLicenceDate;
        String driverLicenceNumber = tokens.length > 7 ? tokens[7].replace(" ", "") : null;
        Date warTicketDate;
        String warTicketNumber = tokens.length > 9 ? tokens[9].replace(" ", ""): null;

        if(StringUtils.isEmpty(surname)) {
            return new LineResult(lineNo, 1, "Поле \"фамилия\" не найдено.", null);
        }
        if(StringUtils.isEmpty(firstName)) {
            return new LineResult(lineNo, 2, "Поле \"имя\" не найдено.", null);
        }
        if(position.length() > 256) {
            return new LineResult(lineNo, 3, "Длина поля \"должность\" должна быть не более 256 символов.", null);
        }

        try {
            passportDate = StringUtils.isNotEmpty(tokens[4].trim()) ? format.parse(tokens[4].trim()) : null;
            driverLicenceDate = tokens.length > 6 ? StringUtils.isNotEmpty(tokens[6].trim()) ? format.parse(tokens[6].trim()) : null : null;
            warTicketDate = tokens.length > 8 ? StringUtils.isNotEmpty(tokens[8].trim()) ? format.parse(tokens[8].trim()) : null : null;
        } catch (ParseException e) {
            return new LineResult(lineNo, 4, "Поле \"Дата выдачи документа\" имеет неверный формат.", null);
        }

        if(isEmptyDocumentParams(driverLicenceNumber, driverLicenceDate) &&
                isEmptyDocumentParams(passportNumber, passportDate) &&
                isEmptyDocumentParams(warTicketNumber, warTicketDate)) {
            return new LineResult(lineNo, 5, "Отсутствует информация об удостоверении личности.", null);
        }

        if(isDateEqLtCurrentDate(driverLicenceDate) ||
                isDateEqLtCurrentDate(passportDate) ||
                isDateEqLtCurrentDate(warTicketDate)) {
            return new LineResult(lineNo, 6, "Неверная дата выдачи документа.", null);
        }

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (checkVisitorNoUnique) {
                if (DAOUtils.existVisitorDogm(persistenceSession, passportNumber, driverLicenceNumber, warTicketNumber)) {
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    return new LineResult(lineNo, 7, "Сотрудник с такими же серией и номером документа уже существует.", null);
                }
            }

            Person person = new Person(firstName, surname, secondName);
            persistenceSession.save(person);
            Visitor visitor = new Visitor(person, passportNumber, passportDate,
                    driverLicenceNumber, driverLicenceDate, warTicketNumber, warTicketDate, VISITORDOGM_TYPE, position);
            persistenceSession.save(visitor);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            return new LineResult(lineNo, 0, "Сотрудник внесен в базу.", visitor.getIdOfVisitor());
        } catch (Exception e) {
            logger.debug("Failed to create visitor from file: ", e);
            return new LineResult(lineNo, 10, e.getMessage(), null);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}
