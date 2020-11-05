--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.174

--Таблица с представителями
CREATE TABLE cf_registry_file_guardians
(
  guidofclient character varying(40),
  rep_firstname character varying(64),
  rep_secondname character varying(128),
  rep_surname character varying(128),
  rep_phone character varying(200),
  rep_who character varying(200),
  rep_legal_representative character varying(128),
  rep_ssoid character varying(40),
  rep_guid character varying(40),
  rep_gender character varying(20)
);

--новые поля при создании представтелей
alter table cf_registrychange_guardians
  add column ssoid character varying(50),
  add column guid character varying(40),
  add column legal_representative character varying(128);

--! Новые значения для параметра ecafe.processor.nsi.registry.mode - file, service, symmetric
--! ФИНАЛИЗИРОВАН 31.01.2019, НЕ МЕНЯТЬ