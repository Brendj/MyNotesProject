/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.symmetric;

import ru.axetta.ecafe.processor.core.partner.nsi.OrgMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChange;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
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

@Component("OrgSymmetricDAOService")
@Scope("singleton")
public class OrgSymmetricDAOService extends OrgMskNSIService {
    private static final Logger logger = LoggerFactory.getLogger(OrgSymmetricDAOService.class);

    @PersistenceContext(unitName = "symmetricPU")
    private EntityManager entityManager;

    public List<ImportRegisterOrgsService.OrgInfo> getOrgs(String orgName, String region) throws Exception {
        List<ImportRegisterOrgsService.OrgInfo> result = new ArrayList<ImportRegisterOrgsService.OrgInfo>();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        Query query = entityManager.createNativeQuery("select distinct "
                + "cast(ib.id as varchar) as building_guid, "                                                                  //0
                + "concat_ws(' / ', dmz.zone_name, dcd.districts_city_name, a.fulltext) as FullAddress, "     //1
                + "i.name as FullName, "                            //2
                + "i.short_name as ShortName, "                     //3
                + "i.inn as Inn, "                                  //4
                + "i.ogrn as Ogrn, "                                //5
                + "i.principal as Person, "                         //6
                + "i.local_id as ExternalUid, "                     //7
                + "upper(cast(i.ekis_guid as varchar)) as EkisGuid, "                       //8
                + "i.ekis_id as EkisIds, "                          //9
                + "dit.institution_type as EkisType, "              //10
                + "dint.institution_type as EkisType2015, "         //11
                + "db.unom as UNOM, "                               //12
                + "db.unad as UNAD, "                               //13
                + "ib.pp_status, "                                  //14
                + "ib.ekis_address_id, "                            //15
                + "dmz.zone_name "                                  //16
                + "FROM "
                + "institutions i "
                + "INNER JOIN institutions_buildings ib ON ib.institution_id = i.id AND ib.deleted_at IS NULL "
                + "LEFT OUTER JOIN addresses a ON a.id = ib.address_id "
                + "LEFT OUTER JOIN dict_institution_types dit ON dit.id = i.institution_type_id "
                + "LEFT OUTER JOIN dict_institution_new_types dint ON dint.id = i.institution_new_type_id "
                + "LEFT OUTER JOIN dict_mos_zones dmz ON dmz.id = a.mos_zones_guid "
                + "LEFT OUTER JOIN dict_city_districts dcd ON dcd.id = a.city_district_guid "
                + "LEFT OUTER JOIN dict_bti db ON db.id = a.bti_guid "
                + "WHERE coalesce(i.archive, false) = false "
                + "AND ib.pp_status IS NOT NULL AND ib.pp_status NOT IN ('','Внедрение ИС ПП отложено','Подключение к ИС ПП не планируется')"
                + (StringUtils.isEmpty(orgName) ? "" : " and i.name like '%" + orgName + "%'")
                + (StringUtils.isEmpty(region) ? "" : " and dmz.zone_name like '%" + region + "%'"));
        List list = query.getResultList();
        for (Object o : list) {
            Object[] row = (Object[])o;
            ImportRegisterOrgsService.OrgInfo info = getInfoWithAddToResult(result, row); //new ImportRegisterOrgsService.OrgInfo();
            info.setOrganizationType(OrganizationType.SCHOOL);
            info.setShortName((String)row[3]);
            info.setOfficialName((String)row[2]);
            info.setInn((String)row[4]);
            info.setAddress((String)row[1]);
            info.setCity("Москва");
            info.setGuid((String)row[8]);
            if (row[12] != null) {
                info.setUnom(Long.parseLong((String)row[12]));
            } else {
                info.setUnom(null);
            }
            if (row[13] != null) {
                info.setUnad(Long.parseLong((String)row[13]));
            } else {
                info.setUnad(null);
            }
            info.setAdditionalId(((BigInteger)row[15]).longValue());
            info.setUniqueAddressId(((BigInteger)row[15]).longValue());
            info.setDirectorFullName((String)row[6]);
            info.setOGRN((String)row[5]);
            info.setMainBuilding(false);
            info.setIntroductionQueue((String)row[14]);
            info.setDirector((String)row[6]);
            info.setRegion((String)row[16]);
            if (info.getOrgInfos().size() == 0) {
                info.setOrgInfos(new ArrayList<ImportRegisterOrgsService.OrgInfo>());
            }
            ImportRegisterOrgsService.OrgInfo item = new ImportRegisterOrgsService.OrgInfo();

            boolean modify = false;
            item.setAddress(info.getAddress());
            item.setShortName(info.getShortName());
            item.setOfficialName(info.getOfficialName());
            item.setCity(info.getCity());
            item.setGuid(info.getGuid());
            item.setInn(info.getInn());
            item.setUnom(info.getUnom());
            item.setUnad(info.getUnad());
            item.setUniqueAddressId(info.getUniqueAddressId());
            item.setIntroductionQueue(info.getIntroductionQueue());
            item.setDirector(info.getDirector());

            Org fOrg = DAOService.getInstance().findOrgByRegistryData(item.getUniqueAddressId(), item.getGuid(),
                    item.getInn(), item.getUnom(), item.getUnad(), true);
            if (fOrg != null) {
                fillInfOWithOrg(item, fOrg);
                item.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
                modify = true;
            } else if ((null == item.getUnom() || null == item.getUnad()) && null == fOrg) {    // 3 этап был пропущен
                result.remove(info);
            } else {
                item.setOperationType(OrgRegistryChange.CREATE_OPERATION);
            }
            info.getOrgInfos().add(item);
            if (modify) {
                info.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
            } else {
                info.setOperationType(OrgRegistryChange.CREATE_OPERATION);
            }
        }
        return result;
    }

    private ImportRegisterOrgsService.OrgInfo getInfoWithAddToResult(List<ImportRegisterOrgsService.OrgInfo> result, Object[] row) {
        for (ImportRegisterOrgsService.OrgInfo info : result) {
            if (row[8] != null && ((String)row[8]).equals(info.getGuid())) {
                return info;
            }
        }
        ImportRegisterOrgsService.OrgInfo info = new ImportRegisterOrgsService.OrgInfo();
        result.add(info);
        return info;
    }

    public List getQueryResult(String str_query) {
        entityManager.setFlushMode(FlushModeType.COMMIT);
        Query query = entityManager.createNativeQuery(str_query);
        return query.getResultList();
    }
}
