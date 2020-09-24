package ru.iteco.nsisync.nsi.catalogs.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_ct_ContactType")
public class ContactType extends AbstractCatalog {
    @Column(name = "id")
    private Integer id;

    public ContactType(Long globalID, Long systemObjectId, String title, Integer isDelete, Integer id){
        this.globalID = globalID;
        this.systemObjectId = systemObjectId;
        this.title = title;
        this.isDelete = isDelete;
        this.id = id;
    }

    public ContactType(){
        // for Hibernate
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public enum ContactTypeEnumJsonFields implements JsonFieldDescriptor {
        ID("id", "Идентификатор");

        ContactTypeEnumJsonFields(String jsonFieldName, String description){
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