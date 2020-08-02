package ru.iteco.nsisync.nsi.catalogs.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_training_form")
public class TrainingForm extends AbstractCatalog {
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", length = 36)
    private String code;

    @Column(name = "education_form")
    private String educationForm;

    @Column(name = "archive", nullable = false)
    private Boolean archive;

    public TrainingForm(){

    }

    public TrainingForm(Long globalID, Long systemObjectId, String title, Integer id, Boolean archive, String code, String educationForm, Integer state) {
        this.globalID = globalID;
        this.systemObjectId = systemObjectId;
        this.title = title;
        this.id = id;
        this.archive = archive;
        this.code = code;
        this.educationForm = educationForm;
        this.isDelete = state;
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

    public String getEducationForm() {
        return educationForm;
    }

    public void setEducationForm(String educationForm) {
        this.educationForm = educationForm;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public enum TrainingFormJsonFields implements JsonFieldDescriptor {
        ID("id", "Идентификатор"),
        EDUCATION_FORM("education_form", "Форма получения образования"),
        ARCHIVE("archive", "Признак архивности"),
        CODE("code", "Код");

        TrainingFormJsonFields(String jsonFieldName, String description){
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
