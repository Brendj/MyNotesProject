package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.wt.WtComplex;

/**
 * Created by nuc on 29.04.2021.
 */
public class ComplexesResponse {
    @Schema(description = "Льготные комплексы")
    private ComplexGroupItem freeComplexes;

    @Schema(description = "Платные комплексы")
    private ComplexGroupItem paidComplexes;

    public ComplexesResponse() {
        this.freeComplexes = new ComplexGroupItem();
        this.paidComplexes = new ComplexGroupItem();
    }

    public ComplexGroupItem getFreeComplexes() {
        return freeComplexes;
    }

    public void setFreeComplexes(ComplexGroupItem freeComplexes) {
        this.freeComplexes = freeComplexes;
    }

    public ComplexGroupItem getPaidComplexes() {
        return paidComplexes;
    }

    public void setPaidComplexes(ComplexGroupItem paidComplexes) {
        this.paidComplexes = paidComplexes;
    }
}
