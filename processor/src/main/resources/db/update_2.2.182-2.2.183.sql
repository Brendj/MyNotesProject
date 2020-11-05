--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.183

-- Таблица групп настроек ОО
CREATE TABLE CF_OrgSettings(
idOfOrgSetting BIGSERIAL NOT NULL,
idOfOrg BIGINT NOT NULL,
createdDate BIGINT NOT NULL,
lastUpdate BIGINT NOT NULL,
settingGroup INTEGER NOT NULL,
version BIGINT NOT NULL,
CONSTRAINT cf_OrgSettings_pk PRIMARY KEY(idOfOrgSetting),
CONSTRAINT cf_OrgSettings_uk UNIQUE(idOfOrg, settingGroup),
CONSTRAINT cf_OrgSettings_org_fk FOREIGN KEY(idOfOrg)
REFERENCES cf_Orgs (idOfOrg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX CF_OrgSettings_GROUP_ORG_IDX ON CF_OrgSettings USING btree (idOfOrg, settingGroup);

-- Таблица значений настроек внутри группы
CREATE TABLE CF_OrgSettings_Items(
idOfOrgSettingItem BIGSERIAL NOT NULL,
idOfOrgSetting BIGINT NOT NULL,
createdDate BIGINT NOT NULL,
lastUpdate BIGINT NOT NULL,
settingType INTEGER NOT NULL,
settingValue character varying(128),
version BIGINT NOT NULL,
CONSTRAINT cf_OrgSettingsItems_pk PRIMARY KEY(idOfOrgSettingItem),
CONSTRAINT cf_OrgSettingsItems_uk UNIQUE(idOfOrgSetting, settingType),
CONSTRAINT cf_OrgSettingsItems_org_fk FOREIGN KEY(idOfOrgSetting)
REFERENCES CF_OrgSettings (idOfOrgSetting) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX CF_OrgSettings_Items_TYPE_GROUP_IDX ON CF_OrgSettings_Items USING btree (idOfOrgSetting, settingType);

--ид ОО последних изменений
alter table cf_subscriber_feeding
  add column idOfOrgLastChange bigint;

--ид ОО последних изменений
alter table cf_clients_cycle_diagrams
  add column idOfOrgLastChange bigint;

--! ФИНАЛИЗИРОВАН 21.05.2019, НЕ МЕНЯТЬ
