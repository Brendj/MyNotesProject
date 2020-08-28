/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by nuc on 14.08.2020.
 */
public class MeshTrainingForm {
    private Long global_id;
    private Integer id;
    private String title;
    private String education_form;
    private Boolean archive;
    private Integer is_deleted;

    public MeshTrainingForm() {

    }

    public Long getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(Long global_id) {
        this.global_id = global_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEducation_form() {
        return education_form;
    }

    public void setEducation_form(String education_form) {
        this.education_form = education_form;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public Integer getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Integer is_deleted) {
        this.is_deleted = is_deleted;
    }
}
