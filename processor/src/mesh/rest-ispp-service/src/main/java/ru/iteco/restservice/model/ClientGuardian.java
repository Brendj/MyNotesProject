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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientGuardianGen")
    @SequenceGenerator(name = "clientGuardianGen", sequenceName = "cf_client_guardian_id_gen_seq")
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

    public ClientGuardian(Long idOfClientGuardian, Client children, Client guardian,
            ClientGuardianRelationType relationType, ClientGuardianRepresentType representType, Integer disabled,
            Boolean deletedState, Long version) {
        this.idOfClientGuardian = idOfClientGuardian;
        this.children = children;
        this.guardian = guardian;
        this.relationType = relationType;
        this.representType = representType;
        this.disabled = disabled;
        this.deletedState = deletedState;
        this.version = version;
    }

    public ClientGuardian(){
    }

    public ClientGuardian(Client guardian, Client child, ClientGuardianRepresentType representType,
            ClientGuardianRelationType relationType, Long version) {
        this.children = child;
        this.guardian = guardian;
        this.relationType = relationType;
        this.representType = representType;
        this.version = version;
        this.disabled = 0;
        this.deletedState = false;
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
