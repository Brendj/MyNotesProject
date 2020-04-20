--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 464

ALTER TABLE cf_preorder_menudetail
  ADD COLUMN mobile character varying(32),
  ADD COLUMN mobileGroupOnCreate integer;

alter table cf_preorder_complex
  add column mobileGroupOnCreate integer;

alter table cf_regular_preorders
  add column mobileGroupOnCreate integer;