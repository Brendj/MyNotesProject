/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 23.04.15
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CardService {
    @Autowired
    private CardWritableRepository cardWritableRepository;

    @Autowired
    private OrgRepository orgRepository;

    public static CardService getInstance(){
        return RuntimeContext.getAppContext().getBean(CardService.class);
    }

    public Card createCard(long idOfOrg, long cardNo, long cardPrintedNo, int type){
        Org org = orgRepository.findOne(idOfOrg);
        return cardWritableRepository.createCard(org, cardNo, cardPrintedNo, type);
    }
}
