package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();

    }


    // Этот метод по колекции idOfOrgList - строит set c с указанием
    // 'Id орг - для которой собирается информация', 'Имя гл корп' - если есть, 'адрес гл корп' - если есть, и set (дружественных орг)
    public static Set<FriendlyOrganizationsInfoModel> getMainBuildingAndFriendlyOrgsList(Session session,
            List<Long> idOfOrgList) {
        Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModelList = new HashSet<FriendlyOrganizationsInfoModel>();

        for (Long idOfOrg : idOfOrgList) {

            Org org = (Org) session.load(Org.class, idOfOrg);
            if (org.isMainBuilding()) {
                Set<Org> friendlyOrg = org.getFriendlyOrg();
                FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel = new FriendlyOrganizationsInfoModel(org.getIdOfOrg(),
                        org.getOfficialName(), org.getAddress(), friendlyOrg);
                friendlyOrganizationsInfoModelList.add(friendlyOrganizationsInfoModel);
            } else {
                Set<Org> friendlyOrg = org.getFriendlyOrg();
                FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel = new FriendlyOrganizationsInfoModel(org.getIdOfOrg(),
                        friendlyOrg);
                friendlyOrganizationsInfoModelList.add(friendlyOrganizationsInfoModel);
            }
        }

        return friendlyOrganizationsInfoModelList;
    }
}
