/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow;

import java.util.ArrayList;
import java.util.List;

public class AllergenResult extends Result {
    private List<Allergen> allergens = new ArrayList<>();

    public List<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<Allergen> allergens) {
        this.allergens = allergens;
    }
}
