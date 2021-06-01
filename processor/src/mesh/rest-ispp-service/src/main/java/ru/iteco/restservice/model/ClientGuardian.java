/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.ClientGuardianRelationType;
import ru.iteco.restservice.model.enums.ClientGuardianRepresentType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_client_guardian")
public class ClientGuardian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idofclientguardian")
    private Long idOfClientGuardian;

    @ManyToOne
    @JoinColumn(name = "idofchildren")
    private Client children;

    @ManyToOne
    @JoinColumn(name = "idofguardian")
    private Client guardian;

    @Column(name = "relation")
    @Enumerated(EnumType.ORDINAL)
    private ClientGuardianRelationType relationType;

    @Column(name = "islegalrepresent")
    private ClientGuardianRepresentType representType;

    @Column(name = "disabled")
    private Integer disabled;

    @Column(name = "deletedstate")
    private Boolean deletedState;

    @Column(name = "version")
    private Long version;

    public ClientGuardian(){

    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdOfClientGuardian() {
        return idOfClientGuardian;
    }

    public void setIdOfClientGuardian(Long idOfClientGuardian) {
        this.idOfClientGuardian = idOfClientGuardian;
    }

    public Client getChildren() {
        return children;
    }

    public void setChildren(Client children) {
        this.children = children;
    }

    public Client getGuardian() {
        return guardian;
    }

    public void setGuardian(Client guardian) {
        this.guardian = guardian;
    }

    public ClientGuardianRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(ClientGuardianRelationType relationType) {
        this.relationType = relationType;
    }

    public ClientGuardianRepresentType getRepresentType() {
        return representType;
    }

    public void setRepresentType(ClientGuardianRepresentType representType) {
        this.representType = representType;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientGuardian that = (ClientGuardian) o;
        return Objects.equals(idOfClientGuardian, that.idOfClientGuardian);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClientGuardian);
    }
}
