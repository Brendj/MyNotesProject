package ru.axetta.ecafe.processor.web.ui.service;

public enum SelectedOrgListType {

    /*0*/ ORG_LIST_PREORDER("Список организаций генерации заявок по предзаказам"),
    /*1*/ ORG_LIST_REMOVE_DUPLICATES("Список организаций удаления дубликатов представителей"),
    /*2*/ ORG_LIST_LOAD_MESH("Список организаций загрузки mesh гуидов представителей"),
    /*3*/ ORG_LIST_REMOVE_RELATION("Список организаций удаления степени родстава");

    private final String description;

    SelectedOrgListType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
