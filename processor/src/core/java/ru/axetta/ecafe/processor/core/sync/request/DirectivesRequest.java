/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 30.06.2016
 */
public class DirectivesRequest implements SectionRequest {

    public static final String SECTION_NAME = "DirectivesRequest";
    private final Boolean tradeConfigChangedSuccess;
    private final Integer isWorkInSummerTime;
    private final Integer recyclingEnabled;

    public DirectivesRequest(Node sectionElement) throws Exception {
        this.tradeConfigChangedSuccess =
                XMLUtils.findFirstChildElement(sectionElement, "TRADE_ACCOUNT_CONFIG_CHANGED_SUCCESS") != null;
        Node isWorkInSummerTimeNode = XMLUtils.findFirstChildElement(sectionElement, "IS_WORK_IN_SUMMER_TIME");
        if (isWorkInSummerTimeNode != null) {
            NamedNodeMap attributes = isWorkInSummerTimeNode.getAttributes();
            String value = attributes.getNamedItem("value").getTextContent();
            this.isWorkInSummerTime = Integer.parseInt(value);
        } else
            this.isWorkInSummerTime = null;

        Node recyclingEnabled = XMLUtils.findFirstChildElement(sectionElement, "IS_RECYCLING_AND_CHANGE_MODE_ENABLED");
        if (recyclingEnabled != null) {
            NamedNodeMap attributes = recyclingEnabled.getAttributes();
            String value = attributes.getNamedItem("value").getTextContent();
            this.recyclingEnabled = Integer.parseInt(value);
        } else
            this.recyclingEnabled = null;
    }

    /*public DirectivesRequest(Node directivesRequestNode, Integer isWorkInSummerTime) throws Exception {
        this.tradeConfigChangedSuccess =
                XMLUtils.findFirstChildElement(directivesRequestNode, "TRADE_ACCOUNT_CONFIG_CHANGED_SUCCESS") != null;
        this.isWorkInSummerTime = isWorkInSummerTime;
    }*/


    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    @Override
    public String toString() {
        return SECTION_NAME;
    }

    public static class Builder implements SectionRequestBuilder {

        public DirectivesRequest build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (DirectivesRequest) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
            if (sectionElement != null) {
                return new DirectivesRequest(sectionElement);
            } else
                return null;
        }
    }

    public Boolean getTradeConfigChangedSuccess() {
        return tradeConfigChangedSuccess;
    }

    public Integer getIsWorkInSummerTime() {
        return isWorkInSummerTime;
    }

    public Integer getRecyclingEnabled() {
        return recyclingEnabled;
    }
}
