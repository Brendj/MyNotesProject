/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Класс сессии.
 */
class Session {

    protected final static byte MSG_TYPE_TEXT   = 0;
    protected final static byte MSG_TYPE_TICKET = 1;
    protected final static byte MSG_TYPE_POR    = 2;
    private final static Logger logger = LoggerFactory.getLogger(Session.class);

    public static class Config {
        private final Listener listener;
        private final Address sourceAddress;
        private Address destinationAddress;
        public static boolean USE63MAX = true;

        public Config(Listener listener, byte ton, byte npi, String address) throws IllegalArgumentException {
            try {
                this.sourceAddress = new Address(ton, npi, address);
            } catch (WrongLengthOfStringException e) {
                throw new IllegalArgumentException("wrong address parameters");
            }
            this.listener = listener;
            this.destinationAddress = new Address();
        }

        public void setDestinationAddress(byte ton, byte npi, String address) throws IllegalArgumentException {
            destinationAddress.setTon(ton);
            destinationAddress.setNpi(npi);
            try {
                destinationAddress.setAddress(address);
            } catch (WrongLengthOfStringException e) {
                throw new IllegalArgumentException("wrong address parameters");
            }
        }

        public String getAddress() {
            return sourceAddress.getAddress();
        }
    }

    public static class StartParams {
        public final String smscIPAddress;
        public final int smscPort;
        public final String systemId;
        public final String systemType;
        public final String serviceType;
        public final String password;
        protected final AddressRange addressRange;
        public final long enquireLinkDelay;
        private final byte replaceIfPresent = Data.DFTL_REPLACE_IFP;
        public final USSD_MAPPINGS ussd_mapping;

        public long SLA_timeout = 60000;//1000 - для коммерческого
        public long SLA_timeout2 = 120000;
        public byte SLA_limit = 5;

        public long throttling_timeout = 60000;//1000 - для коммерческого
        public long throttling_timeout2 = 120000;//5000 - для коммерческого
        public byte throttling_limit = 5;

        public long system_error_timeout = 60000;//10000 - для коммерческого
        public long system_error_timeout2 = 60000;//10000 - для коммерческого
        public byte system_error_limit = 5;

        public long mqf_error_timeout = 60000;
        public long mqf_error_timeout2 = 120000;
        public byte mqf_error_limit = 5;

        public long other_error_timeout = 5000;
        public long other_error_timeout2 = 20000;
        public byte other_error_limit = 3;

        public StartParams() {
            this(null, 0, null, "", "", "", 60000L, Data.DFLT_GSM_TON, Data.DFLT_GSM_NPI, Data.DFLT_ADDR_RANGE, USSD_MAPPINGS.BERKUT_SCHEME_DEST);
        }

        public StartParams(String smscIPAddress, int smscPort, String systemId, String systemType,
                           String serviceType, String password, long enquireLinkDelay, byte addr_ton, byte addr_npi,
                           String addr_range, USSD_MAPPINGS ussd_mapping)
                throws IllegalArgumentException {
            this.smscIPAddress = smscIPAddress;
            this.smscPort = smscPort;
            this.systemId = systemId;
            this.systemType = systemType;
            this.serviceType = serviceType;
            this.password = password;
            this.enquireLinkDelay = enquireLinkDelay;
            this.ussd_mapping = ussd_mapping;
            try {
                this.addressRange = new AddressRange(addr_ton, addr_npi, addr_range);
            } catch (WrongLengthOfStringException e) {
                throw new IllegalArgumentException("wrong adress param length");
            }
        }
    }


    private static class Impl {

        private static enum State {
            INITIAL, START_IN_PROGRESS, STARTED, STOP_IN_PROGRESS, STOPPED, DESTROYED
        }

        private final Config config;
        private State state;
        private boolean sendInProgress;
        private Future enqFuture;
        private ArrayList<Message> messageQueue;
        private final ExecutorService listenerExecutorService;

        private ArrayList<ArrayList<ReceivedLong>> receivedBigMessages;
        private ArrayList<Future> receivedBigMessagesTimeout;

        private Message sendingMessage;
        private int sendingMessageId;
        private Future sendingFuture;
        private org.smpp.Session session;
        private ServerPDUEventListener pduListener;
        private StartParams startParams;
        private final ScheduledExecutorService executorService;

        public Impl(Config config, ScheduledExecutorService executorService, ExecutorService listenerExecutorService) {
            this.executorService = executorService;
            this.config = config;
            this.state = State.INITIAL;
            this.sendInProgress = false;
            this.enqFuture = null;
            this.messageQueue = new ArrayList<Message>();
            this.listenerExecutorService = listenerExecutorService;

            this.receivedBigMessages = new ArrayList<ArrayList<ReceivedLong>>();
            this.receivedBigMessagesTimeout = new ArrayList<Future>();
        }

        public void start(StartParams startParams) {
            if (state != State.INITIAL) {
                executeStartComplete(new SessionException("wrong state.", Error.ERRCODE_WRONG_STATE));
                return;
            }
            state = State.START_IN_PROGRESS;
            this.startParams = startParams;
            try {
                BindRequest request = new BindTransciever();
                BindResponse response;

                TCPIPConnection connection = new TCPIPConnection(startParams.smscIPAddress, startParams.smscPort);
                connection.setReceiveTimeout(20 * 1000);
                session = new org.smpp.Session(connection);

                // set values
                request.setSystemId(startParams.systemId);
                request.setPassword(startParams.password);
                request.setSystemType(startParams.systemType);
                request.setInterfaceVersion((byte) 0x34);
                request.setAddressRange(startParams.addressRange);

                // send the request
                pduListener = new ServerPDUEventListener() {
                    public void handleEvent(final ServerPDUEvent event) {
                        final long receiveTime = System.currentTimeMillis();
                        executorService.execute(new Runnable() {
                            public void run() {
                                onReceiveMessage(event, receiveTime);
                            }
                        });
                    }
                };
                response = session.bind(request, pduListener);

                if (response.getCommandStatus() == Data.ESME_ROK) {
                    state = State.STARTED;
                    createPingTask();
                    executeStartComplete(null);
                } else {
                    state = State.STOPPED;
                    executeStartComplete(new SessionException("bind failed, command status: " + response.getCommandStatus(), response.getCommandStatus()));
                }
            } catch (final Exception e) {
                if (session != null) {
                    try {
                        session.close();
                    } catch (Exception ignored) {
                    }
                }
                state = State.STOPPED;
                executeStartComplete(new SessionException("bind failed: " + e, Error.ERRCODE_BIND_FAILED));
            }
        }

        private void createPingTask() {
            enqFuture = this.executorService.schedule(new Runnable() {
                public void run() {
                    enqLink();
                }
            }, startParams.enquireLinkDelay, TimeUnit.MILLISECONDS);
        }

        private Future createLongMessageTimeoutTask(final ArrayList<ReceivedLong> list) {
            return this.executorService.schedule(new Runnable() {
                public void run() {
                    longMessageTimeout(list);
                }
            }, 59000, TimeUnit.MILLISECONDS);//59 секунд таймаут для приема длинных смс
        }

        private void longMessageTimeout(ArrayList<ReceivedLong> list) {
            receivedBigMessages.remove(list);
            //если надо как-то вывести сообщение, что сообщения из списка list не собрались.
        }

        public void onReceiveMessage(ServerPDUEvent event, long receiveTime) {
            enqFuture.cancel(false);
            enqFuture = null;
            if (event.getPDU().isRequest()) {
                onReceiveRequest(event.getPDU(), receiveTime);
            } else if (event.getPDU().isResponse()) {
                onReceiveResponse(event.getPDU());
            }
            if (enqFuture == null && state == State.STARTED) {
                createPingTask();
            }
        }

        private void onReceiveRequest(PDU pdu, long receiveTime) {
            if (state != State.STARTED) {
                return;
            }
            switch (pdu.getCommandId()) {
                case Data.ENQUIRE_LINK:
                    //System.err.println("enquire_link received. Responding...");
                    try {
                        session.respond(((EnquireLink)pdu).getResponse());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Data.DELIVER_SM:
                    //System.err.println("deliver_sm received");
                    processReceivedDeliverSM(pdu, receiveTime);
                    break;
                case Data.DATA_SM:
                    //System.err.println("data_sm received");
                    processReceivedDataSM(pdu, receiveTime);
                    break;
            }
        }

        private static int ussdSessionToInt(String s) {
            s = s.substring(s.indexOf('#') + 1);
            char c = s.toUpperCase().charAt(0);
            if (c >= 'A' && c <= 'F')
                return Integer.parseInt(s, 16);
            return Integer.parseInt(s, 10);
        }

        private static String ussdMenuPath(String s) {
            if (s == null || s.isEmpty())
                return "";
            int i = s.indexOf('#');
            if (i != -1)
                s = s.substring(0, i);
            i = s.indexOf('*');
            if (i == -1 || i == s.length() - 1)
                return "";
            return s.substring(i + 1);
        }

        private void processReceivedDeliverSM(PDU pdu, long receiveTime) {
            DeliverSM d = (DeliverSM) pdu;
            byte data_coding = d.getDataCoding();
            int ussd_sessid;
            switch (startParams.ussd_mapping) {
                case BERKUT_SCHEME_DEST:
                    ussd_sessid = ussdSessionToInt(d.getDestAddr().getAddress());
                    break;
                case BERKUT_SCHEME_SOURCE:
                    ussd_sessid = ussdSessionToInt(d.getSourceAddr().getAddress());
                    break;
                case NOWSMS_SCHEME_ITS:
                    try {
                        ussd_sessid = d.getItsSessionInfo();
                    } catch (ValueNotSetException ignored) {
                        ussd_sessid = 0;
                    }
                    break;
                default:
                    ussd_sessid = 0;
                    break;
            }
            String ussdMenuPath = ussdMenuPath(d.getDestAddr().getAddress());
            String message_id = "";
            try {
                message_id = d.getReceiptedMessageId();
            } catch (ValueNotSetException ignored) {}
            if (d.getSourceAddr() == null || d.getSourceAddr().getAddress() == null || d.getSourceAddr().getAddress().length() == 0) {
                try {
                   session.respond(d.getResponse());
                } catch (Exception ignored) {}//сообщение неизвестно от кого, шлем лесом
                executeReceived(new SessionException("unknown source", Error.ERRCODE_UNKNOWN_SOURCE), new Empty(message_id, "unknown", ussd_sessid, ussdMenuPath), receiveTime);
                return;
            }
            String msisdn = d.getSourceAddr().getAddress();
            //{ сюда можно выоткнуть проверку на черный список
            //    String address = d.getSourceAddr().getAddress();
            //    if (address d черном списке) {
            //        try {
            //           session.respond(d.getResponse());
            //        } catch (Exception ignored) {}//сообщение от абонента в черном списке, шлем лесом
            //        executeReceived(new SessionException("message from black list: "+address), (byte[])null, data_coding);
            //        break;
            //    }
            //}
            ByteBuffer b = null;
            short seg_id = 0;
            short seg_num = 0;
            short seg_count = 0;
            byte msg_type = MSG_TYPE_TEXT;
            try {
                seg_id = d.getSarMsgRefNum();
                seg_num = d.getSarSegmentSeqnum();
                seg_count = d.getSarTotalSegments();
                //System.err.println("has sar");
            } catch (ValueNotSetException e) {}
            if (d.getSmLength() == 0) {
                try {
                    b = d.getMessagePayload();
                } catch (ValueNotSetException e) {
                    try {
                        session.respond(d.getResponse());
                    } catch (Exception ignored) {
                    }
                    executeReceived(null, new Empty(message_id, msisdn, ussd_sessid, ussdMenuPath), receiveTime);
                    return;
                }
            } else {
                try {
                    b = new ByteBuffer(d.getShortMessage("x-hex").getBytes("x-hex"));
                } catch (UnsupportedEncodingException e) {
                    logger.info("wtf with x-hex???");
                    try {
                        session.respond(d.getResponse());
                    } catch (Exception ignored) {
                    }
                    executeReceived(null, new Empty(message_id, msisdn, ussd_sessid, ussdMenuPath), receiveTime);
                    return;
                }
            }
            //System.err.println("looking for udh");
            if ((d.getEsmClass() & 0x40) != 0) {
                //System.err.println("has udh");
                try {
                    //System.err.println("mess including udh: "+b.getHexDump());
                    byte udhLen = b.removeByte();
                    msg_type = b.removeByte();
                    if (msg_type == 0) {//IEI (склеенное сообщение)
                        byte sar_len = b.removeByte();//IEIL (количество октетов, описывающих конкатенацию)
                        if (sar_len == 4) {
                            seg_id = b.removeShort();
                        } else {
                            seg_id = (short)(b.removeByte() & 0xff);
                        }
                        seg_count = b.removeByte();
                        seg_num = b.removeByte();
                        if (udhLen > sar_len+2) {
                            msg_type = b.removeByte();
                            int count = udhLen - (sar_len + 3);
                            if (count > 0) {
                                b.removeBytes0(count);
                            }
                        }
                    } else {
                        if (udhLen > 1)
                            b.removeBytes0(udhLen-1);
                    }
                    switch (msg_type) {
                        case 0x70:
                            msg_type = MSG_TYPE_TICKET;
                            break;
                        case 0x71:
                            msg_type = MSG_TYPE_POR;
                            break;
                        default:
                            msg_type = MSG_TYPE_TEXT;
                            break;
                    }
                } catch (Exception e) {
                    try {
                       session.respond(d.getResponse());
                    } catch (Exception ignored) {}
                    executeReceived(null, new Empty(message_id, msisdn, ussd_sessid, ussdMenuPath), receiveTime);
                    return;
                }
            }
            if (seg_id == 0) {//получили обычное смс
                SessionException ex = null;
                try {
                    session.respond(d.getResponse());
                } catch (Exception e) {
                    ex = new SessionException("response failed: " + e, Error.ERRCODE_RESPONSE_FAILED);
                }
                executeReceived(ex, (b == null ? null : b.getBuffer()), msg_type, msisdn, message_id, 0, receiveTime, data_coding, ussd_sessid, ussdMenuPath);
            } else {//получили часть длинного смс
                //System.err.println("long sms: segId="+seg_id+" segCount="+seg_count+" segNum="+seg_num);
                try {
                    session.respond(d.getResponse());
                } catch (Exception ignored) {
                    return;
                }
                ReceivedLong message = new ReceivedLong((b == null ? null : b.getBuffer()), seg_id, seg_count, seg_num, d.getSourceAddr(), msg_type, message_id);
                processLongMessage(receiveTime, data_coding, ussd_sessid, ussdMenuPath, seg_num, seg_count, message);
            }
        }

        private void processLongMessage(long receiveTime, byte data_coding, int ussd_sessid, String ussdMenuPath, short seg_num, short seg_count, ReceivedLong message) {
            int arrnum = -1;
            for (int i = 0, max = receivedBigMessages.size(); i < max; ++i) {
                if (receivedBigMessages.get(i).get(0).IsPartOfThisMessage(message)) {
                    arrnum = i;
                    break;
                }
            }
            if (arrnum == -1) {
                arrnum = receivedBigMessages.size();
                ArrayList<ReceivedLong> list = new ArrayList<ReceivedLong>();
                receivedBigMessages.add(list);
                receivedBigMessagesTimeout.add(createLongMessageTimeoutTask(list));
            }
            int i = 0;
            ArrayList<ReceivedLong> list = receivedBigMessages.get(arrnum);
            while (i < list.size() && seg_num > list.get(i).seg_num) {++i;}
            if (i == list.size()) {
                list.add(message);
            } else {
                if (seg_num == list.get(i).seg_num) {
                    list.set(i, message);
                } else {
                    list.add(i, message);
                }
            }
            if (list.size() == seg_count) {
                receivedBigMessages.remove(arrnum);
                receivedBigMessagesTimeout.get(arrnum).cancel(false);
                receivedBigMessagesTimeout.remove(arrnum);
                ByteBuffer bb = new ByteBuffer();
                for (ReceivedLong del : list) {
                    bb.appendBytes(del.bData);
                }
                executeReceived(null, bb.getBuffer(), list.get(0).msg_type, list.get(0).source.getAddress(), list.get(0).messageId, 0, receiveTime, data_coding, ussd_sessid, ussdMenuPath);
            }
        }

        private void processReceivedDataSM(PDU pdu, long receiveTime) {
            String message_id;
            String msisdn;
            ByteBuffer b;
            short seg_id;
            short seg_num;
            short seg_count;
            byte msg_type;
            DataSM dat = (DataSM) pdu;
            byte data_coding = dat.getDataCoding();
            int ussd_sessid;
            switch (startParams.ussd_mapping) {
                case BERKUT_SCHEME_DEST:
                    ussd_sessid = ussdSessionToInt(dat.getDestAddr().getAddress());
                    break;
                case BERKUT_SCHEME_SOURCE:
                    ussd_sessid = ussdSessionToInt(dat.getSourceAddr().getAddress());
                    break;
                case NOWSMS_SCHEME_ITS:
                    try {
                        ussd_sessid = dat.getItsSessionInfo();
                    } catch (ValueNotSetException ignored) {
                        ussd_sessid = 0;
                    }
                    break;
                default:
                    ussd_sessid = 0;
                    break;
            }
            String ussdMenuPath = ussdMenuPath(dat.getDestAddr().getAddress());
            message_id = "";
            try {
                message_id = dat.getReceiptedMessageId();
            } catch (ValueNotSetException ignored) {}
            if (dat.getSourceAddr() == null || dat.getSourceAddr().getAddress() == null || dat.getSourceAddr().getAddress().length() == 0) {
                try {
                   session.respond(dat.getResponse());
                } catch (Exception ignored) {}//сообщение неизвестно от кого, шлем лесом
                executeReceived(new SessionException("unknown source", Error.ERRCODE_UNKNOWN_SOURCE), new Empty(message_id, "unknown", ussd_sessid, ussdMenuPath), receiveTime);
                return;
            }
            msisdn = dat.getSourceAddr().getAddress();
            //{ сюда можно выоткнуть проверку на черный список
            //    String address = dat.getSourceAddr().getAddress();
            //    if (address d черном списке) {
            //        try {
            //           session.respond(dat.getResponse());
            //        } catch (Exception ignored) {}//сообщение от абонента в черном списке, шлем лесом
            //        executeReceived(new SessionException("message from black list: "+address), (byte[])null);
            //        break;
            //    }
            //}
            b = null;
            seg_id = 0;
            seg_num = 0;
            seg_count = 0;
            msg_type = MSG_TYPE_TEXT;
            try {
                b = dat.getMessagePayload();
            } catch (ValueNotSetException e) {
                try {
                   session.respond(dat.getResponse());
                } catch (Exception ignored) {}
                executeReceived(null, new Empty(message_id, msisdn, ussd_sessid, ussdMenuPath), receiveTime);
                return;
            }
            try {
                seg_id = dat.getSarMsgRefNum();
                seg_num = dat.getSarSegmentSeqnum();
                seg_count = dat.getSarTotalSegments();
                //System.err.println("has sar");
            } catch (ValueNotSetException ignored) {}
            //System.err.println("looking for udh");
            if ((dat.getEsmClass() & 0x40) != 0) {
                //System.err.println("has udh");
                try {
                    //System.err.println("mess including udh: "+b.getHexDump());
                    byte udhLen = b.removeByte();
                    msg_type = b.removeByte();
                    if (msg_type == 0) {
                        byte sar_len = b.removeByte();
                        if (sar_len == 4) {
                            seg_id = b.removeShort();
                        } else {
                            seg_id = (short)(b.removeByte() & 0xff);
                        }
                        seg_count = b.removeByte();
                        seg_num = b.removeByte();
                        if (udhLen > sar_len+2) {
                            msg_type = b.removeByte();
                            int count = udhLen - (sar_len + 3);
                            if (count > 0) {
                                b.removeBytes0(count);
                            }
                        }
                    } else {
                        if (udhLen > 1)
                            b.removeBytes0(udhLen-1);
                    }
                    switch (msg_type) {
                        case 0x70:
                            msg_type = MSG_TYPE_TICKET;
                            break;
                        case 0x71:
                            msg_type = MSG_TYPE_POR;
                            break;
                        default:
                            msg_type = MSG_TYPE_TEXT;
                            break;
                    }
                } catch (Exception e) {
                    try {
                       session.respond(dat.getResponse());
                    } catch (Exception ignored) {}
                    executeReceived(null, new Empty(message_id, msisdn, ussd_sessid, ussdMenuPath), receiveTime);
                    return;
                }
            }
            if (seg_id == 0) {//получили обычное смс
                //System.err.println("single sms");
                SessionException ex = null;
                try {
                    session.respond(dat.getResponse());
                } catch (Exception e) {
                    ex = new SessionException("response failed: " + e, Error.ERRCODE_RESPONSE_FAILED);
                }
                executeReceived(ex, b == null ? null : b.getBuffer(), msg_type, msisdn, message_id, 0, receiveTime, data_coding, ussd_sessid, ussdMenuPath);
            } else {//получили часть длинного смс
                //System.err.println("long sms: segId="+seg_id+" segCount="+seg_count+" segNum="+seg_num);
                try {
                   session.respond(dat.getResponse());
                } catch (Exception ignored) {
                    return;
                }
                ReceivedLong message = new ReceivedLong(b == null ? null : b.getBuffer(), seg_id, seg_count, seg_num, dat.getSourceAddr(), msg_type, message_id);
                processLongMessage(receiveTime, data_coding, ussd_sessid, ussdMenuPath, seg_num, seg_count, message);
            }
        }

        private void onReceiveResponse(PDU pdu) {
            if (state != State.STARTED && state != State.STOP_IN_PROGRESS) {
                return;
            }
            switch (pdu.getCommandId()) {
                case Data.SUBMIT_SM_RESP:
                    if (sendInProgress) {
                        if (pdu.getSequenceNumber() == sendingMessageId) {
                            sendingFuture.cancel(false);
                            final Message message = sendingMessage;
                            sendingMessage = null;
                            sendingFuture = null;
                            sendInProgress = false;
                            switch (pdu.getCommandStatus()) {
                                case Data.ESME_ROK:
                                    if (message.isLong && message.len > message.num) {
                                        send(message);
                                    } else {
                                        executeSendComplete(null, message.id, ((SubmitSMResp)pdu).getMessageId());
                                        if (!messageQueue.isEmpty()) {
                                            if (messageQueue.get(0) == null) {
                                                messageQueue.remove(0);
                                                createPingTask();
                                                if (messageQueue.isEmpty()) {
                                                    return;
                                                }
                                            }
                                            Message delayedMessage = messageQueue.get(0);
                                            messageQueue.remove(0);
                                            send(delayedMessage);
                                        }
                                    }
                                    break;
                                case Data.ESME_RTHROTTLED:
                                    if (message.throtCount < startParams.throttling_limit) {
                                        message.num--;
                                        message.throtCount++;
                                        executorService.schedule(new Runnable() {
                                            public void run() {
                                                send(message);
                                            }
                                        }, (message.throtCount==1?startParams.throttling_timeout:startParams.throttling_timeout2), TimeUnit.MILLISECONDS);
                                    } else {
                                        executeSendComplete(new SessionException("throttling error (0x58) limit reached", Error.ERRCODE_THROTTLING_LIMIT_REACHED), message.id, null);
                                        if (!messageQueue.isEmpty()) {
                                            if (messageQueue.get(0) == null) {
                                                messageQueue.remove(0);
                                                createPingTask();
                                                if (messageQueue.isEmpty()) {
                                                    return;
                                                }
                                            }
                                            Message delayedMessage = messageQueue.get(0);
                                            messageQueue.remove(0);
                                            send(delayedMessage);
                                        }
                                    }
                                    break;
                                case 0x440:
                                case Data.ESME_RSYSERR:
                                    if (message.systemErrorCount < startParams.system_error_limit) {
                                        message.num--;
                                        message.systemErrorCount++;
                                        executorService.schedule(new Runnable() {
                                            public void run() {
                                                send(message);
                                            }
                                        }, (message.systemErrorCount==1?startParams.system_error_timeout:startParams.system_error_timeout2), TimeUnit.MILLISECONDS);
                                    } else {
                                        executeSendComplete(new SessionException("system error (0x8, 0x440) limit reached", Error.ERRCODE_SYSTEM_ERROR_LIMIT_REACHED), message.id, null);
                                        if (!messageQueue.isEmpty()) {
                                            if (messageQueue.get(0) == null) {
                                                messageQueue.remove(0);
                                                createPingTask();
                                                if (messageQueue.isEmpty()) {
                                                    return;
                                                }
                                            }
                                            Message delayedMessage = messageQueue.get(0);
                                            messageQueue.remove(0);
                                            send(delayedMessage);
                                        }
                                    }
                                    break;
                                case 0x442://SLA error
                                    if (message.slaErrCount < startParams.SLA_limit) {
                                        message.num--;
                                        message.slaErrCount++;
                                        executorService.schedule(new Runnable() {
                                            public void run() {
                                                send(message);
                                            }
                                        }, (message.slaErrCount==1?startParams.SLA_timeout:startParams.SLA_timeout2), TimeUnit.MILLISECONDS);
                                    } else {
                                        executeSendComplete(new SessionException("SLA error (0x442) limit reached", 0x442), message.id, null);
                                        if (!messageQueue.isEmpty()) {
                                            if (messageQueue.get(0) == null) {
                                                messageQueue.remove(0);
                                                createPingTask();
                                                if (messageQueue.isEmpty()) {
                                                    return;
                                                }
                                            }
                                            Message delayedMessage = messageQueue.get(0);
                                            messageQueue.remove(0);
                                            send(delayedMessage);
                                        }
                                    }
                                    break;
                                case Data.ESME_RMSGQFUL:
                                    if (message.mqfErrCount < startParams.mqf_error_limit) {
                                        message.num--;
                                        message.mqfErrCount++;
                                        executorService.schedule(new Runnable() {
                                            public void run() {
                                                send(message);
                                            }
                                        }, (message.mqfErrCount==1?startParams.mqf_error_timeout:startParams.mqf_error_timeout2), TimeUnit.MILLISECONDS);
                                    } else {
                                        executeSendComplete(new SessionException("0x014 Message Queue Full", Error.ERRCODE_MESSAGE_QUEUE_FULL), message.id, null);
                                        if (!messageQueue.isEmpty()) {
                                            if (messageQueue.get(0) == null) {
                                                messageQueue.remove(0);
                                                createPingTask();
                                                if (messageQueue.isEmpty()) {
                                                    return;
                                                }
                                            }
                                            Message delayedMessage = messageQueue.get(0);
                                            messageQueue.remove(0);
                                            send(delayedMessage);
                                        }
                                    }
                                    break;
                                case 0x443://no pull for push
                                case 0x444://CP not found
                                case 0x445://Service not active
                                case 0x446://incorrect concatenated fragment
                                case 0x447://charging settings error
                                case 0x00A://invalid source address
                                case 0x00B://invalid destination address
                                    executeSendComplete(new SessionException("Error:"+pdu.getCommandStatus(), pdu.getCommandStatus()), message.id, null);
                                    if (!messageQueue.isEmpty()) {
                                        if (messageQueue.get(0) == null) {
                                            messageQueue.remove(0);
                                            createPingTask();
                                            if (messageQueue.isEmpty()) {
                                                return;
                                            }
                                        }
                                        Message delayedMessage = messageQueue.get(0);
                                        messageQueue.remove(0);
                                        send(delayedMessage);
                                    }
                                    break;
                                default:
                                    if (message.otherErrCount < startParams.other_error_limit) {
                                        message.num--;
                                        message.otherErrCount++;
                                        executorService.schedule(new Runnable() {
                                            public void run() {
                                                send(message);
                                            }
                                        }, (message.otherErrCount==1?startParams.other_error_timeout:startParams.other_error_timeout2), TimeUnit.MILLISECONDS);
                                    } else {
                                        executeSendComplete(new SessionException("error limit reached", pdu.getCommandStatus()), message.id, null);
                                        if (!messageQueue.isEmpty()) {
                                            if (messageQueue.get(0) == null) {
                                                messageQueue.remove(0);
                                                createPingTask();
                                                if (messageQueue.isEmpty()) {
                                                    return;
                                                }
                                            }
                                            Message delayedMessage = messageQueue.get(0);
                                            messageQueue.remove(0);
                                            send(delayedMessage);
                                        }
                                    }
                                    break;
                            }
                        } else {
                            //хз, чо с ним делать, я ответ на другое сообщение ждал
                        }
                    } else {
                        //хз, чо с ним делать, я его не ждал вообще
                    }
                    break;
                case Data.ENQUIRE_LINK_RESP:
                    //System.out.println("enquire_link_resp received");
                    if (sendInProgress) {
                        sendingFuture.cancel(false);
                        sendInProgress = false;
                        if (!messageQueue.isEmpty()) {
                            Message delayedMessage = messageQueue.get(0);
                            messageQueue.remove(0);
                            send(delayedMessage);
                        }
                        createPingTask();
                    }
                    break;
            }
        }

        private static Address addUSSDSessionIdToAddress(Address address, String ussd_menuPath, int sessionId) {
            String addressString = address.getAddress();
            if (!addressString.endsWith("#"))
                addressString += '#';
            if (!ussd_menuPath.isEmpty())
                addressString = addressString.replace("#", "*" + ussd_menuPath + "#");
            addressString += (sessionId > 0xA000000 ? Integer.toString(sessionId, 16) : Integer.toString(sessionId));
            try {
                return new Address(address.getTon(), address.getNpi(), addressString);
            } catch (WrongLengthOfStringException e) {
                return address;
            }
        }

        private static Address addUSSDMenuPathToAddress(Address address, String ussd_menuPath) {
            try {
                return new Address(address.getTon(), address.getNpi(), address.getAddress() + (ussd_menuPath.length() > 0 ? "*" + ussd_menuPath : ""));
            } catch (WrongLengthOfStringException e) {
                return address;
            }
        }

        public void send(final Message message) {
            if (state != State.STARTED) {
                executeSendComplete(new SessionException("wrong state", Error.ERRCODE_WRONG_STATE), message.id, null);
                Message m;
                while (!messageQueue.isEmpty()) {
                    m = messageQueue.get(0);
                    messageQueue.remove(0);
                    if (m != null) {
                        executeSendComplete(new SessionException("wrong state", Error.ERRCODE_WRONG_STATE), m.id, null);
                    }
                }
                checkStop();
                return;
            }
            if (sendInProgress) {
                messageQueue.add(message);
                return;
            }
            SubmitSM request = new SubmitSM();
            try {
                if (message.ussd_sessid != 0)
                    switch (startParams.ussd_mapping) {
                        case BERKUT_SCHEME_DEST:
                            request.setSourceAddr(addUSSDSessionIdToAddress(config.sourceAddress, message.ussd_menuPath, message.ussd_sessid));
                            request.setDestAddr(config.destinationAddress);
                            break;
                        case BERKUT_SCHEME_SOURCE:
                            request.setSourceAddr(addUSSDMenuPathToAddress(config.sourceAddress, message.ussd_menuPath));
                            request.setDestAddr(addUSSDSessionIdToAddress(config.destinationAddress, "", message.ussd_sessid));
                            break;
                        case NOWSMS_SCHEME_ITS:
                            request.setItsSessionInfo((short) message.ussd_sessid);
                            request.setSourceAddr(addUSSDMenuPathToAddress(config.sourceAddress, message.ussd_menuPath));
                            request.setDestAddr(config.destinationAddress);
                            break;
                        default:
                            request.setSourceAddr(addUSSDMenuPathToAddress(config.sourceAddress, message.ussd_menuPath));
                            request.setDestAddr(config.destinationAddress);
                            break;
                    }
                else
                {
                    request.setSourceAddr(addUSSDMenuPathToAddress(config.sourceAddress, message.ussd_menuPath));
                    request.setDestAddr(config.destinationAddress);
                }
                if (message.ussd != 0)
                    request.setUssdServiceOp(message.ussd);
                request.setServiceType(startParams.serviceType);
                request.setReplaceIfPresentFlag(startParams.replaceIfPresent);
                request.setPriorityFlag((byte) 1);
                request.setRegisteredDelivery((byte) 1);
                request.assignSequenceNumber(true);
                if (message.isLong) {
                    request.setEsmClass((byte) 0x40);
                    //request.setSarTotalSegments((short)message.len);
                    //request.setSarSegmentSeqnum((short)(message.num+1));
                    //request.setSarMsgRefNum(message.sar_id);
                    ByteBuffer ed = new ByteBuffer();
                    ed.appendByte((byte) 5); // UDH Length
                    ed.appendByte((byte) 0); // IE Identifier 0 - byte for ref num, 8 - short for ref num
                    ed.appendByte((byte) 3); // IE Data Length, 3 for now, 4 if short ref num
                    ed.appendByte(message.sar_id); //Reference Number
                    ed.appendByte((byte) message.len); //Number of pieces
                    ed.appendByte((byte) (message.num + 1)); //Sequence number
                    switch (message.type) {
                        case Message.GSM7BIT:
                            ed.appendString(message.getMessage(), Data.ENC_GSM7BIT);
                            break;
                        case Message.UCS2:
                            request.setDataCoding((byte)0x08);
                            ed.appendString(message.getMessage(), Data.ENC_UTF16_BE);
                            break;
                        case Message.BINARY:
                            request.setDataCoding((byte)-11);
                            ed.appendBytes(message.getBinaryMessage());
                            break;
                        case Message.OTA://bytebuffer в жопу, ota прет в наглую
                            //System.out.println("sending long ota");
                            request.setDataCoding((byte)(0xF6 & 0xff));
                            request.setProtocolId((byte)(0x7F & 0xff));
                            ed.setBuffer(message.getBinaryMessage());
                            break;
                    }
                    request.setShortMessageData(ed);
                } else {
                    switch (message.type) {
                        case Message.GSM7BIT:
                            //System.out.println("sending gsm7bit");
                            request.setShortMessage(message.getMessage(), Data.ENC_GSM7BIT);
                            break;
                        case Message.UCS2:
                            //System.out.println("sending ucs2");
                            request.setDataCoding((byte)0x08);
                            request.setShortMessage(message.getMessage(), Data.ENC_UTF16_BE);
                            break;
                        case Message.BINARY:
                            //System.out.println("sending binary");
                            request.setEsmClass((byte)0x40);
                            request.setDataCoding((byte)-11);
                            ByteBuffer ed = new ByteBuffer();
                            ed.appendByte((byte) 5); // UDH Length
                            ed.appendByte((byte) 0); // IE Identifier 0 - byte for ref num, 8 - short for ref num
                            ed.appendByte((byte) 3); // IE Data Length, 3 for now, 4 if short ref num
                            ed.appendByte((byte) 0) ; //Reference Number
                            ed.appendByte((byte) 1) ; //Number of pieces
                            ed.appendByte((byte) 1) ; //Sequence number
                            ed.appendBytes(message.getBinaryMessage());
                            request.setShortMessageData(ed);
                            break;
                        case Message.OTA:
                            //System.out.println("sending ota");
                            request.setEsmClass((byte)0x40);
                            request.setDataCoding((byte)(0xF6 & 0xff));
                            request.setProtocolId((byte)(0x7F & 0xff));
                            request.setShortMessageData(new ByteBuffer(message.getBinaryMessage()));
                            break;
                    }
                }
                //System.out.println("submit_sm sending");
                this.session.submit(request);
                //System.out.println("submit_sm sended");
                sendingMessageId = request.getSequenceNumber();
                sendInProgress = true;
                sendingMessage = message;
                sendingFuture = executorService.schedule(new Runnable() {
                    public void run() {
                        sendMessageTimeOut();
                    }
                }, Data.RECEIVER_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                executeSendComplete(new SessionException("message not sent"+e, Error.ERRCODE_NOT_SET), message.id, null);
            }
        }

        public void enqLink() {
            if (state != State.STARTED) {
                checkStop();
                return;
            }
            if (sendInProgress) {
                messageQueue.add(null);
                return;
            }
            try {
                //System.out.println("enquire_link sending");
                session.enquireLink(new EnquireLink());
                //System.out.println("enquire_link sended");
                sendInProgress = true;
                sendingFuture = executorService.schedule(new Runnable() {
                    public void run() {
                        pingTimeOut();
                    }
                }, Data.RECEIVER_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                executePingError(new SessionException("ping not sent", Error.ERRCODE_PING_NOT_SENT));
            }
        }

        private void sendMessageTimeOut() {
            final Message message = sendingMessage;
            sendingMessage = null;
            sendingFuture = null;
            sendInProgress = false;
            if (state == State.STARTED || state == State.STOP_IN_PROGRESS) {
                executeSendComplete(new SessionException("timed out", Error.ERRCODE_TIMEOUT_SENDING_MESSAGE), message.id, null);
                checkStop();
            }
            if (!messageQueue.isEmpty()) {
                Message delayedMessage = messageQueue.get(0);
                messageQueue.remove(0);
                if (delayedMessage == null) {
                    if (state == State.STARTED)
                        createPingTask();
                } else {
                    send(delayedMessage);
                }
            }
        }

        private void pingTimeOut() {
            sendingFuture = null;
            sendInProgress = false;
            if (state != State.STARTED) {
                checkStop();
                return;
            }
            if (!messageQueue.isEmpty()) {
                Message delayedMessage = messageQueue.get(0);
                messageQueue.remove(0);
                send(delayedMessage);
            }
            executePingError(new SessionException("enquire_link timed out", Error.ERRCODE_PING_TIMEOUT));
        }

        public void stop() {
            if (state != State.STARTED) {
                executeStopComplete(new SessionException("wrong state", Error.ERRCODE_WRONG_STATE));
                return;
            }
            state = State.STOP_IN_PROGRESS;
            checkStop();
        }

        private void checkStop() {
            if (state != State.STOP_IN_PROGRESS) {
                return;
            }
            if (!sendInProgress && messageQueue.isEmpty()) {
                SessionException ex = null;
                try {
                    logger.info("trying to unbind");
                    if (session.unbind() == null) {
                        throw new Exception("Unsuccessfull unbind");
                    }
                } catch (Exception e) {
                    logger.error("error unbinding in destroy", e);
                    ex = new SessionException("Stopping error " + e, Error.ERRCODE_NOT_SET);
                } finally {
                    try {
                        session.close();
                    } catch (Exception ignored) {
                    }
                }
                state = State.STOPPED;
                executeStopComplete(ex);
            } else {
                logger.info("some trash in sending state or sending queue, cannot unbind right now");
            }
        }

        private void executeSendComplete(final SessionException ex, final long messId, final String serverAssignedId) {
            listenerExecutorService.execute(new Runnable() {
                public void run() {
                    config.listener.sendComplete(ex, messId, serverAssignedId);
                }
            });
        }

        private void executeStartComplete(final SessionException ex) {
            listenerExecutorService.execute(new Runnable() {
                public void run() {
                    config.listener.startComplete(ex);
                }
            });
        }

        private void executePingError(final SessionException ex) {
            listenerExecutorService.execute(new Runnable() {
                public void run() {
                    config.listener.pingError(ex);
                }
            });
        }

        private void executeStopComplete(final SessionException ex) {
            listenerExecutorService.execute(new Runnable() {
                public void run() {
                    config.listener.stopComplete(ex);
                }
            });
        }

        private void executeReceived(final SessionException ex, final MSG msg, final long receiveTime) {
            listenerExecutorService.execute(new Runnable() {
                public void run() {
                    config.listener.received(ex, msg, receiveTime, System.currentTimeMillis());
                }
            });
        }

        private void executeReceived(final SessionException ex, final byte[] message, final byte msg_type, final String msisdn, final String messageId, final int actions, final long receiveTime, final byte data_coding, final int ussd_sessid, final String ussd_menuPath) {
            listenerExecutorService.execute(new Runnable() {
                public void run() {
                    long processingStart = System.currentTimeMillis();
                    MSG msg = new Empty(messageId, msisdn, ussd_sessid, ussd_menuPath);
                    switch (msg_type) {
                        case MSG_TYPE_TEXT:
                            msg = new Text(messageId, msisdn, message, data_coding, ussd_sessid, ussd_menuPath);
                            break;
                        case MSG_TYPE_POR: {//003B 0A 524A45 000000000000 00 AB2E80020004231E4DFF3A01580D2020820F010200201201DED9B3FC7520E63B02D17CC3900023029000230490009000
                            int mess_len = ((message[0] & 0xFF)<<8) | (message[1] & 0xFF);  //RPL
                            int header_len = (message[2] & 0xFF);  //RHL
                            logger.info(
                                    "parsing por:" + Client.toStr(message) + " RPL=" + mess_len + " RHL=" + header_len);
                            String tar = "";
                            try {
                                tar = new String(new byte[]{message[3], message[4], message[5]}, "ASCII"); //TAR
                            } catch (UnsupportedEncodingException ignored) {}
                            byte[] cntr = new byte[6];
                            System.arraycopy(message, 6, cntr, 0, 6);//CNTR+PCNTR
                            byte resp_status_code = message[12];//Response status code, 00 for PoR OK
                            byte[] cs = null;
                            if (header_len > 0x0a) {
                                cs = new byte[header_len-10];
                                System.arraycopy(message, 13, cs, 0, cs.length);
                            }
                            //todo decrypt if need
                            byte[] processedData = new byte[mess_len-header_len-1];
                            System.arraycopy(message, header_len+3, processedData, 0, processedData.length);

                            msg = new PoR(messageId, msisdn, mess_len, header_len, tar, cntr, processedData, resp_status_code, cs, ussd_sessid, ussd_menuPath);
                        }
                        break;
                        case MSG_TYPE_TICKET: {//00B7 15 0200 00 15 534054 000000000000 40E0CF63D45D884A 01018C0000000000000600145003022303030168000A980701015073000071560400017D407B0D790E036275790F088A0220548A0220540F088A0220328A0220420F5E8A5800810302016D82047A30858083031012138403111212810300000086313022B8ECA3215DCD768852E29F97521E24DC6A6B2FA4537D5CF3AA782C13533109DC6A6B2FA4537D5CF3AA782C13533109920820000000000000028A02204F
                            int mess_len = ((message[0] & 0xFF)<<8) | (message[1] & 0xFF);  //RPL
                            int header_len = (message[2] & 0xFF);  //RHL
                            logger.info("parsing ticket:" + Client.toStr(message) + " RPL=" + mess_len + " RHL="
                                    + header_len);
                            int spi = ((message[3] & 0xFF)<<8) | (message[4] & 0xFF); // SPI
                            int kic = (message[5] & 0xFF); // kic
                            int kid = (message[6] & 0xFF); // kid
                            String tar = "";
                            try {
                                tar = new String(new byte[]{message[7], message[8], message[9]}, "ASCII"); //TAR
                            } catch (UnsupportedEncodingException ignored) {}
                            byte[] cntr = new byte[6];
                            System.arraycopy(message, 10, cntr, 0, 6);//CNTR+PCNTR
                            byte[] cs = null;
                            if (header_len > 0x07) {
                                cs = new byte[header_len-13];
                                System.arraycopy(message, 16, cs, 0, cs.length);
                            }
                            //todo decrypt if need
                            byte[] processedData = new byte[mess_len-header_len-1];
                            System.arraycopy(message, header_len+3, processedData, 0, processedData.length);

                            msg = new Ticket(messageId, msisdn, mess_len, header_len, spi, kic, kid, tar, cntr, processedData, cs, ussd_sessid, ussd_menuPath);
                        }
                        break;
                    }
                    config.listener.received(ex, msg, receiveTime, processingStart);
                }
            });
        }

        public void reset() {
            sendingMessage = null;
            sendingMessageId = 0;
            sendingFuture = null;
            session = null;
            pduListener = null;
            startParams = null;
            this.sendInProgress = false;
            this.enqFuture = null;
            this.messageQueue.removeAll(this.messageQueue);
            this.receivedBigMessages.removeAll(this.receivedBigMessages);
            for (Future f : receivedBigMessagesTimeout) {
                f.cancel(false);
            }
            this.receivedBigMessagesTimeout.removeAll(this.receivedBigMessagesTimeout);
            this.state = State.INITIAL;
        }

        public void destroy() {
            reset();
            try {
                if (session != null)
                if (session.unbind() == null) {
                    throw new Exception("Unsuccessfull unbind");
                }
            } catch (Exception e) {
                logger.warn("error unbinding in destroy", e);
            } finally{
                try {
                    if (session != null)
                        session.close();
                    } catch (Exception ignored) {}
            }
            this.state = State.DESTROYED;
        }
    }


    private final ScheduledExecutorService executorService;
    private final ExecutorService listenerExecutorService;
    private final Impl impl;
    private long nextId;
    private boolean destroyed;

    public Session(Config config) {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.listenerExecutorService = Executors.newSingleThreadExecutor();
        this.impl = new Impl(config, this.executorService, this.listenerExecutorService);
        nextId = 0L;
        destroyed = false;
    }

    public void asyncStart(final StartParams startParams) throws SessionException {
        if (destroyed) {
            throw new SessionException("session is destroyed", Error.ERRCODE_SESSION_DESTROYED);
        }
        this.executorService.execute(new Runnable() {
            public void run() {
                impl.start(startParams);
            }
        });
    }

    public long asyncSend(final String mess, final boolean ucs2, final byte ussd, final int ussd_sessid, final String ussd_menuPath) throws SessionException {
        if (destroyed) {
            throw new SessionException("session is destroyed", Error.ERRCODE_SESSION_DESTROYED);
        }
        final Message message = new Message(mess, nextId++, ucs2);
        message.ussd = ussd;
        message.ussd_sessid = ussd_sessid;
        message.ussd_menuPath = ussd_menuPath;
        this.executorService.execute(new Runnable() {
            public void run() {
                impl.send(message);
            }
        });
        return message.id;
    }

    public long asyncSend(final byte[] mess) throws SessionException, IllegalArgumentException {
        if (destroyed) {
            throw new SessionException("session is destroyed", Error.ERRCODE_SESSION_DESTROYED);
        }
        if (mess == null) {
            throw new IllegalArgumentException("message is null");
        }
        final Message message = new Message(mess, nextId++);
        this.executorService.execute(new Runnable() {
            public void run() {
                impl.send(message);
            }
        });
        return message.id;
    }

    public long asyncSendOTA(final byte[] mess, String kc, String kd, byte[] tar, byte[] spi, byte[] key, byte[] tpscts) throws SessionException, IllegalArgumentException {
        if (destroyed) {
            throw new SessionException("session is destroyed", Error.ERRCODE_SESSION_DESTROYED);
        }
        if (mess == null) {
            throw new IllegalArgumentException("message is null");
        }
        final Message message = new Message(mess, nextId++, kc, kd, tar, spi, key, tpscts);
        this.executorService.execute(new Runnable() {
            public void run() {
                impl.send(message);
            }
        });
        return message.id;
    }

    public void asyncStop() throws SessionException {
        if (destroyed) {
            throw new SessionException("session is destroyed", Error.ERRCODE_SESSION_DESTROYED);
        }
        this.executorService.execute(new Runnable() {
            public void run() {
                impl.stop();
            }
        });
    }

    public void reset() throws SessionException {
        if (destroyed) {
            throw new SessionException("session is destroyed", Error.ERRCODE_SESSION_DESTROYED);
        }
        if (impl.state != Impl.State.STOPPED) {
            throw new SessionException("session must be stopped", Error.ERRCODE_WRONG_STATE);
        }
        impl.reset();
    }

    public void destroy() {
        shutdownExecutor(executorService);
        shutdownExecutor(listenerExecutorService);
        impl.destroy();
        destroyed = true;
    }

    private static void shutdownExecutor(ExecutorService executorService) {
        try {
            executorService.shutdown();
        } catch (Exception ignored) {
        }
        try {
            if (!executorService.awaitTermination(20000L, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            try {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
            }
        }
    }

    public static interface Listener extends MessageTypes {

        void startComplete(SessionException ex);

        void sendComplete(SessionException ex, long messageId, String serverAssignedId);

        void stopComplete(SessionException ex);

        void received(SessionException ex, MSG message, long receiveTime, long processTime);

        void pingError(SessionException ex);
    }
}