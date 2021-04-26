package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.ProhibitionFilterType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nuc on 22.04.2021.
 */
@Entity
@Table(name = "cf_prohibitions")
public class ProhibitionMenu {
    @Id
    @Column(name = "idOfProhibitions")
    private Long idOfProhibitions;

    @ManyToOne
    @JoinColumn(name = "idofclient", insertable = false, updatable = false)
    private Client client;

    @Column(name = "filterText")
    private String filterText;

    @Column(name = "prohibitionFilterType")
    @Enumerated(EnumType.ORDINAL)
    private ProhibitionFilterType prohibitionFilterType;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "updateDate")
    private Date updateDate;

    @Column(name = "version")
    private Long version;

    @Column(name = "deletedState")
    private Boolean deletedState;

    public ProhibitionMenu(Long idOfProhibitions, Client client, String filterText,
                           ProhibitionFilterType prohibitionFilterType, Date createDate, Date updateDate, Boolean deletedState) {
        this.idOfProhibitions = idOfProhibitions;
        this.client = client;
        this.filterText = filterText;
        this.prohibitionFilterType = prohibitionFilterType;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.deletedState = deletedState;
    }

    public ProhibitionMenu() {
    }

    public Long getIdOfProhibitions() {
        return idOfProhibitions;
    }

    public void setIdOfProhibitions(Long idOfProhibitions) {
        this.idOfProhibitions = idOfProhibitions;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public ProhibitionFilterType getProhibitionFilterType() {
        return prohibitionFilterType;
    }

    public void setProhibitionFilterType(ProhibitionFilterType prohibitionFilterType) {
        this.prohibitionFilterType = prohibitionFilterType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
