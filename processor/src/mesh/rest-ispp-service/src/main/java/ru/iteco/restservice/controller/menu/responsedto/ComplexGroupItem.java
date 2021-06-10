package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.RegularPreorder;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.servise.data.PreorderAmountData;
import ru.iteco.restservice.servise.data.PreorderComplexAmountData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nuc on 29.04.2021.
 */
public class ComplexGroupItem {
    @Schema(description = "Список комплексов")
    private List<ComplexItem> complexes;

    public ComplexGroupItem() {
        this.complexes = new ArrayList<>();
    }

    public void fillComplexInfo(Set<WtComplex> wtComplexSet, Map<WtComplex, List<WtDish>> map,
                                PreorderAmountData preorderComplexAmounts, List<RegularPreorder> regulars) {
        for (WtComplex wtComplex : wtComplexSet) {
            ComplexItem item = new ComplexItem(wtComplex, map.get(wtComplex), preorderComplexAmounts, regulars);
            complexes.add(item);
        }
    }

    public List<ComplexItem> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<ComplexItem> complexes) {
        this.complexes = complexes;
    }
}
