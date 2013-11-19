/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:52:14
 * To change this template use File | Settings | File Templates.
 */
public class PaymentResponse {

    public static class ResPaymentRegistry {

        public static class Item {

            public static class ClientInfo {

                public static class PersonInfo {

                    private final String firstName;
                    private final String surname;
                    private final String secondName;

                    public PersonInfo(Person person) {
                        this.firstName = person.getFirstName();
                        this.surname = person.getSurname();
                        this.secondName = person.getSecondName();
                    }

                    public PersonInfo(String firstName, String surname, String secondName) {
                        this.firstName = firstName;
                        this.surname = surname;
                        this.secondName = secondName;
                    }

                    public String getFirstName() {
                        return firstName;
                    }

                    public String getSurname() {
                        return surname;
                    }

                    public String getSecondName() {
                        return secondName;
                    }

                    @Override
                    public String toString() {
                        return "PersonInfo{" + "firstName='" + firstName + '\'' + ", surname='" + surname + '\''
                                + ", secondName='" + secondName + '\'' + '}';
                    }
                }

                private final PersonInfo person;
                private final PersonInfo contractPerson;
                private final String address;

                public ClientInfo(Client client) {
                    this.person = new PersonInfo(client.getPerson());
                    this.contractPerson = new PersonInfo(client.getContractPerson());
                    this.address = client.getAddress();
                }

                public PersonInfo getPerson() {
                    return person;
                }

                public PersonInfo getContractPerson() {
                    return contractPerson;
                }

                public String getAddress() {
                    return address;
                }

                @Override
                public String toString() {
                    return "ClientInfo{" + "person=" + person + ", contractPerson=" + contractPerson + ", address='"
                            + address + '\'' + '}';
                }
            }

            public static class CardInfo {

                private final Long cardPrintedNo;

                public CardInfo(Card card) {
                    this.cardPrintedNo = card.getCardPrintedNo();
                }

                public Long getCardPrintedNo() {
                    return cardPrintedNo;
                }

                @Override
                public String toString() {
                    return "CardInfo{" + "cardPrintedNo=" + cardPrintedNo + '}';
                }
            }

            private final PaymentRequest.PaymentRegistry.Payment payment;
            private final Long idOfClient;
            private final Long contractId;
            private final Long tspContragentId;
            private final Long idOfCard;
            private final Long balance;
            private final Long subBalance1;
            private final int result;
            private final String error;
            private final ClientInfo client;
            private final CardInfo card;
            private final HashMap<String, String> addInfo;

            public Item(PaymentRequest.PaymentRegistry.Payment payment, Long idOfClient, Long contractId, Long tspContragentId, Long idOfCard, Long balance,
                    Long subBalance1, int result, String error, HashMap<String, String> addInfo) {
                this.payment = payment;
                this.idOfClient = idOfClient;
                this.contractId = contractId;
                this.tspContragentId = tspContragentId;
                this.idOfCard = idOfCard;
                this.balance = balance;
                this.subBalance1 = subBalance1;
                this.result = result;
                this.error = error;
                this.client = null;
                this.card = null;
                this.addInfo = addInfo;
            }

            public Item(PaymentRequest.PaymentRegistry.Payment payment, Long idOfClient, Long contractId, Long tspContragentId, Long idOfCard, Long balance,
                    int result, String error, Client client, Long subBalance1, HashMap<String, String> addInfo) {
                this.payment = payment;
                this.idOfClient = idOfClient;
                this.contractId = contractId;
                this.tspContragentId = tspContragentId;
                this.idOfCard = idOfCard;
                this.balance = balance;
                this.result = result;
                this.error = error;
                this.subBalance1 = subBalance1;
                this.client = new ClientInfo(client);
                this.card = null;
                this.addInfo = addInfo;
            }

            public Item(PaymentRequest.PaymentRegistry.Payment payment, Long idOfClient, Long contractId, Long tspContragentId, Long idOfCard, Long balance,
                    int result, String error, Client client, Card card, Long subBalance1, HashMap<String, String> addInfo) {
                this.payment = payment;
                this.idOfClient = idOfClient;
                this.contractId = contractId;
                this.tspContragentId = tspContragentId;
                this.idOfCard = idOfCard;
                this.balance = balance;
                this.result = result;
                this.error = error;
                this.subBalance1 = subBalance1;
                this.client = new ClientInfo(client);
                if (null == card) {
                    this.card = null;
                } else {
                    this.card = new CardInfo(card);
                }
                this.addInfo = addInfo;
            }

            public Long getBalance() {
                return balance;
            }

            public PaymentRequest.PaymentRegistry.Payment getPayment() {
                return payment;
            }

            public Long getIdOfClient() {
                return idOfClient;
            }

            public Long getContractId() {
                return contractId;
            }

            public Long getIdOfCard() {
                return idOfCard;
            }

            public int getResult() {
                return result;
            }

            public String getError() {
                return error;
            }

            public ClientInfo getClient() {
                return client;
            }

            public CardInfo getCard() {
                return card;
            }

            public Long getTspContragentId() {
                return tspContragentId;
            }

            public HashMap<String, String> getAddInfo() {
                return addInfo;
            }

            public Long getSubBalance1() {
                return subBalance1;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("RPT");
                element.setAttribute("IdOfPayment", this.payment.getIdOfPayment());
                element.setAttribute("Result", Integer.toString(this.result));
                if (null != this.error) {
                    element.setAttribute("Error", this.error);
                }
                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "payment=" + payment + ", idOfClient=" + idOfClient + ", idOfCard=" + idOfCard
                        + ", balance=" + balance +  ", subBalance1=" + subBalance1 + ", result=" + result + ", error='" + error + '\'' + ", client="
                        + client + ", card=" + card + '}';
            }
        }

        private final List<Item> items = new LinkedList<Item>();

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public Iterator<Item> getItems() {
            return items.iterator();
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResPaymentRegistry");
            for (Item item : this.items) {
                element.appendChild(item.toElement(document));
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResPaymentRegistry{" + "items=" + items + '}';
        }
    }

    private final Long idOfContragent;
    private final Long idOfPacket;
    private final Long version;
    private final Date time;
    private final ResPaymentRegistry resPaymentRegistry;

    public PaymentResponse(Long idOfContragent, Long idOfPacket, Long version, Date time,
            ResPaymentRegistry resPaymentRegistry) {
        this.idOfContragent = idOfContragent;
        this.idOfPacket = idOfPacket;
        this.version = version;
        this.time = time;
        this.resPaymentRegistry = resPaymentRegistry;
    }

    public Document toDocument() throws Exception {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        timeFormat.setTimeZone(localTimeZone);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element dataElement = document.createElement("Data");
        Element bodyElement = document.createElement("Body");

        Element ecafeEnvelopeElement = document.createElement("PaymentExchange");
        ecafeEnvelopeElement.setAttribute("IdOfContragent", this.idOfContragent.toString());
        ecafeEnvelopeElement.setAttribute("IdOfPacket", this.idOfPacket.toString());
        ecafeEnvelopeElement.setAttribute("Version", this.version.toString());
        ecafeEnvelopeElement.setAttribute("SyncTime", timeFormat.format(this.time));

        // ResPaymentRegistry
        ecafeEnvelopeElement.appendChild(resPaymentRegistry.toElement(document));

        bodyElement.appendChild(ecafeEnvelopeElement);
        dataElement.appendChild(bodyElement);
        document.appendChild(dataElement);
        return document;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public Long getVersion() {
        return version;
    }

    public Date getTime() {
        return time;
    }

    public ResPaymentRegistry getResAccRegistry() {
        return resPaymentRegistry;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" + "idOfContragent=" + idOfContragent + ", idOfPacket=" + idOfPacket + ", version="
                + version + ", time=" + time + ", resPaymentRegistry=" + resPaymentRegistry + '}';
    }
}