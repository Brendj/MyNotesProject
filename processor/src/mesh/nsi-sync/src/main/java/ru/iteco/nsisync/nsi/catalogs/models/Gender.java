package ru.iteco.nsisync.nsi.catalogs.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_ct_Gender")
public class Gender extends AbstractCatalog {
    @Column(name = "id")
    private Integer id;

    @Column(name = "code")
    private String code;

    public Gender(Long globalID, Long systemObjectId, String title, Integer isDelete, Integer id, String code){
        this.globalID = globalID;
        this.systemObjectId = systemObjectId;
        this.title = title;
        this.isDelete = isDelete;
        this.id = id;
        this.code = code;
    }

    public Gender(){
        //for Hibernate
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public enum GenderJsonFields implements JsonFieldDescriptor {
        ID("id", "Идентификатор"),
        CODE("code", "Код");

        GenderJsonFields(String jsonFieldName, String description){
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
