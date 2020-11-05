--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.169

-- поиск льгот по GUID для АРМ
alter table cf_categorydiscounts_dszn
  add column GUID character varying(80) not null unique default cast(uuid_in(cast(md5(cast(random() as text) || cast(clock_timestamp() as text)) as cstring)) as varchar);


-- c v88.1 ARM редактирует льготы по GUID, необходимо поднять версию для отправки GUID для уже имеющихся льгот
UPDATE cf_categorydiscounts_dszn
SET version = (SELECT MAX(version) FROM cf_categorydiscounts_dszn) + 1
WHERE deleted <> 1;

alter table cf_client_dtiszn_discount_info add column archived integer;

--! ФИНАЛИЗИРОВАН 19.12.2018, НЕ МЕНЯТЬ