//package ru.axetta.ecafe.processor.core.service;
//
//import ru.axetta.ecafe.processor.core.RuntimeContext;
//import ru.axetta.ecafe.processor.core.persistence.Card;
//import ru.axetta.ecafe.processor.core.persistence.Client;
//import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * User: shamil
// * Date: 27.04.15
// * Time: 17:35
// */
//@Component
//public class CardHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(CardHandler.class);
//
//    @Autowired
//    private CardService cardService;
//
//
//    public static CardHandler getInstance(){
//        return RuntimeContext.getAppContext().getBean(CardHandler.class);
//    }
//
//    //1. Регистрация карты
//    public Card registerNew(long idOfOrg, long cardNo, long cardPrintedNo, int type){
//        return cardService.registerNew(idOfOrg,cardNo,cardPrintedNo,type);
//    }
//
//    //1. Регистрация карты
//    public void registerNew(){
//    }
//
//    //2. Выдача карты клиенту
//    public void issueToClient(Card card, Client client){
//        cardService.issueToClient(card,client);
//    }
//
//    //3. Выдача новой активной карты клиенту взамен старой
//    public void issueToClientInsteadOld(Client client, Card newCard){
//        for(Card card : client.getCards()){
//            cardService.blockAndReset(card);
//            //cardService.unblock(card);
//        }
//
//        cardService.issueToClient(newCard, client);
//    }
//
//    //4. Выдача новой временно-активной карты клиенту с блокировкой основной карты.
//    public void issueToClientWithBlockingPrev(Client client, Card newCard){
//        for(Card card : client.getCards()){
//            cardService.block(card);
//        }
//        cardService.issueToClientTemp(newCard, client);
//
//
//    }
//    //5. Выдача новой временно-активной карты без блокировки основной карты
//    public void issueToClientNoBlockingPrev(Client client, Card newCard){
//        cardService.issueToClientTemp(newCard, client);
//    }
//    //6. Возврат (сброс, аннулирование) активной  карты
//    public void returnOfActiveCard(){
//
//    }
//    //7. Возврат (сброс, аннулирование) временно-активной карты с одновременной  активацией предыдущей  карты клиента
//    public void returnOfTempCardWithActivatingPrev(){
//
//    }
//    //8. Возврат (сброс, аннулирование) временно-активной карты
//    public void returnOfTempCard(){
//
//    }
//    //9. Выдача карты гостю
//    public void issueToGuest(){
//
//    }
//    //10. Возврат карты гостем
//    public void returnByGuest(){
//
//    }
//    //11.  Блокировка карты
//    public void block(){
//
//    }
//
//}
