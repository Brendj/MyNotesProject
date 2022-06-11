package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategory;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategoryItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 09.06.14
 * Time: 15:43
 */

public class ProhibitionMenu {
    private Long idOfProhibitions;
    private Client client;
    private String filterText;
    private ProhibitionFilterType prohibitionFilterType;
    private Date createDate;
    private Date updateDate;
    private Long version;
    private Boolean deletedState;
    private WtDish wtDish;
    private WtCategory wtCategory;
    private WtCategoryItem wtCategoryItem;

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

    public WtDish getWtDish() {
        return wtDish;
    }

    public void setWtDish(WtDish wtDish) {
        this.wtDish = wtDish;
    }

    public WtCategory getWtCategory() {
        return wtCategory;
    }

    public void setWtCategory(WtCategory wtCategory) {
        this.wtCategory = wtCategory;
    }

    public WtCategoryItem getWtCategoryItem() {
        return wtCategoryItem;
    }

    public void setWtCategoryItem(WtCategoryItem wtCategoryItem) {
        this.wtCategoryItem = wtCategoryItem;
    }
}
