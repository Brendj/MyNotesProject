/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.CardActionClient;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.models.Client;
import ru.iteco.cardsync.repo.ClientRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardProcessorService {

    private static final String OK = "Успешная обработка запроса";

    private final Logger log = LoggerFactory.getLogger(CardProcessorService.class);

    private final CardActionRequestService cardActionRequestService;
    private final CardService cardService;
    private final ClientRepository clientRepository;

    public CardProcessorService(CardActionRequestService cardActionRequestService, CardService cardService,
            ClientRepository clientRepository) {
        this.cardActionRequestService = cardActionRequestService;
        this.cardService = cardService;
        this.clientRepository = clientRepository;
    }

    public void processUnblockRequest(BlockPersonEntranceRequest request) {
        try {
            if (request == null) {
                return;
            }

            CardActionRequest blockRequests = cardActionRequestService.findRequestBlockByRequestId(request.getId());
            if (blockRequests == null) {
                cardActionRequestService.writeRecord(request, "В БД нет запроса на блокировку", false);
                return;
            }

            //Для всех карт, заблокированных по одному id
            for (CardActionClient cardActionClient: blockRequests.getCardActionClients()) {
                Client client = cardActionClient.getClient();
                if (client == null) {
                    cardActionRequestService.writeRecord(request, "Не найден клиент для разблокировки карт", false);
                    continue;
                }
                List<Card> cardsActive = cardService.getActiveCard(client, false);
                if (!cardsActive.isEmpty())
                {
                    cardActionRequestService.writeRecord(request, "Есть активные карты на момент разблокировки", false, client);
                    continue;
                }
                List<Card> cards = cardService.getBlockedCard(client);
                if (CollectionUtils.isEmpty(cards)) {
                    cardActionRequestService.writeRecord(request, "Не найдено карт для разблокировки", false, client);
                    continue;
                }
                cardService.unblockCard(cardActionClient.getCard());
            }
            cardActionRequestService.writeRecord(request, OK, true, blockRequests);
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
                        cardActionRequestService.writeRecord(request, "Представители не найдены", false, client);
                        return;
                    }
                    for(Client guardian : client.getGuardians()){
                        processBlockRequestReal(guardian, request, client);
                    }
                } else { // Обработка клиента на прямую
                    processBlockRequest(client, request);
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

                for(Client client : clients){
                    processBlockRequest(client, request);
                }
            }
        } catch (Exception e) {
            log.error(String.format("Error when process request %s", request.getId()), e);
            cardActionRequestService.writeRecord(request, "Ошибка при обработке запроса: " + e.getMessage(), false);
        }
    }

    private void processBlockRequest(Client client, BlockPersonEntranceRequest request) throws Exception {
        processBlockRequestReal(client, request, null);
    }

    private void processBlockRequestReal(Client client, BlockPersonEntranceRequest request, Client clientChild) throws Exception {
        List<Card> cards = cardService.getActiveCard(client, true);
        if (CollectionUtils.isEmpty(cards)) {
            cardActionRequestService
                    .writeRecord(request, "Активных карт нет на момент блокировки", false, client);
            return;
        }
        CardActionRequest requestend = CardActionRequest.buildCardActionRequest(request);
        cardActionRequestService.writeRecord(requestend, "Старт блокировки карт", false);
        requestend.setProcessed(false);
        for (Card c : cards) {
            cardService.blockCard(c);
            cardActionRequestService.writeRecord(requestend, client, c, clientChild, "Карта клиента успешно заблокирована");
        }
        cardActionRequestService.writeRecord(requestend, "Карты клиента успешно заблокированы", true);
    }
}
