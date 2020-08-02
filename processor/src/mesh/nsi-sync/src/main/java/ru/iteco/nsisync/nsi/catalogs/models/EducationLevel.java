package ru.iteco.nsisync.nsi.catalogs.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_ct_educationlevel")
public class EducationLevel extends AbstractCatalog {
    @Column(name = "id")
    private Integer id;

    @Column(name = "shortname", length = 36)
    private String shortName;

    public EducationLevel(Long globalID, Long systemObjectId, String title, Integer isDelete, Integer id, String shortName) {
        this.systemObjectId = systemObjectId;
        this.globalID = globalID;
        this.title = title;
        this.isDelete = isDelete;
        this.id = id;
        this.shortName = shortName;
    }

    public EducationLevel(){
        //for Hibernate
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public enum EducationLevelJsonFields implements JsonFieldDescriptor {
        ID("id", "Идентификатор"),
        SHORT_NAME("short_name", "Краткое наименование");

        EducationLevelJsonFields(String jsonFieldName, String description){
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
