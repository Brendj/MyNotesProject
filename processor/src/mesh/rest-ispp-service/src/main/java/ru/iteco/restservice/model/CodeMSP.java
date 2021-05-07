package ru.iteco.restservice.model;

import javax.persistence.*;

/**
 * Created by nuc on 05.05.2021.
 */
@Entity
@Table(name = "cf_code_msp")
public class CodeMSP {
    @Id
    @Column(name = "idOfCode")
    private Long idOfCode;

    @Column
    private Integer code;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "idOfCategoryDiscount")
    private CategoryDiscount categoryDiscount;

    public Long getIdOfCode() {
        return idOfCode;
    }

    public void setIdOfCode(Long idOfCode) {
        this.idOfCode = idOfCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }
}
