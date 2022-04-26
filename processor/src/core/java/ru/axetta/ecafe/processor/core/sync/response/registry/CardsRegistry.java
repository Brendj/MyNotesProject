///*
// * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
// */
//
//package ru.axetta.ecafe.processor.core.sync.response.registry;
//
//
//import ru.axetta.ecafe.processor.core.persistence.Card;
//import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
//import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.CardsItem;
//
//import org.w3c.dom.DocumentItem;
//import org.w3c.dom.Element;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * 3.10.	Реестр карт
// *
// * User: shamil
// * Date: 30.04.15
// * Time: 10:23
// */
//public class CardsRegistry {
//    public static final String SYNC_NAME = "CardsRegistry";
//
//    private List<CardsItem> itemList;
//
//
//    public Element toElement(DocumentItem document) throws Exception {
//        Element element = document.createElement(SYNC_NAME);
//        for (CardsItem item : this.itemList) {
//            element.appendChild(item.toElement(document));
//        }
//        return element;
//    }
//
//    public void handlerFull(long idOfOrg){
//        CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();
//        build(cardReadOnlyRepository.findAllByOrg(idOfOrg));
//    }
//
//    public void handlerShort(long idOfOrg){
//
//    }
//
//    private void build(List<Card> cardList){
//        if(itemList == null){
//            itemList = new LinkedList<CardsItem>();
//        }
//        for (Card card : cardList) {
//            itemList.add(new CardsItem(card, null));
//        }
//    }
//
//
//
//    public List<CardsItem> getItemList() {
//        return itemList;
//    }
//
//    public void setItemList(List<CardsItem> itemList) {
//        this.itemList = itemList;
//    }
//}
