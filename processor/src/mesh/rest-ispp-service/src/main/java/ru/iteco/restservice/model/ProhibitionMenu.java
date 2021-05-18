package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.ProhibitionFilterType;
import ru.iteco.restservice.model.wt.WtCategory;
import ru.iteco.restservice.model.wt.WtCategoryItem;
import ru.iteco.restservice.model.wt.WtDish;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Created by nuc on 22.04.2021.
 */
@Entity
@Table(name = "cf_prohibitions")
public class ProhibitionMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idofprohibitions")
    private Long idOfProhibitions;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "filterText")
    private String filterText;

    @Column(name = "prohibitionFilterType")
    @Enumerated(EnumType.ORDINAL)
    private ProhibitionFilterType prohibitionFilterType;

    @Column(name = "createDate")
    @Type(type = "ru.iteco.restservice.model.type.DateType")
    private Date createDate;

    @Column(name = "updateDate")
    @Type(type = "ru.iteco.restservice.model.type.DateType")
    private Date updateDate;

    @Column(name = "version")
    private Long version;

    @Column(name = "deletedState")
    private Boolean deletedState;

    @ManyToOne
    @JoinColumn(name = "idofdish")
    private WtDish dish;

    @ManyToOne
    @JoinColumn(name = "idofcategory")
    private WtCategory category;

    @ManyToOne
    @JoinColumn(name = "idofcategoryitem")
    private WtCategoryItem categoryItem;

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

    public ProhibitionMenu(Client client, WtDish dish, WtCategory category, WtCategoryItem categoryItem) {
        this.client = client;
        this.dish = dish;
        this.category = category;
        this.categoryItem = categoryItem;
        this.createDate = new Date();
        this.updateDate = new Date();
        this.deletedState = false;
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

    public WtDish getDish() {
        return dish;
    }

    public void setDish(WtDish dish) {
        this.dish = dish;
    }

    public WtCategory getCategory() {
        return category;
    }

    public void setCategory(WtCategory category) {
        this.category = category;
    }

    public WtCategoryItem getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(WtCategoryItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProhibitionMenu that = (ProhibitionMenu) o;
        return Objects.equals(idOfProhibitions, that.idOfProhibitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfProhibitions);
    }
}
