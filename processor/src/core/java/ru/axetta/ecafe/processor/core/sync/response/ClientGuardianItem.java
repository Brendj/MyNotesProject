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

    public ClientGuardianItem(ClientGuardian clientGuardian) {
        this.idOfGuardian = clientGuardian.getIdOfGuardian();
        this.idOfChildren = clientGuardian.getIdOfChildren();
        this.version = clientGuardian.getVersion();
        this.disabled = clientGuardian.isDisabled();
        this.deleteState = clientGuardian.getDeletedState() ? 1 : 0;
        this.result = null;
    }

    public Integer getDeleteState() {
        return deleteState;
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

    public ClientGuardianItem(ClientGuardian clientGuardian, Integer resCode, String resultMessage) {
        this.idOfGuardian = clientGuardian.getIdOfGuardian();
        this.idOfChildren = clientGuardian.getIdOfChildren();
        this.disabled = clientGuardian.isDisabled();
        this.version = clientGuardian.getVersion();
        this.result = new ResultOperation(resCode, resultMessage);
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfGuardian", idOfGuardian);
        XMLUtils.setAttributeIfNotNull(element, "IdOfChildren", idOfChildren);
        XMLUtils.setAttributeIfNotNull(element, "Disabled", disabled ? "1" : "0");
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deleteState);
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
        return new ClientGuardianItem(idOfGuardian, idOfChildren, disabled, delete);
    }

    private ClientGuardianItem(Long idOfGuardian, Long idOfChildren, Boolean disabled, Integer deleteSate) {
        this.idOfGuardian = idOfGuardian;
        this.idOfChildren = idOfChildren;
        this.disabled = disabled;
        this.deleteState = deleteSate;
    }


    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
