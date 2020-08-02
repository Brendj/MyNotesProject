package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogChangeDTO implements Serializable {
    private String name;
    private Integer id;
    private String technical_name;
    private ChangesDTO data;

    public CatalogChangeDTO(String name, Integer id, String technical_name, ChangesDTO data) {
        this.name = name;
        this.id = id;
        this.technical_name = technical_name;
        this.data = data;
    }

    public CatalogChangeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTechnical_name() {
        return technical_name;
    }

    public void setTechnical_name(String technical_name) {
        this.technical_name = technical_name;
    }

    public ChangesDTO getData() {
        return data;
    }

    public void setData(ChangesDTO data) {
        this.data = data;
    }
}
