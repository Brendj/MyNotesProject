package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class TempCardsOperations {

    private final List<TempCardOperation> tempCardOperationList;

    public TempCardsOperations(List<TempCardOperation> tempCardOperationList) {
        this.tempCardOperationList = tempCardOperationList;
    }

    public List<TempCardOperation> getTempCardOperationList() {
        return tempCardOperationList;
    }
}
