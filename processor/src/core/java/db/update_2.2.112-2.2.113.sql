--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.112

--Увеличение размерности поля для условия выборки в отчетах по расписанию
ALTER TABLE cf_ruleconditions ALTER  conditionconstant TYPE character varying (50000);

--Новое поле для краткого наименование организации для поставщика в сверке организаций
ALTER TABLE cf_orgregistrychange_item ADD COLUMN shortnamesupplierfrom character varying(128);

--Новые поля ручного реестра талонов
ALTER TABLE cf_taloon_approval RENAME COLUMN qty to soldedqty, ADD COLUMN requestedqty integer,
  ADD COLUMN shippedqty integer, ADD COLUMN ispp_state integer NOT NULL DEFAULT 0, ADD COLUMN pp_state integer NOT NULL DEFAULT 0;