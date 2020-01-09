/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChange;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterOrgsService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 05.12.2019.
 */
@Component("OrgNSI3DAOService")
@Scope("singleton")
public class OrgNSI3DAOService extends OrgSymmetricDAOService {
    private static final Logger logger = LoggerFactory.getLogger(OrgNSI3DAOService.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public List<ImportRegisterOrgsService.OrgInfo> getOrgs(String orgName, String region) throws Exception {
        List<ImportRegisterOrgsService.OrgInfo> result = new ArrayList<ImportRegisterOrgsService.OrgInfo>();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        Query query = entityManager.createNativeQuery("select distinct "
                + "'' as building_guid, "                                                                  //0
                + "concat_ws(' / ', addr.area, addr.district, addr.address_asur) as FullAddress, "     //1
                + "org.full_name as FullName, "                            //2
                + "org.short_name as ShortName, "                     //3
                + "cast(org.inn as varchar) as Inn, "                                  //4
                + "org.ogrn as Ogrn, "                                //5
                + "org.director as Person, "                         //6
                + "'' as ExternalUid, "                     //7
                + "'' as EkisGuid, "                       //8
                + "org.eo_id as EkisIds, "                          //9
                + "'' as EkisType, "              //10
                + "'' as EkisType2015, "         //11
                + "cast(addr.unom as varchar) as UNOM, "                               //12
                + "cast(addr.unad as varchar) as UNAD, "                               //13
                + "'' as pp_status, "                                  //14
                + "addr.unique_address_id as ekis_address_id, "                            //15
                + "a.title "                                  //16
                + "FROM "
                + "cf_kf_organization_registry org "
                + "INNER JOIN cf_kf_eo_address addr ON addr.global_object_id = org.global_id "
                + "INNER JOIN cf_kf_ct_admin_district a ON org.eo_district_id = a.system_object_id "
                + "WHERE org.arhiv = false and a.is_deleted = 0 "
                + (StringUtils.isEmpty(orgName) ? "" : " and org.short_name like '%" + orgName + "%'")
                + (StringUtils.isEmpty(region) ? "" : " and a.title like '%" + region + "%'"));
        List list = query.getResultList();
        for (Object o : list) {
            Object[] row = (Object[])o;
            boolean modify = false;
            ImportRegisterOrgsService.OrgInfo item = new ImportRegisterOrgsService.OrgInfo();
            item.setShortName((String)row[3]);
            item.setOfficialName((String)row[2]);
            item.setInn((String)row[4]);
            item.setAddress((String)row[1]);
            item.setCity("Москва");
            item.setGuid((String)row[8]);
            if (row[9] != null) {
                item.setEkisId(Long.parseLong((String)row[9]));
            }
            if (row[12] != null) {
                item.setUnom(Long.parseLong((String)row[12]));
            } else {
                item.setUnom(null);
            }
            if (row[13] != null) {
                item.setUnad(Long.parseLong((String)row[13]));
            } else {
                item.setUnad(null);
            }
            item.setUniqueAddressId(((BigInteger)row[15]).longValue());
            item.setIntroductionQueue((String)row[14]);
            item.setDirector((String)row[6]);

            ImportRegisterOrgsService.OrgInfo info;

            Org fOrg = DAOService.getInstance().findOrgByRegistryData(item.getUniqueAddressId(), item.getGuid(),
                    item.getInn(), item.getUnom(), item.getUnad(), true);

            if (fOrg != null) {
                info = getInfoWithAddToResult(result, row);
                fillInfoWithItem(item, info, ((BigInteger)row[15]).longValue(), (String)row[6], (String)row[5],
                        (String)row[16]);
                fillInfOWithOrg(item, fOrg);
                info.getOrgInfos().add(item);
                info.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
                modify = true;
            } else if ((null != item.getUnom() && null != item.getUnad()) && null == fOrg) {
                info = getInfoWithAddToResult(result, row);
                fillInfoWithItem(item, info, ((BigInteger)row[15]).longValue(), (String)row[6], (String)row[5],
                        (String)row[16]);
                info.setOperationType(OrgRegistryChange.CREATE_OPERATION);
                info.getOrgInfos().add(item);
            }

            if (modify) {
                item.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
            } else {
                item.setOperationType(OrgRegistryChange.CREATE_OPERATION);
            }
        }
        return result;
    }

}
