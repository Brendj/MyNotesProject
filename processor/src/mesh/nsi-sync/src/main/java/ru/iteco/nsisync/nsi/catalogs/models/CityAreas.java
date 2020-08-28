package ru.iteco.nsisync.nsi.catalogs.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_ct_Cityareas")
public class CityAreas extends AbstractCatalog {
    @Column(name = "id")
    private Integer id;

    @Column(name = "parent_id", length = 9)
    private String parentId;

    @Column(name = "bti_id", length = 9)
    private String btiId;

    @Column(name = "bti_title")
    private String btiTitle;

    public CityAreas(Long globalID, Long systemObjectId, String title, Integer isDelete, Integer id, String parentId,
                     String btiId, String btiTitle) {
        this.systemObjectId = systemObjectId;
        this.globalID = globalID;
        this.title = title;
        this.isDelete = isDelete;
        this.id = id;
        this.parentId = parentId;
        this.btiId = btiId;
        this.btiTitle = btiTitle;
    }

    public CityAreas(){
        //for Hibernate
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getBtiId() {
        return btiId;
    }

    public void setBtiId(String btiId) {
        this.btiId = btiId;
    }

    public String getBtiTitle() {
        return btiTitle;
    }

    public void setBtiTitle(String btiTitle) {
        this.btiTitle = btiTitle;
    }

    public enum CityAreasJsonFields implements JsonFieldDescriptor {
        ID("id", "Идентификатор"),
        PARENT_ID("parent_id", "Родительский идентификатор"),
        BTI_ID("bti_id", "Идентификатор БТИ"),
        BTI_TITLE("bti_title", "Наименование БТИ");

        CityAreasJsonFields(String jsonFieldName, String description){
            this.jsonFieldName = jsonFieldName;
            this.description = description;
        }

        private String jsonFieldName;
        private String description;

        @Override
        public String getJsonFieldName() {
            return jsonFieldName;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
