/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.payconv;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.*;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.07.2009
 * Time: 14:45:12
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    private static final int TERMINAL_PAYMENT_METHOD = 4;
    private static final int SYNC_PAYMENT_METHOD = 10;

    private static final String FREECASH_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final String FREECASH_TIME_ZONE = "Europe/Moscow";
    private static final String FREECASH_SOURCE_CHARSET = "windows-1251";
    private static final int FREECASH_PAYMENT_METHOD = TERMINAL_PAYMENT_METHOD;

    private static final String ELECSNET2_TIME_FORMAT = "yyyyMMddHHmmss";
    private static final String ELECSNET2_TIME_ZONE = "Europe/Moscow";
    private static final String ELECSNET2_SOURCE_CHARSET = "windows-1251";
    private static final int ELECSNET2_PAYMENT_METHOD = TERMINAL_PAYMENT_METHOD;

    private static final String MAUSSP_TIME_FORMAT = "dd.MM.yyyy";
    private static final String MAUSSP_TIME_ZONE = "Europe/Moscow";
    private static final String MAUSSP_SOURCE_CHARSET = "windows-1251";
    private static final int MAUSSP_PAYMENT_METHOD = SYNC_PAYMENT_METHOD;

    private static final String MAUSSP_START_TIME_FORMAT = "yyyyMMddHHmmss";
    private static final String MAUSSP_START_TIME_ZONE = "Europe/Moscow";
    private static final String MAUSSP_START_SOURCE_CHARSET = "windows-1251";
    private static final int MAUSSP_START_PAYMENT_METHOD = SYNC_PAYMENT_METHOD;

    private static final String DEST_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final String DEST_TIME_ZONE = "Europe/Moscow";

    private static interface PaymentSystem {

        public static final String[] NAMES = {"freecash", "elecsnet2", "maussp", "maussp_start"};
        public static final int FREE_CASH = 0;
        public static final int ELECSNET2 = 1;
        public static final int MAUSSP = 2;
        public static final int MAUSSP_START = 3;
    }

    private static class ConvParams {

        private static class DestNumberFormats {

            private final DecimalFormat longNumberFormat;

            private DestNumberFormats() {
                this.longNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                this.longNumberFormat.applyPattern("##################0");
            }

            public NumberFormat getLongNumberFormat() {
                return longNumberFormat;
            }
        }

        final DestNumberFormats destNumberFormats;

        final DateFormat destTimeFormat;

        final String sourceFilename;

        final String destFilename;

        final int paymentSystem;

        final long version;

        final long idOfContragent;

        final long idOfPacket;

        final Date syncTime;

        private ConvParams(String sourceFilename, String destFilename, String paymentSystem, long version,
                String idOfContragent, String idOfPacket) throws Exception {
            this.destNumberFormats = new DestNumberFormats();

            TimeZone destTimeZone = TimeZone.getTimeZone(DEST_TIME_ZONE);
            this.destTimeFormat = new SimpleDateFormat(DEST_TIME_FORMAT);
            this.destTimeFormat.setTimeZone(destTimeZone);

            this.sourceFilename = sourceFilename;
            this.destFilename = destFilename;

            this.paymentSystem = ArrayUtils.indexOf(PaymentSystem.NAMES, paymentSystem);
            if (ArrayUtils.INDEX_NOT_FOUND == this.paymentSystem) {
                throw new IllegalArgumentException("Unknown payment system");
            }

            this.version = version;
            this.idOfContragent = Long.parseLong(idOfContragent);
            this.idOfPacket = Long.parseLong(idOfPacket);
            this.syncTime = new Date();
        }

        private ConvParams(ConvParams otherParams) {
            this.destNumberFormats = otherParams.getDestNumberFormats();
            this.destTimeFormat = otherParams.getDestTimeFormat();
            this.sourceFilename = otherParams.getSourceFilename();
            this.destFilename = otherParams.getDestFilename();
            this.paymentSystem = otherParams.getPaymentSystem();
            this.version = otherParams.getVersion();
            this.idOfContragent = otherParams.getIdOfContragent();
            this.idOfPacket = otherParams.getIdOfPacket();
            this.syncTime = otherParams.getSyncTime();
        }

        public DestNumberFormats getDestNumberFormats() {
            return destNumberFormats;
        }

        public DateFormat getDestTimeFormat() {
            return destTimeFormat;
        }

        public String getSourceFilename() {
            return sourceFilename;
        }

        public String getDestFilename() {
            return destFilename;
        }

        public int getPaymentSystem() {
            return paymentSystem;
        }

        public long getVersion() {
            return version;
        }

        public long getIdOfContragent() {
            return idOfContragent;
        }

        public long getIdOfPacket() {
            return idOfPacket;
        }

        public Date getSyncTime() {
            return syncTime;
        }
    }

    private static class FreeCashConvParams extends ConvParams {

        private static class SourceNumberFormats {

            private final DecimalFormat longNumberFormat;
            private final DecimalFormat doubleNumberFormat;

            private SourceNumberFormats() {
                this.longNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                this.longNumberFormat.applyPattern("##################0");
                this.doubleNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator('.');
                this.doubleNumberFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                this.doubleNumberFormat.applyPattern("########0.00");
            }

            public NumberFormat getLongNumberFormat() {
                return longNumberFormat;
            }

            public NumberFormat getDoubleNumberFormat() {
                return doubleNumberFormat;
            }
        }

        final SourceNumberFormats sourceNumberFormats;
        final DateFormat sourceTimeFormat;

        private FreeCashConvParams(ConvParams convParams) throws Exception {
            super(convParams);

            this.sourceNumberFormats = new SourceNumberFormats();
            TimeZone sourceTimeZone = TimeZone.getTimeZone(FREECASH_TIME_ZONE);
            this.sourceTimeFormat = new SimpleDateFormat(FREECASH_TIME_FORMAT);
            this.sourceTimeFormat.setTimeZone(sourceTimeZone);
        }

        public SourceNumberFormats getSourceNumberFormats() {
            return sourceNumberFormats;
        }

        public DateFormat getSourceTimeFormat() {
            return sourceTimeFormat;
        }
    }

    private static class Elecsnet2ConvParams extends ConvParams {

        private static class SourceNumberFormats {

            private final DecimalFormat longNumberFormat;

            private SourceNumberFormats() {
                this.longNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                this.longNumberFormat.applyPattern("##################0");
            }

            public NumberFormat getLongNumberFormat() {
                return longNumberFormat;
            }
        }

        final SourceNumberFormats sourceNumberFormats;
        final DateFormat sourceTimeFormat;

        private Elecsnet2ConvParams(ConvParams convParams) throws Exception {
            super(convParams);
            this.sourceNumberFormats = new SourceNumberFormats();
            TimeZone sourceTimeZone = TimeZone.getTimeZone(ELECSNET2_TIME_ZONE);
            this.sourceTimeFormat = new SimpleDateFormat(ELECSNET2_TIME_FORMAT);
            this.sourceTimeFormat.setTimeZone(sourceTimeZone);
        }

        public SourceNumberFormats getSourceNumberFormats() {
            return sourceNumberFormats;
        }

        public DateFormat getSourceTimeFormat() {
            return sourceTimeFormat;
        }
    }

    private static class MausspConvParams extends ConvParams {

        private static class SourceNumberFormats {

            private final DecimalFormat longNumberFormat;
            private final DecimalFormat realNumberFormat;

            private SourceNumberFormats() {
                this.longNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                this.longNumberFormat.applyPattern("##################0");
                this.realNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                this.realNumberFormat.applyPattern("##################0.00");
                DecimalFormatSymbols decimalFormatSymbols = this.realNumberFormat.getDecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator('.');
                this.realNumberFormat.setDecimalFormatSymbols(decimalFormatSymbols);
            }

            public DecimalFormat getLongNumberFormat() {
                return longNumberFormat;
            }

            public DecimalFormat getRealNumberFormat() {
                return realNumberFormat;
            }
        }

        final SourceNumberFormats sourceNumberFormats;
        final DateFormat sourceTimeFormat;

        private MausspConvParams(ConvParams convParams) throws Exception {
            super(convParams);
            this.sourceNumberFormats = new SourceNumberFormats();
            TimeZone sourceTimeZone = TimeZone.getTimeZone(MAUSSP_TIME_ZONE);
            this.sourceTimeFormat = new SimpleDateFormat(MAUSSP_TIME_FORMAT);
            this.sourceTimeFormat.setTimeZone(sourceTimeZone);
        }

        public SourceNumberFormats getSourceNumberFormats() {
            return sourceNumberFormats;
        }

        public DateFormat getSourceTimeFormat() {
            return sourceTimeFormat;
        }
    }

    private static class MausspStartConvParams extends ConvParams {

        private static class SourceNumberFormats {

            private final DecimalFormat longNumberFormat;

            private SourceNumberFormats() {
                this.longNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
                this.longNumberFormat.applyPattern("##################0");
            }

            public DecimalFormat getLongNumberFormat() {
                return longNumberFormat;
            }

        }

        final SourceNumberFormats sourceNumberFormats;
        final DateFormat sourceTimeFormat;

        private MausspStartConvParams(ConvParams convParams) throws Exception {
            super(convParams);
            this.sourceNumberFormats = new SourceNumberFormats();
            TimeZone sourceTimeZone = TimeZone.getTimeZone(MAUSSP_START_TIME_ZONE);
            this.sourceTimeFormat = new SimpleDateFormat(MAUSSP_START_TIME_FORMAT);
            this.sourceTimeFormat.setTimeZone(sourceTimeZone);
        }

        public SourceNumberFormats getSourceNumberFormats() {
            return sourceNumberFormats;
        }

        public DateFormat getSourceTimeFormat() {
            return sourceTimeFormat;
        }
    }

    public static void main(String[] args) {
        if (5 != args.length) {
            showUsage();
            return;
        }

        ConvParams convParams;
        try {
            convParams = new ConvParams(args[1], args[4], args[0], 1L, args[2], args[3]);
        } catch (Exception e) {
            System.err.println("Failed to parse parameter(s)");
            System.err.print(e.getMessage());
            showUsage();
            return;
        }

        try {
            InputStream sourceStream = null;
            OutputStream destStream = null;
            try {
                sourceStream = new FileInputStream(convParams.getSourceFilename());
                destStream = openDestFile(convParams.getDestFilename());
                convert(sourceStream, destStream, convParams);
            } finally {
                close(destStream);
                close(sourceStream);
            }
        } catch (Exception e) {
            System.err.println("Failed to convert");
            System.err.print(e.getMessage());
        }
    }

    private static void showUsage() {
        System.out.println(
                "Usage: freecash | elecsnet2 | maussp_spart | maussp | other-payment-system-name source-filename id-of-contragent id-of-packet dest-filename");
    }

    private static OutputStream openDestFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.createNewFile()) {
            throw new IOException(String.format("Can't create file \"%s\"", filename));
        }
        return new FileOutputStream(file);
    }

    private static void convert(InputStream sourceStream, OutputStream destStream, ConvParams convParams)
            throws Exception {
        writeRequest(createRequest(sourceStream, convParams), destStream);
    }

    private static Document createRequest(InputStream sourceStream, ConvParams convParams) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document requestDocument = documentBuilder.newDocument();

        Element dataElement = requestDocument.createElement("Data");
        Element bodyElement = requestDocument.createElement("Body");
        Element ecafeEnvelopeElement = requestDocument.createElement("PaymentExchange");
        ecafeEnvelopeElement.setAttribute("IdOfContragent", Long.toString(convParams.getIdOfContragent()));
        ecafeEnvelopeElement.setAttribute("IdOfPacket", Long.toString(convParams.getIdOfPacket()));
        ecafeEnvelopeElement.setAttribute("Version", "3");
        ecafeEnvelopeElement.setAttribute("SyncTime", convParams.getDestTimeFormat().format(convParams.getSyncTime()));

        Element paymentRegistryElement = requestDocument.createElement("PaymentRegistry");
        switch (convParams.getPaymentSystem()) {
            case PaymentSystem.FREE_CASH: {
                FreeCashConvParams freeCashConvParams = new FreeCashConvParams(convParams);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(sourceStream, FREECASH_SOURCE_CHARSET));
                try {
                    fillPaymentRegistryForFreecash(reader, requestDocument, paymentRegistryElement, freeCashConvParams);
                } finally {
                    close(reader);
                }
            }
            break;
            case PaymentSystem.ELECSNET2: {
                Elecsnet2ConvParams elecsnet2CashConvParams = new Elecsnet2ConvParams(convParams);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(sourceStream, ELECSNET2_SOURCE_CHARSET));
                try {
                    fillPaymentRegistryForElecsnet2(reader, requestDocument, paymentRegistryElement,
                            elecsnet2CashConvParams);
                } finally {
                    close(reader);
                }
            }
            break;
            case PaymentSystem.MAUSSP: {
                MausspConvParams mausspConvParams = new MausspConvParams(convParams);
                BufferedReader reader = new BufferedReader(new InputStreamReader(sourceStream, MAUSSP_SOURCE_CHARSET));
                try {
                    fillPaymentRegistryForMaussp(reader, requestDocument, paymentRegistryElement, mausspConvParams);
                } finally {
                    close(reader);
                }
            }
            break;
            case PaymentSystem.MAUSSP_START: {
                MausspStartConvParams mausspConvParams = new MausspStartConvParams(convParams);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(sourceStream, MAUSSP_START_SOURCE_CHARSET));
                try {
                    fillPaymentRegistryForMausspStart(reader, requestDocument, paymentRegistryElement,
                            mausspConvParams);
                } finally {
                    close(reader);
                }
            }
            break;
            default:
                throw new IllegalArgumentException("Unknown payment system");
        }

        ecafeEnvelopeElement.appendChild(paymentRegistryElement);
        bodyElement.appendChild(ecafeEnvelopeElement);
        dataElement.appendChild(bodyElement);
        requestDocument.appendChild(dataElement);

        return requestDocument;
    }

    private static void writeRequest(Document requestDocument, OutputStream destStream) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(requestDocument), new StreamResult(destStream));
    }

    private static void fillPaymentRegistryForFreecash(BufferedReader reader, Document requestDocument,
            Element paymentRegistryElement, FreeCashConvParams convParams) throws Exception {
        String currLine = reader.readLine();
        if (null != currLine) {
            currLine = reader.readLine();
        }
        while (null != currLine) {
            if (StringUtils.isNotEmpty(currLine)) {
                paymentRegistryElement
                        .appendChild(convertFreecashPaymentRegistryLine(currLine, requestDocument, convParams));
            }
            currLine = reader.readLine();
        }
    }

    private static void fillPaymentRegistryForElecsnet2(BufferedReader reader, Document requestDocument,
            Element paymentRegistryElement, Elecsnet2ConvParams convParams) throws Exception {
        String currLine = reader.readLine();
        while (null != currLine) {
            if (StringUtils.isNotEmpty(currLine)) {
                paymentRegistryElement
                        .appendChild(convertElecsnet2PaymentRegistryLine(currLine, requestDocument, convParams));
            }
            currLine = reader.readLine();
        }
    }

    private static void fillPaymentRegistryForMaussp(BufferedReader reader, Document requestDocument,
            Element paymentRegistryElement, MausspConvParams convParams) throws Exception {
        String currLine = reader.readLine();
        if (null != currLine) {
            currLine = reader.readLine();
        }
        while (null != currLine) {
            if (StringUtils.isNotEmpty(currLine)) {
                paymentRegistryElement
                        .appendChild(convertMausspPaymentRegistryLine(currLine, requestDocument, convParams));
            }
            currLine = reader.readLine();
        }
    }

    private static void fillPaymentRegistryForMausspStart(BufferedReader reader, Document requestDocument,
            Element paymentRegistryElement, MausspStartConvParams convParams) throws Exception {
        String currLine = reader.readLine();
        while (null != currLine) {
            if (StringUtils.isNotEmpty(currLine)) {
                paymentRegistryElement
                        .appendChild(convertMausspStartPaymentRegistryLine(currLine, requestDocument, convParams));
            }
            currLine = reader.readLine();
        }
    }

    private static Element convertFreecashPaymentRegistryLine(String line, Document requestDocument,
            FreeCashConvParams convParams) throws Exception {
        Element element = requestDocument.createElement("PT");
        String[] tokens = line.split(";");
        if (tokens.length < 4) {
            throw new IllegalArgumentException("Too few arguments at line");
        }

        FreeCashConvParams.SourceNumberFormats sourceNumberFormats = convParams.getSourceNumberFormats();
        NumberFormat longNumberFormat = sourceNumberFormats.getLongNumberFormat();
        long idOfPayment = longNumberFormat.parse(tokens[0]).longValue();
        Date payTime = convParams.getSourceTimeFormat().parse(tokens[1]);
        long contractId = longNumberFormat.parse(tokens[2]).longValue();
        NumberFormat doubleNumberFormat = sourceNumberFormats.getDoubleNumberFormat();
        double rubSum = doubleNumberFormat.parse(tokens[3]).doubleValue();
        long sum = (long) (rubSum * 100);

        ConvParams.DestNumberFormats destNumberFormats = convParams.getDestNumberFormats();
        NumberFormat destLongNumberFormat = destNumberFormats.getLongNumberFormat();
        element.setAttribute("IdOfPayment", destLongNumberFormat.format(idOfPayment));
        element.setAttribute("ContractId", destLongNumberFormat.format(contractId));
        element.setAttribute("PayTime", convParams.getDestTimeFormat().format(payTime));
        element.setAttribute("Sum", destLongNumberFormat.format(sum));
        element.setAttribute("PaymentMethod", Integer.toString(FREECASH_PAYMENT_METHOD));

        return element;
    }

    private static Element convertElecsnet2PaymentRegistryLine(String line, Document requestDocument,
            Elecsnet2ConvParams convParams) throws Exception {
        Element element = requestDocument.createElement("PT");
        String[] tokens = line.split(";");
        if (tokens.length < 4) {
            throw new IllegalArgumentException("Too few arguments at line");
        }

        Elecsnet2ConvParams.SourceNumberFormats sourceNumberFormats = convParams.getSourceNumberFormats();
        NumberFormat longNumberFormat = sourceNumberFormats.getLongNumberFormat();
        String idOfPayment = tokens[0];
        Date payTime = convParams.getSourceTimeFormat().parse(tokens[1]);
        long contractId = longNumberFormat.parse(tokens[2]).longValue();
        long sum = longNumberFormat.parse(tokens[3]).longValue();

        ConvParams.DestNumberFormats destNumberFormats = convParams.getDestNumberFormats();
        NumberFormat destLongNumberFormat = destNumberFormats.getLongNumberFormat();
        element.setAttribute("IdOfPayment", idOfPayment);
        element.setAttribute("ContractId", destLongNumberFormat.format(contractId));
        element.setAttribute("PayTime", convParams.getDestTimeFormat().format(payTime));
        element.setAttribute("Sum", destLongNumberFormat.format(sum));
        element.setAttribute("PaymentMethod", Integer.toString(ELECSNET2_PAYMENT_METHOD));

        return element;
    }

    private static Element convertMausspPaymentRegistryLine(String line, Document requestDocument,
            MausspConvParams convParams) throws Exception {
        Element element = requestDocument.createElement("PT");
        String[] tokens = line.split(";");
        if (tokens.length < 9) {
            throw new IllegalArgumentException("Too few arguments at line");
        }

        MausspConvParams.SourceNumberFormats sourceNumberFormats = convParams.getSourceNumberFormats();
        NumberFormat longNumberFormat = sourceNumberFormats.getLongNumberFormat();
        NumberFormat realNumberFormat = sourceNumberFormats.getRealNumberFormat();
        String idOfPayment = tokens[1];
        Date payTime = convParams.getSourceTimeFormat().parse(tokens[2]);
        long contractId = longNumberFormat.parse(tokens[5]).longValue();
        long sum = (long) realNumberFormat.parse(tokens[8]).doubleValue() * 100;
        String addPaymentMethod = tokens[7];
        String addIdOfPayment = tokens[3];

        ConvParams.DestNumberFormats destNumberFormats = convParams.getDestNumberFormats();
        NumberFormat destLongNumberFormat = destNumberFormats.getLongNumberFormat();
        element.setAttribute("IdOfPayment", idOfPayment);
        element.setAttribute("ContractId", destLongNumberFormat.format(contractId));
        element.setAttribute("PayTime", convParams.getDestTimeFormat().format(payTime));
        element.setAttribute("Sum", destLongNumberFormat.format(sum));
        element.setAttribute("PaymentMethod", Integer.toString(MAUSSP_PAYMENT_METHOD));
        element.setAttribute("AddPaymentMethod", addPaymentMethod);
        element.setAttribute("AddIdOfPayment", addIdOfPayment);

        return element;
    }

    private static Element convertMausspStartPaymentRegistryLine(String line, Document requestDocument,
            MausspStartConvParams convParams) throws Exception {
        Element element = requestDocument.createElement("PT");
        String[] tokens = line.split(";");
        if (tokens.length < 4) {
            throw new IllegalArgumentException("Too few arguments at line");
        }

        MausspStartConvParams.SourceNumberFormats sourceNumberFormats = convParams.getSourceNumberFormats();
        NumberFormat longNumberFormat = sourceNumberFormats.getLongNumberFormat();
        String idOfPayment = tokens[0];
        Date payTime = convParams.getSourceTimeFormat().parse(tokens[1]);
        long contractId = longNumberFormat.parse(tokens[2]).longValue();
        long sum = longNumberFormat.parse(tokens[3]).longValue();

        ConvParams.DestNumberFormats destNumberFormats = convParams.getDestNumberFormats();
        NumberFormat destLongNumberFormat = destNumberFormats.getLongNumberFormat();
        element.setAttribute("IdOfPayment", idOfPayment);
        element.setAttribute("ContractId", destLongNumberFormat.format(contractId));
        element.setAttribute("PayTime", convParams.getDestTimeFormat().format(payTime));
        element.setAttribute("Sum", destLongNumberFormat.format(sum));
        element.setAttribute("PaymentMethod", Integer.toString(MAUSSP_START_PAYMENT_METHOD));

        return element;
    }

    private static void close(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to close");
            System.err.print(e.getMessage());
        }
    }
}