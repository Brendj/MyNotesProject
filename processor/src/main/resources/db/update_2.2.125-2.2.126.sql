--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.126

--Индексы на таблицу истории синхронизации
CREATE INDEX cf_synchistory_daily_syncdate_idx ON cf_synchistory_daily USING btree (syncdate);

CREATE INDEX cf_groupnames_to_orgs_idx ON cf_groupnames_to_orgs USING btree (version);

--! ФИНАЛИЗИРОВАН (Семенов, 141116) НЕ МЕНЯТЬ