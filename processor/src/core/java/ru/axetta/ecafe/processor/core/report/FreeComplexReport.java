/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Collections;
import java.util.List;

/**
 * Онлайн отчеты -> Отчет по комплексам -> Бесплатные комплексы
 */
public class FreeComplexReport extends AllComplexReport {
    private final List<AllComplexReport.ComplexItem> complexItems;

    public FreeComplexReport() {
        super();
        this.complexItems = Collections.emptyList();
    }

    public List<AllComplexReport.ComplexItem> getComplexItems() {
        return complexItems;
    }

}
