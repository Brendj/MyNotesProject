package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxCells.FoodBoxCellsSync;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class FoodBoxCellsBuilder implements SectionRequestBuilder {

    public FoodBoxCellsBuilder(){
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, FoodBoxCellsSync.SECTION_NAME);
        if (sectionElement != null) {
            return new FoodBoxCellsSync(sectionElement);
        } else
            return null;
    }
}