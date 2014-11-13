package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.ArrayList;
import java.util.List;

/**
 * User: shamil
 * Date: 13.11.14
 * Time: 14:09
 */
public class OrgUtils {

    public static List<Long> extractIds(List<Org> orgList) {
        List<Long> result = new ArrayList<Long>();
        for (Org org : orgList) {
            result.add(org.getIdOfOrg());
        }
        return result;
    }

    public static String extractIdsAsString(List<Org> orgList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (long aLong : extractIds(orgList)) {
            stringBuilder.append(aLong).append(",");
        }
        if(stringBuilder.length() > 0 ){
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();

    }
}
