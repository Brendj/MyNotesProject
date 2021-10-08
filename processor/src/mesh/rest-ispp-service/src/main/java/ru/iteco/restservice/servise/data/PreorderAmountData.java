package ru.iteco.restservice.servise.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 13.05.2021.
 */
public class PreorderAmountData {
    private final List<PreorderComplexAmountData> preorderComplexAmountData;

    public PreorderAmountData() {
        this.preorderComplexAmountData = new ArrayList<>();
    }

    public static PreorderComplexAmountData getByComplexId(PreorderAmountData preorderAmountData, Long complexId) {
        for (PreorderComplexAmountData data : preorderAmountData.getPreorderComplexAmountData()) {
            if (data.getIdOfComplex().equals(complexId)) return data;
        }
        return null;
    }

    public List<PreorderComplexAmountData> getPreorderComplexAmountData() {
        return preorderComplexAmountData;
    }
}
