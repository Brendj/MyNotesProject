package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class TempCardsOperations implements SectionRequest{
    public static final String SECTION_NAME="TempCardsOperations";

    private final List<TempCardOperation> tempCardOperationList;

    public TempCardsOperations(List<TempCardOperation> tempCardOperationList) {
        this.tempCardOperationList = tempCardOperationList;
    }

    public List<TempCardOperation> getTempCardOperationList() {
        return tempCardOperationList;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
