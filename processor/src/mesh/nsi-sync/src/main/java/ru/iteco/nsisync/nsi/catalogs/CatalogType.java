package ru.iteco.nsisync.nsi.catalogs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum CatalogType {
    LEGAL_REPRESENT(34, "Вид представительства"),
    CONTACT_TYPE(35, "Тип контакта"),
    ORGANIZATION_REGISTRY(22, "Реестр Образовательных Организаций"),
    GENDER(40, "Пол"),
    PARALLELS(111, "Параллели"),
    ADMIN_DISTRICT(112, "Административный округ"),
    CITY_AREAS(366, "Округа и районы"),
    EDUCATION_LEVEL(114, "Уровень образования"),
    TRAINING_FORM(	37, "Форма обучения"),
    UNSUPPORTED_TYPE(-1, "Не поддерживаемые типы каталогов");

    CatalogType(Integer id, String description){
        this.id = id;
        this.description = description;
    }

    private Integer id;
    private String description;

    @JsonCreator
    public static CatalogType forValues(@JsonProperty("catalogId") String catalogId){
        Integer id = Integer.valueOf(catalogId);
        for(CatalogType type : CatalogType.values()){
            if(type.getId().equals(id)){
                return type;
            }
        }
        return UNSUPPORTED_TYPE;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
