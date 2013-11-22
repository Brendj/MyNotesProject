/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.SmppObject;
import org.smpp.debug.DefaultDebug;
import org.smpp.debug.FileDebug;

public class Client implements SMPPClient {

    private Session session;

    private int state;
    private String name;
    private final Listener listener;
    private final SessionListener sessionListener;
    private ru.axetta.smpp.client.Session.StartParams startParams;
    private ru.axetta.smpp.client.Session.Config config;

    /**
     * Таймаут при ошибке SLA. По умолчанию 60000. Для коммерческих целей рекомендуется 1000
     */
    public long SLA_timeout = 60000;//1000 - для коммерческого
    /**
     * Таймаут при повторной ошибке SLA. По умолчанию 120000. Для коммерческих целей рекомендуется 5000
     */
    public long SLA_timeout2 = 120000;
    /**
     * Количество попыток отправки при ошибке SLA
     */
    public byte SLA_limit = 5;

    /**
     * Таймаут при ошибке Throttling (т.е. мы превысили лимит отправляемых смс в минуту). По умолчанию 60000. Для коммерческих целей рекомендуется 1000
     */
    public long throttling_timeout = 60000;//1000 - для коммерческого
    /**
     * Таймаут при повторной ошибке Throttling (т.е. мы превысили лимит отправляемых смс в минуту). По умолчанию 120000. Для коммерческих целей рекомендуется 5000
     */
    public long throttling_timeout2 = 120000;//5000 - для коммерческого
    /**
     * Количество попыток отправки при ошибке Throttling (т.е. мы превысили лимит отправляемых смс в минуту)
     */
    public byte throttling_limit = 5;

    /**
     * Таймаут при ошибке system_error. По умолчанию 60000. Для коммерческих целей рекомендуется 1000
     */
    public long system_error_timeout = 60000;//10000 - для коммерческого
    /**
     * Таймаут при повторной ошибке system_error. По умолчанию 120000. Для коммерческих целей рекомендуется 5000
     */
    public long system_error_timeout2 = 60000;//10000 - для коммерческого
    /**
     * Количество попыток отправки при ошибке system_error
     */
    public byte system_error_limit = 5;

    /**
     * Таймаут при ошибке message queue full (смс-центр не успевает отправлять наши сообщения, из-за чего очередь на отправку переполнилась). По умолчанию 60000. Для коммерческих целей рекомендуется 1000
     */
    public long mqf_error_timeout = 60000;
    /**
     * Таймаут при повторной ошибке message queue full (смс-центр не успевает отправлять наши сообщения, из-за чего очередь на отправку переполнилась). По умолчанию 120000. Для коммерческих целей рекомендуется 5000
     */
    public long mqf_error_timeout2 = 120000;
    /**
     * Количество попыток отправки при ошибке message queue full (смс-центр не успевает отправлять наши сообщения, из-за чего очередь на отправку переполнилась)
     */
    public byte mqf_error_limit = 5;

    /**
     * Таймаут при неизвестной ошибке. По умолчанию 5000.
     */
    public long other_error_timeout = 5000;
    /**
     * Таймаут при повторной неизвестной ошибке. По умолчанию 20000.
     */
    public long other_error_timeout2 = 20000;
    /**
     * Количество попыток отправки при неизвестной ошибке.
     */
    public byte other_error_limit = 3;
    /**
     * Частота отправки enquire_link, местного аналога ping. По-умолчанию 60000
     */
    public long pingDelay = 60000L;
    /**
     * Какую юзать ussd-схему.
     */
    public USSD_MAPPINGS ussd_mapping = USSD_MAPPINGS.BERKUT_SCHEME_DEST;

    /**
     * Таймаут реконнекта. При неудаче суммируется для следующей попытки. По-умолчанию 30000
     */
    public long reconnectTimeout = 30000L;
    /**
     * Кол-во попыток реконнекта. По-умолчанию 5
     */
    public int reconnectLimit = 5;

    /**
     * Конструктор SMPP клиента. Все методы этого класса блокирующие.
     * @param listener листнер, содержащий колбэки. Не передавать сюда null, т.к. проверок на это нету.
     */

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(Listener listener, String name) {
        this.state = STATE_OFFLINE;
        this.listener = listener;
        this.name = name;
        this.sessionListener = new SessionListener(this.listener);
    }

    public String getName() {
        return name;
    }

    /**
     * Отправка зашифрованного OTA сообщения.
     * @param message строка с байтами вида "000102030405060708090A0B0C0D0E0F"
     * @param tar use <b>null</b> for default value
     * @param spi use <b>null</b> for default value
     * @param key1 ключ шифрования 1
     * @param tpscts use <b>null</b> for default value
     * @param key2 ключ шифрования 2
     * @param key use <b>null</b> for default value
     * @param msisdn номер получателя
     * @return результат отправки
     */
    public synchronized SendResult sendOTA(String message, long msisdn, String key1, String key2, byte[] tar, byte[] spi, byte[] key, byte[] tpscts) {
        logger.info(name + " sending OTA sms to " + msisdn + "...");
        if (message == null || message.length() < 2) {
            logger.info(name + " cannot send sms: message is empty");
            return new SendResult(ERRCODE_BAD_MESSAGE, getErrorDescription(ERRCODE_BAD_MESSAGE));
        }
        synchronized (sessionListener.syncObj) {
            while (state == STATE_CONNECT) {
                logger.info(name + " smpp client in connect state, waiting");
                try {
                    sessionListener.syncObj.wait();
                } catch (InterruptedException ignored) {}
            }
            if (state != STATE_ONLINE) {
                logger.info(name + " cannot send sms: wrong state");
                return new SendResult(ERRCODE_WRONG_STATE, getErrorDescription(ERRCODE_WRONG_STATE));
            }
        }
        //configuring
        String address = ""+msisdn;
        config.setDestinationAddress((byte)(address.length()>10?1:0), (byte)1, address);
        byte[] data = hexToByte(message);

        //sending
        synchronized (sessionListener.syncObjSend){
            sessionListener.messId = null;
            try {
                logger.info(name + " sending");
                session.asyncSendOTA(data, key1, key2, tar, spi, key, tpscts);
            } catch (SessionException ex) {
                int err = ex.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : ex.errCode;
                logger.error(name + " unexpected error" + ". Error description:" + getErrorDescription(err), ex);
                return new SendResult(err, getErrorDescription(err));
            }//такого просто не может быть
            try {
                sessionListener.syncObjSend.wait();
            } catch (InterruptedException ignored) {}
            if (sessionListener.exSend == null) {
                logger.info(name + " sms sent");
                return new SendResult(ERRCODE_NOT_SET, sessionListener.messId);
            } else {
                int err = sessionListener.exSend.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : sessionListener.exSend.errCode;
                logger.error(name + " sending error: " + sessionListener.exSend.getMessage() + ". Error description:"
                        + getErrorDescription(err), sessionListener.exSend);
                return new SendResult(err, getErrorDescription(err));
            }
        }
    }

    private static final char[] ALL_GSM7BIT_SYMBOLS = {'\u0040', '\u00a3', '\u0024', '\u00a5', '\u00e8', '\u00e9', '\u00f9', '\u00ec', '\u00f2', '\u00c7', '\n', '\u00d8', '\u00f8', '\r', '\u00c5', '\u00e5', '\u0394', '\u005f', '\u03a6', '\u0393', '\u039b', '\u03a9', '\u03a0', '\u03a8', '\u03a3', '\u0398', '\u039e', '\u00c6', '\u00e6', '\u00df', '\u00c9', '\u0020', '\u0021', '\u0022', '\u0023', '\u00a4', '\u0025', '\u0026', '\'', '\u0028', '\u0029', '\u002a', '\u002b', '\u002c', '\u002d', '\u002e', '\u002f', '\u0030', '\u0031', '\u0032', '\u0033', '\u0034', '\u0035', '\u0036', '\u0037', '\u0038', '\u0039', '\u003a', '\u003b', '\u003c', '\u003d', '\u003e', '\u003f', '\u00a1', '\u0041', '\u0042', '\u0043', '\u0044', '\u0045', '\u0046', '\u0047', '\u0048', '\u0049', '\u004a', '\u004b', '\u004c', '\u004d', '\u004e', '\u004f', '\u0050', '\u0051', '\u0052', '\u0053', '\u0054', '\u0055', '\u0056', '\u0057', '\u0058', '\u0059', '\u005a', '\u00c4', '\u00d6', '\u00d1', '\u00dc', '\u00a7', '\u00bf', '\u0061', '\u0062', '\u0063', '\u0064', '\u0065', '\u0066', '\u0067', '\u0068', '\u0069', '\u006a', '\u006b', '\u006c', '\u006d', '\u006e', '\u006f', '\u0070', '\u0071', '\u0072', '\u0073', '\u0074', '\u0075', '\u0076', '\u0077', '\u0078', '\u0079', '\u007a', '\u00e4', '\u00f6', '\u00f1', '\u00fc', '\u00e0', '\u005e', '\u007b', '\u007d', '\\', '\u005b', '\u007e', '\u005d', '\u007c', '\u20ac'};

    /**
     * Отправка текстового сообщения. GSM7bit или UCS2 определится автоматически, также автоматом смска отправится несколькими частями.
     * @param message текст сообщения
     * @param msisdn номер получателя
     * @return результат отправки
     */
    public synchronized SendResult send(String message, long msisdn) {
        return send(message, msisdn, (byte)0, 0, "");
    }
    /**
     * Отправка текстового сообщения. GSM7bit или UCS2 определится автоматически, также автоматом смска отправится несколькими частями.
     * @param message текст сообщения
     * @param msisdn номер получателя
     * @param ussd флаг ussd, 0 если не юзается юссд
     * @param ussd_sessid номер ussd сессии, 0 если не юзается юссд
     * @param ussdMenuPath путь в ussd менюшке. Вида "2*1*3*5*00*1*7"
     * @return результат отправки
     */
    public synchronized SendResult send(String message, long msisdn, final byte ussd, final int ussd_sessid, final String ussdMenuPath) {
        logger.info(name + " sending sms to " + msisdn + "...");
        if (message == null) {
            message = "";
        }
        synchronized (sessionListener.syncObj) {
            while (state == STATE_CONNECT) {
                logger.info(name + " smpp client in connect state, waiting");
                try {
                    sessionListener.syncObj.wait();
                } catch (InterruptedException ignored) {}
            }
            if (state != STATE_ONLINE) {
                logger.warn(name + " cannot send sms: wrong state");
                return new SendResult(ERRCODE_WRONG_STATE, getErrorDescription(ERRCODE_WRONG_STATE));
            }
            logger.info(name + " encoding detection");
        }
        //configuring
        String address = ""+msisdn;
        config.setDestinationAddress((byte)(address.length()>10?1:0), (byte)1, address);
        boolean ucs2 = false;
        for (char c : message.toCharArray()) {
            ucs2 = true;
            for (char c2 : ALL_GSM7BIT_SYMBOLS) {
                if (c == c2) {
                    ucs2 = false;
                    break;
                }
            }
            if (ucs2) {
                break;
            }
        }
        logger.info(ucs2 ? name+" encoding: UCS2" : name+" encoding: Xgsm7bit");
        //sending
        synchronized (sessionListener.syncObjSend){
            sessionListener.messId = null;
            try {
                logger.info(name + " sending");
                session.asyncSend(message, ucs2, ussd, ussd_sessid, ussdMenuPath);
            } catch (SessionException ex) {
                int err = ex.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : ex.errCode;
                logger.error(name + " unexpected error" + ". Error description:" + getErrorDescription(err), ex);
                return new SendResult(err, getErrorDescription(err));
            }//такого просто не может быть
            try {
                sessionListener.syncObjSend.wait();
            } catch (InterruptedException ignored) {}
            if (sessionListener.exSend == null) {
                logger.info(name + " sms sent");
                return new SendResult(ERRCODE_NOT_SET, sessionListener.messId);
            } else {
                int err = sessionListener.exSend.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : sessionListener.exSend.errCode;
                logger.error(name + " sending error: " + sessionListener.exSend.getMessage() + ". Error description:"
                        + getErrorDescription(err), sessionListener.exSend);
                return new SendResult(err, getErrorDescription(err));
            }
        }
    }

    /**
     * Получение текстового описания ошибки
     * @param errcode код ошибки
     * @return описание ошибки
     */
    public static String getErrorDescription(int errcode) {
        switch (errcode) {
            case ERRCODE_WRONG_STATE:
                return "Wrong client state. Most often it is happening because client not connected at the time.";
            case ERRCODE_BIND_FAILED:
                return "Exception while binding. It might be IOException, TimeOutException etc. Or it is just because you set incorrect binding parameters.";
            case ERRCODE_RESPONSE_FAILED:
                return "Cannot response for incoming PDU.";
            case ERRCODE_UNKNOWN_SOURCE:
                return "Got message with empty msisdn field.";
            case ERRCODE_SESSION_DESTROYED:
                return "Trying to work with destroyed session.";
            case ERRCODE_PING_NOT_SENT:
                return "Error sending enquire_link PDU (used instead of ping). Probably not connected with SMSC.";
            case ERRCODE_PING_TIMEOUT:
                return "Timeout reached waiting for enquire_link_resp. Probably not connected with SMSC.";
            case ERRCODE_TIMEOUT_SENDING_MESSAGE:
                return "Timeout reached waiting for submit_sm_resp.";
            case ERRCODE_BAD_MESSAGE:
                return "Sending empty OTA message";
            case org.smpp.Data.ESME_RINVMSGLEN:
                return "SMPPERR: Invalid Message Length (sm_length parameter).";
            case org.smpp.Data.ESME_RINVCMDLEN:
                return "SMPPERR: Invalid Command Length (command_length in SMPP PDU).";
            case org.smpp.Data.ESME_RINVCMDID:
                return "SMPPERR: Invalid Command ID (command_id in SMPP PDU).";
            case org.smpp.Data.ESME_RINVBNDSTS:
                return "SMPPERR: Incorrect BIND status for given command (example: trying to submit a message when bound only as a receiver).";
            case org.smpp.Data.ESME_RALYBND:
                return "SMPPERR: ESME already in bound state (example: sending a second bind command during an existing SMPP session).";
            case org.smpp.Data.ESME_RINVPRTFLG:
                return "SMPPERR: Invalid Priority Flag (priority_flag parameter).";
            case org.smpp.Data.ESME_RINVREGDLVFLG:
                return "SMPPERR: Invalid Regstered Delivery Flag (registered_delivery parameter).";
            case org.smpp.Data.ESME_RSYSERR:
                return "SMPPERR: System Error (indicates server problems on the SMPP host).";
            case org.smpp.Data.ESME_RINVSRCADR:
                return "SMPPERR: Invalid source address (sender/source address is not valid).";
            case org.smpp.Data.ESME_RINVDSTADR:
                return "SMPPERR: Invalid desintation address (recipient/destination phone number is not valid).";
            case org.smpp.Data.ESME_RINVMSGID:
                return "SMPPERR: Message ID is invalid (error only relevant to query_sm, replace_sm, cancel_sm commands).";
            case org.smpp.Data.ESME_RBINDFAIL:
                return "SMPPERR: Bind failed (login/bind failed – invalid login credentials or login restricted by IP address).";
            case org.smpp.Data.ESME_RINVPASWD:
                return "SMPPERR: Invalid password (login/bind failed).";
            case org.smpp.Data.ESME_RINVSYSID:
                return "SMPPERR: Invalid System ID (login/bind failed – invalid username / system id).";
            case org.smpp.Data.ESME_RCANCELFAIL:
                return "SMPPERR: cancel_sm request failed.";
            case org.smpp.Data.ESME_RREPLACEFAIL:
                return "SMPPERR: replace_sm request failed.";
            case org.smpp.Data.ESME_RMSGQFUL:
                return "SMPPERR: Message Queue Full (This can indicate that the SMPP server has too many queued messages and temporarily cannot accept any more messages. It can also indicate that the SMPP server has too many messages pending for the specified recipient and will not accept any more messages for this recipient until it is able to deliver messages that are already in the queue to this recipient.).";
            case org.smpp.Data.ESME_RINVSERTYP:
                return "SMPPERR: Invalid service_type value.";
            case org.smpp.Data.ESME_RADDCUSTFAIL:
                return "SMPPERR: Failed to Add Customer.";
            case org.smpp.Data.ESME_RDELCUSTFAIL:
                return "SMPPERR: Failed to delete Customer.";
            case org.smpp.Data.ESME_RMODCUSTFAIL:
                return "SMPPERR: Failed to modify customer.";
            case org.smpp.Data.ESME_RENQCUSTFAIL:
                return "SMPPERR: Failed to Enquire Customer.";
            case org.smpp.Data.ESME_RINVCUSTID:
                return "SMPPERR: Invalid Customer ID.";
            case org.smpp.Data.ESME_RINVCUSTNAME:
                return "SMPPERR: Invalid Customer Name.";
            case org.smpp.Data.ESME_RINVCUSTADR:
                return "SMPPERR: Invalid Customer Address.";
            case org.smpp.Data.ESME_RINVADR:
                return "SMPPERR: Invalid Address.";
            case org.smpp.Data.ESME_RCUSTEXIST:
                return "SMPPERR: Customer Exists.";
            case org.smpp.Data.ESME_RCUSTNOTEXIST:
                return "SMPPERR: Customer does not exist.";
            case org.smpp.Data.ESME_RADDDLFAIL:
                return "SMPPERR: Failed to Add DL.";
            case org.smpp.Data.ESME_RMODDLFAIL:
                return "SMPPERR: Failed to modify DL.";
            case org.smpp.Data.ESME_RDELDLFAIL:
                return "SMPPERR: Failed to Delete DL.";
            case org.smpp.Data.ESME_RVIEWDLFAIL:
                return "SMPPERR: Failed to View DL.";
            case org.smpp.Data.ESME_RLISTDLSFAIL:
                return "SMPPERR: Failed to list DLs.";
            case org.smpp.Data.ESME_RPARAMRETFAIL:
                return "SMPPERR: Param Retrieve Failed.";
            case org.smpp.Data.ESME_RINVPARAM:
                return "SMPPERR: Invalid Param.";
            case org.smpp.Data.ESME_RINVNUMDESTS:
                return "SMPPERR: Invalid number_of_dests value in submit_multi request.";
            case org.smpp.Data.ESME_RINVDLNAME:
                return "SMPPERR: Invalid distribution list name in submit_multi request.";
            case org.smpp.Data.ESME_RINVDLMEMBDESC:
                return "SMPPERR: Invalid DL Member Description.";
            case org.smpp.Data.ESME_RINVDLMEMBTYP:
                return "SMPPERR: Invalid DL Member Type.";
            case org.smpp.Data.ESME_RINVDLMODOPT:
                return "SMPPERR: Invalid DL Modify Option.";
            case org.smpp.Data.ESME_RINVDESTFLAG:
                return "SMPPERR: Invalid dest_flag in submit_multi request.";
            case org.smpp.Data.ESME_RINVSUBREP:
                return "SMPPERR: Invalid ‘submit with replace’ request (replace_if_present flag set).";
            case org.smpp.Data.ESME_RINVESMCLASS:
                return "SMPPERR: Invalid esm_class field data.";
            case org.smpp.Data.ESME_RCNTSUBDL:
                return "SMPPERR: Cannot submit to distribution list (submit_multi request).";
            case org.smpp.Data.ESME_RSUBMITFAIL:
                return "SMPPERR: Submit message failed.";
            case org.smpp.Data.ESME_RINVSRCTON:
                return "SMPPERR: Invalid Source address TON.";
            case org.smpp.Data.ESME_RINVSRCNPI:
                return "SMPPERR: Invalid Source address NPI.";
            case org.smpp.Data.ESME_RINVDSTTON:
                return "SMPPERR: Invalid Destination address TON.";
            case org.smpp.Data.ESME_RINVDSTNPI:
                return "SMPPERR: Invalid Destination address NPI.";
            case org.smpp.Data.ESME_RINVSYSTYP:
                return "SMPPERR: Invalid system_type field.";
            case org.smpp.Data.ESME_RINVREPFLAG:
                return "SMPPERR: Invalid replace_if_present flag.";
            case org.smpp.Data.ESME_RINVNUMMSGS:
                return "SMPPERR: Invalid number_of_messages parameter.";
            case org.smpp.Data.ESME_RTHROTTLED:
                return "SMPPERR: Throttling error (This indicates that you are submitting messages at a rate that is faster than the provider allows).";
            case org.smpp.Data.ESME_RPROVNOTALLWD:
                return "SMPPERR: Provisioning Not Allowed.";
            case org.smpp.Data.ESME_RINVSCHED:
                return "SMPPERR: Invalid schedule_delivery_time parameter.";
            case org.smpp.Data.ESME_RINVEXPIRY:
                return "SMPPERR: Invalid validity_period parameter / Expiry time.";
            case org.smpp.Data.ESME_RINVDFTMSGID:
                return "SMPPERR: Invalid sm_default_msg_id parameter (this error can sometimes occur if the “Default Sender Address” field is blank in NowSMS).";
            case org.smpp.Data.ESME_RX_T_APPN:
                return "SMPPERR: ESME Receiver Temporary App Error Code.";
            case org.smpp.Data.ESME_RX_P_APPN:
                return "SMPPERR: ESME Receiver Permanent App Error Code (the SMPP provider is rejecting the message due to a policy decision or message filter).";
            case org.smpp.Data.ESME_RX_R_APPN:
                return "SMPPERR: ESME Receiver Reject Message Error Code (the SMPP provider is rejecting the message due to a policy decision or message filter).";
            case org.smpp.Data.ESME_RQUERYFAIL:
                return "SMPPERR: query_sm request failed.";
            case org.smpp.Data.ESME_RINVPGCUSTID:
                return "SMPPERR: Paging Customer ID Invalid No such subscriber";
            case org.smpp.Data.ESME_RINVPGCUSTIDLEN:
                return "SMPPERR: Paging Customer ID length Invalid";
            case org.smpp.Data.ESME_RINVCITYLEN:
                return "SMPPERR: City Length Invalid";
            case org.smpp.Data.ESME_RINVSTATELEN:
                return "SMPPERR: State Length Invalid";
            case org.smpp.Data.ESME_RINVZIPPREFIXLEN:
                return "SMPPERR: Zip Prefix Length Invalid";
            case org.smpp.Data.ESME_RINVZIPPOSTFIXLEN:
                return "SMPPERR: Zip Postfix Length Invalid";
            case org.smpp.Data.ESME_RINVMINLEN:
                return "SMPPERR: MIN Length Invalid";
            case org.smpp.Data.ESME_RINVMIN:
                return "SMPPERR: MIN Invalid (i.e. No such MIN)";
            case org.smpp.Data.ESME_RINVPINLEN:
                return "SMPPERR: PIN Length Invalid";
            case org.smpp.Data.ESME_RINVTERMCODELEN:
                return "SMPPERR: Terminal Code Length Invalid";
            case org.smpp.Data.ESME_RINVCHANNELLEN:
                return "SMPPERR: Channel Length Invalid";
            case org.smpp.Data.ESME_RINVCOVREGIONLEN:
                return "SMPPERR: Coverage Region Length Invalid";
            case org.smpp.Data.ESME_RINVCAPCODELEN:
                return "SMPPERR: Cap Code Length Invalid";
            case org.smpp.Data.ESME_RINVMDTLEN:
                return "SMPPERR: Message delivery time Length Invalid";
            case org.smpp.Data.ESME_RINVPRIORMSGLEN:
                return "SMPPERR: Priority Message Length Invalid";
            case org.smpp.Data.ESME_RINVPERMSGLEN:
                return "SMPPERR: Periodic Messages Length Invalid";
            case org.smpp.Data.ESME_RINVPGALERTLEN:
                return "SMPPERR: Paging Alerts Length Invalid";
            case org.smpp.Data.ESME_RINVSMUSERLEN:
                return "SMPPERR: Short Message User Group Length Invalid";
            case org.smpp.Data.ESME_RINVRTDBLEN:
                return "SMPPERR: Real Time Data broadcasts Length Invalid";
            case org.smpp.Data.ESME_RINVREGDELLEN:
                return "SMPPERR: Registered Delivery Lenght Invalid";
            case org.smpp.Data.ESME_RINVMSGDISTLEN:
                return "SMPPERR: Message Distribution Lenght Invalid";
            case org.smpp.Data.ESME_RINVPRIORMSG:
                return "SMPPERR: Priority Message Length Invalid";
            case org.smpp.Data.ESME_RINVMDT:
                return "SMPPERR: Message delivery time Invalid";
            case org.smpp.Data.ESME_RINVPERMSG:
                return "SMPPERR: Periodic Messages Invalid";
            case org.smpp.Data.ESME_RINVMSGDIST:
                return "SMPPERR: Message Distribution Invalid";
            case org.smpp.Data.ESME_RINVPGALERT:
                return "SMPPERR: Paging Alerts Invalid";
            case org.smpp.Data.ESME_RINVSMUSER:
                return "SMPPERR: Short Message User Group Invalid";
            case org.smpp.Data.ESME_RINVRTDB:
                return "SMPPERR: Real Time Data broadcasts Invalid";
            case org.smpp.Data.ESME_RINVREGDEL:
                return "SMPPERR: Registered Delivery Invalid";
            case org.smpp.Data.ESME_RINVOPTPARSTREAM:
                return "SMPPERR: KIF IW Field out of data";
            case org.smpp.Data.ESME_ROPTPARNOTALLWD:
                return "SMPPERR: Optional Parameter not allowed";
            case org.smpp.Data.ESME_RINVOPTPARLEN:
                return "SMPPERR: Invalid Optional Parameter Length";
            case org.smpp.Data.ESME_RMISSINGOPTPARAM:
                return "SMPPERR: An expected optional TLV parameter is missing.";
            case org.smpp.Data.ESME_RINVOPTPARAMVAL:
                return "SMPPERR: An optional TLV parameter is encoded with an invalid value.";
            case org.smpp.Data.ESME_RDELIVERYFAILURE:
                return "SMPPERR: Generice Message Delivery failure.";
            default:
                return "An unknown error occurred";
        }
    }

    /**
     * Получение текущего статуса клиента.
     * @return текущий статус
     */
    public int getStatus() {
        return state;
    }

    /**
     * Старт клиента. В этом методе происходит коннект к СМС-центру. Параметры частоты пинга ({@link #pingDelay}) и таймаутов ошибок должны быть выставлены до вызова этого метода.
     * @param sourceAddress наш адрес
     * @param smscIPAddress ip адрес смс-центра
     * @param smscPort порт смс-центра
     * @param systemId наш логин
     * @param systemType опциональный параметр для авторизации на смс-центре. Если оно нужно, сисадмин смс-центра должен предоставить его. Обычно это короткая текстовая строка.
     * @param serviceType может быть пустым или содержать следующие значения: CMT, CPT, VMN, VMA, WAP или USSD
     * @param password пароль
     * @return 0 или код ошибки
     */
    public synchronized int start(String sourceAddress, String smscIPAddress, int smscPort, String systemId, String systemType, String serviceType, String password) {
        logger.info(name + " starting smpp client...");
        if (state != STATE_OFFLINE) {
            logger.info(name + " cannot start smpp client: wrong state");
            return ERRCODE_WRONG_STATE;
        }
        sessionListener.client = this;
        state = STATE_CONNECT;
        config = new Session.Config(sessionListener, (byte)0, (byte)1, sourceAddress);
        session = new Session(config);
        startParams = new Session.StartParams(smscIPAddress, smscPort, systemId, systemType, serviceType, password, pingDelay, (byte)0, (byte)0, "", ussd_mapping);

        startParams.SLA_timeout = SLA_timeout;
        startParams.SLA_timeout2 = SLA_timeout2;
        startParams.SLA_limit = SLA_limit;
        startParams.throttling_timeout = throttling_timeout;
        startParams.throttling_timeout2 = throttling_timeout2;
        startParams.throttling_limit = throttling_limit;
        startParams.system_error_timeout = system_error_timeout;
        startParams.system_error_timeout2 = system_error_timeout2;
        startParams.system_error_limit = system_error_limit;
        startParams.other_error_timeout = other_error_timeout;
        startParams.other_error_timeout2 = other_error_timeout2;
        startParams.other_error_limit = other_error_limit;
        startParams.mqf_error_timeout = mqf_error_timeout;
        startParams.mqf_error_timeout2 = mqf_error_timeout2;
        startParams.mqf_error_limit = mqf_error_limit;

        synchronized (sessionListener.syncObjStart){
            try {
                logger.info(name + " starting session");
                session.asyncStart(startParams);
            } catch (SessionException ex) {
                logger.error(name + " unexpected error", ex);
                return ex.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : ex.errCode;
            }//такого просто не может быть
            try {
                sessionListener.syncObjStart.wait();
            } catch (InterruptedException ignored) {}

            if (sessionListener.exStart == null) {
                logger.info(name + " smpp client started");
                state = STATE_ONLINE;
                return ERRCODE_NOT_SET;
            } else {
                logger.error(name + " error starting smpp client" + ". Error description:" + getErrorDescription(
                        sessionListener.exStart.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN
                                : sessionListener.exStart.errCode), sessionListener.exStart);
                sessionListener.client = null;
                session.destroy();
                session = null;
                state = STATE_OFFLINE;
                return (sessionListener.exStart.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : sessionListener.exStart.errCode);
            }
        }
    }

    /**
     * Остановка клиента и разрыв соединения с смс-центром.
     * @return 0 или код ошибки
     */
    public synchronized int stop() {
        logger.info(name + " stopping smpp client...");
        synchronized (sessionListener.syncObj) {
            while (state == STATE_CONNECT) {
                logger.info(name + " smpp client in connect state, waiting");
                try {
                    sessionListener.syncObj.wait();
                } catch (InterruptedException ignored) {}
            }
            if (state != STATE_ONLINE) {
                logger.warn(name + " cannot stop smpp client: wrong state");
                return ERRCODE_WRONG_STATE;
            }
        }
        synchronized (sessionListener.syncObjStop){
            try {
                logger.info(name + " stopping session");
                session.asyncStop();
            } catch (SessionException ex) {
                int err = ex.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : ex.errCode;
                logger.error(name + " unexpected error" + ". Error description:" + getErrorDescription(err), ex);
                return err;
            }//такого просто не может быть
            try {
                sessionListener.syncObjStop.wait();
            } catch (InterruptedException ignored) {}
            sessionListener.client = null;
            session.destroy();
            session = null;
            state = STATE_OFFLINE;
            if (sessionListener.exStop == null) {
                logger.info(name + " smpp client stopped");
                return ERRCODE_NOT_SET;
            } else {
                int err = sessionListener.exStop.errCode == ERRCODE_NOT_SET ? ERRCODE_UNKNOWN : sessionListener.exStop.errCode;
                logger.error(name + " smpp client stopped with error. Error description:" + getErrorDescription(err),
                        sessionListener.exStop);
                return err;
            }
        }
    }

    private static FileDebug fileDebug;
    private static String debugPath = "", debugFile = "";

    /**
     * Включает логгирование в библиотеках OPENSMPP. Будет много довольно низкоуровенвого вывода.
     * @param path путь к файлам лога
     * @param fileName название для файлов
     */
    public static void setLowLevelDebugToFile(String path, String fileName) {
        if (!path.equals(debugPath) || !fileName.equals(debugFile) || fileDebug == null) {
            fileDebug = new FileDebug(path, fileName);
            debugPath = path;
            debugFile = fileName;
        }
        SmppObject.setDebug(fileDebug);
    }

    /**
     * Отключает логгирование в библиотеках OPENSMPP.
     */
    public static void disableLowLevelDebug() {
        SmppObject.setDebug(new DefaultDebug());
        fileDebug = null;
    }

    private static byte[] hexToByte(String s) {
        final String HEXINDEX = "0123456789abcdef";
        s = s.toLowerCase();
        int l = s.length() / 2;
        byte data[] = new byte[l];
        int j = 0;

        for (int i = 0; i < l; i++) {
            char c = s.charAt(j++);
            int n, b;

            n = HEXINDEX.indexOf(c);
            b = n << 4;
            c = s.charAt(j++);
            n = HEXINDEX.indexOf(c);
            b += n;
            data[i] = (byte) b;
        }
        return data;
    }

    /**
     * Преобразует массив байт в строку вида "000102030405060708090A0B0C0D0E0F"
     * @param b массив байт
     * @return результирующая строка
     */
    public static String toStr(byte[] b) {
        String dump = "";
        try {
            int dataLen = b.length;
            for (int i = 0; i < dataLen; i++) {
                dump += Character.forDigit((b[i] >> 4) & 0x0f, 16);
                dump += Character.forDigit(b[i] & 0x0f, 16);
            }
        } catch (Throwable t) {
            dump = "Throwable caught when dumping = " + t;
        }
        return dump;
    }

    private class SessionListener implements Session.Listener, Runnable {

        public SessionException exSend, exStart, exStop;
        public String messId;
        public final Object syncObj;//общий
        public final Object syncObjSend;
        public final Object syncObjStart;
        public final Object syncObjStop;
        private final Listener listener;
        public Client client;

        private SessionListener(Listener listener) {
            this.syncObj = new Object();
            this.syncObjSend = new Object();
            this.syncObjStart = new Object();
            this.syncObjStop = new Object();
            this.listener = listener;
        }

        public void startComplete(SessionException ex) {
            synchronized (syncObjStart) {
                this.exStart = ex;
                syncObjStart.notifyAll();
            }
        }

        public void sendComplete(SessionException ex, long messageId, String serverAssignedId) {
            synchronized (syncObjSend) {
                this.exSend = ex;
                this.messId = serverAssignedId;
                syncObjSend.notifyAll();
            }
        }

        public void stopComplete(SessionException ex) {
            synchronized (syncObjStop) {
                this.exStop = ex;
                syncObjStop.notifyAll();
            }
    }

        public void received(SessionException ex, MSG message, long receiveTime, long processTime) {
            if (ex != null) {
                logger.warn(client.name + " receiving error: " + ex.getMessage(), ex);
            } else {
                logger.info(client.name + " message received");
                listener.received(message, receiveTime, processTime);
            }
        }

        public void pingError(SessionException ex) {
            logger.warn(client.name + " ping error", ex);
            new Thread(this).start();
        }

        public void run() {
            synchronized (syncObj) {
                logger.info(client.name + " ping error: reconnecting");
                if (client == null) {//пока мы ждали своей очереди, уже прошел метод stop клиента
                    logger.info(client.name + " ping error: already stopped, shutting down");
                    return;
                }
                client.state = Client.STATE_CONNECT;

                //стоппим
                synchronized (syncObjStop){
                    try {
                        logger.info(client.name + " ping error: stopping session");
                        session.asyncStop();
                    } catch (SessionException ex) {
                        logger.error(client.name + " ping error: unexpected error", ex);
                        client.session.destroy();
                        client.session = null;
                        client.state = STATE_OFFLINE;
                        syncObj.notifyAll();
                        client = null;
                        listener.error();
                        return;
                    }
                    try {
                        syncObjStop.wait();
                    } catch (InterruptedException ignored) {}
                }
                logger.info(client.name + " ping error: stopped");
                long time = 0;
                for (int amount = 0; amount < client.reconnectLimit && client.state == Client.STATE_CONNECT; time+=client.reconnectTimeout, amount++) {
                    logger.info(client.name + " ping error: waiting for " + time + "ms");
                    try {
                        session.reset();
                    } catch (SessionException ignored) {}
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException ignored) {}
                    synchronized (syncObjStart){
                    try {
                        logger.info(client.name + " ping error: starting session");
                        session.asyncStart(client.startParams);
                    } catch (SessionException ex) {
                        logger.error(client.name + " ping error: unexpected error", ex);
                        client.session.destroy();
                        client.session = null;
                        client.state = STATE_OFFLINE;
                        syncObj.notifyAll();
                        client = null;
                        listener.error();
                        return;
                    }
                    try {
                        syncObjStart.wait();
                    } catch (InterruptedException ignored) {}
                    }
                    if (exStart == null) {
                        logger.info(client.name + " ping error: started");
                        client.state = STATE_ONLINE;
                    } else {
                        logger.warn(client.name + " ping error: not started", exStart);
                    }
                }
                if (client.state == STATE_ONLINE) {
                    logger.info(client.name + " ping error: successfully reconnected");
                    syncObj.notifyAll();
                } else {
                    logger.warn(client.name + " ping error: cannot reconnect");
                    client.session.destroy();
                    client.session = null;
                    client.state = STATE_OFFLINE;
                    syncObj.notifyAll();
                    client = null;
                    listener.error();
                }
            }
        }
    }
}
