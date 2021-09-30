/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDietType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a.voinov on 28.09.2021.
 */
public class OrderDetailFRationTypeWTdiet {
    private static Map<Long, String> values = new HashMap<>();

    public static Map<Long, String> getValues() {
        initvalues();
        return values;
    }

    public static void setValues(Map<Long, String> values) {
        OrderDetailFRationTypeWTdiet.values = values;
    }

    public static String getDescription(Integer key)
    {
        initvalues();
        return values.get(key.longValue());
    }

    public static Integer getCode(String value)
    {
        if (value == null)
            return -1;
        initvalues();
        for (Map.Entry<Long, String> val : values.entrySet()) {
            if (value.equals(val.getValue())) {
                return val.getKey().intValue();
            }
        }
        return -1;
    }

    OrderDetailFRationTypeWTdiet()
    {
        initvalues();
    }

   private static void initvalues()
   {
       if (values == null || values.isEmpty()) {
           List<WtDietType> dietGroupItems = DAOReadonlyService.getInstance().getMapDiet();
           for (WtDietType wtDietType : dietGroupItems) {
               values.put(wtDietType.getIdOfDietType(), wtDietType.getDescription());
           }
       }
   }

}



