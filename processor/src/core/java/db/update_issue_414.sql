--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 414

ALTER TABLE cf_preorder_menudetail
  ADD COLUMN usedsum BIGINT,
  ADD COLUMN usedamount BIGINT;

alter table cf_preorder_linkod
    add column itemcode character varying(32);