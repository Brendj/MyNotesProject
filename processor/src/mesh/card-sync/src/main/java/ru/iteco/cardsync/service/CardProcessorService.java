/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.CardActionClient;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.models.Client;
import ru.iteco.cardsync.repo.CardActionClientRepository;
import ru.iteco.cardsync.repo.CardSyncRepository;
import ru.iteco.cardsync.repo.ClientRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CardProcessorService {

    private static final String OK = "Успешная обработка запроса";

    private final Logger log = LoggerFactory.getLogger(CardProcessorService.class);

    private final CardActionRequestService cardActionRequestService;
    private final CardActionClientService cardActionClientService;
    private final CardSyncService cardSyncService;
    private final CardService cardService;
    private final ClientRepository clientRepository;

    public CardProcessorService(CardActionRequestService cardActionRequestService, CardService cardService,
            ClientRepository clientRepository, CardActionClientService cardActionClientService,
                                CardSyncService cardSyncService) {
        this.cardActionRequestService = cardActionRequestService;
        this.cardSyncService = cardSyncService;
        this.cardActionClientService = cardActionClientService;
        this.cardService = cardService;
        this.clientRepository = clientRepository;
    }

    public boolean newReq(BlockPersonEntranceRequest request)
    {
        List<CardActionRequest> blockRequests = cardActionRequestService.findRequestBlockByRequestIdOLD(request.getId());
        if (blockRequests.isEmpty()) {
            return false;
        }
        if (blockRequests.get(0).getAudit().getCreateDate().getTime() > new Date(1599598800000L).getTime())
            return true;
        else
            return false;
    }

    public void processUnblockRequestOLD(BlockPersonEntranceRequest request) {
        try {
            if (request == null) {
                return;
            }
            List<CardActionRequest> blockRequests = cardActionRequestService.findRequestBlockByRequestIdOLD(request.getId());
            if (blockRequests.isEmpty()) {
                cardActionRequestService.writeRecordOLD(request, "В БД нет запроса на блокировку", false);
                return;
            }
            //Для всех клиентов, заблокированных по одному id
            for (CardActionRequest blockRequest: blockRequests) {
                Client client = blockRequest.getClient();
                if (client == null) {
                    cardActionRequestService.writeRecordOLD(request, "Не найден клиент для разблокировки карт", false);
                    continue;
                }
                List<Card> cardsActive = cardService.getActiveCard(client, false);
                if (!cardsActive.isEmpty())
                {
                    cardActionRequestService.writeRecordOLD(request, "Есть активные карты на момент разблокировки", false, client);
                    continue;
                }
                List<Card> cards = cardService.getBlockedCard(client);
                if (CollectionUtils.isEmpty(cards)) {
                    cardActionRequestService.writeRecordOLD(request, "Не найдено карт для разблокировки", false, client);
                    continue;
                }

                for (Card c : cards) {
                    cardService.unblockCard(c);
                }

                cardActionRequestService.writeRecordOLD(request, OK, true, client);
            }
        } catch (Exception e) {
            log.error(String.format("Error when process request %s", request.getId()), e);
            cardActionRequestService.writeRecordOLD(request, "Ошибка при обработке запроса: " + e.getMessage(), false);
        }
    }

    public void processUnblockRequest(BlockPersonEntranceRequest request) {
        try {
            if (request == null) {
                return;
            }

            CardActionRequest cardActionRequest = CardActionRequest.buildCardActionRequest(request);

            CardActionRequest blockRequests = cardActionRequestService.findRequestBlockByRequestId(request.getId());
            if (blockRequests == null) {
                cardActionRequestService.writeRecord(cardActionRequest, "В БД нет запроса на блокировку", false);
                return;
            }

            cardActionRequestService.writeRecord(cardActionRequest, "Старт разблокировки карт", false);

            //Для всех карт, заблокированных по одному id
            for (CardActionClient cardActionClient: blockRequests.getCardActionClients()) {
                Client client = cardActionClient.getClient();
                if (client == null) {
                    cardActionClientService.writeRecord(cardActionRequest, cardActionClient, null, "Не найден клиент для разблокировки карт");
                    continue;
                }
                List<Card> cardsActive = cardService.getActiveCard(client, false);
                if (!cardsActive.isEmpty())
                {
                    cardActionClientService.writeRecord(cardActionRequest, cardActionClient, cardsActive,  "Есть активные карты на момент разблокировки");
                    continue;
                }
                List<Card> cards = cardService.getBlockedCard(client);
                if (CollectionUtils.isEmpty(cards)) {
                    cardActionClientService.writeRecord(cardActionRequest, cardActionClient, null, "Не найдено карт для разблокировки");
                    continue;
                }
                if (cardActionClient.getCard() != null) {
                    cardService.unblockCard(cardActionClient.getCard());
                    cardSyncService.savechangeforCard(cardActionClient.getCard(), client.getIdoforg());
                    cardActionClientService.writeRecord(cardActionRequest, cardActionClient, null, "Карта клиента успешно разблокирована");
                }
                else
                {
                    cardActionClientService.writeRecord(cardActionRequest, cardActionClient.getComment(), cardActionClient.getClient());
                }
            }
            cardActionRequestService.writeRecord(cardActionRequest, OK, true, blockRequests);
        } catch (Exception e) {
            log.error(String.format("Error when process request %s", request.getId()), e);
            cardActionRequestService.writeRecord(request, "Ошибка при обработке запроса: " + e.getMessage(), false);
        }
    }
    public void processBlockRequest(BlockPersonEntranceRequest request) {
        try {
            if (request == null) {
                return;
            } else if (StringUtils.isEmpty(request.getContingentId()) && StringUtils.isEmpty(request.getStaffId())) {
                cardActionRequestService
                        .writeRecord(request, "Не определен тип клиента (не заполнены поля staff_id, contingent_id)",
                                false);
                return;
            }
            if (StringUtils.isNotEmpty(request.getContingentId())) {
                Client client = clientRepository.findFirstByMeshGuid(request.getContingentId());
                if (client == null) {
                    cardActionRequestService.writeRecord(request, "Обучающийся не найден", false);
                    return;
                }
                if (StringUtils.containsIgnoreCase(client.getAgeGroup(), "дошкол")) { // Обработка дошкольников
                    if(CollectionUtils.isEmpty(client.getGuardians())){
                        cardActionRequestService.writeRecord(request, "Клиент успешно не обработан","Представители не найдены", false, client);
                        return;
                    }
                    List<Client> guardins = new ArrayList<>();
                    guardins.addAll(client.getGuardians());
                    processBlockRequest(request, guardins, client);
                } else { // Обработка клиента на прямую
                    processBlockRequest(request, new ArrayList<Client>(){{ add(client); }}, null);
                }
            } else { // Обработка сотрудников
                if (request.getOrganizationIds().isEmpty())
                {
                    cardActionRequestService.writeRecord(request, "Не указана ОО", false);
                    return;
                }
                List<Client> clients;
                if (request.getMiddleName() != null && !request.getMiddleName().isEmpty()) {
                   clients = clientRepository.getStaffByFIOandOrgId(request.getFirstName(),
                            request.getLastName(), request.getMiddleName(), request.getOrganizationIds());
                }
                else
                {
                    clients = clientRepository.getStaffByFIOandOrgId(request.getFirstName(),
                            request.getLastName(), "", request.getOrganizationIds());
                }

                if(CollectionUtils.isEmpty(clients)){
                    cardActionRequestService.writeRecord(request, "Сотрудник не найден", false);
                    return;
                }

                processBlockRequest(request, clients, null);
            }
        } catch (Exception e) {
            log.error(String.format("Error when process request %s", request.getId()), e);
            cardActionRequestService.writeRecord(request, "Ошибка при обработке запроса: " + e.getMessage(), false);
        }
    }
    private void processBlockRequest(BlockPersonEntranceRequest request, List<Client> clients, Client clientChild) throws Exception {
        CardActionRequest cardActionRequest = CardActionRequest.buildCardActionRequest(request);
        cardActionRequestService.writeRecord(cardActionRequest, "Старт блокировки карт", false);
        for(Client client : clients){
            processBlockRequestForClient(cardActionRequest, client, clientChild);
        }
        cardActionRequestService.writeRecord(cardActionRequest, "Карты клиента успешно заблокированы", true);
    }


    private void processBlockRequestForClient(CardActionRequest cardActionRequest, Client client,
                                     Client clientChild) throws Exception {
        List<Card> cards = cardService.getActiveCard(client, true);
        if (CollectionUtils.isEmpty(cards)) {
            cardActionClientService
                    .writeRecord(cardActionRequest, "Активных карт нет на момент блокировки", client);
            return;
        }

        for (Card c : cards) {
            Integer oldState = c.getState();
            cardService.blockCard(c);
            cardActionClientService.writeRecord(cardActionRequest, client, c, clientChild, "Карта клиента успешно заблокирована", oldState);
            cardSyncService.savechangeforCard(c, client.getIdoforg());
        }
    }
}
