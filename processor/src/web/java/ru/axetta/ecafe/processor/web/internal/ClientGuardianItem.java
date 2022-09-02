package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;

public class ClientGuardianItem {
    private Long idOfGuardian;
    private Long idOfChildren;
    private Long version;
    private boolean disabled;
    private Integer relation;
    private boolean consentToPreOrder;
    private Integer isLegalRepresent;
    private boolean allowedPreorder;

    public ClientGuardianItem() {}

    public ClientGuardianItem(ClientGuardian clientGuardian) {
        this.idOfGuardian = clientGuardian.getIdOfGuardian();
        this.idOfChildren = clientGuardian.getIdOfChildren();
        this.version = clientGuardian.getVersion();
        this.disabled = clientGuardian.isDisabled();
        this.relation = clientGuardian.getRelation() == null ? null : clientGuardian.getRelation().getCode();
        this.consentToPreOrder = ClientManager.getInformedSpecialMenuWithoutSession(clientGuardian.getIdOfChildren(),
                clientGuardian.getIdOfGuardian());
        this.isLegalRepresent = clientGuardian.getRepresentType().getCode();
        this.allowedPreorder = ClientManager.getAllowedPreorderByClientWithoutSession(clientGuardian.getIdOfChildren(),
                clientGuardian.getIdOfGuardian());
    }

    public Long getIdOfGuardian() {
        return idOfGuardian;
    }

    public void setIdOfGuardian(Long idOfGuardian) {
        this.idOfGuardian = idOfGuardian;
    }

    public Long getIdOfChildren() {
        return idOfChildren;
    }

    public void setIdOfChildren(Long idOfChildren) {
        this.idOfChildren = idOfChildren;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    public boolean isConsentToPreOrder() {
        return consentToPreOrder;
    }

    public void setConsentToPreOrder(boolean consentToPreOrder) {
        this.consentToPreOrder = consentToPreOrder;
    }

    public Integer getIsLegalRepresent() {
        return isLegalRepresent;
    }

    public void setIsLegalRepresent(Integer isLegalRepresent) {
        this.isLegalRepresent = isLegalRepresent;
    }

    public boolean isAllowedPreorder() {
        return allowedPreorder;
    }

    public void setAllowedPreorder(boolean allowedPreorder) {
        this.allowedPreorder = allowedPreorder;
    }
}
