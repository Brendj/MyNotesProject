--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 256

--931
ALTER TABLE cf_emias ADD kafka bool NULL;
ALTER TABLE cf_emias ADD archive bool NULL;
ALTER TABLE cf_emias ADD hazard_level_id int4 NULL;
ALTER TABLE cf_emias ADD errormessage varchar NULL;
ALTER TABLE cf_emias ADD idemias varchar NULL;
ALTER TABLE cf_emias ADD processed bool NULL;

--! ФИНАЛИЗИРОВАН 11.03.2021, НЕ МЕНЯТЬ