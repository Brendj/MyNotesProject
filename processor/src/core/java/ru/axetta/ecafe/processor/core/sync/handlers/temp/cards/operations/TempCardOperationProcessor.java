package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class TempCardOperationProcessor extends AbstractProcessor<ResTempCardsOperations> {

    private final TempCardsOperations tempCardsOperations;
    private final List<ResTempCardOperation> resTempCardOperationList;

    public TempCardOperationProcessor(Session persistenceSession, TempCardsOperations tempCardsOperations) {
        super(persistenceSession);
        this.tempCardsOperations = tempCardsOperations;
        resTempCardOperationList = new ArrayList<ResTempCardOperation>();
    }

    @Override
    public ResTempCardsOperations process() throws Exception {
        List<TempCardOperation> tempCardOperations = tempCardsOperations.getTempCardOperationList();
        for (TempCardOperation tempCardOperation: tempCardOperations){
            if (StringUtils.isEmpty(tempCardOperation.getErrorMessage())){
                Card card = DAOUtils.findCardByCardNo(session, tempCardOperation.getIdOfTempCard());
                if(card!=null){
                    addResTempCardOperation(tempCardOperation, 1, "Карта уже зарегистрирована как постоянная");
                } else {
                    CardTemp cardTemp = DAOUtils.findCardTempByCardNo(session, tempCardOperation.getIdOfTempCard());
                    if(cardTemp==null) {
                        addResTempCardOperation(tempCardOperation, 3, "Карта не зарегистрирована как временная");
                    } else {
                        if(cardTemp.getOrg().getIdOfOrg().equals(tempCardOperation.getIdOfOrg())){
                            if(tempCardOperation.getIdOfClient()!=null){
                                resTempCardOperationList.add(registrClientOperation(tempCardOperation, cardTemp));
                            }
                            if(tempCardOperation.getIdOfVisitor()!=null){
                                resTempCardOperationList.add(registrVisitorOperation(tempCardOperation, cardTemp));
                            }
                        } else {
                            addResTempCardOperation(tempCardOperation, 4,"Карта зарегистрирована на другую организацию");
                        }
                    }
                }
            } else {
                addResTempCardOperation(tempCardOperation, 8, tempCardOperation.getErrorMessage());
            }

        }
        return new ResTempCardsOperations(resTempCardOperationList);
    }

    private void addResTempCardOperation(TempCardOperation tempCardOperation, int code, String message) {
        ResTempCardOperation resTempCardOperation = new ResTempCardOperation(tempCardOperation.getIdOfOperation(),code,message);
        resTempCardOperationList.add(resTempCardOperation);
    }

    private ResTempCardOperation registrVisitorOperation(TempCardOperation tempCardOperation, CardTemp cardTemp) {
        Visitor visitor = DAOUtils.existVisitor(session, tempCardOperation.getIdOfVisitor());
        if(visitor==null){
            final String message = String.format("%d не связан с организацией idOfOrg = %d",tempCardOperation.getIdOfClient(), tempCardOperation.getIdOfOrg());
            return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),5,message);
        } else {
            if(tempCardOperation.getIssueExpiryDate()!=null && System.currentTimeMillis()>tempCardOperation.getIssueExpiryDate().getTime()){
                return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),6,"Неверное значение даты окончания действия карты");
            } else {
                CardTempOperation cardTempOperation = DAOUtils.findTempCartOperation(session, tempCardOperation.getIdOfOperation(), cardTemp.getOrg());
                if(cardTempOperation==null){
                    cardTempOperation = new CardTempOperation(tempCardOperation.getIdOfOperation(), cardTemp.getOrg(), cardTemp, CardOperationStation
                            .values()[tempCardOperation.getOperationType()], tempCardOperation.getOperationDate(),visitor);
                    cardTemp.setVisitor(visitor);
                    cardTemp.setValidDate(tempCardOperation.getIssueExpiryDate());
                    session.save(cardTemp);
                    session.save(cardTempOperation);
                    return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),0,null);
                }  else {
                    return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),7,"Операция уже зарегистрирована");
                }
            }
        }
    }

    private ResTempCardOperation registrClientOperation(TempCardOperation tempCardOperation, CardTemp cardTemp) {
        Client clientIsOrg = DAOUtils
                .checkClientBindOrg(session, tempCardOperation.getIdOfClient(), tempCardOperation.getIdOfOrg());
        if(clientIsOrg!=null){
            if(tempCardOperation.getIssueExpiryDate()!=null && System.currentTimeMillis()>tempCardOperation.getIssueExpiryDate().getTime()){
                return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),6,"Неверное значение даты окончания действия карты");
            } else {
                CardOperationStation operation = CardOperationStation.value(tempCardOperation.getOperationType());
                CardTempOperation cardTempOperation = DAOUtils.findTempCartOperation(session, tempCardOperation.getIdOfOperation(), cardTemp.getOrg());
                if(cardTempOperation==null){
                    cardTempOperation = new CardTempOperation(tempCardOperation.getIdOfOperation(), cardTemp.getOrg(), cardTemp,operation, tempCardOperation.getOperationDate(),clientIsOrg);
                    cardTemp.setClient(clientIsOrg);
                    cardTemp.setValidDate(tempCardOperation.getIssueExpiryDate());
                    session.save(cardTemp);
                    session.save(cardTempOperation);
                    return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),0,null);
                }  else {
                    return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),7,"Операция уже зарегистрирована");
                }
            }
        } else {
            final String message = String.format("%d не связан с организацией idOfOrg = %d",tempCardOperation.getIdOfClient(), tempCardOperation.getIdOfOrg());
            return new ResTempCardOperation(tempCardOperation.getIdOfOperation(),5,message);
        }
    }

}
