/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientSmsList {

    public static class Item {

        private final String idOfSms;
        private final Long idOfCard;
        private final Long cardNo;
        private final Long version;
        private final String phone;
        private final Integer contentsType;
        private final String textContents;
        private final Integer deliveryStatus;
        private final Date serviceSendTime;
        private final Date sendTime;
        private final Date deliveryTime;
        private final Long price;
        private final Long idOfTransaction;
        private final Integer eventType;
        private final Long eventId;
        private final Date eventTime;
        private final Long contractId;
        private final Long orgId;
        private final String fio;
        private String guardian;
        private String guardianAsString;
        private String contentsTypeAsString;
        private String deliveryStatusAsString;
        private String child;

        public Item(ClientSms clientSms) {
            this.idOfSms = clientSms.getIdOfSms();
            this.version = clientSms.getVersion();
            this.phone = clientSms.getPhone();
            this.contentsType = clientSms.getContentsType();
            this.deliveryStatus = clientSms.getDeliveryStatus();
            this.textContents = clientSms.getTextContents();
            this.serviceSendTime = clientSms.getServiceSendTime();
            this.sendTime = clientSms.getSendTime();
            this.deliveryTime = clientSms.getDeliveryTime();
            this.price = clientSms.getPrice();
            AccountTransaction accountTransaction = clientSms.getTransaction();
            this.idOfTransaction = accountTransaction==null?null:accountTransaction.getIdOfTransaction();
            Card card = null == accountTransaction ? null : accountTransaction.getCard();
            if (null == card) {
                this.idOfCard = null;
                this.cardNo = null;
            } else {
                this.idOfCard = card.getIdOfCard();
                this.cardNo = card.getCardNo();
            }
            this.eventType = clientSms.getContentsType();
            this.eventId = clientSms.getContentsId();
            this.eventTime = clientSms.getEventTime();
            this.contractId = clientSms.getClient().getContractId();
            this.fio = clientSms.getClient().getPerson().getFullName();
            this.orgId = clientSms.getClient().getOrg().getIdOfOrg();
        }

        public static final int TYPE_NEGATIVE_BALANCE = 1;
        public static final int TYPE_ENTER_EVENT_NOTIFY = 2;
        public static final int TYPE_PAYMENT_REGISTERED = 3;
        public static final int TYPE_LINKING_TOKEN = 4;
        public static final int TYPE_PAYMENT_NOTIFY= 5;
        public static final int TYPE_SMS_SUBSCRIPTION_FEE = 6;
        public static final int TYPE_SMS_SUB_FEE_WITHDRAW = 7;
        public static final int TYPE_SUBSCRIPTION_FEEDING = 8;
        public static final int TYPE_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS = 9;

        public String getIdOfSms() {
            return idOfSms;
        }

        public Long getIdOfCard() {
            return idOfCard;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public Long getVersion() {
            return version;
        }

        public String getPhone() {
            return phone;
        }

        public Integer getContentsType() {
            return contentsType;
        }

        public String getTextContents() {
            return textContents;
        }

        public Integer getDeliveryStatus() {
            return deliveryStatus;
        }

        public Date getServiceSendTime() {
            return serviceSendTime;
        }

        public Date getSendTime() {
            return sendTime;
        }

        public Date getDeliveryTime() {
            return deliveryTime;
        }

        public Long getPrice() {
            return price;
        }

        public Long getIdOfTransaction() {
            return idOfTransaction;
        }

        public Integer getEventType() {
            return eventType;
        }

        public Long getEventId() {
            return eventId;
        }

        public Date getEventTime() {
            return eventTime;
        }

        public Long getContractId() {
            return contractId;
        }

        public String getFio() {
            return fio;
        }

        public String getGuardian() {
            return guardian;
        }

        public void setGuardian(String value) {
            if (value == null) {
                guardian = "";
            } else {
                guardian = value.concat("\n");
            }
        }

        /*public String getGuardianAsString() {
            return isGuardian ? "Да" : "Нет";
        }*/

        public String getContentsTypeAsString() {
            if (contentsType >= 0 && contentsType < ClientSms.CONTENTS_TYPE_DESCRIPTION.length) {
                return ClientSms.CONTENTS_TYPE_DESCRIPTION[contentsType];
            }
            return ClientSms.UNKNOWN_CONTENTS_TYPE_DESCRIPTION;
        }

        public String getDeliveryStatusAsString() {
            if (deliveryStatus >= 0 && deliveryStatus < ClientSms.DELIVERY_STATUS_DESCRIPTION.length) {
                return ClientSms.DELIVERY_STATUS_DESCRIPTION[deliveryStatus];
            }
            return ClientSms.UNKNOWN_DELIVERY_STATUS_DESCRIPTION;
        }

        public String getChild() {
            return child;
        }

        public void setChild(Session session) {
            String childFIO = "";
            try {
                Client client = DAOUtils.findClientByContractId(session, this.contractId);
                List<ClientGuardianItem> wards = ClientManager.loadWardsByClient(session, client.getIdOfClient(), false);
                HashSet<Long> orgs = new HashSet<Long>();
                HashSet<Long> children = new HashSet<Long>();
                children.add(client.getIdOfClient());
                for (ClientGuardianItem cgi : wards) {
                    Client ward = (Client)session.load(Client.class, cgi.getIdOfClient());
                    children.add(ward.getIdOfClient());
                    Org org = ward.getOrg();
                    orgs.add(org.getIdOfOrg());
                    Set<Org> friends = org.getFriendlyOrg();
                    for(Org forg : friends) {
                        orgs.add(forg.getIdOfOrg());
                    }
                }
                Query query = null;
                if (this.eventId != null) {
                    //if (orgs.size() == 0) {
                    //    this.child = "";
                    //    return;
                    //}
                    if (contentsType == ClientSms.TYPE_ENTER_EVENT_NOTIFY) {
                        String squery =
                                "select idOfClient from cf_enterevents where idOfOrg in (" + StringUtils.join(orgs, ",")
                                        + ") and IdOfEnterEvent = :eventId and IdOfClient in (" + StringUtils
                                        .join(children, ",") + ")";
                        query = session.createSQLQuery(squery);
                        query.setParameter("eventId", this.eventId);

                    }
                    if (contentsType == ClientSms.TYPE_PAYMENT_REGISTERED) {
                        String squery =
                                "select t.idOfClient from cf_clientpayments p join cf_transactions t on p.idOfTransaction = t.idOfTransaction "
                                        + " where p.idOfClientPayment = :eventId";
                        query = session.createSQLQuery(squery);
                        query.setParameter("eventId", this.eventId);
                    }
                    if (contentsType == ClientSms.TYPE_PAYMENT_NOTIFY) {
                        String squery =
                                "select idOfClient from cf_orders where idOfOrg in (" + StringUtils.join(orgs, ",")
                                        + ") and IdOfOrder = :eventId and IdOfClient in (" + StringUtils
                                        .join(children, ",") + ")";
                        query = session.createSQLQuery(squery);
                        query.setParameter("eventId", this.eventId);
                    }
                }
                else
                {
                    //Пытаемся извлечь информацию о клиенте из SMS

                    //При возврате билета в музей или при входе в музей
                    if (contentsType == ClientSms.TYPE_NOENTER_MUSEUM_NOTIFICATION ||
                            contentsType == ClientSms.TYPE_ENTER_MUSEUM_NOTIFICATION) {
                        String contactid = this.textContents;
                        String cons = "л/с: ";
                        contactid = contactid.substring(contactid.indexOf(cons) + cons.length());
                        contactid = contactid.substring(0,contactid.indexOf(" ") - 2);
                        String squery ="select idofclient from cf_clients where contractid=" + contactid;
                        query = session.createSQLQuery(squery);
                    }
                }
                if (query != null) {
                    Long clientId = ((BigInteger) query.list().get(0)).longValue();
                    if (clientId != null) {
                        childFIO = ((Client) session.load(Client.class, clientId)).getPerson().getFullName();
                    }
                }
            }
            catch (Exception e) {
                childFIO = "";
            }
            this.child = childFIO;
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Session session, Client client, Date startTime, Date endTime) throws Exception {
        Criteria criteria = session.createCriteria(ClientSms.class);
        criteria.add(Restrictions.ge("serviceSendTime", startTime));
        criteria.add(Restrictions.le("serviceSendTime", endTime));
        criteria.add(Restrictions.eq("client", client));
        criteria.addOrder(Order.asc("eventTime"));

        List<Item> items = new LinkedList<Item>();
        List clientPayments = criteria.list();
        for (Object object : clientPayments) {
            ClientSms clientSms = (ClientSms) object;
            items.add(new Item(clientSms));
        }
        this.items = items;
    }

    public void fillWithClients(Session session, List<Client> clients, Date startTime, Date endTime, HashMap<Long, List<String>> mapGuardians) throws Exception {
        Criteria criteria = session.createCriteria(ClientSms.class);
        criteria.add(Restrictions.ge("serviceSendTime", startTime));
        criteria.add(Restrictions.le("serviceSendTime", endTime));
        criteria.add(Restrictions.in("client", clients));
        criteria.addOrder(Order.asc("client"));
        criteria.addOrder(Order.asc("eventTime"));

        List<Item> items = new LinkedList<Item>();
        List clientPayments = criteria.list();
        for (Object object : clientPayments) {
            ClientSms clientSms = (ClientSms) object;
            Item item = new Item(clientSms);
            List<String> guardians = mapGuardians.get(clientSms.getClient().getIdOfClient());
            /*if (guardians == null || guardians.size() == 0) {
                Item item = new Item(clientSms);
                items.add(item);
            }
            else {
                for (String g : guardians) {
                    Item item = new Item(clientSms);
                    item.setGuardian(g);
                    items.add(item);
                }
            }*/
            item.setGuardian(StringUtils.join(guardians, ",\n"));
            item.setChild(session);
            items.add(item);
        }
        this.items = items;
    }

}