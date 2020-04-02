/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("ImportRegisterNSI3ServiceKafkaWrapper")
@DependsOn({"ImportRegisterNSI3Service", "runtimeContext"})
public class ImportRegisterNSI3ServiceKafkaWrapper extends ImportRegisterFileService {
    private static final Logger logger = LoggerFactory.getLogger(ImportRegisterNSI3ServiceKafkaWrapper.class);
    private final ImportRegisterFileService innerServices = RuntimeContext.getAppContext().getBean("ImportRegisterNSI3Service", ImportRegisterNSI3Service.class);
    private final boolean workWithKafka = workWithKafka();
    protected final String DROP_INDEX = "drop index if exists cf_mh_persons_ekisid_idx";
    protected final String CREATE_INDEX = "create index cf_mh_persons_ekisid_idx on cf_mh_persons using btree (ekisId)";

    private boolean workWithKafka(){
        String mode = RuntimeContext.getInstance().getPropertiesValue(ImportRegisterFileService.MODE_PROPERTY, null);
        return Objects.equals(mode, ImportRegisterFileService.MODE_KAFKA);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void fillOrgGuids(Query query, ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids) {
        innerServices.fillOrgGuids(query, orgGuids);
    }

    @Override
    public String getBadGuids(ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        return innerServices.getBadGuids(orgGuids);
    }

    @Override
    protected String getQueryString() {
        if(!workWithKafka){
            return innerServices.getQueryString();
        } else {
            return "WITH pupls_info AS (\n"
                    + "       SELECT p.personguid,\n"
                    + "              o.guid,\n"
                    + "              p.firstname,\n"
                    + "              p.lastname,\n"
                    + "              p.patronymic,\n"
                    + "              (extract(EPOCH FROM p.birthdate) * 1000) AS birthdate,\n"
                    + "              g.title AS gender,\n"
                    + "              prll.title AS parallel,\n"
                    + "              p.classname,\n"
                    + "              p.deletestate,\n"
                    + "              ag.title AS agegroup,\n"
                    + "              p.ekisid\n"
                    + "       FROM cf_mh_persons AS p\n"
                    + "                   LEFT JOIN cf_orgs AS o ON p.ekisid = o.ekisid\n"
                    + "                   JOIN cf_kf_ct_age_group AS ag ON p.agegroupid = ag.id\n"
                    + "                   JOIN cf_kf_ct_gender AS g ON p.genderid = g.id\n"
                    + "                   JOIN cf_kf_ct_parallel AS prll ON p.parallelid = prll.id\n"
                    + ") SELECT * FROM pupls_info";
        }
    }

    @Override
    protected String getDropIndexStatement() {
        if(!workWithKafka) {
            return innerServices.getDropIndexStatement();
        }
        else return this.DROP_INDEX;
    }

    @Override
    protected String getCreateIndexStatement() {
        if(!workWithKafka) {
            return innerServices.getCreateIndexStatement();
        }
        else return this.CREATE_INDEX;
    }
}
