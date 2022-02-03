package ru.iteco.cardsync.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.service.CardActionRequestService;
import ru.iteco.cardsync.service.CardProcessorService;

import java.util.List;

public class RunnableBlockCardThreadWrapper implements Runnable {
    
    private static final Logger log = LoggerFactory.getLogger(RunnableBlockCardThreadWrapper.class);
    
    private CardProcessorService cardProcessorService;
    private CardActionRequestService cardActionRequestService ;
    
    protected BlockPersonEntranceRequest request;
    protected Long offset;
    protected Integer partitionId;
    protected String message;

    public RunnableBlockCardThreadWrapper(BlockPersonEntranceRequest request, Long offset, Integer partitionId, String message, CardProcessorService cardProcessorService, CardActionRequestService cardActionRequestService) {
        this.request = request;
        this.offset = offset;
        this.partitionId = partitionId;
        this.message = message;
        this.cardProcessorService = cardProcessorService;
        this.cardActionRequestService = cardActionRequestService;
    }

    @Override
    public void run() {
        try {
            blockCard(request, offset, partitionId, message);
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
        }
    }

    private void blockCard(BlockPersonEntranceRequest request, Long offset, Integer partitionId, String message)
    {
        //Проверка на дубли
        //Только 1 раз блокируется и 1 раз разблокируется
        boolean good = true;
        if (request.getAction() == ActionType.block)
        {
            if (!cardActionRequestService.findRequestBlockByRequestIdFull(request.getId()).isEmpty())
            {
                good = false;
                log.info(String.format("ДУБЛЬ block Offset %d, Partition_ID %d, Received JSON: %s", offset, partitionId, message));
            }
        }
        else
        {
            List<CardActionRequest> unblock = cardActionRequestService.findRequestUnblockByRequestIdFull(request.getId());
            //Если для данного запроса уже есть успешная операция разблокирования
            if (!unblock.isEmpty())
            {
                if (unblock.get(0).getProcessed()){
                    good = false;
                    log.info(String.format("ДУБЛЬ unblock Offset %d, Partition_ID %d, Received JSON: %s", offset, partitionId, message));
                }
            }
        }
        if (good)
        {
            log.info(String.format("Offset %d, Partition_ID %d, Received JSON: %s", offset, partitionId, message));
            commitJson(request);
        }
    }

    private void commitJson(BlockPersonEntranceRequest request) {
        switch (request.getAction()) {
            case block:
                cardProcessorService.processBlockRequest(request);
                break;
            case unblock:
                cardProcessorService.processUnblockRequest(request);
        }
    }
}