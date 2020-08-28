package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogDataDTO implements Serializable {
    private String id;
    private CatalogChangeDTO catalog;

    public CatalogDataDTO(String id, CatalogChangeDTO catalog) {
        this.id = id;
        this.catalog = catalog;
    }

    public CatalogDataDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CatalogChangeDTO getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogChangeDTO catalog) {
        this.catalog = catalog;
    }
}
