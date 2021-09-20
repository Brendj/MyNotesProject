package ru.iteco.restservice.servise.data;

import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nuc on 13.05.2021.
 */
public class PreorderComplexAmountData {
    private final Long idOfComplex;
    private final Integer amount;
    private final Long idOfPreorderComplex;
    private final List<PreorderMenuDetail> menuDetails;

    public PreorderComplexAmountData(Long idOfComplex, Integer amount, Long idOfPreorderComplex) {
        this.idOfComplex = idOfComplex;
        this.amount = amount;
        this.idOfPreorderComplex = idOfPreorderComplex;
        this.menuDetails = new ArrayList<>();
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public Integer getAmount() {
        return amount;
    }

    public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public List<PreorderMenuDetail> getMenuDetails() {
        return menuDetails;
    }
}
