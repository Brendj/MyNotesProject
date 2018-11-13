--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.166

-- таблица заявлений на льготное питание
ALTER TABLE cf_applications_for_food
    add column servicenumber character varying(128),
    add column creatortype integer NOT NULL DEFAULT 0,
    add column idofdocorder character varying(128),
    add column docorderdate bigint,
    drop column idoforgoncreate;

ALTER TABLE cf_client_dtiszn_discount_info
    ALTER column dtiszncode TYPE bigint;

ALTER TABLE cf_applications_for_food
    add column archived integer;