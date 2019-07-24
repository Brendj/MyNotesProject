--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.188

--188
ALTER TABLE cf_externalevents ADD COLUMN address varchar;
ALTER TABLE cf_externalevents ADD COLUMN orgShortName varchar;

--179
INSERT INTO cf_clientgroups VALUES (2411,1100000080,'Перемещенные');
INSERT INTO cf_clientgroups VALUES (96,1100000080,'Перемещенные');
INSERT INTO cf_clientgroups VALUES (2504,1100000080,'Перемещенные');
INSERT INTO cf_clientgroups VALUES (100,1100000080,'Перемещенные');
INSERT INTO cf_clientgroups VALUES (324,1100000080,'Перемещенные');
INSERT INTO cf_clientgroups VALUES (57,1100000080,'Перемещенные');

--! ФИНАЛИЗИРОВАН 24.07.2019, НЕ МЕНЯТЬ