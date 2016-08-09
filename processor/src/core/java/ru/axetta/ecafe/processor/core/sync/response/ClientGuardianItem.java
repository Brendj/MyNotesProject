package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianItem {

    private Long idOfGuardian;
    private Long idOfChildren;
    private Long version;
    private Integer deleteState;
    private ResultOperation result;
    private Boolean disabled;
    private Integer relation;

    public ClientGuardianItem(ClientGuardian clientGuardian) {
        this.idOfGuardian = clientGuardian.getIdOfGuardian();
        this.idOfChildren = clientGuardian.getIdOfChildren();
        this.version = clientGuardian.getVersion();
        this.disabled = clientGuardian.isDisabled();
        this.deleteState = clientGuardian.getDeletedState() ? 1 : 0;
        this.relation = clientGuardian.getRelation() == null ? null : clientGuardian.getRelation().ordinal();
        this.result = null;
    }

    public ClientGuardianItem(ClientGuardian clientGuardian, Integer resCode, String resultMessage) {
        this(clientGuardian);
        this.result = new ResultOperation(resCode, resultMessage);
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public Boolean isDeleted() {
        return deleteState != null && deleteState > 0;
    }

    public void setResult(ResultOperation result) {
        this.result = result;
    }

    public Long getIdOfGuardian() {
        return idOfGuardian;
    }

    public Long getIdOfChildren() {
        return idOfChildren;
    }


    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfGuardian", idOfGuardian);
        XMLUtils.setAttributeIfNotNull(element, "IdOfChildren", idOfChildren);
        XMLUtils.setAttributeIfNotNull(element, "Disabled", disabled ? "1" : "0");
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deleteState);
        XMLUtils.setAttributeIfNotNull(element, "Relation", relation);
        if(this.result!=null){
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        return element;
    }

    public static ClientGuardianItem build(Node itemNode){
        Long idOfGuardian = XMLUtils.getLongAttributeValue(itemNode, "IdOfGuardian");
        Long idOfChildren = XMLUtils.getLongAttributeValue(itemNode, "IdOfChildren");
        Boolean disabled = (1 == XMLUtils.getIntegerValueZeroSafe(itemNode, "Disabled"));
        Integer delete = XMLUtils.getIntegerValueZeroSafe(itemNode, "D");
        Integer relation = XMLUtils.getIntegerAttributeValue(itemNode, "Relation");
        return new ClientGuardianItem(idOfGuardian, idOfChildren, disabled, delete, relation);
    }

    private ClientGuardianItem(Long idOfGuardian, Long idOfChildren, Boolean disabled, Integer deleteSate, Integer relation) {
        this.idOfGuardian = idOfGuardian;
        this.idOfChildren = idOfChildren;
        this.disabled = disabled;
        this.deleteState = deleteSate;
        this.relation = relation;
    }


    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }
}
