--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.124

-- Исправление заполнения поля Короткий адрес
UPDATE cf_orgs AS o
SET shortaddress = address;

UPDATE cf_orgs AS o
SET shortaddress = i.a FROM (SELECT idoforg, trim(FROM substring(address FROM '%/%/#"%/%#"' FOR '#')) as a FROM cf_orgs
where trim(FROM substring(address FROM '%/%/#"%/%#"' FOR '#')) <> '') i
WHERE i.idoforg = o.idoforg;

UPDATE cf_orgs AS o
SET shortaddress = i.a FROM (SELECT idoforg, trim(FROM substring(address FROM '%/%/#"%/%/%#"' FOR '#')) as a FROM cf_orgs
where trim(FROM substring(address FROM '%/%/#"%/%/%#"' FOR '#')) <> '') i
WHERE i.idoforg = o.idoforg;

-- Поле "должность" для временных посетителей
ALTER TABLE cf_visitors
  ADD COLUMN position character varying (128);

UPDATE cf_visitors
SET position = 'Инженер ИС ПП'
WHERE VisitorType = 1;

drop index CF_ClientSms_Price_idx;

--! ФИНАЛИЗИРОВАН (Семенов, 171016) НЕ МЕНЯТЬ