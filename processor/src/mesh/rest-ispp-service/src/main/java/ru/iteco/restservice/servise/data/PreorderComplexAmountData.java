package ru.iteco.restservice.servise.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 13.05.2021.
 */
public class PreorderComplexAmountData {
    private final Long idOfComplex;
    private final Integer amount;
    private final Map<Long, Integer> dishAmounts;

    public PreorderComplexAmountData(Long idOfComplex, Integer amount) {
        this.idOfComplex = idOfComplex;
        this.amount = amount;
        this.dishAmounts = new HashMap<>();
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public Integer getAmount() {
        return amount;
    }

    public Map<Long, Integer> getDishAmounts() {
        return dishAmounts;
    }

}
