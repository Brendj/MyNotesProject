package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged.FoodBoxPreorderChanged;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class FoodBoxPreorderBuilder implements SectionRequestBuilder {

    public FoodBoxPreorderBuilder(){
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, FoodBoxPreorderChanged.SECTION_NAME);
        if (sectionElement != null) {
            return new FoodBoxPreorderChanged(sectionElement);
        } else
            return null;
    }
}