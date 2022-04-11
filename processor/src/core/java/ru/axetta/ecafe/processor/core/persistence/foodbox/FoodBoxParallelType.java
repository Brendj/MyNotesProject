/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;


import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import java.util.List;

public class FoodBoxParallelType {
    private Long parallelTypeId;
    private Integer parallel;

    public Integer getParallel() {
        return parallel;
    }

    public void setParallel(Integer parallel) {
        this.parallel = parallel;
    }

    public Long getParallelTypeId() {
        return parallelTypeId;
    }

    public void setParallelTypeId(Long parallelTypeId) {
        this.parallelTypeId = parallelTypeId;
    }

    public static class FoodBoxByParallel {
        private static List<FoodBoxParallelType> parallelTypes = null;

        public static List<FoodBoxParallelType> getParallelTypes() {
            if (parallelTypes == null)
            {
                parallelTypes = DAOReadonlyService.getInstance().getParallelsType();
            }
            return parallelTypes;
        }

        public static void setParallelTypes(List<FoodBoxParallelType> parallelTypes) {
            FoodBoxByParallel.parallelTypes = parallelTypes;
        }
    }
}
