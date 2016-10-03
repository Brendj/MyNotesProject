--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.123

ALTER TABLE CF_OrderDetails ADD COLUMN manufacturer CHARACTER VARYING(128);

ALTER TABLE Cf_GroupNames_To_Orgs ADD COLUMN parentGroupName CHARACTER VARYING(128);
ALTER TABLE Cf_GroupNames_To_Orgs ADD COLUMN isMiddleGroup INTEGER;