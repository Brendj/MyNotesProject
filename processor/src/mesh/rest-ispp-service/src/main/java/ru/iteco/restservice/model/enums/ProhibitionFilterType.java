package ru.iteco.restservice.model.enums;

public enum ProhibitionFilterType {
    /*0*/ PROHIBITION_BY_GOODS_NAME("По полному наименованию товара"),
    /*1*/ PROHIBITION_BY_FILTER("По фильтру указанному в фильтре"),
    /*2*/ PROHIBITION_BY_GROUP_NAME("По полному наименованию группы");

    private final String description;

    private ProhibitionFilterType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ProhibitionFilterType getTypeBuId(int id){
        if(id<0 && id>ProhibitionFilterType.values().length) return null;
        return ProhibitionFilterType.values()[id];
    }
}
