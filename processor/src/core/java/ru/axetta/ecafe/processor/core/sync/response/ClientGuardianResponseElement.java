package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.criterion.Example;
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
public class ClientGuardianResponseElement {

    private Long idOfGuardian;
    private Long idOfChildren;
    private Long version;
    private Integer deleteState;
    private ResultOperation result;

    public ClientGuardianResponseElement(ClientGuardian clientGuardian) {
        this.idOfGuardian = clientGuardian.getIdOfGuardian();
        this.idOfChildren = clientGuardian.getIdOfChildren();
        this.version = clientGuardian.getVersion();
        this.result = null;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setResult(ResultOperation result) {
        this.result = result;
    }

    /* idOfClientGuardian == null */
    public ClientGuardian createNewClientGuardian(){
        return new ClientGuardian(idOfChildren, idOfGuardian);
    }

    public Example createExampleRestriction(){
        return Example.create(new ClientGuardian(idOfChildren, idOfGuardian));
    }

    public ClientGuardianResponseElement(ClientGuardian clientGuardian, Integer resCode, String resultMessage) {
        this.idOfGuardian = clientGuardian.getIdOfGuardian();
        this.idOfChildren = clientGuardian.getIdOfChildren();
        this.version = clientGuardian.getVersion();
        this.result = new ResultOperation(resCode, resultMessage);
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfGuardian", idOfGuardian);
        XMLUtils.setAttributeIfNotNull(element, "IdOfChildren", idOfChildren);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deleteState);
        if(this.result!=null){
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        return element;
    }

    public static ClientGuardianResponseElement build(Node itemNode){
        Long idOfGuardian = XMLUtils.getLongAttributeValue(itemNode, "IdOfGuardian");
        Long idOfChildren = XMLUtils.getLongAttributeValue(itemNode, "IdOfChildren");
        Integer delete = XMLUtils.getIntegerValueZeroSafe(itemNode, "D");
        return new ClientGuardianResponseElement(idOfGuardian, idOfChildren, delete);
    }

    private ClientGuardianResponseElement(Long idOfGuardian, Long idOfChildren, Integer deleteSate) {
        this.idOfGuardian = idOfGuardian;
        this.idOfChildren = idOfChildren;
        this.deleteState = deleteSate;
    }


}
