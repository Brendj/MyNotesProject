package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.12.13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardian {

    private Long idOfClientGuardian;
    private Long version;
    private Long idOfChildren;
    private Long idOfGuardian;
    private Integer guardianType;
    private Boolean disabled;

    protected ClientGuardian() {}

    public ClientGuardian(Long idOfChildren, Long idOfGuardian) {
        this.idOfChildren = idOfChildren;
        this.idOfGuardian = idOfGuardian;
    }

    public Long getIdOfClientGuardian() {
        return idOfClientGuardian;
    }

    public void setIdOfClientGuardian(Long idOfClientGuardian) {
        this.idOfClientGuardian = idOfClientGuardian;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdOfChildren() {
        return idOfChildren;
    }

    public void setIdOfChildren(Long idOfChildren) {
        this.idOfChildren = idOfChildren;
    }

    public Long getIdOfGuardian() {
        return idOfGuardian;
    }

    public void setIdOfGuardian(Long idOfGuardian) {
        this.idOfGuardian = idOfGuardian;
    }

    public Integer getGuardianType() {
        return guardianType;
    }

    public void setGuardianType(Integer guardianType) {
        this.guardianType = guardianType;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        if (disabled == null) {
            this.disabled = false;
        } else {
            this.disabled = disabled;
        }
    }
}
