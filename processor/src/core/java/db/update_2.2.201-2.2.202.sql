--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 202

-- 21: Добавление предопределенной группы для всех организаций
INSERT INTO cf_clientgroups (idoforg, idofclientgroup, groupname)
SELECT org.idoforg, 1100000120, 'Вне ОУ'
FROM cf_orgs org;

alter table cf_orgs add column ekisId bigint;