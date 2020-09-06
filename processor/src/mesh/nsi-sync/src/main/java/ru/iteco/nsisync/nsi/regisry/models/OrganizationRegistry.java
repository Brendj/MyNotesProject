package ru.iteco.nsisync.nsi.regisry.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_OrganizationRegistry")
public class OrganizationRegistry extends AbstractRegistry {
    @Column(name = "eo_id")
    private Long eoId;

    @Column(name = "full_name", length = 512)
    private String fullName;

    @Column(name = "short_name", length = 512)
    private String shortName;

    @Column(name = "type2_id")
    private Integer type2Id;

    @Column(name = "inn")
    private Long inn;

    @Column(name = "ogrn", length = 64)
    private String ogrn;

    @Column(name = "subordination_value", length = 512)
    private String subordinationValue;

    @Column(name = "director", length = 512)
    private String director;

    @Column(name = "arhiv")
    private Boolean arhiv;

    @Column(name = "egisso_id", length = 128)
    private String egissoId;

    @Column(name = "xaisactive")
    private Integer xaIsActive;

    @Column(name = "founder", length = 512)
    private String founder;

    public OrganizationRegistry(Long globalId, String system_object_id, Long eoId, String fullName, String shortName,
                                Integer type2Id, Long inn, String ogrn, String subordinationValue, String director,
                                Boolean arhiv, String egissoId, Integer xaIsActive, String founder) {
        this.globalId = globalId;
        this.systemObjectId = system_object_id;
        this.eoId = eoId;
        this.fullName = fullName;
        this.shortName = shortName;
        this.type2Id = type2Id;
        this.inn = inn;
        this.ogrn = ogrn;
        this.subordinationValue = subordinationValue;
        this.director = director;
        this.arhiv = arhiv;
        this.egissoId = egissoId;
        this.xaIsActive = xaIsActive;
        this.founder = founder;
    }

    public OrganizationRegistry() {
    }

    public Long getEoId() {
        return eoId;
    }

    public void setEoId(Long eoId) {
        this.eoId = eoId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getType2Id() {
        return type2Id;
    }

    public void setType2Id(Integer type2Id) {
        this.type2Id = type2Id;
    }

    public Long getInn() {
        return inn;
    }

    public void setInn(Long inn) {
        this.inn = inn;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getSubordinationValue() {
        return subordinationValue;
    }

    public void setSubordinationValue(String subordinationValue) {
        this.subordinationValue = subordinationValue;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Boolean getArhiv() {
        return arhiv;
    }

    public void setArhiv(Boolean arhiv) {
        this.arhiv = arhiv;
    }

    public String getEgissoId() {
        return egissoId;
    }

    public void setEgissoId(String egissoId) {
        this.egissoId = egissoId;
    }

    public Integer getXaIsActive() {
        return xaIsActive;
    }

    public void setXaIsActive(Integer xaIsActive) {
        this.xaIsActive = xaIsActive;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public enum OrganizationRegistryEnumJsonFields implements JsonFieldDescriptor {
        EO_ID("eo_id", "ЕКИС ID"),
        FULL_NAME("full_name", "Наименование образовательного учреждения"),
        SHORT_NAME("short_name", "Краткое наименование образовательного учреждения"),
        TYPE_2("type2_id", "Вид образовательного учреждения"),
        INN("inn", "ИНН"),
        OGRN("ogrn", "ОГРН"),
        SUBORDINATION_ID("subordination_id", "Подчиненность"),
        DIRECTOR("director", "ФИО руководителя"),
        ARHIV("arhiv", "Признак архивности"),
        EGISSO_ID("egisso_id", "Код ЕГИССО"),
        EO_ADDRESS_ID("eo_address", "Вложенная структура адресов корпусов ОО"),
        XA_IS_ACTIVE("xa_is_active", "Признак действия юридического лица"),
        FOUNDER("founder", "Основатель");

        OrganizationRegistryEnumJsonFields(String jsonFieldName, String description){
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
