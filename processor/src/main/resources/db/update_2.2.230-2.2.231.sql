--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 231

--662
create table cf_orgs_precontract_ids (
    idOfPreContractId bigserial,
    version bigint NOT NULL DEFAULT 0,
    idOfOrg bigint not null,
    contractId bigint not null,
    used boolean not null default false,
    createddate bigint,
    useddate bigint,
    CONSTRAINT cf_orgs_precontract_ids_pk PRIMARY KEY (idOfPreContractId),
    CONSTRAINT cf_orgs_precontract_ids_idoforg_fk FOREIGN KEY (idoforg)
    REFERENCES cf_orgs (idoforg) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_orgs_precontract_ids_idoforg_idx ON cf_orgs_precontract_ids USING btree (idOfOrg) WHERE not used;
CREATE UNIQUE INDEX cf_orgs_precontract_ids_contarctid_idx ON cf_orgs_precontract_ids USING btree (contractId);

COMMENT ON TABLE cf_orgs_precontract_ids IS 'Предварительно рассчитанные номера л/с по организациям';
COMMENT ON COLUMN cf_orgs_precontract_ids.idOfPreContractId IS 'ID записи';
COMMENT ON COLUMN cf_orgs_precontract_ids.version IS 'Версия записи';
COMMENT ON COLUMN cf_orgs_precontract_ids.idOfOrg IS 'Ссылка на организацию';
COMMENT ON COLUMN cf_orgs_precontract_ids.contractId IS 'Номер л/с';
COMMENT ON COLUMN cf_orgs_precontract_ids.used IS 'Признак, что данный л/с присвоен клиенту';
COMMENT ON COLUMN cf_orgs_precontract_ids.createddate IS 'Дата-время создания записи';

--! ФИНАЛИЗИРОВАН 17.09.2020, НЕ МЕНЯТЬ