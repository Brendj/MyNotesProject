package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxDishRemain.FoodBoxDishRemain;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class FoodBoxDishRemainBuilder implements SectionRequestBuilder {

    public FoodBoxDishRemainBuilder(){
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, FoodBoxDishRemain.SECTION_NAME);
        if (sectionElement != null) {
            return new FoodBoxDishRemain(sectionElement);
        } else
            return null;
    }
}