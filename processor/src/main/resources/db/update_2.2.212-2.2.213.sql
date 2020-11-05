--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 213

ALTER TABLE cf_preorder_menudetail
  ADD COLUMN usedsum BIGINT not null default 0,
  ADD COLUMN usedamount BIGINT not null default 0;

alter table cf_preorder_linkod
  add column itemcode character varying(32);

ALTER TABLE cf_kf_organization_registry ADD COLUMN xaIsActive INTEGER DEFAULT 1;

--! ФИНАЛИЗИРОВАН 26.03.2020, НЕ МЕНЯТЬ