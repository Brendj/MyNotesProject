--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.121

--Признак использования турникета с 1 сентября
ALTER TABLE cf_org_accessories
  ADD COLUMN usedsinceseptember boolean NOT NULL DEFAULT false;

CREATE INDEX cf_lastprocesssectionsdates_idoforg_idx
ON cf_lastprocesssectionsdates USING btree (idoforg);

--! ФИНАЛИЗИРОВАН (Семенов, 120916) НЕ МЕНЯТЬ