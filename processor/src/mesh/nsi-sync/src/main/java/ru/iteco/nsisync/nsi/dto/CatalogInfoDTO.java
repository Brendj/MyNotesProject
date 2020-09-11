package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.iteco.nsisync.nsi.PacketType;
import ru.iteco.nsisync.nsi.catalogs.CatalogType;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogInfoDTO implements Serializable {
    private Long id;
    private PacketType type;
    private CatalogType catalogId;
    private String name;
    private CatalogDataDTO data;

    public CatalogInfoDTO(Long id, PacketType type, CatalogType catalogId, String name, CatalogDataDTO data) {
        this.id = id;
        this.type = type;
        this.catalogId = catalogId;
        this.name = name;
        this.data = data;
    }

    public CatalogInfoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PacketType getType() {
        return type;
    }

    public void setType(PacketType type) {
        this.type = type;
    }

    public CatalogType getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(CatalogType catalogId) {
        this.catalogId = catalogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatalogDataDTO getData() {
        return data;
    }

    public void setData(CatalogDataDTO data) {
        this.data = data;
    }
}
